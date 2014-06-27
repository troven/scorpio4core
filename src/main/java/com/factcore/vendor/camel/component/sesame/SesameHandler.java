package com.factcore.vendor.camel.component.sesame;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.camel.Message;
import org.apache.camel.util.ExchangeHelper;
import org.apache.commons.io.output.WriterOutputStream;
import org.openrdf.query.*;
import org.openrdf.query.resultio.QueryResultIO;
import org.openrdf.query.resultio.TupleQueryResultFormat;
import org.openrdf.query.resultio.TupleQueryResultWriter;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Map;

/**
 * Scorpio (c) 2014
 * Module: com.factcore.vendor.camel.component.sparql
 * User  : lee
 * Date  : 25/06/2014
 * Time  : 11:33 AM
 */
public class SesameHandler {
	private static final Logger log = LoggerFactory.getLogger(SesameHandler.class);
	RepositoryConnection connection = null;

	boolean inferred = true, autoClose = false;
	private int maxQueryTime = -1;
	String sparql = null;

	public SesameHandler(RepositoryConnection connection, String sparql, boolean isInferred, int maxQueryTime, boolean autoClose) {
		this.connection=connection;
		this.sparql=sparql;
		this.inferred=isInferred;
		this.maxQueryTime=maxQueryTime;
		this.autoClose=autoClose;
		log.debug("SPARQLHandler: "+sparql);
	}

	@Handler
	public void handle(Exchange exchange) throws MalformedQueryException, RepositoryException, QueryResultHandlerException, QueryEvaluationException, IOException {
		Map<String,Object> headers = exchange.getIn().getHeaders();

		if (sparql==null||sparql.equals ("")) {
			sparql = exchange.getIn().getBody(String.class);
			if ( sparql==null||sparql.equals ("") ) {
				sparql = (String) headers.get("sparql.query");
				if ( sparql==null||sparql.equals ("") ) {
					throw new MalformedQueryException("Missing SPARQL query");
				} else log.debug("Header SPARQL: "+sparql);
			} else log.debug("Body SPARQL: "+sparql);
		} else log.debug("Parameter SPARQL: "+sparql);

		String contentType = ExchangeHelper.getContentType(exchange);
		if (contentType==null||contentType.equals("")) {
			log.debug("Accept-Types:"+headers.get("Accept"));
			contentType = (String) headers.get("Accept");
		}

		TupleQueryResultFormat parserFormatForMIMEType = QueryResultIO.getParserFormatForMIMEType(contentType, TupleQueryResultFormat.JSON);
		headers.put("Content-Type", parserFormatForMIMEType.getDefaultMIMEType()+";"+parserFormatForMIMEType.getCharset());

//		headers.put("sparql.query", sparql);

		// prepare SPARQL/XML results
		StringWriter stringWriter = handle(sparql, parserFormatForMIMEType);
		log.trace(stringWriter.toString());

		// output message
		String results = stringWriter.toString();
		log.debug("SPARQL HEADERS: "+contentType+"\n"+headers);
		log.debug("\tQuery: "+sparql);

		Message out = exchange.getIn();
		out.setBody(results);
		out.setHeaders(headers);
//		exchange.setPattern(ExchangePattern.InOut);
//		out.setAttachments(exchange.getIn().getAttachments());
		if (autoClose) {
			log.debug("Closing connection: "+exchange.getFromEndpoint().getEndpointUri());
			connection.close();
		}
	}

	public StringWriter handle(String sparql, TupleQueryResultFormat parserFormatForMIMEType) throws MalformedQueryException, RepositoryException, QueryResultHandlerException, QueryEvaluationException, IOException {
		StringWriter stringWriter = new StringWriter();
		OutputStream out = new WriterOutputStream(stringWriter);
		handle(sparql, out, parserFormatForMIMEType);
		out.close();
		return stringWriter;
	}

	public void handle(String sparql, OutputStream out, TupleQueryResultFormat parserFormatForMIMEType) throws MalformedQueryException, RepositoryException, QueryResultHandlerException, QueryEvaluationException {
		// handle query and result set
		TupleQuery tupleQuery = connection.prepareTupleQuery(QueryLanguage.SPARQL, sparql);
		tupleQuery.setIncludeInferred(isInferred());
		if (maxQueryTime>0) tupleQuery.setMaxQueryTime(getMaxQueryTime());

		TupleQueryResultWriter resultWriter = QueryResultIO.createWriter(parserFormatForMIMEType, out);
		resultWriter.startQueryResult(new ArrayList());
		tupleQuery.evaluate(resultWriter);
	}

	public RepositoryConnection getConnection() {
		return connection;
	}

	public boolean isInferred() {
		return inferred;
	}

	public int getMaxQueryTime() {
		return maxQueryTime;
	}

}

package com.factcore.vendor.camel.component.any23;

import org.apache.any23.Any23;
import org.apache.any23.source.DocumentSource;
import org.apache.any23.source.StringDocumentSource;
import org.apache.any23.writer.NTriplesWriter;
import org.apache.any23.writer.TripleHandler;
import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.camel.util.ExchangeHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * Scorpio (c) 2014
 * Module: com.factcore.vendor.camel.component.any23
 * User  : lee
 * Date  : 24/06/2014
 * Time  : 10:29 PM
 */
public class Any23Handler {
	static protected final Logger log = LoggerFactory.getLogger(Any23Handler.class);

	public Any23Handler(Map<String, Object> params) {
	}

	@Handler
	public void handle(Exchange exchange) throws Exception {
		log.debug("ANY23: "+exchange);
		Any23 runner = new Any23();
		runner.setHTTPUserAgent("x-any23-camel-agent");

		String contentType = ExchangeHelper.getContentType(exchange);
		String body = exchange.getIn().getBody(String.class);
		String uri = exchange.getFromEndpoint().getEndpointUri();

		log.debug("\tmime: "+contentType);
		log.debug("\tfrom: "+uri);
		log.debug("\tbody: "+body);

		DocumentSource source = new StringDocumentSource(body, uri, contentType);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		TripleHandler handler = getTripleHandler(contentType, out);
		try {
			runner.extract(source, handler);
		} catch(Exception e) {

		} finally {
			handler.close();
		}
		exchange.getOut().setBody(out.toString("UTF-8"));
	}

	public TripleHandler getTripleHandler(String contentType, OutputStream out) {
		return new NTriplesWriter(out);
	}
}



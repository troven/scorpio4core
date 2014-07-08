package com.scorpio4.vendor.camel.flo;

import com.scorpio4.assets.Asset;
import com.scorpio4.assets.AssetRegister;
import com.scorpio4.assets.SesameAssetRegister;
import com.scorpio4.fact.FactSpace;
import com.scorpio4.iq.exec.Scripting;
import com.scorpio4.vendor.sesame.util.RDFCollections;
import com.scorpio4.vocab.COMMON;
import org.apache.camel.*;
import org.apache.camel.builder.DataFormatClause;
import org.apache.camel.builder.ExpressionBuilder;
import org.apache.camel.builder.PredicateBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.ChoiceDefinition;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.language.LanguageExpression;
import org.openrdf.model.*;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Scorpio (c) 2014
 * Module: com.scorpio4.vendor.camel
 * User  : lee
 * Date  : 21/06/2014
 * Time  : 5:58 PM
 */
public class RDFCamelPlanner extends FLOSupport {
	static protected final Logger log = LoggerFactory.getLogger(RDFCamelPlanner.class);

	FactSpace factSpace = null;
	int count = 0;
	AssetRegister assetRegister = null;
	String vocabURI = COMMON.CAMEL_FLO;
	URI TO = null;

	public RDFCamelPlanner(CamelContext camelContext, FactSpace factSpace) throws Exception {
		super(camelContext);
		init(factSpace);
	}

	private void init(FactSpace factSpace) {
		this.factSpace=factSpace;
		assetRegister = new SesameAssetRegister(factSpace.getConnection());
		TO = factSpace.getConnection().getValueFactory().createURI(getVocabURI() + "to");
	}

	public void setVocabURI(String prefix) {
		this.vocabURI = prefix;
	}

	public String getVocabURI() {
		return vocabURI;
	}

	public int plan() throws Exception {
		return plan(factSpace.getIdentity());
	}

	public int plan(final String routeURI) throws Exception {
		final RepositoryConnection connection = factSpace.getConnection();
		final ValueFactory vf = connection.getValueFactory();
		String FROM = toVocabURI("from");
		log.debug("Plan Route: "+routeURI+" -> "+FROM);
		RepositoryResult<Statement> froms = connection.getStatements(vf.createURI(routeURI), vf.createURI(FROM), null, false);
		Map seen = new HashMap();
		while(froms.hasNext()){
			Statement next = froms.next();
			final Value _routeID = next.getSubject();
			final Value _from = next.getObject();
			if (seen.containsKey(_from.stringValue())) {
				break;
			}
			seen.put(_from.stringValue(), true);
			RouteBuilder routing = new org.apache.camel.builder.RouteBuilder() {
				@Override
				public void configure() throws Exception {
//					errorHandler(deadLetterChannel("mock:error"));
					log.debug("Configure Route ("+_routeID+") -> "+_from);
					String from = _from.stringValue();
					log.debug("\troute -> "+from+" @ "+_routeID);
					RouteDefinition tried = from(from);
//					tried.doTry();

					log.debug("\ttry -> " + tried);
					ProcessorDefinition ended = tryResource(connection, tried, (Resource) _from);
					log.debug("\tended -> " + ended+" <- "+tried);

					if (ended.getOutputs().isEmpty()) {
						log.warn("NO ENDPOINT: "+_from);
						ended.to("log:missing-endpoint");
					}

//					tried.doCatch(Exception.class).to("log:catch:" + routeURI).
//					doFinally().to("log:finally:" + routeURI).
					ended.end();
					log.debug("\tfinally:" + ended);
					count++;
				}
			};
			context.addRoutes(routing);
		}
		return count;
	}

	private String toVocabURI(String localname) {
		return getVocabURI()+localname;
	}

	protected ProcessorDefinition tryResource(final RepositoryConnection connection, final ProcessorDefinition fromRoute, final Resource from) throws RepositoryException, CamelException, ClassNotFoundException, IOException {
		log.debug("TRY from: "+from+" -> "+fromRoute);

		RepositoryResult<Statement> plannedRoutes = connection.getStatements(from, null, null, false);
		while(plannedRoutes.hasNext()) {
			Statement next = plannedRoutes.next();
			String action = next.getPredicate().stringValue();
			Value to = next.getObject();
			if (action.startsWith(getVocabURI()) ) {
				tryAction(connection, fromRoute, to, next.getPredicate(), action.substring(getVocabURI().length()));
			} else {
				log.debug("Ignored Predicate: " + action);
			}
		}
		return fromRoute;
	}

	protected ProcessorDefinition tryAction(RepositoryConnection connection, ProcessorDefinition from, Value _to, URI predicate, String action) throws RepositoryException, CamelException, ClassNotFoundException, IOException {
		String to = _to.stringValue();
		if (to.equals(toVocabURI("stop"))) return from.stop();
		if (to.equals(toVocabURI("end"))) return from.end();
		if (to.equals(toVocabURI("endChoice"))) return from.endChoice();

		log.debug("TRY action ->"+ action+" -> "+to);

		if (action.equals("to") || action.equals("do") && _to instanceof Resource) {
			from = doRouting(connection, from, (Resource)_to, predicate);
		} else if (action.equals("bean") ) {
			log.info("to-bean: "+to);
			from = from.beanRef(to);
		} else if (action.startsWith("setBody") || action.startsWith("body")) {
			from = from.setBody(toExpression(connection, _to, action));
		} else if (action.startsWith("setFaultBody") || action.startsWith("fault") ) {
			from = from.setFaultBody(toExpression(connection, _to, action));
		} else if (action.startsWith("aggregate")) {
			from = from.aggregate(toExpression(connection, _to, action));
		} else if (action.startsWith("validate")) {
			from = from.validate(toExpression(connection, _to, action));
		} else if (action.equals("multicast")) {
			from = from.multicast().to(to);
		} else if (action.equals("loadbalance")) {
			from = from.loadBalance().to(to);
		} else if (action.startsWith("script")) {
			from = from.process( toScriptProcessor(connection,_to, action) );
		} else if (action.startsWith("split")) {
			from = from.split(toExpression(connection, _to, action));
		} else if (action.startsWith("marshal:")) {
			from = doMarshal(action, from);
		} else if (action.startsWith("unmarshal:")) {
			from = doUnmarshal(action, from);
		} else if (action.startsWith("filter")) {
			from = from.filter(toPredicate(connection, _to, action));
		} else if (action.startsWith("sort")) {
			from = from.sort(toExpression(connection, _to, action));
		} else if (action.startsWith("split")) {
			from = from.split(toExpression(connection, _to, action));
		} else if (action.equals("threads")) {
			from = from.threads().to(to);
		} else if (action.equals("onCompletion")) {
			from = from.onCompletion().to(to);
		} else if (action.equals("transacted")) {
			from = from.transacted().to(to);
		} else if (action.startsWith("resequence")) {
			from = from.resequence(toExpression(connection, _to, action));
		} else if (action.equals("convertBodyTo") && action.equals("as")) {
			// "as" is a FLO synonym for convertBodyTo
			from = doConvertBodyTo(from, to);
		} else if (action.startsWith("recipientList")) {
			from = from.recipientList(toExpression(connection, _to, action));
		} else if (action.startsWith("loop")) {
			from = from.loop(toExpression(connection, _to, action));
		} else if (action.equals("stop")) {
			from = from.stop();
		} else if (action.equals("end")) {
			from = from.end();
		} else if (action.equals("endChoice")) {
			from = from.endChoice();
		} else if (action.startsWith("delay")) {
			from = from.delay(toExpression(connection, _to, action));
		} else if (action.equals("choice")) {
			from = doChoice(connection, from.choice(), action, _to);
		} else if (action.equals("when") && from instanceof ChoiceDefinition) {
			from = doChoice(connection, (ChoiceDefinition)from, action, _to);
		} else if (action.equals("otherwise") && from instanceof ChoiceDefinition) {
			ChoiceDefinition choice = (ChoiceDefinition)from;
			from = choice.otherwise().to(to);
		} else if (action.equals("if")) {
			from = doChoice(connection, from.choice(), action, _to);
		} else if (action.startsWith("transform")) {
			from = from.transform(toExpression(connection, _to, action));
		} else log.warn("??? action: " + action);

		return from;
	}

	private ProcessorDefinition doRouting(RepositoryConnection connection, ProcessorDefinition from, Resource _to, URI predicate) throws RepositoryException, CamelException, ClassNotFoundException, IOException {
		RDFCollections collection = new RDFCollections(connection);
		if ( !collection.isList(_to) ) {
			log.debug("\tTO multi: "+_to);
			// blank nodes are multi-cast
			if (_to instanceof BNode) {
				return tryResource(connection, from.multicast(),  _to);
			}
			// otherwise, we must either be a bean or a route
			String next = _to.stringValue();
			Object bean = context.getRegistry().lookupByName(next);
			if (bean!=null) {
				log.debug("\tTO bean: "+_to);
				return from.bean(bean);
			} else {
				log.debug("\tTO: "+_to);
				return from.to(next);
			}
		}

		Collection<Value> pipeline = collection.getList(_to);
		log.debug("\tTO pipeline: "+_to+" x "+pipeline.size());
		for(Value pipe: pipeline) {
			if (pipe instanceof Resource) from = tryResource(connection, from, (BNode) pipe);
			else log.warn("Invalid TO: "+pipe);
		}
		return from;
	}

	ProcessorDefinition doChoice(RepositoryConnection connection, ChoiceDefinition from, String action, Value to) throws RepositoryException, CamelException, ClassNotFoundException, IOException {
		if (to instanceof BNode) {
			return tryResource(connection,from,(Resource)to).endChoice();
		} else if (to instanceof Literal) {
			return from.when(new LanguageExpression(getActionLanguage(action), to.stringValue())).endChoice();
		} else if (to instanceof Resource) {
			return tryResource(connection,from.when(toPredicate(connection, to, action)),(Resource)to).endChoice();
		}
		return from;
	}

	private ProcessorDefinition doConvertBodyTo(ProcessorDefinition from, String to) throws ClassNotFoundException {
		if (to.startsWith("bean:")) {
			String type = to.substring(5);
			from = from.convertBodyTo(Class.forName(type));
		} else if (to.startsWith("classpath:")) {
			String type = to.substring(9);
			from = from.convertBodyTo(Class.forName(type));
		}
		return from.to(to);
	}

	private ProcessorDefinition doUnmarshal(String action, ProcessorDefinition from) {
		String type = action.substring("unmarshal:".length());
		DataFormatClause unmarshal = from.unmarshal();
		log.info("Marshall: "+type+" -> "+unmarshal);
		switch(type) {
			case "csv": from = unmarshal.csv(); break;
			case "avro": from = unmarshal.avro(); break;
			case "base64": from = unmarshal.base64(); break;
			case "castor": from = unmarshal.castor(); break;
			case "gzip": from = unmarshal.gzip(); break;
			case "jaxb": from = unmarshal.jaxb(); break;
			case "hl7": from = unmarshal.hl7(); break;
			case "jibx": from = unmarshal.jibx(); break;
			case "protobuf": from = unmarshal.protobuf(); break;
			case "rss": from = unmarshal.rss(); break;
			case "secureXML": from = unmarshal.secureXML(); break;
			case "serialization": from = unmarshal.serialization(); break;
			case "soapjaxb": from = unmarshal.soapjaxb(); break;
			case "string": from = unmarshal.string(); break;
			case "syslog": from = unmarshal.syslog(); break;
			case "tidyMarkup": from = unmarshal.tidyMarkup(); break;
			case "xmlBeans": from = unmarshal.xmlBeans(); break;
			case "xmljson": from = unmarshal.xmljson(); break;
			case "zip": from = unmarshal.zip(); break;
			case "zipFile": from = unmarshal.zipFile(); break;
		}
		return from;
	}

	private ProcessorDefinition doMarshal(String action, ProcessorDefinition from) {
		String type = action.substring("marshal:".length());
		DataFormatClause marshal = from.marshal();
		log.info("Marshall: "+type+" -> "+marshal);
		switch(type) {
			case "csv": from = marshal.csv(); break;
			case "avro": from = marshal.avro(); break;
			case "base64": from = marshal.base64(); break;
			case "castor": from = marshal.castor(); break;
			case "gzip": from = marshal.gzip(); break;
			case "jaxb": from = marshal.jaxb(); break;
			case "hl7": from = marshal.hl7(); break;
			case "jibx": from = marshal.jibx(); break;
			case "protobuf": from = marshal.protobuf(); break;
			case "rss": from = marshal.rss(); break;
			case "secureXML": from = marshal.secureXML(); break;
			case "serialization": from = marshal.serialization(); break;
			case "soapjaxb": from = marshal.soapjaxb(); break;
			case "string": from = marshal.string(); break;
			case "syslog": from = marshal.syslog(); break;
			case "tidyMarkup": from = marshal.tidyMarkup(); break;
			case "xmlBeans": from = marshal.xmlBeans(); break;
			case "xmljson": from = marshal.xmljson(); break;
			case "zip": from = marshal.zip(); break;
			case "zipFile": from = marshal.zipFile(); break;
		}
		return from;
	}

	// *** Make it So ***

	private Predicate toPredicate(RepositoryConnection connection, Value to, String action) {
		if (to instanceof Literal) {
			return new LanguageExpression(getActionLanguage(action), to.stringValue());
		}
		return new RDFBasedPredicate(connection, to.stringValue());
	}

	private Processor toScriptProcessor(RepositoryConnection connection, final Value to, final String language) throws IOException {
		Asset asset = null;
		if (to instanceof Resource) {
			asset = assetRegister.getAsset(to.stringValue(), null);
		} else if (to instanceof Literal) {
			final Literal literal = (Literal)to;
			URI dataType = literal.getDatatype();
			log.debug("\tScript: "+language+"\n"+literal);
			if (dataType==null) {
				// default to Simple Expression, if data-type is missing
				return new Processor() {
					@Override
					public void process(Exchange exchange) throws Exception {
						Expression expression = new LanguageExpression(getActionLanguage(language), literal.stringValue()).createExpression(context);
						Object evaluate = expression.evaluate(exchange, Object.class);
						exchange.getIn().setBody(evaluate);
					}
				};
			}
			asset = new Asset(to.stringValue(), dataType==null?null:dataType.stringValue());
		}

		final Scripting scripting = new Scripting();
		return new ScriptProcessor(scripting, asset);
	}

	private String getActionLanguage(String language) {
		int ix = language.indexOf(":");
		if (ix<0) return "simple";
		return language.substring(ix+1);
	}

	Expression toExpression(RepositoryConnection connection, Value to, String action) throws RepositoryException, CamelException, ClassNotFoundException {
		if (to instanceof Literal) {
			return new LanguageExpression(getActionLanguage(action), to.stringValue());
		}
		return new RDFBasedExpression(connection,to.stringValue());
	}


}
class RDFBasedPredicate implements Predicate {
	RepositoryConnection connection;
	Expression refExpression;

	public RDFBasedPredicate(RepositoryConnection connection, String to) {
		this.connection=connection;
		refExpression = ExpressionBuilder.refExpression(to);
		RDFCamelPlanner.log.debug("Predicate: "+to+" -> "+refExpression);
	}

	@Override
	public boolean matches(Exchange exchange) {
		RDFCamelPlanner.log.debug("refExpression: "+refExpression);
		if (refExpression==null) return false;
		Predicate predicate = PredicateBuilder.toPredicate(refExpression);
		RDFCamelPlanner.log.debug("Predicate: "+predicate);
		return predicate==null?false:predicate.matches(exchange);
	}
}

class RDFBasedExpression implements Expression {
	RepositoryConnection connection;
	Expression expression = null;

	public RDFBasedExpression(RepositoryConnection connection, String to) {
		this.connection=connection;
		expression = ExpressionBuilder.refExpression(to);
	}

	@Override
	public <T> T evaluate(Exchange exchange, Class<T> tClass) {
		RDFCamelPlanner.log.debug("Expression: "+expression+" -> "+expression.getClass()+" -> "+tClass);
		if (expression==null) return null;
		return expression.evaluate(exchange,tClass);
	}
}

class ScriptProcessor implements Processor {
	Scripting scripting;
	Asset asset;

	ScriptProcessor(final Scripting scripting, final Asset asset) {
		this.scripting=scripting;
		this.asset=asset;
	}

	@Override
	public void process(Exchange exchange) throws Exception {
		Object body =  scripting.execute(asset, exchange.getIn().getHeaders());
		exchange.getOut().setBody(body);
		exchange.getOut().setHeaders(exchange.getIn().getHeaders());
		exchange.getOut().setAttachments(exchange.getIn().getAttachments());

	}
}
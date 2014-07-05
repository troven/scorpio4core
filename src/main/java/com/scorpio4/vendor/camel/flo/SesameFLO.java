package com.scorpio4.vendor.camel.flo;

import com.scorpio4.assets.AssetRegister;
import com.scorpio4.assets.SesameAssetRegister;
import com.scorpio4.fact.FactSpace;
import com.scorpio4.vendor.sesame.util.RDFList;
import com.scorpio4.vocab.COMMON;
import org.apache.camel.*;
import org.apache.camel.builder.DataFormatClause;
import org.apache.camel.builder.ExpressionBuilder;
import org.apache.camel.builder.PredicateBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.ChoiceDefinition;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.language.SimpleExpression;
import org.openrdf.model.*;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class SesameFLO extends FLOSupport {
	static protected final Logger log = LoggerFactory.getLogger(SesameFLO.class);

	FactSpace factSpace = null;
	int count = 0;
	AssetRegister assetRegister = null;
	String baseURI = COMMON.CAMEL_FLO;
	URI TO = null;

	public SesameFLO(CamelContext camelContext, FactSpace factSpace) throws Exception {
		super(camelContext);
		init(factSpace);
	}

	private void init(FactSpace factSpace) {
		this.factSpace=factSpace;
		assetRegister = new SesameAssetRegister(factSpace.getConnection());
		TO = factSpace.getConnection().getValueFactory().createURI(getBaseURI() + "to");
	}

	public void setBaseURI(String prefix) {
		this.baseURI = prefix;
	}

	public String getBaseURI() {
		return baseURI;
	}

	public int plan() throws Exception {
		return plan(factSpace);
	}

	public int plan(FactSpace space) throws Exception {
		plan(space,space.getIdentity());
		return count;
	}

	public void plan(FactSpace space, final String routeURI) throws Exception {
		final RepositoryConnection connection = space.getConnection();
		final ValueFactory vf = connection.getValueFactory();
		String FROM = getBaseURI()+"from";
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
					log.debug("\troute:"+from+" @ "+_routeID);
					RouteDefinition tried = from(from);
//					tried.doTry();

					log.debug("\ttry:" + tried);
					ProcessorDefinition ended = tryResource(connection, tried, (Resource) _from);

					if (ended.getOutputs().isEmpty()) {
						log.warn("NO ENDPOINT: "+_from);
						ended.to("log:missing-endpoint");
					}
					log.debug("\tended:" + ended+" <- "+tried);

//					tried.doCatch(Exception.class).to("log:catch:" + routeURI).
//					doFinally().to("log:finally:" + routeURI).
					ended.end();
					log.debug("\tfinally:" + ended);
					count++;
				}
			};
			context.addRoutes(routing);
		}
	}

	protected ProcessorDefinition tryResource(final RepositoryConnection connection, final ProcessorDefinition fromRoute, final Resource from) throws RepositoryException, CamelException, ClassNotFoundException {
		log.debug("Plan From: "+from+" -> "+fromRoute);

		RepositoryResult<Statement> plannedRoutes = connection.getStatements(from, null, null, false);
		while(plannedRoutes.hasNext()) {
			Statement next = plannedRoutes.next();
			String action = next.getPredicate().stringValue();
			Value to = next.getObject();
			if (to instanceof BNode) {
				tryResource(connection, fromRoute, (BNode) to);
			} else  if (action.startsWith(getBaseURI()) && (to instanceof Resource)) {
				tryAction(connection, fromRoute, to, next.getPredicate(), action.substring(getBaseURI().length()));
			}
		}
		return fromRoute;
	}

	protected ProcessorDefinition tryAction(RepositoryConnection connection, ProcessorDefinition from, Value _to, URI predicate, String action) throws RepositoryException, CamelException, ClassNotFoundException {
		String to = _to.stringValue();
		if (to.equals(COMMON.CAMEL_FLO+"stop")) return from.stop();
		if (to.equals(COMMON.CAMEL_FLO+"end")) return from.end();
		if (to.equals(COMMON.CAMEL_FLO+"endChoice")) return from.endChoice();
		log.debug(action+" action: -> "+to);

		if (action.equals("to") || action.equals("do")) {
			from = doAction(connection, from, _to, predicate);
		} else if (action.equals("bean") ) {
			log.info("to-bean: "+to);
			from = from.beanRef(to);
		} else if (action.equals("setBody")) {
			from = from.setBody(toExpression(connection, _to));
		} else if (action.equals("setFaultBody")) {
			from = from.setFaultBody(toExpression(connection, _to));
		} else if (action.equals("validate")) {
			from = from.validate(toExpression(connection, _to));
		} else if (action.equals("validate")) {
			from = from.validate(toExpression(connection, _to));
		} else if (action.equals("validate")) {
			from = from.validate(toExpression(connection, _to));
		} else if (action.equals("split")) {
			from = from.split(toExpression(connection, _to));
		} else if (action.startsWith("marshal:")) {
			from = doMarshal(action, from);
		} else if (action.startsWith("unmarshal:")) {
			from = doUnmarshal(action, from);
		} else if (action.equals("filter")) {
			from = from.filter(new RDFBasedPredicate(connection, to));
		} else if (action.equals("sort")) {
			from = from.sort(toExpression(connection, _to));
		} else if (action.equals("split")) {
			from = from.split(toExpression(connection, _to));
		} else if (action.equals("threads")) {
			from = from.threads().to(to);
		} else if (action.equals("onCompletion")) {
			from = from.onCompletion().to(to);
		} else if (action.equals("transacted")) {
			from = from.transacted().to(to);
		} else if (action.equals("resequence")) {
			from = from.resequence(toExpression(connection, _to));
		} else if (action.equals("convertBodyTo") && action.equals("as")) {
			from = doConvertBodyTo(from, to);
		} else if (action.equals("recipientList")) {
			from = from.recipientList(toExpression(connection, _to));
		} else if (action.equals("loop")) {
			from = from.loop(toExpression(connection, _to));
		} else if (action.equals("stop")) {
			from = from.stop();
		} else if (action.equals("end")) {
			from = from.end();
		} else if (action.equals("endChoice")) {
			from = from.endChoice();
		} else if (action.equals("delay")) {
			from = from.delay(toExpression(connection, _to));
		} else if (action.equals("choice")) {
			from = doChoice(connection, from.choice(), _to);
		} else if (action.equals("when") && from instanceof ChoiceDefinition) {
			from = doChoice(connection, (ChoiceDefinition)from, _to);
		} else if (action.equals("if") && from instanceof ChoiceDefinition) {
			from = doChoice(connection, (ChoiceDefinition)from, _to);
		} else if (action.equals("otherwise") && from instanceof ChoiceDefinition) {
			ChoiceDefinition choice = (ChoiceDefinition)from;
			from = choice.otherwise().to(to);
		} else if (action.equals("transform")) {
			from = from.transform(toExpression(connection, _to));
		} else log.warn("??? action: " + action);

		return from;
	}

	ProcessorDefinition doChoice(RepositoryConnection connection, ChoiceDefinition from, Value to) throws RepositoryException, CamelException, ClassNotFoundException {
		if (to instanceof BNode) {
			return tryResource(connection,from,(Resource)to).endChoice();
		} else if (to instanceof Literal) {
			return from.choice().when(new SimpleExpression(to.stringValue())).endChoice();
		} else if (to instanceof Resource) {
			return tryResource(connection,from.choice().when(new RDFBasedPredicate(connection, to.stringValue())),(Resource)to).endChoice();
		}
		return from;
	}

	Expression toExpression(RepositoryConnection connection, Value to) throws RepositoryException, CamelException, ClassNotFoundException {
		if (to instanceof Literal) {
			return new SimpleExpression(to.stringValue());
		}
		return new RDFBasedExpression(connection,to.stringValue());
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

	private ProcessorDefinition doAction(RepositoryConnection connection, ProcessorDefinition from, Value _to, URI predicate) throws RepositoryException, CamelException, ClassNotFoundException {
		RDFList toList = new RDFList(connection);
		Collection<Value> pipeline = toList.getList((Resource)_to , predicate);
		if (pipeline.isEmpty()) {
			// blank nodes are multi-cast
			if (_to instanceof BNode) return tryResource(connection, from.multicast(), (BNode) _to);
			// literals set the body using SimpleExpressions
			if (_to instanceof Literal) return from.setBody( new SimpleExpression(_to.stringValue()) );
			// otherwise, we must either be a bean ...
			String next = _to.stringValue();
			if (next.startsWith("bean:")) return from.beanRef(next);
			// or a route
			else return from.to(next);
		} else {
			for(Value pipe: pipeline) {
				if (pipe instanceof Resource) from = tryResource(connection, from, (BNode) pipe);
			}
		}
		return from;
	}

}
class RDFBasedPredicate implements Predicate {
	RepositoryConnection connection;
	Expression refExpression;

	public RDFBasedPredicate(RepositoryConnection connection, String to) {
		this.connection=connection;
		refExpression = ExpressionBuilder.refExpression(to);
		SesameFLO.log.debug("Predicate: "+to+" -> "+refExpression);
	}

	@Override
	public boolean matches(Exchange exchange) {
		SesameFLO.log.debug("refExpression: "+refExpression);
		if (refExpression==null) return false;
		Predicate predicate = PredicateBuilder.toPredicate(refExpression);
		SesameFLO.log.debug("Predicate: "+predicate);
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
		SesameFLO.log.debug("Expression: "+expression+" -> "+expression.getClass()+" -> "+tClass);
		if (expression==null) return null;
		return expression.evaluate(exchange,tClass);
	}
}

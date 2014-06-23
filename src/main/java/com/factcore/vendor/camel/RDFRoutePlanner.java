package com.factcore.vendor.camel;

import com.factcore.assets.AssetRegister;
import com.factcore.assets.SesameAssetRegister;
import com.factcore.fact.FactSpace;
import com.factcore.vendor.sesame.util.RDFList;
import com.factcore.vocab.COMMON;
import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.ChoiceDefinition;
import org.apache.camel.model.PipelineDefinition;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.spi.EndpointStrategy;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * Scorpio (c) 2014
 * Module: com.factcore.vendor.camel
 * User  : lee
 * Date  : 21/06/2014
 * Time  : 5:58 PM
 */
public class RDFRoutePlanner extends RoutePlanner {
	static private final Logger log = LoggerFactory.getLogger(RDFRoutePlanner.class);

	public static final String CAMEL_FROM = COMMON.CAMEL+"from";
	public static final String CAMEL_TO = COMMON.CAMEL+"to";
	FactSpace factSpace = null;
	int count = 0;
	AssetRegister assetRegister = null;

	public RDFRoutePlanner(FactSpace factSpace) throws Exception {
		super();
		this.factSpace=factSpace;
		assetRegister = new SesameAssetRegister(factSpace.getConnection());
	}

	public RDFRoutePlanner(CamelContext camelContext, FactSpace factSpace) throws Exception {
		super(camelContext);
		this.factSpace=factSpace;
		assetRegister = new SesameAssetRegister(factSpace.getConnection());
	}

	public int plan() throws Exception {
		plan(factSpace);
		context.addRegisterEndpointCallback(new EndpointStrategy() {
			@Override
			public Endpoint registerEndpoint(String uri, Endpoint endpoint) {
				log.debug("Register: "+uri+" -> "+endpoint);
				return endpoint;
			}
		});
		return count;
	}

	public int plan(FactSpace space) throws Exception {
		plan(space,space.getIdentity());
		return count;
	}

	public void plan(FactSpace space, String routeURI) throws Exception {
		final RepositoryConnection connection = space.getConnection();
		final ValueFactory vf = connection.getValueFactory();
		log.debug("Route: "+routeURI+" -> "+CAMEL_FROM);
		RepositoryResult<Statement> froms = connection.getStatements(vf.createURI(routeURI), vf.createURI(CAMEL_FROM), null, false);
		while(froms.hasNext()){
			Statement next = froms.next();
			final Value _from = next.getObject();
			log.debug("From: "+_from);
			RouteBuilder routing = new org.apache.camel.builder.RouteBuilder() {
				@Override
				public void configure() throws Exception {
					String from = _from.stringValue();
					log.debug("\t"+from);
					RouteDefinition route = plan(connection, vf, from, from(from));
					log.debug("\t" + route);
					route.end();
					count++;
				}
			};
			context.addRoutes(routing);
		}
	}

	static RouteDefinition plan(final RepositoryConnection connection, final ValueFactory vf, final String from, final ProcessorDefinition _route) throws RepositoryException, CamelException {
		ProcessorDefinition route = _route;
		RepositoryResult<Statement> plannedRoutes = connection.getStatements(vf.createURI(from), null, null, false);
		if (!plannedRoutes.hasNext()) {
			return (RouteDefinition)route.to("log:from-missing-to");
		}
		while(plannedRoutes.hasNext()) {
			plan(connection, vf, _route, plannedRoutes.next());
		}
		return (RouteDefinition)route;
	}

	private static void plan(RepositoryConnection connection, ValueFactory vf, ProcessorDefinition route, Statement endpoint) throws CamelException, RepositoryException {
		String action = endpoint.getPredicate().stringValue();
		if (action.startsWith(COMMON.CAMEL)) {
			action = action.substring(COMMON.CAMEL.length());

			String to = endpoint.getObject().stringValue();
			log.debug("\t\t"+action+" -> "+to);
			if (action.equals("to")) {
				route = route.to(to);
			} else if (action.equals("bean")) {
				to = to.substring(5);
				log.info("BeanRoute: "+to);
				route = route.beanRef(to);
			} else if (action.equals("pipeline")) {
				to = to.substring(9);
				RDFList rdfList = new RDFList(connection);
				Collection<Value> pipeTo = rdfList.getList(to, COMMON.CAMEL + "pipeline");
				PipelineDefinition pipe = route.pipeline();
				log.info("PipelineRoute: "+pipeTo);
				for(Value piped:pipeTo) pipe.to(piped.stringValue());
			}else if (action.equals("try")||action.equals("catch")||action.equals("finally")) {
				log.info("TryRoute: "+to);
				log.warn("TRY/CATCH not implemented");
			}
			else if (action.equals("choice")) {
				ChoiceDefinition choice = route.choice();
				choice.when(new RDFCamelChoice(connection, to));
			}
			else throw new CamelException("Unknown Camel Action: "+action);

			plan(connection, vf, to, route);
		}
		else log.trace("Unknown predicate: " + action);
	}

}
class RDFCamelChoice implements Predicate {
	RepositoryConnection connection;
	String to;

	public RDFCamelChoice(RepositoryConnection connection, String to) {
		this.connection=connection;
		this.to=to;
	}

	@Override
	public boolean matches(Exchange exchange) {
		return false;
	}
}

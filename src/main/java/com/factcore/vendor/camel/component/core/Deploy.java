package com.factcore.vendor.camel.component.core;

import com.factcore.deploy.SesameDeployer;
import com.factcore.oops.AssetNotSupported;
import com.factcore.oops.FactException;
import com.factcore.oops.IQException;
import com.factcore.vendor.camel.component.CoreComponent;
import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Scorpio (c) 2014
 * Module: com.factcore.vendor.camel.component.asset
 * User  : lee
 * Date  : 23/06/2014
 * Time  : 4:53 PM
 */
public class Deploy {
	static protected final Logger log = LoggerFactory.getLogger(Deploy.class);
	protected CoreComponent coreComponent;
	protected String uri;

	public Deploy(CoreComponent coreComponent, String uri) throws IOException {
		this.coreComponent=coreComponent;
		this.uri=uri;
	}


	@Handler
	public void execute(Exchange exchange) throws RepositoryException, ExecutionException, IQException, InterruptedException, IOException, AssetNotSupported, FactException {
		Map<String, Object> headers = exchange.getIn().getHeaders();
		Object body = exchange.getIn().getBody();

		SesameDeployer sesameDeployer = new SesameDeployer(coreComponent.getFactSpace());
		sesameDeployer.setDeployScripts(true);
		sesameDeployer.setDeployRDF(true);

		if (uri.equals("") && body!=null) {
			String from = exchange.getFromEndpoint().getEndpointUri();
			log.info("Deploy From: "+from);
			log.info("\t"+headers);
//			sesameDeployer.deploy(from, new StringInputStream());
		} else {
			log.info("Deploy URL: "+uri);
			sesameDeployer.deploy(new URL(uri));
		}

		exchange.getOut().setBody(body);
		exchange.getOut().setHeaders(headers);
	}

}

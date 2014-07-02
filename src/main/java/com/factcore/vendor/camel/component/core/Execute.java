package com.factcore.vendor.camel.component.core;

import com.factcore.iq.exec.Executor;
import com.factcore.iq.exec.Inferring;
import com.factcore.iq.exec.Templating;
import com.factcore.iq.exec.Scripting;
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
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Scorpio (c) 2014
 * Module: com.factcore.vendor.camel.component.core
 * User  : lee
 * Date  : 25/06/2014
 * Time  : 3:38 PM
 */
public class Execute {
	static protected final Logger log = LoggerFactory.getLogger(Deploy.class);
	protected CoreComponent coreComponent;
	protected String uri;


	public Execute(CoreComponent coreComponent, String substring) {
		this.coreComponent=coreComponent;
		this.uri=substring;
	}

	@Handler
	public void execute(Exchange exchange) throws RepositoryException, ExecutionException, IQException, InterruptedException, IOException, AssetNotSupported, FactException {
		Map<String, Object> headers = exchange.getIn().getHeaders();
		Object body = exchange.getIn().getBody();

		Executor executor = new Executor(coreComponent.getFactSpace());
		executor.addExecutable(new Scripting());
		executor.addExecutable(new Templating());
		executor.addExecutable(new Inferring(coreComponent.getFactSpace()));

		Map<String,Future> executed = executor.execute(uri, headers);
		exchange.getOut().setBody(executed);
		exchange.getOut().setHeaders(headers);
	}


}

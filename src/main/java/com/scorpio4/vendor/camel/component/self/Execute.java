package com.scorpio4.vendor.camel.component.self;

import com.scorpio4.ExecutionEnvironment;
import com.scorpio4.iq.exec.*;
import com.scorpio4.oops.AssetNotSupported;
import com.scorpio4.oops.IQException;
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
 * Module: com.scorpio4.vendor.camel.component.self
 * User  : lee
 * Date  : 25/06/2014
 * Time  : 3:38 PM
 */
public class Execute extends Base {
	static protected final Logger log = LoggerFactory.getLogger(Deploy.class);
	Executable executable;

	public Execute(ExecutionEnvironment engine, String uri, Executable executable) throws IOException {
		super(engine, uri);
		this.executable=executable;
	}

	@Handler
	public void execute(Exchange exchange) throws RepositoryException, ExecutionException, IQException, InterruptedException, IOException, AssetNotSupported{
		Map<String, Object> headers = exchange.getIn().getHeaders();

		Executor executor = new Executor(engine.getFactSpace());
		executor.addExecutable(executable);

		Map<String,Future> executed = executor.execute(uri, headers);
		exchange.getOut().setBody(executed);
		exchange.getOut().setHeaders(headers);
	}


}

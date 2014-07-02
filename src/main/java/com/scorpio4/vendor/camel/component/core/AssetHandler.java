package com.scorpio4.vendor.camel.component.core;

import com.scorpio4.iq.exec.Executable;
import com.scorpio4.oops.AssetNotSupported;
import com.scorpio4.oops.IQException;
import com.scorpio4.vendor.camel.component.CoreComponent;
import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.openrdf.repository.RepositoryException;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Scorpio (c) 2014
 * Module: com.scorpio4.vendor.camel.component.asset
 * User  : lee
 * Date  : 23/06/2014
 * Time  : 11:01 AM
 */
public class AssetHandler extends Base {

	public AssetHandler(CoreComponent coreComponent, String substring) throws IOException {
		super(coreComponent,substring);
	}

	@Override
	public Executable getExecutable() {
		return null;
	}

	@Handler
	public void execute(Exchange exchange) throws RepositoryException, ExecutionException, IQException, InterruptedException, IOException, AssetNotSupported {
		super.execute(exchange);
	}
}

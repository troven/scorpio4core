package com.factcore.vendor.camel.component.core;

import com.factcore.assets.AssetHelper;
import com.factcore.iq.exec.Executable;
import com.factcore.oops.AssetNotSupported;
import com.factcore.oops.IQException;
import com.factcore.vendor.camel.component.CoreComponent;
import com.factcore.vocab.COMMON;
import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.camel.util.ExchangeHelper;
import org.openrdf.repository.RepositoryException;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Scorpio (c) 2014
 * Module: com.factcore.vendor.camel.component.asset
 * User  : lee
 * Date  : 23/06/2014
 * Time  : 11:01 AM
 */
public class Raw extends Base {

	public Raw(CoreComponent coreComponent, String substring) throws IOException {
		super(coreComponent,substring);
	}

	@Override
	public Executable getExecutable() {
		return null;
	}

	@Handler
	public void execute(Exchange exchange) throws RepositoryException, ExecutionException, IQException, InterruptedException, IOException, AssetNotSupported {

		Map<String, Object> headers = exchange.getIn().getHeaders();

		String contentType = ExchangeHelper.getContentType(exchange);
		String mimeType = COMMON.MIME_TYPE+contentType;
		if (asset!=null) {
			asset = AssetHelper.getAsset(asset, headers);
			log.debug("Asset: "+getClass().getSimpleName()+" -> "+uri);
			asset.setMimeType(mimeType);
			exchange.getOut().setBody(asset.getContent());
			exchange.getOut().setHeaders(headers);
		} else {
			asset = coreComponent.getAssetRegister().getAsset(uri,mimeType);
			if (asset!=null) log.debug("Found !!");
			log.debug("Missing ("+contentType+") Raw Asset: "+getClass().getSimpleName()+" -> "+uri);
			log.debug("\t{}", headers);
			exchange.getOut().setBody(exchange.getIn().getBody());
			exchange.getOut().setHeaders(headers);
		}
	}
}

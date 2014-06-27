package com.factcore.vendor.camel.component.core;

import com.factcore.assets.AssetHelper;
import com.factcore.iq.exec.Executable;
import com.factcore.oops.AssetNotSupported;
import com.factcore.oops.IQException;
import com.factcore.vendor.camel.component.CoreComponent;
import com.factcore.vocab.COMMON;
import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.camel.Message;
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

		Message in = exchange.getIn();
		Map<String, Object> headers = in.getHeaders();
		Message out = exchange.getOut();
		out.setHeaders(headers);

		String contentType = ExchangeHelper.getContentType(exchange);
		String mimeType = COMMON.MIME_TYPE+contentType;

		if (asset==null) {
			asset = coreComponent.getAssetRegister().getAsset(uri,mimeType);
			if (asset==null) {
				log.debug("Missing (" + contentType + ") Raw Asset: " + getClass().getSimpleName() + " -> " + uri);
				out.setBody(in.getBody());
				return;
			}
		}
		if (asset!=null) {
			asset = AssetHelper.getAsset(asset, headers);
			log.debug("Asset: "+getClass().getSimpleName()+" -> "+uri);
			out.setBody(asset.getContent());
		}
	}
}

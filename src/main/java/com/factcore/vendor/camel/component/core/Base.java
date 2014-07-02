package com.factcore.vendor.camel.component.core;

import com.factcore.assets.Asset;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Scorpio (c) 2014
 * Module: com.factcore.vendor.camel.component.asset
 * User  : lee
 * Date  : 23/06/2014
 * Time  : 3:28 AM
 */
public abstract class Base {
	static protected final Logger log = LoggerFactory.getLogger(Base.class);
	protected CoreComponent coreComponent;
	protected String uri;
	protected Asset asset;

	public Base(CoreComponent coreComponent, String uri) throws IOException {
		this.coreComponent = coreComponent;
		this.uri=uri;
		asset = coreComponent.getAssetRegister().getAsset(uri,null);

		log.info(getClass().getSimpleName()+":Asset -> "+(asset==null?"not found: ":asset.getMimeType())+" -> "+uri+"\n"+asset);
	}

	public abstract Executable getExecutable();

	@Handler
	public void execute(Exchange exchange) throws RepositoryException, ExecutionException, IQException, InterruptedException, IOException, AssetNotSupported {
		Message in = exchange.getIn();
		Message out = exchange.getOut();

		Map<String, Object> headers = in.getHeaders();
		Map body = in.getBody(Map.class);
		String contentType = ExchangeHelper.getContentType(exchange);
		String mimeType = COMMON.MIME_TYPE+contentType;

		if (asset==null) {
			asset = coreComponent.getAssetRegister().getAsset(uri,mimeType);
			if (asset==null) {
				log.debug("Missing (" + contentType + ") Raw Asset: " + getClass().getSimpleName() + " -> " + uri);
				out.setBody(in.getBody());
				out.setFault(true);
				return;
			}
		}
		String endpointUri = exchange.getFromEndpoint().getEndpointUri();
		asset = AssetHelper.getAsset(asset, headers);
		log.debug("Asset: " + getClass().getSimpleName() + " -> " + uri);

		out.setHeaders(headers);
		out.setAttachments(in.getAttachments());

		Executable exec = getExecutable();
		if (exec!=null) {
			log.debug("Executing: "+ endpointUri);
			Map parameters = new HashMap();
			parameters.put("exchange", exchange);
			parameters.put("header", headers);
			parameters.put("body", body);
			Future done = exec.execute(asset, parameters);
			out.setBody(done.get());
		} else {
			log.debug("Asset Only: "+ endpointUri);
			out.setBody(asset.getContent());
		}
	}

}

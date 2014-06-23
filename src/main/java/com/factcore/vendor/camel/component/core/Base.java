package com.factcore.vendor.camel.component.core;

import com.factcore.assets.Asset;
import com.factcore.iq.exec.Executable;
import com.factcore.oops.AssetNotSupported;
import com.factcore.oops.IQException;
import com.factcore.vendor.camel.component.CoreComponent;
import com.factcore.vocab.COMMON;
import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.camel.util.ExchangeHelper;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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
		log.info(getClass().getSimpleName()+"Asset -> "+asset.getMimeType()+" -> "+uri+"\n"+asset);
	}

	public abstract Executable getExecutable();


	@Handler
	public void execute(Exchange exchange) throws RepositoryException, ExecutionException, IQException, InterruptedException, IOException, AssetNotSupported {
		Executable exec = getExecutable();
		Map<String, Object> headers = exchange.getIn().getHeaders();
		Map body = exchange.getIn().getBody(Map.class);
		body = body==null?headers:body;
		String contentType = ExchangeHelper.getContentType(exchange);
		if (asset!=null) {
			log.debug("Asset: "+getClass().getSimpleName()+" -> "+uri+"\n\t"+asset.getMimeType());
			if (asset.getMimeType()==null) asset.setMimeType(COMMON.MIME_TYPE+contentType);
			Future done = exec.execute(asset, body);
			exchange.getOut().setBody(done.get());
			exchange.getOut().setHeaders(headers);
		} else {
			asset = coreComponent.getAssetRegister().getAsset(uri,COMMON.MIME_TYPE+contentType);
			if (asset!=null) log.debug("Found !!");
			log.debug("Missing ("+contentType+") Asset: "+getClass().getSimpleName()+" -> "+uri);
			log.debug("\t{}", headers);
			exchange.getOut().setBody(exchange.getIn().getBody());
			exchange.getOut().setHeaders(headers);
		}
	}
}

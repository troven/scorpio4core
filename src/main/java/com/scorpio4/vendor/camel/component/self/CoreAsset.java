package com.scorpio4.vendor.camel.component.self;

import com.scorpio4.ExecutionEnvironment;
import com.scorpio4.assets.Asset;
import com.scorpio4.assets.AssetHelper;
import com.scorpio4.oops.AssetNotSupported;
import com.scorpio4.oops.FactException;
import com.scorpio4.oops.IQException;
import com.scorpio4.vocab.COMMON;
import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.camel.Message;
import org.apache.camel.util.ExchangeHelper;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Scorpio (c) 2014
 * Module: com.scorpio4.vendor.camel.component.asset
 * User  : lee
 * Date  : 23/06/2014
 * Time  : 3:28 AM
 */
public class CoreAsset extends Base {
	static protected final Logger log = LoggerFactory.getLogger(CoreAsset.class);
	protected Asset asset;

	public CoreAsset(ExecutionEnvironment engine, String assetURI) throws IOException {
		super(engine,assetURI);
		asset = engine.getAssetRegister().getAsset(assetURI,null);
		log.info(getClass().getSimpleName()+":Asset -> "+(asset==null?"not found: ":asset.getMimeType())+" -> "+assetURI+"\n"+asset);
	}

	@Handler
	public void execute(Exchange exchange) throws RepositoryException, ExecutionException, IQException, InterruptedException, IOException, AssetNotSupported, FactException {
		Message in = exchange.getIn();
		Message out = exchange.getOut();

		Map<String, Object> headers = in.getHeaders();
		out.setHeaders(headers);
		out.setAttachments(in.getAttachments());

		String contentType = ExchangeHelper.getContentType(exchange);
		String mimeType = COMMON.MIME_TYPE+contentType;

		if (asset==null) {
			asset = engine.getAssetRegister().getAsset(uri,mimeType);
			if (asset==null) {
				log.debug("Missing (" + contentType + ") Raw Asset: " + getClass().getSimpleName() + " -> " + uri);
				out.setBody(in.getBody());
				out.setFault(true);
				return;
			}
		}
		asset = AssetHelper.getAsset(asset, headers);
		log.debug("Asset: " + getClass().getSimpleName() + " -> " + uri);

		out.setBody(asset.getContent());
	}

}

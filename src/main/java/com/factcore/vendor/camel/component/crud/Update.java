package com.factcore.vendor.camel.component.crud;

import com.factcore.oops.ConfigException;
import com.factcore.oops.FactException;
import com.factcore.vendor.camel.component.CRUDComponent;
import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.camel.Message;

import java.io.IOException;
import java.util.Map;

/**
 * Scorpio (c) 2014
 * Module: com.factcore.vendor.camel.component.crud
 * User  : lee
 * Date  : 23/06/2014
 * Time  : 1:50 AM
 */
public class Update extends Base {
	public Update(CRUDComponent crud, String asset, Map<String, Object> params) throws FactException, IOException, ConfigException {
		super(crud, asset);
	}

	@Handler
	public void update(Exchange exchange) throws FactException, IOException, ConfigException {
		Message out = exchange.getOut();
		Message in = exchange.getIn();
		Map<String, Object> headers = in.getHeaders();
		Map body = in.getBody(Map.class);
		body = body==null?headers:body;

		log.debug("UPDATE: "+assetURI+" -> "+exchange);
		headers.put("sparql.update", assetURI);

		log.debug("head: " + headers);
		log.debug("body: "+ in.getBody());
		Map updated = crud.getCRUD().update(body);
		out.setBody(updated);
		out.setHeaders(headers);
	}
}

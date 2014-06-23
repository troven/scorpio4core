package com.factcore.vendor.camel.component.crud;

import com.factcore.oops.ConfigException;
import com.factcore.oops.FactException;
import com.factcore.vendor.camel.component.CRUDComponent;
import org.apache.camel.Exchange;
import org.apache.camel.Handler;

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
	public Update(CRUDComponent crud, String asset) throws FactException, IOException, ConfigException {
		super(crud, asset);
	}

	@Handler
	public void update(Exchange exchange) throws FactException, IOException, ConfigException {
		log.debug("UPDATE: "+assetURI+" -> "+exchange);
		Map<String, Object> headers = exchange.getIn().getHeaders();
		Map body = exchange.getIn().getBody(Map.class);
		body = body==null?headers:body;
		headers.put("sparql.update", assetURI);

		log.debug("head: "+headers);
		log.debug("body: "+exchange.getIn().getBody());
		Map updated = crud.getCRUD().update(body);
		exchange.getOut().setBody(updated);
		exchange.getOut().setHeaders(headers);
	}
}

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
public class Delete extends Base {
	public Delete(CRUDComponent crud, String asset, Map<String, Object> params) throws FactException, IOException, ConfigException {
		super(crud, asset);
	}

	@Handler
	public void delete(Exchange exchange) throws FactException, IOException, ConfigException {
		log.debug("DELETE: "+assetURI+" -> "+exchange);
		Map<String, Object> headers = exchange.getIn().getHeaders();
		Map body = exchange.getIn().getBody(Map.class);
		body = body==null?headers:body;
		headers.put("sparql.delete", assetURI);

		log.debug("head: "+headers);
		log.debug("body: "+exchange.getIn().getBody());
		Map deleted = crud.getCRUD().delete(body);
		exchange.getOut().setBody(deleted);
		exchange.getOut().setHeaders(headers);
	}
}

package com.factcore.vendor.camel.component.crud;

import com.factcore.oops.ConfigException;
import com.factcore.oops.FactException;
import com.factcore.vendor.camel.component.CRUDComponent;
import org.apache.camel.Exchange;
import org.apache.camel.Handler;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

/**
 * Scorpio (c) 2014
 * Module: com.factcore.vendor.camel.component.crud
 * User  : lee
 * Date  : 23/06/2014
 * Time  : 1:50 AM
 */
public class Read extends Base {


	public Read(CRUDComponent crud, String asset) throws FactException, IOException, ConfigException {
		super(crud, asset);
	}

	@Handler
	public void read(Exchange exchange) throws FactException, IOException, ConfigException {
		Map<String, Object> headers = exchange.getIn().getHeaders();
		Map body = exchange.getIn().getBody(Map.class);
		body = body==null?headers:body;
		headers.put("sparql.read", assetURI);

		log.debug("READ: "+assetURI+" -> "+exchange);
		log.debug("head: "+headers);
		log.debug("body: "+exchange.getIn().getBody());
		Collection<Map> read = crud.getCRUD().read(assetURI, body);
		exchange.getOut().setBody(read);
		exchange.getOut().setHeaders(headers);
	}
}

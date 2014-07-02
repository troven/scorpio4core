package com.scorpio4.vendor.camel.component.self;

import com.scorpio4.oops.ConfigException;
import com.scorpio4.oops.FactException;
import com.scorpio4.vendor.camel.component.SelfComponent;
import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 * Scorpio (c) 2014
 * Module: com.scorpio4.vendor.camel.component.self
 * User  : lee
 * Date  : 23/06/2014
 * Time  : 5:19 PM
 */
public class Lifecycle {
	static protected final Logger log = LoggerFactory.getLogger(Lifecycle.class);

	SelfComponent self;
	String action, uri;

	public Lifecycle(SelfComponent self, String uri, String action, Map<String, Object> params) {
		this.self=self;
		this.uri=uri;
		this.action=action;
	}

	@Handler
	public void doAction(Exchange exchange) throws FactException, IOException, ConfigException {
		Map<String, Object> headers = exchange.getIn().getHeaders();
		Map body = exchange.getIn().getBody(Map.class);
		body = body==null?headers:body;

		log.info("Lifecycle: "+action+" @ "+uri);
		log.info("\t"+headers);

		exchange.getOut().setBody(body);
		exchange.getOut().setHeaders(headers);
	}
}

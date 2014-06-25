package com.factcore.vendor.camel.component;

import com.factcore.vendor.camel.component.any23.Any23Handler;
import org.apache.camel.Endpoint;
import org.apache.camel.component.bean.BeanEndpoint;
import org.apache.camel.component.bean.BeanProcessor;
import org.apache.camel.component.bean.ClassComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Scorpio (c) 2014
 * Module: com.factcore.vendor.camel.component
 * User  : lee
 * Date  : 24/06/2014
 * Time  : 2:43 PM
 */
public class Any23Component extends ClassComponent {
	static protected final Logger log = LoggerFactory.getLogger(CoreComponent.class);

	public Any23Component() {
	}

	protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
		return new BeanEndpoint(remaining, this, new BeanProcessor(new Any23Handler(), getCamelContext()));
	}
}

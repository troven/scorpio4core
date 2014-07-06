package com.scorpio4.vendor.camel.component;

import com.scorpio4.ExecutionEnvironment;
import org.apache.camel.Exchange;
import org.apache.camel.Handler;

/**
 * scorpio4-oss (c) 2014
 * Module: com.scorpio4.vendor.camel.component
 * User  : lee
 * Date  : 6/07/2014
 * Time  : 5:44 PM
 */
public class EngineLifecycle {
	ExecutionEnvironment engine;

	public EngineLifecycle(ExecutionEnvironment engine) {
	}

	@Handler
	public void handle(Exchange exchange) {

	}
}

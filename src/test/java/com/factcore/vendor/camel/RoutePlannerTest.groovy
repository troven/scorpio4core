package com.factcore.vendor.camel

import org.junit.Test
/**
 * Scorpio (c) 2014
 * Module: com.factcore.vendor.camel
 * User  : lee
 * Date  : 19/06/2014
 * Time  : 11:46 AM
 *
 *
 */
class RoutePlannerTest extends groovy.util.GroovyTestCase{

	@Test
	void testRoute() {
		RoutePlanner routing = new RoutePlanner();
		routing.start()
		def plan = routing.plan("direct://RoutingTest", "file://temp/to.test/");
		assert plan!=null;

		log.println("-"*40)
		routing.plan("direct://RoutingTest2", "direct://RoutingTest");

		log.println("-"*40)
		def result = routing.plan("restlet://demo/", "file://temp/restlet/" );
		assert result!=null;
		println result;

		log.println("="*40)
		result = routing.trigger("direct://RoutingTest2", "TESTING #2\n"+new Date() );
		assert result!=null;
		println result;

		Thread.sleep(10000)
	}
}

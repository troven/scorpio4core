package com.factcore.vendor.camel

import com.factcore.vendor.camel.planner.RoutePlanner
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
		def plan = routing.plan("direct://test", "file://temp/to.test/");
		assert plan!=null;

		log.println("="*40)
		def result = routing.trigger("direct://test", "TESTING #2\n"+new Date() );
		assert result!=null;
		println result;
	}
}

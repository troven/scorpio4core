package com.scorpio4.vendor.camel

import com.scorpio4.vendor.camel.planner.FLOSupport
import org.junit.Test
/**
 * Scorpio (c) 2014
 * Module: com.scorpio4.vendor.camel
 * User  : lee
 * Date  : 19/06/2014
 * Time  : 11:46 AM
 *
 *
 */
class FLOSupportTest extends groovy.util.GroovyTestCase{

	@Test
	void testRoute() {
		FLOSupport routing = new FLOSupport();
		routing.start()
		def plan = routing.plan("direct://test", "file://temp/to.test/");
		assert plan!=null;

		log.println("="*40)
		def result = routing.trigger("direct://test", "TESTING #2\n"+new Date() );
		assert result!=null;
		println result;
	}
}

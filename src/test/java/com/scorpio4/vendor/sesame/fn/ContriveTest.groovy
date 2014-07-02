package com.scorpio4.vendor.sesame.fn

import com.factcore.util.Stopwatch
import com.factcore.vocab.COMMON
import org.junit.Test

/**
 * Fact:Core (c) 2012
 * User: lee
 * Date: 2/08/13
 * Time: 3:19 PM
 * 
 * This code does something useful
 */
class ContriveTest {

	@Test
	void testGetURI() {
		Contrive contrive = new Contrive();
		assert contrive.getURI().startsWith(COMMON.FN+"contrive");
	}

	void testEvaluateContrive() {
		Contrive contrive = new Contrive();
		String contrived = contrive.evaluate("urn:example:","123");
		assert contrived == "urn:example:40bd001563085fc35165329ea1ff5c5ecbdbbeef";
		contrived = contrive.evaluate("urn:example:","123:456");
		assert contrived == "urn:example:3b32005cba4ab5d70a0f42225b62e8f3863c2433";
	}

	void testEvaluateTimed() {
		Contrive contrive = new Contrive();
		def stopwatch = new Stopwatch();
		int count = 1000000;
		for(int i=0;i<count;i++) {
			contrive.evaluate("urn:example:",""+i);
		}
		double avgTime = stopwatch.getTotalTime()/count;
		println "Contrived ${count} URIs in: ${stopwatch} -> ${avgTime}ms each";
	}
}

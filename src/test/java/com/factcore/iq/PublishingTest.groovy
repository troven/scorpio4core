package com.factcore.iq

import com.factcore.assets.Asset
import com.factcore.iq.exec.Templating
/**
 * Fact:Core (c) 2014
 * Module: com.factcore.iq
 * User  : lee
 * Date  : 17/06/2014
 * Time  : 10:02 PM
 *
 *
 */
class PublishingTest extends GroovyTestCase {

    void testWith() {
        def publisher = new Templating();
	    Asset template = new Asset("hello \${greeting}");
        def future = publisher.execute(template, [ "greeting": "world"])
        assert future!=null;
        Writable writable = future.get();
        assert writable!=null;
        assert writable.toString().equals("hello world");
    }
}

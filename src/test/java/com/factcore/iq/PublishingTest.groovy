package com.factcore.iq

import com.factcore.assets.Asset
import com.factcore.iq.exec.Publishing

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
        def publisher = new Publishing(new Asset("hello \${greeting}"));
        def future = publisher.with([ "greeting": "world"])
        assert future!=null;
        Writable template = future.get();
        assert template!=null;
        assert template.toString().equals("hello world");
    }
}

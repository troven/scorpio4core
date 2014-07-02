package com.scorpio4.iq

import com.factcore.assets.Asset
import com.factcore.iq.exec.Scripting
import com.factcore.vocab.COMMON
import org.junit.Test

import java.util.concurrent.Future
/**
 * Fact:Core (c) 2014
 * Module: com.factcore.iq
 * User  : lee
 * Date  : 17/06/2014
 * Time  : 9:44 PM
 *
 *
 */
class ScriptTest extends groovy.util.GroovyTestCase {

    @Test
    void testWith() {
        Scripting script = new Scripting();
	    Asset asset = new Asset("return 'hello world'", COMMON.MIME_GROOVY);
        Future done = script.execute(asset, new HashMap());
        assert done.get()!=null;
        assert done.get() == "hello world";
    }

}

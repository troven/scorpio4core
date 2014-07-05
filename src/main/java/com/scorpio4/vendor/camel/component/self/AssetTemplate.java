package com.scorpio4.vendor.camel.component.self;

import com.scorpio4.ExecutionEnvironment;
import com.scorpio4.iq.exec.Templating;

import java.io.IOException;

/**
 * Scorpio (c) 2014
 * Module: com.scorpio4.vendor.camel.component.asset
 * User  : lee
 * Date  : 23/06/2014
 * Time  : 3:28 AM
 */
public class AssetTemplate extends Execute {

	public AssetTemplate(ExecutionEnvironment engine, String uri) throws IOException {
		super(engine,uri, new Templating());
	}

}

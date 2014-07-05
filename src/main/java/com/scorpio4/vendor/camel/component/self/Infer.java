package com.scorpio4.vendor.camel.component.self;

import com.scorpio4.ExecutionEnvironment;
import com.scorpio4.iq.exec.Inferring;

import java.io.IOException;

/**
 * Scorpio (c) 2014
 * Module: com.scorpio4.vendor.camel.component.asset
 * User  : lee
 * Date  : 23/06/2014
 * Time  : 3:28 AM
 */
public class Infer extends Execute {

	public Infer(ExecutionEnvironment engine, String uri) throws IOException {
		super(engine,uri, new Inferring(engine.getFactSpace()));
	}


}

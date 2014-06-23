package com.factcore.vendor.camel.component.core;

import com.factcore.iq.exec.Executable;
import com.factcore.iq.exec.Scripting;
import com.factcore.vendor.camel.component.CoreComponent;

import java.io.IOException;

/**
 * Scorpio (c) 2014
 * Module: com.factcore.vendor.camel.component.asset
 * User  : lee
 * Date  : 23/06/2014
 * Time  : 3:28 AM
 */
public class Script extends Base {

	public Script(CoreComponent coreComponent, String substring) throws IOException {
		super(coreComponent,substring);
	}

	@Override
	public Executable getExecutable() {
		return new Scripting();
	}
}

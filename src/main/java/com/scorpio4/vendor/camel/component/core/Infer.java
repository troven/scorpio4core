package com.scorpio4.vendor.camel.component.core;

import com.scorpio4.iq.exec.Executable;
import com.scorpio4.iq.exec.Inferring;
import com.scorpio4.vendor.camel.component.CoreComponent;

import java.io.IOException;

/**
 * Scorpio (c) 2014
 * Module: com.scorpio4.vendor.camel.component.asset
 * User  : lee
 * Date  : 23/06/2014
 * Time  : 3:28 AM
 */
public class Infer extends Base{

	public Infer(CoreComponent coreComponent, String substring) throws IOException {
		super(coreComponent,substring);
	}

	@Override
	public Executable getExecutable() {
		return new Inferring(coreComponent.getFactSpace());
	}

}

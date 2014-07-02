package com.scorpio4.vendor.camel.component.core;

import com.scorpio4.iq.exec.Executable;
import com.scorpio4.iq.exec.SPARQLing;
import com.scorpio4.vendor.camel.component.CoreComponent;

import java.io.IOException;

/**
 * Scorpio (c) 2014
 * Module: com.scorpio4.vendor.camel.component.asset
 * User  : lee
 * Date  : 23/06/2014
 * Time  : 3:28 AM
 */
public class SPARQL extends Base {

	public SPARQL(CoreComponent coreComponent, String substring) throws IOException {
		super(coreComponent,substring);
	}

	@Override
	public Executable getExecutable() {
		return new SPARQLing(coreComponent.getFactSpace());
	}
}

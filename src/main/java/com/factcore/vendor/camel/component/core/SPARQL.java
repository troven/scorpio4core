package com.factcore.vendor.camel.component.core;

import com.factcore.iq.exec.Executable;
import com.factcore.iq.exec.SPARQLing;
import com.factcore.vendor.camel.component.CoreComponent;

import java.io.IOException;

/**
 * Scorpio (c) 2014
 * Module: com.factcore.vendor.camel.component.asset
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

package com.scorpio4.vendor.camel.component;

import com.scorpio4.assets.AssetRegister;
import com.scorpio4.fact.FactSpace;
import com.scorpio4.vendor.camel.component.core.*;
import org.apache.camel.Endpoint;
import org.apache.camel.component.bean.BeanEndpoint;
import org.apache.camel.component.bean.BeanProcessor;
import org.apache.camel.component.bean.ClassComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Scorpio (c) 2014
 * Module: com.scorpio4.vendor.camel
 * User  : lee
 * Date  : 22/06/2014
 * Time  : 11:51 PM
 */
public class CoreComponent extends ClassComponent {
	static protected final Logger log = LoggerFactory.getLogger(CoreComponent.class);

	protected FactSpace factSpace;
	protected AssetRegister assetRegister;

	public CoreComponent(FactSpace factSpace, AssetRegister assetRegister) {
		this.factSpace=factSpace;
		this.assetRegister=assetRegister;
	}

	protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
		Object executable = null;
		if (remaining.startsWith("script:")) {
			executable = new Script(this, remaining.substring(7));
		} else if (remaining.startsWith("infer:")) {
			executable = new Infer(this, remaining.substring(6));
		} else if (remaining.startsWith("publish:")) {
			executable = new Publish(this, remaining.substring(8));
		} else if (remaining.startsWith("sparql:")) {
			executable = new SPARQL(this, remaining.substring(7));
		} else if (remaining.startsWith("asset:")) {
			executable = new AssetHandler(this, remaining.substring(6));
		} else if (remaining.startsWith("deploy:")) {
			executable = new Deploy(this, remaining.substring(7));
		} else if (remaining.startsWith("execute:")) {
			executable = new Execute(this, remaining.substring(8));
		}

		return executable==null?null:new BeanEndpoint(uri, this, new BeanProcessor(executable, getCamelContext()));
	}

	public FactSpace getFactSpace() {
		return factSpace;
	}

	public AssetRegister getAssetRegister() {
		return assetRegister;
	}
}

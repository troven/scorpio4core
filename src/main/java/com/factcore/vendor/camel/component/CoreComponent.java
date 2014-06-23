package com.factcore.vendor.camel.component;

import com.factcore.assets.AssetRegister;
import com.factcore.fact.FactSpace;
import com.factcore.vendor.camel.component.core.*;
import org.apache.camel.Endpoint;
import org.apache.camel.component.bean.BeanEndpoint;
import org.apache.camel.component.bean.BeanProcessor;
import org.apache.camel.component.bean.ClassComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Scorpio (c) 2014
 * Module: com.factcore.vendor.camel
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
		if (remaining.startsWith("script:")) {
			return new BeanEndpoint(uri, this, new BeanProcessor(new Script(this, remaining.substring(7)), getCamelContext()));
		} else if (remaining.startsWith("infer:")) {
			return new BeanEndpoint(uri, this, new BeanProcessor(new Infer(this, remaining.substring(6)), getCamelContext()));
		} else if (remaining.startsWith("publish:")) {
			return new BeanEndpoint(uri, this, new BeanProcessor(new Publish(this, remaining.substring(8)), getCamelContext()));
		} else if (remaining.startsWith("sparql:")) {
			return new BeanEndpoint(uri, this, new BeanProcessor(new SPARQL(this, remaining.substring(7)), getCamelContext()));
		} else if (remaining.startsWith("asset:")) {
			return new BeanEndpoint(uri, this, new BeanProcessor(new Raw(this, remaining.substring(6)), getCamelContext()));
		} else if (remaining.startsWith("deploy:")) {
			return new BeanEndpoint(uri, this, new BeanProcessor(new Deploy(this, remaining.substring(7)), getCamelContext()));
		}

		return null;
	}

	public FactSpace getFactSpace() {
		return factSpace;
	}

	public AssetRegister getAssetRegister() {
		return assetRegister;
	}
}

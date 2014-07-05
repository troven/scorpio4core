package com.scorpio4;

import com.scorpio4.assets.AssetRegister;
import com.scorpio4.fact.FactSpace;
import com.scorpio4.util.Identifiable;
import com.scorpio4.vendor.sesame.RepositoryManager;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

import java.util.Map;

/**
 * scorpio4-oss (c) 2014
 * Module: com.scorpio4.runtime
 * User  : lee
 * Date  : 5/07/2014
 * Time  : 9:58 PM
 */
public interface ExecutionEnvironment extends Identifiable {

	public FactSpace getFactSpace();

	public BeanDefinitionRegistry getRegistry();

	public AssetRegister getAssetRegister();

	public RepositoryManager getRepositoryManager();

	public Map getConfig();
}

package com.scorpio4.runtime;

import com.scorpio4.assets.AssetRegister;
import com.scorpio4.util.Identifiable;
import org.openrdf.repository.Repository;
import org.openrdf.repository.sail.config.RepositoryResolver;
import org.springframework.context.ApplicationContext;

import java.util.Map;

/**
 * scorpio4-oss (c) 2014
 * Module: com.scorpio4.runtime
 * User  : lee
 * Date  : 5/07/2014
 * Time  : 9:58 PM
 */
public interface ExecutionEnvironment extends Identifiable {

	public ApplicationContext getRegistry();

	public AssetRegister getAssetRegister();

	public RepositoryResolver getRepositoryManager();

	public Repository getRepository();

	public Map getConfig();

	public void reboot() throws Exception;
}
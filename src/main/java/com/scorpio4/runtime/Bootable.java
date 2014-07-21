package com.scorpio4.runtime;

/**
 * scorpio4-oss (c) 2014
 * Module: com.scorpio4.runtime
 * @author lee
 * Date  : 9/07/2014
 * Time  : 8:51 PM
 *
 * @author lee
 *
 * Represents an core component that self-configures (boots) using the resources encapsulated by
 * the ExecutionEnvironment.
 *
 */
public interface Bootable {

	public void boot(ExecutionEnvironment engine) throws Exception;

}

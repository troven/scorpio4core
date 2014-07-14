package com.scorpio4.oops;
/*
 *   Scorpio4 - Apache Licensed
 *   Copyright (c) 2009-2014 Lee Curtis, All Rights Reserved.
 *
 *
 */
/**
 * Scorpio4 (c) 2010-2013
 * @author lee
 * Date: 24/01/13
 * Time: 12:54 AM
 * <p/>
 * Thrown when state/configuration is invalid
 */
public class ConfigException extends Exception {

	public ConfigException(String s) {
		super(s);
	}

	public ConfigException(String s, Throwable throwable) {
		super(s,throwable);
	}

	public String toString() {
		return getMessage();
	}
}

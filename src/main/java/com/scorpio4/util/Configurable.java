package com.scorpio4.util;
/*
 *   Scorpio4 - Apache Licensed
 *   Copyright (c) 2009-2014 Lee Curtis, All Rights Reserved.
 *
 *

 */

import com.scorpio4.oops.ConfigException;

import java.util.Map;

/**
 * Scorpio4 (c) 2010-2013
 * @author lee
 *
 */
public interface Configurable {

    public void configure(Map config) throws ConfigException;
	public Map getConfiguration();

}

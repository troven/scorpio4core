package com.scorpio4.util;
/*
 *   Scorpio4 - Apache Licensed
 *   Copyright (c) 2009-2014 Lee Curtis, All Rights Reserved.
 *
 *

 */

/**
 * Scorpio4 (c) 2010-2013
 * @author lee
 * Date: 14/01/13
 * Time: 4:54 AM
 *
 * @author lee
 *
 * Represents an entity that is known by a runtime-unique identifier.
 * In most cases the return String.class should be a URI.
 */
public interface Identifiable {

	public String getIdentity();
}

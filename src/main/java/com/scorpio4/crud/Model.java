package com.scorpio4.crud;
/*
 *   Scorpio4 - Apache Licensed
 *   Copyright (c) 2009-2014 Lee Curtis, All Rights Reserved.
 *
 *
 */

import com.scorpio4.util.Identifiable;

import java.util.Map;

/**
 * Scorpio4 (c) 2010-2013
 * @author lee
 * Date: 14/01/13
 * Time: 4:54 AM
 *
 * Represents an Identifiable collection of name/value pairs.
 *
 * The runtime package implements a GenericModel which delegates to a Map.
 *
 */
public interface Model extends Map, Identifiable {

}

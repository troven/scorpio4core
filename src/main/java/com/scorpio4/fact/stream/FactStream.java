package com.scorpio4.fact.stream;
/*
 *   Scorpio4 - Apache Licensed
 *   Copyright (c) 2009-2014 Lee Curtis, All Rights Reserved.
 *
 *
 */

import com.scorpio4.oops.FactException;
import com.scorpio4.util.Identifiable;

/**
 * Scorpio4 (c) 2012
 * @author lee
 * Date: 4/09/13
 * Time: 4:15 PM
 *
 * @author lee
 *
 * FactStream - can be used for reading or writing.
 * When writing each fact is committed to the underlying source
 * When reading it functions as a callback/listener
 *
 */

public interface FactStream extends Identifiable {

	public void fact(String s, String p, Object o) throws FactException;

    public void fact(String s, String p, Object o, String xsdType) throws FactException;
}

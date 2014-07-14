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
 * Date: 31/08/12
 * Time: 7:57 AM
 * <p/>
 * Thrown when data or transformation operations fails
 */
public class FactException extends Exception {

	public FactException(String msg) {
		super(msg);
	}

	public FactException(String msg, Throwable thrown) {
		super(msg, thrown);
	}

	public FactException(Exception t) {
		super(t);
	}
}

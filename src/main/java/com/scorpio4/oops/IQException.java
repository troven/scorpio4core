package com.scorpio4.oops;
/*
 *   Scorpio4 - Apache Licensed
 *   Copyright (c) 2009-2014 Lee Curtis, All Rights Reserved.
 *
 *
 */
/**
 * Created with IntelliJ IDEA.
 * @author lee
 * Date: 19/10/12
 * Time: 1:49 AM
 *
 * Thrown when some dynamically invoked behaviour fails.
 * Typical examples are scripts and other executable assets
 */
public class IQException extends Exception {

	public IQException(String msg) {
		super(msg);
	}

	public IQException(Throwable thrown) {
		super("Re-Thrown: "+thrown.getMessage(), thrown);
	}

	public IQException(String msg, Throwable thrown) {
		super(msg, thrown);
	}

}

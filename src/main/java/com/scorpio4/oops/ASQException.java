package com.scorpio4.oops;

/**
 * scorpio4-oss (c) 2014
 * Module: com.scorpio4.oops
 * User  : lee
 * Date  : 16/07/2014
 * Time  : 1:30 PM
 */
public class ASQException extends Exception {

	public ASQException(String s) {
		super(s);
	}

	public ASQException(String s, Throwable throwable) {
		super(s,throwable);
	}

}

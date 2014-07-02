package com.scorpio4.oops;

/**
 * Scorpio (c) 2014
 * Module: com.scorpio4.oops
 * User  : lee
 * Date  : 23/06/2014
 * Time  : 3:21 AM
 */
public class AssetNotSupported extends Exception {

	public AssetNotSupported(String msg) {
		super(msg);
	}

	public AssetNotSupported(String msg, Exception e) {
		super(msg,e);
	}
}

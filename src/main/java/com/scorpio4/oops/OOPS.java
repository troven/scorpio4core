package com.scorpio4.oops;

/**
 * scorpio4-oss (c) 2014
 * Module: com.scorpio4.oops
 * User  : lee
 * Date  : 19/07/2014
 * Time  : 11:26 PM
 *
 * Catalog of Exception URIs
 *
 */
public class OOPS {
	public final static String NS = "http://scorpio4.com/v1/oops/";

	public final static String LOD_RESPONSE = NS + "";

	public String oops(Object src, String uuid) {
		return NS+uuid;
	}

}

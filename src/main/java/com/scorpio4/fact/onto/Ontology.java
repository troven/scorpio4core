package com.scorpio4.fact.onto;

/**
 * Scorpio (c) 2014
 * Module: com.scorpio4.fact
 * User  : lee
 * Date  : 26/06/2014
 * Time  : 9:26 AM
 */
public interface Ontology {

	public String getLabel(String uri);
	public String getComment(String uri);
	public String getRange(String uri);
	public String getDomain(String uri);
	public String getType(String uri);
}

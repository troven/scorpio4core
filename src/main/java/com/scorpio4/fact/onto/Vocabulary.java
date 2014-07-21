package com.scorpio4.fact.onto;

/**
 * Scorpio (c) 2014
 * Module: com.scorpio4.fact
 * @author lee
 * Date  : 26/06/2014
 * Time  : 9:26 AM
 *
 * @author lee
 *
 * Represents a simplfied abstraction of an RDFS vocabulary,
 * the results are specifically for end-user viewing rather than programmatic interrogation.
 *
 * The implementation must return a String representation for each method call.
 * If a valid representation is not available for a known term, then the implementation
 * must return some user-presentatable representation of URI, for examplea a CURIE.
 *
 * A NULL must only be returned when the URI is not a term defined by the vocabulary.
 */
public interface Vocabulary {

	public boolean isKnown(String uri);

	public String getLabel(String uri);
	public String getComment(String uri);
	public String getRange(String uri);
	public String getDomain(String uri);
	public String getType(String uri);
}

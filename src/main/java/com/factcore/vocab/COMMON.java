package com.factcore.vocab;
/*
 *   Fact:Core - CONFIDENTIAL
 *   Unpublished Copyright (c) 2009-2014 Lee Curtis, All Rights Reserved.
 *
 *   NOTICE:  All information contained herein is, and remains the property of Lee Curtis. The intellectual and technical concepts contained
 *   herein are proprietary to Lee Curtis and may be covered by Australian, U.S. and Foreign Patents, patents in process, and are protected by trade secret or copyright law.
 *   Dissemination of this information or reproduction of this material is strictly forbidden unless prior written permission is obtained
 *   from Lee Curtis.  Access to the source code contained herein is hereby forbidden to anyone except current Lee Curtis employees, managers or contractors who have executed
 *   Confidentiality and Non-disclosure agreements explicitly covering such access.
 *
 *   The copyright notice above does not evidence any actual or intended publication or disclosure  of  this source code, which includes
 *   information that is confidential and/or proprietary, and is a trade secret, of Lee Curtis.   ANY REPRODUCTION, MODIFICATION, DISTRIBUTION, PUBLIC  PERFORMANCE,
 *   OR PUBLIC DISPLAY OF OR THROUGH USE  OF THIS  SOURCE CODE  WITHOUT  THE EXPRESS WRITTEN CONSENT OF LEE CURTIS IS STRICTLY PROHIBITED, AND IN VIOLATION OF APPLICABLE
 *   LAWS AND INTERNATIONAL TREATIES.  THE RECEIPT OR POSSESSION OF  THIS SOURCE CODE AND/OR RELATED INFORMATION DOES NOT CONVEY OR IMPLY ANY RIGHTS
 *   TO REPRODUCE, DISCLOSE OR DISTRIBUTE ITS CONTENTS, OR TO MANUFACTURE, USE, OR SELL ANYTHING THAT IT  MAY DESCRIBE, IN WHOLE OR IN PART.
 *
 */
/**
 * Fact:Core (c) 2010-2013
 *
 * Define namespaces for essential core vocabularies
 *
 * 1st May 2014 - Licensed to Apscore
 *
 */
public interface COMMON {

    public static String OWL = "http://www.w3.org/2002/07/owl#";
	public static String RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	public static String RDFS = "http://www.w3.org/2000/01/rdf-schema#";
	public static String RDFS_TYPE = RDF+"type";
    public static String RDFS_SUBPROPERTY = RDFS+"subPropertyOf";
	public static String A = RDFS_TYPE;
	public static String RDFS_SUBCLASS = RDFS+"subClassOf";
	public static String ASA = RDFS_SUBCLASS ;
    public static String SAMEAS = OWL+"sameAs";
	public static String LABEL = RDFS+"label" ;
	public static String COMMENT = RDFS+"comment" ;
    public static String DOMAIN = RDFS+"domain" ;
    public static String RANGE = RDFS+"range" ;

    // Common Vocabularies;

	public static String XSD = "http://www.w3.org/2001/XMLSchema#";
    public static String DC = "http://purl.org/dc/elements/1.1/";
    public static String DCTERMS = "http://purl.org/dc/terms/";
    public static String SKOS = "http://www.w3.org/2004/02/skos/core#";
    public static String GEO = "http://www.w3.org/2003/01/geo/wgs84_pos#";
    public static final String ACL = "http://www.w3.org/ns/auth/acl#";

    public static String MIME_TYPE = "http://www.iana.org/assignments/media-types/";

    public static String MIME_GROOVY = MIME_TYPE+ "application/x-groovy";
    public static String MIME_JAVASCRIPT = MIME_TYPE+ "application/x-javascript";
    public static String MIME_PLAIN = MIME_TYPE+"text/plain";
    public static String MIME_JSON = MIME_TYPE+ "application/json";
    public static String MIME_PROPERTIES = MIME_TYPE+ "text/x-java-properties";
    public static String MIME_SPARQL = MIME_TYPE+"application/x-sparql-query";
    public static String MIME_SQL = MIME_TYPE+"text/x-sql";

    public static final String MIME_HTML = MIME_TYPE+ "text/html";
    public static final String MIME_XHTML = MIME_TYPE+ "application/xhtml+xml";
    public static final String MIME_CSV = MIME_TYPE+"text/csv";
    public static final String MIME_XML = MIME_TYPE+"application/xml";

    //    public static String ASSETS = "urn:factcore:assets:";
    public static String SELF = "http://factcore.com/";

    public static String CORE = SELF+"core/";
    public static String FN = SELF+"vendor/sesame/fn/";

	public static String CAMEL = "http://factcore.com/core/camel/";
}

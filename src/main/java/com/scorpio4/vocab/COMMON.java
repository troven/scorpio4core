package com.scorpio4.vocab;
/*
 *   Scorpio4 - CONFIDENTIAL
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
 * Scorpio4 (c) 2010-2013
 *
 * Define namespaces for essential core vocabularies
 *
 * 1st May 2014 - Licensed to Apscore
 *
 */
public interface COMMON {

    public static final String OWL = "http://www.w3.org/2002/07/owl#";
	public static final String RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	public static final String RDFS = "http://www.w3.org/2000/01/rdf-schema#";
	public static final String RDFS_TYPE = RDF+"type";
    public static final String RDFS_SUBPROPERTY = RDFS+"subPropertyOf";
	public static final String A = RDFS_TYPE;
	public static final String RDFS_SUBCLASS = RDFS+"subClassOf";
	public static final String ASA = RDFS_SUBCLASS ;
    public static final String SAMEAS = OWL+"sameAs";
	public static final String LABEL = RDFS+"label" ;
	public static final String COMMENT = RDFS+"comment" ;
    public static final String DOMAIN = RDFS+"domain" ;
    public static final String RANGE = RDFS+"range" ;

    // Common Vocabularies;

	public static final String XSD = "http://www.w3.org/2001/XMLSchema#";
	public static final String XSD_NULL = "http://www.w3.org/2001/XMLSchema#null";
    public static final String DC = "http://purl.org/dc/elements/1.1/";
    public static final String DCTERMS = "http://purl.org/dc/terms/";
    public static final String SKOS = "http://www.w3.org/2004/02/skos/core#";
    public static final String GEO = "http://www.w3.org/2003/01/geo/wgs84_pos#";
    public static final String ACL = "http://www.w3.org/ns/auth/acl#";

    public static final String MIME_TYPE = "http://www.iana.org/assignments/media-types/";

    public static final String MIME_GROOVY = MIME_TYPE+ "application/x-groovy";
    public static final String MIME_JAVASCRIPT = MIME_TYPE+ "application/x-javascript";
    public static final String MIME_PLAIN = MIME_TYPE+"text/plain";
    public static final String MIME_JSON = MIME_TYPE+ "application/json";
    public static final String MIME_PROPERTIES = MIME_TYPE+ "text/x-java-properties";
    public static final String MIME_SPARQL = MIME_TYPE+"application/x-sparql-query";
    public static final String MIME_SQL = MIME_TYPE+"text/x-sql";
	public static final String MIME_JSON_LD = MIME_TYPE+"application/ld+json";

    public static final String MIME_HTML = MIME_TYPE+ "text/html";
    public static final String MIME_XHTML = MIME_TYPE+ "application/xhtml+xml";
    public static final String MIME_CSV = MIME_TYPE+"text/csv";
    public static final String MIME_XML = MIME_TYPE+"application/xml";

    public static final String SELF = "http://scorpio4.com/";

    public static final String CORE = SELF+"vocab/v1/";
    public static final String FN = SELF+"vendor/sesame/fn/";

	public static final String ACTIVE_FLO = CORE+"flo/";


	public static final String STRING = XSD+"string";
	public static final String INTEGER = XSD+"integer";
	public static final String DOUBLE= XSD+"double";
	public static final String FLOAT = XSD+"float";
	public static final String DATE = XSD+"date";
	public static final String ANY_URI = XSD+"anyURI";
	public static final String DATE_TIME = XSD+"dateTime";
	public static final String BOOLEAN = XSD+"boolean";

	public static final String LIST = RDF+"list";
}

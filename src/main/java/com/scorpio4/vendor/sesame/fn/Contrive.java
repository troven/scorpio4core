package com.scorpio4.vendor.sesame.fn;
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
import org.apache.commons.codec.digest.DigestUtils;
import org.openrdf.query.algebra.evaluation.ValueExprEvaluationException;
import org.openrdf.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A custom SPARQL function that derives (contrives) a permanent URI from a set of 1 or more values
 * The 1st argument is the prefix, subsequent arguments are used to contrive the permanent URI
 *
 * To build the custom function, ensure that
 * ./META-INF/services/org.openrdf.query.algebra.evaluation.function.Function is configured correctly
 * i.e. it contains a line "com.scorpio4.vendor.sesame.fn.Contrive"
 *
 * To use the custom function, copy the JAR file into the classpath for Sesame.
 * Test the function using:
	 PREFIX fn: <http://scorpio4.com/openrdf/function/>

	 SELECT ?this ?that WHERE {
	 ?this a rdfs:Class.
	 BIND(fn:contrive("urn:example:contrived:",?this) AS ?that)
	 }

 *
 *
 */
public class Contrive extends CustomFunction {
	private static final Logger log = LoggerFactory.getLogger(Contrive.class);

	public Contrive() {
	}

	public String getFunctionName() {
		return "contrive";
	}

	/**
	 * Executes the Contrive function.
	 *
	 * @return A URI representing a universally unique identifier
	 *
	 * @throws ValueExprEvaluationException
	 *		 if more insufficient arguments are supplied
	 */

	public Value evaluate(ValueFactory valueFactory, Value... args) throws ValueExprEvaluationException {
		if (args.length < 2)  throw new ValueExprEvaluationException("contrive() requires a mandatory prefix and at least one value");

		// get the prefix (1st argument)
		Value prefixURI = args[0];

		// get the contrived values (multi-part keys)
		StringBuilder contrived = new StringBuilder();
		for(int i=1;i<args.length;i++) contrived.append(args[i].stringValue());

		// append the SHA hash of the contrived values to the prefix
		String contrivedURI = prefixURI.stringValue() + DigestUtils.shaHex(contrived.toString());

		// output contrived URI
		return valueFactory.createURI(contrivedURI);
	}
}

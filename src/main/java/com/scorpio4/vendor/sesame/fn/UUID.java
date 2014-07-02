package com.scorpio4.vendor.sesame.fn;
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
import com.scorpio4.util.IdentityHelper;
import org.openrdf.query.algebra.evaluation.ValueExprEvaluationException;
import org.openrdf.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * a custom SPARQL function that generates a unique (UUID) string
 */
public class UUID extends CustomFunction {
	private static final Logger log = LoggerFactory.getLogger(Contrive.class);

	public UUID() {
	}

	@Override
	public String getFunctionName() {
		return "UUID";
	}

	/**
 * Executes the UUID function.
 *
 * @return A URI representing a universally unique identifier
 *
 * @throws ValueExprEvaluationException
 *		 if more than one argument is supplied or if the supplied argument is not a literal.
 */

	public Value evaluate(ValueFactory valueFactory, Value... args) throws ValueExprEvaluationException {
		if (args.length > 1) {
			throw new ValueExprEvaluationException("fn:UUID() accepts an optional prefix string");
		}

		log.debug("Generate UUID");

		if (args.length==0) {
			return valueFactory.createURI( IdentityHelper.uuid() );
		}

		return valueFactory.createURI( IdentityHelper.uuid(args[0].toString()) );
	}

}

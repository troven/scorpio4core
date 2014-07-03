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
import org.openrdf.query.algebra.evaluation.ValueExprEvaluationException;
import org.openrdf.model.*;
 
/**
 * a custom SPARQL function that calculates the distance between two geo-coordinates
 */
public class GeoDistance extends CustomFunction {

	@Override
	public String getFunctionName() {
		return "GeoDistance";
	}

	/**
 * Executes the GeoDistance(lon1,lat1,lon2,lat2) function.
 *
 * @return A Double representing a distance between two geo-coordinates
 *
 * @throws ValueExprEvaluationException
 *		 if incorrect number of argument is supplied
 */

	public Value evaluate(ValueFactory valueFactory, Value... args) throws ValueExprEvaluationException {
		// our palindrome function expects only a single argument, so throw an error if there's more than one
		if (args.length != 4 && args.length != 5) {
			throw new ValueExprEvaluationException("fn:GeoDistance(lon1,lat1,lon2,lat2,[inMiles]) requires 4 or 5 parameters");
		}
		boolean inMiles = false;
		double lon1 = ((Literal)args[0]).doubleValue();
		double lat1 = ((Literal)args[1]).doubleValue();
		double lon2 = ((Literal)args[2]).doubleValue();
		double lat2 = ((Literal)args[3]).doubleValue();
		if (args.length>4) {
			inMiles = ((Literal)args[4]).booleanValue();
		}

		return valueFactory.createLiteral(haversineDistance(lon1,lat1,lon2,lat2,inMiles));
	}

	// http://www.movable-type.co.uk/scripts/latlong.html
	// haversine distance (does not account for earth's elipsoidal shape. accuracy: +/- 0.3%)

	protected double haversineDistance(double lon1, double lat1, double lon2, double lat2, boolean inMiles) {
		double earthRadius = (inMiles?3958.75:6371.0); // miles or KMs
		double dLat = Math.toRadians(lat2-lat1);
		double dLon = Math.toRadians(lon2-lon1);
		double sindLat = Math.sin(dLat / 2);
		double sindLon = Math.sin(dLon / 2);
		double a = Math.pow(sindLat, 2) + Math.pow(sindLon, 2) * Math.cos(lat1) * Math.cos(lat2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		return (earthRadius * c);
	}
}

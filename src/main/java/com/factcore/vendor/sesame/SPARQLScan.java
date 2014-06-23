package com.factcore.vendor.sesame;
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

import com.factcore.vendor.sesame.util.VariableCollector;
import org.openrdf.query.algebra.Var;
import org.openrdf.query.parser.ParsedQuery;
import org.openrdf.query.parser.sparql.SPARQLParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Fact:Core (c) 2010-2013
 * User: lee
 *
 * Scans SPARQL and determines the variable bindings, and with their possible RDF types
 *
 * !! BROKEN !! Only here for educational purposes
 *
 */
public class SPARQLScan {
	private static final Logger log = LoggerFactory.getLogger(SPARQLScan.class);
	SPARQLParser parser = new SPARQLParser();

	public SPARQLScan() {

	}

	public SPARQLScan(String queryString) throws Exception {
		log.debug(queryString);

		Map<String, String> types = scanTypes(queryString);
		for (Map.Entry<String, String> type: types.entrySet()) {
			log.debug(type.getValue());
		}
	}

	public Map<String,String> scanTypes(String queryString) throws Exception {
		ParsedQuery query = parser.parseQuery(queryString, null);
		VariableCollector collector = new VariableCollector();
		query.getTupleExpr().visit(collector);
		Map typesOf = new HashMap();
		Map<String, Var> vars = collector.getVariables();
		for (Map.Entry<String, Var> var : vars.entrySet()) {
			StringBuilder sparql = new StringBuilder("SELECT DISTINCT ");
			String varName = var.getKey();
//			sparql.append("?").append(varName);
			sparql.append(" ?type WHERE { ?").append(varName).append(" a ?type.");

			List<Object> ranges = collector.getRanges(varName);
			if (ranges!=null) {
				sparql.append("{");
				for(Object range: ranges) {
					sparql.append("<" ).append(range.toString()).append("> rdfs:range ?type.");
				}
				sparql.append("}");
			}

			List<Object> domains = collector.getDomains(varName);
			if (domains!=null) {
				if (ranges!=null) sparql.append(" UNION ");
				sparql.append("{");
				for(Object domain: domains) {
					sparql.append("<" ).append(domain.toString()).append("> rdfs:domain ?type.");
				}
				sparql.append("}");
			}
			sparql.append("}");
			typesOf.put(varName, sparql.toString());
		}
		return typesOf;
	}
}

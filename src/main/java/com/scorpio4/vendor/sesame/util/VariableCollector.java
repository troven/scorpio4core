package com.scorpio4.vendor.sesame.util;
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
import org.openrdf.query.algebra.StatementPattern;
import org.openrdf.query.algebra.Var;
import org.openrdf.query.algebra.helpers.QueryModelVisitorBase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Scorpio4 (c) 2010-2013
 * User: lee
 * Date: 13/01/13
 * Time: 10:21 PM
 * <p/>
 * Visits each RDF Statement in a SPARQL query, and collects bound Variables
 */
public class VariableCollector extends QueryModelVisitorBase<Exception> {
	Map variables = null;
	Map ranges = null;
	Map domains = null;

	public VariableCollector() {
		this.variables = new HashMap();
		this.ranges= new HashMap();
		this.domains = new HashMap();
	}

	public Map getVariables() {
		return this.variables;
	}

	public List<Object> getRanges(String varName) {
		return (List)this.ranges.get(varName);
	}

	public List<Object> getDomains(String varName) {
		return (List)this.domains.get(varName);
	}

	@Override
	public void meet(StatementPattern node) {
		String varName = null;
		varName = collectProjection(node.getSubjectVar());
		if (varName!=null) {
			collectDomain(varName, node.getPredicateVar());
		}
		varName = collectProjection(node.getPredicateVar());
		if (varName!=null) {
			collectRange(varName, node.getSubjectVar());
		}
		varName = collectProjection(node.getObjectVar());
		if (varName!=null) {
			collectRange(varName, node.getPredicateVar());
		}
		try {
			super.meet(node);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private List collectRange(String varName, Var var) {
		List range = (List) this.ranges.get(varName);
		if (range==null) {
			range = new ArrayList();
			this.ranges.put(varName, range);
		}
		if (var.hasValue()) {
			range.add(var.getValue());
		}
		return range;
	}

	private List collectDomain(String varName, Var var) {
		List domain = (List) this.domains.get(varName);
		if (domain==null) {
			domain = new ArrayList();
			this.domains.put(varName, domain);
		}
		if (var.hasValue()) {
			domain.add(var.getValue());
		}
		return domain;
	}

	private String collectProjection(Var var) {
		if (var.getName()!=null && !var.isAnonymous()) {
			this.variables.put(var.getName(), var);
			return var.getName();
		}
		return null;
	}

}

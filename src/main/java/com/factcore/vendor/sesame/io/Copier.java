package com.factcore.vendor.sesame.io;
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
import org.openrdf.query.*;
import org.openrdf.repository.*;
import org.openrdf.rio.*;

public class Copier { 
	RepositoryConnection src = null;

	public Copier(Repository src_repo) {
		from(src);
	}

	public Copier(RepositoryConnection src, RepositoryConnection dst) throws RepositoryException, QueryEvaluationException, RDFHandlerException, MalformedQueryException {
		from(src);
		to("CONSTRUCT {?s ?p ?o} WHERE {?s ?p ?o}", dst);
	}

	public void from(Repository src_repo) throws RepositoryException {
		this.src = src_repo.getConnection();
	}

	public void from(RepositoryConnection src) {
		this.src = src;
	}

	public void to(String query, Repository dst_repo) throws RepositoryException, QueryEvaluationException, RDFHandlerException, MalformedQueryException {
		RepositoryConnection dst = dst_repo.getConnection();
		to(query,dst);
	}

	public void to(String query, RepositoryConnection dst) throws RepositoryException, QueryEvaluationException, RDFHandlerException, MalformedQueryException {
		RDFHandler handler = new org.openrdf.repository.util.RDFInserter(dst);
//		handler.enforceContext(Resource... contexts);
		to(query, handler);
	}

	public void to(String query, RDFHandler dst) throws RepositoryException, QueryEvaluationException, RDFHandlerException, MalformedQueryException {
		//	CONSTRUCT a query
		GraphQuery graphQuery = src.prepareGraphQuery(QueryLanguage.SPARQL, query);
		//  execute query on src and copy results into dst via an RDF Handler
		graphQuery.evaluate(dst);
		//  clean-up
//		tupleQuery.close();
	}


}
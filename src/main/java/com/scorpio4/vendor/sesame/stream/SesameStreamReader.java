package com.scorpio4.vendor.sesame.stream;
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

import com.scorpio4.fact.stream.FactStream;
import com.scorpio4.oops.FactException;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.query.*;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.semarglproject.vocab.XSD;

import java.util.Map;

/**
 * FactCore (c) 2013
 * Module: com.factcore.vendor.sparql
 * User  : lee
 * Date  : 22/11/2013
 * Time  : 5:06 PM
 */
public class SesameStreamReader {
    private RepositoryConnection repositoryConnection = null;
    private FactStream learn = null;

    public SesameStreamReader(Repository repository, FactStream learn) throws RepositoryException {
        this.repositoryConnection=repository.getConnection();
        setFactStream(learn);
    }

    public SesameStreamReader(RepositoryConnection repositoryConnection, FactStream learn) {
        this.repositoryConnection=repositoryConnection;
        setFactStream(learn);
    }

    public RepositoryConnection getConnection() {
        return this.repositoryConnection;
    }

    public FactStream getFactStream() {
        return learn;
    }

    public void setFactStream(FactStream learn) {
        this.learn = learn;
    }

    public void stream(String query, Map bindings) throws FactException {
        if (query==null) throw new FactException("urn:factcore:fact:finder:stream:oops:missing-query");
        try {
            FactStream stream = getFactStream();
            GraphQuery graphQuery = getConnection().prepareGraphQuery(QueryLanguage.SPARQL, query);
            GraphQueryResult result = graphQuery.evaluate();
            while (result.hasNext()) {
                Statement stmt = result.next();
                if (stmt.getObject() instanceof Literal) {
                    Literal value = (Literal)stmt.getObject();
                    String dataType = XSD.STRING;
                    if (value.getDatatype()!=null) dataType = value.getDatatype().toString();
                    stream.fact(stmt.getSubject().toString(), stmt.getPredicate().toString(), value.getLabel(), dataType);
                } else {
                    stream.fact(stmt.getSubject().toString(), stmt.getPredicate().toString(), stmt.getObject().toString());
                }
            }
            result.close();
        } catch (RepositoryException e) {
            throw new FactException("Repository failed: "+e.getMessage(), e);
        } catch (QueryEvaluationException e) {
            throw new FactException("Query failed: "+e.getMessage()+"\n"+query, e);
        } catch (MalformedQueryException e) {
            throw new FactException("Query error: "+e.getMessage()+"\n"+query, e);
        }
    }

}

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

import com.scorpio4.fact.FactSpace;
import com.scorpio4.fact.stream.FactStream;
import com.scorpio4.oops.FactException;
import com.scorpio4.vocab.COMMON;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

/**
 * FactCore (c) 2013
 * Module: com.factcore.vendor.sparql
 * User  : lee
 * Date  : 26/10/2013
 * Time  : 8:54 PM
 */
public class SesameStreamWriter implements FactStream {
    ValueFactory vf = null;
    RepositoryConnection conn = null;
    URI context = null;
    boolean absolute = true;

	public SesameStreamWriter(FactSpace factSpace) throws RepositoryException {
		this(factSpace.getConnection(), factSpace.getIdentity());
	}

    public SesameStreamWriter(RepositoryConnection conn, String context_uri) throws RepositoryException {
        this.conn = conn;
        this.conn.setAutoCommit(true);
        this.vf = this.conn.getValueFactory();
        this.context = vf.createURI(context_uri);
    }

    public void clear() throws FactException {
        try {
            this.conn.clear(this.context);
        } catch (RepositoryException e) {
            throw new FactException("urn:factcore:finder:sparql:oops:clear#"+e.getMessage(),e);
        }
    }

    @Override
    public void fact(String s, String p, Object o) throws FactException {
        if (s==null||p==null||o==null) return;
        try {
            if (absolute) this.conn.remove(vf.createURI(s), vf.createURI(p), null, context);
            this.conn.add(vf.createURI(s), vf.createURI(p), vf.createURI(o.toString()), context );
        } catch (RepositoryException e) {
            throw new FactException("urn:factcore:finder:sparql:oops:add-fact#"+e.getMessage(),e);
        }
    }

    @Override
    public void fact(String s, String p, Object o, String xsdType) throws FactException {
        if (s==null||p==null||o==null||xsdType==null) return;
        try {
            if (xsdType.indexOf(":")<1) xsdType = COMMON.XSD+xsdType;
            if (absolute) this.conn.remove(vf.createURI(s), vf.createURI(p), null, context);
            this.conn.add(vf.createURI(s), vf.createURI(p), vf.createLiteral(o.toString(), vf.createURI(xsdType)), context );
        } catch (RepositoryException e) {
            throw new FactException("urn:factcore:finder:sparql:oops:add-literal#"+e.getMessage(),e);
        }
    }

    public void done() throws FactException {
        try {
            this.conn.commit();
        } catch (RepositoryException e) {
            throw new FactException("urn:factcore:finder:sparql:oops:not-done#"+e.getMessage(),e);
        }
    }

    @Override
    public String getIdentity() {
        return context.toString();
    }
}

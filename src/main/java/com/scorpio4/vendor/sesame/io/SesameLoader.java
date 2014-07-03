package com.scorpio4.vendor.sesame.io;
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
import com.scorpio4.fact.stream.N3Stream;
import com.scorpio4.oops.FactException;
import org.openrdf.model.URI;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.ConnectException;
import java.net.URL;

/**
 * Scorpio4 (c) 2010-2013
 * User: lee
 * Date: 24/01/13
 * Time: 2:42 AM
 * <p/>
 * This code does something useful
 */
public class SesameLoader {
	private static final Logger log = LoggerFactory.getLogger(SesameLoader.class);

	RepositoryConnection connection = null;

	public SesameLoader(RepositoryConnection connection) {
		this.connection = connection;
	}

    public boolean load(N3Stream n3, String baseURI) throws FactException {
        return load(n3.toString(), baseURI);
    }

	public boolean load(String n3, String baseURI) throws FactException {
		try {
			StringReader reader = new StringReader(n3);
			URI base_context = connection.getValueFactory().createURI(baseURI);
//            connection.begin();
			connection.add(reader, baseURI, RDFFormat.N3, base_context);
			return true;
		} catch (ConnectException ce) {
			throw new FactException("Connection failed", ce);
		} catch (RDFParseException rpe) {
			throw new FactException("Can't parse RDF/N3", rpe);
		} catch (RepositoryException e) {
			throw new FactException("Repository failed", e);
		} catch (IOException e) {
            throw new FactException("I/O failed", e);
        }
    }

	public boolean load(File n3) throws RepositoryException, FactException {
		return load(n3, n3.toURI().toString());
	}

	public boolean load(File n3, String baseURI) throws RepositoryException, FactException {
		return load(n3, baseURI, null);
	}

	public boolean load(File n3, final String baseURI, final String userURI) throws RepositoryException, FactException {
		boolean done = false;
		if (n3.isFile() && n3.exists() && n3.getName().endsWith(".n3")) {
			done = loadN3(n3, baseURI, userURI);
		} else if (n3.exists() && n3.isDirectory()) {
			loadDirectory(n3, baseURI, userURI);
		}
		return done;
	}

	public boolean loadDirectory(File n3, String baseURI, String userURI) throws FactException {
		try {
			if (!n3.exists() || !n3.isDirectory()) throw new FactException("Can't load directory: "+n3.getAbsolutePath());
			log.debug("Loading from: " + n3.getAbsolutePath());
			connection.setAutoCommit(false);
			boolean done = false;
			File[] files = n3.listFiles();
			for(File file: files) {
				done = done && load(file, baseURI, userURI);
			}
			connection.setAutoCommit(true);
			return done;
		} catch(RepositoryException re) {
			throw new FactException("Repository Error @ " + n3.getAbsolutePath(), re);
		}
	}

	public boolean loadN3(File n3, String baseURI, String userURI) throws FactException {
		try {
			URI context = connection.getValueFactory().createURI(baseURI);
/*
			URI fileContext = connection.getValueFactory().createURI("file://" + n3.getAbsolutePath());
			if (userURI!=null) {
				URI userContext = connection.getValueFactory().createURI(userURI);
				connection.add(n3, baseURI, RDFFormat.N3, context, fileContext, userContext);
			} else
*/
			connection.add(n3, baseURI, RDFFormat.N3, context);

			return true;
		} catch (RDFParseException rpe) {
			throw new FactException("N3 Typo in " + n3.getAbsolutePath() + " -> " + rpe.getMessage());
		} catch (ConnectException ce) {
			throw new FactException("Connection Error in " + n3.getAbsolutePath() + " -> ", ce);
		} catch (IOException ioe) {
			throw new FactException("IO Error in " + n3.getAbsolutePath() + " -> ", ioe);
		} catch (RepositoryException re) {
			throw new FactException("Repository Error @ " + n3.getAbsolutePath(), re);
		}
	}

	public boolean load(URL n3, String baseURI) throws FactException {
		try {
			URI context = connection.getValueFactory().createURI(baseURI);
			URI fileContext = connection.getValueFactory().createURI(n3.toExternalForm());
			connection.add(n3, baseURI, RDFFormat.N3, context, fileContext);
			return true;
		} catch (RDFParseException rpe) {
			throw new FactException("N3 Typo @ " + n3.toExternalForm() + " -> " , rpe);
		} catch (ConnectException ce) {
			throw new FactException("Connection Error @ " + n3.toExternalForm() + " -> " , ce);
		} catch (IOException ioe) {
			throw new FactException("IO Error @ " + n3.toExternalForm() + " -> " , ioe);
		} catch (RepositoryException re) {
			throw new FactException("Repository Error @ " + n3.toExternalForm(), re);
		}
	}
}

package com.scorpio4.assets;

import com.scorpio4.vocab.COMMON;
import org.openrdf.model.*;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * FactCore (c) 2013
 * Module: com.scorpio4.scripts
 * User  : lee
 * Date  : 1/05/2014
 * Time  : 3:35 PM
 */
public class SesameAssetRegister extends BaseAssetRegister {
	private static final Logger log = LoggerFactory.getLogger(SesameAssetRegister.class);
    public static String HAS_ASSET = COMMON.CORE+"hasAsset";

	RepositoryConnection connection = null;
    String hasAsset = HAS_ASSET;

	boolean inferred = false;

	public SesameAssetRegister(RepositoryConnection connection) {
		this.connection=connection;
	}

    public void setAssetPredicate(String assetPredicate) {
        this.hasAsset = assetPredicate;
    }

	public Asset getAsset(String uri, String type) throws IOException {
        return getAsset(uri, hasAsset, type);
    }

    @Override
    public String getString(String uri, String mimeType) throws IOException {
        Asset asset = getAsset(uri,mimeType);
        return asset==null?null:asset.toString();
    }

    public Asset getAsset(String uri, String hasScript, String type) throws IOException {
		ValueFactory vf = connection.getValueFactory();
		try {
			URI s = vf.createURI(uri), p = vf.createURI(hasScript);
			RepositoryResult<Statement> statements = connection.getStatements(s , p, null, inferred);
			String script = null;
			log.info("getSesameAsset(): "+s+" -> "+p+" -> "+statements.hasNext());
			for(Statement statement: statements.asList()) {
				Value object = statement.getObject();
				if (object instanceof Literal) {
					Literal literal = (Literal)object;
					if (type!=null) {
						URI datatype = literal.getDatatype();
						if (type.equals(datatype.toString())) {
							script = literal.stringValue();
							log.info("Typed Script: "+statement.getSubject());
						} else {
							script = literal.stringValue();
							log.info("Mis-typed ("+datatype.toString()+" & "+type+"): "+statement.getSubject());
						}

					} else if (script == null || script.equals("")) {
						script = literal.stringValue();
                        type = literal.getDatatype().toString();
						log.warn("Default Script: "+statement.getSubject()+" -> "+type);
					} else {
						log.debug("Skipping: "+statement.getSubject());
					}
				} else {
					log.debug("Not Literal: "+statement.getSubject()+" -->"+object);
				}
			}
			log.info("Found "+type+" @ "+uri+" --> "+(script!=null?true:false));
			return script==null?null:new Asset(script,type);
		} catch (RepositoryException e) {
			throw new IOException(e.getMessage(),e);
		}
	}

	public boolean isInferred() {
		return inferred;
	}

	public void setInferred(boolean inferred) {
		this.inferred = inferred;
	}

}

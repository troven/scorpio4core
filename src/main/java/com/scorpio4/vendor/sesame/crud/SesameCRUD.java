package com.scorpio4.vendor.sesame.crud;

import com.scorpio4.assets.Asset;
import com.scorpio4.assets.AssetHelper;
import com.scorpio4.assets.AssetRegister;
import com.scorpio4.assets.JARAssetRegister;
import com.scorpio4.crud.CRUD;
import com.scorpio4.crud.Model;
import com.scorpio4.fact.FactSpace;
import com.scorpio4.oops.ConfigException;
import com.scorpio4.oops.FactException;
import com.scorpio4.vocab.COMMON;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

/**
 * Scorpio4 (c) 2014
 * Module: com.scorpio4.vendor.sesame.crud
 * User  : lee
 * Date  : 17/06/2014
 * Time  : 4:53 PM
 */
public class SesameCRUD implements CRUD {
    private final Logger log = LoggerFactory.getLogger(getClass());

    RepositoryConnection connection;
    String context = null;
    AssetRegister assetRegister;

    public SesameCRUD(FactSpace factSpace) throws FactException {
        this(factSpace, new JARAssetRegister());
    }

    public SesameCRUD(FactSpace factSpace, AssetRegister assetRegister) throws FactException {
        this(factSpace.getConnection(), factSpace.getIdentity(), assetRegister);
    }

    public SesameCRUD(RepositoryConnection connection, String context, AssetRegister assetRegister) {
        this.connection=connection;
        this.assetRegister=assetRegister==null?new JARAssetRegister():assetRegister;
        this.context = context;
    }

    public RepositoryConnection getConnection() {
        return connection;
    }

	public Asset getAsset(String queryURI, Map model) throws FactException, IOException, ConfigException {
		Asset sparqlAsset = assetRegister.getAsset(queryURI, COMMON.MIME_SPARQL);
		Asset asset = AssetHelper.getAsset(sparqlAsset, model);
		return asset;
	}

	public void close() throws RepositoryException {
		this.connection.close();
	}

    @Override
    public Model create(Map model) throws FactException {
//        return new SesameCreate(this,asset).execute();
	    return null;
    }

    @Override
    public Collection<Map> read(String queryURI, Map model) throws FactException, IOException, ConfigException {
        Asset asset = getAsset(queryURI, model);
        log.info("READ: "+asset.getContent());
        return new SesameRead(this,asset.toString()).execute();
    }

    @Override
    public Map update(Map model) throws FactException {
        return null;
    }

    @Override
    public Map delete(Map model) throws FactException {
        return null;
    }

    @Override
    public boolean exists(Map model) throws FactException {
        return false;
    }

    @Override
    public Model identify(Map model) throws FactException {
        return null;
    }

}

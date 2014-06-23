package com.factcore.vendor.sesame.crud;

import com.factcore.assets.Asset;
import com.factcore.assets.AssetHelper;
import com.factcore.assets.AssetRegister;
import com.factcore.assets.JARAssetRegister;
import com.factcore.crud.CRUD;
import com.factcore.crud.Model;
import com.factcore.fact.FactSpace;
import com.factcore.oops.ConfigException;
import com.factcore.oops.FactException;
import com.factcore.vocab.COMMON;
import org.openrdf.repository.RepositoryConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

/**
 * Fact:Core (c) 2014
 * Module: com.factcore.vendor.sesame.crud
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

    @Override
    public Model create(Map model) throws FactException {
        return null;
    }

    @Override
    public Collection<Map> read(String queryURI, Map model) throws FactException, IOException, ConfigException {
        Asset sparqlAsset = assetRegister.getAsset(queryURI, COMMON.MIME_SPARQL);
        Asset asset = AssetHelper.getAsset(sparqlAsset, model);
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

package com.factcore.assets;


import org.openrdf.repository.RepositoryConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * CueBicControls (c) 2014
 * Module: com.cuebic.assets
 * User  : lee
 * Date  : 27/05/2014
 * Time  : 9:28 AM
 */
public class AssetRegisters extends BaseAssetRegister {
	private static final Logger log = LoggerFactory.getLogger(AssetRegisters.class);

	List<AssetRegister> assets = new ArrayList();
    Map bindings = null;

	public AssetRegisters(RepositoryConnection connection) {
        init(connection);
	}

    public AssetRegisters(RepositoryConnection connection, Map bindings) {
        init(connection);
        setBindings(bindings);
    }

    private void init(RepositoryConnection connection) {
        add(new JARAssetRegister()); // jar-packaged assets are preferred
        SesameAssetRegister rdfAssets = new SesameAssetRegister(connection);
        rdfAssets.setInferred(true);
        add(rdfAssets);
    }

    public void setBindings(Map bindings) {
        this.bindings=bindings;
    }

	public void add(AssetRegister asset) {
		assets.add(asset);
	}

	@Override
	public Asset getAsset(String uri, String mimeType) throws IOException {
        Asset found = null;
		for(AssetRegister asset: assets) {
			try {
				found = found!=null?found:asset.getAsset(uri, mimeType);
				log.debug("Checked: "+asset+" -> "+found);
			} catch (IOException e) {
				log.debug(e.getMessage());
				// do nothing
			}
		}
        if (bindings!=null && found!=null) {
            return AssetHelper.getAsset(found,bindings);
        }
		return found;
	}

    @Override
    public String getString(String uri, String mimeType) throws IOException {
        Asset asset = getAsset(uri, mimeType);
        return asset==null?null:asset.toString();
    }
}

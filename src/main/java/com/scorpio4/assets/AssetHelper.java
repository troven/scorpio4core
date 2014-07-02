package com.scorpio4.assets;

import com.scorpio4.oops.ConfigException;
import com.scorpio4.template.PicoTemplate;
import com.scorpio4.vocab.COMMON;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import java.util.Properties;

/**
 * Fact:Core (c) 2014
 * Module: com.scorpio4.assets
 * User  : lee
 * Date  : 18/06/2014
 * Time  : 5:43 PM
 */
public class AssetHelper {

    public static Asset getAsset(Asset asset, Map parameters) throws IOException {
        if (asset==null) return null;
	    if (parameters==null||parameters.isEmpty()) return asset;
        try {
            PicoTemplate picoTemplate = new PicoTemplate(asset.toString());
            String translate = picoTemplate.translate(parameters);
            return new Asset(translate, asset.getMimeType());
        } catch (ConfigException e) {
            throw new IOException(e.getMessage(),e);
        }
    }

	public Map getJSON(AssetRegister assetRegister, String resourcePath) throws IOException {
		Asset asset = assetRegister.getAsset(resourcePath, COMMON.MIME_JSON);
		return getJSON(asset);
	}

	public Map getJSON(Asset asset) throws IOException {
		Gson gson = new Gson();
		return gson.fromJson(new StringReader(asset.toString()),Map.class);
	}

	public Properties getProperties(AssetRegister assetRegister, String resourcePath) throws IOException {
		Asset asset = assetRegister.getAsset(resourcePath, COMMON.MIME_PROPERTIES);
		return getProperties(asset);
	}

	public Properties getProperties(Asset asset) throws IOException {
		Gson gson = new Gson();
		return gson.fromJson(new StringReader(asset.toString()),Properties.class);
	}


}

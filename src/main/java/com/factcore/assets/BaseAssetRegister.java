package com.factcore.assets;

import com.factcore.vocab.COMMON;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import java.util.Properties;

/**
 * Fact:Core (c) 2014
 * Module: com.factcore.assets
 * User  : lee
 * Date  : 17/06/2014
 * Time  : 5:45 PM
 */
public abstract class BaseAssetRegister implements AssetRegister {

    @Override
    public Asset getAsset(String uri, String mimeType) throws IOException {
        return new Asset(getString(uri, mimeType));
    }

    public abstract String getString(String uri, String mimeType) throws IOException;

    public Map getJSON(String resourcePath) throws IOException {
        String string = getString(resourcePath, COMMON.MIME_JSON);
        Gson gson = new Gson();
        return gson.fromJson(new StringReader(string),Map.class);
    }

    public Properties getProperties(String resourcePath) throws IOException {
        String string = getString(resourcePath, COMMON.MIME_PROPERTIES);
        Gson gson = new Gson();
        return gson.fromJson(new StringReader(string),Properties.class);
    }

}

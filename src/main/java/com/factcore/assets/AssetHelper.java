package com.factcore.assets;

import com.factcore.oops.ConfigException;
import com.factcore.template.PicoTemplate;

import java.io.IOException;
import java.util.Map;

/**
 * Fact:Core (c) 2014
 * Module: com.factcore.assets
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

}

package com.scorpio4.template;

import com.scorpio4.assets.Asset;
import com.scorpio4.assets.AssetRegister;
import com.scorpio4.assets.JARAssetRegister;
import com.scorpio4.fact.FactSpace;
import com.scorpio4.oops.ConfigException;
import com.scorpio4.vocab.COMMON;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

/**
 * Scorpio4 (c) 2014
 * Module: com.scorpio4.template
 * User  : lee
 * Date  : 17/06/2014
 * Time  : 8:48 PM
 */
public class PicoAssetTemplates {
    FactSpace factSpace = null;
    AssetRegister assetRegister;
    String mimeType = COMMON.MIME_PLAIN;

    public PicoAssetTemplates(FactSpace factSpace) {
        this.factSpace=factSpace;
        this.assetRegister = new JARAssetRegister();
    }

    public PicoAssetTemplates(FactSpace factSpace, AssetRegister assetRegister) {
        this.factSpace=factSpace;
        this.assetRegister=assetRegister;
    }

    public void generate(String templateURI, Collection<Map> models) throws IOException, ConfigException {
        Asset asset = assetRegister.getAsset(templateURI, mimeType);
        if (asset!=null) {
            PicoTemplate pico = new PicoTemplate(asset.toString());
            for(Map model: models) {
                String translate = pico.translate(model);
            }
        }
    }
}

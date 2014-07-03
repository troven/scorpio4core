package com.scorpio4.assets;

import java.io.IOException;

/**
 * scorpio4 (c) 2013-2014
 * Module: com.scorpio4.assets
 * User  : lee
 * Date  : 1/05/2014
 * Time  : 4:19 PM
 */
public interface AssetRegister {

	public Asset getAsset(String uri, String mimeType) throws IOException;
}

package com.scorpio4.assets;

import java.io.IOException;

/**
 * scorpio4 (c) 2013-2014
 * Module: com.scorpio4.assets
 * @author lee
 * Date  : 1/05/2014
 * Time  : 4:19 PM
 *
 * @author lee
 * @see com.scorpio4.assets.Asset
 *
 * retrieves Assets identified by URI and optionally, mimeType.
 *
 * The MimeType may be NULL in which case the first matching resource should be found.
 * The returned Asset must include a valid MimeType or an IOException must be thrown if missing or ambiguous.
 *
 */
public interface AssetRegister {

	public Asset getAsset(String uri, String mimeType) throws IOException;
}

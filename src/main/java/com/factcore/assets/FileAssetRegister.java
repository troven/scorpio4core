package com.factcore.assets;

import org.apache.commons.io.IOUtils;

import java.io.*;

/**
 * cuebic (c) 2013
 * Module: com.cuebic.util
 * User  : lee
 * Date  : 26/02/2014
 * Time  : 11:24 PM
 */
public class FileAssetRegister extends BaseAssetRegister {
	File home = null;

	public FileAssetRegister(File home) {
		this.home=home;
	}

	// Locate resource in user's home directory, otherwise as a ClassResource
	public InputStream getResource(String resourcePath) throws FileNotFoundException {
		if (home!=null && home.exists()) {
			File assets = new File(home, resourcePath);
			if (assets.exists()) return new FileInputStream(assets);
		}
		return Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath);
	}

	public String getString(String resourcePath) throws IOException {
		InputStream resource = getResource(resourcePath);
		if (resource==null) throw new IOException("Can't locate resource: "+resourcePath);
		StringWriter writer = new StringWriter();
		IOUtils.copy(resource, writer);
		return writer.toString();
	}

    @Override
    public String getString(String uri, String mimeType) throws IOException {
        return getString(uri); // ignore mimeType
    }
}

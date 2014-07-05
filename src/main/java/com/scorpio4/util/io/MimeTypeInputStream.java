package com.scorpio4.util.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * scorpio4-oss (c) 2014
 * Module: com.scorpio4.util.io
 * User  : lee
 * Date  : 5/07/2014
 * Time  : 4:49 PM
 */
public class MimeTypeInputStream extends InputStream {
	String mimeType;
	InputStream in;

	public MimeTypeInputStream(InputStream in, String mimeType) {
		this.mimeType=mimeType;
		this.in=in;
	}

	@Override
	public int read() throws IOException {
		return in.read();
	}
}

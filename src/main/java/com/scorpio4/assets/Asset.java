package com.scorpio4.assets;

import com.scorpio4.util.Identifiable;
import com.scorpio4.vocab.COMMONS;
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;

import javax.activation.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Scorpio4 (c) 2014
 * Module: com.scorpio4.assets
 * @author lee
 * Date  : 17/06/2014
 * Time  : 8:50 PM
 *
 * @author lee
 * @see com.scorpio4.util.Identifiable
 *
 * An Asset identifies and encapsulates mime-typed content.
 * In most internal use cases, the content is simple a String.class
 *
 */
public class Asset implements Identifiable, DataSource {
	String identity = null;
    Object content = null;
    String mimeType = null;

	public Asset(String identity, Object content, String mimeType) {
		this.identity=identity;
		this.content=content;
		this.mimeType=mimeType;
	}

    public Object getContent() {
        return content;
    }

    public String getMimeType() {
        return mimeType;
    }

	@Override
	public InputStream getInputStream() throws IOException {
		byte[] bytes = getContent().toString().getBytes();
		return new ByteInputStream(bytes, bytes.length);
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
//		new ByteOutputStream();
		return null;
	}

	public String getContentType() {
		if (mimeType==null) return "application/octet-stream";
		if (mimeType.startsWith(COMMONS.MIME_TYPE)) {
			return getMimeType().substring(COMMONS.MIME_TYPE.length());
		}
		return mimeType;
	}

	@Override
	public String getName() {
		return getIdentity();
	}

	public String toString() {
        return content.toString();
    }

	public void setMimeType(String mimeType) {
		this.mimeType=mimeType;
	}

	@Override
	public String getIdentity() {
		return identity;
	}
}



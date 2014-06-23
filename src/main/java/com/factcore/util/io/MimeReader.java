package com.factcore.util.io;

import java.io.IOException;
import java.io.Reader;

/**
 * Fact:Core (c) 2013
 * Module: com.factcore.util.io
 * User  : lee
 * Date  : 25/03/2014
 * Time  : 3:17 AM
 */
public class MimeReader extends Reader {
    String mimeType;
    Reader reader = null;

    public MimeReader(Reader reader, String mimeType) {
        this.reader=reader;
        this.mimeType=mimeType;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        return reader.read(cbuf,off,len);
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}
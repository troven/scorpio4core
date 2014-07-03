package com.scorpio4.util.io;
/*
 *   Scorpio4 - CONFIDENTIAL
 *   Unpublished Copyright (c) 2009-2014 Lee Curtis, All Rights Reserved.
 *
 *   NOTICE:  All information contained herein is, and remains the property of Lee Curtis. The intellectual and technical concepts contained
 *   herein are proprietary to Lee Curtis and may be covered by Australian, U.S. and Foreign Patents, patents in process, and are protected by trade secret or copyright law.
 *   Dissemination of this information or reproduction of this material is strictly forbidden unless prior written permission is obtained
 *   from Lee Curtis.  Access to the source code contained herein is hereby forbidden to anyone except current Lee Curtis employees, managers or contractors who have executed
 *   Confidentiality and Non-disclosure agreements explicitly covering such access.
 *
 *   The copyright notice above does not evidence any actual or intended publication or disclosure  of  this source code, which includes
 *   information that is confidential and/or proprietary, and is a trade secret, of Lee Curtis.   ANY REPRODUCTION, MODIFICATION, DISTRIBUTION, PUBLIC  PERFORMANCE,
 *   OR PUBLIC DISPLAY OF OR THROUGH USE  OF THIS  SOURCE CODE  WITHOUT  THE EXPRESS WRITTEN CONSENT OF LEE CURTIS IS STRICTLY PROHIBITED, AND IN VIOLATION OF APPLICABLE
 *   LAWS AND INTERNATIONAL TREATIES.  THE RECEIPT OR POSSESSION OF  THIS SOURCE CODE AND/OR RELATED INFORMATION DOES NOT CONVEY OR IMPLY ANY RIGHTS
 *   TO REPRODUCE, DISCLOSE OR DISTRIBUTE ITS CONTENTS, OR TO MANUFACTURE, USE, OR SELL ANYTHING THAT IT  MAY DESCRIBE, IN WHOLE OR IN PART.
 *
 */
import com.scorpio4.oops.FactException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

/**
 * ProjectArchiver (c) 2013
 * Module: com.projectArchiver.io
 * User  : lee
 * Date  : 14/01/2014
 * Time  : 12:57 AM
 */
public class JarArchiver {
    private static final Logger log = LoggerFactory.getLogger(JarArchiver.class);
	JarOutputStream jarOutputStream = null;
    Manifest manifest = new Manifest();

	public JarArchiver(File file) throws IOException {
		open(file);
	}

	public void open(File file) throws IOException {
		addAttribute(Attributes.Name.MANIFEST_VERSION, "1.0");
        addAttribute(Attributes.Name.IMPLEMENTATION_VENDOR, "Scorpio4");
        addAttribute(Attributes.Name.IMPLEMENTATION_VENDOR_ID, file.toURI().toString());
		jarOutputStream = new JarOutputStream(new FileOutputStream(file), manifest);
	}

    public void addAttribute(Attributes.Name name, String value) {
        manifest.getMainAttributes().put(name,value);
    }

    public void addAttribute(String name, String value) {
        manifest.getMainAttributes().put(new Attributes.Name(name),value);
    }

	public String add(InputStream inputStream, String filename, String comment, long size) throws IOException, NoSuchAlgorithmException, FactException {
        if (jarOutputStream==null) throw new IOException("not open");
		JarEntry entry = new JarEntry(filename);
		entry.setSize(size);

		entry.setMethod(JarEntry.DEFLATED); // compressed
		entry.setTime( System.currentTimeMillis() );
		if (comment!=null) entry.setComment(comment);
		jarOutputStream.putNextEntry(entry);

        // copy I/O & return SHA-1 fingerprint
		String fingerprint = Fingerprint.copy(inputStream, jarOutputStream, 4096);
		IOUtils.copy(inputStream, jarOutputStream);
        log.debug("JAR add "+size+" bytes: "+filename+" -> "+fingerprint);

		jarOutputStream.closeEntry();
//		jarOutputStream.finish();
        return fingerprint;
	}

	public void close() throws IOException {
        if (jarOutputStream==null) throw new IOException("not open");
		jarOutputStream.close();
        jarOutputStream = null;
	}

}

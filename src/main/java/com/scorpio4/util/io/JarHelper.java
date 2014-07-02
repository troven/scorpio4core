package com.scorpio4.util.io;
/*
 *   Fact:Core - CONFIDENTIAL
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.jar.*;
import java.net.*;

public class JarHelper {
    private static final Logger log = LoggerFactory.getLogger(JarHelper.class);

	public static File toLocalFile(URL url, File dir) throws IOException {
		String filename = url.getFile().substring(url.getFile().lastIndexOf(File.separator));
		return new File(dir, filename);
	}

	public static File toLocalDir(URL url, File dir) throws IOException {
		File file = toLocalFile(url, dir);
		return new File(file.getParentFile(), stripExtension(file.getName()) );
	}

	public static String stripExtension(String txt) {
		if (txt==null) return null;
		int ix = txt.lastIndexOf(".");
		if (ix>0) return txt.substring(0,ix);
		return txt;
	}

	public static List install(URL url, File dir) throws IOException {
		File file = toLocalFile(url, dir);
		if (!file.exists()) return extract(url, dir);
		return null;
	}

	public static List extract(URL url, File dir) throws IOException {
		File file = toLocalFile(url, dir);
		if (file.exists()) file.delete();
		else file.getParentFile().mkdirs();
		copy( url.openStream(), new FileOutputStream(file) );
		return extract(file, dir);
	}

	public static List extract(File file, File dir) throws IOException {
		File destDir = new File(file.getParentFile(), stripExtension(file.getName()) );
		destDir.mkdirs();
		return extract(new JarFile(file), destDir);
	}

	public static List extract(JarFile jarFile, File destDir) throws IOException {
		Enumeration<JarEntry> entries = jarFile.entries();
		List copied = new ArrayList();
		while (entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();
			File file = new File(destDir, entry.getName());

			if (entry.getName().startsWith("META-INF")) {
				// do nothing
			} else if (entry.getName().startsWith(".")) {
				// do nothing
			} else if (entry.getName().equals("com")) {
				// do nothing
			} else if (entry.getName().equals("net")) {
				// do nothing
			} else if (entry.getName().equals("org")) {
				// do nothing
			} else if (entry.getName().endsWith(".class")) {
				// do nothing
			} else if (entry.isDirectory()) {
				file.mkdirs();
			} else {
				file.getParentFile().mkdirs();
				copy(jarFile.getInputStream(entry), new FileOutputStream(file));
				copied.add(file);
			}
		}
		return copied;
	}

	public static int copy(InputStream is, OutputStream os) throws IOException {
		byte[] buffer = new byte[4096];
		int bytesRead = 0;
		while ((bytesRead = is.read(buffer)) != -1) {
			os.write(buffer, 0, bytesRead);
		}
		os.flush();
		os.close();
		is.close();
		return bytesRead;
	}

    public static Properties loadProperties(String file) {
        try {
            Properties properties = new Properties();
            InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(file);
            if (in!=null) properties.load(in);
            return properties;
        } catch (IOException e) {
            return null;
        }
    }

}

package com.factcore.util.io;
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

import java.io.File;

public class FileHelper {

	public static String localize(File root, File file) {
		return toRelative(root,file);
	}

	public static String toRelative(File root, File file) {
		if (root==null || file == null || !file.getAbsolutePath().startsWith(root.getAbsolutePath())) return null;
		if (root.equals(file)) return ".";
		return file.getAbsolutePath().substring(root.getAbsolutePath().length()+1);
	}

	public static String toExtension(String txt) {
		if (txt==null) return null;
		int ix = txt.lastIndexOf(".");
		if (ix>0) return txt.substring(ix+1);
		return null;
	}

	public static String stripExtension(String txt) {
		if (txt==null) return null;
		int ix = txt.lastIndexOf(".");
		if (ix>0) return txt.substring(0,ix);
		return txt;
	}

	public static File fromQID(File root, String uuid) {
		if (uuid.startsWith("urn:")) uuid = uuid.substring(4);
		uuid = uuid.replace(":",File.separator);
		return new File(root, uuid);
	}

	public static String toQID(File root, File file) {
		return toQID(localize(root, file));
	}

	public static String toQID(String txt) {
		if (txt==null) return null;
		return stripExtension(txt).replace("\\/","::");
	}
}

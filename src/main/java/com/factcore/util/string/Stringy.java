package com.factcore.util.string;
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

import java.io.IOException;
import java.io.Reader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Fact:Core (c) 2010-2013
 * User: lee
 * Date: 12/02/13
 * Time: 10:57 PM
 * <p/>
 * This code does something useful
 */
public class Stringy {
	private static final Logger log = LoggerFactory.getLogger(Stringy.class);

	public static String toString(Reader reader) throws IOException {
		StringBuffer buffer = new StringBuffer();
		char[] chars = new char[1024];
		int len = 0;
		while( (len = reader.read(chars))>0) {
			buffer.append(chars, 0 , len);
		}
		return buffer.toString();
	}

	public static String toHash(String string, String algo) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance(algo);
			byte[] digest = md.digest(string.getBytes());
			StringBuilder stringy = new StringBuilder();
			for(byte bcode: digest) {
				stringy.append(String.format("%02x", bcode));
			}
			return stringy.toString();
		} catch (NoSuchAlgorithmException e) {
			log.debug("No "+algo+" algorithm found for: "+string);
			return null;
		}
	}
}

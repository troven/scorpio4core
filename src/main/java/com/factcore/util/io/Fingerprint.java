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
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * FactCore (c) 2013
 * Module: com.factcore.file
 * User  : lee
 * Date  : 24/10/2013
 * Time  : 7:18 PM
 */
public class Fingerprint {
    public final static String DEFAULT_ALGO = "SHA-1";

    public static String identify(File file) throws IOException, NoSuchAlgorithmException {
        return identify(new FileInputStream(file), 4096);
    }

    public static String identify(InputStream in) throws IOException, NoSuchAlgorithmException {
        return identify(in, 4096);
    }

    public static String identify(InputStream in, int blocksize) throws IOException, NoSuchAlgorithmException {
        return identify(in, blocksize, DEFAULT_ALGO);
    }

    public static String identify(InputStream in, int blocksize, String algorithm) throws IOException, NoSuchAlgorithmException {
        if (in.markSupported()) in.reset();
        MessageDigest md = MessageDigest.getInstance(algorithm);
        byte[] buffer = new byte[blocksize];

        while( in.read(buffer)>0 ) {
            md.update(buffer);
        }
        in.close();
        return toHex(md.digest());
    }

	public static String identify(byte[] buffer) throws IOException, NoSuchAlgorithmException {
		return identify(buffer, DEFAULT_ALGO);
	}

	public static String identify(byte[] buffer, String algorithm) throws IOException, NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance(algorithm);
		md.update(buffer);
		return toHex(md.digest());
	}

    public static String copy(InputStream in, OutputStream out, int blocksize) throws IOException, NoSuchAlgorithmException {
        return copy(in,out,blocksize, DEFAULT_ALGO);
    }

    public static String copy(InputStream in, OutputStream out, int blocksize, String algorithm) throws IOException, NoSuchAlgorithmException {
        if (in.markSupported()) in.reset();
        MessageDigest md = MessageDigest.getInstance(algorithm);
        byte[] buffer = new byte[blocksize];

        while( in.read(buffer)>0 ) {
            md.update(buffer);
            out.write(buffer);
            out.flush();
        }
        return toHex(md.digest());
    }

    private static String toHex(byte[] data) {
        StringBuffer buffer = new StringBuffer();
        for (int i=0; i<data.length; i++)
        {
            if (i % 4 == 0 && i != 0)
                buffer.append("");
            int x = (int) data[i];
            if (x<0)
                x+=256;
            if (x<16)
                buffer.append("0");
            buffer.append(Integer.toString(x,16));
        }
        return buffer.toString();
    }
}

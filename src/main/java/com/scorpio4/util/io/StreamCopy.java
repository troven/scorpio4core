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

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Scorpio4 (c) 2010-2013
 * User: lee
 * Date: 12/02/13
 * Time: 6:27 PM
 * <p/>
 * This code does something useful
 */
public class StreamCopy {
	private static final Logger log = LoggerFactory.getLogger(StreamCopy.class);

	public StreamCopy(InputStream input, OutputStream output) throws IOException {
		log.debug("Copying binary stream ...");
		copy(input,output);
		input.close();
	}

    public StreamCopy(InputStream input, File output) throws IOException {
        log.debug("Streaming binary to: ${output.absolutePath}");
        FileOutputStream fos = new FileOutputStream(output);
        copy(input,fos);
        fos.close();
        input.close();
    }

	public StreamCopy(File input, OutputStream output) throws IOException {
		log.debug("Streaming binary: : ${input.absolutePath}");
		InputStream inStream = new FileInputStream(input);
		if (input.exists()) copy(inStream,output);
		inStream.close();
	}

	public static void copy( Reader input, Writer output, int buffer_size) throws IOException {
		char[] buffer = new char[buffer_size+16];
		int n = 0;
		while (-1 != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			output.flush();
		}
		output.flush();
	}

	public static void copy(InputStream input, OutputStream output) throws IOException {
		IOUtils.copy(input, output);
		output.flush();
	}

    public void process(InputStream input, OutputStream output) throws IOException {
        IOUtils.copy(input, output);
        output.flush();
    }

	public static String toString(InputStream input) throws IOException {
		return toString(input, "UTF-8");
	}

	public static String toString(InputStream inputStream, String encoding) throws IOException {
		StringWriter writer = new StringWriter();
		IOUtils.copy(inputStream, writer, encoding);
		return writer.toString();
	}
}

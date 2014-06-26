package com.factcore.vendor.camel.component.fingerprint;

import com.factcore.util.io.Fingerprint;
import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.camel.Message;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;

/**
 * Scorpio (c) 2014
 * Module: com.factcore.vendor.camel.component.fingerprint
 * User  : lee
 * Date  : 26/06/2014
 * Time  : 11:20 PM
 */
public class FingerprintHandler {

	public FingerprintHandler() {
	}


	@Handler
	public void handle(Exchange exchange) throws IOException, NoSuchAlgorithmException {
		Message in = exchange.getIn();
		InputStream body = in.getBody(InputStream.class);
		String identify = null;
		identify = Fingerprint.identify(body);
		in.getHeaders().put("scorpio4.fingerprint", identify);

	}
}

package com.scorpio4.vendor.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Scorpio (c) 2014
 * Module: com.scorpio4.vendor.spring
 * User  : lee
 * Date  : 1/07/2014
 * Time  : 12:47 AM
 */
public class HelloWorld {
	static protected final Logger log = LoggerFactory.getLogger(HelloWorld.class);
	GreetingsEarthling greetingsEarthling;

	public HelloWorld() {
		log.debug("Hello Spring");
	}

	public HelloWorld(GreetingsEarthling earthling) {
		this.greetingsEarthling=earthling;
		log.debug(earthling.toString());
	}

	public boolean isWelcomed() {
		return greetingsEarthling!=null;
	}

	public String toString() {
		return isWelcomed()?greetingsEarthling.toString():"Who are?";
	}
}

package com.factcore.fact.stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FactCore (c) 2013
 * Module: com.factcore.fact.stream
 * User  : lee
 * Date  : 25/10/2013
 * Time  : 10:45 PM
 */
public class DebugStream extends N3Stream implements FactStream {
    private static final Logger log = LoggerFactory.getLogger(DebugStream.class);

    public DebugStream() {
    }

    public DebugStream(String id) {
        super(id);
    }

    @Override
    public void fact(String s, String p, Object o) {
        log.trace("<"+s+"> <"+p+"><"+o+">.");
        super.fact(s,p,o);
    }

    @Override
    public void fact(String s, String p, Object o, String type) {
        log.trace("<"+s+"> <"+p+"> \'"+o+"\'^^xsd:"+type+".");
        super.fact(s,p,o,type);
    }
}

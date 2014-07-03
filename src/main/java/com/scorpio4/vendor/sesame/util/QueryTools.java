package com.scorpio4.vendor.sesame.util;

import org.openrdf.query.*;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Scorpio4 (c) 2014
 * Module: com.scorpio4.vendor.sesame.util
 * User  : lee
 * Date  : 17/06/2014
 * Time  : 10:16 PM
 */
public class QueryTools {
    private static final Logger log = LoggerFactory.getLogger(QueryTools.class);

    public static Collection<Map> toCollection(RepositoryConnection connection, String sparql) throws IOException, RepositoryException, MalformedQueryException, QueryEvaluationException {
        TupleQuery tuple = connection.prepareTupleQuery(QueryLanguage.SPARQL, sparql);

        TupleQueryResult result = tuple.evaluate();
        Collection reply = new ArrayList();
        while(result.hasNext()) {
            BindingSet bindingSet = result.next();
            Map map = new HashMap() {
            };
            for(Binding name:bindingSet) {
                map.put(name.getName(), name.getValue().stringValue() );
            }
            log.trace("SPARQL binding: "+map);
            reply.add(map);
        }
        log.trace("SPARQL found: "+reply.size()+" results");
        return reply;
    }
}

package com.factcore.iq

import com.factcore.iq.exec.SPARQLing
import org.openrdf.repository.http.HTTPRepository
/**
 * Fact:Core (c) 2014
 * Module: com.factcore.iq
 * User  : lee
 * Date  : 17/06/2014
 * Time  : 10:22 PM
 *
 *
 */
class SPARQLingTest extends GroovyTestCase {

    void testWith() {
        def repository = new HTTPRepository("http://127.0.0.1:8080/openrdf-sesame/", "FactTools");
        def connection = repository.getConnection();
        def sparqling = new SPARQLing(connection, "SELECT DISTINCT ?o WHERE {?s ?p ?o}");
        def future = sparqling.with(["hello": "world"])
        assert future!=null;
        def result = future.get();
        assert result!=null;
        assert result instanceof Collection<Map>;
        assert !result.isEmpty();
        connection.close();
        repository.shutDown();
    }
}

package com.factcore.tools

import com.factcore.fact.FactSpace
import com.factcore.iq.exec.Executor
import org.openrdf.repository.http.HTTPRepository

/**
 * Fact:Core (c) 2014
 * Module: com.factcore.tools
 * User  : lee
 * Date  : 18/06/2014
 * Time  : 4:47 PM
 *
 *
 */
class ExecutorTest extends GroovyTestCase {

    void testDoRun() {
        HTTPRepository repository = new HTTPRepository("http://127.0.0.1:8080/openrdf-sesame/","FactTools");
        def connection = repository.getConnection();

        def toolChain = new Executor(new FactSpace(connection, "urn:BlankProject:"));
        def executed = toolChain.run("urn:BlankProject:")
        connection.close();
        repository.shutDown();
    }
}

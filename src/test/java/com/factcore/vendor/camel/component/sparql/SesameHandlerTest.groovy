package com.factcore.vendor.camel.component.sparql

import com.factcore.vendor.camel.component.SesameHandler
import org.openrdf.query.resultio.TupleQueryResultFormat
import org.openrdf.repository.http.HTTPRepository
/**
 * Scorpio (c) 2014
 * Module: com.factcore.vendor.camel.component.sparql
 * User  : lee
 * Date  : 25/06/2014
 * Time  : 12:03 PM
 *
 *
 */
public class SesameHandlerTest extends GroovyTestCase {

	void testHandle() {
		HTTPRepository repository = new HTTPRepository("http://127.0.0.1:8080/openrdf-sesame/","FactTools");
		def connection = repository.getConnection();

		def handler = new SesameHandler(connection);
		String sparql = "SELECT DISTINCT ?this WHERE { ?that a ?this }";

		def writer = handler.handle(sparql, TupleQueryResultFormat.JSON);

		println writer;

	}
}

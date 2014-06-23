package com.factcore.vendor.sesame.util;

import com.factcore.vocab.COMMON;
import org.openrdf.model.Namespace;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;

/**
 * ORIGINAL: (c) Lee Curtis, 2009-2014.
 *
 * FactCore (c) 2011-2013
 * Module: com.factcore.util
 * User  : lee
 * Date  : 7/05/2014
 * Time  : 1:16 PM
 */
public class RDFPrefixer {


	public static String addSPARQLPrefix(RepositoryConnection repositoryConnection, String query) throws RepositoryException {
		StringBuffer names$ = new StringBuffer();
		RepositoryResult<Namespace> allns = repositoryConnection.getNamespaces();
		for(Namespace ns: allns.asList()) {
			if (!ns.getPrefix().equals("")) {
				names$.
						append("PREFIX ").
						append(ns.getPrefix()).
						append(": <").
						append(ns.getName()).
						append(">\n");
			}
		}
		names$.append(query);
		return names$.toString();
	}

	public static String addN3Prefix(RepositoryConnection repositoryConnection, String n3s) throws RepositoryException {
		StringBuffer names$ = new StringBuffer();
		RepositoryResult<Namespace> allns = repositoryConnection.getNamespaces();
		for(Namespace ns: allns.asList()) {
			if (!ns.getPrefix().equals("")) {
				names$.
						append("@prefix ").
						append(ns.getPrefix()).
						append(": <").
						append(ns.getName()).
						append(">.\n");
			}
		}
		names$.append(n3s);
		return names$.toString();
	}

	public static String getNamespaces() {
		StringBuilder n3 = new StringBuilder();
		n3.append("@prefix rdf: <"+ RDF.NAMESPACE+">.\n");
		n3.append("@prefix rdfs: <"+ RDFS.NAMESPACE+">.\n");
		n3.append("@prefix owl: <"+ OWL.NAMESPACE+">.\n");
		n3.append("@prefix skos: <"+ COMMON.SKOS+">.\n");
		n3.append("@prefix dc: <"+ COMMON.DC+">.\n");
		n3.append("@prefix xsd: <"+ COMMON.XSD+">.\n");
		n3.append("@prefix acl: <"+ COMMON.ACL+">.\n");

		return n3.toString();
	}


}

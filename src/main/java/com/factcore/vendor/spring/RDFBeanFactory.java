package com.factcore.vendor.spring;

import com.factcore.fact.FactSpace;
import org.openrdf.repository.RepositoryConnection;
import org.springframework.context.support.GenericApplicationContext;

/**
 * Fact:Core (c) 2014
 * Module: com.factcore.vendor.spring
 * User  : lee
 * Date  : 18/06/2014
 * Time  : 9:27 AM
 */
public class RDFBeanFactory extends GenericApplicationContext {
    FactSpace factSpace = null;

    public RDFBeanFactory(RepositoryConnection repositoryConnection, String context) {
        this.factSpace = new FactSpace(repositoryConnection, context);
    }

    public RDFBeanFactory(FactSpace factSpace) {
        this.factSpace = factSpace;
    }

}

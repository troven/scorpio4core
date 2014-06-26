package com.factcore.vendor.spring;

import com.factcore.fact.FactSpace;
import org.openrdf.repository.RepositoryConnection;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
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

	@Override
	public Object getBean(String name) throws BeansException {
		return null;
	}

	@Override
	public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
		return null;
	}

	@Override
	public <T> T getBean(Class<T> requiredType) throws BeansException {
		return null;
	}

	@Override
	public Object getBean(String name, Object... args) throws BeansException {
		return null;
	}

	@Override
	public boolean containsBean(String name) {
		return false;
	}

	@Override
	public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
		return name.startsWith("bean:");
	}

	@Override
	public boolean isPrototype(String name) throws NoSuchBeanDefinitionException {
		return false;
	}

	@Override
	public boolean isTypeMatch(String name, Class<?> targetType) throws NoSuchBeanDefinitionException {
		return false;
	}

	@Override
	public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
		return null;
	}

	@Override
	public String[] getAliases(String name) {
		return new String[0];
	}
}

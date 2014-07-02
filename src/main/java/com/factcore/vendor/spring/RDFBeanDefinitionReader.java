package com.factcore.vendor.spring;

import com.factcore.iq.bean.BeanConverter;
import com.factcore.util.Identifiable;
import com.factcore.vendor.sesame.util.RDFList;
import com.factcore.vendor.sesame.util.RDFScalars;
import com.factcore.vocab.COMMON;
import org.openrdf.model.*;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.semarglproject.vocab.XSD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinitionReader;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.Collection;

/**
 * Scorpio (c) 2014
 * Module: com.factcore.vendor.spring
 * User  : lee
 * Date  : 18/06/2014
 * Time  : 9:27 AM
 */
public class RDFBeanDefinitionReader extends AbstractBeanDefinitionReader implements Identifiable {
	static protected final Logger log = LoggerFactory.getLogger(RDFBeanDefinitionReader.class);

	String NS = COMMON.CORE+"bean/";
	String BEAN = NS +"Bean";
	String ontology = NS;
	RepositoryConnection connection;
	ValueFactory vf = null;
	BeanConverter converter = new BeanConverter();

	public RDFBeanDefinitionReader(RepositoryConnection connection, BeanDefinitionRegistry registry) {
		super(registry);
		Assert.notNull(connection, "RepositoryConnection can't be NULL");
		this.connection=connection;
		vf = connection.getValueFactory();
		setBeanClassLoader(Thread.currentThread().getContextClassLoader());
    }

	public void setIdentity(String ontology) {
		this.ontology=ontology;
	}


	public void alias(String name, String alias) {
		getRegistry().registerAlias(name, alias);
	}


	@Override
	public int loadBeanDefinitions(String resource) throws BeanDefinitionStoreException {
		try {
			return read(connection, resource);
		} catch (RepositoryException e) {
			throw new BeanDefinitionStoreException("Repository Error: "+e.getMessage(),e);
		} catch (ClassNotFoundException e) {
			throw new BeanDefinitionStoreException("Class Not Found: "+e.getMessage(),e);
		}
	}

	@Override
	public int loadBeanDefinitions(Resource resource) throws BeanDefinitionStoreException {
		try {
			return read(connection, resource);
		} catch (RepositoryException e) {
			throw new BeanDefinitionStoreException("Repository Error: "+e.getMessage(),e);
		} catch (IOException e) {
			throw new BeanDefinitionStoreException("IO Error: "+e.getMessage(),e);
		} catch (ClassNotFoundException e) {
			throw new BeanDefinitionStoreException("Class Not Found: "+e.getMessage(),e);
		}
	}

	public int read(RepositoryConnection connection, Resource resource) throws RepositoryException, IOException, ClassNotFoundException {
		Assert.notNull(connection, "RepositoryConnection must not be null");
		return read(connection, resource.getURI().toString());
	}

	private int read(RepositoryConnection connection, String resource) throws RepositoryException, ClassNotFoundException {
		int count = 0;
		URI resourceURI = resource==null?null:vf.createURI(resource);
		log.debug("RDF Bean: "+resource);
		RepositoryResult<Statement> beans = connection.getStatements( resourceURI, RDF.TYPE, vf.createURI(BEAN), true);
		while(beans.hasNext()) {
			Statement bean = beans.next();
			String beanClass = bean.getSubject().stringValue();
			AbstractBeanDefinition beanDefinition = defineBean(bean);
			getRegistry().registerBeanDefinition(beanClass, beanDefinition);
			log.debug("Register: "+beanClass+" -> "+beanDefinition);
			if (beanDefinition!=null) count++;
		}
		return count;

	}

	private AbstractBeanDefinition defineBean(Statement bean) throws RepositoryException, ClassNotFoundException {
		String beanClass = bean.getSubject().stringValue();
		AbstractBeanDefinition defineBean = defineBean(beanClass);

		/*
		 * Scalar Definitions
		 */

		org.openrdf.model.Resource beanURI = bean.getSubject();
		RDFScalars rdfScalars= new RDFScalars(connection);

		Literal lazyInit = rdfScalars.getLiteral(beanURI, createURI( "lazyInit"), XSD.BOOLEAN);
		if (lazyInit!=null) defineBean.setLazyInit(lazyInit.booleanValue());

		Literal initMethod = rdfScalars.getLiteral(beanURI, createURI( "initMethod"), XSD.STRING);
		if (initMethod!=null) defineBean.setInitMethodName(initMethod.stringValue());

		Literal destroyMethod = rdfScalars.getLiteral(beanURI, createURI( "destroyMethod"), XSD.STRING);
		if (destroyMethod!=null) defineBean.setDestroyMethodName(destroyMethod.stringValue());

		Literal lenient = rdfScalars.getLiteral(beanURI, createURI( "lenient"), XSD.BOOLEAN);
		if (lenient!=null) defineBean.setLenientConstructorResolution(lenient.booleanValue());

		Literal enforceInit = rdfScalars.getLiteral(beanURI, createURI( "enforceInit"), XSD.BOOLEAN);
		if (enforceInit!=null) defineBean.setEnforceInitMethod(enforceInit.booleanValue());

		Literal enforceDestroy = rdfScalars.getLiteral(beanURI, createURI( "enforceDestroy"), XSD.BOOLEAN);
		if (enforceDestroy!=null) defineBean.setEnforceDestroyMethod(enforceDestroy.booleanValue());

		Literal primary = rdfScalars.getLiteral(beanURI, createURI( "primary"), XSD.BOOLEAN);
		if (primary!=null) defineBean.setPrimary(primary.booleanValue());

		Literal autoWire = rdfScalars.getLiteral(beanURI, createURI( "autoWire"), XSD.BOOLEAN);
		if (autoWire!=null) defineBean.setAutowireCandidate(autoWire.booleanValue());

		Literal description = rdfScalars.getLiteral(beanURI, createURI( "description"), XSD.STRING);
		if (description!=null) defineBean.setDescription(description.stringValue());

		defineBean.setDependencyCheck(AbstractBeanDefinition.DEPENDENCY_CHECK_ALL);

		/*
		 * Vector Definitions
		 */

		RDFList rdfList = new RDFList(connection);
		// constructor arguments
		int i=0;
		URI newPredicate = createURI( "new");
		ConstructorArgumentValues argValues = new ConstructorArgumentValues();
		Collection<Value> initArgs = rdfList.getList(beanURI, newPredicate);
		for(Value initValue: initArgs) {
			i = addToArguments(argValues, i, initValue);
		}
		if (i==0) {
			// handle single scalar reference
			Value arg = rdfScalars.getValue(beanURI, newPredicate);
			if (arg!=null) addToArguments(argValues, 0, arg);
		}
		defineBean.setConstructorArgumentValues(argValues);

		// dependsOn
		Collection<Value> dependsList = rdfList.getList(beanURI, createURI( "dependsOn"));
		String[] dependsOn = new String[dependsList.size()];
		i=0;
		for(Value depends: dependsList) {
			dependsOn[i++]=depends.stringValue();
		}
		if (i>0) {
			log.debug("\tDepends On: "+dependsOn);
			defineBean.setDependsOn(dependsOn);
		}

		// properties
		MutablePropertyValues propertyValues = defineBean.getPropertyValues();
		RepositoryResult<Statement> statements = connection.getStatements(beanURI, null, null, false);
		while(statements.hasNext()) {
			Statement next = statements.next();
			URI pURI = next.getPredicate();
			if (pURI.toString().startsWith(getIdentity())) {
				addToProperties(propertyValues, pURI.getLocalName(), next.getObject());
			}
		}
		log.debug("Defined: "+beanClass+" @ "+defineBean);
		return defineBean;
	}

	private URI createURI(String localPart) {
		return vf.createURI(getBaseURI() + localPart);
	}

	private int addToArguments(ConstructorArgumentValues argValues, int ix, Value value) {
		if (value instanceof URI) {
			String uri = value.stringValue();
			log.debug("\tRef: "+uri);
			argValues.addIndexedArgumentValue(ix++, new RuntimeBeanReference(uri));
		} else if (value instanceof Literal) {
			Literal literal = (Literal)value;
			log.debug("\tNew: "+literal);
			Object o = converter.convertToType(literal.stringValue(), literal.getDatatype().toString());
			argValues.addIndexedArgumentValue(ix++, o, o.getClass().getCanonicalName());
		} else {
			log.debug("\tinit? "+value);
		}
		return ix;
	}

	private void addToProperties(MutablePropertyValues propertyValues, String local, Value value) {
		if (value instanceof URI) {
			String uri = value.stringValue();
			log.debug("\tRef: "+uri);
			propertyValues.add(local, new RuntimeBeanReference(uri));
		} else if (value instanceof Literal) {
			Literal literal = (Literal)value;
			log.debug("\tNew: "+literal);
			Object o = converter.convertToType(literal.stringValue(), literal.getDatatype().toString());
			propertyValues.add(local, o);
		} else {
			log.debug("\tinit? "+value);
		}
	}

	protected AbstractBeanDefinition defineBean(String beanClass) throws ClassNotFoundException {
		if (beanClass.startsWith("bean:")) beanClass = beanClass.substring(5);
		GenericBeanDefinition beanDef = new GenericBeanDefinition();
		beanDef.setBeanClassName(beanClass);
		beanDef.setPropertyValues(new MutablePropertyValues());
		beanDef.setScope(beanDef.SCOPE_DEFAULT);
		beanDef.setLazyInit(true);
		beanDef.setSynthetic(false);
		beanDef.setAbstract(false);
		beanDef.setAutowireCandidate(true);
		log.debug("Resolving: "+beanClass);
		beanDef.resolveBeanClass(getBeanClassLoader());
		return beanDef;
	}


	public String getBaseURI() {
		return NS;
	}

	@Override
	public String getIdentity() {
		return ontology;
	}
}

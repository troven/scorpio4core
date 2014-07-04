package com.scorpio4.vendor.spring;

import com.scorpio4.iq.bean.BeanConverter;
import com.scorpio4.util.Identifiable;
import com.scorpio4.vendor.sesame.util.RDFList;
import com.scorpio4.vendor.sesame.util.RDFScalars;
import com.scorpio4.vocab.COMMON;
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
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.*;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Scorpio (c) 2014
 * Module: com.scorpio4.vendor.spring
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
	protected Map reserved = new HashMap();
	RDFList rdfList;
	RDFScalars rdfScalars;


	public RDFBeanDefinitionReader(RepositoryConnection connection) {
		this(connection, new GenericApplicationContext());
	}

	public RDFBeanDefinitionReader(RepositoryConnection connection, BeanDefinitionRegistry registry) {
		super(registry);
		Assert.notNull(connection, "RepositoryConnection can't be NULL");
		this.connection=connection;
		vf = connection.getValueFactory();
		rdfList = new RDFList(connection);
		rdfScalars = new RDFScalars(connection);
		setBeanClassLoader(Thread.currentThread().getContextClassLoader());
		defaultReservedWords();
    }

	public void setIdentity(String ontology) {
		this.ontology=ontology;
	}

	public void defaultReservedWords() {
		reserved.put("new", Collection.class);
		reserved.put("dependsOn", Collection.class);
		reserved.put("constructor", Collection.class);
	}


	public void alias(String name, String alias) {
		getRegistry().registerAlias(name, alias);
	}


	@Override
	public int loadBeanDefinitions(String resource) throws BeanDefinitionStoreException {
		try {
			return read(resource);
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
		Assert.notNull(connection, "RepositoryConnection must not be NULL");
		return read(resource.getURI().toString());
	}

	private int read(String resource) throws RepositoryException, ClassNotFoundException {
		int count = 0;
		URI resourceURI = resource==null?null:vf.createURI(resource);

		if (resource.startsWith("bean:")) {
			count+= readPrototype(resourceURI);
		} else {
			count+= readSingleton(resourceURI);
		}
		return count;

	}

	private int readSingleton(URI resourceURI) throws RepositoryException, ClassNotFoundException {
		int count = 0;
		log.debug("Bean Singleton: "+resourceURI);
		RepositoryResult<Statement> beans = connection.getStatements( resourceURI, RDF.TYPE, null, true);
		while(beans.hasNext()) {
			Statement bean = beans.next();
			boolean isBean = bean.getObject().stringValue().startsWith("bean:");
			boolean knownBean = getRegistry().containsBeanDefinition(bean.getSubject().stringValue());
			if (!knownBean && isBean && bean.getObject() instanceof org.openrdf.model.Resource) {
				org.openrdf.model.Resource classURI = (org.openrdf.model.Resource) bean.getObject();
				count+= readBean(bean.getSubject(), classURI, BeanDefinition.SCOPE_SINGLETON);
			}
		}
		return count;
	}

	private int readPrototype(org.openrdf.model.Resource resourceURI) throws RepositoryException, ClassNotFoundException {
		return readBean(resourceURI, resourceURI, BeanDefinition.SCOPE_PROTOTYPE);
	}

	private int readBean(org.openrdf.model.Resource resource, org.openrdf.model.Resource classURI, String scope) throws RepositoryException, ClassNotFoundException {
		int count = 0;
		log.debug("readBean("+scope+") -> "+ resource+" @ "+classURI);
		RepositoryResult<Statement> beans = connection.getStatements(classURI, RDF.TYPE, vf.createURI(BEAN), true);
		while(beans.hasNext()) {
			Statement bean = beans.next();
			String beanClass = bean.getSubject().stringValue();
			if (!getRegistry().containsBeanDefinition(resource.stringValue())) {
				AbstractBeanDefinition beanDefinition = defineBean(resource, bean);
				beanDefinition.setScope(scope);
				if (beanDefinition!=null) {
					getRegistry().registerBeanDefinition(resource.stringValue(), beanDefinition);
					log.debug(count+") Registered: "+resource+" @ "+beanClass+" -> "+beanDefinition);
					count++;
				}
			} else {
//s				log.trace("Re-Register: " + beanClass);
			}
		}
		return count;
	}

	private AbstractBeanDefinition defineBean(org.openrdf.model.Resource beanURI, Statement bean) throws RepositoryException, ClassNotFoundException {
		String beanClass = bean.getSubject().stringValue();
		AbstractBeanDefinition defineBean = defineBean(beanClass);

		/*
		 * Scalar Definitions
		 */

		Literal lazyInit = rdfScalars.getLiteral(beanURI, createURI( "lazyInit"), XSD.BOOLEAN);
		if (lazyInit!=null) defineBean.setLazyInit(lazyInit.booleanValue());

		Literal initMethod = rdfScalars.getLiteral(beanURI, createURI( "initMethod"), XSD.STRING);
		if (initMethod!=null) defineBean.setInitMethodName(initMethod.stringValue());

		Literal destroyMethod = rdfScalars.getLiteral(beanURI, createURI( "destroyMethod"), XSD.STRING);
		if (destroyMethod!=null) defineBean.setDestroyMethodName(destroyMethod.stringValue());

		Literal lenient = rdfScalars.getLiteral(beanURI, createURI( "lenient"), XSD.BOOLEAN);
		if (lenient!=null) defineBean.setLenientConstructorResolution(lenient.booleanValue());

		Literal singleton = rdfScalars.getLiteral(beanURI, createURI( "singleton"), XSD.BOOLEAN);
		boolean isSingleton = connection.hasStatement(bean.getSubject(), RDF.TYPE, bean.getSubject(), false);
		if ( (singleton!=null && singleton.booleanValue()) || isSingleton) {
			defineBean.setScope(BeanDefinition.SCOPE_SINGLETON);
		}

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

		// constructor arguments
		ConstructorArgumentValues argValues = new ConstructorArgumentValues();
		int i = createConstructor(0, beanURI, argValues, createURI( "new"));
		if (i==0) i = createConstructor(0, beanURI, argValues, createURI( "constructor"));
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

		MutablePropertyValues propertyValues = defineBeanProperties(beanURI, defineBean);
		log.debug("Defined: "+beanClass+" @ "+defineBean);
		log.debug("\tproperties:"+Arrays.toString(propertyValues.getPropertyValues()));
		return defineBean;
	}

	private int createConstructor(int i, org.openrdf.model.Resource beanURI, ConstructorArgumentValues argValues, URI newPredicate) throws RepositoryException {
		Collection<Value> initArgs = this.rdfList.getList(beanURI, newPredicate);
		for(Value initValue: initArgs) {
			i = addToArguments(argValues, i, initValue);
		}
		if (i==0) {
			// handle single scalar reference
			Value arg = rdfScalars.getValue(beanURI, newPredicate);
			if (arg!=null) addToArguments(argValues, 0, arg);
		}
		return i;
	}

	protected MutablePropertyValues defineBeanProperties(org.openrdf.model.Resource beanURI, AbstractBeanDefinition defineBean) throws RepositoryException {
		// properties
		MutablePropertyValues propertyValues = defineBean.getPropertyValues();
		RepositoryResult<Statement> statements = connection.getStatements(beanURI, null, null, false);
		while(statements.hasNext()) {
			Statement next = statements.next();
			URI pURI = next.getPredicate();
			if (pURI.toString().startsWith(getIdentity())) {
				String localName = pURI.getLocalName();
				boolean isReserved = reserved.containsKey(localName);
				log.debug("\tProperty: "+localName+" = "+next.getObject()+(isReserved?" RESERVED":""));
				if (!isReserved) {
					addToProperties(propertyValues, localName, next.getObject());
				}
			}
		}
		return propertyValues;
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
			URI datatype = literal.getDatatype();
			if (datatype==null) datatype = vf.createURI(XSD.STRING);
			log.debug("\tNew: "+literal+" @ "+datatype);

			Object o = converter.convertToType(literal.stringValue(), datatype.toString());
			argValues.addIndexedArgumentValue(ix++, o, o.getClass().getCanonicalName());
		} else {
			log.debug("\tinit? "+value);
		}
		return ix;
	}

	private void addToProperties(MutablePropertyValues propertyValues, String local, Value value) {
		if (value instanceof URI) {
			String uri = value.stringValue();
			log.debug("\tNew Ref: "+uri);
			propertyValues.add(local, new RuntimeBeanReference(uri));
		} else if (value instanceof Literal) {
			Literal literal = (Literal)value;
			Object o = converter.convertToType(literal.stringValue(), literal.getDatatype().toString());
			log.debug("\tNew: "+literal+" -> "+o);
			propertyValues.add(local, o);
		} else {
			log.debug("\tinit? "+value);
		}
	}

	protected AbstractBeanDefinition defineBean(String beanClass) throws ClassNotFoundException {
		if (beanClass.startsWith("bean:")) beanClass = beanClass.substring(5);
		GenericBeanDefinition beanDef = new GenericBeanDefinition();
		beanDef.setBeanClassName(beanClass);
		beanDef.setNonPublicAccessAllowed(true);
		beanDef.setPropertyValues(new MutablePropertyValues());
		beanDef.setScope(beanDef.SCOPE_PROTOTYPE);
		beanDef.setLazyInit(true);
		beanDef.setSynthetic(false);
		beanDef.setAutowireMode(beanDef.AUTOWIRE_CONSTRUCTOR);
		beanDef.setAbstract(false);
		beanDef.setLenientConstructorResolution(true);
		beanDef.setAutowireCandidate(true);
		beanDef.setDependencyCheck(RootBeanDefinition.DEPENDENCY_CHECK_NONE);
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

	public Object getBean(String name) {
		BeanDefinitionRegistry registry = getRegistry();
		if (registry instanceof AbstractApplicationContext) {
			log.debug("getBean: "+name+" from "+((AbstractApplicationContext) registry).getClassLoader());
			AbstractApplicationContext applicationContext = (AbstractApplicationContext)registry;
			log.debug("Beans: " + Arrays.toString(applicationContext.getBeanDefinitionNames()));
			Object bean = applicationContext.getBean(name);
			return bean;
		}
		return null;
	}
}

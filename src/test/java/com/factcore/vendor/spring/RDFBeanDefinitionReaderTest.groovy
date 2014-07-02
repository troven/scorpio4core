package com.factcore.vendor.spring

import org.junit.Before
import org.openrdf.repository.RepositoryConnection
import org.openrdf.repository.sail.SailRepository
import org.openrdf.rio.RDFFormat
import org.openrdf.sail.inferencer.fc.ForwardChainingRDFSInferencer
import org.openrdf.sail.memory.MemoryStore
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.ApplicationContext
import org.springframework.context.support.GenericApplicationContext

/**
 * Scorpio (c) 2014
 * Module: com.factcore.vendor.spring
 * User  : lee
 * Date  : 30/06/2014
 * Time  : 11:33 PM
 *
 *
 */
class RDFBeanDefinitionReaderTest extends GroovyTestCase {
	RepositoryConnection connection;

	@Before
	void init() {
		if (connection!=null) return;
		def store = new SailRepository(new ForwardChainingRDFSInferencer(new MemoryStore()));
		store.initialize();
		connection = store.getConnection();
		connection.begin()
		def stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("com/factcore/vendor/spring/HelloWorld.n3");
		assert stream!=null;
		connection.add(stream, "urn:test", RDFFormat.N3);
		connection.commit()
	}

	void testRegister() {
		init()
		ApplicationContext applicationContext = new GenericApplicationContext();

		RDFBeanDefinitionReader beanie = new RDFBeanDefinitionReader(connection, applicationContext);
		BeanDefinition beanDef = beanie.register("bean:com.factcore.vendor.spring.HelloWorld");
		println "Registered: "+beanDef;
	}

	void testDefineBean() {
		init()
		ApplicationContext applicationContext = new GenericApplicationContext();

		RDFBeanDefinitionReader beanie = new RDFBeanDefinitionReader(connection, applicationContext);
		BeanDefinition beanDef = beanie.defineBean("com.factcore.vendor.spring.HelloWorld");
		println "Defined: "+beanDef;
	}

	void testLoadDefinitions() {
		init()
		ApplicationContext applicationContext = new GenericApplicationContext();

		RDFBeanDefinitionReader beanie = new RDFBeanDefinitionReader(connection, applicationContext);
		def loaded = beanie.loadBeanDefinitions("bean:com.factcore.vendor.spring.HelloWorld");
		loaded+= beanie.loadBeanDefinitions("bean:com.factcore.vendor.spring.GreetingsEarthling");
		println "Loaded: "+loaded;
		def bean = applicationContext.getBean("bean:com.factcore.vendor.spring.HelloWorld");
		assert bean!=null;
		assert com.factcore.vendor.spring.HelloWorld.isInstance(bean);
		assert bean.isWelcomed();
		println "Hello Bean: "+bean;
	}
}

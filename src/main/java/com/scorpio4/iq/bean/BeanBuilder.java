package com.scorpio4.iq.bean;

import com.scorpio4.fact.stream.FactStream;
import com.scorpio4.oops.FactException;
import com.scorpio4.oops.IQException;
import com.scorpio4.vocab.COMMON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * scorpio4 (c) 2013
 *
 * All software contained herein is, and remains
 * the property of Lee Curtis and licensed suppliers.
 * The intellectual and technical concepts contained
 * herein are proprietary to Lee Curtis.
 * Dissemination of this software is strictly forbidden
 * unless prior written permission is obtained
 * from Lee Curtis.
 *
 * Module: com.scorpio4.iq
 * User  : lee
 * Date  : 3/12/2013
 * Time  : 11:42 PM
 */
public class BeanBuilder implements FactStream {
	public static final Logger log = LoggerFactory.getLogger(BeanBuilder.class);

	private ClassLoader classLoader = null;
	protected HashMap<String, Object> idComponentMap = new HashMap<String, Object>();
	protected HashMap<Object, String> componentIdMap = new HashMap<Object, String>();
	protected HashMap<String, Tuple> containerMap = new HashMap<String, Tuple>();
	protected HashMap<String, Map<String, String>> attributesMap = new HashMap();
	protected HashMap<String, String> classMap = new HashMap<String, String>();

	public String baseURI = COMMON.CORE+"bean/";
	public String INSTANCE_OF = baseURI+"asa";

	protected Object delegate = null;
	protected ConvertsType convertsType = new BeanConverter();
	protected boolean buildExamples = true;

	public BeanBuilder() {
		setClassLoader(BeanBuilder.class.getClassLoader());
		setDelegate(this);
	}

	public BeanBuilder(Object delegate) {
		setClassLoader(BeanBuilder.class.getClassLoader());
		setDelegate(delegate);
	}

	// basic getters
	public Object getBean(String id) {
		return idComponentMap.get(id);
	}

	public String getBeanId(Object component) {
		return componentIdMap.get(component);
	}

	// hack: build examples

	public boolean isBuildExamples() {
		return buildExamples;
	}

	public void setBuildExamples(boolean useExamples) {
		this.buildExamples = useExamples;
	}


	public ClassLoader getClassLoader() {
		return classLoader;
	}

	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	// non-destructive add-all
	public void addClassResolvers(Map<String, Object> classes) {
		if (classes==null||classes.isEmpty()) return;
		for(Map.Entry<String, Object> nsClass: classes.entrySet()) {
			addClassResolver(nsClass.getKey(), nsClass.getValue().toString());
		}
	}

	public void addClassResolver(String ns_class, String className) {
		classMap.put(ns_class,className);
	}

	public void addClass(Class clazz) {
		classMap.put(clazz.getSimpleName(),clazz.getCanonicalName());
		classMap.put(getIdentity()+clazz.getSimpleName(),clazz.getCanonicalName());
	}

	protected Object getBean(String instanceID, String classURI) throws IQException {

		Object bean = idComponentMap.get(instanceID);
		if (bean!=null) {
			log.trace("Old Bean: " + instanceID + " -> " + bean.getClass().getCanonicalName() );
			return bean;
		}

		bean = newBean(classURI);
		idComponentMap.put(instanceID, bean);
		componentIdMap.put(bean, instanceID);
		log.debug("get (new) Bean: " + instanceID + " => " + bean.getClass().getCanonicalName());

		return bean;
	}

	public void build() throws IQException {

		for(String $child: attributesMap.keySet()) {
			Object child = idComponentMap.get($child);
			if (child==null) {
				Tuple tuple = containerMap.get($child);
				log.error("Missing: "+$child+" -> "+tuple);
				return;
			}
			Map attributes = attributesMap.get($child);
			attributes = attributes==null?new HashMap():attributes;
			setBeanAttributes(child, attributes);
			delegate(null, child, attributes);

//            if (isBuildExamples() && child instanceof Examples) {
//                Examples example = (Examples)child;
//                example.examples();
//            }
		}

		log.debug("Parent/Child map: "+containerMap);
		for(String $parent: containerMap.keySet()) {
			Tuple tuple = containerMap.get($parent);
			Object parent = getBean($parent);
			Object child = getBean(tuple.o.toString());
			if (parent!=null && child!=null) {
				log.debug("Bean Parent/Child: "+$parent+" -> "+tuple.o);
				Map attributes = attributesMap.get($parent);
				delegate(parent, tuple.p.toString(), child);
				delegate(parent, child, attributes);
			} else {
				log.trace("Missing Parent/Child: "+$parent+" -> "+tuple.o);
			}
		}
	}

	protected void setBeanAttributes(Object object, Map<String, String> metaUI) throws IQException {
		BeanInfo bi = null;
		log.debug("Set Bean Attributes: "+ object+"  -> "+metaUI);
		try {
			bi = Introspector.getBeanInfo(object.getClass());
			log.debug("Bean Info: "+ bi.getBeanDescriptor().getDisplayName());
		} catch (IntrospectionException e1) {
			throw new IQException("urn:scorpio4:oops:config#"+object.toString(),e1);
		}
		PropertyDescriptor pds[] = bi.getPropertyDescriptors();
		for (int i = 0; i < pds.length; i++) {
			setBeanProperty(object, pds[i], metaUI);
		}
		MethodDescriptor mds[] = bi.getMethodDescriptors();
		for (int i = 0; i < mds.length; i++) {
			setBeanMethod(object, mds[i], metaUI);
		}
	}

	// bean methods

	private void setBeanMethod(Object object, MethodDescriptor md, Map<String, String> metaUI) throws IQException {
		String name = md.getName();
		String id = getBeanId(object);

		Method method = md.getMethod();
		Type[] type = method.getGenericParameterTypes();
		Type value = method.getGenericReturnType();
		if (metaUI.containsKey(name)) {
			log.trace("+Method: " + id + " -> " + name + " --> "+ Arrays.toString(type)+" => "+value);
		} else {
//            log.trace("Method: " + id + " -> " + name + " --> "+Arrays.toString(type)+" => "+value);
		}
	}

	// bean methods

	private void setBeanProperty(Object obj, PropertyDescriptor pd, Map<String, String> metaUI) throws IQException {
		String name = pd.getName();
		// resolve local, then global
		String value = metaUI.get(name);
		value = value==null?metaUI.get(getIdentity()+name):value;
		if (value!=null) {
			log.trace("setBeanProperty: "+name+" => "+value);
			Method writeMethod = pd.getWriteMethod();
			Class type = pd.getPropertyType();
			if (writeMethod!=null && convertsType.isTypeSupported(type)) {
				try {
					Object convertedValue = convertsType.convertToType(value, type);
					log.debug("Set: "+obj.toString()+" -> "+name+" := "+value+" / "+convertedValue+"  ["+writeMethod.getName()+"]");
					writeMethod.invoke(obj, convertedValue);
				} catch (IllegalAccessException e1) {
					throw new IQException("urn:scorpio4:iq:builder:oops:property-access#"+name,e1);
				} catch (InvocationTargetException e1) {
					throw new IQException("urn:scorpio4:iq:builder:oops:property-invocation#"+name,e1);
				}
			} else {
				log.trace("?method: "+name+" -> "+writeMethod);
			}
		}
	}

	public void setDelegate(Object d) {
		this.delegate=d;
	}

	protected Object delegate(Object parent, String p, Object child) throws IQException {
		if (delegate==null) return child;
		if (parent==null) throw new NullPointerException("Parent cannot be NULL");
		if (child==null) throw new NullPointerException("Child cannot be NULL");

		log.debug("Delegate method: "+p+"("+parent+", "+child+")");
		BeanInfo bi = null;
		try {
			bi = Introspector.getBeanInfo(delegate.getClass());
		} catch (IntrospectionException e1) {
			throw new IQException("urn:scorpio4:oops:config#",e1);
		}
		MethodDescriptor mds[] = bi.getMethodDescriptors();
		int l = 0;
		for (int mi = 0; mi < mds.length; mi++) {
			Method method = mds[mi].getMethod();
			if (p.endsWith(method.getName())) {
				int paramCount = method.getGenericParameterTypes().length;
				if (paramCount>1) {
					Object[] params = new Object[paramCount];
					params[l++] = parent;
					params[l++] = child;
					String $child = getBeanId(child);
					Map attributes = attributesMap.get($child);
					try {
						l = annotatedParameters(method, l, params, parent, child, attributes);
						log.debug("Invoking Method: "+method.getName()+" (x"+paramCount+" / "+l+") -> "+Arrays.toString(params));
						method.invoke(delegate, params);
						log.debug("Linked: " + p + " -> " + method.getName() + Arrays.toString(params)+" -> "+attributes);
					} catch (IllegalAccessException e) {
						throw new IQException("urn:scorpio4:iq:builder:oops:illegal-access#"+method.getName(),e);
					} catch (InvocationTargetException e) {
						throw new IQException("urn:scorpio4:iq:builder:oops:invocation-failed#"+method.getName(),e);
					}
				}
			}
		}
		return child;
	}

	protected Object delegate(Object c, Map<String, String> attributes) throws IQException {
		return delegate(null, c,attributes);
	}

	// Heuristic:
	// Invoke all methods on delegate that end with the Child classname.
	// @Using annotations are used to identify attribute names

	protected Object delegate(Object p, Object c, Map<String, String> attributes) throws IQException {
		if (delegate==null) return c;
		BeanInfo bi = null;
		try {
			bi = Introspector.getBeanInfo(delegate.getClass());
		} catch (IntrospectionException e1) {
			throw new IQException("urn:scorpio4:oops:config#",e1);
		}
		MethodDescriptor mds[] = bi.getMethodDescriptors();

		for (int mi = 0; mi < mds.length; mi++) {
//            log.trace("Delegate?: "+mds[mi].getName()+" -> "+mds[mi].getDisplayName()+" -> "+mds[mi].getShortDescription());
			Method method = mds[mi].getMethod();
			try {
				delegate(method,p,c,attributes);

			} catch (IllegalAccessException e) {
				throw new IQException("urn:scorpio4:iq:builder:oops:illegal-access#"+method.getName(),e);
			} catch (InvocationTargetException e) {
				throw new IQException("urn:scorpio4:iq:builder:oops:invocation-failed#"+method.getName(),e);
			}
		}
		return c;
	}

	protected Object delegate(Method method, Object p, Object c, Map<String, String> attributes) throws IQException, InvocationTargetException, IllegalAccessException {

		Annotation[][] parameterAnnotations = method.getParameterAnnotations();
		Object[] params = new Object[parameterAnnotations.length];
		Type[] genericParameterTypes = method.getGenericParameterTypes();

		if (genericParameterTypes.length<2) {
//            log.trace("Insufficient Parameters: " + method.getName()  + " only has "+ genericParameterTypes.length);
			return c;
		}

		// fail fast, check number of parameters match
		if (params.length!=method.getParameterTypes().length) {
			log.error("Mismatched Parameters: " + method.getName() + " " + params.length + " & " + genericParameterTypes.length);
			throw new IQException("urn:scorpio4:iq:builder:oops:incorrect-parameter-count#"+method.getName());
		}
		int l = 0;
		if (isInstanceOf(p, genericParameterTypes[l]) ) {
			params[l++] = p; // first parameter is our parent bean
		}
		// does our child have expected parameter type
		if (!isInstanceOf(c, genericParameterTypes[l])) return c;

		params[l++] = c; // next parameter is our child bean

//        log.debug("Delegate to: "+method.getName()+" with "+params.length+" parameters as: "+typeOf);

		// Non-annotated parameters will be null.
		l = annotatedParameters(method, l, params, p, c, attributes);

		// Method must have at least one @Using annotation.
		if (l>1) {
			method.invoke(delegate, params);
			log.debug("Invoked: "+method.getName()+" args: "+l+"("+Arrays.toString(params)+")");
		} else {
			log.trace("No @. Skipped: "+method.getName()+" "+l+"("+Arrays.toString(params)+") "+parameterAnnotations.length);
		}
		return c;
	}

	protected boolean isInstanceOf(Object c, Type type) throws IQException {
		if (c==null||type==null) return false;
		if (!(type instanceof Class)) return false;
		Class typeOf = (Class)type;
		return typeOf.isInstance(c);
	}

	protected int annotatedParameters(Method method, int ix, Object[] params, Object p, Object c, Map<String, String> attributes) throws IQException, InvocationTargetException, IllegalAccessException {
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();
		Type[] genericParameterTypes = method.getGenericParameterTypes();
		for(int i0=0;i0<parameterAnnotations.length;i0++) {
			log.debug("A0: " + Arrays.toString(parameterAnnotations[i0]));
			for(int i1=0;i1<parameterAnnotations[i0].length;i1++) {
				Annotation annotation = parameterAnnotations[i0][i1];
				if (annotation instanceof Using) {
					Using usingAnnotation = (Using)parameterAnnotations[i0][i1];
					if (usingAnnotation!=null && attributes!=null) {
						Object value = attributes.get(usingAnnotation.value());
						if (value!=null) {
							Object convertedValue = convertsType.convertToType(value.toString(), (Class) genericParameterTypes[ix] );
							params[ix++] = convertedValue;
							log.trace("["+ix+"] "+usingAnnotation + " -> " + value+" ("+convertedValue.getClass()+")");
						} else {
							log.trace(" "+ix+"] "+usingAnnotation + "-> "+attributes);
						}
					} else {
						log.debug("No attributess: "+i0+" & "+i1);
					}
				}
			}
		}
		return ix;
	}

	// create a new bean

	private Object newBean(String classURI) throws IQException {
		// simple guess if we're an explicit canonical path rather than a mapping
		String className = classURI.indexOf(".")>0&&classURI.indexOf(":")<0?classURI:resolveBeanClass(classURI);
		try {
			Class cc = getClassLoader().loadClass(className);
			return  cc.newInstance();
		} catch (ClassNotFoundException e) {
			throw new IQException("urn:scorpio4:iq:builder:oops:missing-class#"+className);
		} catch (InstantiationException e) {
			throw new IQException("urn:scorpio4:iq:builder:oops:instantiate#"+className,e);
		} catch (IllegalAccessException e) {
			throw new IQException("urn:scorpio4:iq:builder:oops:class-not-public#"+className,e);
		} catch(Exception e) {
			throw new IQException("urn:scorpio4:iq:builder:oops:unknown#"+className,e);
		}
	}

	private String resolveBeanClass(String classURI) throws IQException {
		String className = classMap.get(classURI);
		if (className!=null) {
			log.debug("Resolved class: " + classURI+" -> "+className);
			return className;
		}
		throw new IQException("urn:scorpio4:iq:builder:oops:unresolved-class#"+classURI);
	}

	// Implement FactStream

	@Override
	public void fact(String s, String p, Object o) throws FactException {
		log.debug("<> " + s + " -> " + p + " -> " + o);
		if (p.equals(INSTANCE_OF)) {
			try {
				Object ignored = getBean(s, o.toString());
				Map attributes = new HashMap();
				attributes.put("this", s);
				attributesMap.put( s, attributes );
			} catch (IQException e) {
				log.error("IQ Error: " + s + " -> " + p + " -> " + o, e);
			}
		} else {
			containerMap.put(s, new Tuple(p,o) );
		}
	}

	@Override
	public void fact(String s, String p, Object o, String xsdType) throws FactException {
		log.debug("=: " + s + " -> " + p + " -> " + o);
		Map<String,String> attributes = attributesMap.get(s);
		if (attributes == null) {
			attributes = new HashMap();
			attributes.put("this", s);
			attributesMap.put( s, attributes );
		}
		attributes.put(p,o.toString());
	}


	@Override
	public String getIdentity() {
		return baseURI;
	}

	public void setIdentity(String identity) {
		this.baseURI=identity;
	}

	// clear component including component caches
	public void clear() {
		idComponentMap.clear();
		componentIdMap.clear();
		classMap.clear();
		reset();
	}

	// reset build-time state whilst preserving component cache  - getBean and getBeanId still work
	public BeanBuilder reset() {
		containerMap.clear();
		attributesMap.clear();
		return this;
	}

}
class Tuple {
	Object p = null, o = null;
	public Tuple(Object p, Object o){ this.p=p; this.o=o; }
	public String toString() { return p+":->"+o; }
}

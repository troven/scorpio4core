package com.factcore.deploy;
/*
 *   Fact:Core - CONFIDENTIAL
 *   Unpublished Copyright (c) 2009-2014 Lee Curtis, All Rights Reserved.
 *
 *   NOTICE:  All information contained herein is, and remains the property of Lee Curtis. The intellectual and technical concepts contained
 *   herein are proprietary to Lee Curtis and may be covered by Australian, U.S. and Foreign Patents, patents in process, and are protected by trade secret or copyright law.
 *   Dissemination of this information or reproduction of this material is strictly forbidden unless prior written permission is obtained
 *   from Lee Curtis.  Access to the source code contained herein is hereby forbidden to anyone except current Lee Curtis employees, managers or contractors who have executed
 *   Confidentiality and Non-disclosure agreements explicitly covering such access.
 *
 *   The copyright notice above does not evidence any actual or intended publication or disclosure  of  this source code, which includes
 *   information that is confidential and/or proprietary, and is a trade secret, of Lee Curtis.   ANY REPRODUCTION, MODIFICATION, DISTRIBUTION, PUBLIC  PERFORMANCE,
 *   OR PUBLIC DISPLAY OF OR THROUGH USE  OF THIS  SOURCE CODE  WITHOUT  THE EXPRESS WRITTEN CONSENT OF LEE CURTIS IS STRICTLY PROHIBITED, AND IN VIOLATION OF APPLICABLE
 *   LAWS AND INTERNATIONAL TREATIES.  THE RECEIPT OR POSSESSION OF  THIS SOURCE CODE AND/OR RELATED INFORMATION DOES NOT CONVEY OR IMPLY ANY RIGHTS
 *   TO REPRODUCE, DISCLOSE OR DISTRIBUTE ITS CONTENTS, OR TO MANUFACTURE, USE, OR SELL ANYTHING THAT IT  MAY DESCRIBE, IN WHOLE OR IN PART.
 *
 * 1st May 2014 - Licensed to Apscore
 */

import com.factcore.assets.SesameAssetRegister;
import com.factcore.util.Identifiable;
import com.factcore.vendor.sesame.io.SPARQLer;
import com.factcore.vocab.COMMON;
import com.factcore.fact.FactSpace;
import com.factcore.oops.FactException;
import com.factcore.util.Steps;
import com.factcore.util.io.StreamCopy;
import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class SesameDeployer implements Identifiable {
    private final Logger log = LoggerFactory.getLogger(getClass());

	private boolean deployRDF = true;
	private boolean deployScripts = false;
	private boolean obliterate = false;
    private boolean isBooted = false;

	private ValueFactory values = null;

    private String identity;
	private RepositoryConnection connection = null;
	private Map<String, String> extension2type = new HashMap();
    private Map<String, Boolean> scriptTypes = new HashMap();

	// RDFS PRIMARIES//
	private URI provenanceContext = null, a = null, subClassOf = null, hasAsset = null;
    URI textType = null, rdfsLabel = null, mimeExtension = null;
    ScriptEngineManager sem = new ScriptEngineManager();

    public SesameDeployer(FactSpace factSpace) throws FactException {
        init(factSpace.getConnection(), factSpace.getIdentity());
    }

	public void init(RepositoryConnection connection, String context) throws FactException {
        try {
            setConnection(connection);
            setProvenance(context);
            initScriptEngines();
        } catch (RepositoryException e) {
            throw new FactException("Repository error:"+e.getMessage(),e);
        }
    }

	protected void initScriptEngines() throws FactException {
		List<ScriptEngineFactory> scriptEngineFactories = sem.getEngineFactories();
		try {
			this.textType = values.createURI(COMMON.MIME_PLAIN);
            this.rdfsLabel = values.createURI(COMMON.RDFS+"label");
            this.mimeExtension = values.createURI(COMMON.MIME_TYPE+"extension");
			for (int i=0;i<scriptEngineFactories.size();i++) {
				ScriptEngineFactory sef = scriptEngineFactories.get(i);
				List<String> extensions = sef.getExtensions();
				List mimes = sef.getMimeTypes();
				for(int m=0;m<mimes.size();m++) {
					URI mimeURI = values.createURI(COMMON.MIME_TYPE+mimes.get(m));
					getConnection().add(mimeURI, subClassOf, textType, provenanceContext);
					getConnection().add(mimeURI, a, textType, provenanceContext);
					getConnection().add(mimeURI, rdfsLabel, values.createLiteral(sef.getLanguageName()), provenanceContext);
					log.debug("Scripting Abilities: "+sef.getLanguageName()+" --> "+mimeURI.toString());
					for(int y=0;y<extensions.size();y++) {
						getConnection().add(mimeURI, mimeExtension, values.createLiteral(extensions.get(y)));
						extension2type.put(extensions.get(y), mimeURI.toString());
                        scriptTypes.put(extensions.get(y), new Boolean(true));
					}
				}
			}
			extension2type.put("sparql", COMMON.MIME_TYPE+ "application/x-sparql-query");
			extension2type.put("camel.xml",	COMMON.MIME_TYPE+ "application/x-camel-route");
			extension2type.put("asq",	COMMON.MIME_TYPE+ "application/x-asq");

			extension2type.put("xhtml", COMMON.MIME_TYPE+ "application/xhtml+xml");
			extension2type.put("txt", COMMON.MIME_TYPE+"plain/text");
            extension2type.put("json", COMMON.MIME_TYPE+ "application/json");
            extension2type.put("css", COMMON.MIME_TYPE+"plain/css");
            extension2type.put("xml", COMMON.MIME_TYPE+"application/xml");
		} catch (RepositoryException e) {
			throw new FactException("Repository failed: "+e.getMessage(),e);
		}
	}

	public void clean() throws FactException {
        try {
            getConnection().clear(this.provenanceContext);
	        getConnection().commit();
	        SPARQLer.defaultNamespaces(getConnection());
        } catch (RepositoryException e) {
            throw new FactException("Obliterate failed: "+e.getMessage(), e);
        }
		if (isDeployingScripts()) initScriptEngines();
        isBooted = true;
        log.debug("Cleaned: "+this.provenanceContext);
	}

	public void deploy(File zipFile) throws FactException, IOException, RepositoryException {
		log.debug("Deploying: "+zipFile.getAbsolutePath());
		if (zipFile.isDirectory()) {
			deploy(zipFile, zipFile, true);
		} else {
            deploy(new JarFile(zipFile));
		}
		getConnection().commit();
	}

	public void deploy(java.net.URL url) throws FactException, IOException {
		JarURLConnection jarURL = (JarURLConnection)url.openConnection();
		deploy(jarURL.getJarFile());
	}

	public void deploy(ZipFile zipFile) throws FactException, IOException {
        log.debug("Deploying Archive: "+zipFile.getName());
		try {
			Enumeration entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = (ZipEntry) entries.nextElement();
                if(!entry.isDirectory()) deploy(zipFile, entry);
			}
		} catch (RepositoryException e) {
			throw new FactException("Repository failed: "+zipFile.getName(), e);
		} catch (RDFParseException e) {
			throw new FactException("N3 failed: "+zipFile.getName(), e);
		}
	}

	protected void deploy(ZipFile zipFile, ZipEntry entry ) throws FactException, IOException, RDFParseException, RepositoryException {
		deploy( "hacked:"+entry.getName(), zipFile.getInputStream(entry) );
	}

	protected int deploy(File home, File dir, boolean recurse) throws FactException, IOException {
		log.debug("Deploying Directory: "+dir.getAbsolutePath());
		int count = 0;
		File[] files = dir.listFiles();
		if(files==null) throw new IOException("Directory listing failed: "+dir.getAbsolutePath());
		for(int i=0;i<files.length;i++) {
			if (files[i].getName().startsWith(".")) {
				// ignore
			} else if (files[i].isFile()) {
				deploy(home, files[i]);
				count ++;
			}
		}

        for(int i=0;i<files.length;i++) {
            if (files[i].getName().startsWith(".")) {
                // ignore
            } else if (files[i].isDirectory() && recurse) {
                count += deploy(home, files[i], recurse);
            }
        }

		return count;
	}

	protected void deploy(File home, File file) throws FactException, IOException {
		FileInputStream inStream = new FileInputStream(file);
		deploy(Steps.localize("", home.getParentFile(), file), inStream);
        inStream.close();
	}

	public void deploy(String localPath, InputStream inStream) throws FactException, IOException {
        String extension = Steps.toExtension(localPath);
        if (extension==null) {
//          throw new FactException("Missing extension. Resource type unknown: "+localPath);
            log.debug("Missing Extension. Resource type unknown: "+localPath);
            return;
        }
		try {
			if (extension.equals("n3") && isDeployingRDF()) {
				n3(localPath, inStream);
			} else if (extension.equals("ttl") && isDeployingRDF()) {
				deployTTL(localPath, inStream);
			} else if (extension.equals("rdf.xml") && isDeployingRDF()) {
				deployRDFXML(localPath, inStream);
            } else if (extension.equals("rdf") && isDeployingRDF()) {
                deployRDFXML(localPath, inStream);
			} else if (extension.equals("owl.xml") && isDeployingRDF()) {
				deployRDFXML(localPath, inStream);
            } else if (extension.equals("owl") && isDeployingRDF()) {
                deployRDFXML(localPath, inStream);
            } else if (extension.equals("nt") && isDeployingRDF()) {
                deployNT(localPath, inStream);
			} else if (scriptTypes.containsKey(extension) && isDeployingScripts() ) {
                script(localPath, inStream);
            } else if(extension2type.containsKey(extension) && isDeployingScripts() ) {
                asset(localPath, inStream);
            }
		} catch (RDFParseException e) {
			throw new FactException("Corrupt RDF: "+e.getMessage()+"-> "+localPath+" -> "+e.getLineNumber()+":"+e.getColumnNumber(),e);
		} catch (RepositoryException e) {
			throw new FactException("Failed to import RDF: "+localPath,e);
		}
	}

    public void n3(String localPath, InputStream inStream) throws IOException, RDFParseException, RepositoryException {
        String baseURN = toURN(localPath, ":");
        log.debug("Deploying N3: "+baseURN+" in: "+provenanceContext+" ("+inStream.available()+")");
        getConnection().add(inStream, baseURN, RDFFormat.N3, provenanceContext);
        describeAsset(baseURN);
    }

    public void asset(String scriptName, InputStream inStream) throws IOException, RDFParseException, RepositoryException {
        String uri = toURN(scriptName, ":");
        log.debug("Asset: "+scriptName+" -> "+uri);
        URI scriptURI = values.createURI(uri);
        asset(scriptName, scriptURI, inStream);
    }

    public void script(String scriptName, InputStream inStream) throws IOException, RDFParseException, RepositoryException {
        String uri = toURN(scriptName, ".");
        log.debug("Script: "+scriptName+" -> "+uri);
        URI beaURI = values.createURI(uri);
        asset(scriptName, beaURI, inStream);
    }

    protected void asset(String scriptName, URI scriptURI, InputStream inStream) throws IOException, RDFParseException, RepositoryException {
		String script = StreamCopy.toString(inStream);
		String extension = Steps.toExtension(scriptName);

		String scriptType = extension2type.get(extension);
		if (scriptType==null) return; // script extension no recognised ... silently ignore
		URI mimeType = values.createURI(scriptType);

		Literal scriptBody = values.createLiteral(script, mimeType);
        getConnection().add( scriptURI, hasAsset, scriptBody, provenanceContext);
        log.debug("Deployed "+extension+" asset: " + scriptName+" -> "+scriptURI.toString() + " as " + scriptType);

//        getConnection().add( scriptURI , rdfsLabel, values.createLiteral(scriptName), provenanceContext );
        describeAsset(scriptURI.toString());

//      URI scriptTypeURI = values.createURI(VOCAB.IQ+PrettyString.camelCase(extension)+"Behaviour");
//      getConnection().add(scriptURI, values.createURI(VOCAB.A), scriptTypeURI, applicationContext);
//		getConnection().add(scriptURI, a , scriptClass, applicationContext);
	}

    private void describeAsset(String baseURN) throws RepositoryException {
        String label = new Steps(baseURN).local();
        getConnection().add( values.createURI(baseURN) , rdfsLabel, values.createLiteral(label), provenanceContext );
    }

    public void deployNT(String localPath, InputStream inStream) throws IOException, RDFParseException, RepositoryException {
        String baseURN = toURN(localPath, ":");
        log.debug("Deploying NTriples for: "+baseURN+" in: "+provenanceContext);
        getConnection().add(inStream, baseURN, RDFFormat.NTRIPLES, provenanceContext);
        describeAsset(baseURN);
    }

	public void deployTTL(String localPath, InputStream inStream) throws IOException, RDFParseException, RepositoryException {
        String baseURN = toURN(localPath, ":");
		log.debug("Deploying TTL for: "+baseURN+" in: "+provenanceContext);
		getConnection().add(inStream, baseURN, RDFFormat.TURTLE, provenanceContext);
        describeAsset(baseURN);
	}

	public void deployRDFXML(String localPath, InputStream inStream) throws IOException, RDFParseException, RepositoryException {
        String baseURN = toURN(localPath, ":");
		log.debug("Deploying RDF/XML for: "+baseURN+" in: "+provenanceContext);
		getConnection().add(inStream, baseURN, RDFFormat.RDFXML, provenanceContext);
        describeAsset(baseURN);
	}

	public ClassLoader loadClasses(URL zipFile) throws ClassNotFoundException, NoSuchMethodException {
		return loadClasses(zipFile, getClass().getClassLoader());
	}

	public ClassLoader loadClasses(URL zipFile, ClassLoader classLoader) throws ClassNotFoundException, NoSuchMethodException {
		URL[] jars = new URL[1];
		jars[0] = zipFile;
		return new URLClassLoader (jars, classLoader);
	}

//	public void setFinder(String uri) throws ConfigException, FactException {
//        setProvenance(uri);
//		FactSource core = this.factCore.getFactSource(uri);
//		if (core==null || !(core instanceof SesameFactSource)) throw new FactException("Finder is not Sesame");
//		this.connection = ((SesameFactSource)core).getConnection();
//	}

	public RepositoryConnection getConnection() {
		return connection;
	}

 	public void setConnection(RepositoryConnection connection) throws RepositoryException {
		this.connection = connection;
		this.values = connection.getValueFactory();
		this.a = values.createURI(COMMON.RDFS_TYPE);
		this.subClassOf = values.createURI(COMMON.RDFS_SUBCLASS);
        this.hasAsset = values.createURI(SesameAssetRegister.HAS_ASSET);
	}

    public void setProvenance(String provenance) {
        this.identity=provenance;
        this.provenanceContext = values.createURI(provenance);
    }

	public boolean isDeployingRDF() {
		return deployRDF;
	}

	public void setDeployRDF(boolean deployRDF) {
		this.deployRDF = deployRDF;
	}

	public boolean isDeployingScripts() {
		return deployScripts;
	}

	public void setDeployScripts(boolean deployScripts) {
		this.deployScripts = deployScripts;
	}

    public String toURN(String localName, String sep) {
        Steps steps = new Steps(localName);
        steps = steps.stripExtension();
        return getIdentity()+steps.toString( 1,-1,sep);
    }

    @Override
    public String getIdentity() {
        return identity;
    }
}

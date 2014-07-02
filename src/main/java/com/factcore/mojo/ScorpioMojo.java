package com.factcore.mojo;

import com.factcore.assets.Asset;
import com.factcore.assets.AssetRegister;
import com.factcore.assets.AssetRegisters;
import com.factcore.fact.FactSpace;
import com.factcore.util.Stopwatch;
import com.factcore.util.map.MapUtil;
import com.factcore.vendor.sesame.RepositoryManager;
import com.factcore.vendor.sesame.store.NativeRDFSRepository;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.http.HTTPRepository;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Hashtable;
import java.util.Map;

/**
 * Fact:Core (c) 2014
 * Module: com.factcore.maven
 * User  : lee
 * Date  : 16/06/2014
 * Time  : 5:36 PM
 */
public abstract class ScorpioMojo extends AbstractMojo {
	public File srcPath = null, targetPath = null, tempPath = null, resourcesPath=null;
	public String identity = null;
	public Stopwatch stopWatch = new Stopwatch();
	String PROJECT_PREFIX = "scorpio4";

//    public MavenProject project;

	public Repository repository = null;
	public RepositoryConnection connection = null;
	public AssetRegister assetRegister;
	public RepositoryManager repositoryManager;

	public ScorpioMojo() {
    }

    public void initialize() throws MojoFailureException, RepositoryException, MalformedURLException, RepositoryConfigException {
        Hashtable properties = getProject().getProperties();
        this.identity = MapUtil.getString(properties, PROJECT_PREFIX+".id", null);
        if (this.identity==null) throw new MojoFailureException("Missing <factcore.id>");

        this.srcPath = MapUtil.getFile(properties, PROJECT_PREFIX+".src.path", getProject().getBasedir() );
        this.resourcesPath = MapUtil.getFile(properties, PROJECT_PREFIX+".resources.path", srcPath );
        this.targetPath = MapUtil.getFile(properties, PROJECT_PREFIX+".target.path", getProject().getBasedir() );
        this.tempPath = MapUtil.getFile(properties, PROJECT_PREFIX+".temp.path", getProject().getBasedir() );

        initializeRepository();
    }

    protected void initializeRepository() throws RepositoryException, MalformedURLException, RepositoryConfigException {
	    this.repositoryManager = new RepositoryManager(getTempPath());
	    this.repository = repositoryManager.getRepository(getIdentity());
//	    this.repository = newLocalRepository();
        this.connection = repository.getConnection();
	    getLog().info("Repository: "+getIdentity()+" @ "+getTempPath());
        this.assetRegister = new AssetRegisters(connection);
    }

    public void finished() throws RepositoryException {
        if (connection!=null && connection.isOpen()) connection.close();
        if (repository!=null && repository.isInitialized()) repository.shutDown();
        getLog().info("Finished MoJo: "+getClass().getSimpleName());
    }

    public abstract MavenProject getProject();

    public File getSrcPath() {
        return srcPath;
    }

    public File getResourcesPath() {
        return resourcesPath;
    }

    public File getTargetPath() {
        return targetPath;
    }

    public File getTempPath() {
        return tempPath;
    }

    public String getAppName() {
        return getProject().getName();
    }

    public Asset getAsset(String uri, String mimeType) throws IOException {
        return assetRegister.getAsset(uri,mimeType);
    }

    public String getIdentity() {
        return this.identity;
    }

    public Repository getRepository() {
        return repository;
    }

    public RepositoryConnection getConnection() {
        return connection;
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            initialize();
            executeInternal();
        } catch (Exception e) {
            getLog().error(e.getMessage());
            throw new MojoExecutionException("Fact Error: "+e.getMessage(), e);
        } finally {
            try {
                finished();
            } catch (RepositoryException e) {
                throw new MojoExecutionException("Repository Shutdown Error", e);
            }
        }
    }

    public FactSpace getFactSpace() {
        return new FactSpace(getConnection(), getIdentity());
    }

    public abstract void executeInternal() throws Exception;

    public Repository newLocalRepository() throws RepositoryException {
        File dataDir = new File(tempPath, "sandbox");
        dataDir.mkdirs();
        NativeRDFSRepository nativeRDFSRepository = new NativeRDFSRepository(dataDir);
        nativeRDFSRepository.initialize();
        return nativeRDFSRepository;
    }

    public Repository newSandboxRepository(Map properties) throws RepositoryException {
        String host = MapUtil.getString(properties, PROJECT_PREFIX+".upload.host", null);
        String name = MapUtil.getString(properties, PROJECT_PREFIX+".upload.name", null);
        File dataDir = new File(tempPath, "sandbox");
        dataDir.mkdirs();

        if (host==null||host.equals("")) {
            getLog().info("Using Native Repository: "+ dataDir.getAbsolutePath());
            return new NativeRDFSRepository(dataDir);
        }

        HTTPRepository repository = new HTTPRepository(host, name);
        repository.setDataDir(dataDir);
        repository.initialize();
        getLog().info("Connected to "+ host+name );
        return repository;
    }
}

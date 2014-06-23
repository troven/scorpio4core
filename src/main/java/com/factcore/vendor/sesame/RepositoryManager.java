package com.factcore.vendor.sesame;
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
 */

import com.factcore.util.Identifiable;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfig;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.manager.LocalRepositoryManager;
import org.openrdf.repository.manager.RemoteRepositoryManager;
import org.openrdf.repository.manager.RepositoryInfo;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.NotifyingSail;
import org.openrdf.sail.helpers.SailBase;
import org.openrdf.sail.inferencer.fc.ForwardChainingRDFSInferencer;
import org.openrdf.sail.memory.MemoryStore;
import org.openrdf.sail.nativerdf.NativeStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Fact:Core (c) 2010-2013
 * User: lee
 * Date: 18/01/13
 * Time: 7:07 PM
 * <p/>
 * This code does something useful
 */
public class RepositoryManager extends org.openrdf.repository.manager.RepositoryManager implements Identifiable {
	private static final Logger log = LoggerFactory.getLogger(RepositoryManager.class);

	private String coreName = null;
	private URL location = null;
	private LocalRepositoryManager localManager = null;
	private RemoteRepositoryManager remoteManager = null;

	public RepositoryManager(String name, URL remoteServer, File home) throws RepositoryException, MalformedURLException {
		setCoreName(name);
		if (home!=null) {
            initialize(home);
		}
		if (remoteServer!=null) {
            initialize(remoteServer);
		}
		initialize();
	}

    private void initialize(URL url) {
        setLocation(url);
        setRemoteManager(new RemoteRepositoryManager(url.toExternalForm()));
//			remoteManager.setUsernameAndPassword(username, password);
        log.debug("Initialized Remote Repository: "+ getLocation().toString() );
    }

    private void initialize(File home) throws MalformedURLException {
        home.mkdirs();
        setLocalManager(new LocalRepositoryManager(home));
        log.debug("Initialized Local Repository: "+home.getAbsolutePath() );
        setLocation(home.toURI().toURL());
    }

    public void initialize() throws RepositoryException {
		if (getLocalManager()!=null) getLocalManager().initialize();
		if (getRemoteManager()!=null) getRemoteManager().initialize();
	}

	public Repository getCoreRepository() throws RepositoryException, RepositoryConfigException {
		Repository repo = null;
		if (getLocalManager()!=null) {
			repo = getLocalManager().getRepository(this.getCoreName());
			if (repo!=null) {
				log.debug("Local Core Repository: "+ this.getCoreName());
				return repo;
			}
		}
		if (getRemoteManager()!=null) {
			repo = getRemoteManager().getRepository(this.getCoreName());
			if (repo!=null) {
				log.debug("Remote Core Repository: "+ this.getCoreName());
				return repo;
			}
		}
		log.error("Missing Core Repository: " + this.getCoreName());
		throw new RepositoryException("Missing Core Repository: "+ this.getCoreName());
	}

	public void addRepositoryConfig(RepositoryConfig config) throws RepositoryConfigException, RepositoryException {
		if (isRemote(config.getID())) {
			log.debug("Adding Remote Repository: "+config.getID() );
			getRemoteManager().addRepositoryConfig(config);
		} else {
			log.debug("Adding Local Repository: "+config.getID() );
			getLocalManager().addRepositoryConfig(config);
		}
	}

	public Repository getRepository(String id) throws RepositoryException, RepositoryConfigException {
		Repository repo = getLocalManager().getRepository(id);
		if (repo!=null) return repo;
		return getRemoteManager().getRepository(id);
	}

	@Override
	protected Repository createSystemRepository() throws RepositoryException {
		throw new RepositoryException("SystemRepository is protected");
	}

	@Override
	protected Repository createRepository(String id) throws RepositoryConfigException, RepositoryException {
		throw new RepositoryException("Repository can't be created, only added");
	}

	public Repository createRepository() throws RepositoryException {
		SailBase base_store = new MemoryStore();
		Repository repo = new SailRepository(new ForwardChainingRDFSInferencer((NotifyingSail) base_store));
		repo.initialize();
		return repo;
	}

	public static Repository createRepository(File dataDir) throws RepositoryException {
		dataDir.mkdirs();
		String indexes = "spoc,posc,cosp";
		SailBase base_store = new NativeStore(dataDir, indexes);
		Repository repo = new SailRepository(new ForwardChainingRDFSInferencer((NotifyingSail) base_store));
		repo.initialize();
		return repo;
	}

	@Override
	public RepositoryInfo getRepositoryInfo(String id) throws RepositoryException {
		RepositoryInfo repoInfo = getLocalManager().getRepositoryInfo(id);
		if (repoInfo!=null) return repoInfo;
		return getRemoteManager().getRepositoryInfo(id);
	}

	@Override
	public Collection<RepositoryInfo> getAllRepositoryInfos(boolean skipSystemRepo) throws RepositoryException {
		List<RepositoryInfo> repoInfos = new ArrayList();
		repoInfos.addAll(getLocalManager().getAllRepositoryInfos(skipSystemRepo));
		repoInfos.addAll(getRemoteManager().getAllRepositoryInfos(skipSystemRepo));
		return repoInfos;
	}

	@Override
	protected void cleanUpRepository(String repositoryID) throws IOException {
	}

	protected boolean isRemote(String uri) {
		return uri.startsWith("http://")||uri.startsWith("https://");
	}

	@Override
	public URL getLocation()  {
		return location;
	}

	public String getIdentity() {
		return location.toExternalForm();
	}

	public String getCoreName() {
		return coreName;
	}

	public void setCoreName(String coreName) {
		this.coreName = coreName;
	}

	public void setLocation(URL location) {
		this.location = location;
	}

	public LocalRepositoryManager getLocalManager() {
		return localManager;
	}

	public void setLocalManager(LocalRepositoryManager localManager) {
		this.localManager = localManager;
	}

	public RemoteRepositoryManager getRemoteManager() {
		return remoteManager;
	}

	public void setRemoteManager(RemoteRepositoryManager remoteManager) {
		this.remoteManager = remoteManager;
	}
}

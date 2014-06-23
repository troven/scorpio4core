package com.factcore.vendor.sesame.store;

import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.inferencer.fc.ForwardChainingRDFSInferencer;
import org.openrdf.sail.nativerdf.NativeStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * KnowPoint (c) 2013
 * Module: com.cuebic.persist
 * User  : lee
 * Date  : 13/12/2013
 * Time  : 11:36 PM
 */
public class NativeRDFSRepository extends SailRepository {
	private static final Logger log = LoggerFactory.getLogger(NativeRDFSRepository.class);

	public NativeRDFSRepository(File repositoryDir) throws RepositoryException {
		super(new ForwardChainingRDFSInferencer(new NativeStore(repositoryDir, "spoc") ));
        initialize();
		if (!isWritable()) throw new RepositoryException("CoreRepository is not writable: "+repositoryDir.getAbsolutePath());
		log.debug("NativeRDFSRepository @ "+repositoryDir.getAbsolutePath());
	}
}

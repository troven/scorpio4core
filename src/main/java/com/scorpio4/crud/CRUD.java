package com.scorpio4.crud;
/*
 *   Scorpio4 - Apache Licensed
 *   Copyright (c) 2009-2014 Lee Curtis, All Rights Reserved.
 *
 *
 */

import com.scorpio4.oops.ConfigException;
import com.scorpio4.oops.FactException;
import com.scorpio4.util.Identifiable;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

/**
 * Scorpio4 (c) 2010-2013
 * @author lee
 * Date: 31/08/12
 * Time: 2:45 PM
 *
 * Defines a basic CRUD interface
 */
public interface CRUD  extends Identifiable {

    public Model create(Map model) throws FactException;

    public Collection<Map> read(String queryURI, Map model) throws FactException, IOException, ConfigException;

    public Map update(Map model) throws FactException;

    public Map delete(Map model) throws FactException;

    public boolean exists(Map model) throws FactException;

    public Model identify(Map model) throws FactException;

	public Identifiable identity(Map model) throws FactException;
}

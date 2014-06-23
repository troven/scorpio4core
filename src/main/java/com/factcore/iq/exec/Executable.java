package com.factcore.iq.exec;

import com.factcore.assets.Asset;
import com.factcore.oops.AssetNotSupported;
import com.factcore.oops.IQException;

import java.util.Map;
import java.util.concurrent.Future;

/**
 * Fact:Core (c) 2014
 * Module: com.factcore.iq
 * User  : lee
 * Date  : 17/06/2014
 * Time  : 9:18 PM
 */
public interface Executable {

    public Future execute(Asset asset, Map bindings) throws IQException, AssetNotSupported;
}

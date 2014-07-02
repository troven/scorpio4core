package com.scorpio4.iq.exec;

import com.scorpio4.assets.Asset;
import com.scorpio4.oops.AssetNotSupported;
import com.scorpio4.oops.IQException;

import java.util.Map;
import java.util.concurrent.Future;

/**
 * Fact:Core (c) 2014
 * Module: com.scorpio4.iq
 * User  : lee
 * Date  : 17/06/2014
 * Time  : 9:18 PM
 */
public interface Executable {

    public Future execute(Asset asset, Map bindings) throws IQException, AssetNotSupported;
}

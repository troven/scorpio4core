package com.scorpio4.iq.exec;

import com.scorpio4.assets.Asset;
import com.scorpio4.oops.AssetNotSupported;
import com.scorpio4.oops.IQException;

import java.util.Map;
import java.util.concurrent.Future;

/**
 * Scorpio4 (c) 2014
 * Module: com.scorpio4.iq
 * @author lee
 * Date  : 17/06/2014
 * Time  : 9:18 PM
 */
public interface Executable {

    public Future execute(Asset asset, Map bindings) throws IQException, AssetNotSupported;
}

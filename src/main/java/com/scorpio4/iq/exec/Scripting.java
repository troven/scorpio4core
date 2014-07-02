package com.scorpio4.iq.exec;

import com.scorpio4.assets.Asset;
import com.scorpio4.oops.AssetNotSupported;
import com.scorpio4.oops.IQException;
import com.scorpio4.vocab.COMMON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.*;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Fact:Core (c) 2014
 * Module: com.scorpio4.iq
 * User  : lee
 * Date  : 17/06/2014
 * Time  : 9:20 PM
 */
public class Scripting implements Executable {
    ScriptEngineManager scriptEngineManager = new ScriptEngineManager();

    public Scripting() {
    }

    public boolean canExecute(Asset asset) {
	    String contentType = asset.getContentType();
	    ScriptEngine engineByMimeType = scriptEngineManager.getEngineByMimeType(contentType);
        return engineByMimeType!=null;
    }

    @Override
    public Future execute(Asset asset, Map parameters) throws IQException, AssetNotSupported {
	    if (!canExecute(asset)) throw new AssetNotSupported("Not Script: "+asset.getMimeType()+"\n"+asset);
        try {
            return new NotFutureScript(this, asset, parameters);
        } catch (ScriptException e) {
            throw new IQException(e.getMessage());
        }
    }

}

class NotFutureScript implements Future {
    private final Logger log = LoggerFactory.getLogger(Scripting.class);
    Object result = null;
    Scripting scripting = null;

    public NotFutureScript(Scripting scripting, Asset asset, Map paramaters) throws ScriptException, IQException {
        this.scripting=scripting;
        execute(asset,paramaters);
    }

    public void execute(Asset script, Map parameters) throws ScriptException, IQException {
        Bindings bindings = new SimpleBindings();
        bindings.putAll(parameters);
        String mimeType = script.getMimeType().substring(COMMON.MIME_TYPE.length());
        log.info("Executing "+mimeType+" script");
        ScriptEngine scriptEngine = scripting.scriptEngineManager.getEngineByMimeType(mimeType);
        if (scriptEngine==null) throw new IQException("Unknown script: "+mimeType);
        result = scriptEngine.eval(script.toString(), bindings);
        log.debug("Result: " + result);
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return true;
    }

    @Override
    public Object get() throws InterruptedException, ExecutionException {
        return result;
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return result;
    }
}

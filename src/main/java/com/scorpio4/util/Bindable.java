package com.scorpio4.util;
/*
 *   Scorpio4 - Apache Licensed
 *   Copyright (c) 2009-2014 Lee Curtis, All Rights Reserved.
 *
 *
 */
import java.util.Map;

/**
 * scorpio4 (c) 2013
 * Module: com.scorpio4.util
 * @author lee
 * Date  : 18/09/13
 * Time  : 9:26 PM
 */
public interface Bindable {

    public void bind(Map bindings);
	public Map getBindings();

}

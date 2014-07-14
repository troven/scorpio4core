package com.scorpio4.iq.bean;
/*
 *   Scorpio4 - Apache Licensed
 *   Copyright (c) 2009-2014 Lee Curtis, All Rights Reserved.
 *
 *
 */
/**
 * scorpio4 (c) 2013
 * Module: com.scorpio4.iq.bean
 * User  : lee
 * Date  : 31/12/2013
 * Time  : 9:22 PM
 *
 * @author lee
 *
 * A simple abstraction for a generic type converter.
 *
 */
public interface ConvertsType {

    public boolean isTypeSupported(Class type);

    public Object convertToType(String value, Class type) throws ClassCastException;

}

package com.scorpio4.iq.bean;
/*
 *   Scorpio4 - Apache Licensed
 *   Copyright (c) 2009-2014 Lee Curtis, All Rights Reserved.
 *
 *
 */
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * scorpio4 (c) 2013
 * Module: com.scorpio4.iq.annotations
 * @author lee
 * Date  : 31/12/2013
 * Time  : 7:42
 *
 * @author lee
 *
 * Carry-over from Fact:Core's BeanBuilder.
 *
 */
@Retention(RetentionPolicy.RUNTIME)

public @interface Using {
    public String value();
}

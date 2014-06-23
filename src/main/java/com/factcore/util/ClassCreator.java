package com.factcore.util;
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
import com.factcore.oops.IQException;
import com.factcore.util.map.MapUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Fact:Core (c) 2010-2013
 * User: lee
 * Date: 19/02/13
 * Time: 8:02 PM
 * <p/>
 * This code does something useful
 */
public class ClassCreator {
    private static final Logger log = LoggerFactory.getLogger(ClassCreator.class);

	public ClassCreator() {

	}

	public static Identifiable newInstance(Map<String, Object> classConfig) throws IQException {
		return newInstance(classConfig, Identifiable.class);
	}

    public static Identifiable newInstance(Map<String, Object> classConfig, Class type) throws IQException {
        return (Identifiable)newInstance(MapUtil.getString(classConfig,"classname", "IQException"), type);
    }

	public static Object newInstance(Object classname, Class type) throws IQException {
		if (classname==null) return null;
		return newInstance(classname.toString(), type);
	}

    public static Object newInstance(Class type) throws IQException {
        return newInstance( type.getCanonicalName(), type);
    }

	public static Object newInstance(String classname, Class type) throws IQException {
		try {
			Class clasz = Class.forName(classname);
			Object object = clasz.newInstance();
			if (object==null) throw new IQException("urn:factcore:util:creator:oops:null-instance#"+classname);
			if(type!=null && !type.isInstance(object)) {
                log.error("Class:Creator: "+object+" of type: "+type+" -> "+object.getClass().getInterfaces());
                throw new IQException("urn:factcore:util:creator:oops:not-instance#"+type);
            }
			return object;
		} catch (InstantiationException e) {
			throw new IQException("urn:factcore:util:creator:oops:instantiate-failed#"+e.getMessage(),e);
		} catch (IllegalAccessException e) {
			throw new IQException("urn:factcore:util:creator:oops:illegal-access#"+e.getMessage(),e);
		} catch (ClassNotFoundException e) {
			throw new IQException("urn:factcore:util:creator:oops:unknown-class#"+e.getMessage(),e);
		}
	}

}

package com.scorpio4.util.map;
/*
 *   Scorpio4 - CONFIDENTIAL
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
import com.scorpio4.util.DateXSD;
import org.limewire.collection.CharSequenceKeyAnalyzer;
import org.limewire.collection.PatriciaTrie;
import org.limewire.collection.Trie;

import java.io.File;
import java.util.*;

/**
 * Scorpio4 (c) 2010-2013
 * User: lee
 * Date: 22/01/13
 * Time: 7:01 PM
 * <p/>
 * This code does something useful
 */
public class MapUtil {

	public MapUtil() {
	}

	public static boolean getBoolean(Map model, String key, boolean _default) {
		Object value = model.get(key);
		if (value==null) return _default;
		return Boolean.parseBoolean(value.toString());
	}

	public static int getInt(Map model, String key, int _default) {
		Object value = model.get(key);
		if (value==null) return _default;
		return Integer.parseInt(value.toString());
	}

	public static long getLong(Map model, String key, long _default) {
		Object value = model.get(key);
		if (value==null) return _default;
		return Long.parseLong(value.toString());
	}

	public static Date getDate(Map model, String key, Date _default) {
		Object value = model.get(key);
		if (value==null) return _default;
		DateXSD xsd =  new DateXSD();
		return xsd.parse(value.toString());
	}

	public static String getString(Map model, String key, String _default) {
		Object value = model.get(key);
		if (value==null) return _default;
		return value.toString();
	}

	public static String getString(Map model, String key) {
		Object value = model.get(key);
		if (value==null) return null;
		return value.toString();
	}

	public static File getFile(Map model, String key, File _default) {
		Object value = model.get(key);
		if (value==null) return _default;
		return new File(value.toString());
	}

	public static List<String> getKeys(Map map) {
		Set<Object> keys = map.keySet();
		List list = new ArrayList();
		for (Object k : keys) list.add(k);
		return list;
	}

    public static Trie<String,Map> getTrieByKey(Collection <Map<String,Object>> things) {
        return getTrieByKey(things, "this");
    }

    public static Trie<String,Map> getTrieByKey(Collection <Map<String,Object>> things, String key) {
        Trie models = new PatriciaTrie<String,Object>(new CharSequenceKeyAnalyzer());
        for(Map thing: things) {
            models.put(thing.get(key), thing);
        }
        return models;
    }

    public static Map<String,Map> getMapByKey(Collection <Map<String,Object>> things) {
        return getMapByKey(things, "this");
    }

    public static Map<String,Map> getMapByKey(Collection <Map<String,Object>> things, String key) {
        HashMap models = new HashMap();
        for(Map thing: things) {
            if (thing.containsKey(key)) {
                models.put(thing.get(key), thing);
            }
        }
        return models;
    }

//    public static Map<String,Map> getConfig(Collection <Map<String,Object>> things) {
//        return getConfig(things, VOCAB.IQ+"configuration#", "name", "value" );
//    }

    public static Map<String,Map> getConfig(Collection <Map<String,Object>> things, String prefix, String name, String value) {
        HashMap config = new HashMap();
        for(Map thing: things) {
            if (thing.containsKey(name) && thing.containsKey(value)) {
                String id = (String) thing.get(name);
                if (prefix!=null && id.startsWith(prefix)) id = id.substring(prefix.length());
                config.put(id, thing.get(value));
            }
        }
        return config;
    }
}

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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.IllegalFormatException;

public class DateXSD {
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

    public DateXSD () {}

    public DateXSD (TimeZone timeZone)  {
        simpleDateFormat.setTimeZone(timeZone);
    }

    public DateXSD (String format, String timeZone)  {
		this.simpleDateFormat = new SimpleDateFormat(format);
        setTimeZone(timeZone);
    }

    public DateXSD (String format)  {
		this.simpleDateFormat = new SimpleDateFormat(format);
    }

    /**
    *  Parse a xml date string in the format produced by this class only.
    *  This method cannot parse all valid xml date string formats -
    *  so don't try to use it as part of a general xml parser
    */
    public synchronized Date parse(String xmlDateTime)  {
        if ( xmlDateTime.length() != 25 )  return null; // Date not in expected xml datetime format
		try {
	        StringBuilder sb = new StringBuilder(xmlDateTime);
	        sb.deleteCharAt(22);
	        return simpleDateFormat.parse(sb.toString());
		} catch(java.text.ParseException pe) {
			return null;
		}
    }

    public synchronized String format()  {
    	return format(new Date());
    }

    public static synchronized String today()  {
		DateXSD self = new DateXSD();
    	return self.format(new Date());
    }

    public synchronized String format(long now)  {
    	return format(new Date(now));
    }

    public synchronized String format(Date xmlDateTime)  {
    	if (xmlDateTime==null) return null;
    	try {
	        String s =  simpleDateFormat.format(xmlDateTime);
	        StringBuilder sb = new StringBuilder(s);
	        if (sb.length()>22) sb.insert(22, ':');
	        return sb.toString();
    	} catch(IllegalFormatException ife) {
    		return null;
    	}
    }

    public synchronized void setTimeZone(String timezone)  {
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(timezone));
    }
}

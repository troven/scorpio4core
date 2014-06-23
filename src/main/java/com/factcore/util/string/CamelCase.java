package com.factcore.util.string;
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
public class CamelCase  {

	public CamelCase()  {
	}
	
/**
	 * Converts the string with a camel case or with underscores and replace it 
	 * with spaces between each word, and underscores replaced by spaces.
	 * For example "javaProgramming" and "JAVA_PROGRAMMING"
	 * would both become Java Programming".
	 * @param str The String to convert
	 * @return The converted String
	 */

	public static String toTitleCase( String str ) {
		if( str == null || str.length() == 0 ) {
			return str;
		}
 
		StringBuffer result = new StringBuffer();
 
		/*
		 * Pretend space before first character
		 */
		char prevChar = ' ';
 
		/*
		 * Change underscore to space, insert space before capitals
		 */
		for( int i = 0; i < str.length(); i++ ) {
			char c = str.charAt( i );
			if( c == '_' ) {
				result.append( ' ' );
			}
			else if( prevChar == ' ' || prevChar == '_' ) {
				result.append( Character.toUpperCase( c ) );
			}
			else if( Character.isUpperCase( c ) && !Character.isUpperCase( prevChar ) ) {
				/*
				 * Insert space before start of word if camel case
				 */
				result.append( ' ' );
				result.append( Character.toUpperCase( c ) );
			}
			else {
				result.append( Character.toLowerCase(c) );
			}
 
			prevChar = c;
		}
 
		return result.toString();
	}

	public static void main(String[] args) {
		System.err.println("TEST #1 "+CamelCase.toTitleCase("hrDiskStorageMedia"));
	}
	
}


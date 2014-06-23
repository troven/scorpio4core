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
public class LevenshteinDistance	{

// Author: Chas Emerick  

	public LevenshteinDistance() {
	}

    // LC
	public static boolean alike(String s, String t) {
		return similar(s,t)>0.5;
	}

	public static double similar(String s, String t) {
		int max = Math.max(s.length(),t.length());
		double same = similarityNoCase(s,t);
		return 1.0 - (same/max);
	}

	public static int similarityNoCase(String s, String t) {
		return similarity(s.toLowerCase(),t.toLowerCase());
	}

	public static int similarity (String s, String t) {
		if (s == null || t == null) {
			throw new IllegalArgumentException("Strings must not be null");
		}

	/*
		The difference between this impl. and the previous is that, rather 
		than creating and retaining a matrix of size s.length()+1 by t.length()+1, 
		we maintain two single-dimensional arrays of length s.length()+1.	The first, d,
		is the 'current working' distance array that maintains the newest distance cost
		counts as we iterate through the characters of String s.	Each time we increment
		the index of String t we are comparing, d is copied to p, the second int[].	Doing so
		allows us to retain the previous cost counts as required by the algorithm (taking 
		the minimum of the cost count to the left, up one, and diagonally up and to the left
		of the current cost count being calculated).	(Note that the arrays aren't really 
		copied anymore, just switched...this is clearly much better than cloning an array 
		or doing a System.arraycopy() each time	through the outer loop.)

		Effectively, the difference between the two implementations is this one does not 
		cause an out of memory condition when calculating the LD over two very large strings.			
	*/
	
	int n = s.length(); // length of s
	int m = t.length(); // length of t
		
	if (n == 0) {
		return m;
	} else if (m == 0) {
		return n;
	}

	int p[] = new int[n+1]; //'previous' cost array, horizontally
	int d[] = new int[n+1]; // cost array, horizontally
	int _d[]; //placeholder to assist in swapping p and d

	// indexes into strings s and t
	int i; // iterates through s
	int j; // iterates through t

	char t_j; // jth character of t

	int cost; // cost

	for (i = 0; i<=n; i++) {
		p[i] = i;
	}
		
	for (j = 1; j<=m; j++) {
		t_j = t.charAt(j-1);
		d[0] = j;

		for (i=1; i<=n; i++) {
				cost = s.charAt(i-1)==t_j ? 0 : 1;
				// minimum of cell to the left+1, to the top+1, diagonally left and up +cost				
				d[i] = Math.min(Math.min(d[i-1]+1, p[i]+1),	p[i-1]+cost);	
		}

		// copy current distance counts to 'previous row' distance counts
		_d = p;
		p = d;
		d = _d;
	} 

	// our last action in the above loop was to switch d and p, so p now 
	// actually has the most recent cost counts
	return p[n];
}
	public static void main(String[] arg) {
		LevenshteinDistance ld = new LevenshteinDistance();
		long now = System.currentTimeMillis();
//		System.err.println("TIME:"+(System.currentTimeMillis()-now));
		System.err.println(">"+ld.alike("INDAGO RESOURCES LTD","Indago Resources / Western Metals"));
		System.err.println(">"+ld.similar("INDAGO RESOURCES LTD","Indago Resources / Western Metals"));
		System.err.println(">"+ld.alike("Westfield Holdings","Westfield Trust"));
	}
	
}

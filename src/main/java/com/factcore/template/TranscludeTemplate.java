package com.factcore.template;
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
import com.factcore.oops.ConfigException;
import com.factcore.oops.FactException;
import ognl.OgnlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Fact:Core (c) 2010-2013
 * User: lee
 * Date: 18/02/13
 * Time: 9:07 AM
 * <p/>
 * This code does something useful
 */
public class TranscludeTemplate extends PicoTemplate {
    private static final Logger log = LoggerFactory.getLogger(TranscludeTemplate.class);

    Transcluder transcluder = null;

	public TranscludeTemplate(String template, Transcluder transcluder) throws FactException, ConfigException {
        setTranscluder(transcluder);
		try {
			setTokens("{{", "}}");
			parse(template);
		} catch (OgnlException e) {
			throw new FactException("urn:factcore:output:template:oops:invalid#"+e.getMessage(), e);
		}
	}

    protected void acceptNode(String $node) {
        if ($node.startsWith("@")) {
            if (transcluder!=null) {
                Object $page = transcluder.transclude($node.substring(1));
                if ($page!=null) nodes.add($page);
                else nodes.add(getStartToken()+"missing-"+$node+getEndToken());
            }
            else nodes.add($node);
        } else {
            super.acceptNode($node);
        }
    }
    public Transcluder getTranscluder() {
        return transcluder;
    }

    public void setTranscluder(Transcluder transcluder) {
        this.transcluder = transcluder;
    }

}


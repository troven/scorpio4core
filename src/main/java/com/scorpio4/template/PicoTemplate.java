package com.scorpio4.template;
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
import com.scorpio4.oops.ConfigException;
import ognl.Ognl;
import ognl.OgnlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Scorpio4 (c) 2010-2013
 * User: lee
 *
 * 'PicoTemplate' populates a template string with instance values from a model Map
 *
 */
public class PicoTemplate {
	private static final Logger log = LoggerFactory.getLogger(PicoTemplate.class);
	protected List nodes = new ArrayList();
	private String startToken = "{{";
	private String endToken = "}}";
	private boolean fatalResolution = false;
	private String originalTemplate = null;

	protected PicoTemplate() {
	}

	public PicoTemplate(String template, String startToken, String endToken) throws ConfigException{
		try {
			setTokens(startToken, endToken);
			parse(template);
		} catch (OgnlException e) {
			throw new ConfigException("Template is invalid", e);
		}
	}

	public PicoTemplate(String template) throws ConfigException {
		this(template, false);
	}

	public PicoTemplate(String template, boolean fatalResolution) throws ConfigException{
		try {
			setFatalResolution(fatalResolution);
			parse(template);
		} catch (OgnlException e) {
			throw new ConfigException("Template is invalid", e);
		}
	}

	public void parse(String template) throws OgnlException, ConfigException {
        if (template==null||template.equals("")) return;
		this.originalTemplate = template;
		int lx = 0, ix = 0;
		ix = template.indexOf(getStartToken());
		while (ix>=0) {
			nodes.add(template.substring(lx,ix));
			lx = template.indexOf(getEndToken(),ix);
			if (lx>0) {
                String $node = template.substring(ix+getStartToken().length(), lx);
                acceptNode($node);
				lx = lx+getEndToken().length();
			} else throw new ConfigException("missing "+ getEndToken() +" at "+ix);
			ix = template.indexOf(getStartToken(),lx);
		}
		if (lx<template.length()) {
			nodes.add(template.substring(lx));
		}
	}

    protected void acceptNode(String $node) {
        try {
            nodes.add(Ognl.parseExpression($node));
        } catch (OgnlException e) {
            log.debug("urn:scorpio4:output:template:pico:oops:invalid-expression#"+$node,e);
        }
    }

    public String translate(Object model) throws ConfigException {
		try {
			StringBuilder $string = new StringBuilder();
			for(int i=0;i<nodes.size();i++) {
                Object node = nodes.get(i);
                if (node==null) {
                    log.error("NULL Node:"+$string);
                } else if (node instanceof String)
					$string.append(nodes.get(i));
				else {
					Object value = Ognl.getValue(node, model);
					if (value==null) {
						if (isFatalResolution()) throw new ConfigException("Unresolved attribute: "+node+" from: "+model);
						$string.append(startToken).append("unknown-").append(node).append(endToken);
					} else {
log.debug("Node:"+node.getClass()+" -->"+value.toString());
						$string.append(value.toString());
					}
				}
			}
			return $string.toString();
		} catch(OgnlException e) {
			throw new ConfigException("Evaluation failed", e);
		}
	}

    public void setTokens(String startToken, String endToken) {
		setStartToken(startToken);
		setEndToken(endToken);
	}


	public String getStartToken() {
		return startToken;
	}

	public void setStartToken(String startToken) {
		this.startToken = startToken;
	}

	public String getEndToken() {
		return endToken;
	}

	public void setEndToken(String endToken) {
		this.endToken = endToken;
	}

	public boolean isFatalResolution() {
		return fatalResolution;
	}

	public String getOriginalTemplate() {
		return originalTemplate;
	}

	public void setFatalResolution(boolean fatalResolution) {
		this.fatalResolution = fatalResolution;
	}
}

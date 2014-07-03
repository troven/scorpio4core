package com.scorpio4.vendor.sesame.fn;
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
import net.sf.classifier4J.summariser.SimpleSummariser;
import org.openrdf.model.Literal;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.algebra.evaluation.ValueExprEvaluationException;

/**
 * scorpio4 (c) 2013
 * Module: com.scorpio4.vendor.sesame.fn
 * User  : lee
 * Date  : 29/10/2013
 * Time  : 7:22 PM
 */
public class Summarize extends CustomFunction {

    public Summarize() {
    }

    @Override
    public String getFunctionName() {
        return "summarize";
    }

    @Override
    public Value evaluate(ValueFactory valueFactory, Value... args) throws ValueExprEvaluationException {
        if (args.length<1) throw new ValueExprEvaluationException("Missing term to summarize");
        if (args.length>2) throw new ValueExprEvaluationException("Too many terms");
        SimpleSummariser summariser = new SimpleSummariser();
        int lines = args.length>1?((Literal)args[1]).intValue():1;
        if (lines<1||lines>32) throw new ValueExprEvaluationException("Number of lines must be between 1 and 32");
        String summary = summariser.summarise(args[0].stringValue(),lines);
        return valueFactory.createLiteral(summary);
    }
}

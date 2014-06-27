package com.factcore.vendor.sesame.fn;

import com.factcore.oops.ConfigException;
import com.factcore.template.PicoTemplate;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.algebra.evaluation.ValueExprEvaluationException;

import java.util.HashMap;
import java.util.Map;

/**
 * Scorpio (c) 2014
 * Module: com.factcore.vendor.sesame.fn
 * User  : lee
 * Date  : 27/06/2014
 * Time  : 12:43 PM
 */
public class Templater extends CustomFunction {

	public Templater() {

	}

	@Override
	public String getFunctionName() {
		return "Templater";
	}

	@Override
	public Value evaluate(ValueFactory valueFactory, Value... args) throws ValueExprEvaluationException {
		if (args.length<2) throw new ValueExprEvaluationException("insufficient arguments: "+args);
		try {
			PicoTemplate picoTemplate = new PicoTemplate(args[0].stringValue());
			Map map = new HashMap();
			for(int i=1;i<args.length;i++) {
				map.put(i-1, args[i]);
			}
			String translated = picoTemplate.translate(map);
			return valueFactory.createURI(translated);
		} catch (ConfigException e) {
			throw new ValueExprEvaluationException("insufficient arguments: "+e.getMessage(),e);
		}
	}
}

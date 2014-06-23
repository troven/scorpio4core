package com.factcore.vendor.sesame.fn;
 
import com.factcore.vocab.COMMON;
import org.openrdf.query.algebra.evaluation.function.Function;
import org.openrdf.query.algebra.evaluation.ValueExprEvaluationException;
import org.openrdf.model.*;
 
/**
 * a custom SPARQL function that determines whether an input literal string is a palindrome.
 * see: http://rivuli-development.com/further-reading/sesame-cookbook/creating-custom-sparql-functions/
 *
 * To build the custom function, ensure that
 * ./META-INF/services/org.openrdf.query.algebra.evaluation.function.Function is configured correctly
 *
 * To use the custom function, copy the JAR file into the classpath.
 *
 */
public abstract class CustomFunction implements Function {

  /**
   * return the URI 'http://example.org/custom-function/xyz' as a String
   */
  public String getURI() {
      return COMMON.FN + getFunctionName();
  }

	public abstract String getFunctionName();

	/**
   * Executes the custom function.
   */
  public abstract Value evaluate(ValueFactory valueFactory, Value... args) throws ValueExprEvaluationException;

}
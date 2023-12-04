package io.keikai.devref.advanced;


import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.functions.MultiOperandNumericFunction;

/**
 * Implement a numeric function that accepts multiple operations
 * @author Hawk
 *
 */
public class MySubtotal extends MultiOperandNumericFunction {

	protected MySubtotal() {
		super(false,false, false);
	}
	
	/**
	 * inherited method, ValueEval evaluate(ValueEval[] args, int srcCellRow,
	 * int srcCellCol), will call this overridden method This function depends
	 * on MultiOperandNumericFunction that evaluates all arguments to double.
	 * This function sums all double values in cells.
	 */
	@Override
	protected double evaluate(double[] values) throws EvaluationException {
		double sum = 0;
		for (int i = 0 ; i < values.length ; i++){
			sum += values[i];
		}
		return sum;
	}
}

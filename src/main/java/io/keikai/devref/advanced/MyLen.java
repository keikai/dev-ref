package io.keikai.devref.advanced;


import io.keikai.range.formula.*;

public class MyLen extends TextFunction {

	@Override
	protected ValueEval evaluateFunc(ValueEval[] args, int srcCellRow, int srcCellCol)
			throws EvaluationException {
		if (args[0] instanceof StringEval){
			String text = ((StringEval)args[0]).getStringValue();
			if (text.length() == 1){
				return new StringEval(text.length()+" character");
			}else{
				return new StringEval(text.length()+" characters");
			}
		}
		return ErrorEval.VALUE_INVALID;
	}

}

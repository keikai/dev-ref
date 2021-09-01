package io.keikai.devref.advanced;

import org.zkoss.poi.ss.formula.OperationEvaluationContext;
import org.zkoss.poi.ss.formula.eval.*;
import org.zkoss.poi.ss.formula.functions.FreeRefFunction;

/**
 * This is an example to show a custom function that can access sheets and cells.</br>
 * An implementation for function <a href="https://support.microsoft.com/en-us/office/cell-function-51bd39a5-f338-4dbe-a33f-955d67c2b2cf">CELL()</a>.
 * Only handle the 1st parameter, info_type, and only support "content" type.
 * Doesn't handle the 2nd optional parameter. <br/>
 * It requires to register this function to make it work in keikai, see {@link FunctionRegistration}.
 */
public class MyCell implements FreeRefFunction {
    @Override
    public ValueEval evaluate(ValueEval[] valueEvals, OperationEvaluationContext context) {
        if (valueEvals.length < 1) {
            throw new IllegalArgumentException("at least 1 parameter required");
        }

        if (!validateArguments(valueEvals)) {
            return ErrorEval.VALUE_INVALID;
        }
        ValueEval evalCell = context.getRefEvaluatorForCurrentSheet()
                .getEvalForCell(context.getRowIndex(), context.getColumnIndex());
        if (evalCell instanceof StringEval) {
            return new StringEval("l");
        } else if (evalCell instanceof BlankEval) {
            return new StringEval("b");
        } else {
            return new StringEval("v");
        }
    }

    private boolean validateArguments(ValueEval[] valueEvals) {
        if (!(valueEvals[0] instanceof StringEval)) {
            return false;
        }
        String infoType = ((StringEval) valueEvals[0]).getStringValue();
        if (!"type".equals(infoType)) {
            return false;
        }

        return true;
    }
}

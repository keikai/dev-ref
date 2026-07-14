package io.keikai.devref.advanced;


import io.keikai.range.formula.*;

/**
 * This is an example to show a custom function that can access the passed-in cell values.</br>
 * An implementation for function <a href="https://support.microsoft.com/en-us/office/cell-function-51bd39a5-f338-4dbe-a33f-955d67c2b2cf">CELL()</a>.
 * Only handle the 1st parameter, info_type, and only support "type" info type.
 * The 2nd parameter is the reference to check. <br/>
 * It requires to register this function to make it work in keikai, see {@link FunctionRegistration}.
 * <p>
 * Since Keikai 7.0, {@code FreeRefFunction} moved to {@code io.keikai.range.formula} and its
 * evaluation context parameter became an opaque {@code Object}; the POI
 * {@code OperationEvaluationContext} is no longer available. Hence this example determines the
 * cell type from the referenced argument instead of the current-sheet evaluator.
 */
public class MyCell implements FreeRefFunction {
    @Override
    public ValueEval evaluate(ValueEval[] valueEvals, Object context) {
        if (valueEvals.length < 1) {
            throw new IllegalArgumentException("at least 1 parameter required");
        }

        if (!validateArguments(valueEvals)) {
            return ErrorEval.VALUE_INVALID;
        }
        ValueEval evalCell = valueEvals.length > 1 ? valueEvals[1] : BlankEval.INSTANCE;
        if (evalCell instanceof RefEval) {
            evalCell = ((RefEval) evalCell).getInnerValueEval(((RefEval) evalCell).getFirstSheetIndex());
        }
        if (evalCell instanceof StringEval) {
            return new StringEval("l");
        } else if (evalCell instanceof BlankEval) {
            return new StringEval("b");
        } else {
            return new StringEval("v");
        }
    }

    private boolean validateArguments(ValueEval[] valueEvals) {
        ValueEval infoTypeEval = valueEvals[0];
        if (infoTypeEval instanceof RefEval) {
            infoTypeEval = ((RefEval) infoTypeEval).getInnerValueEval(((RefEval) infoTypeEval).getFirstSheetIndex());
        }
        if (!(infoTypeEval instanceof StringEval)) {
            return false;
        }
        String infoType = ((StringEval) infoTypeEval).getStringValue();
        if (!"type".equals(infoType)) {
            return false;
        }

        return true;
    }
}

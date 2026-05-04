package spreadsheet.Model.Expression;

import java.util.ArrayList;

/**
 * Leaf node of the expression tree.
 * Represents a constant double value (e.g. "5", "3.14").
 * Composite pattern: leaf — has no operands.
 */
public class ConstantExpression implements Expression {
    private final double value;

    public ConstantExpression(double value) {
        this.value = value;
    }

    @Override
    public double evaluate() {
        return value;
    }

    @Override
    public void addOperand(Expression... expression) {
        throw new UnsupportedOperationException("Constant expressions have no operands");
    }

    @Override
    public ArrayList<Expression> getOperands() {
        return new ArrayList<>();
    }
}

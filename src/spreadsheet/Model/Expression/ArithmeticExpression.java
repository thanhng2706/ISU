package spreadsheet.Model.Expression;

import java.util.ArrayList;
import java.util.function.BinaryOperator;

/**
 * Composite node of the expression tree.
 * Represents a binary arithmetic operation (+, -, *, /).
 * Composite pattern: composite — holds exactly 2 operand Expressions.
 *
 * Uses a BinaryOperator<Double> so adding new operators later
 * requires no modification to this class (Open/Closed Principle).
 */
public class ArithmeticExpression implements Expression {
    private final BinaryOperator<Double> operation;
    private final ArrayList<Expression> operands = new ArrayList<>();

    public ArithmeticExpression(String operator) {
        this.operation = switch (operator) {
            case "+" -> Double::sum;
            case "-" -> (a, b) -> a - b;
            case "*" -> (a, b) -> a * b;
            case "/" -> (a, b) -> a / b;
            default -> throw new IllegalArgumentException("Unknown operator: " + operator);
        };
    }

    @Override
    public double evaluate() {
        double left  = operands.get(0).evaluate();
        double right = operands.get(1).evaluate();
        return operation.apply(left, right);
    }

    @Override
    public void addOperand(Expression... expressions) {
        for (Expression e : expressions) {
            operands.add(e);
        }
    }

    @Override
    public ArrayList<Expression> getOperands() {
        return operands;
    }
}

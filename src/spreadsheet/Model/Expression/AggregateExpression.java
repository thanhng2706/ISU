package spreadsheet.Model.Expression;

import spreadsheet.Model.Expression.Strategy.AggregateStrategy;
import java.util.ArrayList;
import java.util.List;

/**
 * Composite node – represents an aggregate function (SUM, COUNT, AVE, MIN, MAX).
 *
 * Strategy pattern: the evaluation algorithm is injected via AggregateStrategy,
 * so new aggregate operators can be added without modifying this class.
 *
 * getAllValues() on each operand handles cell ranges correctly
 * (e.g. AVE(A1:A3, 10) expands to 4 values, not 2).
 */
public class AggregateExpression implements Expression {
    private final AggregateStrategy strategy;
    private final ArrayList<Expression> operands = new ArrayList<>();

    public AggregateExpression(AggregateStrategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public double evaluate() {
        List<Double> allValues = new ArrayList<>();
        for (Expression operand : operands) {
            allValues.addAll(operand.getAllValues());
        }
        return strategy.apply(allValues);
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

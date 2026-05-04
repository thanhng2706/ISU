package spreadsheet.Model.Expression.Strategy;

import java.util.List;

/**
 * Strategy pattern – defines the algorithm for an aggregate operation.
 * Adding a new aggregate operator only requires a new Strategy class;
 * no changes to AggregateExpression are needed (Open/Closed Principle).
 */
public interface AggregateStrategy {
    double apply(List<Double> values);
}

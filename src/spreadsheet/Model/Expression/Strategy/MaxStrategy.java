package spreadsheet.Model.Expression.Strategy;

import java.util.List;

public class MaxStrategy implements AggregateStrategy {
    @Override
    public double apply(List<Double> values) {
        return values.stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
    }
}

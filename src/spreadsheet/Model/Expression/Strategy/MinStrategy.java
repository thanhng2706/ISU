package spreadsheet.Model.Expression.Strategy;

import java.util.List;

public class MinStrategy implements AggregateStrategy {
    @Override
    public double apply(List<Double> values) {
        return values.stream().mapToDouble(Double::doubleValue).min().orElse(0.0);
    }
}

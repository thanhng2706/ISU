package spreadsheet.Model.Expression.Strategy;

import java.util.List;

public class AveStrategy implements AggregateStrategy {
    @Override
    public double apply(List<Double> values) {
        return values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }
}

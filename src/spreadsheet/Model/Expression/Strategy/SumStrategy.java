package spreadsheet.Model.Expression.Strategy;

import java.util.List;

public class SumStrategy implements AggregateStrategy {
    @Override
    public double apply(List<Double> values) {
        return values.stream().mapToDouble(Double::doubleValue).sum();
    }
}

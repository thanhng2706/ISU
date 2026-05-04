package spreadsheet.Model.Expression.Strategy;

import java.util.List;

public class CountStrategy implements AggregateStrategy {
    @Override
    public double apply(List<Double> values) {
        return values.size();
    }
}

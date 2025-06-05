import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import spreadsheet.Controller.ExpressionParser;
import spreadsheet.Model.Cell.CellComponent;
import spreadsheet.Model.CellRepository;
import spreadsheet.Model.Expression.Expression;


public class ParseandEvaluateTest {

    @Test
    public void testExpressionParser_constant(){
        String raw = "11";
        Expression expression = ExpressionParser.convertExpression(raw);

        double value = expression.evaluate();
        assertEquals(value, 11);
    }

    @Test
    public void testExpressionParser_constantAsExpr(){
        String raw = "=11";
        Expression expression = ExpressionParser.convertExpression(raw);

        double value = expression.evaluate();
        assertEquals(value, 11);
    }

    @Test
    public void testExpressionParser_SimpleArithmetic1(){
        String raw = "=5+3*2";
        Expression expression = ExpressionParser.convertExpression(raw);

        double value = expression.evaluate();
        assertEquals(value, 11);
    }

    @Test
    public void testExpressionParser_SimpleArithmetic2(){
        String raw = "=(1+9)+4*2";
        Expression expression = ExpressionParser.convertExpression(raw);

        double value = expression.evaluate();
        assertEquals(value, 18);
    }

    @Test
    public void testExpressionParser_SimpleArithmetic3(){
        String raw = "=(1+2+3+4+5)*2-30/2*4";
        Expression expression = ExpressionParser.convertExpression(raw);

        double value = expression.evaluate();
        assertEquals(value, -30.0);
    }


    @Test
    public void testExpressionParser_SimpleArithmetic4(){
        String raw = "=2+10*3+10/4";
        Expression expression = ExpressionParser.convertExpression(raw);

        double value = expression.evaluate();
        assertEquals(value, 34.5);
    }

    @Test
    public void testExpressionParser_SimpleArithmetic5(){
        String raw = "=5+4*(2+100/(10-2))";
        Expression expression = ExpressionParser.convertExpression(raw);

        double value = expression.evaluate();
        assertEquals(value, 63.0);
    }

}

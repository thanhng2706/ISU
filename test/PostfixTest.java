import org.junit.jupiter.api.Test;
import spreadsheet.Controller.ExpressionParser;
import spreadsheet.Model.Cell.CellComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class PostfixTest {

    @Test
    public void testTokenize1(){
        String raw = "=AVE(A1:A2, 10+B1, B2-B2/10)+10*6";
        ArrayList<String> tokens = ExpressionParser.tokenize(raw);
        ArrayList<String>  expected = new ArrayList<>(Arrays.asList("AVE", "(", "A1:A2", ",", "10", "+", "B1", ",", "B2", "-", "B2", "/", "10", ")", "+", "10", "*", "6"));
        System.out.println(tokens);
        assertEquals(tokens, expected );
    }

    @Test
    public void testTokenize2(){
        String raw = "=AVE(A1:A2, SUM(A1:B1), 20*(55-10*5))";
        ArrayList<String> tokens = ExpressionParser.tokenize(raw);
        ArrayList<String>  expected = new ArrayList<>(Arrays.asList("AVE", "(", "A1:A2", ",", "SUM", "(", "A1:B1", ")",",", "20", "*", "(", "55", "-", "10", "*", "5", ")", ")"));
        System.out.println(tokens);
        assertEquals(tokens, expected );
    }

    @Test
    public void testTokenize3(){
        String raw = "9";
        ArrayList<String> tokens = ExpressionParser.tokenize(raw);
        ArrayList<String>  expected = new ArrayList<>(Arrays.asList("9"));
        System.out.println(tokens);
        assertEquals(tokens, expected );
    }

    @Test
    public void testinfixToPostfix1(){
        String raw = "=10+ 7 - 5* 2 +14 / 2";
        Queue<String> postfix = ExpressionParser.infixToPostfix(ExpressionParser.tokenize(raw));
        ArrayList<String>  expected = new ArrayList<>(Arrays.asList("10", "7", "+", "5", "2", "*", "-", "14", "2", "/", "+"));
        System.out.println(postfix);
        assertEquals(postfix, expected );
    }

    @Test
    public void testinfixToPostfix2(){
        String raw = "=AVE(A1:A2, 10+B1, B2-B2/10)+10*6";
        Queue<String> postfix = ExpressionParser.infixToPostfix(ExpressionParser.tokenize(raw));
        ArrayList<String>  expected = new ArrayList<>(Arrays.asList("A1:A2", "10", "B1", "+", "B2", "B2", "10", "/", "-",  "AVE", "3", "10", "6", "*", "+"));
        System.out.println(postfix);
        assertEquals(postfix, expected );
    }

}

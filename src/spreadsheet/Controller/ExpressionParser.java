package spreadsheet.Controller;

/*
 * Controller-layer facade over the expression parsing pipeline.
 * Tests import this class; GridViewModel also uses this.
 *
 * Pipeline:
 *   Step 1 – tokenize()            : string  → ArrayList<String> (infix tokens)
 *   Step 2 – infixToPostfix()      : infix   → Queue<String>     (postfix tokens)
 *   Step 3 – postfixToExpression() : postfix → Expression tree
 */

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import spreadsheet.Model.Cell.CellComponent;
import spreadsheet.Model.Cell.CellGroup;
import spreadsheet.Model.CellRepository;
import spreadsheet.Model.Expression.AggregateExpression;
import spreadsheet.Model.Expression.ArithmeticExpression;
import spreadsheet.Model.Expression.CellGroupExpression;
import spreadsheet.Model.Expression.CellReferenceExpression;
import spreadsheet.Model.Expression.ConstantExpression;
import spreadsheet.Model.Expression.Expression;
import spreadsheet.Model.Expression.Strategy.AggregateStrategy;
import spreadsheet.Model.Expression.Strategy.AveStrategy;
import spreadsheet.Model.Expression.Strategy.CountStrategy;
import spreadsheet.Model.Expression.Strategy.MaxStrategy;
import spreadsheet.Model.Expression.Strategy.MinStrategy;
import spreadsheet.Model.Expression.Strategy.SumStrategy;

public class ExpressionParser {

    /**
     * Facade entry point.
     * Converts a raw formula string (e.g. "=SUM(A1:B3)") into an Expression tree.
     */
    public static Expression convertExpression(String rawExpression) {
        if (rawExpression.startsWith("=")) {
            rawExpression = rawExpression.substring(1);
        }
        ArrayList<String> tokens  = tokenize(rawExpression);
        Queue<String>     postfix = infixToPostfix(tokens);
        return postfixToExpression(postfix);
    }

    // =========================================================
    //  STEP 1 – TOKENIZE
    // =========================================================

    public static ArrayList<String> tokenize(String input) {
        if (input.startsWith("=")) {
            input = input.substring(1);
        }
        ArrayList<String> tokens = new ArrayList<>();
        Matcher matcher = Pattern.compile(buildRegex()).matcher(input);
        while (matcher.find()) {
            tokens.add(matcher.group());
        }
        return tokens;
    }

    private static String buildRegex() {
        return
            // Cell ranges (e.g. A1:B3) — must come before single cell refs
            "[A-Za-z]+\\d+:[A-Za-z]+\\d+"
            // Single cell refs (e.g. A1, AA10)
            + "|[A-Za-z]+\\d+"
            // Function keywords — do NOT include the '('
            + "|(?i)(SUM|AVE|COUNT|MIN|MAX)(?=\\()"
            // Numbers (int or decimal)
            + "|\\d+(\\.\\d+)?"
            // Single-char operators and punctuation
            + "|[()+\\-*/,]";
    }

    // =========================================================
    //  STEP 2 – INFIX → POSTFIX  (Shunting-Yard)
    // =========================================================

    public static Queue<String> infixToPostfix(ArrayList<String> tokens) {
        Deque<String>  operatorDeque = new ArrayDeque<>();
        Deque<Integer> argumentDeque = new ArrayDeque<>();
        Queue<String>  outputQueue   = new LinkedList<>();

        for (String token : tokens) {
            if (isConstant(token) || isCellSymbol(token)) {
                outputQueue.offer(token);

            } else if (isAggregateSymbol(token)) {
                operatorDeque.push(token);
                argumentDeque.push(1);

            } else if (isArithmeticOperator(token)) {
                while (!operatorDeque.isEmpty()
                        && precedence(operatorDeque.peek()) >= precedence(token)) {
                    outputQueue.offer(operatorDeque.pop());
                }
                operatorDeque.push(token);

            } else if (token.equals("(")) {
                operatorDeque.push(token);

            } else if (token.equals(")")) {
                while (!operatorDeque.isEmpty() && !operatorDeque.peek().equals("(")) {
                    outputQueue.offer(operatorDeque.pop());
                }
                if (!operatorDeque.isEmpty()) operatorDeque.pop(); // pop '('
                if (!operatorDeque.isEmpty() && isAggregateSymbol(operatorDeque.peek())) {
                    int argCount = argumentDeque.pop();
                    outputQueue.offer(operatorDeque.pop());
                    outputQueue.offer(String.valueOf(argCount));
                }

            } else if (token.equals(",")) {
                while (!operatorDeque.isEmpty() && !operatorDeque.peek().equals("(")) {
                    outputQueue.offer(operatorDeque.pop());
                }
                argumentDeque.push(argumentDeque.pop() + 1);
            }
        }

        while (!operatorDeque.isEmpty()) {
            outputQueue.offer(operatorDeque.pop());
        }

        return outputQueue;
    }

    // =========================================================
    //  STEP 3 – POSTFIX → EXPRESSION TREE
    // =========================================================

    private static Expression postfixToExpression(Queue<String> outputQueue) {
        Deque<Expression> stack = new ArrayDeque<>();

        while (!outputQueue.isEmpty()) {
            String token = outputQueue.poll();

            if (isConstant(token)) {
                // --- Constant ---
                stack.push(new ConstantExpression(Double.parseDouble(token)));

            } else if (isCellRangeSymbol(token)) {
                // --- Cell range (e.g. "A1:B3") ---
                String[] parts = token.split(":");
                int[] start = parseCellFormat(parts[0]);
                int[] end   = parseCellFormat(parts[1]);
                CellGroup group = new CellGroup();
                for (int col = start[0]; col <= end[0]; col++) {
                    for (int row = start[1]; row <= end[1]; row++) {
                        group.addCell(CellRepository.getInstance().getReferenceCellComponent(row, col));
                    }
                }
                stack.push(new CellGroupExpression(group));

            } else if (isSingleCellSymbol(token)) {
                // --- Single cell ref ---
                int[] coords = parseCellFormat(token);
                CellComponent cell = CellRepository.getInstance().getReferenceCellComponent(coords[1], coords[0]);
                stack.push(new CellReferenceExpression(cell));

            } else if (isArithmeticOperator(token)) {
                // --- Arithmetic (+, -, *, /) ---
                ArithmeticExpression expr = new ArithmeticExpression(token);
                ArrayList<Expression> args = new ArrayList<>();
                args.add(stack.pop()); // right
                args.add(stack.pop()); // left
                Collections.reverse(args);
                expr.addOperand(args.toArray(new Expression[0]));
                stack.push(expr);

            } else if (isAggregateSymbol(token)) {
                // --- Aggregate (SUM, COUNT, AVE, MIN, MAX) ---
                int argCount = Integer.parseInt(outputQueue.poll());
                AggregateExpression expr = new AggregateExpression(getStrategy(token));
                ArrayList<Expression> args = new ArrayList<>();
                for (int i = 0; i < argCount; i++) args.add(stack.pop());
                Collections.reverse(args);
                expr.addOperand(args.toArray(new Expression[0]));
                stack.push(expr);
            }
        }

        return stack.isEmpty() ? null : stack.pop();
    }

    // =========================================================
    //  HELPERS
    // =========================================================

    private static boolean isConstant(String token) {
        return token.matches("\\d+(\\.\\d+)?");
    }

    private static boolean isCellSymbol(String token) {
        return token.matches("([A-Za-z]+\\d+)(:[A-Za-z]+\\d+)?");
    }

    private static boolean isSingleCellSymbol(String token) {
        return token.matches("[A-Za-z]+\\d+");
    }

    private static boolean isCellRangeSymbol(String token) {
        return token.matches("[A-Za-z]+\\d+:[A-Za-z]+\\d+");
    }

    private static boolean isArithmeticOperator(String token) {
        return token.length() == 1 && "+-*/".contains(token);
    }

    private static boolean isAggregateSymbol(String token) {
        return switch (token.toUpperCase()) {
            case "SUM", "AVE", "COUNT", "MIN", "MAX" -> true;
            default -> false;
        };
    }

    private static int precedence(String operator) {
        return switch (operator.toUpperCase()) {
            case "+", "-"                                  -> 2;
            case "*", "/"                                  -> 3;
            case "SUM", "AVE", "COUNT", "MIN", "MAX"       -> 4;
            default                                        -> 0;
        };
    }

    private static AggregateStrategy getStrategy(String type) {
        return switch (type.toUpperCase()) {
            case "SUM"   -> new SumStrategy();
            case "AVE"   -> new AveStrategy();
            case "COUNT" -> new CountStrategy();
            case "MIN"   -> new MinStrategy();
            case "MAX"   -> new MaxStrategy();
            default -> throw new IllegalArgumentException("Unknown aggregate: " + type);
        };
    }

    public static int[] parseCellFormat(String coord) {
        int[] result = new int[2];
        Matcher m = Pattern.compile("([a-zA-Z]+)(\\d+)").matcher(coord);
        if (m.matches()) {
            result[0] = columnLettersToIndex(m.group(1));
            result[1] = Integer.parseInt(m.group(2)) - 1;
        }
        return result;
    }

    private static int columnLettersToIndex(String letters) {
        int col = 0;
        for (char c : letters.toUpperCase().toCharArray()) {
            col = col * 26 + (c - 'A');
        }
        return col;
    }
}

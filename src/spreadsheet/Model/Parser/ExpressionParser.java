/*
 * Copyright (c) 2025 Sakire Arslan Ay and Quan Dinh
 *
 * This file was developed for educational purposes as part of CS4233:
 * Object-Oriented Analysis & Design at Worcester Polytechnic Institute.
 *
 * The project is based on the “Spreadsheet” application developed by Quan Dinh.
 *
 * All rights reserved. Redistribution and modification outside the scope
 * of this course are not permitted without prior written permission.
 */

package spreadsheet.Model.Parser;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import spreadsheet.Model.Expression.Expression;

/**
 * ExpressionParser is responsible for converting a string literal to a computable "Expression"
 * ExpressionParser uses the "Shunting yard" algorithm. https://en.wikipedia.org/wiki/Shunting_yard_algorithm
 */
public class ExpressionParser {

    /**
     * The singular public method that is used to convert (Facade pattern)
     * 
     * How to use:
     *      ExpressionParser.convertExpression(rawExpression)
     * 
     * @param rawExpression is the string literal of the expression - EX: "=A1+A2"
     * @return the Expression object
     */
    public static Expression convertExpression(String rawExpression){
        //Remove the = symbol at the start if there is one
        if(rawExpression.startsWith("=")){
            rawExpression = rawExpression.substring(1);
        }

        /**
         * Passing the string literal to 3 major steps
         *  - **Step 1**: Tokenize the string expression to an array of tokens (i.e "A1", "1", "+", "SUM" are tokens).
         *  - **Step 2**: Convert the array of tokens from infix notation to postfix notation.
         *  - **Step 3**: Convert the array of tokens from postfix notation into an expression tree.
         * 
         * Result of each step is used for the next step
         */
        ArrayList<String> tokens = tokenize(rawExpression);
        Queue<String> outputQueue = infixToPostfix(tokens);
        Expression expression = postfixToExpression(outputQueue);

        return expression;
    }

    /**
     * ------------------------------------------------
     *      STEP 1: TOKENIZE TO INFIX FORMAT
     * ------------------------------------------------
     */

     /**
      * Tokenize the string literal into a list of operators, constants, and cell references
      * The tokens are sorted in infix order
      * 
      *     Infix notation:   (3 + A1) * 20   < Humans like to evaluate this
      *     Postfix notation: 3 A1 + 20 *     < Computers like to evaluate this
      *
      * @param input is the string literal of the expression
      * @return an ArrayList of tokens
      */
    private static ArrayList<String> tokenize(String input){
        ArrayList<String> tokens = new ArrayList<>();
        String regex = regexBuilder();  //calls to the regexBuilder (helper function)

        //Compile the regex pattern and match it against the input to get the Matcher object (to extract the tokens)
        Matcher matcher = Pattern.compile(regex).matcher(input);

        //Search for the next sequence that matches the regex
        while(matcher.find()){
            //Add the most recent match to the list
            tokens.add(matcher.group());
        }

        return tokens;
    }

    //============== STEP 1 HELPER ===================

    /**
     * regexBuilder just helps modularize the regex patterns that are being used to extract tokens 
     * (see each part below to understand what type of tokens are being extracted)
     * It can be simplified to a singular string declaration, but harder to read.
     *
     * @return the string regex
     */
    private static String regexBuilder(){
        StringBuilder regexBuilder = new StringBuilder();

        // Part for cell ranges (e.g., A2:B6)
        regexBuilder.append("[A-Za-z]+\\d+:");
        regexBuilder.append("[A-Za-z]+\\d+");

        // Part for single cell references (e.g., A1, B10, Z999)
        regexBuilder.append("|[A-Za-z]+\\d+");

        // Part for function names (e.g., SUM, AVG, etc.) followed by a (, but does not includes the ( in the final matcher
        regexBuilder.append("|(?i)(SUM|AVE|COUNT)(?=\\()");

        // Part for numbers (integers and decimals)
        regexBuilder.append("|\\d+(\\.\\d+)?");

        // Part for operators and punctuation (+, -, *, /, (, ), and comma)
        regexBuilder.append("|[()+\\-*/,]");

        return regexBuilder.toString();
    }

    /**
     * ------------------------------------------------
     *      STEP 2: CONVERT INFIX TO POSTFIX
     * ------------------------------------------------
     */

     /**
      * Convert the list of tokens from infix notation to postfix notation
      * Postfix notations help us prep for converting it to an expression tree (your Java object - Expression)
      * Postfix notation encodes the order of operations directly within the sequence of tokens (meaning you can compute from left to right directly)
      * 
      * Data structure
      *     operatorDeque = Stack of operator symbols
      *     argumentDeque = Stack of counts of arguments for aggregations
      *     outputQueue   = Queue of the output in postfix notation
      *
      * The algorithm in simple:
      *     Iterate through each token.
      *     If the token is:
      *         1. Constant or Cell reference
      *             Offer it to the output queue
      *         2. Aggregate operator
      *             Push it to the operator stack
      *             Push 1 to the operator stack (start the argument count for this aggregate operator)
      *         3. Arithmetic operator
      *             Pop all the operators from the stack that have higher or equal precedence than the current token into the output queue (e.g * has higher precedence than +)
      *             Push the current operator into the operator stack
      *         4. ( symbol
      *             Push ( onto the operator stack
      *         5. ) symbol
      *             Pop all the operators from the stack into the output queue until ( is reached
      *             Pop the ( if it is still in stack (do not add it to queue)
      *             If the operator stack has an aggregate operator after the ( is popped (because aggregate often followed by parenthesis - e.g SUM(...))
      *                 Pop the argument stack to get the argument count
      *                 Offer the aggregate operator to the output queue
      *                 Offer the argument count to the output queue
      *         6. , symbol
      *             Pop all the operators from the stack into the output queue until ( is reached
      *             Pop the argument count to get the number, and to update it to a new value
      *             Push the incremented count back into the argument stack
      *     After iteration finished
      *         Pop all the leftover operators in the stack into the output queue
      *
      *
      * @param tokens the infix tokens from part 1
      * @return a queue of postfix tokens
      */
    private static Queue<String> infixToPostfix(ArrayList<String> tokens) {
        Deque<String> operatorDeque = new ArrayDeque<>();
        Deque<Integer> argumentDeque = new ArrayDeque<>();
        Queue<String> outputQueue = new LinkedList<>();

        for (String token : tokens) {
            if (token.matches("\\d+") || isCellSymbol(token)) {  // Constant or Cell Reference
                outputQueue.offer(token);  // Add Operand to queue
            } else if (isAggregateSymbol(token)) {  // Aggregate function
                operatorDeque.push(token);  // Push aggregate to operator stack
                argumentDeque.push(1);
            } else if ("+-*/".contains(token)) {  // Operator
                while (!operatorDeque.isEmpty() && precedence(operatorDeque.peek()) >= precedence(token)) {
                    outputQueue.offer(operatorDeque.pop());  // Pop operators to queue based on precedence
                }
                operatorDeque.push(token);  // Push current operator onto stack
            } else if (token.equals("(")) {
                operatorDeque.push(token);  // Push '(' onto stack
            } else if (token.equals(")")) {  // Handle closing parenthesis
                while (!operatorDeque.peek().equals("(")) {
                    outputQueue.offer(operatorDeque.pop());  // Pop operators to queue until '('
                }
                operatorDeque.pop();  // Pop '('
                if (!operatorDeque.isEmpty() && isAggregateSymbol(operatorDeque.peek())) {
                    // Handle aggregate function
                    int argCount = argumentDeque.pop();
                    outputQueue.offer(operatorDeque.pop());  // Pop aggregate function (e.g., SUM, AVE, COUNT)
                    outputQueue.offer(String.valueOf(argCount));
                }
            } else if (token.equals(",")){
                // Pop operators to output until reaching '('
                while (!operatorDeque.peek().equals("(")) {
                    outputQueue.offer(operatorDeque.pop());
                }
                // Increment current function's argument count
                int currentCount = argumentDeque.pop();
                argumentDeque.push(currentCount + 1);
            }
        }

        // Pop remaining operators from operator stack to output queue
        while (!operatorDeque.isEmpty()) {
            outputQueue.offer(operatorDeque.pop());
        }

        return outputQueue;
    }

    //============== STEP 2-3 HELPER ===================

    /**
     * precedence helps with PEMDAS
     * higher precedence = being popped into output queue earlier = calculated earlier (first)
     * @param operator the operator symbol
     * @return  the int represent precedence
     */
    private static int precedence(String operator){
        return switch (operator) {
            case "+", "-" -> 1;
            case "*", "/" -> 2;
            case "SUM", "AVE", "COUNT" -> 3;
            default -> 0;
        };
    }

    /**
     * simple check if the token is an aggregate operator
     * @param token the token
     * @return true if it is an aggregate symbol
     */
    private static boolean isAggregateSymbol(String token){
        return token.equals("SUM") || token.equals("AVE") || token.equals("COUNT");
    }
    
    /**
     * simple check if the token is a cell symbol (cell single or group)
     * @param token the token
     * @return true if it is a cell symbol
     */
    private static boolean isCellSymbol(String token){
        return token.matches("([A-Za-z]+\\d+)(:[A-Za-z]+\\d+)?");
    }

    /**
     * ------------------------------------------------
     *      STEP 3: CONVERT POSTFIX TO EXPRESSION
     * ------------------------------------------------
     */

     /**
      * Convert the postfix tokens to Expression
      * Data structure
      *     stack = Stack of Expression
      *
      * The algorithm in simple:
      *     Iterate through each token.
      *     If the token is:
      *         1. Constant
      *             Wrap it in appropriate expression type
      *             Push it to the stack
      *         2. Cell symbol
      *             Fetch it from the CellRepository
      *             Wrap it in appropriate expression type
      *             Push it to the stack
      *         3. Operator
      *             Create Expression-Operator using Supplier pattern
      *             If Expression is Aggregate, set argument count to the next value in the tokens queue
      *             If Expression is Arithmetic, set argument count to 2
      *             For every argument count, pop the arguments from the stack into a temporary list
      *             Reverse the temporary list to set the order to operation correctly
      *             Add each argument of the temporary list as operands of the operator Expression
      *     Push the operator to the stack
      *
      * The final expression is the singular expression in the stack, so pop it to get it.
      *
      * @param outputQueue the Postfix tokens
      * @return the Expression object
      */
    private static Expression postfixToExpression(Queue<String> outputQueue){
        // TODO
        return null;
    }

    //============== STEP 3 HELPER ===================

    /**
     * Parse a string coord into numerical rows and columns
     * For example, 
     *      A1 will be parsed into [0, 0]
     *      B3 will be parsed into [1, 2]
     * 
     * @param coord  a string coordinate (i.e "A1", "AA1", "A1:B2")
     * @return an array of size 2, with coord[0] = column and coord[1] = row
     */
    private static int[] parseCellFormat(String coord){
        int[] parsedCoords = new int[2];

        Pattern pattern = Pattern.compile("([a-zA-Z]+)(\\d+)");
        Matcher matcher = pattern.matcher(coord);

        if(matcher.matches()){
            String letters = matcher.group(1);
            String digits = matcher.group(2);

            parsedCoords[0] = getColumn(letters);
            parsedCoords[1] = getRow(digits);
        }
        return parsedCoords;
    }

    /**
     * Convert a string character into the numerical column representation
     * @param colString the column as a string
     * @return an int representing the column
     */
    private static int getColumn(String colString){
        int sum = 0;
        for(char c: colString.toCharArray()){
            sum += c;
        }
        return sum-'A';
    }

    /**
     * Convert a string number into the numerical row representation
     * @param colString the Row as a string
     * @return an int representing the row
     */
    private static int getRow(String rowString){
        int row = Integer.parseInt(rowString);
        return row-1;
    }
}

    
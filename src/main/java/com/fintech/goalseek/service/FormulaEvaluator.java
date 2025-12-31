package com.fintech.goalseek.service;

import com.fintech.goalseek.exception.FormulaEvaluationException;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.DoubleUnaryOperator;

/**
 * Service for evaluating mathematical expressions.
 * Uses exp4j for parsing and evaluating formulas.
 */
@Service
public class FormulaEvaluator {

    /**
     * Validate a formula expression with the given variables.
     */
    public void validateFormula(String expression, List<String> variables) {
        try {
            ExpressionBuilder builder = new ExpressionBuilder(expression);
            for (String variable : variables) {
                builder.variable(variable);
            }
            Expression exp = builder.build();
            
            // Set test values to validate
            for (String variable : variables) {
                exp.setVariable(variable, 1.0);
            }
            
            double result = exp.evaluate();
            if (Double.isNaN(result) || Double.isInfinite(result)) {
                throw new FormulaEvaluationException("Formula produces invalid result with test values");
            }
        } catch (IllegalArgumentException e) {
            throw new FormulaEvaluationException("Invalid formula: " + e.getMessage());
        }
    }

    /**
     * Evaluate a formula with given variable values.
     */
    public double evaluate(String expression, Map<String, Double> variables) {
        try {
            ExpressionBuilder builder = new ExpressionBuilder(expression);
            for (String variable : variables.keySet()) {
                builder.variable(variable);
            }
            Expression exp = builder.build();
            
            for (Map.Entry<String, Double> entry : variables.entrySet()) {
                exp.setVariable(entry.getKey(), entry.getValue());
            }
            
            return exp.evaluate();
        } catch (Exception e) {
            throw new FormulaEvaluationException("Error evaluating formula: " + e.getMessage());
        }
    }

    /**
     * Create a function that evaluates the formula with one variable varying.
     * All other variables are fixed to their known values.
     */
    public DoubleUnaryOperator createFunction(String expression, 
                                               Map<String, Double> knownValues,
                                               String variableToSeek) {
        return x -> {
            try {
                ExpressionBuilder builder = new ExpressionBuilder(expression);
                
                // Add all variables
                for (String variable : knownValues.keySet()) {
                    builder.variable(variable);
                }
                builder.variable(variableToSeek);
                
                Expression exp = builder.build();
                
                // Set known values
                for (Map.Entry<String, Double> entry : knownValues.entrySet()) {
                    exp.setVariable(entry.getKey(), entry.getValue());
                }
                
                // Set the variable we're seeking
                exp.setVariable(variableToSeek, x);
                
                return exp.evaluate();
            } catch (Exception e) {
                return Double.NaN;
            }
        };
    }
}

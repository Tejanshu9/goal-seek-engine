package com.fintech.goalseek.service;

import com.fintech.goalseek.algorithm.BrentAlgorithm;
import com.fintech.goalseek.algorithm.GoalSeekAlgorithm;
import com.fintech.goalseek.algorithm.GoalSeekResult;
import com.fintech.goalseek.dto.GoalSeekRequest;
import com.fintech.goalseek.dto.GoalSeekResponse;
import com.fintech.goalseek.entity.Formula;
import com.fintech.goalseek.exception.GoalSeekException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.DoubleUnaryOperator;

/**
 * Service for performing goal seek operations on formulas.
 */
@Service
public class GoalSeekService {

    private final FormulaService formulaService;
    private final FormulaEvaluator formulaEvaluator;
    private final List<GoalSeekAlgorithm> algorithms;
    private final BrentAlgorithm brentAlgorithm;

    @Value("${goalseek.max-iterations:1000}")
    private int maxIterations;

    @Value("${goalseek.tolerance:1e-10}")
    private double tolerance;

    @Value("${goalseek.default-lower-bound:-1000000}")
    private double defaultLowerBound;

    @Value("${goalseek.default-upper-bound:1000000}")
    private double defaultUpperBound;

    public GoalSeekService(FormulaService formulaService, FormulaEvaluator formulaEvaluator,
                           List<GoalSeekAlgorithm> algorithms, BrentAlgorithm brentAlgorithm) {
        this.formulaService = formulaService;
        this.formulaEvaluator = formulaEvaluator;
        this.algorithms = algorithms;
        this.brentAlgorithm = brentAlgorithm;
    }

    public GoalSeekResponse seekGoal(GoalSeekRequest request) {
        Formula formula = formulaService.getFormulaEntityByName(request.getFormulaName());

        if (!formula.getVariables().contains(request.getSeekVariable())) {
            throw new GoalSeekException("Seek variable '" + request.getSeekVariable() +
                    "' is not a valid variable in formula '" + formula.getName() + "'");
        }

        for (String variable : formula.getVariables()) {
            if (!variable.equals(request.getSeekVariable()) &&
                !request.getKnownValues().containsKey(variable)) {
                throw new GoalSeekException("Missing value for variable: " + variable);
            }
        }

        double lowerBound = request.getLowerBound() != null ? request.getLowerBound() : defaultLowerBound;
        double upperBound = request.getUpperBound() != null ? request.getUpperBound() : defaultUpperBound;

        DoubleUnaryOperator function = formulaEvaluator.createFunction(
                formula.getExpression(),
                request.getKnownValues(),
                request.getSeekVariable()
        );

        if (request.getInitialGuess() != null) {
            double guess = request.getInitialGuess();
            double range = Math.max(Math.abs(guess) * 10, upperBound - lowerBound);
            lowerBound = guess - range / 2;
            upperBound = guess + range / 2;
        }

        GoalSeekResult result = brentAlgorithm.solve(
                function,
                request.getTargetValue(),
                lowerBound,
                upperBound,
                tolerance,
                maxIterations
        );

        if (!result.isConverged()) {
            for (GoalSeekAlgorithm algorithm : algorithms) {
                if (algorithm.getName().equals("Brent")) continue;

                GoalSeekResult altResult = algorithm.solve(
                        function,
                        request.getTargetValue(),
                        lowerBound,
                        upperBound,
                        tolerance,
                        maxIterations
                );

                if (altResult.isConverged() || altResult.getError() < result.getError()) {
                    result = altResult;
                    if (result.isConverged()) break;
                }
            }
        }

        Map<String, Double> allValues = new HashMap<>(request.getKnownValues());
        allValues.put(request.getSeekVariable(), result.getValue());

        return GoalSeekResponse.builder()
                .success(result.isConverged())
                .formulaName(request.getFormulaName())
                .seekVariable(request.getSeekVariable())
                .computedValue(result.getValue())
                .targetValue(request.getTargetValue())
                .achievedValue(result.getAchievedValue())
                .error(result.getError())
                .iterations(result.getIterations())
                .algorithm(result.getAlgorithm())
                .allValues(allValues)
                .message(result.getMessage())
                .build();
    }

    public double evaluateFormula(String formulaName, Map<String, Double> values) {
        Formula formula = formulaService.getFormulaEntityByName(formulaName);
        return formulaEvaluator.evaluate(formula.getExpression(), values);
    }
}

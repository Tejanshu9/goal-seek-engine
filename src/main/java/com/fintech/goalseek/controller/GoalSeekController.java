package com.fintech.goalseek.controller;

import com.fintech.goalseek.dto.GoalSeekRequest;
import com.fintech.goalseek.dto.GoalSeekResponse;
import com.fintech.goalseek.service.GoalSeekService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for goal seek operations.
 */
@RestController
@RequestMapping("/api/goal-seek")
@Tag(name = "Goal Seek", description = "APIs for performing goal seek calculations")
public class GoalSeekController {

    private final GoalSeekService goalSeekService;

    public GoalSeekController(GoalSeekService goalSeekService) {
        this.goalSeekService = goalSeekService;
    }

    @PostMapping
    @Operation(summary = "Perform goal seek",
               description = "Finds the value of a variable that produces the target result for a given formula")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Goal seek completed"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters or goal seek failed"),
        @ApiResponse(responseCode = "404", description = "Formula not found")
    })
    public ResponseEntity<GoalSeekResponse> seekGoal(@Valid @RequestBody GoalSeekRequest request) {
        GoalSeekResponse response = goalSeekService.seekGoal(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/evaluate/{formulaName}")
    @Operation(summary = "Evaluate a formula",
               description = "Evaluates a formula with given variable values (utility endpoint)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Formula evaluated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid parameters"),
        @ApiResponse(responseCode = "404", description = "Formula not found")
    })
    public ResponseEntity<Map<String, Object>> evaluateFormula(
            @PathVariable String formulaName,
            @RequestBody Map<String, Double> values) {
        double result = goalSeekService.evaluateFormula(formulaName, values);
        return ResponseEntity.ok(Map.of(
                "formulaName", formulaName,
                "inputValues", values,
                "result", result
        ));
    }
}

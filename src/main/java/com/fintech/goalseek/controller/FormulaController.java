package com.fintech.goalseek.controller;

import com.fintech.goalseek.dto.FormulaRequest;
import com.fintech.goalseek.dto.FormulaResponse;
import com.fintech.goalseek.service.FormulaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing financial formulas.
 */
@RestController
@RequestMapping("/api/formulas")
@Tag(name = "Formula Management", description = "APIs for creating and managing financial formulas")
public class FormulaController {

    private final FormulaService formulaService;

    public FormulaController(FormulaService formulaService) {
        this.formulaService = formulaService;
    }

    @PostMapping
    @Operation(summary = "Create a new formula",
               description = "Creates a new financial formula with the specified expression and variables")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Formula created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid formula expression or parameters"),
        @ApiResponse(responseCode = "409", description = "Formula with same name already exists")
    })
    public ResponseEntity<FormulaResponse> createFormula(@Valid @RequestBody FormulaRequest request) {
        FormulaResponse response = formulaService.createFormula(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Get all formulas", description = "Retrieves a list of all registered formulas")
    public ResponseEntity<List<FormulaResponse>> getAllFormulas() {
        return ResponseEntity.ok(formulaService.getAllFormulas());
    }

    @GetMapping("/{name}")
    @Operation(summary = "Get formula by name", description = "Retrieves a specific formula by its unique name")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Formula found"),
        @ApiResponse(responseCode = "404", description = "Formula not found")
    })
    public ResponseEntity<FormulaResponse> getFormulaByName(
            @Parameter(description = "Name of the formula") @PathVariable String name) {
        return ResponseEntity.ok(formulaService.getFormulaByName(name));
    }

    @PutMapping("/{name}")
    @Operation(summary = "Update a formula", description = "Updates an existing formula with new expression or variables")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Formula updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid formula expression or parameters"),
        @ApiResponse(responseCode = "404", description = "Formula not found")
    })
    public ResponseEntity<FormulaResponse> updateFormula(
            @Parameter(description = "Name of the formula to update") @PathVariable String name,
            @Valid @RequestBody FormulaRequest request) {
        return ResponseEntity.ok(formulaService.updateFormula(name, request));
    }

    @DeleteMapping("/{name}")
    @Operation(summary = "Delete a formula", description = "Deletes a formula by its name")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Formula deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Formula not found")
    })
    public ResponseEntity<Void> deleteFormula(
            @Parameter(description = "Name of the formula to delete") @PathVariable String name) {
        formulaService.deleteFormula(name);
        return ResponseEntity.noContent().build();
    }
}

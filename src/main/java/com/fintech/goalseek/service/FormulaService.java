package com.fintech.goalseek.service;

import com.fintech.goalseek.dto.FormulaRequest;
import com.fintech.goalseek.dto.FormulaResponse;
import com.fintech.goalseek.entity.Formula;
import com.fintech.goalseek.exception.FormulaNotFoundException;
import com.fintech.goalseek.exception.FormulaDuplicateException;
import com.fintech.goalseek.repository.FormulaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing financial formulas.
 */
@Service
public class FormulaService {

    private final FormulaRepository formulaRepository;
    private final FormulaEvaluator formulaEvaluator;

    public FormulaService(FormulaRepository formulaRepository, FormulaEvaluator formulaEvaluator) {
        this.formulaRepository = formulaRepository;
        this.formulaEvaluator = formulaEvaluator;
    }

    @Transactional
    public FormulaResponse createFormula(FormulaRequest request) {
        if (formulaRepository.existsByName(request.getName())) {
            throw new FormulaDuplicateException("Formula with name '" + request.getName() + "' already exists");
        }

        formulaEvaluator.validateFormula(request.getExpression(), request.getVariables());

        Formula formula = Formula.builder()
                .name(request.getName().trim().toUpperCase().replaceAll("[^A-Z0-9]+", "_").replaceAll("^_+|_+$", ""))
                .expression(request.getExpression())
                .description(request.getDescription())
                .outputVariable(request.getOutputVariable())
                .variables(request.getVariables())
                .build();

        Formula saved = formulaRepository.save(formula);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<FormulaResponse> getAllFormulas() {
        return formulaRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FormulaResponse getFormulaByName(String name) {
        Formula formula = formulaRepository.findByName(name)
                .orElseThrow(() -> new FormulaNotFoundException("Formula not found: " + name));
        return mapToResponse(formula);
    }

    @Transactional(readOnly = true)
    public Formula getFormulaEntityByName(String name) {
        return formulaRepository.findByName(name)
                .orElseThrow(() -> new FormulaNotFoundException("Formula not found: " + name));
    }

    @Transactional
    public FormulaResponse updateFormula(String name, FormulaRequest request) {
        Formula formula = formulaRepository.findByName(name)
                .orElseThrow(() -> new FormulaNotFoundException("Formula not found: " + name));

        if (!name.equals(request.getName()) && formulaRepository.existsByName(request.getName())) {
            throw new FormulaDuplicateException("Formula with name '" + request.getName() + "' already exists");
        }

        formulaEvaluator.validateFormula(request.getExpression(), request.getVariables());

        formula.setName(request.getName());
        formula.setExpression(request.getExpression());
        formula.setDescription(request.getDescription());
        formula.setOutputVariable(request.getOutputVariable());
        formula.setVariables(request.getVariables());

        Formula saved = formulaRepository.save(formula);
        return mapToResponse(saved);
    }

    @Transactional
    public void deleteFormula(String name) {
        Formula formula = formulaRepository.findByName(name)
                .orElseThrow(() -> new FormulaNotFoundException("Formula not found: " + name));
        formulaRepository.delete(formula);
    }

    private FormulaResponse mapToResponse(Formula formula) {
        return FormulaResponse.builder()
                .id(formula.getId())
                .name(formula.getName())
                .expression(formula.getExpression())
                .description(formula.getDescription())
                .outputVariable(formula.getOutputVariable())
                .variables(formula.getVariables())
                .createdAt(formula.getCreatedAt())
                .updatedAt(formula.getUpdatedAt())
                .build();
    }
}

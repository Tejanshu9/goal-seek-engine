package com.fintech.goalseek.config;

import com.fintech.goalseek.dto.FormulaRequest;
import com.fintech.goalseek.service.FormulaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Initializes sample financial formulas on application startup.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final FormulaService formulaService;

    public DataInitializer(FormulaService formulaService) {
        this.formulaService = formulaService;
    }

    @Override
    public void run(String... args) {
        log.info("Initializing sample financial formulas...");
        createSampleFormulas();
        log.info("Sample formulas initialized successfully");
    }

    private void createSampleFormulas() {
        // SIP Future Value
        createFormula("SIP_FUTURE_VALUE",
                "P * (((1 + r)^n - 1) / r) * (1 + r)",
                "Calculate future value of SIP investment. P=monthly investment, r=monthly rate, n=months",
                "FV", List.of("P", "r", "n"));

        // EMI Formula
        createFormula("EMI_CALCULATION",
                "P * r * (1 + r)^n / ((1 + r)^n - 1)",
                "Calculate EMI. P=principal, r=monthly interest rate, n=number of months",
                "EMI", List.of("P", "r", "n"));

        // Simple Interest
        createFormula("SIMPLE_INTEREST",
                "P * R * T / 100",
                "Calculate simple interest. P=principal, R=annual rate, T=time in years",
                "SI", List.of("P", "R", "T"));

        // Compound Interest
        createFormula("COMPOUND_INTEREST",
                "P * (1 + r/n)^(n*t)",
                "Calculate compound interest amount. P=principal, r=annual rate, n=compounds per year, t=years",
                "A", List.of("P", "r", "n", "t"));

        // Present Value
        createFormula("PRESENT_VALUE",
                "CF / (1 + r)^n",
                "Calculate present value of future cash flow. CF=cash flow, r=discount rate, n=periods",
                "PV", List.of("CF", "r", "n"));

        // Future Value
        createFormula("FUTURE_VALUE",
                "PV * (1 + r)^n",
                "Calculate future value. PV=present value, r=rate, n=periods",
                "FV", List.of("PV", "r", "n"));

        // Credit Utilization
        createFormula("CREDIT_UTILIZATION",
                "(UsedCredit / CreditLimit) * 100",
                "Calculate credit utilization percentage",
                "Utilization", List.of("UsedCredit", "CreditLimit"));

        // ROI
        createFormula("ROI",
                "((FinalValue - InitialValue) / InitialValue) * 100",
                "Calculate ROI percentage",
                "ROI", List.of("FinalValue", "InitialValue"));

        // Debt-to-Income
        createFormula("DEBT_TO_INCOME",
                "(MonthlyDebt / MonthlyIncome) * 100",
                "Calculate debt-to-income ratio",
                "DTI", List.of("MonthlyDebt", "MonthlyIncome"));
    }

    private void createFormula(String name, String expression, String description,
                               String outputVar, List<String> variables) {
        try {
            formulaService.createFormula(FormulaRequest.builder()
                    .name(name)
                    .expression(expression)
                    .description(description)
                    .outputVariable(outputVar)
                    .variables(variables)
                    .build());
            log.info("Created formula: {}", name);
        } catch (Exception e) {
            log.warn("Could not create formula {}: {}", name, e.getMessage());
        }
    }
}

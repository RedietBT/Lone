package com.example.lone.Lone.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanApplicationRequest {

    @NotNull(message = "Loan amount is required")
    @DecimalMin(value = "100.00", message = "Loan amount must be at least 100.00")
    private BigDecimal loanAmount;

    @NotBlank(message = "Loan type is required")
    private String loanType; // e.g., "Personal Loan", "Home Loan", "Car Loan"

    @NotNull(message = "Duration in months is required")
    @Min(value = 3, message = "Loan duration must be at least 6 months")
    private Integer durationMonths;

    @NotBlank(message = "Loan purpose is required")
    private String purpose;

    @NotNull(message = "Annual income is required")
    @DecimalMin(value = "0.00", inclusive = false, message = "Annual income must be positive")
    private BigDecimal annualIncome;
}

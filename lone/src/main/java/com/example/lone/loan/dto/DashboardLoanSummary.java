package com.example.lone.loan.dto;

import com.example.lone.loan.model.LoanStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardLoanSummary {

    private Long id;
    private BigDecimal loanAmount;
    private String loanType;
    private Integer durationMonths;
    private LoanStatus status;
    private LocalDate loanStartDate;
    private BigDecimal monthlyEmi;

    private LocalDate nextEmiDueDate;
    private BigDecimal nextEmiAmount;
    private BigDecimal outstandingBalance;
    private Long remainingPayments;
}

package com.example.lone.Lone.dto;

import com.example.lone.Lone.model.LoanStatus;
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
public class LoanApplicationResponse {

    private Long id;
    private String applicantFirstname;
    private String applicantLastname;
    private String applicantEmail;
    private BigDecimal loanAmount;
    private String loneType;
    private Integer durationMonths;
    private String purpose;
    private BigDecimal annualIncome;
    private LoanStatus status;

    private LocalDate localStartDate;
    private BigDecimal monthlyEmi;
    private String repaymentSchedule;
    private String adminRemarks;
}

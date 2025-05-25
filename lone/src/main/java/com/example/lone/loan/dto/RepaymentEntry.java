package com.example.lone.loan.dto;

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
public class RepaymentEntry {
    private Integer monthNumber;
    private LocalDate paymentDate;
    private BigDecimal monthlyEmi;
    private BigDecimal principalPaid;
    private BigDecimal interestPaid;
    private BigDecimal remainingBalance;
}
package com.example.lone.loan.dto;

import com.example.lone.loan.model.LoanStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoanApprovalRequest {

    @NotNull(message = "Loan status (APPROVED/REJECTED) is required")
    private LoanStatus status;

    @Size(max = 500, message = "Remarks cannot exceed 500 characters")
    private String remarks;
}

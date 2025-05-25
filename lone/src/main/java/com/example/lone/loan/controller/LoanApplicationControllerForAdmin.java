package com.example.lone.loan.controller;

import com.example.lone.loan.dto.LoanApplicationResponse;
import com.example.lone.loan.dto.LoanApprovalRequest;
import com.example.lone.loan.services.LoanApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/loans")
@RequiredArgsConstructor
public class LoanApplicationControllerForAdmin {

    private final LoanApplicationService loanApplicationService;

    //Get all loans
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<LoanApplicationResponse>> getAllLoanApplications(){
        List<LoanApplicationResponse> loanApplicationResponses = loanApplicationService.getAllLoanApplication();
        return ResponseEntity.ok(loanApplicationResponses);
    }

    //Get pending loans
    @GetMapping("/pending")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<LoanApplicationResponse>> getPendingLoanApplications(){
        List<LoanApplicationResponse> loanApplicationResponses= loanApplicationService.getPendingLoanApplications();
        return ResponseEntity.ok(loanApplicationResponses);
    }

    //Get single loan application by Id
    @GetMapping("/{loanId}")
    @PreAuthorize("hasAuthority('ADMIN') or @loanSecurity.isOwner(#loanId)")
    public ResponseEntity<LoanApplicationResponse> getLoanApplicationById(@PathVariable Long loanId){
        LoanApplicationResponse loanApplicationResponse = loanApplicationService.getLoanApplicationById(loanId);
        return ResponseEntity.ok(loanApplicationResponse);
    }

    @PutMapping("/{loanId}/approve")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<LoanApplicationResponse> approveLoanApplication(
            @PathVariable Long loanId,
            @RequestBody(required = false) @Valid LoanApprovalRequest remarkRequest) { // Remarks are optional
        LoanApplicationResponse approvedLoan = loanApplicationService.approveLoanApplication(loanId, remarkRequest);
        return ResponseEntity.ok(approvedLoan);
    }

    @PutMapping("/{loanId}/reject")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<LoanApplicationResponse> rejectLoanApplication(
            @PathVariable Long loanId,
            @RequestBody(required = false) @Valid LoanApprovalRequest remarkRequest) { // Remarks are optional
        LoanApplicationResponse rejectedLoan = loanApplicationService.rejectLoanApplication(loanId, remarkRequest);
        return ResponseEntity.ok(rejectedLoan);
    }

}

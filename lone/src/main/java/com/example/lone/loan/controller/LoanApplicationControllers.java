package com.example.lone.loan.controller;


import com.example.lone.loan.dto.DashboardLoanSummary;
import com.example.lone.loan.dto.LoanApplicationRequest;
import com.example.lone.loan.model.LoanApplication;
import com.example.lone.loan.services.LoanApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RestController
@RequestMapping("/api/v1/loans")
@RequiredArgsConstructor
public class LoanApplicationControllers {

    private final LoanApplicationService loanApplicationService;

    @PostMapping("/apply")
    public ResponseEntity<String> applyForLoan(@Valid @RequestBody LoanApplicationRequest request){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        LoanApplication submittedLone = loanApplicationService.submitLoanApplication(request, userEmail);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Loan application submitted successfully with ID:" + submittedLone.getId());
    }

    //Get single loan application by Id
    @GetMapping("/my")
    @PreAuthorize("hasAnyAuthority('USER')")
    public ResponseEntity<List<DashboardLoanSummary>> getCustomerDashboard() {
        List<DashboardLoanSummary> dashboardLoans = loanApplicationService.getCustomerDashboardLoans();
        return ResponseEntity.ok(dashboardLoans);
    }

    @GetMapping("{loanId}/repayment")
//    @PreAuthorize("hasAuthority('ADMIN') or @loanSecurity.isOwner(#loanId)") // Admin can view any, user can view their own
    public ResponseEntity<String> getLoanRepaymentSchedule(@PathVariable Long loanId) {
        String schedule = loanApplicationService.getRepaymentSchedule(loanId);
        return ResponseEntity.ok(schedule);
    }
}

package com.example.lone.loan.controler;


import com.example.lone.loan.dto.LoanApplicationRequest;
import com.example.lone.loan.dto.LoanApplicationResponse;
import com.example.lone.loan.model.LoanApplication;
import com.example.lone.loan.services.LoanApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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
    @PreAuthorize("hasAuthority('ADMIN') or @loanSecurity.isOwner(#loanId)")
    public ResponseEntity<LoanApplicationResponse> getLoanApplicationById(@PathVariable Long loanId){
        LoanApplicationResponse loanApplicationResponse = loanApplicationService.getLoanApplicationById(loanId);
        return ResponseEntity.ok(loanApplicationResponse);
    }
}

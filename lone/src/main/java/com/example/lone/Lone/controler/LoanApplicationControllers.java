package com.example.lone.Lone.controler;


import com.example.lone.Lone.dto.LoanApplicationRequest;
import com.example.lone.Lone.model.LoanApplication;
import com.example.lone.Lone.services.LoanApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}

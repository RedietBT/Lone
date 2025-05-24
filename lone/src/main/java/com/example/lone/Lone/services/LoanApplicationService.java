package com.example.lone.Lone.services;

import com.example.lone.Lone.dto.LoanApplicationRequest;
import com.example.lone.Lone.model.LoanApplication;
import com.example.lone.Lone.model.LoanStatus;
import com.example.lone.Lone.repository.LoanApplicationRepository;
import com.example.lone.userAuth.user.User;
import com.example.lone.userAuth.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoanApplicationService {

    private final LoanApplicationRepository loanApplicationRepository;
    private final UserRepository userRepository;

    @Transactional
    public LoanApplication submitLoanApplication(LoanApplicationRequest request, String userEmail){
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email:" + userEmail));

        LoanApplication loanApplication = LoanApplication.builder()
                .user(user)
                .loanAmount(request.getLoanAmount())
                .loanType(request.getLoanType())
                .durationMonths(request.getDurationMonths())
                .purpose(request.getPurpose())
                .annualIncome(request.getAnnualIncome())
                .status(LoanStatus.PENDING)
                .build();

        return loanApplicationRepository.save(loanApplication);
    }
}

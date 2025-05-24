package com.example.lone.Lone.services;

import com.example.lone.Lone.dto.LoanApplicationRequest;
import com.example.lone.Lone.dto.LoanApplicationResponse;
import com.example.lone.Lone.model.LoanApplication;
import com.example.lone.Lone.model.LoanStatus;
import com.example.lone.Lone.repository.LoanApplicationRepository;
import com.example.lone.userAuth.user.User;
import com.example.lone.userAuth.user.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Transactional(readOnly = true)
    public List<LoanApplicationResponse> getAllLoanApplication(){
        return loanApplicationRepository.findAll().stream()
                .map(this::mapToLoanApplicationResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LoanApplicationResponse> getPendingLoanApplications() {
        return loanApplicationRepository.findByStatus(LoanStatus.PENDING).stream()
                .map(this::mapToLoanApplicationResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    private LoanApplicationResponse mapToLoanApplicationResponse (LoanApplication loanApplication){
        return LoanApplicationResponse.builder()
                .id(loanApplication.getId())
                .applicantFirstname(loanApplication.getUser().getFirstname())
                .applicantLastname(loanApplication.getUser().getLastname())
                .applicantEmail(loanApplication.getUser().getEmail())
                .loanAmount(loanApplication.getLoanAmount())
                .loneType(loanApplication.getLoanType())
                .durationMonths(loanApplication.getDurationMonths())
                .purpose(loanApplication.getPurpose())
                .annualIncome(loanApplication.getAnnualIncome())
                .status(loanApplication.getStatus())

                .localStartDate(loanApplication.getLoanStartDate())
                .monthlyEmi(loanApplication.getMonthlyEmi())
                .repaymentSchedule(loanApplication.getRepaymentSchedule())
                .adminRemarks(loanApplication.getAdminRemarks())
                .build();
    }
}

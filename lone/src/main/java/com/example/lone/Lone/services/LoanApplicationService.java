package com.example.lone.Lone.services;

import com.example.lone.Lone.dto.LoanApplicationRequest;
import com.example.lone.Lone.dto.LoanApplicationResponse;
import com.example.lone.Lone.dto.LoanApprovalRequest;
import com.example.lone.Lone.model.LoanApplication;
import com.example.lone.Lone.model.LoanStatus;
import com.example.lone.Lone.repository.LoanApplicationRepository;
import com.example.lone.LoneApplication;
import com.example.lone.userAuth.user.User;
import com.example.lone.userAuth.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanApplicationService {

    private final LoanApplicationRepository loanApplicationRepository;
    private final UserRepository userRepository;

    //submit loan
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

    //get information about loans
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

    @Transactional(readOnly = true)
    public LoanApplicationResponse getLoanApplicationById(Long loanId){
        LoanApplication loanApplication = loanApplicationRepository.findById(loanId)
                .orElseThrow(() -> new EntityNotFoundException("Loan Application not found with ID:" + loanId));
        return mapToLoanApplicationResponse(loanApplication);
    }

    //approve or reject loans
    @Transactional
    public LoanApplicationResponse processLoanApplication(Long loanId, LoanApprovalRequest approvalRequest) {
        LoanApplication loanApplication = loanApplicationRepository.findById(loanId)
                .orElseThrow(() -> new EntityNotFoundException("Loan application not found with ID: " + loanId));

        // Ensure the loan is in PENDING state before processing
        if (loanApplication.getStatus() != LoanStatus.PENDING) {
            throw new IllegalStateException("Loan application with ID " + loanId + " is not in PENDING status and cannot be processed.");
        }

        loanApplication.setStatus(approvalRequest.getStatus());
        loanApplication.setAdminRemarks(approvalRequest.getRemarks());

        if (approvalRequest.getStatus() == LoanStatus.APPROVED) {
            loanApplication.setLoanStartDate(LocalDate.now());

            // Calculate Monthly EMI
            // P = Principal Loan Amount (loanAmount)
            // R = Monthly Interest Rate (Annual Rate / 12 / 100)
            // N = Number of Payments (durationMonths)
            // EMI = P * R * (1 + R)^N / ((1 + R)^N – 1)

            BigDecimal principal = loanApplication.getLoanAmount();
            double annualInterestRate = 0.10;
            BigDecimal monthlyInterestRate = BigDecimal.valueOf(annualInterestRate / 12.0);
            int numberOfPayments = loanApplication.getDurationMonths();

            if (monthlyInterestRate.compareTo(BigDecimal.ZERO) == 0) {
                loanApplication.setMonthlyEmi(principal.divide(BigDecimal.valueOf(numberOfPayments), 2, RoundingMode.HALF_UP));
            } else {
                BigDecimal powNumerator = monthlyInterestRate.add(BigDecimal.ONE).pow(numberOfPayments);
                BigDecimal emi = principal
                        .multiply(monthlyInterestRate)
                        .multiply(powNumerator)
                        .divide(powNumerator.subtract(BigDecimal.ONE), 2, RoundingMode.HALF_UP); // 2 decimal places for currency
                loanApplication.setMonthlyEmi(emi);
            }

            // Generate Repayment Schedule
            StringBuilder schedule = new StringBuilder();
            BigDecimal remainingBalance = principal;
            LocalDate currentDate = LocalDate.now();

            schedule.append("Repayment Schedule:\n");
            for (int i = 1; i <= numberOfPayments; i++) {
                BigDecimal interestPayment = remainingBalance.multiply(monthlyInterestRate).setScale(2, RoundingMode.HALF_UP);
                BigDecimal principalPayment = loanApplication.getMonthlyEmi().subtract(interestPayment);
                remainingBalance = remainingBalance.subtract(principalPayment);
                currentDate = currentDate.plusMonths(1);

                schedule.append(String.format("Month %d (%s): EMI=%.2f, Principal=%.2f, Interest=%.2f, Remaining=%.2f\n",
                        i, currentDate, loanApplication.getMonthlyEmi(), principalPayment, interestPayment, remainingBalance.max(BigDecimal.ZERO)));
            }
            loanApplication.setRepaymentSchedule(schedule.toString());

        } else if (approvalRequest.getStatus() == LoanStatus.REJECTED) {
            loanApplication.setLoanStartDate(null);
            loanApplication.setMonthlyEmi(null);
            loanApplication.setRepaymentSchedule(null);
        }

        LoanApplication updatedLoan = loanApplicationRepository.save(loanApplication);
        return mapToLoanApplicationResponse(updatedLoan);
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

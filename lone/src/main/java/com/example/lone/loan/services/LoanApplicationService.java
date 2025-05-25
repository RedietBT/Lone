package com.example.lone.loan.services;

import com.example.lone.loan.dto.LoanApplicationRequest;
import com.example.lone.loan.dto.LoanApplicationResponse;
import com.example.lone.loan.dto.LoanApprovalRequest;
import com.example.lone.loan.model.LoanApplication;
import com.example.lone.loan.model.LoanStatus;
import com.example.lone.loan.repository.LoanApplicationRepository;
import com.example.lone.userAuth.user.User;
import com.example.lone.userAuth.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    //approve loans
    @Transactional
    public LoanApplicationResponse approveLoanApplication(Long loanId, LoanApprovalRequest remarkRequest) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("User attempting to approve: " + authentication.getName());
        System.out.println("User's authorities: " + authentication.getAuthorities());

        LoanApplication loanApplication = loanApplicationRepository.findById(loanId)
                .orElseThrow(() -> new EntityNotFoundException("Loan application not found with ID: " + loanId));

        if (loanApplication.getStatus() != LoanStatus.PENDING) {
            throw new IllegalStateException("Loan application with ID " + loanId + " is not in PENDING status and cannot be approved.");
        }

        loanApplication.setStatus(LoanStatus.APPROVED);
        loanApplication.setAdminRemarks(remarkRequest != null ? remarkRequest.getRemarks() : null);

        loanApplication.setLoanStartDate(LocalDate.now());

        // Calculate Monthly EMI
        BigDecimal principal = loanApplication.getLoanAmount();
        double annualInterestRate = 0.10;
        BigDecimal monthlyInterestRate = BigDecimal.valueOf(annualInterestRate / 12.0);
        int numberOfPayments = loanApplication.getDurationMonths();

        BigDecimal emi;
        if (monthlyInterestRate.compareTo(BigDecimal.ZERO) == 0) {
            emi = principal.divide(BigDecimal.valueOf(numberOfPayments), 2, RoundingMode.HALF_UP);
        } else {
            BigDecimal powNumerator = monthlyInterestRate.add(BigDecimal.ONE).pow(numberOfPayments);
            emi = principal
                    .multiply(monthlyInterestRate)
                    .multiply(powNumerator)
                    .divide(powNumerator.subtract(BigDecimal.ONE), 2, RoundingMode.HALF_UP);
        }
        loanApplication.setMonthlyEmi(emi);

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

        LoanApplication updatedLoan = loanApplicationRepository.save(loanApplication);
        return mapToLoanApplicationResponse(updatedLoan);
    }

    //reject loan
    @Transactional
    public LoanApplicationResponse rejectLoanApplication(Long loanId, LoanApprovalRequest remarkRequest) {
        LoanApplication loanApplication = loanApplicationRepository.findById(loanId)
                .orElseThrow(() -> new EntityNotFoundException("Loan application not found with ID: " + loanId));

        if (loanApplication.getStatus() != LoanStatus.PENDING) {
            throw new IllegalStateException("Loan application with ID " + loanId + " is not in PENDING status and cannot be rejected.");
        }

        loanApplication.setStatus(LoanStatus.REJECTED);
        loanApplication.setAdminRemarks(remarkRequest != null ? remarkRequest.getRemarks() : null);

        loanApplication.setLoanStartDate(null);
        loanApplication.setMonthlyEmi(null);
        loanApplication.setRepaymentSchedule(null);

        LoanApplication updatedLoan = loanApplicationRepository.save(loanApplication);
        return mapToLoanApplicationResponse(updatedLoan);
    }

    //get repayment schedule
    @Transactional(readOnly = true)
    public String getRepaymentSchedule(Long loanId) {
        LoanApplication loanApplication = loanApplicationRepository.findById(loanId)
                .orElseThrow(() -> new EntityNotFoundException("Loan application not found with ID: " + loanId));

        if (loanApplication.getStatus() != LoanStatus.APPROVED || loanApplication.getRepaymentSchedule() == null) {
            throw new IllegalStateException("Repayment schedule is not available for loan ID " + loanId + ". Loan might not be approved or schedule not generated.");
        }
        return loanApplication.getRepaymentSchedule();
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

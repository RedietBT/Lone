package com.example.lone.web.controller;

import com.example.lone.loan.dto.DashboardLoanSummary;
import com.example.lone.loan.dto.LoanApplicationRequest;


import com.example.lone.loan.services.LoanApplicationService;
import com.example.lone.userAuth.user.User;
import com.example.lone.userAuth.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('USER')")
public class CustomerWebController {

    private final LoanApplicationService loanApplicationService;
    private final UserRepository userRepository; // To get the current user's details for dashboard title

    @GetMapping("/customer/dashboard")
    public String customerDashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        Optional<User> currentUser = userRepository.findByEmail(userEmail);
        currentUser.ifPresent(user -> model.addAttribute("userName", user.getFirstname())); // Pass user's name to template

        List<DashboardLoanSummary> loans = loanApplicationService.getCustomerDashboardLoans();
        model.addAttribute("loans", loans);
        return "customer/dashboard"; // Corresponds to src/main/resources/templates/customer/dashboard.html
    }

    @GetMapping("/customer/apply-loan")
    public String showApplyLoanForm(Model model) {
        model.addAttribute("loanApplicationRequest", new LoanApplicationRequest());
        return "customer/apply-loan"; // Corresponds to src/main/resources/templates/customer/apply-loan.html
    }

    @PostMapping("/customer/apply-loan")
    public String applyForLoan(@ModelAttribute("loanApplicationRequest") LoanApplicationRequest request,
                               RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        try {
            loanApplicationService.submitLoanApplication(request, userEmail);
            redirectAttributes.addFlashAttribute("successMessage", "Loan application submitted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error submitting loan application: " + e.getMessage());
        }
        return "redirect:/customer/dashboard"; // Redirect back to dashboard
    }

    @GetMapping("/customer/loan/{loanId}/repayment-schedule")
    @PreAuthorize("@loanSecurity.isOwner(#loanId)") // Use @loanSecurity to ensure user owns the loan
    public String viewRepaymentSchedule(@PathVariable Long loanId, Model model, RedirectAttributes redirectAttributes) {
        try {
            String schedule = loanApplicationService.getRepaymentSchedule(loanId);
            model.addAttribute("loanId", loanId);
            model.addAttribute("repaymentSchedule", schedule);
            return "customer/repayment-schedule"; // Corresponds to src/main/resources/templates/customer/repayment-schedule.html
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Loan not found: " + e.getMessage());
            return "redirect:/customer/dashboard";
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage()); // e.g., "Schedule not generated"
            return "redirect:/customer/dashboard";
        }
    }
}
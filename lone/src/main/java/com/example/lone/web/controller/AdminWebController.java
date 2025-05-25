package com.example.lone.web.controller;

import com.example.lone.loan.dto.LoanApplicationResponse;
import com.example.lone.loan.dto.LoanApprovalRequest;
import com.example.lone.loan.services.LoanApplicationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')") // Only ADMINs can access these pages
public class AdminWebController {

    private final LoanApplicationService loanApplicationService;

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {
        List<LoanApplicationResponse> pendingLoans = loanApplicationService.getPendingLoanApplications();
        List<LoanApplicationResponse> allLoans = loanApplicationService.getAllLoanApplication(); // Or just approved/rejected
        model.addAttribute("pendingLoans", pendingLoans);
        model.addAttribute("allLoans", allLoans); // All loans for admin overview
        return "admin/dashboard"; // Corresponds to src/main/resources/templates/admin/dashboard.html
    }

    @GetMapping("/admin/loan/{loanId}/details")
    public String viewLoanDetails(@PathVariable Long loanId, Model model, RedirectAttributes redirectAttributes) {
        try {
            LoanApplicationResponse loan = loanApplicationService.getLoanApplicationById(loanId);
            model.addAttribute("loan", loan);
            model.addAttribute("loanApprovalRequest", new LoanApprovalRequest()); // For approval/rejection form
            return "admin/loan-details"; // Corresponds to src/main/resources/templates/admin/loan-details.html
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Loan not found: " + e.getMessage());
            return "redirect:/admin/dashboard";
        }
    }

    @PostMapping("/admin/loan/{loanId}/approve")
    public String approveLoan(@PathVariable Long loanId,
                              @ModelAttribute("loanApprovalRequest") LoanApprovalRequest remarkRequest,
                              RedirectAttributes redirectAttributes) {
        try {
            loanApplicationService.approveLoanApplication(loanId, remarkRequest);
            redirectAttributes.addFlashAttribute("successMessage", "Loan " + loanId + " approved successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error approving loan " + loanId + ": " + e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/admin/loan/{loanId}/reject")
    public String rejectLoan(@PathVariable Long loanId,
                             @ModelAttribute("loanApprovalRequest") LoanApprovalRequest remarkRequest,
                             RedirectAttributes redirectAttributes) {
        try {
            loanApplicationService.rejectLoanApplication(loanId, remarkRequest);
            redirectAttributes.addFlashAttribute("successMessage", "Loan " + loanId + " rejected successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error rejecting loan " + loanId + ": " + e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }
}
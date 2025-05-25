package com.example.lone.loan.security;

import com.example.lone.loan.repository.LoanApplicationRepository;
import com.example.lone.userAuth.user.User;
import com.example.lone.userAuth.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("loanSecurity")
@RequiredArgsConstructor
public class LoanSecurity {

    private final LoanApplicationRepository loanApplicationRepository;
    private final UserRepository userRepository;

    public boolean isOwner(Long loanId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName(); // Get email of current authenticated user

        System.out.println("Checking ownership for loanId: " + loanId + " by user: " + userEmail); // DEBUG LOG 1

        // Find the authenticated user
        Optional<User> currentUser = userRepository.findByEmail(userEmail);
        if (currentUser.isEmpty()) {
            System.out.println("Current user not found for email: " + userEmail); // DEBUG LOG 2
            return false; // User not found, can't be owner
        }
        User actualCurrentUser = currentUser.get();
        System.out.println("Authenticated user ID: " + actualCurrentUser.getId()); // DEBUG LOG 3

        // Find the loan application
        return loanApplicationRepository.findById(loanId)
                .map(loan -> {
                    boolean isOwner = loan.getUser().getId().equals(actualCurrentUser.getId());
                    System.out.println("Loan userId: " + loan.getUser().getId() + ", Authenticated userId: " + actualCurrentUser.getId() + ", Is Owner: " + isOwner); // DEBUG LOG 4
                    return isOwner;
                })
                .orElseGet(() -> {
                    System.out.println("Loan application not found for ID: " + loanId); // DEBUG LOG 5
                    return false; // Loan not found
                });
    }
}
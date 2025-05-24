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

    public boolean isOwner(Long loanId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        Optional<User> currentUser = userRepository.findByEmail(userEmail);
        if (currentUser.isEmpty()){
            return false;
        }

        return loanApplicationRepository.findById(loanId)
                .map(loan -> loan.getUser().equals(currentUser.get().getId()))
                .orElse(false);
    }
}

package com.example.lone.loan.repository;

import com.example.lone.loan.model.LoanApplication;
import com.example.lone.loan.model.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanApplicationRepository extends JpaRepository<LoanApplication, Long> {

    List<LoanApplication> findByStatus(LoanStatus status);
    List<LoanApplication> findByUserEmail(String email);
}

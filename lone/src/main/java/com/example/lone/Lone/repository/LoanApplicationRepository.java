package com.example.lone.Lone.repository;

import com.example.lone.Lone.model.LoanApplication;
import com.example.lone.Lone.model.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanApplicationRepository extends JpaRepository<LoanApplication, Long> {

    List<LoanApplication> findByStatus(LoanStatus status);
    List<LoanApplication> findByUserEmail(String email);
}

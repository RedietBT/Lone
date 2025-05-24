package com.example.lone.Lone.repository;

import com.example.lone.Lone.model.LoanApplication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanApplicationRepository extends JpaRepository<LoanApplication, Long> {
}

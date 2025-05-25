package com.example.lone.loan.model;

import com.example.lone.userAuth.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "loan_application")
public class LoanApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private BigDecimal loanAmount;

    @Column(nullable = false)
    private String loanType;

    @Column(nullable = false)
    private Integer durationMonths;

    @Column(nullable = false, length = 500)
    private String purpose;

    @Column(nullable = false)
    private BigDecimal annualIncome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanStatus status;

    private LocalDate loanStartDate;
    private BigDecimal monthlyEmi;
    @Lob
    @Column(length = 4000)
    private String repaymentSchedule;
    @Column(length = 500)
    private String adminRemarks;
}

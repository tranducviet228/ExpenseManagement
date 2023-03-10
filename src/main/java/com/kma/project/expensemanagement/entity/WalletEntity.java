package com.kma.project.expensemanagement.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "wallets")
public class WalletEntity extends BaseEntity {

    @Column(name = "account_balance")
    private Long accountBalance;

    @Column(name = "name")
    private String name;

    @Column(name = "account_type")
    private String accountType;

    @Column(name = "currency")
    private String currency;

    @Column(name = "description")
    private String description;

    @Column(name = "is_report")
    private boolean isReport;

//    @Column(name = "created_at", nullable = false)
//    private LocalDateTime createdAt;
//
//    @Column(name = "updated_at", nullable = false)
//    private LocalDateTime updatedAt;
//
//    @Column(name = "created_by")
//    private Long createdBy;
//
//    @Column(name = "updated_by")
//    private Long updatedBy;

    // Constructor, getters and setters
}
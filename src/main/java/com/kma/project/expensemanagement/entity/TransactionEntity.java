package com.kma.project.expensemanagement.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class TransactionEntity extends BaseEntity {

    @Column(name = "amount")
    private Long amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private CategoryEntity category;

    @Column(name = "category_name")
    private String categoryName;

    @Column(name = "description")
    private String description;

    @Column(name = "arise_date")
    private LocalDateTime ariseDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id")
    private WalletEntity wallet;

    @Column(name = "wallet_name")
    private String walletName;

    @Column(name = "is_report")
    private boolean isReport;

    @Column(name = "transaction_type")
    private String transactionType;

    @Lob
    @Column(name = "image")
    private byte[] image;

}
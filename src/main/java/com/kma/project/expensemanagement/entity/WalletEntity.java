package com.kma.project.expensemanagement.entity;

import com.kma.project.expensemanagement.enums.RoleOfGroup;
import com.kma.project.expensemanagement.enums.ScopeType;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Data
@Table(name = "wallets")
public class WalletEntity extends BaseEntity {

    @Column(name = "account_balance")
    private BigDecimal accountBalance;

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

    @Column(name = "group_id")
    private Long groupId;

    @Enumerated(EnumType.STRING)
    private ScopeType scopeType;
}
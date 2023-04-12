package com.kma.project.expensemanagement.entity;

import com.vladmihalcea.hibernate.type.array.StringArrayType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "expense_limits")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@TypeDefs(

        {
                @TypeDef(
                        name = "string-array",
                        typeClass = StringArrayType.class
                )
        }
)
@DynamicUpdate
public class ExpenseLimitEntity extends BaseEntity {

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "actual_amount")
    private BigDecimal actualAmount;

    @Column(name = "limit_name")
    private String limitName;

    @Column(name = "category_ids", columnDefinition = "varchar[]")
    @Type(type = "string-array")
    private String[] categoryIds;

    @Column(name = "wallet_ids", columnDefinition = "varchar[]")
    @Type(type = "string-array")
    private String[] walletIds;

    @Column(name = "from_date")
    private LocalDate fromDate;

    @Column(name = "to_date")
    private LocalDate toDate;


}

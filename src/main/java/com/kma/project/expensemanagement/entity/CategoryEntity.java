package com.kma.project.expensemanagement.entity;

import com.kma.project.expensemanagement.enums.CategoryType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Getter
@Setter
@Entity
@DynamicUpdate
@Table(name = "categories")
public class CategoryEntity extends BaseEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "description")
    private String description;

    @Column(name = "is_pay")
    private boolean isPay;

    @Column(name = "logo_image_id")
    private Long logoImageID;

    @Enumerated(EnumType.STRING)
    @Column(name = "category_type")
    private CategoryType categoryType;
}
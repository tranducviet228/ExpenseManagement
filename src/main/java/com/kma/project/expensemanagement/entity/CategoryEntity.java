package com.kma.project.expensemanagement.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
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

    @Column(name = "logo_image")
    private String logoImage;

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

}
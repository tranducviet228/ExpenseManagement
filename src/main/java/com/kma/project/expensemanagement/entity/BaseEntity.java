package com.kma.project.expensemanagement.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime createdAt;
    private LocalDateTime updateAt;
    private Long createdBy;
    private Long updateBy;

    public BaseEntity() {
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updateAt = LocalDateTime.now();
//        this.createdBy =
    }

    @PreUpdate
    public void preUpdate() {
        this.updateAt = LocalDateTime.now();
        //
    }
}

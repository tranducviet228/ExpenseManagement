package com.kma.project.expensemanagement.entity;

import com.kma.project.expensemanagement.enums.RoleOfGroup;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@DynamicUpdate
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Table(name = "group_members")
public class GroupMemberEntity extends BaseEntity {

    @Column(name = "group_id")
    private Long groupId;

    @Column(name = "user_id")
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "group_role")
    private RoleOfGroup roleOfGroup;
}
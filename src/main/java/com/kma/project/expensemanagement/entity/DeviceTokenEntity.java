package com.kma.project.expensemanagement.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "device_tokens")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class DeviceTokenEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String token;
}

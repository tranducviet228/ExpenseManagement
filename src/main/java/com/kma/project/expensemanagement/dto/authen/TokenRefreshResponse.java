package com.kma.project.expensemanagement.dto.authen;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TokenRefreshResponse {

    private String accessToken;

    private String refreshToken;

    private String expiredAccessDate;

}

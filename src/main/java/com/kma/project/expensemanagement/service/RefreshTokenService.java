package com.kma.project.expensemanagement.service;

import com.kma.project.expensemanagement.dto.request.TokenRefreshRequest;
import com.kma.project.expensemanagement.dto.response.TokenRefreshResponse;
import com.kma.project.expensemanagement.entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenService {

    Optional<RefreshToken> findByToken(String token);

    RefreshToken createRefreshToken(String username);

    RefreshToken verifyExpiration(RefreshToken token);

    TokenRefreshResponse refreshToken(TokenRefreshRequest refreshRequest);

}

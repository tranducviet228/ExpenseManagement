package com.kma.project.expensemanagement.service;

import com.kma.project.expensemanagement.dto.request.*;
import com.kma.project.expensemanagement.exception.AppResponseDto;

public interface UserService {

    AppResponseDto signUp(SignUpRequest signupRequest);

    AppResponseDto signIn(LoginRequest loginRequest);

    void verifyOtp(OtpRequestDto otpRequestDto);

    void createNewPassword(NewPasswordRequestDto newPasswordRequestDto);

    void changePassword(ChangePasswordRequestDto changePasswordRequestDto);

}

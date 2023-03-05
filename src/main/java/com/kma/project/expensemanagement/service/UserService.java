package com.kma.project.expensemanagement.service;

import com.kma.project.expensemanagement.dto.request.*;
import com.kma.project.expensemanagement.dto.response.JwtResponse;

public interface UserService {

    void signUp(SignUpRequest signupRequest);

    JwtResponse signIn(LoginRequest loginRequest);

    void verifyOtp(OtpRequestDto otpRequestDto);

    void createNewPassword(NewPasswordRequestDto newPasswordRequestDto);

    void changePassword(ChangePasswordRequestDto changePasswordRequestDto);

}

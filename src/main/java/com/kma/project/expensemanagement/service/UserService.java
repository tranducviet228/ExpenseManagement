package com.kma.project.expensemanagement.service;

import com.kma.project.expensemanagement.dto.authen.*;
import com.kma.project.expensemanagement.dto.request.UserInputDto;
import com.kma.project.expensemanagement.dto.response.PageResponse;
import com.kma.project.expensemanagement.dto.response.UserOutputDto;
import com.kma.project.expensemanagement.exception.AppResponseDto;

public interface UserService {

    AppResponseDto signUp(SignUpRequest signupRequest);

    AppResponseDto signIn(LoginRequest loginRequest);

    void verifyOtp(OtpRequestDto otpRequestDto);

    void createNewPassword(NewPasswordRequestDto newPasswordRequestDto);

    void changePassword(ChangePasswordRequestDto changePasswordRequestDto);

    PageResponse<UserOutputDto> getAllUser(Integer page, Integer size, String sort, String search);

    UserOutputDto updateUser(Long userId, UserInputDto dto);

    void delete(Long userId);

    UserOutputDto getDetailUser(Long userId);

}

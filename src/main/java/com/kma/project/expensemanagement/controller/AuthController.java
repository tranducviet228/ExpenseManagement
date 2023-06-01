package com.kma.project.expensemanagement.controller;

import com.kma.project.expensemanagement.dto.authen.*;
import com.kma.project.expensemanagement.exception.AppResponseDto;
import com.kma.project.expensemanagement.service.MailService;
import com.kma.project.expensemanagement.service.RefreshTokenService;
import com.kma.project.expensemanagement.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;

//@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
@Api(tags = "Quản lí tài khoản")
public class AuthController {

    @Autowired
    RefreshTokenService refreshTokenService;

    @Autowired
    MailService mailService;

    @Autowired
    UserService userService;


    @ApiOperation(value = "Đăng nhập")
    @PostMapping("/sign-in")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.signIn(request));
    }

    @ApiOperation(value = "Đăng kí")
    @PostMapping("/sign-up")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest request) {
        return ResponseEntity.ok(userService.signUp(request));
    }

    @ApiOperation(value = "Refresh token")
    @PostMapping("/refresh-token")
    public AppResponseDto<?> refreshtoken(@Valid @RequestBody TokenRefreshRequest request) {
        return AppResponseDto.builder().httpStatus(200).data(refreshTokenService.refreshToken(request)).build();
    }

    @ApiOperation(value = "Quên mật khẩu")
    @PostMapping("/forgot-password")
    public AppResponseDto forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        try {
            mailService.sendMail(request.getEmail());
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return AppResponseDto.builder().httpStatus(200).message("OTP sent successfully").build();
    }

    @ApiOperation(value = "Gửi otp và kiểm tra otp")
    @PostMapping("/send-otp")
    public AppResponseDto sendOtp(@Valid @RequestBody OtpRequestDto request) {
        userService.verifyOtp(request);
        return AppResponseDto.builder().httpStatus(200).message("verify OTP successfully").build();
    }

    @ApiOperation(value = "Tạo mật khẩu mới")
    @PostMapping("/new-password")
    public AppResponseDto createNewPassword(@Valid @RequestBody NewPasswordRequestDto request) {
        userService.createNewPassword(request);
        return AppResponseDto.builder().httpStatus(200).message("Create new password successfully").build();
    }

    @ApiOperation(value = "Thay đổi mật khẩu")
    @PostMapping("/change-password")
    public AppResponseDto changePassword(@Valid @RequestBody ChangePasswordRequestDto request) {
        userService.changePassword(request);
        return AppResponseDto.builder().httpStatus(200).message("Change password successfully").build();
    }

}

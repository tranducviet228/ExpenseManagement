package com.kma.project.expensemanagement.controller;

import com.kma.project.expensemanagement.dto.authen.*;
import com.kma.project.expensemanagement.service.MailService;
import com.kma.project.expensemanagement.service.RefreshTokenService;
import com.kma.project.expensemanagement.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
@Api(tags = "Quản lí tài khoản, đăng nhập, đăng kí")
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
    public ResponseEntity<?> refreshtoken(@Valid @RequestBody TokenRefreshRequest request) {
        return ResponseEntity.ok(refreshTokenService.refreshToken(request));
    }

    @ApiOperation(value = "Quên mật khẩu")
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        try {
            mailService.sendMail(request.getEmail());
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok("OTP sent successfully");
    }

    @ApiOperation(value = "Gửi otp và kiểm tra otp")
    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(@Valid @RequestBody OtpRequestDto request) {
        userService.verifyOtp(request);
        return ResponseEntity.ok("verify OTP successfully");
    }

    @ApiOperation(value = "Tạo mật khẩu mới")
    @PostMapping("/new-password")
    public ResponseEntity<String> createNewPassword(@Valid @RequestBody NewPasswordRequestDto request) {
        userService.createNewPassword(request);
        return ResponseEntity.ok("Change password successfully");
    }

    @ApiOperation(value = "Thay đổi mật khẩu")
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordRequestDto request) {
        userService.changePassword(request);
        return ResponseEntity.ok("Change password successfully");
    }

}

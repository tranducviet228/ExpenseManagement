package com.kma.project.expensemanagement.service.impl;

import com.kma.project.expensemanagement.dto.request.*;
import com.kma.project.expensemanagement.dto.response.JwtResponse;
import com.kma.project.expensemanagement.entity.RefreshToken;
import com.kma.project.expensemanagement.entity.RoleEntity;
import com.kma.project.expensemanagement.entity.UserEntity;
import com.kma.project.expensemanagement.enums.ERole;
import com.kma.project.expensemanagement.exception.AppException;
import com.kma.project.expensemanagement.repository.RoleRepository;
import com.kma.project.expensemanagement.repository.UserRepository;
import com.kma.project.expensemanagement.security.jwt.JwtUtils;
import com.kma.project.expensemanagement.security.services.UserDetailsImpl;
import com.kma.project.expensemanagement.service.RefreshTokenService;
import com.kma.project.expensemanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    RefreshTokenService refreshTokenService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;

    @Transactional
    @Override
    public void signUp(SignUpRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw AppException.builder().errorCodes(Collections.singletonList("error.username-exist")).build();
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw AppException.builder().errorCodes(Collections.singletonList("error.email-exist")).build();
        }
        // Create new user's account
        UserEntity user = new UserEntity(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                passwordEncoder.encode(signUpRequest.getPassword()));

        Set<RoleEntity> roles = new HashSet<>();
        RoleEntity userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> AppException.builder().errorCodes(Collections.singletonList("error.role-not-exist")).build());
        roles.add(userRole);
        user.setRoles(roles);
        userRepository.save(user);
    }

    @Transactional
    @Override
    public JwtResponse signIn(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = "Bearer " + jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getUsername());

        return JwtResponse.builder()
                .refreshToken(refreshToken.getToken())
                .id(userDetails.getId())
                .username(userDetails.getUsername())
                .email(userDetails.getEmail())
                .accessToken(jwt).build();
    }

    @Override
    public void verifyOtp(OtpRequestDto otpRequestDto) {
        UserEntity userEntity = userRepository.findByEmail(otpRequestDto.getEmail())
                .orElseThrow(() -> AppException.builder().errorCodes(Collections.singletonList("error.email-not-found")).build());

        if (!otpRequestDto.getOtp().equals(userEntity.getOtp())) {
            throw AppException.builder().errorCodes(Collections.singletonList("error.otp-not-valid")).build();
        }
    }

    @Transactional
    @Override
    public void createNewPassword(NewPasswordRequestDto newPasswordRequestDto) {
        UserEntity userEntity = userRepository.findByEmail(newPasswordRequestDto.getEmail())
                .orElseThrow(() -> AppException.builder().errorCodes(Collections.singletonList("error.email-not-found")).build());
        if (!newPasswordRequestDto.getPassword().equals(newPasswordRequestDto.getConfirmPassword())) {
            throw AppException.builder().errorCodes(Collections.singletonList("error.password-not-correct")).build();
        }
        userEntity.setPassword(passwordEncoder.encode(newPasswordRequestDto.getPassword()));
        userRepository.save(userEntity);
    }

    @Transactional
    @Override
    public void changePassword(ChangePasswordRequestDto changePasswordRequestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        if (!changePasswordRequestDto.getPassword().equals(changePasswordRequestDto.getConfirmPassword())) {
            throw AppException.builder().errorCodes(Collections.singletonList("error.password-not-correct")).build();
        }
        if (!passwordEncoder.matches(changePasswordRequestDto.getCurrentPassword(), userDetails.getPassword())) {
            throw AppException.builder().errorCodes(Collections.singletonList("error.password-not-correct")).build();
        }
        UserEntity userEntity = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> AppException.builder().errorCodes(Collections.singletonList("error.username-not-found")).build());
        userEntity.setPassword(passwordEncoder.encode(changePasswordRequestDto.getPassword()));
        userRepository.save(userEntity);
    }
}

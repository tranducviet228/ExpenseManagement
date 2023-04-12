package com.kma.project.expensemanagement.service.impl;

import com.kma.project.expensemanagement.dto.authen.*;
import com.kma.project.expensemanagement.dto.request.UserUpdateDto;
import com.kma.project.expensemanagement.dto.response.PageResponse;
import com.kma.project.expensemanagement.dto.response.UserOutputDto;
import com.kma.project.expensemanagement.entity.RefreshToken;
import com.kma.project.expensemanagement.entity.RoleEntity;
import com.kma.project.expensemanagement.entity.UserEntity;
import com.kma.project.expensemanagement.enums.ERole;
import com.kma.project.expensemanagement.exception.AppException;
import com.kma.project.expensemanagement.exception.AppResponseDto;
import com.kma.project.expensemanagement.mapper.UserMapper;
import com.kma.project.expensemanagement.repository.RoleRepository;
import com.kma.project.expensemanagement.repository.UserRepository;
import com.kma.project.expensemanagement.security.jwt.JwtUtils;
import com.kma.project.expensemanagement.security.services.UserDetailsImpl;
import com.kma.project.expensemanagement.service.RefreshTokenService;
import com.kma.project.expensemanagement.service.UserService;
import com.kma.project.expensemanagement.utils.PageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
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

    @Autowired
    private UserMapper userMapper;

    @Value("${viet.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    @Transactional
    @Override
    public AppResponseDto signUp(SignUpRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw AppException.builder().errorCodes(Collections.singletonList("error.username-exist")).build();
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw AppException.builder().errorCodes(Collections.singletonList("error.email-exist")).build();
        }
        // Create new user's account
        UserEntity user = new UserEntity(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                passwordEncoder.encode(signUpRequest.getPassword()),
                signUpRequest.getFullName(),
                signUpRequest.getPhone());

        Set<RoleEntity> roles = new HashSet<>();
        RoleEntity userRole;
        if (!signUpRequest.getRoles().isEmpty()) {
            for (String item : signUpRequest.getRoles()) {
                userRole = roleRepository.findByName(ERole.valueOf(item))
                        .orElseThrow(() -> AppException.builder().errorCodes(Collections.singletonList("error.role-not-exist")).build());
                roles.add(userRole);
            }
        } else {
            userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> AppException.builder().errorCodes(Collections.singletonList("error.role-not-exist")).build());
            roles.add(userRole);
        }
        user.setRoles(roles);
        userRepository.save(user);
        return AppResponseDto.builder().httpStatus(200).message("Đăng kí thành công").build();
    }

    @Transactional
    @Override
    public AppResponseDto signIn(LoginRequest loginRequest) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        } catch (Exception e) {
            throw AppException.builder().errorCodes(Collections.singletonList("error.login-fail")).build();
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = "Bearer " + jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getUsername());
        Date expiredDate = new Date((new Date()).getTime() + jwtExpirationMs);
        LocalDateTime localDate = expiredDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        JwtResponse jwtResponse = JwtResponse.builder()
                .refreshToken(refreshToken.getToken())
                .id(userDetails.getId())
                .username(userDetails.getUsername())
                .email(userDetails.getEmail())
                .accessToken(jwt)
                .expiredAccessDate(localDate.toString())
                .expiredRefreshDate(refreshToken.getExpiryDate().toString())
                .build();
        return AppResponseDto.builder().data(jwtResponse).httpStatus(200).message("Đăng nhập thành công").build();

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

    @Override
    public PageResponse<UserOutputDto> getAllUser(Integer page, Integer size, String sort, String search) {
        Pageable pageable = PageUtils.customPageable(page, size, sort);
        search = PageUtils.buildSearch(search);
        Page<UserEntity> pageUser = userRepository.findAllByEmailLikeIgnoreCaseOrUsernameLikeIgnoreCase(pageable, search, search);
        return PageUtils.formatPageResponse(pageUser.map(userEntity -> {
            return userMapper.convertToDto(userEntity);
        }));
    }

    @Transactional
    @Override
    public UserOutputDto updateUser(Long userId, UserUpdateDto dto) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> AppException.builder().errorCodes(Collections.singletonList("error.entity-not-found")).build());
        if (!userEntity.getUsername().equals(dto.getUsername()) && dto.getUsername() != null) {
            if (userRepository.existsByUsername(dto.getUsername())) {
                throw AppException.builder().errorCodes(Collections.singletonList("error.username-exist")).build();
            }
            userEntity.setUsername(dto.getUsername());
        }

        if (!userEntity.getEmail().equals(dto.getEmail()) && dto.getEmail() != null) {
            if (userRepository.existsByEmail(dto.getEmail())) {
                throw AppException.builder().errorCodes(Collections.singletonList("error.email-exist")).build();
            }
            userEntity.setEmail(dto.getEmail());
        }
        if (dto.getPassword() != null && dto.getConfirmPassword() != null) {
            if (!dto.getPassword().equals(dto.getConfirmPassword())) {
                throw AppException.builder().errorCodes(Collections.singletonList("error.password-not-correct")).build();
            }
            userEntity.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        Set<RoleEntity> roles = new HashSet<>();
        if (!dto.getRoles().isEmpty()) {
            for (String item : dto.getRoles()) {
                RoleEntity userRole = roleRepository.findByName(ERole.valueOf(item))
                        .orElseThrow(() -> AppException.builder().errorCodes(Collections.singletonList("error.role-not-exist")).build());
                roles.add(userRole);
            }
        }
        userEntity.setRoles(roles);
        userRepository.save(userEntity);
        return userMapper.convertToDto(userEntity);
    }

    @Transactional
    @Override
    public void delete(Long userId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> AppException.builder().errorCodes(Collections.singletonList("error.entity-not-found")).build());
        userRepository.delete(userEntity);
    }

    @Override
    public UserOutputDto getDetailUser(Long userId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> AppException.builder().errorCodes(Collections.singletonList("error.entity-not-found")).build());
        return userMapper.convertToDto(userEntity);
    }
}

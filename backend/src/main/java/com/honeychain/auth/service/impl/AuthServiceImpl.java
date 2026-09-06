package com.honeychain.auth.service.impl;

import com.honeychain.auth.dto.LoginRequest;
import com.honeychain.auth.dto.LoginResponse;
import com.honeychain.auth.dto.SendOtpRequest;
import com.honeychain.auth.dto.VerifyOtpRequest;
import com.honeychain.auth.service.AuthService;
import com.honeychain.auth.service.OtpService;
import com.honeychain.common.dto.ApiResponse;
import com.honeychain.common.exception.ApiException;
import com.honeychain.common.exception.BadRequestException;
import com.honeychain.common.exception.UnauthorizedException;
import com.honeychain.security.jwt.JwtService;
import com.honeychain.user.entity.Role;
import com.honeychain.user.entity.User;
import com.honeychain.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final OtpService otpService;

    public AuthServiceImpl(UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            OtpService otpService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.otpService = otpService;
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        logger.info("Processing login request for phone/username: {}", request.getPhoneNumber());

        User user = userRepository.findByPhoneNumber(request.getPhoneNumber().trim())
                .orElseThrow(() -> new BadRequestException("Invalid credentials"));

        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new UnauthorizedException("User account is inactive. Please contact support.");
        }

        if (user.getPassword() == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid credentials");
        }

        String token = jwtService.generateToken(user.getPhoneNumber(), user.getRole().name());
        return LoginResponse.of(token, user.getRole().name(), "Login successful");
    }

    @Override
    public ApiResponse<Void> sendOtp(SendOtpRequest request) {
        String phone = request.getPhoneNumber().trim();
        logger.info("Sending OTP for phone: {}", phone);

        otpService.generateAndSendOtp(phone);
        return ApiResponse.success("OTP sent successfully to " + phone);
    }

    @Override
    @Transactional
    public LoginResponse verifyOtp(VerifyOtpRequest request) {
        String phone = request.getPhoneNumber().trim();
        String otp = request.getOtp().trim();
        logger.info("Verifying OTP for phone: {}", phone);

        boolean isValid = otpService.verifyOtp(phone, otp);
        if (!isValid) {
            throw new BadRequestException("Invalid or expired OTP");
        }

        User user = userRepository.findByPhoneNumber(phone)
                .orElseGet(() -> {
                    Role role = request.getRole() != null ? request.getRole() : Role.CUSTOMER;
                    logger.info("Auto-registering user with phone {} and role {}", phone, role);
                    User newUser = new User(phone, null, role, true);
                    return userRepository.save(newUser);
                });

        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new UnauthorizedException("User account is inactive. Please contact support.");
        }

        String token = jwtService.generateToken(user.getPhoneNumber(), user.getRole().name());
        return LoginResponse.of(token, user.getRole().name(), "OTP verified successfully");
    }
}

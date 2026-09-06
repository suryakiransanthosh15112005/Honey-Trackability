package com.honeychain.auth.service;

import com.honeychain.auth.dto.LoginRequest;
import com.honeychain.auth.dto.LoginResponse;
import com.honeychain.auth.dto.SendOtpRequest;
import com.honeychain.auth.dto.VerifyOtpRequest;
import com.honeychain.common.dto.ApiResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    ApiResponse<Void> sendOtp(SendOtpRequest request);

    LoginResponse verifyOtp(VerifyOtpRequest request);
}

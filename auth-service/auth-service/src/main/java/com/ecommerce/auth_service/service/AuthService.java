package com.ecommerce.auth_service.service;

import com.ecommerce.auth_service.dto.LoginRequest;
import com.ecommerce.auth_service.dto.LoginResponse;
import com.ecommerce.auth_service.dto.RegistrationRequest;
import com.ecommerce.auth_service.dto.RegistrationResponse;

public interface AuthService {
    RegistrationResponse registerUser(RegistrationRequest request);

    LoginResponse login(LoginRequest request);
}

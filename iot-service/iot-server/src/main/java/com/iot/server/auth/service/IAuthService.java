package com.iot.server.auth.service;

import com.iot.server.auth.dto.LoginRequest;
import com.iot.server.auth.model.AuthenticatedUser;

public interface IAuthService {

    AuthenticatedUser authenticate(LoginRequest request);
}

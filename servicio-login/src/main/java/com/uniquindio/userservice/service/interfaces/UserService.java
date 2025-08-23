package com.uniquindio.userservice.service;

import com.uniquindio.userservice.dto.UserResponse;
import com.uniquindio.userservice.dto.UserRegistration;

public interface UserService {

    UserResponse registerUser(UserRegistration user);

}

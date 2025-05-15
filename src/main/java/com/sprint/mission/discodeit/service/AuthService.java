package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.dto.response.UserFoundResponse;
import com.sprint.mission.discodeit.dto.response.UserResponse;

public interface AuthService {

    UserFoundResponse login(LoginRequest request);
}

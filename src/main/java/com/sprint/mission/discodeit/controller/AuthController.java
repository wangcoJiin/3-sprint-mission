package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@RestController
public class AuthController implements AuthApi {

    private final AuthService authService;

    // 로그인
    @PostMapping(path = "login")
    public ResponseEntity<UserDto> login(
            @Valid @RequestBody LoginRequest request
    ){
        UserDto response = authService.login(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    // CSRF 토큰을 발급하는 API
    @GetMapping(path = "/csrf-token")
    public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
        /*
         * Spring Security는 POST, PUT, DELETE 요청에 대해서만 CSRF 검증을 함
         * 그래서 일반적인 GET / 요청만으로는 CSRF 토큰이 초기화되지 않을 수 있음
         * 따라서 명시적으로 csrfToken.getToken()을 호출해서 강제로 토큰을 초기화하는 트리거를 만들어줌
         */
        String tokenValue = csrfToken.getToken();

        log.debug("[AuthController] CSRF 토큰 요청: {}", tokenValue);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}

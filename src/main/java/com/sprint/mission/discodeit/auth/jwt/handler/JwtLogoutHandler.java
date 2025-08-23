package com.sprint.mission.discodeit.auth.jwt.handler;

import com.sprint.mission.discodeit.auth.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.auth.jwt.registry.JwtRegistry;
import com.sprint.mission.discodeit.auth.service.DiscodeitUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

    private final JwtTokenProvider tokenProvider;
    private final JwtRegistry jwtRegistry;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) {
        log.info("[JwtLogoutHandler] 로그아웃 처리 시작");
        try {
            // 레지스트리에서 사용자의 활성 JWT 무효화
            if (authentication != null && authentication.getPrincipal() instanceof DiscodeitUserDetails userDetails) {
                UUID userId = userDetails.getId();
                jwtRegistry.invalidateJwtInformationByUserId(userId);
                log.debug("[JwtLogoutHandler] 레지스트리 무효화 완료 - userId={}", userId);
            }

        } catch (Exception e) {
            log.warn("[JwtLogoutHandler] 처리 중 예외: {}", e.getMessage(), e);
        }
        finally {
            // 쿠키 만료
            tokenProvider.expireRefreshCookie(response);
            // 컨텍스트 정리
            SecurityContextHolder.clearContext();
            log.debug("[JwtLogoutHandler] 리프레시 토큰 쿠키 만료, SecurityContext 초기화");
        }
    }
}

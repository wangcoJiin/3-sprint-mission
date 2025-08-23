package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.auth.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.auth.jwt.dto.JwtDto;
import com.sprint.mission.discodeit.auth.jwt.registry.JwtInformation;
import com.sprint.mission.discodeit.auth.jwt.registry.JwtRegistry;
import com.sprint.mission.discodeit.auth.service.DiscodeitUserDetails;
import com.sprint.mission.discodeit.auth.service.DiscodeitUserDetailsService;
import com.sprint.mission.discodeit.auth.util.OnlineStatusUtil;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.auth.InvalidTokenException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final SessionRegistry sessionRegistry;
    private final OnlineStatusUtil onlineStatusUtil;
    private final JwtTokenProvider jwtTokenProvider;
    private final DiscodeitUserDetailsService userDetailsService;
    private final JwtRegistry jwtRegistry;


    // 유저의 권한을 수정
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public UserDto updateUserRole(UserRoleUpdateRequest request) {
        User user = userRepository.findById(request.userId())
            .orElseThrow(() -> new UserNotFoundException(request.userId()));

        Role oldRole = user.getRole();

        log.info("[BasicAuthService] {} 님의 권한 변경을 시도합니다.", user.getUsername());

        if (!oldRole.equals(request.newRole())){
            user.updateRole(request.newRole());
            User updatedUser = userRepository.save(user);

            // 권한이 변경된 유저의 세션을 무효화 하기
            invalidateUserSessions(user.getUsername());
            log.info("[BasicAuthService] {} 님의 모든 세션을 무효화하고 권한을 변경했습니다.", user.getUsername());

            return userMapper.toDto(updatedUser, false);
        }

        log.info("[BasicAuthService] 기존 권한과 동일합니다.");
        boolean isOnline = onlineStatusUtil.isOnlineUser(user.getId());
        return userMapper.toDto(user, isOnline);
    }

    // 수정된 유저의 세션을 무효화
    private void invalidateUserSessions(String username) {
        try {
            log.info("[BasicAuthService] {} 님의 세션 무효화 시작.", username);

            // SessionRegistry에서 모든 주체(Principal) 조회
            List<Object> allPrincipals = sessionRegistry.getAllPrincipals();
            log.info("[UserService] 전체 로그인된 사용자 수: {} ", allPrincipals.size());

            // 해당 사용자의 모든 세션 정보 찾기
            for (Object principal : allPrincipals) {
                if (principal instanceof UserDetails userDetails) {
                    String principalName = userDetails.getUsername();
                    log.info("[BasicAuthService] 확인중인 사용자: {}", principalName);

                    if (username.equals(principalName)) {
                        // 해당 사용자의 모든 세션 정보 가져오기
                        List<SessionInformation> sessionInformations = sessionRegistry.getAllSessions(
                            principal, false);
                        log.info("[BasicAuthService] 해당 사용자의 활성 세션 수: {}", sessionInformations.size());

                        // 모든 세션 무효화
                        for (SessionInformation session : sessionInformations) {
                            session.expireNow();
                            log.debug("[BasicAuthService] 세션 만료 처리 완료 - sessionId={}", session.getSessionId());
                        }
                        break;
                    }
                } else {
                    // principal 타입이 UserDetails가 아닐 수 있으므로디버그 로깅
                    log.debug("[BasicAuthService] UserDetails가 아닌 principal 발견: {}", principal.getClass().getName());
                }
            }
        }
        catch (Exception e) {
            log.error("[BasicAuthService] 세션 무효화 중 오류 발생: {}", e.getMessage());
            log.error("[BasicAuthService] 오류 설명 메시지", e);
            // 세션 무효화 실패는 권한 변경 자체를 실패시키지 않음 (DB 변경은 유지)
        }
    }

    @Override
    public JwtDto refreshToken(String refreshToken, HttpServletResponse response) {

        if (refreshToken == null) {
            throw new InvalidTokenException("리프레시 토큰이 없습니다.");
        }

        // 레지스트리 활성 리프레시 인지?
        if (!jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken)) {
            log.debug("[AuthService] 레지스트리에 없는 리프레스 토큰 입니다.");
            jwtTokenProvider.expireRefreshCookie(response);
            throw new InvalidTokenException("유효하지 않은 리프레시 토큰입니다.");
        }

        // 유효성 확인
        if (!jwtTokenProvider.validateRefreshToken(refreshToken)) {
            log.debug("[AuthService] 리프레시 토큰 유효성 확인에 실패했습니다.");
            jwtTokenProvider.expireRefreshCookie(response);
            throw new InvalidTokenException("리프레시 토큰이 유효하지 않습니다.");
        }

        try {
            // username 추출
            String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
            // 사용자 로드
            DiscodeitUserDetails userDetails = (DiscodeitUserDetails) userDetailsService.loadUserByUsername(username);
            UserDto userDto = userDetails.getUserDto();

            // 새 토큰 발급
            String newAccessToken = jwtTokenProvider.generateAccessToken(userDetails);
            String newRefreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

            JwtInformation newJwtInformation = new JwtInformation(
                userDto,
                newAccessToken,
                newRefreshToken
            );

            // 레지스트리 회전
            jwtRegistry.rotateJwtInformation(
                refreshToken,
                newJwtInformation
            );

            // 리프레시 쿠키 교체
            jwtTokenProvider.addRefreshCookie(response, newRefreshToken);

            // 응답 바디 구성
            log.debug("[AuthService] 토큰 재발급 완료 - username={}", username);
            return new JwtDto(userDto, newAccessToken);

        } catch (Exception e) {
            log.error("[AuthService] 토큰 생성 중 오류", e);
            jwtTokenProvider.expireRefreshCookie(response);
            throw new InvalidTokenException("토큰 생성 중 오류가 발생했습니다.");
        }
    }
}


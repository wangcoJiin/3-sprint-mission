package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import java.util.List;
import java.util.logging.Logger;
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

    private static final Logger logger = Logger.getLogger(BasicAuthService.class.getName()); // 필드로 Logger 선언

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final SessionRegistry sessionRegistry;

    // 유저의 권한을 수정
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public UserDto updateUserRole(UserRoleUpdateRequest request) {
        User user = userRepository.findById(request.userId())
            .orElseThrow(() -> new UserNotFoundException(request.userId()));

        Role oldRole = user.getRole();

        log.info("[BasicUserService] {} 님의 권한 변경을 시도합니다.", user.getUsername());

        if (!oldRole.equals(request.newRole())){
            user.updateRole(request.newRole());
            User updatedUser = userRepository.save(user);

            // 권한이 변경된 유저의 세션을 무효화 하기
            invalidateUserSessions(user.getUsername());
            log.info("[BasicUserService] {} 님의 모든 세션을 무효화하고 권한을 변경했습니다.", user.getUsername());

            return userMapper.toDto(updatedUser);
        }

        log.info("[BasicUserService] 기존 권한과 동일합니다.");
        return userMapper.toDto(user);
    }

    // 수정된 유저의 세션을 무효화
    private void invalidateUserSessions(String username) {
        try {
            log.info("[BasicUserService] {} 님의 세션 무효화 시작.", username);

            // SessionRegistry에서 모든 주체(Principal) 조회
            List<Object> allPrincipals = sessionRegistry.getAllPrincipals();
            log.info("[UserService] 전체 로그인된 사용자 수: {} ", allPrincipals.size());

            // 해당 사용자의 모든 세션 정보 찾기
            for (Object principal : allPrincipals) {
                log.info("[BasicUserService] Principal 실제 타입: {}", principal.getClass().getName());

                UserDetails userDetails = (UserDetails) principal;
                String principalName = userDetails.getUsername();

                log.info("[BasicUserService] 확인중인 사용자: {}", principalName);

                if (username.equals(principalName)) {
                    // 해당 사용자의 모든 세션 정보 가져오기
                    List<SessionInformation> sessionInformations = sessionRegistry.getAllSessions(
                        principal, false);
                    log.info("[BasicUserService] 해당 사용자의 활성 세션 수: {}", sessionInformations.size());

                    // 모든 세션 무효화
                    for (SessionInformation session : sessionInformations) {
                        session.expireNow();
                    }

                    break;
                }
            }
        }
        catch (Exception e) {
            log.error("[BasicUserService] 세션 무효화 중 오류 발생: {}", e.getMessage());
            log.error("[BasicUserService] 오류 설명 메시지", e);
            // 세션 무효화 실패는 권한 변경 자체를 실패시키지 않음 (DB 변경은 유지)
        }
    }

}


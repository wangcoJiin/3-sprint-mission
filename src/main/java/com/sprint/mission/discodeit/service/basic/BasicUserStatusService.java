package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import java.util.logging.Logger;

@RequiredArgsConstructor
@Service
public class BasicUserStatusService implements UserStatusService {

    private static final Logger logger = Logger.getLogger(BasicUserStatusService.class.getName()); // 필드로 Logger 선언


    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    // 상태 생성
    @Override
    public UserStatus createUserStatus(UserStatusCreateRequest request) {

        if (userRepository.findUserById(request.userId()).isEmpty()) {
            throw new NoSuchElementException("UserStatusService: 해당 유저가 존재하지 않습니다." + request.userId());
        }

        // 이미 UserStatus가 존재하는지 검사
        if (userStatusRepository.findStatus(request.userId()).isPresent()) {
            throw new IllegalArgumentException("UserStatusService: 이미 UserStatus가 존재합니다.");
        }

        // 새 UserStatus 생성
        Instant lastActiveAt = request.lastActiveAt();
        UserStatus userStatus = new UserStatus(request.userId(), lastActiveAt);

        return userStatusRepository.saveUserStatus(userStatus);
    }

    // 유저의 접속 상태 조회
    @Override
    public UserStatus findUserStatusById(UUID userId) {
        return userStatusRepository.findStatus(userId)
                .orElseThrow(
                () -> new NoSuchElementException("UserStatusService: 해당 유저가 존재하지 않습니다." + userId));
    }

    // 저장된 상태 전체 조회
    @Override
    public List<UserStatus> findAllStatus() {
        return userStatusRepository.findAllStatus().stream()
                .toList();
    }

    // 유저 상태 업데이트 (접속 시간을 기준으로)
    @Override
    public UserStatus updateUserStatus(UUID userId, UserStatusUpdateRequest request) {
        Instant newLastActiveAt = request.newLastActiveAt();

        UserStatus userStatus = userStatusRepository.findStatus(userId)
                .orElseThrow(
                        () -> new NoSuchElementException("UserStatusService: 해당 유저가 존재하지 않습니다." + userId));

        userStatus.updateStatus(newLastActiveAt);

        return userStatusRepository.saveUserStatus(userStatus);
    }

    // 상태 아이디로 삭제
    @Override
    public void deleteUserStatus(UUID statusId) {
        if((userStatusRepository.findById(statusId)).isEmpty()){
            throw new NoSuchElementException("UserStatusService: 해당하는 UserStatus가 없습니다. ");
        }
        userStatusRepository.deleteById(statusId);
    }

    //유저 아이디로 상태 삭제
    @Override
    public void deleteUserStatusByUserId(UUID userId) {

        if (userRepository.findUserById(userId).isEmpty()) {
            throw new NoSuchElementException("UserStatusService: 해당 유저가 존재하지 않습니다." + userId);
        }

        userStatusRepository.deleteUserStatus(userId);
    }
}

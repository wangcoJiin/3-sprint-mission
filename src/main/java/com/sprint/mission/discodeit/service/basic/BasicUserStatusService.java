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
    public UserStatus create(UserStatusCreateRequest request) {

        if (userRepository.findById(request.userId()).isEmpty()) {
            throw new NoSuchElementException("UserStatusService: 해당 유저가 존재하지 않습니다." + request.userId());
        }

        // 이미 UserStatus가 존재하는지 검사
        if (userStatusRepository.findByUserId(request.userId()).isPresent()) {
            throw new IllegalArgumentException("UserStatusService: 이미 UserStatus가 존재합니다.");
        }

        // 새 UserStatus 생성
        Instant lastActiveAt = request.lastActiveAt();
        UserStatus userStatus = new UserStatus(request.userId(), lastActiveAt);

        return userStatusRepository.save(userStatus);
    }

    // 접속 상태 조회
    @Override
    public UserStatus find(UUID userStatusId) {
        return userStatusRepository.findById(userStatusId)
                .orElseThrow(
                        () -> new NoSuchElementException("UserStatusService:  userStatus가 존재하지 않습니다. "+  userStatusId));
    }

    // 저장된 상태 전체 조회
    @Override
    public List<UserStatus> findAll() {
        return userStatusRepository.findAll().stream()
                .toList();
    }

    @Override
    public UserStatus update(UUID userStatusId, UserStatusUpdateRequest request) {
        Instant newLastActiveAt = request.newLastActiveAt();

        UserStatus userStatus = userStatusRepository.findById(userStatusId)
                .orElseThrow(
                        () -> new NoSuchElementException("UserStatusService:  userStatus가 존재하지 않습니다. "+  userStatusId));
        userStatus.update(newLastActiveAt);

        return userStatusRepository.save(userStatus);
    }

    // 유저 상태 업데이트 (접속 시간을 기준으로)
    @Override
    public UserStatus updateByUserId(UUID userId, UserStatusUpdateRequest request) {
        Instant newLastActiveAt = request.newLastActiveAt();

        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(
                        () -> new NoSuchElementException("UserStatusService: 해당 유저가 존재하지 않습니다." + userId));

        userStatus.update(newLastActiveAt);

        return userStatusRepository.save(userStatus);
    }

    // 상태 아이디로 삭제
    @Override
    public void delete(UUID statusId) {
        if((userStatusRepository.findById(statusId)).isEmpty()){
            throw new NoSuchElementException("UserStatusService: 해당하는 UserStatus가 없습니다. ");
        }
        userStatusRepository.deleteById(statusId);
    }
}

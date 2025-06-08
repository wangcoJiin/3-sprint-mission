package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;


@RequiredArgsConstructor
@Service
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserMapper userMapper;
    private final BinaryContentStorage binaryContentStorage;


    @Override
    @Transactional
    public UserDto create(UserCreateRequest request, Optional<BinaryContentCreateRequest> optionalProfileImage) {

        // 이메일 중복 검사
        if (userRepository.existsByEmail(request.email())){
            throw new IllegalArgumentException("UserService: " + request.email() + "은 이미 존재하는 이메일 입니다.");
        }

        // 이름 중복 검사
        if (userRepository.existsByUsername(request.username())){
            throw new IllegalArgumentException("UserService: " + request.username() + "은 이미 존재하는 이름 입니다.");
        }

        // 유저 생성
        User user = new User(
                request.username(),
                request.email(),
                request.password()
        );

        // 영속성 컨테이너 등록
        User savedUser = userRepository.save(user);

        // 프로필 이미지 처리 (기본 이미지 or 전달된 이미지)
        BinaryContent profile = optionalProfileImage
                .map(profileImage -> {
                    BinaryContent content = new BinaryContent(
                            profileImage.fileName(),
                            (long) profileImage.bytes().length,
                            profileImage.contentType()
                    );
                    binaryContentStorage.put(content.getId(), profileImage.bytes());
                    savedUser.updateProfile(content);

                    return binaryContentRepository.save(content);
                })
                .orElseGet(() -> {
                    try {
                        ClassPathResource resource = new ClassPathResource("static/images/default-avatar.png");
                        byte[] data = resource.getInputStream().readAllBytes();
                        BinaryContent defaultContent = new BinaryContent("default-avatar.png", (long) data.length, "image/png");
                        savedUser.updateProfile(defaultContent);

                        binaryContentStorage.put(defaultContent.getId(), data);

                        return binaryContentRepository.save(defaultContent);
                    } catch (IOException e) {
                        throw new RuntimeException("UserService: 기본 프로필 이미지 로드 실패", e);
                    }
                });

        // 유저 활동 상태 생성
        UserStatus userStatus = new UserStatus(savedUser, Instant.now());
        savedUser.setStatus(userStatus);

        return userMapper.toDto(savedUser);
    }

    // 아이디로 검색
    @Override
    @Transactional(readOnly = true)
    public UserDto find(UUID id) {
        //유저 조회
        User user = userRepository.findById(id)
                .orElseThrow(
                        () -> new NoSuchElementException("UserService: 해당하는 유저가 존재하지 않습니다."));

        UserStatus userStatus = userStatusRepository.findUserStatusByUserId(user.getId())
                .orElseThrow(
                        () -> new NoSuchElementException("UserService: 해당하는 유저상태가 존재하지 않습니다."));

        return userMapper.toDto(user);
    }


    // 전체 유저 조회
    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAll() {

        return userRepository.findAll()
                .stream()
                .map(userMapper::toDto)
                .toList();
    }


     // 유저 정보와 프로필 이미지 수정
    @Override
    @Transactional
    public UserDto update(UUID userId, UserUpdateRequest userUpdateRequest, Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {

        // 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(
                        () -> new IllegalArgumentException("UserService: 해당하는 유저가 존재하지 않습니다."));

        // 이메일 중복 검사
        if (userRepository.existsByEmail(userUpdateRequest.newEmail())){
            throw new IllegalArgumentException("UserService: " + userUpdateRequest.newEmail() + "은 이미 존재하는 이메일 입니다.");
        }

        // 이름 중복 검사
        if (userRepository.existsByUsername(userUpdateRequest.newUsername())){
            throw new IllegalArgumentException("UserService: " + userUpdateRequest.newUsername() + "은 이미 존재하는 이름 입니다.");
        }

        if((userUpdateRequest.newEmail()) != null){
            user.updateEmail(userUpdateRequest.newEmail());
        }

        if((userUpdateRequest.newUsername()) != null){
            user.updateName(userUpdateRequest.newUsername());
        }

        if((userUpdateRequest.newPassword()) != null) {
            user.updatePassword(userUpdateRequest.newPassword());
        }

        // 기존 이미지 있으면 삭제
        if (user.getProfile() != null){
            binaryContentRepository.deleteById(user.getProfile().getId());
        }

        // 프로필 이미지 처리 (기본 이미지 or 전달된 이미지)
        BinaryContent savedBinary = optionalProfileCreateRequest
                .map(profileImage -> {
                    // 기존 이미지 있으면 삭제
                    Optional.ofNullable(user.getProfile())
                        .ifPresent(profile -> {
                            binaryContentStorage.delete(profile.getId());
                            binaryContentRepository.deleteById(profile.getId());
                        });

                    // 새 이미지 등록
                    BinaryContent content = new BinaryContent(
                            profileImage.fileName(),
                            (long) profileImage.bytes().length,
                            profileImage.contentType()
                    );
                    binaryContentStorage.put(content.getId(), profileImage.bytes());
                    user.updateProfile(content);

                    return content;
                })
                .orElse(null);

        return userMapper.toDto(user);
    }


     // 유저 삭제
    @Override
    @Transactional
    public void delete(UUID id) {
        // 유저 조회
        User user = userRepository.findById(id)
                .orElseThrow(
                        () -> new NoSuchElementException("UserService: 해당하는 유저가 존재하지 않습니다."));

        // 바이트 파일 삭제
        Optional.ofNullable(user.getProfile())
                .ifPresent(profile -> binaryContentStorage.delete(profile.getId()));

        // 유저 삭제 - 영속성 전이로 UserStatus, BinaryContent 삭제
        userRepository.delete(user);
    }
}

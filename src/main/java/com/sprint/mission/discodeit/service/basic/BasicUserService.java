package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * DTO로 파라미터 받는 테스트용 서비스 구현체
 */

@RequiredArgsConstructor
@Service
public class BasicUserService implements UserService {

    private static final Logger logger = Logger.getLogger(BasicUserService.class.getName());

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

    // 기본 프로필 저장
    private Optional<BinaryContentCreateRequest> loadDefaultProfileImage() {
        try {
            ClassPathResource resource = new ClassPathResource("static/images/default-avatar.png");
            InputStream inputStream = resource.getInputStream();
            byte[] data = inputStream.readAllBytes();

            BinaryContentCreateRequest defaultImage = new BinaryContentCreateRequest(
                    "default-avatar.png",
                    "image/png",
                    data
            );
            return Optional.of(defaultImage);
        } catch (IOException e) {
            throw new RuntimeException("기본 프로필 이미지 로드 실패", e);
        }
    }

    // 유저 생성
    @Override
    public User createUser(UserCreateRequest request, Optional<BinaryContentCreateRequest> profileImage) {

        // 이메일 중복 검사 없을 수도 있으니까 옵셔널로.. 이미 존재하면 유저 객체 만들지 않고 생성 종료
        Optional<User> existUserEmail = userRepository.findUserByEmail(request.email());
        if (existUserEmail.isPresent()){
            throw new IllegalArgumentException(request.email() + "은 이미 존재하는 이메일 입니다.");
        }
        // 이름 중복 검사
        Optional<User> existUserName = userRepository.findUserByName(request.username());
        if (existUserName.isPresent()){
            throw new IllegalArgumentException(request.username() + "은 이미 존재하는 이름 입니다.");
        }

        //1. UserEmail 객체 생성
        User user = new User(
                request.username(),
                request.email(),
                request.password()
        );

        Optional<BinaryContentCreateRequest> imageToUse = profileImage.isPresent()
                ? profileImage
                : loadDefaultProfileImage(); // 여기서 기본 이미지 설정

        // 2. 프로필 이미지 있으면 저장
        UUID profileId = imageToUse
                .map(profileImageRequest -> {
                    String fileName = profileImageRequest.fileName();
                    String contentType = profileImageRequest.contentType();
                    byte[] bytes = profileImageRequest.bytes();

                    BinaryContent newProfileImage = new BinaryContent(fileName, contentType, bytes);
                    binaryContentRepository.saveBinaryContent(newProfileImage);
                    user.updateProfileId(newProfileImage.getId());
                    userRepository.saveUser(user);
                    return newProfileImage.getId();
                })
                .orElse(null); // 없으면 그냥 null



        // 3. 접속 상태 생성
        Instant now = Instant.now();
        UserStatus userStatus = new UserStatus(user.getId(), now);
        userStatusRepository.saveUserStatus(userStatus);

        // 4. 유저 저장
        User createdUser = userRepository.saveUser(user);

        // 5. 최종적으로 생성된 User 반환
        return createdUser;
    }


    @Override
    public void addUserToRepository(User user) {
        userRepository.saveUser(user);
    }

    // 아이디로 검색
    @Override
    public UserDto getUserById(UUID id) {

        //유저 조회
        User user = userRepository.findUserById(id)
                    .orElseThrow(
                    () -> new NoSuchElementException("해당하는 유저가 존재하지 않습니다."));


        UserStatus test = userStatusRepository.findStatus(user.getId())
                    .orElseThrow(
                            () -> new NoSuchElementException("해당하는 유저상태가 존재하지 않습니다."));

        // UserStatus 조회
        Boolean online = userStatusRepository.findStatus(user.getId())
                .map(UserStatus::isOnline)
                .orElse(null);

        // 패스워드는 반환하지 않기
        return new UserDto(
                user.getId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getUsername(),
                user.getEmail(),
                user.getProfileId(),
                online
        );
    }

    // 이름으로 검색하기
    @Override
    public Optional<UserDto> searchUsersByName(String name) {

        //유저 조회
        Optional<User> foundUserResult = userRepository.findUserByName(name);

        if (foundUserResult.isEmpty()) {
            logger.warning("조회된 유저가 없습니다.");
            return Optional.empty();
        }

        User user = foundUserResult.get();

        // UserStatus 조회
        Boolean online = userStatusRepository.findStatus(user.getId())
                .map(UserStatus::isOnline)
                .orElse(null);

        // 패스워드는 반환하지 않기
        UserDto response = new UserDto(
                user.getId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getUsername(),
                user.getEmail(),
                user.getProfileId(),
                online
        );

        return Optional.of(response);
    }

    // 전체 유저 조회
    @Override
    public List<UserDto> getAllUsers() {

        List<User> foundResult = userRepository.findUserAll();

        return foundResult.stream()
                .map(user -> {

                    // 요소(유저)마다 상태 추출
                    Boolean online = userStatusRepository.findStatus(user.getId())
                            .map(UserStatus::isOnline)
                            .orElse(null);
                    
                    return new UserDto(
                            user.getId(),
                            user.getCreatedAt(),
                            user.getUpdatedAt(),
                            user.getUsername(),
                            user.getEmail(),
                            user.getProfileId(),
                            online
                    );
                })
                .toList();
    }



    // 유저 정보와 프로필 이미지 수정
    @Override
    public User update(UUID userId, UserUpdateRequest userUpdateRequest, Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
        
//        유저 조회
        Optional<User> foundUser = userRepository.findUserById(userId);
        if(foundUser.isEmpty()){
            logger.warning("조회된 유저가 없습니다.");
            return null;
        }

        // 이메일 중복 검사 없을 수도 있으니까 옵셔널로.. 이미 존재하면 유저 객체 만들지 않고 생성 종료
        Optional<User> existUserEmail = userRepository.findUserByEmail(userUpdateRequest.newEmail());
        if (existUserEmail.isPresent() && !existUserEmail.get().getId().equals(userId)){
            throw new IllegalArgumentException(userUpdateRequest.newEmail() + "은 이미 존재하는 이메일 입니다.");
        }
        // 이름 중복 검사
        Optional<User> existUserName = userRepository.findUserByName(userUpdateRequest.newUsername());
        if (existUserName.isPresent() && !existUserName.get().getId().equals(userId)){
            throw new IllegalArgumentException(userUpdateRequest.newUsername() + "은 이미 존재하는 이름 입니다.");
        }

        User user = foundUser.get();

        if((userUpdateRequest.newEmail()) != null){
            user.updateEmail(userUpdateRequest.newEmail());
        }

        if((userUpdateRequest.newUsername()) != null){
            user.updateUserName(userUpdateRequest.newUsername());
        }

        if((userUpdateRequest.newPassword()) != null) {
            user.updatePassword(userUpdateRequest.newPassword());
        }


        // 기존 이미지 있으면 삭제
        if (user.getProfileId() != null){
            boolean deleteResult = binaryContentRepository.deleteById(user.getProfileId());
            if (deleteResult){
                logger.info("프로필 이미지가 삭제되었습니다.");
            } else{
                logger.warning("프로필 이미지 삭제에 실패했습니다.");
                return null;
            }
        }

        UUID nullableProfileId = optionalProfileCreateRequest
                .map(profileRequest -> {
                    Optional.ofNullable(user.getProfileId())
                            .ifPresent(binaryContentRepository::deleteById);

                    String fileName = profileRequest.fileName();
                    String contentType = profileRequest.contentType();
                    byte[] bytes = profileRequest.bytes();
                    BinaryContent binaryContent = new BinaryContent(fileName, contentType, bytes);
                    binaryContentRepository.saveBinaryContent(binaryContent);

                    user.updateProfileId(binaryContent.getId());
                    return binaryContent.getId();
                })
                .orElse(null);

        return userRepository.saveUser(user);

    }

    // 유저 삭제
    @Override
    public boolean deleteUserById(UUID id) {
        //유저 조회
        Optional<User> foundUser = userRepository.findUserById(id);
        if(foundUser.isEmpty()){
            logger.warning("조회된 유저가 없습니다.");
            return false;
        }

        User user = foundUser.get();

        // 이미지 있으면 같이 삭제
        if (user.getProfileId() != null){
            boolean deleteResult = binaryContentRepository.deleteById(user.getProfileId());
            if (deleteResult){
                logger.info("프로필 이미지가 삭제되었습니다.");
            } else{
                logger.warning("프로필 이미지 삭제에 실패했습니다.");
                return false;
            }
        }

        // 접속 상태 삭제
        userStatusRepository.deleteUserStatus(id);

        // 유저 삭제
        userRepository.deleteUser(id);
        return true;
    }
}

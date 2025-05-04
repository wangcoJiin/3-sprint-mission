package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.ProfileImageCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserProfileImageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserCreateResponse;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
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

    private final UserRepository userRepositoryService;
    private final UserStatusRepository fileUserStatusRepository;
    private final BinaryContentRepository fileBinaryContentRepository;

    // 유저 생성
    @Override
    public UserCreateResponse createUser(UserCreateRequest request, Optional<ProfileImageCreateRequest> ProfileImage) {

        // 이메일 중복 검사 없을 수도 있으니까 옵셔널로.. 이미 존재하면 유저 객체 만들지 않고 생성 종료
        Optional<User> existUserEmail = userRepositoryService.findUserByEmail(request.userEmail());
        if (existUserEmail.isPresent()){
            logger.info(request.userEmail() + "은 이미 존재하는 이메일 입니다.");
            logger.info("계정 생성에 실패했습니다.");
            return null;
        }
        // 이름 중복 검사
        Optional<User> existUserName = userRepositoryService.findUserByName(request.name());
        if (existUserName.isPresent()){
            logger.info(request.name() + "은 이미 존재하는 이름 입니다.");
            logger.info("계정 생성에 실패했습니다.");
            return null;
        }

        //1. UserEmail 객체 생성
        User user = new User(
                request.name(),
                request.userEmail(),
                request.userPassword()
        );

        // 2. 프로필 이미지 있으면 저장
        UUID profileId = ProfileImage
                .map(profileImageRequest -> {
                    BinaryContent profileImage = new BinaryContent(
                            null,
                            null,
                            profileImageRequest.data());
                    fileBinaryContentRepository.saveBinaryContent(profileImage);
                    user.updateProfileId(profileImage.getId());
                    return profileImage.getId();
                })
                .orElse(null); // 없으면 그냥 null

        // 3. 접속 상태 생성
        UserStatus userStatus = new UserStatus(user.getId());
        fileUserStatusRepository.saveUserStatus(userStatus);

        // 4. 유저 저장
        userRepositoryService.saveUser(user);

        // UserStatus 조회
        Optional<UserStatus> result = fileUserStatusRepository.findStatus(user.getId());
        String isOnline = result.map(UserStatus::getStatus)
                .orElse("Unknown");


        // 5. 최종적으로 생성된 User 반환
        // 이 때 User + UserStatus → UserResponse로 변환
        return new UserCreateResponse(
                user.getId(),
                user.getName(),
                user.getUserEmail(),
                user.getProfileId(),
                isOnline
        );
    }


    @Override
    public void addUserToRepository(User user) {
        userRepositoryService.saveUser(user);

    }

    // 아이디로 검색
    @Override
    public UserResponse getUserById(UUID id) {
        //유저 조회
        User user = userRepositoryService.findUserById(id);
        if (user == null){
            System.out.println("조회된 유저가 없습니다.");
        }

        // UserStatus 조회
        Optional<UserStatus> result = fileUserStatusRepository.findStatus(id);
        String isOnline = result.map(UserStatus::getStatus)
                .orElse("Unknown");

        // 패스워드는 반환하지 않기
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getUserEmail(),
                user.getProfileId(),
                isOnline
        );
    }

    // 이름으로 검색하기
    @Override
    public Optional<UserResponse> searchUsersByName(String name) {

        //유저 조회
        Optional<User> foundUserResult = userRepositoryService.findUserByName(name);

        if (foundUserResult.isEmpty()) {
            System.out.println("조회된 유저가 없습니다.");
            return Optional.empty();
        }

        User user = foundUserResult.get();

        // UserStatus 조회
        Optional<UserStatus> foundStatusResult = fileUserStatusRepository.findStatus(user.getId());
        String isOnline = foundStatusResult.map(UserStatus::getStatus)
                .orElse("Unknown");

        // 패스워드는 반환하지 않기
        UserResponse response = new UserResponse(
                user.getId(),
                user.getName(),
                user.getUserEmail(),
                user.getProfileId(),
                isOnline
        );

        return Optional.of(response);
    }

    // 전체 유저 조회
    @Override
    public List<UserResponse> getAllUsers() {

        List<User> foundResult = userRepositoryService.findUserAll();

        return foundResult.stream()
                .map(user -> {
                    // 요소(유저)마다 상태 추출
                    Optional<UserStatus> foundStatusResult = fileUserStatusRepository.findStatus(user.getId());
                    String status = foundStatusResult.map(UserStatus::getStatus)
                            .orElse("Unknown");

                    return new UserResponse(
                            user.getId(),
                            user.getName(),
                            user.getUserEmail(),
                            user.getProfileId(),
                            status
                    );
                })
                .toList();
    }

    // 이름 수정
    @Override
    public boolean updateUserName(UUID id, String newName) {
        //유저 조회
        User user = userRepositoryService.findUserById(id);

        if (user == null){
            System.out.println("조회된 유저가 없습니다.");
            return false;
        }
        userRepositoryService.updateUserName(user, newName);
        System.out.println("유저의 이름이 변경되었습니다.");

        return true;
    }

    //유저 프로필 사진 변경
    @Override
    public boolean updateProfileImage(UserProfileImageUpdateRequest request) {
        //유저 조회
        User user = userRepositoryService.findUserById(request.userId());
        if(user == null){
            System.out.println("조회된 유저가 없습니다.");
            return false;
        }

        // 기존 이미지 있으면 삭제
        if (user.getProfileId() != null){
            boolean deleteResult = fileBinaryContentRepository.deleteById(user.getProfileId());
            if (deleteResult){
                System.out.println("프로필 이미지가 삭제되었습니다.");
            } else{
                System.out.println("프로필 이미지 삭제에 실패했습니다.");
                return false;
            }
        }

        // 새 프로필 이미지 저장
        BinaryContent newProfileImage = new BinaryContent(
                request.userId(),
                null,
                request.newImageData());
        fileBinaryContentRepository.saveBinaryContent(newProfileImage);

        // 유저 객체에 새로운 프로필 id 연결
        user.updateProfileId(newProfileImage.getId());

        // 유저 저장
        userRepositoryService.saveUser(user);

        System.out.println("프로필 이미지가 변경되었습니다");

        return true;
    }

    // 유저 삭제
    @Override
    public boolean deleteUserById(UUID id) {
        //유저 조회
        User user = userRepositoryService.findUserById(id);
        if(user == null){
            System.out.println("조회된 유저가 없습니다.");
            return false;
        }

        // 이미지 있으면 같이 삭제
        if (user.getProfileId() != null){
            boolean deleteResult = fileBinaryContentRepository.deleteById(user.getProfileId());
            if (deleteResult){
                System.out.println("프로필 이미지가 삭제되었습니다.");
            } else{
                System.out.println("프로필 이미지 삭제에 실패했습니다.");
                return false;
            }
        }

        // 접속 상태 삭제
        boolean deleteStatusResult = fileUserStatusRepository.deleteUserStatus(id);
        if (deleteStatusResult){
            System.out.println("접속 상태 정보가 삭제되었습니다.");
        } else{
            System.out.println("접속 상태 정보 삭제에 실패했습니다.");
            return false;
        }

        // 유저 삭제
        boolean deleteUserResult = userRepositoryService.deleteUser(id);
        if (deleteUserResult){
            System.out.println("유저가 삭제되었습니다.");
        } else{
            System.out.println("유저 삭제에 실패했습니다");
            return false;
        }

        return true;
    }
}

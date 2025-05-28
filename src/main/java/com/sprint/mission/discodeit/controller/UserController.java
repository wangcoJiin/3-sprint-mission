package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Tag(name = "User", description = "User API")
@RequiredArgsConstructor
@RequestMapping("/api/users")
@RestController
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;

    // 유저 전체 조회
    @GetMapping
    @Operation(summary = "전체 User 목록 조회")
    @ApiResponses(
        value = {
                @ApiResponse(responseCode = "200", description = "User 목록 조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserDto.class))))
        }
    )
    public ResponseEntity<List<UserDto>> findAll() {
        List<UserDto> users = userService.findAll();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(users);
    }


    // 신규 유저 생성 요청
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "User 등록")
    @ApiResponses(
        value = {
                @ApiResponse(responseCode = "201", description = "User가 성공적으로 생성됨",
                    content = @Content(schema = @Schema(implementation = User.class))),
                @ApiResponse(responseCode = "400", description = "같은 email 또는 username를 사용하는 User가 이미 존재함",
                    content = @Content(examples = @ExampleObject(value = "User with email {email} already exists")))
        }
    )
    public ResponseEntity<User> create(
            @Parameter(description = "User 생성 정보")
            @RequestPart("userCreateRequest") UserCreateRequest userCreateRequest,

            @Parameter(description = "User 프로필 이미지")
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {

        // 컨트롤러는 필요하다면 값을 가공해서 서비스에게 넘겨줘야 한다.
        // 서비스는 오로지 작업만! 해야함.
        Optional<BinaryContentCreateRequest> profileRequest =
                Optional.ofNullable(profile)
                        .flatMap(this::resolveProfileRequest);

        User createdUser = userService.create(userCreateRequest, profileRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdUser);
    }


    //유저 정보 수정
    @PatchMapping(path = "/{userId}"
            , consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @Operation(summary = "User 정보 수정")
    @ApiResponses(
        value = {
                @ApiResponse(responseCode = "200", description = "User 정보가 성공적으로 수정됨",
                    content = @Content(schema = @Schema(implementation = User.class))),
                @ApiResponse(responseCode = "400", description = "같은 email 또는 username를 사용하는 User가 이미 존재함",
                    content = @Content(examples = @ExampleObject(value = "User with email {newEmail} already exists"))),
                @ApiResponse(responseCode = "404", description = "User를 찾을 수 없음",
                    content = @Content(examples = @ExampleObject(value = "User with id {userId} not found")))
        }
    )
    public ResponseEntity<User> update(
            @Parameter(description = "수정할 User ID")
            @PathVariable("userId") UUID userId,

            @Parameter(description = "수정할 User 정보")
            @RequestPart("userUpdateRequest") UserUpdateRequest userUpdateRequest,

            @Parameter(description = "수정할 User 프로필 이미지")
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {
        Optional<BinaryContentCreateRequest> request =
                Optional.ofNullable(profile)
                        .flatMap(this::resolveProfileRequest);

        User updatedUser = userService.update(userId, userUpdateRequest, request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updatedUser);
    }

    //유저 삭제
    @DeleteMapping(path = "{userId}")
    @Operation(summary = "User 삭제")
    @ApiResponses(
        value = {
                @ApiResponse(responseCode = "204", description = "User가 성공적으로 삭제됨"),
                @ApiResponse(responseCode = "404", description = "User를 찾을 수 없음",
                    content = @Content(examples = @ExampleObject(value = "User with id {userId} not found")))
        }
    )
    public ResponseEntity<Void> delete(
            @Parameter(description = "삭제할 User ID")
            @PathVariable("userId") UUID userId
    ){
        boolean deleted = userService.delete(userId);

        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    // 유저 상태 업데이트 요청
    @PatchMapping(path = "{userId}/userStatus")
    @Operation(summary = "User 온라인 상태 업데이트")
    @ApiResponses(
        value = {
                @ApiResponse(responseCode = "200", description = "User 온라인 상태가 성공적으로 업데이트됨",
                    content = @Content(schema = @Schema(implementation = UserStatus.class))),
                @ApiResponse(responseCode = "404", description = "해당 User의 UserStatus를 찾을 수 없음",
                    content = @Content(examples = @ExampleObject(value = "UserStatus with userId {userId} not found")))
        }
    )
    public ResponseEntity<UserStatus> updateUserStatusByUserId(
            @Parameter(description = "상태를 변경할 User ID")
            @PathVariable("userId") UUID userId,

            @Parameter(description = "변경할 User 온라인 상태 정보")
            @RequestBody UserStatusUpdateRequest request
    ) {
        UserStatus updatedStatus = userStatusService.updateByUserId(userId, request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updatedStatus);
    }

    // 단일 파일 처리
    // MultipartFile 타입의 요청값을 BinaryContentCreateRequest 타입으로 변환하기 위한 메서드
    private Optional<BinaryContentCreateRequest> resolveProfileRequest(MultipartFile profile) {

        if (profile == null || profile.isEmpty()) {
            // 기본 이미지 경로로부터 BinaryContentCreateRequest 생성
            try {
                ClassPathResource resource = new ClassPathResource("static/images/default-avatar.png");
                byte[] bytes = resource.getInputStream().readAllBytes();

                BinaryContentCreateRequest defaultRequest = new BinaryContentCreateRequest(
                        "default-avatar.png",
                        "image/png",
                        bytes
                );
                return Optional.of(defaultRequest);
            } catch (IOException e) {
                throw new RuntimeException("기본 이미지 파일을 읽는 중 오류가 발생했습니다.", e);
            }
        } else {
            // 컨트롤러가 요청받은 파라미터 중 MultipartFile 타입의 데이터가 존재한다면:
            try {
                BinaryContentCreateRequest binaryContentCreateRequest = new BinaryContentCreateRequest(
                        profile.getOriginalFilename(),
                        profile.getContentType(),
                        profile.getBytes()
                );
                return Optional.of(binaryContentCreateRequest);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}

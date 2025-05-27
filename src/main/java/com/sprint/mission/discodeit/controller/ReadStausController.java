package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "ReadStatus", description = "Message 읽음 상태 API")
@RequiredArgsConstructor
@RequestMapping("/api/readStatuses")
@RestController
public class ReadStausController {

    private final ReadStatusService readStatusService;


    // 유저의 ReadStatus 목록 조회
    @GetMapping
    @Operation(summary = "User의 Message 읽음 상태 목록 조회")
    @ApiResponses(
        value = {
                @ApiResponse(responseCode = "200", description = "Message ReadStatus 목록 조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReadStatus.class))))
        }
    )
    public ResponseEntity<List<ReadStatus>> findAllByUserId(
            @Parameter(description = "조회할 User ID")
            @RequestParam("userId") UUID userId
    ) {
        List<ReadStatus> result = readStatusService.findReadStatusByUserId(userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }


    // 특정 채널의 메시지 수신 정보 생성
    @PostMapping
    @Operation(summary = "Message 읽음 상태 생성")
    @ApiResponses(
        value = {
                @ApiResponse(responseCode = "201", description = "Message ReadStatus 가 성공적으로 생성됨",
                    content = @Content(schema = @Schema(implementation = ReadStatus.class))),
                @ApiResponse(responseCode = "400", description = "이미 읽음 상태가 존재함",
                    content = @Content(examples = @ExampleObject(value = "ReadStatus with userId {userId} and channelId {channelId} already exists"))),
                @ApiResponse(responseCode = "404", description = "Channel 또는 User를 찾을 수 없음",
                    content = @Content(examples = @ExampleObject(value = "Channel | User with id {channelId | userId} not found")))
        }
    )
    public ResponseEntity<ReadStatus> create(
            @Parameter(description = "생성할 ReadStatus 정보")
            @RequestBody ReadStatusCreateRequest request
    ){
        ReadStatus created = readStatusService.createReadStatus(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }


    // 특정 채널의 메시지 수신 정보 수정
    @PatchMapping("/{readStatusId}")
    @Operation(summary = "Message 읽음 상태 수정")
    @ApiResponses(
        value = {
                @ApiResponse(responseCode = "200", description = "Message 읽음 상태가 성공적으로 수정됨",
                    content = @Content(schema = @Schema(implementation = ReadStatus.class))),
                @ApiResponse(responseCode = "404", description = "Message 읽음 상태를 찾을 수 없음",
                    content = @Content(examples = @ExampleObject(value = "ReadStatus with id {readStatusId} not found")))
        }
    )
    public ResponseEntity<ReadStatus> update(
            @Parameter(description = "수정할 읽음 상태 ID")
            @PathVariable("readStatusId") UUID readStatusId,
            @Parameter(description = "수정할 ReadStatus 정보")
            @RequestBody ReadStatusUpdateRequest request
    ) {
        ReadStatus updated = readStatusService.updateReadStatus(readStatusId, request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updated);
    }

}

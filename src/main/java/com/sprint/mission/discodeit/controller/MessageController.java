package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateReq;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Tag(name = "Message", description = "Message API")
@RequiredArgsConstructor
@RequestMapping("/api/messages")
@RestController
public class MessageController {

    private final MessageService messageService;

    // 메시지 생성
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Message 생성")
    @ApiResponses(
        value = {
                @ApiResponse(responseCode = "201", description = "Message가 성공적으로 생성됨",
                    content = @Content(schema = @Schema(implementation = Message.class))),
                @ApiResponse(responseCode = "404", description = "Channel 또는 User를 찾을 수 없음",
                    content = @Content(examples = @ExampleObject(value = "Channel | Author with id {channelId | authorId} not found")))

        }
    )
    public ResponseEntity<Message> create(
            @Parameter(description = "메시지 생성 정보")
            @RequestPart("messageCreateRequest") MessageCreateRequest request,
            @Parameter(description = "Message 첨부 파일들")
            @RequestPart(value = "attachments", required = false) List<MultipartFile> attachedFiles
    ){
        List<BinaryContentCreateRequest> attachedFileRequest = resolveAttachmentsRequest(attachedFiles);

        Message createMessage = messageService.createMessage(request, attachedFileRequest);
//        System.out.println(createMessage);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createMessage);
    }


    // 채널의 메시지 조회
    @GetMapping
    @Operation(summary = "Channel의 Message 목록 조회")
    @ApiResponses(
        value = {
                @ApiResponse(responseCode = "200", description = "Message 목록 조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Message.class))))
        }
    )
    public ResponseEntity<List<Message>> findAllByChannelId(
            @Parameter(description = "조회할 Channel ID")
            @RequestParam("channelId") UUID channelId
    ){
        List<Message> getMessage =  messageService.findallByChannelId(channelId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(getMessage);
    }

    // 메시지 수정
    @PatchMapping(path = "/{messageId}")
    @Operation(summary = "Message 내용 수정")
    @ApiResponses(
        value = {
                @ApiResponse(responseCode = "200", description = "Message가 성공적으로 수정됨",
                    content = @Content(schema = @Schema(implementation = Message.class))),
                @ApiResponse(responseCode = "404", description = "Message를 찾을 수 없음",
                    content = @Content(examples = @ExampleObject(value = "Message with id {messageId} not found")))
        }
    )
    public ResponseEntity<Message> update(
            @Parameter(description = "수정할 Message ID")
            @PathVariable("messageId") UUID messageId,
            @Parameter(description = "수정할 메시지 정보")
            @RequestBody MessageUpdateRequest request
    ){
        Message updated = messageService.updateMessage(messageId, request);


        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updated);
    }

    //메시지 삭제
    @DeleteMapping("/{messageId}")
    @Operation(summary = "Message 삭제")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "204", description = "Message가 성공적으로 삭제됨"),
                    @ApiResponse(responseCode = "404", description = "Message를 찾을 수 없음",
                            content = @Content(examples = @ExampleObject(value = "Message with id {messageId} not found")))
            }
    )
    public ResponseEntity<Void> delete(
            @Parameter(description = "삭제할 Message ID")
            @PathVariable("messageId") UUID messageId

    ){
        messageService.deletedMessage(messageId);

        return ResponseEntity.noContent().build();
    }

    

    // 다중 파일 처리
    // MultipartFile 타입의 요청값을 BinaryContentCreateRequest 타입으로 변환하기 위한 메서드
    private Optional<BinaryContentCreateRequest> resolveProfileRequest(MultipartFile attachedFile) {

        if(attachedFile.isEmpty()) {
            // 컨트롤러가 요청받은 파라미터 중 MultipartFile 타입의 데이터가 비어있다면:
            return Optional.empty();
        } else {
            // 컨트롤러가 요청받은 파라미터 중 MultipartFile 타입의 데이터가 존재한다면:
            try {
                BinaryContentCreateRequest binaryContentCreateRequest = new BinaryContentCreateRequest(
                        attachedFile.getOriginalFilename(),
                        attachedFile.getContentType(),
                        attachedFile.getBytes()
                );
                return Optional.of(binaryContentCreateRequest);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // 다중 파일 처리
    private List<BinaryContentCreateRequest> resolveAttachmentsRequest(List<MultipartFile> attachedFiles) {
        if (attachedFiles == null || attachedFiles.isEmpty()) return List.of();

        return attachedFiles.stream()
                .filter(file -> !file.isEmpty())
                .map(file -> {
                    try {
                        return new BinaryContentCreateRequest(
                                file.getOriginalFilename(),
                                file.getContentType(),
                                file.getBytes()
                        );
                    } catch (IOException e) {
                        throw new RuntimeException("첨부파일 처리 중 오류 발생", e);
                    }
                })
                .toList();
    }
}

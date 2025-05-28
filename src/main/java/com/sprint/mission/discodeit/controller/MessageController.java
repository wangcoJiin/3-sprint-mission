package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.MessageApi;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
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


@RequiredArgsConstructor
@RequestMapping("/api/messages")
@RestController
public class MessageController implements MessageApi {

    private final MessageService messageService;

    // 메시지 생성
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Message> create(
            @RequestPart("messageCreateRequest") MessageCreateRequest request,
            @RequestPart(value = "attachments", required = false) List<MultipartFile> attachedFiles
    ){
        List<BinaryContentCreateRequest> attachedFileRequest = resolveAttachmentsRequest(attachedFiles);

        Message createMessage = messageService.create(request, attachedFileRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createMessage);
    }

    // 채널의 메시지 조회
    @GetMapping
    public ResponseEntity<List<Message>> findAllByChannelId(
            @RequestParam("channelId") UUID channelId
    ){
        List<Message> getMessage =  messageService.findAllByChannelId(channelId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(getMessage);
    }

    // 메시지 수정
    @PatchMapping(path = "/{messageId}")
    public ResponseEntity<Message> update(
            @PathVariable("messageId") UUID messageId,
            @RequestBody MessageUpdateRequest request
    ){
        Message updated = messageService.update(messageId, request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updated);
    }

    //메시지 삭제
    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> delete(
            @PathVariable("messageId") UUID messageId

    ){
        messageService.delete(messageId);
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

package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.BinaryContentService;
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
import java.util.UUID;

@Tag(name = "BinaryContent", description = "첨부 파일 API")
@RequiredArgsConstructor
@RequestMapping("/api/binaryContents")
@RestController
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    @GetMapping("/{binaryContentId}")
    @Operation(summary = "첨부 파일 조회")
    @ApiResponses(
        value = {
                @ApiResponse(responseCode = "200", description = "첨부 파일 조회 성공",
                    content = @Content(schema = @Schema(implementation = BinaryContent.class))),
                @ApiResponse(responseCode = "404", description = "첨부 파일을 찾을 수 없음",
                    content = @Content(examples = @ExampleObject(value = "BinaryContent with id {binaryContentId} not found")))
        }
    )
    public ResponseEntity<BinaryContent> find(
            @Parameter(description = "조회할 첨부파일 ID")
            @PathVariable("binaryContentId") UUID binaryContentId
    ) {
        BinaryContent binaryContent = binaryContentService.findBinaryContentById(binaryContentId);
        return ResponseEntity.ok(binaryContent);
    }

    // BinaryContent 다중 조회
    @GetMapping
    @Operation(summary = "여러 첨부 파일 조회")
    @ApiResponses(
        value = {
                @ApiResponse(responseCode = "200", description = "첨부 파일 목록 조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = BinaryContent.class))))
        }
    )
    public ResponseEntity<List<BinaryContent>> findAllByIdIn(
            @Parameter(description = "조회할 첨부 파일 ID 목록")
            @RequestParam("binaryContentIds") List<UUID> binaryContentIds) {
        List<BinaryContent> binaryContents = binaryContentService.findAllBinaryContent(binaryContentIds);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(binaryContents);
    }
}

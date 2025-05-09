package com.sprint.mission.discodeit.dto.request;


/**
 * 유저 생성 DTO
 * @param name
 * @param userEmail
 * @param userPassword
 */

public record UserCreateRequest(
        String name,
        String userEmail,
        String userPassword
) { }

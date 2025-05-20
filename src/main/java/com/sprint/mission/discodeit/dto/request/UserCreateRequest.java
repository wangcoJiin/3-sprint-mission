package com.sprint.mission.discodeit.dto.request;


/**
 * 유저 생성 DTO
 * @param username
 * @param email
 * @param password
 */

public record UserCreateRequest(
        String username,
        String email,
        String password
) { }

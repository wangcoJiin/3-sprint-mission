package com.sprint.mission.discodeit.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    USER_NOT_FOUND("유저를 찾을 수 없습니다. "),
    USER_NOT_FOUND_BY_NAME("해당하는 이름으로 유저를 찾을 수 없습니다. "),
    DUPLICATE_USER_NAME("이미 존재하는 유저 이름입니다. "),
    DUPLICATE_USER_EMAIL("이미 존재하는 유저 이메일입니다. "),
    INVALID_USER_PASSWORD("비밀번호가 일치하지 않습니다. "),

    CHANNEL_NOT_FOUND("채널을 찾을 수 없습니다. "),
    PRIVATE_CHANNEL_UPDATE("비공개 채널은 수정할 수 없습니다. "),

    MESSAGE_NOT_FOUND("메시지를 찾을 수 없습니다. "),

    BINARY_CONTENT_NOT_FOUND("바이너리 컨텐츠를 찾을 수 없습니다. "),

    USER_STATUS_NOT_FOUND("유저 접속 상태를 찾을 수 없습니다. "),

    READ_STATUS_NOT_FOUND("유저의 읽음 상태를 찾을 수 없습니다. ");

    private final String message;

    ErrorCode(String message){
        this.message = message;
    }
}

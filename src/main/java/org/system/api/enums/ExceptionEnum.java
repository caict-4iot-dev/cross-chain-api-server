package org.system.api.enums;

public enum ExceptionEnum {
    SUCCESS(0, "success"),

    PARAM_ERROR(1, "invalid param"),

    SYS_ERROR(101, "system error"),

    RELAY_GRPC_ERROR(102, "relay grpc error"),

    NOT_EXIST(103, "not exist"),

    PLATFORM_REPEATED_INIT(104, "api platform repeated init"),

    API_KEY_NOT_EXIST(105, "api-key not exist"),

    API_KEY_ERROR(106, "api-key error"),

    ACCESS_TOKEN_INVALID(107, "access token invalid"),

    USER_ID_ERROR(108, "user id and manager are the same"),

    CREDENTIAL_APPLY_NOT_EXIST(109, "cert apply record not exist"),

    APPLY_AUDITED(110, "apply has been audited"),

    API_KEY_IS_EXISTED(111, "user id only can has one api key"),

    NOT_API_MANAGER(111, "not an api manager"),

    ;
    private Integer errorCode;
    private String message;

    ExceptionEnum(Integer errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public Integer getErrorCode() {
        return errorCode;
    }


    public String getMessage() {
        return message;
    }

}

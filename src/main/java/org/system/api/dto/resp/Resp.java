package org.system.api.dto.resp;


import com.alipay.antchain.bridge.commons.exception.AntChainBridgeCommonsException;
import com.alipay.antchain.bridge.relayer.facade.admin.utils.FacadeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.system.api.enums.ExceptionEnum;
import org.system.api.exception.APIException;


import java.io.Serializable;

public class Resp implements Serializable {
    private Integer errorCode;
    private String message;

    public void buildCommonField(Integer errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public void buildAPIExceptionField(APIException e) {
        buildCommonField(e.getErrorCode(), e.getErrorMessage());
    }

    public void buildArgumentExceptionField(IllegalArgumentException e) {
        buildCommonField(ExceptionEnum.PARAM_ERROR.getErrorCode(), e.getMessage());
    }

    public void buildFacadeExceptionField(FacadeException e) {
        buildCommonField(ExceptionEnum.RELAY_GRPC_ERROR.getErrorCode(), e.getMessage());
    }

    public void buildAntChainBridgeCommonsExceptionField(AntChainBridgeCommonsException e) {
        buildCommonField(Integer.parseInt(e.getCode()), e.getMessage());
    }

    public void buildSuccessField() {
        buildExceptionEnumField(ExceptionEnum.SUCCESS);
    }

    public void buildSysExceptionField() {
        buildExceptionEnumField(ExceptionEnum.SYS_ERROR);
    }

    public void buildExceptionEnumField(ExceptionEnum e) {
        buildCommonField(e.getErrorCode(), e.getMessage());
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

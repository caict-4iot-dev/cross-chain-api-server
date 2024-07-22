package org.system.api.controller;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.system.api.biz.AuthBiz;
import org.system.api.dto.req.*;
import org.system.api.dto.resp.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthBiz authBiz;

    // api server init, only once
    @PostMapping(value = "/init")
    public DataResp<ApiKeyRespDto> init(){
        log.info("request url:{}","/auth/init");
        return authBiz.init();
    }

    @PostMapping(value = "/applyApiKey")
    public DataResp<ApplyApiKeyRespDto> applyApiKey(@Validated @RequestBody ApplyApiKeyReqDto applyApiKeyReqDto) {
        log.info("request url:{}******params:{}", "/auth/applyApiKey", JSONObject.toJSON(applyApiKeyReqDto));
        DataResp<ApplyApiKeyRespDto> respDtoDataResp = authBiz.applyApiKey(applyApiKeyReqDto);
        if (respDtoDataResp.getData() == null) {
            // 返回结果为空，抛出异常或返回错误信息
            throw new IllegalStateException("Api key application failed.");
        }
        return authBiz.applyApiKey(applyApiKeyReqDto);
    }

    @PostMapping(value = "/getApiKeyApply")
    public DataResp<GetApiKeyApplyRespDto> getApiKeyApply(@Validated @RequestBody GetApiKeyApplyReqDto getApiKeyApplyReqDto) {
        log.info("request url:{}******params:{}", "/auth/getApiKeyApply", JSONObject.toJSON(getApiKeyApplyReqDto));
        return authBiz.getApiKeyApply(getApiKeyApplyReqDto);
    }

    @PostMapping(value = "/auditApply")
    public DataResp<ApiKeyRespDto> auditApply(@RequestHeader("accessToken") String accessToken, @Valid @RequestBody AuditReqDto auditReqDto) {
        log.info("request url:{}******params:{}", "/auth/auditApply", JSONObject.toJSON(auditReqDto));
        return authBiz.auditApply(accessToken, auditReqDto);
    }

    @PostMapping(value = "/getApiKey")
    public DataResp<ApiKeyRespDto> getApiKey(@Valid @RequestBody GetApiKeyReqDto getApiKeyReqDto) {
        log.info("request url:{}******params:{}", "/auth/getApiKey", JSONObject.toJSON(getApiKeyReqDto));
        return authBiz.getApiKey(getApiKeyReqDto);
    }

    @PostMapping(value = "/getAccessToken")
    public DataResp<AccessTokenRespDto> getAccessToken(@Valid @RequestBody AccessTokenReqDto accessTokenReqDto) {
        log.info("request url:{}******params:{}", "/auth/getAccessToken", JSONObject.toJSON(accessTokenReqDto));
        return authBiz.getAccessToken(accessTokenReqDto);
    }
}

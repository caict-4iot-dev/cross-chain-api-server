package org.system.api.biz;

import cn.bif.module.encryption.key.PrivateKeyManager;
import cn.bif.module.encryption.key.PublicKeyManager;
import cn.hutool.core.date.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.system.api.common.constant.Constants;
import org.system.api.common.utils.*;
import org.system.api.dto.req.*;
import org.system.api.dto.resp.*;
import org.system.api.enums.ExceptionEnum;
import org.system.api.enums.StatusEnum;
import org.system.api.exception.APIException;
import org.system.api.model.ApiKeyDomain;
import org.system.api.service.ApiKeyService;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Component
public class AuthBiz {

    @Value("${object-identity.manager.address}")
    private String managerAddress;

    @Autowired
    ApiKeyService apiKeyService;

    @Autowired
    private RedisUtil redisUtil;

    private static final Logger logger = LoggerFactory.getLogger(ApiBiz.class);

    public DataResp<ApiKeyRespDto> init() {
        DataResp<ApiKeyRespDto> dataResp = new DataResp<>();
        try {
            ApiKeyDomain managerApikey = apiKeyService.getApiKeyDomain(1);
            if (!Tools.isNull(managerApikey) && managerApikey.getInitTag().equals(1)) {
                throw new APIException(ExceptionEnum.PLATFORM_REPEATED_INIT);
            }

            //create api key
            String apiKey = AppUtils.getAppId();
            String secret = AppUtils.getAppSecret(apiKey);
            ApiKeyDomain apiKeyDomain = new ApiKeyDomain();
            apiKeyDomain.setApiKey(apiKey);
            apiKeyDomain.setApiSecret(secret);
            apiKeyDomain.setUserId(managerAddress);
            apiKeyDomain.setInitTag(1);
            apiKeyService.insert(apiKeyDomain);

            ApiKeyRespDto apiKeyRespDto = new ApiKeyRespDto();
            apiKeyRespDto.setApiKey(apiKey);
            apiKeyRespDto.setApiSecret(secret);
            apiKeyRespDto.setUserId(managerAddress);
            apiKeyRespDto.setRemark("manager's api key");
            dataResp.setData(apiKeyRespDto);
            dataResp.buildSuccessField();
        } catch (APIException e) {
            dataResp.buildAPIExceptionField(e);
        } catch (Exception e) {
            logger.error("platform init error");
            dataResp.buildSysExceptionField();
        }
        return dataResp;
    }

    public DataResp<ApplyApiKeyRespDto> applyApiKey(ApplyApiKeyReqDto apiKeyApplyReqDto) {
        DataResp<ApplyApiKeyRespDto> dataResp = new DataResp<ApplyApiKeyRespDto>();
        try {
            if (!PublicKeyManager.isAddressValid(apiKeyApplyReqDto.getUserId())) {
                throw new APIException(ExceptionEnum.PARAM_ERROR);
            }
            if (apiKeyApplyReqDto.getUserId().equals(managerAddress)) {
                throw new APIException(ExceptionEnum.USER_ID_ERROR);
            }

            ApiKeyDomain apiKeyDomainTemp = apiKeyService.getApiKeyByUserId(apiKeyApplyReqDto.getUserId());
            if (!Tools.isNull(apiKeyDomainTemp)) {
                throw new APIException(ExceptionEnum.API_KEY_IS_EXISTED);
            }

            String applyNo = IdGenerator.createApplyNo();
            ApiKeyDomain apiKeyDomain = new ApiKeyDomain();
            apiKeyDomain.setApplyNo(applyNo);
            apiKeyDomain.setStatus(StatusEnum.APPLYING.getCode());
            apiKeyDomain.setUserId(apiKeyApplyReqDto.getUserId());
            apiKeyDomain.setCreateTime(DateUtil.currentSeconds());
            apiKeyDomain.setInformation(apiKeyApplyReqDto.getInformation());
            apiKeyDomain.setRemark(apiKeyApplyReqDto.getRemark());

            apiKeyService.insert(apiKeyDomain);
            ApplyApiKeyRespDto respDto = new ApplyApiKeyRespDto();
            respDto.setApplyNo(applyNo);
            dataResp.setData(respDto);
            dataResp.buildSuccessField();
        } catch (APIException e) {
            dataResp.buildAPIExceptionField(e);
        } catch (Exception e) {
            logger.error("apply api key error");
            dataResp.buildSysExceptionField();
        }
        return dataResp;
    }

    public DataResp<GetApiKeyApplyRespDto> getApiKeyApply(GetApiKeyApplyReqDto getApiKeyApplyReqDto) {
        DataResp<GetApiKeyApplyRespDto> dataResp = new DataResp<GetApiKeyApplyRespDto>();
        try {
            ApiKeyDomain apiKeyDomain = apiKeyService.getApiKeyByApply(getApiKeyApplyReqDto.getApplyNo());
            if (Tools.isNull(apiKeyDomain)) throw new APIException(ExceptionEnum.CREDENTIAL_APPLY_NOT_EXIST);

            GetApiKeyApplyRespDto getApiKeyApplyRespDto = getGetApiKeyApplyRespDto(apiKeyDomain);
            dataResp.setData(getApiKeyApplyRespDto);
            dataResp.buildSuccessField();
        } catch (APIException e) {
            dataResp.buildAPIExceptionField(e);
        } catch (Exception e) {
            logger.error("get api key apply error");
            dataResp.buildSysExceptionField();
        }
        return dataResp;
    }

    private static GetApiKeyApplyRespDto getGetApiKeyApplyRespDto(ApiKeyDomain apiKeyDomain) {
        GetApiKeyApplyRespDto getApiKeyApplyRespDto = new GetApiKeyApplyRespDto();
        getApiKeyApplyRespDto.setApplyNo(apiKeyDomain.getApplyNo());
        getApiKeyApplyRespDto.setInformation(apiKeyDomain.getInformation());
        getApiKeyApplyRespDto.setRemark(apiKeyDomain.getRemark());
        getApiKeyApplyRespDto.setStatus(apiKeyDomain.getStatus());
        getApiKeyApplyRespDto.setUserId(apiKeyDomain.getUserId());
        getApiKeyApplyRespDto.setCreateTime(apiKeyDomain.getCreateTime());
        getApiKeyApplyRespDto.setUpdateTime(apiKeyDomain.getUpdateTime());
        return getApiKeyApplyRespDto;
    }

    public DataResp<ApiKeyRespDto> auditApply(String accessToken, AuditReqDto auditReqDto) {
        DataResp<ApiKeyRespDto> dataResp = new DataResp<ApiKeyRespDto>();
        try {
            //check access token
            Map<String, String> paramMap = JwtUtil.decode(accessToken);
            if (paramMap == null) {
                throw new APIException(ExceptionEnum.ACCESS_TOKEN_INVALID);
            }

            String userId = paramMap.get(Constants.USER_ID);
            if (!userId.equals(managerAddress)) {
                throw new APIException(ExceptionEnum.NOT_API_MANAGER);
            }

            String token = redisUtil.get(userId);
            if (!token.equals(accessToken)) {
                throw new APIException(ExceptionEnum.ACCESS_TOKEN_INVALID);
            }

            //1.get apply
            String applyNo = auditReqDto.getApplyNo();
            ApiKeyDomain apiKeyDomain = apiKeyService.getApiKeyByApply(applyNo);
            if (Tools.isNull(apiKeyDomain)) throw new APIException(ExceptionEnum.CREDENTIAL_APPLY_NOT_EXIST);
            if (!Tools.isNull(apiKeyDomain) && !StatusEnum.APPLYING.getCode().equals(apiKeyDomain.getStatus())) {
                throw new APIException(ExceptionEnum.APPLY_AUDITED);
            }

            //2.deal request
            Integer status = auditReqDto.getStatus();
            String reason = auditReqDto.getReason();
            ApiKeyRespDto apiKeyRespDto = new ApiKeyRespDto();
            if (status.equals(StatusEnum.AUDIT_PASS.getCode())) {
                String apiKey = AppUtils.getAppId();
                String secret = AppUtils.getAppSecret(apiKey);
                apiKeyDomain.setApiKey(apiKey);
                apiKeyDomain.setApiSecret(secret);

                apiKeyRespDto.setApiKey(apiKey);
                apiKeyRespDto.setApiSecret(secret);
                apiKeyRespDto.setUserId(apiKeyDomain.getUserId());
            }
            apiKeyDomain.setStatus(status);
            apiKeyDomain.setUpdateTime(DateUtil.currentSeconds());
            apiKeyService.updateApplyByNo(apiKeyDomain);

            apiKeyRespDto.setStatus(status);
            apiKeyRespDto.setRemark(reason);
            dataResp.setData(apiKeyRespDto);
            dataResp.buildSuccessField();
        } catch (APIException e) {
            dataResp.buildAPIExceptionField(e);
        } catch (Exception e) {
            logger.error("audit apply error");
            dataResp.buildSysExceptionField();
        }
        return dataResp;
    }

    public DataResp<ApiKeyRespDto> getApiKey(GetApiKeyReqDto getApiKeyReqDto) {
        DataResp<ApiKeyRespDto> dataResp = new DataResp<ApiKeyRespDto>();
        try {
            ApiKeyDomain apiKeyDomain = apiKeyService.getApiKeyByApply(getApiKeyReqDto.getApplyNo());
            if (Tools.isNull(apiKeyDomain)) throw new APIException(ExceptionEnum.CREDENTIAL_APPLY_NOT_EXIST);

            ApiKeyRespDto apiKeyRespDto = new ApiKeyRespDto();
            apiKeyRespDto.setApiSecret(apiKeyDomain.getApiSecret());
            apiKeyRespDto.setApiKey(apiKeyDomain.getApiKey());
            apiKeyRespDto.setStatus(apiKeyDomain.getStatus());
            apiKeyRespDto.setRemark(apiKeyDomain.getRemark());
            apiKeyRespDto.setUserId(apiKeyDomain.getUserId());
            dataResp.setData(apiKeyRespDto);
            dataResp.buildSuccessField();
        } catch (APIException e) {
            dataResp.buildAPIExceptionField(e);
        } catch (Exception e) {
            logger.error("get api key error");
            dataResp.buildSysExceptionField();
        }
        return dataResp;
    }

    public DataResp<AccessTokenRespDto> getAccessToken(AccessTokenReqDto accessTokenReqDto) {
        DataResp<AccessTokenRespDto> dataResp = new DataResp<AccessTokenRespDto>();
        String apiKey = accessTokenReqDto.getApiKey();
        String apiSecret = accessTokenReqDto.getApiSecret();
        String userId = accessTokenReqDto.getUserId();
        try {
            ApiKeyDomain apiKeyDomain = apiKeyService.getApiKeyByUserId(userId);
            if (Tools.isNull(apiKeyDomain)) {
                throw new APIException(ExceptionEnum.API_KEY_NOT_EXIST);
            }
            if (!apiKey.equals(apiKeyDomain.getApiKey()) || !apiSecret.equals(apiKeyDomain.getApiSecret())) {
                throw new APIException(ExceptionEnum.API_KEY_ERROR);
            }

            String user = apiKeyDomain.getUserId();
            Map<String,String> tokenMap = new HashMap<>();
            tokenMap.put(Constants.API_KEY_MARK, apiKeyDomain.getApiKey());
            tokenMap.put(Constants.USER_ID, user);
            String accessToken = JwtUtil.encode(tokenMap);

            redisUtil.setex(userId, accessToken, Constants.ACCESS_TOKEN_EXPIRES);
            AccessTokenRespDto accessTokenRespDto = new AccessTokenRespDto();
            accessTokenRespDto.setAccessToken(accessToken);
            accessTokenRespDto.setExpireIn(Constants.ACCESS_TOKEN_EXPIRES);
            dataResp.setData(accessTokenRespDto);
            dataResp.buildSuccessField();
        }catch (APIException e) {
            dataResp.buildAPIExceptionField(e);
        }catch (Exception e){
            logger.error("get access token error");
            dataResp.buildSysExceptionField();
        }
        return dataResp;
    }
}

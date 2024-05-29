package org.system.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.system.api.mapper.ApiKeyMapper;
import org.system.api.model.ApiKeyDomain;

@Service
public class ApiKeyService {

    @Autowired
    private ApiKeyMapper apiKeyMapper;

    public int insert(ApiKeyDomain apiKeyDomain){
        return apiKeyMapper.insert(apiKeyDomain);
    }

    public ApiKeyDomain getApiKeyByApply(String applyNo) {
        return apiKeyMapper.getApiKeyByApply(applyNo);
    }

    public ApiKeyDomain getApiKeyByUserId(String userId) {
        return apiKeyMapper.getApiKeyByUserId(userId);
    }

    public ApiKeyDomain getApiKeyDomain(Integer id) {
        return apiKeyMapper.getApiKeyDomain(id);
    }

    public int updateApplyByNo(ApiKeyDomain apiKeyDomain) {
        return apiKeyMapper.updateApplyByNo(apiKeyDomain);
    }
}

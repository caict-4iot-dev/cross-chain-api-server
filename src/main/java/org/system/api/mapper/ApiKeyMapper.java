package org.system.api.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.system.api.model.ApiKeyDomain;

@Mapper
public interface ApiKeyMapper {
    public int insert(ApiKeyDomain apiKeyDomain);
    public ApiKeyDomain getApiKeyByApply(String applyNo);
    public ApiKeyDomain getApiKeyByUserId(String userId);
    public ApiKeyDomain getApiKeyDomain(Integer id);
    public int updateApplyByNo(ApiKeyDomain apiKeyDomain);
}

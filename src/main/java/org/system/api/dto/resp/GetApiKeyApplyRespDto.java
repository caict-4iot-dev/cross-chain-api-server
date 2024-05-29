package org.system.api.dto.resp;

import lombok.Data;

@Data
public class GetApiKeyApplyRespDto {
    private String applyNo;
    private String information;
    private String remark;
    private Integer status;
    private String userId;
    private long createTime;
    private long updateTime;
}
package org.system.api.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ApiKeyDomain {
    private Integer id;
    private String applyNo;
    private String information;
    private String remark;
    private Integer status;
    private String apiKey;
    private String apiSecret;
    private String userId;
    private long createTime;
    private long updateTime;
    private Integer initTag;
}

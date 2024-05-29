package org.system.api.dto.resp;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ApiKeyRespDto {
    private String apiKey;
    private String apiSecret;
    private String userId;
    private Integer status;
    private String remark;
}

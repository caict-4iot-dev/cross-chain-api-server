package org.system.api.dto.resp;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GetBlockchainHeightRespDto {
    private String height;
}

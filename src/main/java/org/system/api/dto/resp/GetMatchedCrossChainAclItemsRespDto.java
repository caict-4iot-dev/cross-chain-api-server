package org.system.api.dto.resp;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class GetMatchedCrossChainAclItemsRespDto {
    private List<String> bizId;
}

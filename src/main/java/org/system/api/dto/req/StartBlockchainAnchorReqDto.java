package org.system.api.dto.req;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

import static org.system.api.common.constant.MessageConstant.DESC_VALID_NULL;
import static org.system.api.common.constant.MessageConstant.DESC_VALID_STRING;

@Data
public class StartBlockchainAnchorReqDto {
    @NotBlank(message = DESC_VALID_NULL)
    @Length(min = 1, max = 32, message = DESC_VALID_STRING)
    private String domainName;
}

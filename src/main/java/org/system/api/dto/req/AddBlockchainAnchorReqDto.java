package org.system.api.dto.req;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import static org.system.api.common.constant.MessageConstant.*;
import static org.system.api.common.constant.MessageConstant.VALID_MAIN_BID_FORMAT;

@Data
public class AddBlockchainAnchorReqDto {
    @NotBlank(message = DESC_VALID_NULL)
    @Length(min = 1, max = 32, message = DESC_VALID_STRING)
    private String domainName;

    @NotBlank(message = DESC_VALID_NULL)
    @Length(min = 1, max = 32, message = DESC_VALID_STRING)
    private String product;

    @NotBlank(message = DESC_VALID_NULL)
    @Length(min = 1, max = 512, message = DESC_VALID_STRING)
    private String confFile;
}

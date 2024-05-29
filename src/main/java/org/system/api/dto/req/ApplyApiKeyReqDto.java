package org.system.api.dto.req;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import static org.system.api.common.constant.MessageConstant.*;

@Data
public class ApplyApiKeyReqDto {
    @NotBlank(message = DESC_VALID_NULL)
    @Length(min = 1, max = 64, message = DESC_VALID_STRING)
    @Pattern(regexp = PATTERN_MAIN_BID, message = VALID_MAIN_BID_FORMAT)
    private String userId;

    @NotBlank(message = DESC_VALID_NULL)
    @Length(min = 1, max = 512, message = DESC_VALID_STRING)
    private String information;

    @Length(max = 512, message = DESC_VALID_STRING)
    private String remark;
}

package org.system.api.dto.req;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import static org.system.api.common.constant.MessageConstant.*;

@Data
public class GetApiKeyApplyReqDto {
    @NotBlank(message = DESC_VALID_NULL)
    @Length(min = 1, max = 64, message = DESC_VALID_STRING)
    private String applyNo;
}

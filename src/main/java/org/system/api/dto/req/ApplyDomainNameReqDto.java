package org.system.api.dto.req;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import static org.system.api.common.constant.MessageConstant.*;

@Getter
@Setter
@Data
public class ApplyDomainNameReqDto {
    @NotBlank(message = DESC_VALID_NULL)
    @Length(min = 1, max = 32, message = DESC_VALID_STRING)
    private String domainName;

    @NotBlank(message = DESC_VALID_NULL)
    @Length(min = 1, max = 128, message = DESC_VALID_STRING)
    private String publicKey;
}

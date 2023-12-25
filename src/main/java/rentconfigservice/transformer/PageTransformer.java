package rentconfigservice.transformer;

import org.springframework.data.domain.Page;
import rentconfigservice.core.dto.PageDto;
import rentconfigservice.core.dto.UserInfoDto;

public interface PageTransformer {

    PageDto<UserInfoDto> transformPageDtoFromPage(Page<UserInfoDto> page);
}

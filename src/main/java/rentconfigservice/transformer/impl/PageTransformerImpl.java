package rentconfigservice.transformer.impl;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import rentconfigservice.core.dto.PageDto;
import rentconfigservice.core.dto.UserInfoDto;
import rentconfigservice.transformer.PageTransformer;

@Component
public class PageTransformerImpl implements PageTransformer {

    @Override
    public PageDto<UserInfoDto> transformPageDtoFromPage(Page<UserInfoDto> page) {
        return new PageDto<UserInfoDto>().setNumber(page.getNumber())
                .setSize(page.getSize())
                .setTotalPages(page.getTotalPages())
                .setTotalElements(page.getTotalElements())
                .setFirst(page.isFirst())
                .setNumberOfElements(page.getNumberOfElements())
                .setLast(page.isLast())
                .setContent(page.getContent());
    }

}

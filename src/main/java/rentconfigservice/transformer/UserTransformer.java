package rentconfigservice.transformer;

import rentconfigservice.core.dto.UserCreationDto;
import rentconfigservice.core.dto.UserDetailsDto;
import rentconfigservice.core.dto.UserInfoDto;
import rentconfigservice.core.dto.UserRegistrationDto;
import rentconfigservice.core.entity.User;

public interface UserTransformer {

    UserInfoDto transformInfoDtoFromEntity(User user);

    UserCreationDto transformCreateDtoFromEntity(User user);

    User transformEntityFromCreateDto(UserCreationDto userCreationDto);

    User transformEntityFromRegistrationDto(UserRegistrationDto userRegistrationDto);

    UserCreationDto transformCreationDtoFromRegistrationDto(UserRegistrationDto userRegistrationDto);

    UserCreationDto transformCreationDtoFromDetailsDto(UserDetailsDto userDetailsDto, String password);
}

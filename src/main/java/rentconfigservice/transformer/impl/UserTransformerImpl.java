package rentconfigservice.transformer.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rentconfigservice.core.dto.UserCreationDto;
import rentconfigservice.core.dto.UserInfoDto;
import rentconfigservice.core.dto.UserRegistrationDto;
import rentconfigservice.core.entity.User;
import rentconfigservice.core.entity.UserRole;
import rentconfigservice.core.entity.UserStatus;
import rentconfigservice.service.UserPasswordEncoder;
import rentconfigservice.transformer.UserTransformer;

@Component
public class UserTransformerImpl implements UserTransformer {

    @Autowired
    private UserPasswordEncoder userPasswordEncoder;

    @Override
    public UserInfoDto transformInfoDtoFromEntity(User user) {
        return new UserInfoDto()
                .setId(user.getId())
                .setEmail(user.getEmail())
                .setFio(user.getFio())
                .setRole(user.getUserRole())
                .setStatus(user.getStatus())
                .setCreatedDate(user.getCreationDate())
                .setUpdatedDate(user.getUpdateDate());
    }

    @Override
    public UserCreationDto transformCreateDtoFromEntity(User user) {
        return new UserCreationDto()
                .setEmail(user.getEmail())
                .setPassword(user.getPassword())
                .setFio(user.getFio())
                .setRole(user.getUserRole())
                .setStatus(user.getStatus());
    }

    @Override
    public User transformEntityFromCreateDto(UserCreationDto userCreationDto) {
        return new User()
                .setEmail(userCreationDto.getEmail())
                .setPassword(userPasswordEncoder.encodePassword(userCreationDto.getPassword()))
                .setFio(userCreationDto.getFio())
                .setUserRole(userCreationDto.getRole())
                .setStatus(userCreationDto.getStatus());
    }

    @Override
    public User transformEntityFromRegistrationDto(UserRegistrationDto userRegistrationDto) {
        return new User()
                .setEmail(userRegistrationDto.getEmail())
                .setPassword(userPasswordEncoder.encodePassword(userRegistrationDto.getPassword()))
                .setFio(userRegistrationDto.getFio())
                .setUserRole(UserRole.USER)
                .setStatus(UserStatus.WAITING_ACTIVATION);
    }
}

package rentconfigservice.transformer.impl;

import org.springframework.stereotype.Component;
import rentconfigservice.core.dto.UserCreationDto;
import rentconfigservice.core.dto.UserDetailsDto;
import rentconfigservice.core.dto.UserInfoDto;
import rentconfigservice.core.dto.UserRegistrationDto;
import rentconfigservice.core.entity.User;
import rentconfigservice.core.entity.UserRole;
import rentconfigservice.core.entity.UserStatus;
import rentconfigservice.service.UserPasswordEncoder;
import rentconfigservice.transformer.UserTransformer;

import java.time.ZoneId;

@Component
public class UserTransformerImpl implements UserTransformer {

    private final UserPasswordEncoder userPasswordEncoder;

    public UserTransformerImpl(UserPasswordEncoder userPasswordEncoder) {
        this.userPasswordEncoder = userPasswordEncoder;
    }

    @Override
    public UserInfoDto transformInfoDtoFromEntity(User user) {
        return new UserInfoDto()
                .setId(user.getId())
                .setMail(user.getEmail())
                .setFio(user.getFio())
                .setRole(user.getUserRole())
                .setStatus(user.getStatus())
                .setCreatedDate(user.getCreationDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                .setUpdatedDate(user.getUpdateDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
    }

    @Override
    public UserCreationDto transformCreateDtoFromEntity(User user) {
        return new UserCreationDto()
                .setMail(user.getEmail())
                .setPassword(user.getPassword())
                .setFio(user.getFio())
                .setRole(user.getUserRole())
                .setStatus(user.getStatus());
    }

    @Override
    public User transformEntityFromCreateDto(UserCreationDto userCreationDto) {
        return new User()
                .setEmail(userCreationDto.getMail())
                .setPassword(userPasswordEncoder.encodePassword(userCreationDto.getPassword()))
                .setFio(userCreationDto.getFio())
                .setUserRole(userCreationDto.getRole())
                .setStatus(userCreationDto.getStatus());
    }

    @Override
    public User transformEntityFromRegistrationDto(UserRegistrationDto userRegistrationDto) {
        return new User()
                .setEmail(userRegistrationDto.getMail())
                .setPassword(userPasswordEncoder.encodePassword(userRegistrationDto.getPassword()))
                .setFio(userRegistrationDto.getFio())
                .setUserRole(UserRole.USER)
                .setStatus(UserStatus.WAITING_ACTIVATION);
    }

    @Override
    public UserCreationDto transformCreationDtoFromRegistrationDto(UserRegistrationDto userRegistrationDto){
        return new UserCreationDto().setMail(userRegistrationDto.getMail())
                .setPassword(userRegistrationDto.getPassword())
                .setFio(userRegistrationDto.getFio())
                .setStatus(UserStatus.WAITING_ACTIVATION)
                .setRole(UserRole.USER);
    }

    @Override
    public UserCreationDto transformCreationDtoFromDetailsDto(UserDetailsDto userDetailsDto, String password){
        return new UserCreationDto().setMail(userDetailsDto.getEmail())
                .setPassword(password)
                .setFio(userDetailsDto.getFio())
                .setRole(userDetailsDto.getRole())
                .setStatus(UserStatus.WAITING_ACTIVATION);
    }
}

package rentconfigservice.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import rentconfigservice.core.dto.*;
import rentconfigservice.core.entity.User;

import java.util.UUID;

public interface UserService {

    Page<UserInfoDto> getAllUsers(Pageable pageable);

    UserInfoDto findUserById(UUID id);

    User createUser(UserCreationDto userCreationDto);

    User updateUser(UserCreationDto userCreationDto, UUID id);

    User registrateUser(UserRegistrationDto userRegistrationDto);

    String loginUser(UserLoginDto userLoginDto);

    void verifyUserByEmailAndToken(TemporarySecretTokenDto temporarySecretTokenDto);

    UserInfoDto findInfoAboutMe();

    User updatePassword(PasswordUpdateDto passwordUpdateDto);

    void sendPasswordRestoreLink(String email);
}

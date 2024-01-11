package rentconfigservice.service;

import rentconfigservice.core.dto.PasswordUpdateDto;
import rentconfigservice.core.dto.TemporarySecretTokenDto;
import rentconfigservice.core.dto.UserInfoDto;
import rentconfigservice.core.dto.UserLoginDto;
import rentconfigservice.core.dto.UserRegistrationDto;
import rentconfigservice.core.entity.User;

public interface AuthenticationService {

    User registrateUser(UserRegistrationDto userRegistrationDto);

    String loginUser(UserLoginDto userLoginDto);

    void verifyUserByEmailAndToken(TemporarySecretTokenDto temporarySecretTokenDto);

    UserInfoDto findInfoAboutMe();

    User updatePassword(PasswordUpdateDto passwordUpdateDto);

    void sendPasswordRestoreLink(String email);
}

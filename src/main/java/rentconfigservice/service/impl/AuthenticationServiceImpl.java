package rentconfigservice.service.impl;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rentconfigservice.aop.Audited;
import rentconfigservice.core.dto.PasswordUpdateDto;
import rentconfigservice.core.dto.TemporarySecretTokenDto;
import rentconfigservice.core.dto.UserDetailsDto;
import rentconfigservice.core.dto.UserInfoDto;
import rentconfigservice.core.dto.UserLoginDto;
import rentconfigservice.core.dto.UserQueryDto;
import rentconfigservice.core.dto.UserRegistrationDto;
import rentconfigservice.core.entity.User;
import rentconfigservice.core.entity.UserStatus;
import rentconfigservice.service.AuthenticationService;
import rentconfigservice.service.EmailMessageBuilder;
import rentconfigservice.service.EmailService;
import rentconfigservice.service.TemporarySecretTokenService;
import rentconfigservice.service.UserPasswordEncoder;
import rentconfigservice.service.UserService;
import rentconfigservice.service.jwt.JwtHandler;
import rentconfigservice.transformer.UserTransformer;

import static rentconfigservice.core.entity.AuditedAction.INFO_ABOUT_ME;
import static rentconfigservice.core.entity.AuditedAction.LOGIN;
import static rentconfigservice.core.entity.AuditedAction.REGISTRATION;
import static rentconfigservice.core.entity.AuditedAction.UPDATE_PASSWORD;
import static rentconfigservice.core.entity.AuditedAction.VERIFICATION;
import static rentconfigservice.core.entity.EssenceType.USER;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserService userService;
    private final UserTransformer userTransformer;
    private final UserPasswordEncoder userPasswordEncoder;
    private final EmailService emailService;
    private final EmailMessageBuilder emailMessageBuilder;
    private final TemporarySecretTokenService temporarySecretTokenService;
    private final JwtHandler jwtHandler;

    public AuthenticationServiceImpl(UserService userService,
                                     UserTransformer userTransformer,
                                     UserPasswordEncoder userPasswordEncoder,
                                     EmailService emailService,
                                     EmailMessageBuilder emailMessageBuilder,
                                     TemporarySecretTokenService temporarySecretTokenService,
                                     JwtHandler jwtHandler) {
        this.userService = userService;
        this.userTransformer = userTransformer;
        this.userPasswordEncoder = userPasswordEncoder;
        this.emailService = emailService;
        this.emailMessageBuilder = emailMessageBuilder;
        this.temporarySecretTokenService = temporarySecretTokenService;
        this.jwtHandler = jwtHandler;
    }

    @Transactional
    @Override
    @Audited(auditedAction = REGISTRATION, essenceType = USER)
    public User registrateUser(UserRegistrationDto userRegistrationDto) {
        return userService.createUser(
                userTransformer.transformCreationDtoFromRegistrationDto(userRegistrationDto)
        );
    }

    @Override
    @Audited(auditedAction = LOGIN, essenceType = USER)
    public String loginUser(UserLoginDto userLoginDto) {
        UserQueryDto userQueryDto = userService.getUserQueryDto(userLoginDto.getMail());

        if (!userPasswordEncoder.passwordMatches(userLoginDto.getPassword(), userQueryDto.getPassword())) {
            throw new RuntimeException("wrong password");
        }
        if (!userQueryDto.getStatus().equals(UserStatus.ACTIVATED)) {
            throw new RuntimeException("verification failed or your account deactivated");
        }

        return jwtHandler.generateAccessToken(userService.getUserDetailsDto(userLoginDto.getMail()));
    }

    @Transactional
    @Override
    @Audited(auditedAction = VERIFICATION, essenceType = USER)
    public void verifyUserByEmailAndToken(TemporarySecretTokenDto temporarySecretTokenDto) {
        String email = temporarySecretTokenService.getEmailByToken(temporarySecretTokenDto.getSecretToken().toString());
        userService.activationUser(email);
        temporarySecretTokenService.deleteEntityByEmailAndToken(
                email,
                temporarySecretTokenDto.getSecretToken().toString()
        );
    }

    @Override
    @Audited(auditedAction = INFO_ABOUT_ME, essenceType = USER)
    public UserInfoDto findInfoAboutMe() {
        UserDetailsDto userDetailsDto = (UserDetailsDto) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        return userService.findUserById(userDetailsDto.getId());
    }

    @Transactional
    @Override
    public void sendPasswordRestoreLink(String email) {
        String token = temporarySecretTokenService.createToken(email);
        emailService.sendSimpleMessage(
                email,
                emailMessageBuilder.buildUpdatePasswordSubject(),
                emailMessageBuilder.buildUpdatePasswordMessage(token));
    }

    @Transactional
    @Override
    @Audited(auditedAction = UPDATE_PASSWORD, essenceType = USER)
    public User updatePassword(PasswordUpdateDto passwordUpdateDto) {
        String email = temporarySecretTokenService.getEmailByToken(passwordUpdateDto.getToken().toString());
        UserDetailsDto userDetailsDto = userService.getUserDetailsDto(email);
        temporarySecretTokenService.deleteEntityByEmailAndToken(email, passwordUpdateDto.getToken().toString());
        userService.createUserByAdmin(userTransformer.transformCreationDtoFromDetailsDto(userDetailsDto, passwordUpdateDto.getPassword()));
        return userService.createUserByAdmin(userTransformer
                .transformCreationDtoFromDetailsDto(userDetailsDto, passwordUpdateDto.getPassword()));
    }
}

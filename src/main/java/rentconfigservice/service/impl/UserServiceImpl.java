package rentconfigservice.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import rentconfigservice.aop.Audited;
import rentconfigservice.core.dto.*;
import rentconfigservice.core.entity.User;
import rentconfigservice.core.entity.UserStatus;
import rentconfigservice.exception.EntityNotFoundException;
import rentconfigservice.repository.UserRepository;
import rentconfigservice.service.*;
import rentconfigservice.service.jwt.JwtHandler;
import rentconfigservice.transformer.UserTransformer;

import java.util.List;
import java.util.UUID;

import static rentconfigservice.core.entity.AuditedAction.*;
import static rentconfigservice.core.entity.EssenceType.USER;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserTransformer userTransformer;
    private final UserPasswordEncoder userPasswordEncoder;
    private final TemporarySecretTokenService temporarySecretTokenService;
    private final EmailService emailService;
    private final EmailMessageBuilder emailMessageBuilder;
    private final JwtHandler jwtHandler;

    public UserServiceImpl(
            UserRepository userRepository,
            UserTransformer userTransformer,
            UserPasswordEncoder userPasswordEncoder,
            TemporarySecretTokenService temporarySecretTokenService,
            EmailService emailService,
            EmailMessageBuilder emailMessageBuilder,
            JwtHandler jwtHandler) {
        this.userRepository = userRepository;
        this.userTransformer = userTransformer;
        this.userPasswordEncoder = userPasswordEncoder;
        this.temporarySecretTokenService = temporarySecretTokenService;
        this.emailService = emailService;
        this.emailMessageBuilder = emailMessageBuilder;
        this.jwtHandler = jwtHandler;
    }

    @Override
    @Audited(auditedAction = INFO_ABOUT_ALL_USERS, essenceType = USER)
    public Page<UserInfoDto> getAllUsers(Pageable pageable) {
        Page<User> entityPage = userRepository.findAll(pageable);
        List<UserInfoDto> dtoList = entityPage.stream()
                .map(it -> userTransformer.transformInfoDtoFromEntity(it))
                .toList();
        return new PageImpl<UserInfoDto>(dtoList, entityPage.getPageable(), entityPage.getTotalElements());
    }

    @Override
    @Audited(auditedAction = INFO_ABOUT_USER_BY_ID, essenceType = USER)
    public UserInfoDto findUserById(UUID id) {
        User userEntity = getUserById(id);
        return userTransformer.transformInfoDtoFromEntity(userEntity);
    }

    @Override
    @Audited(auditedAction = CREATE_USER, essenceType = USER)
    public User createUser(UserCreationDto userCreationDto) {
        User userForSave = userRepository.save(userTransformer.transformEntityFromCreateDto(userCreationDto));
        String token = temporarySecretTokenService.createToken(userForSave.getEmail());
        emailService.sendSimpleMessage(
                userCreationDto.getEmail(),
                emailMessageBuilder.buildVerificationSubject(),
                emailMessageBuilder.buildVerificationMessage(userCreationDto.getEmail(), token)
        );
        return userForSave;
    }

    @Override
    @Audited(auditedAction = UPDATE_USER, essenceType = USER)
    public User updateUser(UserCreationDto userCreationDto, UUID id) {
        User userEntity = getUserById(id);
        userEntity
                .setEmail(userCreationDto.getEmail())
                .setPassword(userPasswordEncoder.encodePassword(userCreationDto.getPassword()))
                .setFio(userCreationDto.getFio())
                .setUserRole(userCreationDto.getRole())
                .setStatus(userCreationDto.getStatus());
        userRepository.save(userEntity);
        return userEntity;
    }

    @Override
    @Audited(auditedAction = REGISTRATION, essenceType = USER)
    public User registrateUser(UserRegistrationDto userRegistrationDto) {
        User userForSave = userRepository.save(userTransformer.transformEntityFromRegistrationDto(userRegistrationDto));
        String token = temporarySecretTokenService.createToken(userForSave.getEmail());
        emailService.sendSimpleMessage(
                userRegistrationDto.getEmail(),
                emailMessageBuilder.buildVerificationSubject(),
                emailMessageBuilder.buildVerificationMessage(userRegistrationDto.getEmail(), token)
        );
        return userForSave;
    }

    @Override
    @Audited(auditedAction = LOGIN, essenceType = USER)
    public String loginUser(UserLoginDto userLoginDto) {
        UserQueryDto userQueryDto = userRepository.findPasswordAndStatusByEmail(
                userLoginDto.getEmail()).orElseThrow(() -> new EntityNotFoundException("User", userLoginDto.getEmail())
        );

        if (!userPasswordEncoder.passwordMatches(userLoginDto.getPassword(), userQueryDto.getPassword())) {
            throw new RuntimeException("wrong password");
        }

        if (!userQueryDto.getStatus().equals(UserStatus.ACTIVATED)) {
            throw new RuntimeException("verification failed");
        }
        UserDetailsDto userDetailsDto = userRepository.findIdFioAndRoleByEmail(
                userLoginDto.getEmail()).orElseThrow(() -> new EntityNotFoundException("User", userLoginDto.getEmail()));
        return jwtHandler.generateAccessToken(userDetailsDto);
    }

    @Override
    @Audited(auditedAction = VERIFICATION, essenceType = USER)
    public void verifyUserByEmailAndToken(TemporarySecretTokenDto temporarySecretTokenDto) {
        String email = temporarySecretTokenService.getEmailByToken(temporarySecretTokenDto.getSecretToken().toString());
        userRepository.updateStatusByEmail(UserStatus.ACTIVATED, temporarySecretTokenDto.getEmail());
        temporarySecretTokenService.deleteEntityByEmailAndToken(
                temporarySecretTokenDto.getEmail(),
                temporarySecretTokenDto.getSecretToken().toString()
        );
    }

    @Override
    @Audited(auditedAction = INFO_ABOUT_ME, essenceType = USER)
    public UserInfoDto findInfoAboutMe() {
        UserDetailsDto userDetailsDto = (UserDetailsDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User userEntity = userRepository.findById(userDetailsDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("User", userDetailsDto.getId()));
        return userTransformer.transformInfoDtoFromEntity(userEntity);
    }

    @Override
    public void sendPasswordRestoreLink(String email) {
        String token = temporarySecretTokenService.createToken(email);
        emailService.sendSimpleMessage(
                email,
                emailMessageBuilder.buildUpdatePasswordSubject(),
                emailMessageBuilder.buildUpdatePasswordMessage(token));
    }

    @Override
    @Audited(auditedAction = UPDATE_PASSWORD, essenceType = USER)
    public User updatePassword(PasswordUpdateDto passwordUpdateDto) {
        String email = temporarySecretTokenService.getEmailByToken(passwordUpdateDto.getToken().toString());
        User userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User", email));
        if (userEntity.getStatus() == UserStatus.WAITING_ACTIVATION) {
            userEntity.setStatus(UserStatus.ACTIVATED);
        }
        userEntity.setPassword(userPasswordEncoder.encodePassword(passwordUpdateDto.getPassword()));
        userRepository.save(userEntity);
        temporarySecretTokenService.deleteEntityByEmailAndToken(email, passwordUpdateDto.getToken().toString());
        return userEntity;
    }

    private User getUserById(UUID id) {
        return userRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User", id));
    }
}

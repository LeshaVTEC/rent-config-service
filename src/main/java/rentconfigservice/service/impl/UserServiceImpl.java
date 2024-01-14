package rentconfigservice.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rentconfigservice.aop.Audited;
import rentconfigservice.core.dto.*;
import rentconfigservice.core.entity.User;
import rentconfigservice.core.entity.UserStatus;
import rentconfigservice.exception.EntityNotFoundException;
import rentconfigservice.exception.ValidationException;
import rentconfigservice.repository.UserRepository;
import rentconfigservice.service.*;
import rentconfigservice.transformer.UserTransformer;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static rentconfigservice.core.entity.AuditedAction.*;
import static rentconfigservice.core.entity.EssenceType.USER;

@Service
public class  UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserTransformer userTransformer;
    private final UserPasswordEncoder userPasswordEncoder;
    private final TemporarySecretTokenService temporarySecretTokenService;
    private final EmailService emailService;
    private final EmailMessageBuilder emailMessageBuilder;

    public UserServiceImpl(
            UserRepository userRepository,
            UserTransformer userTransformer,
            UserPasswordEncoder userPasswordEncoder,
            TemporarySecretTokenService temporarySecretTokenService,
            EmailService emailService,
            EmailMessageBuilder emailMessageBuilder) {
        this.userRepository = userRepository;
        this.userTransformer = userTransformer;
        this.userPasswordEncoder = userPasswordEncoder;
        this.temporarySecretTokenService = temporarySecretTokenService;
        this.emailService = emailService;
        this.emailMessageBuilder = emailMessageBuilder;
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

    @Transactional
    @Override
    @Audited(auditedAction = CREATE_USER, essenceType = USER)
    public User createUserByAdmin(UserCreationDto userCreationDto) {
        validateEmail(userCreationDto.getMail());
        User userForSave = userRepository.saveAndFlush(userTransformer.transformEntityFromCreateDto(userCreationDto));
        String token = temporarySecretTokenService.createToken(userForSave.getEmail());
//        emailService.sendSimpleMessage(
//                userCreationDto.getEmail(),
//                emailMessageBuilder.buildVerificationSubject(),
//                emailMessageBuilder.buildVerificationMessage(userCreationDto.getEmail(), token)
//        );
        return userForSave;
    }

    @Override
    @Audited(auditedAction = UPDATE_USER, essenceType = USER)
    public User updateUser(UserCreationDto userCreationDto, UUID id, LocalDateTime updatedDate) {
        User userEntity = getUserById(id);
        userEntity
                .setEmail(userCreationDto.getMail())
                .setPassword(userPasswordEncoder.encodePassword(userCreationDto.getPassword()))
                .setFio(userCreationDto.getFio())
                .setUserRole(userCreationDto.getRole())
                .setStatus(userCreationDto.getStatus());
        if (userEntity.getUpdateDate().truncatedTo(ChronoUnit.MILLIS).isEqual(updatedDate)) {
            userRepository.saveAndFlush(userEntity);
        } else {
            throw new ValidationException("version field - " + updatedDate
                    .toInstant(ZoneOffset.ofTotalSeconds(0))
                    .toEpochMilli());
        }
        return userEntity;
    }

    @Override
    public UserQueryDto getUserQueryDto(String email){
        return userRepository.findPasswordAndStatusByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User", email)
        );
    }

    @Override
    public UserDetailsDto getUserDetailsDto(String email) {
        return userRepository.findIdFioAndRoleByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User", email));
    }

    @Override
    public void activationUser(String email) {
        userRepository.updateStatusByEmail(UserStatus.ACTIVATED, email);
    }

    @Transactional
    @Override
    public User createUser(UserCreationDto userCreationDto) {
        validateEmail(userCreationDto.getMail());
        User userForSave = userRepository.saveAndFlush(userTransformer.transformEntityFromCreateDto(userCreationDto));
        String token = temporarySecretTokenService.createToken(userForSave.getEmail());
        emailService.sendSimpleMessage(
                userCreationDto.getMail(),
                emailMessageBuilder.buildVerificationSubject(),
                emailMessageBuilder.buildVerificationMessage(userCreationDto.getMail(), token)
        );
        return userForSave;
    }

    private User getUserById(UUID id) {
        return userRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User", id));
    }

    private void validateEmail(String email){
        if (userRepository.existsByEmail(email)) {
            throw new ValidationException("Данный mail уже зарегистрирован");
        }
    }
}

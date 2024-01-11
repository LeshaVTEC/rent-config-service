package rentconfigservice.controller;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rentconfigservice.core.dto.MessageResponse;
import rentconfigservice.core.dto.PageDto;
import rentconfigservice.core.dto.PasswordUpdateDto;
import rentconfigservice.core.dto.TemporarySecretTokenDto;
import rentconfigservice.core.dto.UserCreationDto;
import rentconfigservice.core.dto.UserInfoDto;
import rentconfigservice.core.dto.UserLoginDto;
import rentconfigservice.core.dto.UserRegistrationDto;
import rentconfigservice.service.AuthenticationService;
import rentconfigservice.service.UserService;
import rentconfigservice.transformer.PageTransformer;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@RestController
@RequestMapping(value = "/users")
public class UserController {

    private final UserService userService;
    private final AuthenticationService authenticationService;
    private final PageTransformer pageTransformer;

    public UserController(
            UserService userService,
            AuthenticationService authenticationService,
            PageTransformer pageTransformer
    ) {
        this.userService = userService;
        this.authenticationService = authenticationService;
        this.pageTransformer = pageTransformer;
    }

    @GetMapping
    public PageDto<UserInfoDto> getAllUsers(@RequestParam(name = "page", defaultValue = "1") Integer page,
                                            @RequestParam(value = "size", defaultValue = "20") Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return pageTransformer.transformPageDtoFromPage(userService.getAllUsers(pageable));
    }

    @GetMapping("/{id}")
    public UserInfoDto getUserById(
            @PathVariable UUID id
    ) {
        return userService.findUserById(id);
    }

    @PostMapping
    public MessageResponse createUser(@Validated @RequestBody UserCreationDto userCreationDto) {
        userService.createUserByAdmin(userCreationDto);
        return new MessageResponse("Пользователь добавлен!");
    }

    @PutMapping("/{id}/dt_update/{dt_update}")
    public MessageResponse updateUser(
            @PathVariable(name = "id") UUID id,
            @PathVariable(name="dt_update") Long updateDate,
            @Validated @RequestBody UserCreationDto userCreationDto) {
        LocalDateTime updatedDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(updateDate), ZoneId.systemDefault());
        userService.updateUser(userCreationDto, id, updatedDate);
        return new MessageResponse("Пользователь обновлён!");
    }

    @PostMapping("/registration")
    public MessageResponse registerUser(@Validated @RequestBody UserRegistrationDto userRegistrationDto) {
        authenticationService.registrateUser(userRegistrationDto);
        return new MessageResponse("Пользователь добавлен!");
    }

    @PostMapping("/login")
    public MessageResponse loginUser(@Validated @RequestBody UserLoginDto userLoginDto) {
        String token = authenticationService.loginUser(userLoginDto);
        return new MessageResponse("Вход выполнен. Токен для Authorization Header " + token);
    }

    @GetMapping("/verification")
    public MessageResponse verifyUser(
            @Validated @Email @NotNull @RequestParam String email,
            @Validated @Size(min = 36, max = 36, message = "size must be 36") @NotNull @RequestParam String token
    ) {
        TemporarySecretTokenDto temporarySecretTokenDto = new TemporarySecretTokenDto(email, UUID.fromString(token));
        authenticationService.verifyUserByEmailAndToken(temporarySecretTokenDto);
        return new MessageResponse("You have been verified");
    }

    @GetMapping("/me")
    public UserInfoDto getInfoAboutMe() {
        return authenticationService.findInfoAboutMe();
    }

    @PostMapping("/send-password-restore-link")
    public MessageResponse sendPasswordRestoreLink(
            @Validated
            @Email
            @NotNull
            @RequestParam String email
    ) {
        authenticationService.sendPasswordRestoreLink(email);
        return new MessageResponse("Ссылка на сброс пароля отправлена вам на почту");
    }

    @PostMapping("/update-password")
    public MessageResponse updatePassword(@Validated @RequestBody PasswordUpdateDto passwordUpdateDto) {
        authenticationService.updatePassword(passwordUpdateDto);
        return new MessageResponse("Пароль изменен");
    }
}

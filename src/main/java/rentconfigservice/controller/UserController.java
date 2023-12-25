package rentconfigservice.controller;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import rentconfigservice.core.dto.*;
import rentconfigservice.service.TemporarySecretTokenService;
import rentconfigservice.service.UserService;
import rentconfigservice.transformer.PageTransformer;

import java.util.UUID;

@RestController
@RequestMapping(value = "/users")
public class UserController {

    private final UserService userService;
    private final PageTransformer pageTransformer;
    private final TemporarySecretTokenService temporarySecretTokenService;

    public UserController(UserService userService, PageTransformer pageTransformer, TemporarySecretTokenService temporarySecretTokenService) {
        this.userService = userService;
        this.pageTransformer = pageTransformer;
        this.temporarySecretTokenService = temporarySecretTokenService;
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
        userService.createUser(userCreationDto);
        return new MessageResponse("Пользователь добавлен!");
    }

    @PutMapping("/{id}")
    public MessageResponse updateUser(
            @Validated @NotNull @PathVariable UUID id,
            @Validated @RequestBody UserCreationDto userCreationDto) {
        userService.updateUser(userCreationDto, id);
        return new MessageResponse("Пользователь обновлён!");
    }

    @PostMapping("/registration")
    public MessageResponse registerUser(@Validated @RequestBody UserRegistrationDto userRegistrationDto) {
        userService.registrateUser(userRegistrationDto);
        return new MessageResponse("Пользователь добавлен!");
    }

    @PostMapping("/login")
    public MessageResponse loginUser(@Validated @RequestBody UserLoginDto userLoginDto) {
        String token = userService.loginUser(userLoginDto);
        return new MessageResponse("Вход выполнен. Токен для Authorization Header " + token);
    }

    @GetMapping("/verification")
    public MessageResponse verifyUser(
            @Validated @Email @NotNull @RequestParam String email,
            @Validated @Size(min = 36, max = 36, message = "size must be 36") @NotNull @RequestParam String token
    ) {
        TemporarySecretTokenDto temporarySecretTokenDto = new TemporarySecretTokenDto(email, UUID.fromString(token));
        userService.verifyUserByEmailAndToken(temporarySecretTokenDto);
        return new MessageResponse("Пользователь верифицирован");
    }

    @GetMapping("/me")
    public UserInfoDto getInfoAboutMe() {
        return userService.findInfoAboutMe();
    }

    @PostMapping("/send-password-restore-link")
    public MessageResponse sendPasswordRestoreLink(
            @Validated
            @Email
            @NotNull
            @RequestParam String email
    ) {
        userService.sendPasswordRestoreLink(email);
        return new MessageResponse("Ссылка на сброс пароля отправлена вам на почту");
    }

    @PostMapping("/update-password")
    public MessageResponse updatePassword(@Validated @RequestBody PasswordUpdateDto passwordUpdateDto) {
        userService.updatePassword(passwordUpdateDto);
        return new MessageResponse("Пароль изменен");
    }
}

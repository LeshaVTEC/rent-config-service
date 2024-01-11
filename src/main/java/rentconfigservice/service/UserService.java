package rentconfigservice.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import rentconfigservice.core.dto.*;
import rentconfigservice.core.entity.User;

import java.time.LocalDateTime;
import java.util.UUID;

public interface UserService {

    Page<UserInfoDto> getAllUsers(Pageable pageable);

    UserInfoDto findUserById(UUID id);

    User createUserByAdmin(UserCreationDto userCreationDto);

    User createUser(UserCreationDto userCreationDto);

    User updateUser(UserCreationDto userCreationDto, UUID id, LocalDateTime updateDate);

    UserQueryDto getUserQueryDto(String email);

    UserDetailsDto getUserDetailsDto(String email);

    void activationUser(String email);
}

package rentconfigservice.core.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import rentconfigservice.core.entity.UserRole;
import rentconfigservice.core.entity.UserStatus;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class UserCreationDto {

    @Email(message = "Email should be valid")
    @NotNull
    private String mail;

    @NotNull
    @Size(min = 6, max = 12)
    private String password;

    private String fio;

    @Enumerated(EnumType.STRING)
    @NotNull
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @NotNull
    private UserStatus status;
}

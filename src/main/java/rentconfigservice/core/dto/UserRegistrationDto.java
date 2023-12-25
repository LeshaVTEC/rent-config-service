package rentconfigservice.core.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class UserRegistrationDto {

    @Email(message = "Email should be valid")
    @NotNull
    private String email;

    @NotNull
    @Size(min = 6, max = 12)
    private String password;

    private String fio;

}

package rentconfigservice.core.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class PasswordUpdateDto {

    @Size(min = 36, max = 36, message = "size must be 36 letters")
    @NotNull
    private UUID token;

    @NotNull
    @Size(min = 6, max = 12)
    private String password;
}

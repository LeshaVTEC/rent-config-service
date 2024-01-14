package rentconfigservice.core.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import rentconfigservice.core.entity.UserRole;
import rentconfigservice.core.entity.UserStatus;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class UserInfoDto implements Identifiable {

    @JsonProperty("uuid")
    private UUID id;

    @Email(message = "Email should be valid")
    @NotNull
    private String mail;

    private String fio;

    @Enumerated(EnumType.STRING)
    @NotNull
    private UserRole role;
    @Enumerated(EnumType.STRING)
    @NotNull
    private UserStatus status;

    @JsonProperty("dt_create")
    private Long createdDate;

    @JsonProperty("dt_update")
    private Long updatedDate;
}

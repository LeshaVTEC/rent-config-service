package rentconfigservice.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import rentconfigservice.core.entity.UserRole;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class UserDetailsDto implements Identifiable, Userable {

    private UUID id;

    private String email;

    private String fio;

    private UserRole role;

}

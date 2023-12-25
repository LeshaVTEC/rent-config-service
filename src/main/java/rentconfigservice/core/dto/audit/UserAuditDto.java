package rentconfigservice.core.dto.audit;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class UserAuditDto {

    private UUID userId;

    private String email;

    private String fio;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

}

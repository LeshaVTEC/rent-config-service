package rentconfigservice.core.dto;

import rentconfigservice.core.entity.UserRole;

import java.util.UUID;

public interface Userable extends Identifiable {

    UUID getId();

    String getEmail();

    String getFio();

    UserRole getRole();
}

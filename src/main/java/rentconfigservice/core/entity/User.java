package rentconfigservice.core.entity;

import jakarta.persistence.*;
import rentconfigservice.core.dto.Userable;

@Entity
@Table(schema = "config", name = "users")
public class User extends BaseEntity implements Userable {

    private String email;

    private String password;

    private String fio;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private UserRole userRole = UserRole.USER;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    public User() {
    }

    public User(String email, String password, String fio, UserRole userRole, UserStatus status) {
        this.email = email;
        this.password = password;
        this.fio = fio;
        this.userRole = userRole;
        this.status = status;

    }

    @Override
    public UserRole getRole() {
        return userRole;
    }

    public String getEmail() {
        return email;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getFio() {
        return fio;
    }

    public User setFio(String fio) {
        this.fio = fio;
        return this;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public User setUserRole(UserRole userRole) {
        this.userRole = userRole;
        return this;
    }

    public UserStatus getStatus() {
        return status;
    }

    public User setStatus(UserStatus status) {
        this.status = status;
        return this;
    }

}

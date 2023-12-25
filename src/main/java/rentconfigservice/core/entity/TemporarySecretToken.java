package rentconfigservice.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(schema = "config", name = "tokens")
public class TemporarySecretToken {

    @Id
    @Column(name = "email")
    private String email;

    @Column(name = "temporary_secret_token")
    private UUID secretToken;

    public TemporarySecretToken() {}

    public TemporarySecretToken(String email, UUID secretToken) {
        this.email = email;
        this.secretToken = secretToken;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UUID getSecretToken() {
        return secretToken;
    }

    public void setSecretToken(UUID secretToken) {
        this.secretToken = secretToken;
    }
}

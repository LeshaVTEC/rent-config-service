package rentconfigservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import rentconfigservice.core.entity.TemporarySecretToken;

import java.util.UUID;

@Repository
public interface TemporarySecretTokenRepository extends JpaRepository<TemporarySecretToken, UUID> {

    @Modifying
    @Transactional
    @Query("DELETE FROM TemporarySecretToken AS tst WHERE tst.email = :email AND tst.secretToken = :token")
    Integer deleteEntityByEmailAndToken(String email, UUID token);

    @Query("SELECT tst.email FROM TemporarySecretToken AS tst WHERE tst.secretToken = :token")
    String findEmailByToken(UUID token);

}

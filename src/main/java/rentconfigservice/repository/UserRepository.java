package rentconfigservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import rentconfigservice.core.dto.UserDetailsDto;
import rentconfigservice.core.dto.UserQueryDto;
import rentconfigservice.core.entity.User;
import rentconfigservice.core.entity.UserStatus;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Page<User> findAll(Pageable pageable);

    @Query("SELECT new rentconfigservice.core.dto.UserQueryDto(u.password, u.status) FROM User AS u WHERE u.email = :email")
    Optional<UserQueryDto> findPasswordAndStatusByEmail(String email);

    @Query("SELECT new rentconfigservice.core.dto.UserDetailsDto(u.id, u.email, u.fio, u.userRole) FROM User AS u WHERE u.email = :email")
    Optional<UserDetailsDto> findIdFioAndRoleByEmail(String email);

    @Modifying
    @Transactional
    @Query("UPDATE User AS u SET u.status = :status WHERE u.email = :email")
    void updateStatusByEmail(UserStatus status, String email);

    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);
}

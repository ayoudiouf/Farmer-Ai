package sn.farmerai.repository;

import sn.farmerai.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByTelephone(String telephone);
    boolean existsByTelephone(String telephone);
}

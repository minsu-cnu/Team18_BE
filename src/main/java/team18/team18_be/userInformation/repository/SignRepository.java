package team18.team18_be.userInformation.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import team18.team18_be.auth.entity.User;
import team18.team18_be.userInformation.entity.Sign;

public interface SignRepository extends JpaRepository<Sign, Long> {

  Optional<Sign> findByUser(User user);
}

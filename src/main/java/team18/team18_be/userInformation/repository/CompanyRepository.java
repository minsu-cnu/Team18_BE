package team18.team18_be.userInformation.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import team18.team18_be.auth.entity.User;
import team18.team18_be.userInformation.entity.Company;

public interface CompanyRepository extends JpaRepository<Company, Long> {

  Optional<List<Company>> findByUser(User user);
}

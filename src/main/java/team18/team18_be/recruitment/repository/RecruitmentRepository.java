package team18.team18_be.recruitment.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import team18.team18_be.recruitment.entity.Recruitment;
import team18.team18_be.userInformation.entity.Company;

public interface RecruitmentRepository extends JpaRepository<Recruitment, Long> {
  List<Recruitment> findByCompany(Optional<Company> company);
}

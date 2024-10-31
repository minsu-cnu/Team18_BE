package team18.team18_be.recruitment.mapper;

import java.util.NoSuchElementException;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import team18.team18_be.recruitment.dto.request.RecruitmentRequest;
import team18.team18_be.recruitment.entity.Recruitment;
import team18.team18_be.recruitment.entity.RecruitmentContent;
import team18.team18_be.userInformation.entity.Company;
import team18.team18_be.userInformation.repository.CompanyRepository;

@Mapper(componentModel = "spring")
public interface RecruitmentMapper {
  RecruitmentMapper INSTANCE = Mappers.getMapper(RecruitmentMapper.class);

  Recruitment toRecruitment(String koreanTitle, String vietnameseTitle,
      RecruitmentRequest recruitmentRequest, RecruitmentContent recruitmentContent, Company company);

}

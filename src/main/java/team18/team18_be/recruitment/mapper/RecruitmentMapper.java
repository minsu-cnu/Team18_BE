package team18.team18_be.recruitment.mapper;

import java.util.Date;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import team18.team18_be.recruitment.dto.request.RecruitmentRequest;
import team18.team18_be.recruitment.dto.response.RecruitmentResponse;
import team18.team18_be.recruitment.entity.Recruitment;
import team18.team18_be.recruitment.entity.RecruitmentContent;
import team18.team18_be.userInformation.entity.Company;

@Mapper(componentModel = "spring")
public interface RecruitmentMapper {

  RecruitmentMapper INSTANCE = Mappers.getMapper(RecruitmentMapper.class);


  Recruitment toRecruitment(String koreanTitle, String vietnameseTitle,
      RecruitmentRequest recruitmentRequest, RecruitmentContent recruitmentContent, Company company,
      Boolean hiring,
      Date uploadDate);

  RecruitmentResponse toRecruitmentResponse(Recruitment recruitment,
      RecruitmentContent recruitmentContent);
}

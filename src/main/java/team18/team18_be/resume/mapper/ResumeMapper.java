package team18.team18_be.resume.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import team18.team18_be.auth.entity.User;
import team18.team18_be.resume.dto.request.ResumeRequest;
import team18.team18_be.resume.dto.response.ResumeResponse;
import team18.team18_be.resume.entity.Resume;

@Mapper(componentModel = "spring")
public interface ResumeMapper {
  ResumeMapper INSTANCE = Mappers.getMapper(ResumeMapper.class);
  ResumeResponse toResumeResponse(Resume resume);
  ResumeResponse toResumeAndApplyResponse(Resume resume,String motivation);

  Resume toResume(ResumeRequest resumeRequest, User user);
}

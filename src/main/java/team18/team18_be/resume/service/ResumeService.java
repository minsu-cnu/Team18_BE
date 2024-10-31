package team18.team18_be.resume.service;

import java.util.NoSuchElementException;
import java.util.Objects;
import org.springframework.stereotype.Service;
import team18.team18_be.auth.entity.User;
import team18.team18_be.auth.repository.AuthRepository;
import team18.team18_be.resume.dto.request.ResumeRequest;
import team18.team18_be.resume.dto.response.ResumeResponse;
import team18.team18_be.resume.entity.Resume;
import team18.team18_be.resume.mapper.ResumeMapper;
import team18.team18_be.resume.repository.ResumeRepository;

@Service
public class ResumeService {

  private final ResumeRepository resumeRepository;
  private final ResumeMapper resumeMapper;

  public ResumeService(ResumeRepository resumeRepository,
      ResumeMapper resumeMapper) {
    this.resumeRepository = resumeRepository;
    this.resumeMapper = resumeMapper;
  }

  public void saveResume(ResumeRequest resumeRequest, User user) {
    resumeRepository.save(resumeMapper.toResume(resumeRequest, user));
  }

  public ResumeResponse findResumeByEmployeeId(User user) {
    return resumeMapper.toResumeResponse(resumeRepository.findByUser(user));
  }

  public ResumeResponse findResumeById(Long resumeId, Long userId) {
    Resume resume = resumeRepository.findById(resumeId)
        .orElseThrow(() -> new NoSuchElementException("해당하는 이력서가 존재하지 않습니다."));

    if (!Objects.equals(resume.getResumeId(), userId)) {
      //에러(권한없음)
    }

    return resumeMapper.toResumeResponse(resume);
  }

  private Resume mapResumeRequestToResume(ResumeRequest resumeRequest, User user) {
    return new Resume(resumeRequest.applicantName(), resumeRequest.address(),
        resumeRequest.phoneNumber(), resumeRequest.career(), resumeRequest.korean(),
        resumeRequest.selfIntroduction(), user);
  }

}

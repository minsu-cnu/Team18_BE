package team18.team18_be.resume.service;

import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;
import team18.team18_be.apply.entity.ApplicationForm;
import team18.team18_be.apply.entity.Apply;
import team18.team18_be.apply.repository.ApplicationFormRepository;
import team18.team18_be.apply.repository.ApplyRepository;
import team18.team18_be.auth.entity.User;
import team18.team18_be.exception.ResumeAlreadyExistsException;
import team18.team18_be.resume.dto.request.ResumeRequest;
import team18.team18_be.resume.dto.response.ResumeAndApplyResponse;
import team18.team18_be.resume.dto.response.ResumeResponse;
import team18.team18_be.resume.entity.Resume;
import team18.team18_be.resume.mapper.ResumeMapper;
import team18.team18_be.resume.repository.ResumeRepository;

@Service
public class ResumeService {

  private final ResumeRepository resumeRepository;
  private final ResumeMapper resumeMapper;
  private final ApplyRepository applyRepository;
  private final ApplicationFormRepository applicationFormRepository;

  public ResumeService(ResumeRepository resumeRepository,
      ResumeMapper resumeMapper, ApplyRepository applyRepository,
      ApplicationFormRepository applicationFormRepository) {
    this.resumeRepository = resumeRepository;
    this.resumeMapper = resumeMapper;
    this.applyRepository = applyRepository;
    this.applicationFormRepository = applicationFormRepository;
  }

  public Long saveResume(ResumeRequest resumeRequest, User user) {
    return resumeRepository.save(resumeMapper.toResume(resumeRequest, user)).getResumeId();
  }

  public ResumeResponse findResumeByEmployee(User user) {
    return resumeMapper.toResumeResponse(resumeRepository.findByUser(user));
  }

  public ResumeAndApplyResponse findResumeById(Long resumeId, Long applyId) {
    Resume resume = resumeRepository.findById(resumeId)
        .orElseThrow(() -> new NoSuchElementException("해당하는 이력서가 존재하지 않습니다."));
    Apply apply = applyRepository.findById(applyId)
        .orElseThrow(() -> new NoSuchElementException("해당하는 지원이 존재하지 않습니다."));
    ApplicationForm applicationForm = applicationFormRepository.findByApply(apply)
        .orElseThrow(() -> new NoSuchElementException("해당하는 지원이 존재하지 않습니다."));
    return resumeMapper.toResumeAndApplyResponse(resume, applicationForm.getMotivation());
  }

  public void existence(User user) {
    if (resumeRepository.findByUser(user) != null) {
      throw new ResumeAlreadyExistsException("해당 유저에 대한 이력서가 존재합니다.");
    }
  }
}

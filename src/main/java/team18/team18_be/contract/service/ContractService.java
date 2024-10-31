package team18.team18_be.contract.service;

import com.itextpdf.text.DocumentException;
import jakarta.transaction.Transactional;
import java.io.IOException;
import org.springframework.stereotype.Service;
import team18.team18_be.apply.entity.Apply;
import team18.team18_be.apply.repository.ApplyRepository;
import team18.team18_be.auth.entity.User;
import team18.team18_be.common.FileService;
import team18.team18_be.common.PdfService;
import team18.team18_be.contract.dto.request.ContractRequest;
import team18.team18_be.contract.dto.response.ContractResponse;
import team18.team18_be.contract.entity.Contract;
import team18.team18_be.contract.repository.ContractRepository;
import team18.team18_be.exception.NotFoundException;

@Service
public class ContractService {

  private final ContractRepository contractRepository;
  private final ApplyRepository applyRepository;
  private final FileService fileService;
  private final PdfService pdfService;

  public ContractService(ContractRepository contractRepository, ApplyRepository applyRepository,
      FileService fileService, PdfService pdfService) {
    this.contractRepository = contractRepository;
    this.applyRepository = applyRepository;
    this.fileService = fileService;
    this.pdfService = pdfService;
  }

  public void handleEmployerContractCreation(ContractRequest request, User user)
      throws DocumentException, IOException {
    byte[] pdfData = pdfService.createPdf(request, user);
    String dirName = "contracts";
    String fileName = user.getId() + "_" + request.applyId() + ".pdf";
    String pdfUrl = fileService.uploadContractPdf(pdfData, dirName, fileName);
    createContract(request, pdfUrl);
  }

  public void handleEmployeeSignature(ContractRequest request, User user)
      throws DocumentException, IOException {
    byte[] pdfData = fileService.getPdf(request);
    byte[] updatedPdfData = pdfService.fillInEmployeeSign(pdfData, user);
    byte[] image = pdfService.convertPdfToImage(updatedPdfData);

    String dirName = "contracts";
    String pdfFileName = user.getId() + "_" + request.applyId() + "update.pdf";
    String imageFileName = user.getId() + "_" + request.applyId() + "update.png";

    String pdfUrl = fileService.uploadContractPdf(updatedPdfData, dirName, pdfFileName);
    String imageUrl = fileService.uploadContractPdf(image, dirName, imageFileName);

    updateContractFiles(request, pdfUrl, imageUrl);
  }

  public void createContract(ContractRequest request, String pdfUrl) {

    Apply apply = applyRepository.findById(request.applyId())
        .orElseThrow(() -> new NotFoundException("해당 applyId가 존재하지 않습니다."));

    contractRepository.save(
        Contract.builder()
            .salary(request.salary())
            .workingHours(request.workingHours())
            .imageFileUrl(null)
            .pdfFileUrl(pdfUrl)
            .annualPaidLeave(request.annualPaidLeave())
            .dayOff(request.dayOff())
            .rule(request.rule())
            .apply(apply).build()
    );
  }

  @Transactional
  private void updateContractFiles(ContractRequest request, String pdfUrl, String imageUrl) {
    Contract contract = contractRepository.findByApplyId(request.applyId())
        .orElseThrow(() -> new NotFoundException("해당 applyId의 Contract가 존재하지 않습니다."));
    contract.updatePdfFileUrl(pdfUrl);
    contract.updateImageFileUrl(imageUrl);
  }

  public ContractResponse getContractPdfUrl(Long applyId) {
    Contract contract = contractRepository.findByApplyId(applyId)
        .orElseThrow(() -> new NotFoundException("해당 applyId의 Contract 객체를 찾을 수 없습니다."));
    return new ContractResponse(contract.getPdfFileUrl());
  }

  public ContractResponse getContractImageUrl(Long applyId) {
    Contract contract = contractRepository.findByApplyId(applyId)
        .orElseThrow(() -> new NotFoundException("해당 applyId의 Contract 객체를 찾을 수 없습니다."));
    return new ContractResponse(contract.getImageFileUrl());
  }

}

package team18.team18_be.contract.controller;

import com.itextpdf.text.DocumentException;
import io.swagger.annotations.ApiOperation;
import jakarta.validation.Valid;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team18.team18_be.auth.entity.User;
import team18.team18_be.config.resolver.LoginUser;
import team18.team18_be.contract.dto.request.ContractRequest;
import team18.team18_be.contract.dto.response.ContractResponse;
import team18.team18_be.contract.service.ContractService;

@RestController
@RequestMapping("/api/contract")
public class ContractController {

  private final ContractService contractService;

  public ContractController(ContractService contractService) {
    this.contractService = contractService;
  }

  @ApiOperation(value = "근로계약서 등록 메서드 - 고용주 등록")
  @PostMapping
  public ResponseEntity<Void> makeContract(@Valid @RequestBody ContractRequest request,
      @LoginUser User user) throws DocumentException, IOException {
    contractService.handleEmployerContractCreation(request, user);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @ApiOperation(value = "근로계약서 등록 메서드 - 근로자 서명 등록")
  @PostMapping("/employee")
  public ResponseEntity<Void> fillInEmployeeSign(@Valid @RequestBody ContractRequest request,
      @LoginUser User user) throws IOException, DocumentException {
    contractService.handleEmployeeSignature(request, user);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @ApiOperation(value = "근로계약서 id별 pdf url 반환 메서드")
  @GetMapping("/{applyId}/download")
  public ResponseEntity<ContractResponse> downloadContract(@PathVariable("applyId") Long id) {
    return ResponseEntity.ok(contractService.getContractPdfUrl(id));
  }

  @ApiOperation(value = "근로계약서 id별 image url 반환 메서드")
  @GetMapping("/{applyId}/preview")
  public ResponseEntity<ContractResponse> previewContract(@PathVariable("applyId") Long id) {
    return ResponseEntity.ok(contractService.getContractImageUrl(id));
  }
}

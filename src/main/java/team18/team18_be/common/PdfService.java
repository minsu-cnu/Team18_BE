package team18.team18_be.common;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import team18.team18_be.auth.entity.User;
import team18.team18_be.contract.dto.request.ContractRequest;

@Service
public class PdfService {

  private final FileService fileService;

  public PdfService(FileService fileService) {
    this.fileService = fileService;
  }

  public byte[] createPdf(ContractRequest request, User user, String pdfName)
      throws DocumentException, IOException {

    ClassPathResource resource = new ClassPathResource(pdfName);
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

    // PDF 준비
    PdfStamper stamper = preparePdfStamper(resource.getContentAsByteArray(), byteArrayOutputStream);
    PdfContentByte contentByte = stamper.getOverContent(1);

    // 폰트 준비
    BaseFont font = prepareFont();
    contentByte.setFontAndSize(font, 10);

    // 글자 추가
    contentByte.showTextAligned(Paragraph.ALIGN_CENTER, Integer.toString(request.salary()), 350,
        630, 0);
    contentByte.showTextAligned(Paragraph.ALIGN_CENTER, request.workingHours(), 350, 550, 0);
    contentByte.showTextAligned(Paragraph.ALIGN_CENTER, request.dayOff(), 350, 475, 0);
    contentByte.showTextAligned(Paragraph.ALIGN_CENTER, request.annualPaidLeave(), 350, 400, 0);
    contentByte.showTextAligned(Paragraph.ALIGN_CENTER,
        request.workingPlace() + " / " + request.responsibilities(), 350, 320, 0);
    contentByte.showTextAligned(Paragraph.ALIGN_CENTER, request.rule(), 350, 250, 0);

    // 서명 추가
    contentByte.showTextAligned(Paragraph.ALIGN_CENTER, user.getName(), 457, 123, 0);

    // 고용주 서명 받기
    byte[] imageBytes = fileService.getSignImage(user);
    addImageToPdf(contentByte, imageBytes, 50, 50, 465, 103);

    // PdfStamper 닫기
    stamper.close();

    return byteArrayOutputStream.toByteArray();
  }

  public byte[] fillInEmployeeSign(byte[] pdfData, User user)
      throws IOException, DocumentException {

    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    PdfStamper stamper = preparePdfStamper(pdfData, byteArrayOutputStream);
    PdfContentByte contentByte = stamper.getUnderContent(1);

    // 폰트 준비
    BaseFont font = prepareFont();
    contentByte.setFontAndSize(font, 10);

    // 서명 추가
    contentByte.showTextAligned(Paragraph.ALIGN_CENTER, user.getName(), 457, 98, 0);

    // 근로자 서명 받기
    byte[] imageBytes = fileService.getSignImage(user);
    addImageToPdf(contentByte, imageBytes, 50, 50, 465, 83);

    // PdfStamper 닫기
    stamper.close();

    return byteArrayOutputStream.toByteArray();
  }

  private PdfStamper preparePdfStamper(byte[] pdfData, ByteArrayOutputStream byteArrayOutputStream)
      throws IOException, DocumentException {
    PdfReader reader = new PdfReader(pdfData);
    return new PdfStamper(reader, byteArrayOutputStream);
  }

  private BaseFont prepareFont() throws DocumentException, IOException {
    // 폰트 지정
    return BaseFont.createFont("gothic.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
  }

  public byte[] convertPdfToImage(byte[] pdfData) throws IOException {
    try (PDDocument document = PDDocument.load(pdfData)) {
      PDFRenderer pdfRenderer = new PDFRenderer(document);
      BufferedImage image = pdfRenderer.renderImageWithDPI(0, 300, ImageType.RGB);
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      ImageIO.write(image, "png", byteArrayOutputStream);
      return byteArrayOutputStream.toByteArray();
    }
  }

  private void addImageToPdf(PdfContentByte contentByte, byte[] imageBytes, int width, int height,
      int x, int y) throws IOException, DocumentException {
    Image image = Image.getInstance(imageBytes);
    image.scaleToFit(width, height);
    image.setAbsolutePosition(x, y);
    contentByte.addImage(image);
  }

}

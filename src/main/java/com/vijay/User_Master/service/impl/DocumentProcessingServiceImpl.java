package com.vijay.User_Master.service.impl;

import com.vijay.User_Master.entity.Document;
import com.vijay.User_Master.service.DocumentProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Implementation of DocumentProcessingService for extracting text from various document types
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentProcessingServiceImpl implements DocumentProcessingService {

    @Value("${app.document.upload-dir:uploads/documents}")
    private String uploadDir;

    private final Tika tika = new Tika();

    @Override
    public DocumentProcessingResult processDocument(MultipartFile file, Long ownerId, String category, String description, String tags) {
        try {
            log.info("Processing document: {} for owner: {}", file.getOriginalFilename(), ownerId);

            // Validate document
            DocumentValidationResult validation = validateDocument(file);
            if (!validation.isValid()) {
                return DocumentProcessingResult.error(validation.getErrorMessage());
            }

            // Create upload directory if not exists
            Path uploadPath = Paths.get(uploadDir, ownerId.toString());
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate unique filename
            String fileExtension = getFileExtension(file.getOriginalFilename());
            String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
            Path filePath = uploadPath.resolve(uniqueFileName);

            // Save file
            Files.copy(file.getInputStream(), filePath);

            // Create document entity
            Document document = new Document();
            document.setFileName(uniqueFileName);
            document.setOriginalFileName(file.getOriginalFilename());
            document.setFileType(validation.getFileType());
            document.setMimeType(validation.getMimeType());
            document.setFileSize(file.getSize());
            document.setFilePath(filePath.toString());
            document.setOwnerId(ownerId);
            document.setCategory(category != null ? category : "general");
            document.setTags(tags);
            document.setDescription(description);
            document.setIsProcessed(false);
            document.setCreatedAt(LocalDateTime.now());
            document.setUpdatedAt(LocalDateTime.now());

            // Extract text content
            String extractedContent = extractTextContent(file.getInputStream(), file.getOriginalFilename(), validation.getMimeType());
            document.setExtractedContent(extractedContent);

            // Generate summary
            String summary = generateDocumentSummary(extractedContent, file.getOriginalFilename(), category);
            document.setSummary(summary);

            document.setIsProcessed(true);
            document.setProcessedAt(LocalDateTime.now());

            log.info("Successfully processed document: {} with {} characters extracted", 
                    file.getOriginalFilename(), extractedContent.length());

            return DocumentProcessingResult.success(extractedContent, summary, document, filePath.toString());

        } catch (Exception e) {
            log.error("Error processing document: {}", e.getMessage(), e);
            return DocumentProcessingResult.error("Error processing document: " + e.getMessage());
        }
    }

    @Override
    public DocumentProcessingResult processDocumentFromPath(String filePath, Long ownerId, String category, String description, String tags) {
        try {
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                return DocumentProcessingResult.error("File not found: " + filePath);
            }

            String fileName = path.getFileName().toString();
            String mimeType = tika.detect(path.toFile());

            // Validate file type
            if (!isSupportedFileType(fileName, mimeType)) {
                return DocumentProcessingResult.error("Unsupported file type: " + fileName);
            }

            // Create document entity
            Document document = new Document();
            document.setFileName(fileName);
            document.setOriginalFileName(fileName);
            document.setFileType(Document.DocumentType.fromMimeType(mimeType).name());
            document.setMimeType(mimeType);
            document.setFileSize(Files.size(path));
            document.setFilePath(filePath);
            document.setOwnerId(ownerId);
            document.setCategory(category != null ? category : "general");
            document.setTags(tags);
            document.setDescription(description);
            document.setIsProcessed(false);
            document.setCreatedAt(LocalDateTime.now());
            document.setUpdatedAt(LocalDateTime.now());

            // Extract text content
            try (InputStream inputStream = Files.newInputStream(path)) {
                String extractedContent = extractTextContent(inputStream, fileName, mimeType);
                document.setExtractedContent(extractedContent);

                // Generate summary
                String summary = generateDocumentSummary(extractedContent, fileName, category);
                document.setSummary(summary);

                document.setIsProcessed(true);
                document.setProcessedAt(LocalDateTime.now());

                return DocumentProcessingResult.success(extractedContent, summary, document, filePath);
            }

        } catch (Exception e) {
            log.error("Error processing document from path: {}", e.getMessage(), e);
            return DocumentProcessingResult.error("Error processing document: " + e.getMessage());
        }
    }

    @Override
    public String extractTextContent(InputStream inputStream, String fileName, String mimeType) {
        try {
            String fileExtension = getFileExtension(fileName).toLowerCase();

            switch (fileExtension) {
                case ".pdf":
                    return extractFromPDF(inputStream);
                case ".doc":
                    return extractFromWordDoc(inputStream);
                case ".docx":
                    return extractFromWordDocx(inputStream);
                case ".xls":
                    return extractFromExcelXls(inputStream);
                case ".xlsx":
                    return extractFromExcelXlsx(inputStream);
                case ".ppt":
                    return extractFromPowerPointPpt(inputStream);
                case ".pptx":
                    return extractFromPowerPointPptx(inputStream);
                default:
                    // Try Tika as fallback
                    return tika.parseToString(inputStream);
            }

        } catch (Exception e) {
            log.error("Error extracting text from document: {}", e.getMessage(), e);
            return "Error extracting text content: " + e.getMessage();
        }
    }

    @Override
    public String generateDocumentSummary(String content, String fileName, String category) {
        if (content == null || content.trim().isEmpty()) {
            return "No content available for summary generation.";
        }

        // Simple summary generation (first 200 characters + word count)
        String preview = content.length() > 200 ? content.substring(0, 200) + "..." : content;
        int wordCount = content.split("\\s+").length;

        return String.format("Document: %s\nCategory: %s\nWord Count: %d\nPreview: %s", 
                fileName, category != null ? category : "General", wordCount, preview);
    }

    @Override
    public DocumentValidationResult validateDocument(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return DocumentValidationResult.invalid("File is empty");
            }

            if (file.getSize() > 50 * 1024 * 1024) { // 50MB limit
                return DocumentValidationResult.invalid("File size exceeds 50MB limit");
            }

            String originalFileName = file.getOriginalFilename();
            if (originalFileName == null || originalFileName.trim().isEmpty()) {
                return DocumentValidationResult.invalid("File name is required");
            }

            String mimeType = file.getContentType();
            if (!isSupportedFileType(originalFileName, mimeType)) {
                return DocumentValidationResult.invalid("Unsupported file type. Supported types: " + 
                        String.join(", ", getSupportedFileTypes()));
            }

            Document.DocumentType documentType = Document.DocumentType.fromFileName(originalFileName);
            return DocumentValidationResult.valid(documentType.name(), documentType.getMimeType());

        } catch (Exception e) {
            log.error("Error validating document: {}", e.getMessage(), e);
            return DocumentValidationResult.invalid("Error validating document: " + e.getMessage());
        }
    }

    @Override
    public String[] getSupportedFileTypes() {
        return new String[]{"pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx"};
    }

    @Override
    public boolean isSupportedFileType(String fileName, String mimeType) {
        try {
            if (fileName == null) return false;
            Document.DocumentType.fromFileName(fileName);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    // Private helper methods for text extraction

    private String extractFromPDF(InputStream inputStream) throws Exception {
        try (PDDocument document = PDDocument.load(inputStream)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    private String extractFromWordDoc(InputStream inputStream) throws Exception {
        try (HWPFDocument document = new HWPFDocument(inputStream)) {
            WordExtractor extractor = new WordExtractor(document);
            return extractor.getText();
        }
    }

    private String extractFromWordDocx(InputStream inputStream) throws Exception {
        StringBuilder content = new StringBuilder();
        try (XWPFDocument document = new XWPFDocument(inputStream)) {
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                content.append(paragraph.getText()).append("\n");
            }
        }
        return content.toString();
    }

    private String extractFromExcelXls(InputStream inputStream) throws Exception {
        StringBuilder content = new StringBuilder();
        try (HSSFWorkbook workbook = new HSSFWorkbook(inputStream)) {
            content.append(extractExcelContent(workbook));
        }
        return content.toString();
    }

    private String extractFromExcelXlsx(InputStream inputStream) throws Exception {
        StringBuilder content = new StringBuilder();
        try (XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {
            content.append(extractExcelContent(workbook));
        }
        return content.toString();
    }

    private String extractExcelContent(Workbook workbook) {
        StringBuilder content = new StringBuilder();
        for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
            Sheet sheet = workbook.getSheetAt(sheetIndex);
            content.append("Sheet: ").append(sheet.getSheetName()).append("\n");
            
            for (Row row : sheet) {
                StringBuilder rowContent = new StringBuilder();
                for (Cell cell : row) {
                    String cellValue = getCellValueAsString(cell);
                    if (!cellValue.isEmpty()) {
                        rowContent.append(cellValue).append("\t");
                    }
                }
                if (rowContent.length() > 0) {
                    content.append(rowContent.toString().trim()).append("\n");
                }
            }
            content.append("\n");
        }
        return content.toString();
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    private String extractFromPowerPointPpt(InputStream inputStream) throws Exception {
        // For .ppt files, we'll use Tika as POI support is limited
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            return tika.parseToString(new ByteArrayInputStream(baos.toByteArray()));
        }
    }

    private String extractFromPowerPointPptx(InputStream inputStream) throws Exception {
        // For .pptx files, we'll use Tika as it handles PowerPoint better
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            return tika.parseToString(new ByteArrayInputStream(baos.toByteArray()));
        }
    }

    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        return lastDotIndex > 0 ? fileName.substring(lastDotIndex) : "";
    }
}

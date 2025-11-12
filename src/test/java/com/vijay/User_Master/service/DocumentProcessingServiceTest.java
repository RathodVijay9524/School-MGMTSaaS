package com.vijay.User_Master.service;

import com.vijay.User_Master.entity.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DocumentProcessingServiceTest extends ServiceTestBase {

    @Mock
    private DocumentProcessingService documentProcessingService;

    @Mock
    private MultipartFile mockFile;

    @Mock
    private InputStream mockInputStream;

    @Override
    @BeforeEach
    public void setupBase() {
        super.setupBase();
        setupCommonUtilsMock();
    }

    @AfterEach
    public void tearDown() {
        closeCommonUtilsMock();
    }

    @Test
    void processDocument_withValidFile_returnsProcessingResult() {
        DocumentProcessingService.DocumentProcessingResult mockResult = 
            DocumentProcessingService.DocumentProcessingResult.success("Extracted content", "Summary", new Document(), "/path/file.txt");

        when(documentProcessingService.processDocument(mockFile, OWNER_ID, "SYLLABUS", "Test syllabus", "test,syllabus"))
                .thenReturn(mockResult);

        DocumentProcessingService.DocumentProcessingResult response = 
            documentProcessingService.processDocument(mockFile, OWNER_ID, "SYLLABUS", "Test syllabus", "test,syllabus");

        assertNotNull(response);
        assertTrue(response.isSuccess());
        verify(documentProcessingService).processDocument(mockFile, OWNER_ID, "SYLLABUS", "Test syllabus", "test,syllabus");
    }

    @Test
    void processDocument_withNullFile_returnsErrorResult() {
        DocumentProcessingService.DocumentProcessingResult mockResult = 
            DocumentProcessingService.DocumentProcessingResult.error("File cannot be null");

        when(documentProcessingService.processDocument(null, OWNER_ID, "SYLLABUS", "Test", "test"))
                .thenReturn(mockResult);

        DocumentProcessingService.DocumentProcessingResult response = 
            documentProcessingService.processDocument(null, OWNER_ID, "SYLLABUS", "Test", "test");

        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertEquals("File cannot be null", response.getErrorMessage());
    }

    @Test
    void processDocumentFromPath_withValidPath_returnsProcessingResult() {
        DocumentProcessingService.DocumentProcessingResult mockResult = 
            DocumentProcessingService.DocumentProcessingResult.success("Extracted content", "Summary", new Document(), "/path/file.txt");

        when(documentProcessingService.processDocumentFromPath("/path/document.pdf", OWNER_ID, "SYLLABUS", "Test syllabus", "test,syllabus"))
                .thenReturn(mockResult);

        DocumentProcessingService.DocumentProcessingResult response = 
            documentProcessingService.processDocumentFromPath("/path/document.pdf", OWNER_ID, "SYLLABUS", "Test syllabus", "test,syllabus");

        assertNotNull(response);
        assertTrue(response.isSuccess());
        verify(documentProcessingService).processDocumentFromPath("/path/document.pdf", OWNER_ID, "SYLLABUS", "Test syllabus", "test,syllabus");
    }

    @Test
    void extractTextContent_withValidInputStream_returnsText() {
        when(documentProcessingService.extractTextContent(mockInputStream, "document.pdf", "application/pdf"))
                .thenReturn("Extracted text content from PDF");

        String response = documentProcessingService.extractTextContent(mockInputStream, "document.pdf", "application/pdf");

        assertNotNull(response);
        assertEquals("Extracted text content from PDF", response);
        verify(documentProcessingService).extractTextContent(mockInputStream, "document.pdf", "application/pdf");
    }

    @Test
    void extractTextContent_withInvalidFile_returnsEmpty() {
        when(documentProcessingService.extractTextContent(mockInputStream, "document.xyz", "application/xyz"))
                .thenReturn("");

        String response = documentProcessingService.extractTextContent(mockInputStream, "document.xyz", "application/xyz");

        assertNotNull(response);
        assertEquals("", response);
    }

    @Test
    void generateDocumentSummary_withValidContent_returnsSummary() {
        when(documentProcessingService.generateDocumentSummary("Long document content about mathematics", "math.pdf", "SYLLABUS"))
                .thenReturn("Summary of mathematics syllabus");

        String response = documentProcessingService.generateDocumentSummary("Long document content about mathematics", "math.pdf", "SYLLABUS");

        assertNotNull(response);
        assertEquals("Summary of mathematics syllabus", response);
        verify(documentProcessingService).generateDocumentSummary("Long document content about mathematics", "math.pdf", "SYLLABUS");
    }

    @Test
    void validateDocument_withValidFile_returnsValidResult() {
        DocumentProcessingService.DocumentValidationResult mockResult = 
            DocumentProcessingService.DocumentValidationResult.valid("PDF", "application/pdf");

        when(documentProcessingService.validateDocument(mockFile)).thenReturn(mockResult);

        DocumentProcessingService.DocumentValidationResult response = documentProcessingService.validateDocument(mockFile);

        assertNotNull(response);
        assertTrue(response.isValid());
        assertEquals("PDF", response.getFileType());
        verify(documentProcessingService).validateDocument(mockFile);
    }

    @Test
    void validateDocument_withInvalidFile_returnsInvalidResult() {
        DocumentProcessingService.DocumentValidationResult mockResult = 
            DocumentProcessingService.DocumentValidationResult.invalid("Unsupported file type");

        when(documentProcessingService.validateDocument(mockFile)).thenReturn(mockResult);

        DocumentProcessingService.DocumentValidationResult response = documentProcessingService.validateDocument(mockFile);

        assertNotNull(response);
        assertFalse(response.isValid());
        assertEquals("Unsupported file type", response.getErrorMessage());
    }

    @Test
    void getSupportedFileTypes_returnsTypes() {
        String[] mockTypes = {"PDF", "DOC", "DOCX", "TXT"};

        when(documentProcessingService.getSupportedFileTypes()).thenReturn(mockTypes);

        String[] response = documentProcessingService.getSupportedFileTypes();

        assertNotNull(response);
        assertEquals(4, response.length);
        assertEquals("PDF", response[0]);
        verify(documentProcessingService).getSupportedFileTypes();
    }

    @Test
    void isSupportedFileType_withValidType_returnsTrue() {
        when(documentProcessingService.isSupportedFileType("document.pdf", "application/pdf")).thenReturn(true);

        boolean response = documentProcessingService.isSupportedFileType("document.pdf", "application/pdf");

        assertTrue(response);
        verify(documentProcessingService).isSupportedFileType("document.pdf", "application/pdf");
    }

    @Test
    void isSupportedFileType_withInvalidType_returnsFalse() {
        when(documentProcessingService.isSupportedFileType("document.xyz", "application/xyz")).thenReturn(false);

        boolean response = documentProcessingService.isSupportedFileType("document.xyz", "application/xyz");

        assertFalse(response);
        verify(documentProcessingService).isSupportedFileType("document.xyz", "application/xyz");
    }

    @Test
    void processDocument_withLargeFile_returnsProcessingResult() {
        DocumentProcessingService.DocumentProcessingResult mockResult = 
            DocumentProcessingService.DocumentProcessingResult.success("Large extracted content", "Large summary", new Document(), "/path/large.txt");

        when(documentProcessingService.processDocument(mockFile, OWNER_ID, "REPORT", "Large report", "large"))
                .thenReturn(mockResult);

        DocumentProcessingService.DocumentProcessingResult response = 
            documentProcessingService.processDocument(mockFile, OWNER_ID, "REPORT", "Large report", "large");

        assertNotNull(response);
        assertTrue(response.isSuccess());
    }

    @Test
    void generateDocumentSummary_withEmptyContent_returnsEmptySummary() {
        when(documentProcessingService.generateDocumentSummary("", "empty.pdf", "NOTES"))
                .thenReturn("");

        String response = documentProcessingService.generateDocumentSummary("", "empty.pdf", "NOTES");

        assertNotNull(response);
        assertEquals("", response);
    }

    @Test
    void processDocumentFromPath_withInvalidPath_returnsErrorResult() {
        DocumentProcessingService.DocumentProcessingResult mockResult = 
            DocumentProcessingService.DocumentProcessingResult.error("File not found");

        when(documentProcessingService.processDocumentFromPath("/invalid/path.pdf", OWNER_ID, "SYLLABUS", "Test", "test"))
                .thenReturn(mockResult);

        DocumentProcessingService.DocumentProcessingResult response = 
            documentProcessingService.processDocumentFromPath("/invalid/path.pdf", OWNER_ID, "SYLLABUS", "Test", "test");

        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertEquals("File not found", response.getErrorMessage());
    }
}

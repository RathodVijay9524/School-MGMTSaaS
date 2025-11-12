package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.DocumentStatistics;
import com.vijay.User_Master.entity.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DocumentServiceTest extends ServiceTestBase {

    @Mock
    private DocumentService documentService;

    @Mock
    private MultipartFile mockFile;

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
    void uploadDocument_withValidFile_returnsDocument() {
        Document mockDocument = new Document();
        mockDocument.setId(1L);

        when(documentService.uploadDocument(mockFile, OWNER_ID, "SYLLABUS", "Test syllabus", "test,syllabus"))
                .thenReturn(mockDocument);

        Document response = documentService.uploadDocument(mockFile, OWNER_ID, "SYLLABUS", "Test syllabus", "test,syllabus");

        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(documentService).uploadDocument(mockFile, OWNER_ID, "SYLLABUS", "Test syllabus", "test,syllabus");
    }

    @Test
    void uploadDocument_withNullFile_throwsException() {
        when(documentService.uploadDocument(null, OWNER_ID, "SYLLABUS", "Test", "test"))
                .thenThrow(new IllegalArgumentException("File cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                documentService.uploadDocument(null, OWNER_ID, "SYLLABUS", "Test", "test"));
    }

    @Test
    void getDocumentById_withValidId_returnsDocument() {
        Document mockDocument = new Document();
        mockDocument.setId(1L);

        when(documentService.getDocumentById(1L, OWNER_ID)).thenReturn(Optional.of(mockDocument));

        Optional<Document> response = documentService.getDocumentById(1L, OWNER_ID);

        assertTrue(response.isPresent());
        assertEquals(1L, response.get().getId());
    }

    @Test
    void getDocumentById_withInvalidId_returnsEmpty() {
        when(documentService.getDocumentById(999L, OWNER_ID)).thenReturn(Optional.empty());

        Optional<Document> response = documentService.getDocumentById(999L, OWNER_ID);

        assertFalse(response.isPresent());
    }

    @Test
    void getDocuments_withValidOwner_returnsPagedDocuments() {
        List<Document> documents = new ArrayList<>();
        documents.add(new Document());
        Page<Document> page = new PageImpl<>(documents);

        when(documentService.getDocuments(OWNER_ID, 0, 10, "id", "ASC")).thenReturn(page);

        Page<Document> response = documentService.getDocuments(OWNER_ID, 0, 10, "id", "ASC");

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
    }

    @Test
    void getDocumentsByCategory_withValidCategory_returnsDocuments() {
        List<Document> documents = new ArrayList<>();
        documents.add(new Document());
        Page<Document> page = new PageImpl<>(documents);

        when(documentService.getDocumentsByCategory(OWNER_ID, "SYLLABUS", 0, 10, "id", "ASC")).thenReturn(page);

        Page<Document> response = documentService.getDocumentsByCategory(OWNER_ID, "SYLLABUS", 0, 10, "id", "ASC");

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
    }

    @Test
    void getDocumentsByFileType_withValidFileType_returnsDocuments() {
        List<Document> documents = new ArrayList<>();
        documents.add(new Document());
        Page<Document> page = new PageImpl<>(documents);

        when(documentService.getDocumentsByFileType(OWNER_ID, "PDF", 0, 10, "id", "ASC")).thenReturn(page);

        Page<Document> response = documentService.getDocumentsByFileType(OWNER_ID, "PDF", 0, 10, "id", "ASC");

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
    }

    @Test
    void searchDocuments_withValidQuery_returnsDocuments() {
        List<Document> documents = new ArrayList<>();
        documents.add(new Document());
        Page<Document> page = new PageImpl<>(documents);

        when(documentService.searchDocuments(OWNER_ID, "mathematics", 0, 10, "id", "ASC")).thenReturn(page);

        Page<Document> response = documentService.searchDocuments(OWNER_ID, "mathematics", 0, 10, "id", "ASC");

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
    }

    @Test
    void searchDocumentsByTags_withValidTags_returnsDocuments() {
        List<Document> documents = new ArrayList<>();
        documents.add(new Document());
        Page<Document> page = new PageImpl<>(documents);

        when(documentService.searchDocumentsByTags(OWNER_ID, "important,exam", 0, 10, "id", "ASC")).thenReturn(page);

        Page<Document> response = documentService.searchDocumentsByTags(OWNER_ID, "important,exam", 0, 10, "id", "ASC");

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
    }

    @Test
    void getProcessedDocuments_withValidOwner_returnsProcessedDocuments() {
        List<Document> documents = new ArrayList<>();
        documents.add(new Document());
        Page<Document> page = new PageImpl<>(documents);

        when(documentService.getProcessedDocuments(OWNER_ID, 0, 10, "id", "ASC")).thenReturn(page);

        Page<Document> response = documentService.getProcessedDocuments(OWNER_ID, 0, 10, "id", "ASC");

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
    }

    @Test
    void getUnprocessedDocuments_withValidOwner_returnsUnprocessedDocuments() {
        List<Document> mockDocuments = new ArrayList<>();
        mockDocuments.add(new Document());

        when(documentService.getUnprocessedDocuments(OWNER_ID)).thenReturn(mockDocuments);

        List<Document> response = documentService.getUnprocessedDocuments(OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void processUnprocessedDocuments_withValidOwner_returnsProcessedDocuments() {
        List<Document> mockDocuments = new ArrayList<>();
        mockDocuments.add(new Document());

        when(documentService.processUnprocessedDocuments(OWNER_ID)).thenReturn(mockDocuments);

        List<Document> response = documentService.processUnprocessedDocuments(OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void updateDocument_withValidData_returnsUpdatedDocument() {
        Document mockDocument = new Document();
        mockDocument.setId(1L);

        when(documentService.updateDocument(1L, OWNER_ID, "NOTES", "Updated notes", "notes,updated"))
                .thenReturn(mockDocument);

        Document response = documentService.updateDocument(1L, OWNER_ID, "NOTES", "Updated notes", "notes,updated");

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void deleteDocument_withValidId_returnsTrue() {
        when(documentService.deleteDocument(1L, OWNER_ID)).thenReturn(true);

        boolean result = documentService.deleteDocument(1L, OWNER_ID);

        assertTrue(result);
        verify(documentService).deleteDocument(1L, OWNER_ID);
    }

    @Test
    void deleteDocument_withInvalidId_returnsFalse() {
        when(documentService.deleteDocument(999L, OWNER_ID)).thenReturn(false);

        boolean result = documentService.deleteDocument(999L, OWNER_ID);

        assertFalse(result);
    }

    @Test
    void getDocumentStatistics_withValidOwner_returnsStatistics() {
        DocumentStatistics mockStats = new DocumentStatistics();

        when(documentService.getDocumentStatistics(OWNER_ID)).thenReturn(mockStats);

        DocumentStatistics response = documentService.getDocumentStatistics(OWNER_ID);

        assertNotNull(response);
        verify(documentService).getDocumentStatistics(OWNER_ID);
    }

    @Test
    void getDocumentsForRAGQuery_withValidQuery_returnsDocuments() {
        List<Document> mockDocuments = new ArrayList<>();
        mockDocuments.add(new Document());

        when(documentService.getDocumentsForRAGQuery(OWNER_ID, "algebra")).thenReturn(mockDocuments);

        List<Document> response = documentService.getDocumentsForRAGQuery(OWNER_ID, "algebra");

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void bulkUploadDocuments_withValidFiles_returnsDocuments() {
        List<MultipartFile> files = new ArrayList<>();
        files.add(mockFile);

        List<Document> mockDocuments = new ArrayList<>();
        mockDocuments.add(new Document());

        when(documentService.bulkUploadDocuments(files, OWNER_ID, "SYLLABUS", "Bulk upload", "bulk"))
                .thenReturn(mockDocuments);

        List<Document> response = documentService.bulkUploadDocuments(files, OWNER_ID, "SYLLABUS", "Bulk upload", "bulk");

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void bulkUploadDocuments_withEmptyList_returnsEmptyList() {
        List<MultipartFile> files = new ArrayList<>();
        List<Document> mockDocuments = new ArrayList<>();

        when(documentService.bulkUploadDocuments(files, OWNER_ID, "SYLLABUS", "Bulk upload", "bulk"))
                .thenReturn(mockDocuments);

        List<Document> response = documentService.bulkUploadDocuments(files, OWNER_ID, "SYLLABUS", "Bulk upload", "bulk");

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    void getDocuments_withMultipleDocuments_returnsAllDocuments() {
        List<Document> documents = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            documents.add(new Document());
        }
        Page<Document> page = new PageImpl<>(documents);

        when(documentService.getDocuments(OWNER_ID, 0, 10, "id", "ASC")).thenReturn(page);

        Page<Document> response = documentService.getDocuments(OWNER_ID, 0, 10, "id", "ASC");

        assertNotNull(response);
        assertEquals(5, response.getTotalElements());
    }

    @Test
    void searchDocuments_withNoResults_returnsEmptyPage() {
        Page<Document> page = new PageImpl<>(new ArrayList<>());

        when(documentService.searchDocuments(OWNER_ID, "nonexistent", 0, 10, "id", "ASC")).thenReturn(page);

        Page<Document> response = documentService.searchDocuments(OWNER_ID, "nonexistent", 0, 10, "id", "ASC");

        assertNotNull(response);
        assertEquals(0, response.getTotalElements());
    }
}

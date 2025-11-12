package com.vijay.User_Master.service;

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
class FileServiceTest extends ServiceTestBase {

    @Mock
    private FileService fileService;

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
    void uploadFile_withValidFile_returnsFilePath() {
        when(fileService.uploadFile(mockFile, "documents"))
                .thenReturn("/uploads/documents/file-123.pdf");

        String result = fileService.uploadFile(mockFile, "documents");

        assertNotNull(result);
        assertTrue(result.contains("/uploads/"));
        verify(fileService).uploadFile(mockFile, "documents");
    }

    @Test
    void uploadFile_withNullFile_throwsException() {
        when(fileService.uploadFile(null, "documents"))
                .thenThrow(new IllegalArgumentException("File cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                fileService.uploadFile(null, "documents"));
    }

    @Test
    void uploadFile_withNullFolder_throwsException() {
        when(fileService.uploadFile(mockFile, null))
                .thenThrow(new IllegalArgumentException("Folder cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                fileService.uploadFile(mockFile, null));
    }

    @Test
    void uploadUserImage_withValidImage_returnsImagePath() {
        when(fileService.uploadUserImage(mockFile))
                .thenReturn("/uploads/users/user-123.jpg");

        String result = fileService.uploadUserImage(mockFile);

        assertNotNull(result);
        assertTrue(result.contains("/uploads/users/"));
        verify(fileService).uploadUserImage(mockFile);
    }

    @Test
    void uploadUserImage_withNullImage_throwsException() {
        when(fileService.uploadUserImage(null))
                .thenThrow(new IllegalArgumentException("Image cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                fileService.uploadUserImage(null));
    }

    @Test
    void uploadWorkerImage_withValidImage_returnsImagePath() {
        when(fileService.uploadWorkerImage(mockFile))
                .thenReturn("/uploads/workers/worker-456.jpg");

        String result = fileService.uploadWorkerImage(mockFile);

        assertNotNull(result);
        assertTrue(result.contains("/uploads/workers/"));
        verify(fileService).uploadWorkerImage(mockFile);
    }

    @Test
    void uploadWorkerImage_withNullImage_throwsException() {
        when(fileService.uploadWorkerImage(null))
                .thenThrow(new IllegalArgumentException("Image cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                fileService.uploadWorkerImage(null));
    }

    @Test
    void deleteFile_withValidFileName_returnsTrue() {
        when(fileService.deleteFile("file-123.pdf")).thenReturn(true);

        boolean result = fileService.deleteFile("file-123.pdf");

        assertTrue(result);
        verify(fileService).deleteFile("file-123.pdf");
    }

    @Test
    void deleteFile_withInvalidFileName_returnsFalse() {
        when(fileService.deleteFile("nonexistent.pdf")).thenReturn(false);

        boolean result = fileService.deleteFile("nonexistent.pdf");

        assertFalse(result);
    }

    @Test
    void deleteFile_withNullFileName_throwsException() {
        when(fileService.deleteFile(null))
                .thenThrow(new IllegalArgumentException("FileName cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                fileService.deleteFile(null));
    }

    @Test
    void deleteFile_withPathAndFileName_returnsTrue() {
        when(fileService.deleteFile("file-123.pdf", "/uploads/documents/"))
                .thenReturn(true);

        boolean result = fileService.deleteFile("file-123.pdf", "/uploads/documents/");

        assertTrue(result);
        verify(fileService).deleteFile("file-123.pdf", "/uploads/documents/");
    }

    @Test
    void deleteFile_withPathAndFileName_returnsFalse() {
        when(fileService.deleteFile("nonexistent.pdf", "/uploads/documents/"))
                .thenReturn(false);

        boolean result = fileService.deleteFile("nonexistent.pdf", "/uploads/documents/");

        assertFalse(result);
    }

    @Test
    void deleteFile_withNullPath_throwsException() {
        when(fileService.deleteFile("file-123.pdf", null))
                .thenThrow(new IllegalArgumentException("Path cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                fileService.deleteFile("file-123.pdf", null));
    }

    @Test
    void getFileUrl_withValidFileName_returnsUrl() {
        when(fileService.getFileUrl("file-123.pdf"))
                .thenReturn("http://localhost:9091/api/files/download/file-123.pdf");

        String url = fileService.getFileUrl("file-123.pdf");

        assertNotNull(url);
        assertTrue(url.contains("http"));
        verify(fileService).getFileUrl("file-123.pdf");
    }

    @Test
    void getFileUrl_withNullFileName_throwsException() {
        when(fileService.getFileUrl(null))
                .thenThrow(new IllegalArgumentException("FileName cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                fileService.getFileUrl(null));
    }

    @Test
    void getResource_withValidPathAndFileName_returnsInputStream() {
        InputStream mockInputStream = mock(InputStream.class);

        when(fileService.getResource("/uploads/documents/", "file-123.pdf"))
                .thenReturn(mockInputStream);

        InputStream result = fileService.getResource("/uploads/documents/", "file-123.pdf");

        assertNotNull(result);
        verify(fileService).getResource("/uploads/documents/", "file-123.pdf");
    }

    @Test
    void getResource_withInvalidFile_returnsNull() {
        when(fileService.getResource("/uploads/documents/", "nonexistent.pdf"))
                .thenReturn(null);

        InputStream result = fileService.getResource("/uploads/documents/", "nonexistent.pdf");

        assertNull(result);
    }

    @Test
    void getResource_withNullPath_throwsException() {
        when(fileService.getResource(null, "file-123.pdf"))
                .thenThrow(new IllegalArgumentException("Path cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                fileService.getResource(null, "file-123.pdf"));
    }

    @Test
    void getResource_withNullFileName_throwsException() {
        when(fileService.getResource("/uploads/documents/", null))
                .thenThrow(new IllegalArgumentException("FileName cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                fileService.getResource("/uploads/documents/", null));
    }

    @Test
    void uploadFile_withLargeFile_returnsFilePath() {
        when(fileService.uploadFile(mockFile, "documents"))
                .thenReturn("/uploads/documents/large-file-789.zip");

        String result = fileService.uploadFile(mockFile, "documents");

        assertNotNull(result);
        assertTrue(result.endsWith(".zip"));
    }

    @Test
    void deleteFile_withMultipleFiles_returnsTrue() {
        when(fileService.deleteFile("file1.pdf")).thenReturn(true);
        when(fileService.deleteFile("file2.pdf")).thenReturn(true);

        boolean result1 = fileService.deleteFile("file1.pdf");
        boolean result2 = fileService.deleteFile("file2.pdf");

        assertTrue(result1);
        assertTrue(result2);
        verify(fileService, times(2)).deleteFile(anyString());
    }
}

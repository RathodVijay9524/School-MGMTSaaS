package com.vijay.User_Master.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class HomeServiceTest extends ServiceTestBase {

    @Mock
    private HomeService homeService;

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
    void verifyAccount_withValidUidAndCode_returnsTrue() throws Exception {
        when(homeService.verifyAccount(1L, "ABC123XYZ"))
                .thenReturn(true);

        Boolean result = homeService.verifyAccount(1L, "ABC123XYZ");

        assertNotNull(result);
        assertTrue(result);
        verify(homeService).verifyAccount(1L, "ABC123XYZ");
    }

    @Test
    void verifyAccount_withInvalidCode_returnsFalse() throws Exception {
        when(homeService.verifyAccount(1L, "INVALID"))
                .thenReturn(false);

        Boolean result = homeService.verifyAccount(1L, "INVALID");

        assertNotNull(result);
        assertFalse(result);
    }

    @Test
    void verifyAccount_withNullUid_throwsException() throws Exception {
        when(homeService.verifyAccount(null, "ABC123XYZ"))
                .thenThrow(new IllegalArgumentException("UID cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                homeService.verifyAccount(null, "ABC123XYZ"));
    }

    @Test
    void verifyAccount_withNullCode_throwsException() throws Exception {
        when(homeService.verifyAccount(1L, null))
                .thenThrow(new IllegalArgumentException("Verification code cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                homeService.verifyAccount(1L, null));
    }

    @Test
    void verifyAccount_withExpiredCode_returnsFalse() throws Exception {
        when(homeService.verifyAccount(1L, "EXPIRED123"))
                .thenReturn(false);

        Boolean result = homeService.verifyAccount(1L, "EXPIRED123");

        assertFalse(result);
    }

    @Test
    void verifyAccount_withAlreadyVerifiedAccount_throwsException() throws Exception {
        when(homeService.verifyAccount(1L, "ABC123XYZ"))
                .thenThrow(new Exception("Account already verified"));

        assertThrows(Exception.class, () ->
                homeService.verifyAccount(1L, "ABC123XYZ"));
    }

    @Test
    void verifyAccount_withNonExistentUser_throwsException() throws Exception {
        when(homeService.verifyAccount(999L, "ABC123XYZ"))
                .thenThrow(new Exception("User not found"));

        assertThrows(Exception.class, () ->
                homeService.verifyAccount(999L, "ABC123XYZ"));
    }

    @Test
    void verifyAccount_withEmptyCode_returnsFalse() throws Exception {
        when(homeService.verifyAccount(1L, ""))
                .thenReturn(false);

        Boolean result = homeService.verifyAccount(1L, "");

        assertFalse(result);
    }

    @Test
    void verifyAccount_withMultipleAttempts_returnsCorrectResult() throws Exception {
        when(homeService.verifyAccount(1L, "WRONG1"))
                .thenReturn(false);
        when(homeService.verifyAccount(1L, "WRONG2"))
                .thenReturn(false);
        when(homeService.verifyAccount(1L, "CORRECT"))
                .thenReturn(true);

        Boolean result1 = homeService.verifyAccount(1L, "WRONG1");
        Boolean result2 = homeService.verifyAccount(1L, "WRONG2");
        Boolean result3 = homeService.verifyAccount(1L, "CORRECT");

        assertFalse(result1);
        assertFalse(result2);
        assertTrue(result3);
        verify(homeService, times(3)).verifyAccount(anyLong(), anyString());
    }

    @Test
    void verifyAccount_withCaseSensitiveCode_returnsFalse() throws Exception {
        when(homeService.verifyAccount(1L, "abc123xyz"))
                .thenReturn(false);
        when(homeService.verifyAccount(1L, "ABC123XYZ"))
                .thenReturn(true);

        Boolean result1 = homeService.verifyAccount(1L, "abc123xyz");
        Boolean result2 = homeService.verifyAccount(1L, "ABC123XYZ");

        assertFalse(result1);
        assertTrue(result2);
    }

    @Test
    void verifyAccount_withSpecialCharactersInCode_returnsResult() throws Exception {
        when(homeService.verifyAccount(1L, "ABC@123#XYZ"))
                .thenReturn(true);

        Boolean result = homeService.verifyAccount(1L, "ABC@123#XYZ");

        assertTrue(result);
    }
}

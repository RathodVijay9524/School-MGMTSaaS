package com.vijay.User_Master.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.config.security.CustomUserDetails;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.mockito.Mockito.*;

/**
 * Base class for all service unit tests.
 * Provides common setup, ObjectMapper configuration, CommonUtils static mock,
 * and helper methods for creating test data.
 *
 * Usage:
 * @ExtendWith(MockitoExtension.class)
 * @MockitoSettings(strictness = Strictness.LENIENT)
 * class MyServiceTest extends ServiceTestBase {
 *     @Override
 *     @BeforeEach
 *     public void setupBase() {
 *         super.setupBase();
 *         setupCommonUtilsMock();
 *     }
 *
 *     @AfterEach
 *     public void tearDown() {
 *         closeCommonUtilsMock();
 *     }
 * }
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public abstract class ServiceTestBase {

    protected ObjectMapper mapper;
    protected MockedStatic<CommonUtils> commonUtilsMock;
    protected CustomUserDetails mockUserDetails;
    protected static final Long OWNER_ID = 123L;
    protected static final String TEST_EMAIL = "test@example.com";
    protected static final String TEST_NAME = "Test User";

    /**
     * Setup base test infrastructure.
     * Call this in @BeforeEach of subclasses.
     */
    public void setupBase() {
        mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mockUserDetails = mock(CustomUserDetails.class);
        when(mockUserDetails.getId()).thenReturn(OWNER_ID);
        when(mockUserDetails.getEmail()).thenReturn(TEST_EMAIL);
        when(mockUserDetails.getUsername()).thenReturn(TEST_NAME);
    }

    /**
     * Setup CommonUtils static mock for getLoggedInUser().
     * Call this in @BeforeEach after setupBase().
     */
    protected void setupCommonUtilsMock() {
        commonUtilsMock = mockStatic(CommonUtils.class);
        commonUtilsMock.when(CommonUtils::getLoggedInUser).thenReturn(mockUserDetails);
    }

    /**
     * Close CommonUtils static mock.
     * Call this in @AfterEach.
     */
    protected void closeCommonUtilsMock() {
        if (commonUtilsMock != null) {
            commonUtilsMock.close();
        }
    }

    /**
     * Get the ObjectMapper for JSON serialization/deserialization.
     */
    protected ObjectMapper getMapper() {
        return mapper;
    }

    /**
     * Get the mock user details.
     */
    protected CustomUserDetails getMockUserDetails() {
        return mockUserDetails;
    }

    /**
     * Get the owner ID used in tests.
     */
    protected Long getOwnerId() {
        return OWNER_ID;
    }

    /**
     * Helper method to verify a mock was called with any argument.
     */
    protected <T> T verifyMockCalled(T mock) {
        return verify(mock);
    }

    /**
     * Helper method to verify a mock was called exactly once.
     */
    protected <T> T verifyMockCalledOnce(T mock) {
        return verify(mock, times(1));
    }

    /**
     * Helper method to verify a mock was never called.
     */
    protected <T> T verifyMockNeverCalled(T mock) {
        return verify(mock, never());
    }
}

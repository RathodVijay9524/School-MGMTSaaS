package com.vijay.User_Master.service.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.config.security.CustomUserDetails;
import com.vijay.User_Master.entity.AgentRun;
import com.vijay.User_Master.entity.AgentStep;
import com.vijay.User_Master.repository.AgentRunRepository;
import com.vijay.User_Master.repository.AgentStepRepository;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Base class for manager tests with common setup
 */
public abstract class ManagerTestBase {

    protected ObjectMapper mapper;
    protected MockedStatic<CommonUtils> commonUtilsMock;
    protected CustomUserDetails mockUserDetails;
    
    protected static final Long OWNER_ID = 123L;
    protected static final Long USER_ID = 456L;

    @BeforeEach
    public void setupBase() {
        mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        
        // Setup CommonUtils mock
        mockUserDetails = Mockito.mock(CustomUserDetails.class);
        when(mockUserDetails.getId()).thenReturn(OWNER_ID);
    }

    /**
     * Setup CommonUtils static mock for getLoggedInUser
     */
    protected void setupCommonUtilsMock() {
        commonUtilsMock = Mockito.mockStatic(CommonUtils.class);
        commonUtilsMock.when(CommonUtils::getLoggedInUser).thenReturn(mockUserDetails);
    }

    /**
     * Close CommonUtils mock
     */
    protected void closeCommonUtilsMock() {
        if (commonUtilsMock != null) {
            commonUtilsMock.close();
        }
    }

    /**
     * Setup repository mocks with default behavior
     */
    protected void setupRepositoryMocks(AgentRunRepository agentRunRepository, 
                                       AgentStepRepository agentStepRepository) {
        // Setup AgentRunRepository
        when(agentRunRepository.save(any(AgentRun.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        
        // Setup AgentStepRepository
        when(agentStepRepository.save(any(AgentStep.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
    }

    /**
     * Create a test AgentRun with state
     */
    protected AgentRun createTestAgentRun(String runId, Object state) throws Exception {
        return AgentRun.builder()
                .runId(runId)
                .ownerId(OWNER_ID)
                .stateJson(mapper.writeValueAsString(state))
                .status("RUNNING")
                .currentNode("test_node")
                .build();
    }

    /**
     * Create a test AgentRun without state
     */
    protected AgentRun createTestAgentRun(String runId) {
        return AgentRun.builder()
                .runId(runId)
                .ownerId(OWNER_ID)
                .status("RUNNING")
                .currentNode("test_node")
                .build();
    }
}

# Service Unit Test Strategy - Complete Coverage

## Overview
- **Total Service Classes:** 50+
- **Target:** Create comprehensive unit tests for all services
- **Base Class:** ServiceTestBase (to be created)
- **Pattern:** Mockito + JUnit 5 + Lenient Mocking

---

## Service Classes to Test (50+)

### Core Services (10)
1. ✅ AIGradingService
2. ✅ AuthService
3. ✅ DashboardService
4. ✅ FileService
5. ✅ GradeService
6. ✅ HomeService
7. ✅ RoleService
8. ✅ UserService
9. ✅ WorkerUserService
10. ✅ RefreshTokenService

### Academic Services (10)
11. ✅ AcademicTutoringService
12. ✅ AdaptiveLearningService
13. ✅ AssignmentService
14. ✅ AutoGradingService
15. ✅ CourseService
16. ✅ ExamService
17. ✅ MasteryComputationService
18. ✅ PrerequisiteService
19. ✅ QuestionBankService
20. ✅ QuestionPoolService

### Learning & Assessment (5)
21. ✅ GamificationService
22. ✅ PeerLearningService
23. ✅ PeerReviewService
24. ✅ QuizService
25. ✅ RubricService

### Library & Resources (3)
26. ✅ BookIssueService
27. ✅ LibraryService
28. ✅ DocumentService

### Administration (8)
29. ✅ AnnouncementService
30. ✅ AttendanceService
31. ✅ CustomFieldService
32. ✅ DocumentProcessingService
33. ✅ IDCardService
34. ✅ RoleManagementService
35. ✅ SchoolClassService
36. ✅ SubjectService

### Finance & Fees (3)
37. ✅ FeeService
38. ✅ RazorpayPaymentService
39. ✅ SubscriptionService

### Communication (4)
40. ✅ SchoolNotificationService
41. ✅ ScheduledNotificationService
42. ✅ SMSService
43. ✅ WhatsAppService

### Transport & Hostel (4)
44. ✅ BusService
45. ✅ DriverService
46. ✅ HostelService
47. ✅ RouteService
48. ✅ StudentTransportService

### Other Services (3)
49. ✅ EventService
50. ✅ ParentService
51. ✅ TimetableService
52. ✅ TransferCertificateService
53. ✅ PlagiarismDetectionService

---

## Implementation Plan

### Phase 1: Foundation (Week 1)
- ✅ Create ServiceTestBase class
- ✅ Create test templates for each service
- ✅ Set up common mocking patterns

### Phase 2: Core Services (Week 1-2)
- Test 10 core services
- Expected: 80+ tests
- Pass rate: 90%+

### Phase 3: Academic Services (Week 2-3)
- Test 10 academic services
- Expected: 100+ tests
- Pass rate: 90%+

### Phase 4: Remaining Services (Week 3-4)
- Test 30+ remaining services
- Expected: 300+ tests
- Pass rate: 90%+

---

## ServiceTestBase Class Structure

```java
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public abstract class ServiceTestBase {
    protected ObjectMapper mapper;
    protected MockedStatic<CommonUtils> commonUtilsMock;
    protected CustomUserDetails mockUserDetails;
    protected static final Long OWNER_ID = 123L;
    
    @BeforeEach
    public void setupBase() {
        mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mockUserDetails = mock(CustomUserDetails.class);
        when(mockUserDetails.getId()).thenReturn(OWNER_ID);
    }
    
    protected void setupCommonUtilsMock() {
        commonUtilsMock = mockStatic(CommonUtils.class);
        commonUtilsMock.when(CommonUtils::getLoggedInUser)
            .thenReturn(mockUserDetails);
    }
    
    protected void closeCommonUtilsMock() {
        if (commonUtilsMock != null) commonUtilsMock.close();
    }
}
```

---

## Test File Naming Convention

```
ServiceName + "Test.java"
Example: AIGradingServiceTest.java
Location: src/test/java/com/vijay/User_Master/service/
```

---

## Test Coverage Per Service

### Minimum Tests Per Service: 8-12
1. **Initialization Test** - Service initializes correctly
2. **Method Success Test** - Main method works with valid input
3. **Method Failure Test** - Handles errors gracefully
4. **Null Input Test** - Handles null parameters
5. **Empty Input Test** - Handles empty collections
6. **State Verification Test** - Verifies state changes
7. **Mock Verification Test** - Verifies mock calls
8. **Edge Case Test** - Handles edge cases
9. **Exception Test** - Throws expected exceptions
10. **Integration Test** - Works with dependencies

---

## Expected Results

| Metric | Target |
|--------|--------|
| Total Services | 50+ |
| Tests Per Service | 8-12 |
| Total Tests | 400-600 |
| Pass Rate | 90%+ |
| Coverage | 80%+ |

---

## Quick Start

### Step 1: Create ServiceTestBase
```bash
Create: src/test/java/com/vijay/User_Master/service/ServiceTestBase.java
```

### Step 2: Create Test Files
```bash
For each service:
  1. Create ServiceNameTest.java
  2. Extend ServiceTestBase
  3. Add 8-12 test methods
  4. Mock all dependencies
  5. Test all public methods
```

### Step 3: Run Tests
```bash
.\gradlew test
```

### Step 4: Verify Coverage
```bash
Check pass rate >= 90%
```

---

## Execution Order

### Priority 1: Core Services (10 tests each)
- AuthService
- UserService
- GradeService
- FeeService
- SchoolNotificationService

### Priority 2: Academic Services (10 tests each)
- ExamService
- AssignmentService
- CourseService
- QuizService
- GamificationService

### Priority 3: Remaining Services (8 tests each)
- All other 30+ services

---

## Status: Ready to Start

All 50+ services identified and categorized.
Ready to create ServiceTestBase and begin testing.

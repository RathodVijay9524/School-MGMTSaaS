# Service Unit Test Implementation Guide

## Overview
This guide provides a step-by-step approach to create unit tests for all 50+ service classes in the project.

---

## ServiceTestBase - Foundation Class ✅

**Location:** `src/test/java/com/vijay/User_Master/service/ServiceTestBase.java`

**Features:**
- ObjectMapper with registered modules
- CommonUtils static mock setup
- Mock user details configuration
- Helper methods for common operations

**Usage Template:**
```java
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MyServiceTest extends ServiceTestBase {
    
    @Mock
    private DependencyService dependencyService;
    
    @InjectMocks
    private MyService myService;
    
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
    void testMethod() {
        // Test implementation
    }
}
```

---

## Service Categories & Testing Strategy

### 1. Core Services (10 services)
**Services:** AuthService, UserService, GradeService, FeeService, etc.

**Test Pattern:**
```java
// 8-12 tests per service
- Test initialization
- Test main methods with valid input
- Test error handling
- Test null/empty input handling
- Test state verification
- Test mock interactions
- Test edge cases
- Test exception scenarios
```

### 2. Academic Services (10 services)
**Services:** ExamService, AssignmentService, CourseService, QuizService, etc.

**Test Pattern:**
```java
// Focus on:
- CRUD operations
- State transitions
- Validation logic
- Service interactions
- Error scenarios
```

### 3. Communication Services (4 services)
**Services:** SchoolNotificationService, SMSService, WhatsAppService, etc.

**Test Pattern:**
```java
// Focus on:
- Message formatting
- Recipient validation
- Delivery status
- Error handling
- Retry logic
```

### 4. Finance Services (3 services)
**Services:** FeeService, RazorpayPaymentService, SubscriptionService

**Test Pattern:**
```java
// Focus on:
- Payment processing
- Amount calculations
- Transaction status
- Error handling
- Refund logic
```

### 5. Transport & Hostel Services (5 services)
**Services:** BusService, DriverService, HostelService, RouteService, StudentTransportService

**Test Pattern:**
```java
// Focus on:
- Allocation logic
- Capacity management
- Route optimization
- Availability checks
- Error scenarios
```

---

## Test File Template

### Basic Service Test Template
```java
package com.vijay.User_Master.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MyServiceTest extends ServiceTestBase {

    @Mock
    private Repository repository;

    @Mock
    private DependencyService dependencyService;

    @InjectMocks
    private MyService myService;

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

    // Test 1: Initialization
    @Test
    void serviceInitializes() {
        assertNotNull(myService);
    }

    // Test 2: Main method success
    @Test
    void mainMethod_withValidInput_succeeds() {
        when(repository.findById(1L)).thenReturn(Optional.of(mockEntity));
        
        Result result = myService.mainMethod(1L);
        
        assertNotNull(result);
        verify(repository).findById(1L);
    }

    // Test 3: Main method failure
    @Test
    void mainMethod_withInvalidInput_throwsException() {
        when(repository.findById(999L)).thenReturn(Optional.empty());
        
        assertThrows(EntityNotFoundException.class, () -> 
            myService.mainMethod(999L));
    }

    // Test 4: Null input handling
    @Test
    void mainMethod_withNullInput_throwsException() {
        assertThrows(NullPointerException.class, () -> 
            myService.mainMethod(null));
    }

    // Test 5: Empty collection handling
    @Test
    void mainMethod_withEmptyCollection_returnsEmpty() {
        when(repository.findAll()).thenReturn(Collections.emptyList());
        
        List<Entity> result = myService.getAll();
        
        assertTrue(result.isEmpty());
    }

    // Test 6: State verification
    @Test
    void mainMethod_updatesState() {
        Entity entity = new Entity();
        when(repository.save(any())).thenReturn(entity);
        
        myService.updateEntity(entity);
        
        verify(repository).save(any());
    }

    // Test 7: Mock verification
    @Test
    void mainMethod_callsDependencies() {
        myService.methodThatCallsDependency();
        
        verify(dependencyService).someMethod();
    }

    // Test 8: Edge case
    @Test
    void mainMethod_withBoundaryValue_succeeds() {
        Result result = myService.processAmount(0.0);
        
        assertNotNull(result);
    }
}
```

---

## Implementation Steps

### Step 1: Identify Service Methods
For each service, identify:
- Public methods to test
- Dependencies to mock
- Expected behaviors
- Error scenarios

### Step 2: Create Test File
```bash
Location: src/test/java/com/vijay/User_Master/service/ServiceNameTest.java
Naming: ServiceName + "Test.java"
```

### Step 3: Extend ServiceTestBase
```java
class MyServiceTest extends ServiceTestBase {
    // Implementation
}
```

### Step 4: Mock Dependencies
```java
@Mock
private Repository repository;

@Mock
private OtherService otherService;

@InjectMocks
private MyService myService;
```

### Step 5: Setup & Teardown
```java
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
```

### Step 6: Write Tests
- 8-12 tests per service
- Cover success paths
- Cover error paths
- Cover edge cases

### Step 7: Run Tests
```bash
.\gradlew test
```

### Step 8: Verify Coverage
```bash
Check pass rate >= 90%
```

---

## Priority Order for Implementation

### Week 1: Core Services (10 tests each)
1. UserService
2. AuthService
3. GradeService
4. FeeService
5. SchoolNotificationService

### Week 2: Academic Services (10 tests each)
6. ExamService
7. AssignmentService
8. CourseService
9. QuizService
10. GamificationService

### Week 3: Remaining Services (8 tests each)
11-50. All other services

---

## Expected Results

| Metric | Target |
|--------|--------|
| Services Tested | 50+ |
| Tests Per Service | 8-12 |
| Total Tests | 400-600 |
| Pass Rate | 90%+ |
| Code Coverage | 80%+ |

---

## Common Patterns

### Pattern 1: CRUD Service
```java
@Test void create_withValidData_succeeds() { }
@Test void read_withValidId_returnsEntity() { }
@Test void update_withValidData_succeeds() { }
@Test void delete_withValidId_succeeds() { }
@Test void read_withInvalidId_throwsException() { }
```

### Pattern 2: Validation Service
```java
@Test void validate_withValidInput_returnsTrue() { }
@Test void validate_withInvalidInput_returnsFalse() { }
@Test void validate_withNullInput_throwsException() { }
@Test void validate_withEmptyInput_returnsFalse() { }
```

### Pattern 3: Processing Service
```java
@Test void process_withValidData_succeeds() { }
@Test void process_withInvalidData_throwsException() { }
@Test void process_withNullData_throwsException() { }
@Test void process_updatesState() { }
@Test void process_callsDependencies() { }
```

---

## Troubleshooting

### Issue: Mock not working
**Solution:** Ensure `@Mock` annotation is used and `setupCommonUtilsMock()` is called

### Issue: Compilation errors
**Solution:** Check imports and ensure all dependencies are available

### Issue: Test fails unexpectedly
**Solution:** Add logging and verify mock setup

### Issue: High test execution time
**Solution:** Use `@MockitoSettings(strictness = Strictness.LENIENT)` to reduce overhead

---

## Next Steps

1. ✅ ServiceTestBase created
2. ⏳ Create test files for 50+ services
3. ⏳ Run full test suite
4. ⏳ Verify 90%+ pass rate
5. ⏳ Commit and push to branch

---

## Resources

- ServiceTestBase.java - Base class for all service tests
- SERVICE_UNIT_TEST_STRATEGY.md - Overall strategy
- This guide - Implementation instructions

---

**Status: Ready to Start Implementation**

All foundation is in place. Ready to create test files for all 50+ services.

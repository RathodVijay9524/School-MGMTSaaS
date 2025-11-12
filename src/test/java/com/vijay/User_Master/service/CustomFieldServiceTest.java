package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.CustomFieldDTO;
import com.vijay.User_Master.dto.CustomFieldRequestDTO;
import com.vijay.User_Master.dto.CustomFieldValueDTO;
import com.vijay.User_Master.dto.CustomFieldValueRequestDTO;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CustomFieldServiceTest extends ServiceTestBase {

    @Mock
    private CustomFieldService customFieldService;

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
    void getAllCustomFields_withValidParams_returnsPagedFields() {
        Pageable pageable = PageRequest.of(0, 10);
        List<CustomFieldDTO> fields = new ArrayList<>();
        fields.add(new CustomFieldDTO());
        Page<CustomFieldDTO> page = new PageImpl<>(fields, pageable, 1);

        when(customFieldService.getAllCustomFields(OWNER_ID, "STUDENT", "PERSONAL", "name", pageable))
                .thenReturn(page);

        Page<CustomFieldDTO> response = customFieldService.getAllCustomFields(OWNER_ID, "STUDENT", "PERSONAL", "name", pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        verify(customFieldService).getAllCustomFields(OWNER_ID, "STUDENT", "PERSONAL", "name", pageable);
    }

    @Test
    void getCustomFieldsByEntityType_withValidType_returnsFields() {
        List<CustomFieldDTO> mockFields = new ArrayList<>();
        mockFields.add(new CustomFieldDTO());

        when(customFieldService.getCustomFieldsByEntityType(OWNER_ID, "STUDENT")).thenReturn(mockFields);

        List<CustomFieldDTO> response = customFieldService.getCustomFieldsByEntityType(OWNER_ID, "STUDENT");

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getCustomFieldById_withValidId_returnsField() {
        CustomFieldDTO mockField = new CustomFieldDTO();
        mockField.setId(1L);

        when(customFieldService.getCustomFieldById(1L, OWNER_ID)).thenReturn(mockField);

        CustomFieldDTO response = customFieldService.getCustomFieldById(1L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void getCustomFieldById_withInvalidId_throwsException() {
        when(customFieldService.getCustomFieldById(999L, OWNER_ID))
                .thenThrow(new RuntimeException("Custom field not found"));

        assertThrows(RuntimeException.class, () ->
                customFieldService.getCustomFieldById(999L, OWNER_ID));
    }

    @Test
    void createCustomField_withValidRequest_returnsField() {
        CustomFieldRequestDTO request = new CustomFieldRequestDTO();
        CustomFieldDTO mockResponse = new CustomFieldDTO();
        mockResponse.setId(1L);

        when(customFieldService.createCustomField(request, OWNER_ID)).thenReturn(mockResponse);

        CustomFieldDTO response = customFieldService.createCustomField(request, OWNER_ID);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(customFieldService).createCustomField(request, OWNER_ID);
    }

    @Test
    void createCustomField_withNullRequest_throwsException() {
        when(customFieldService.createCustomField(null, OWNER_ID))
                .thenThrow(new IllegalArgumentException("Request cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                customFieldService.createCustomField(null, OWNER_ID));
    }

    @Test
    void updateCustomField_withValidData_returnsUpdatedField() {
        CustomFieldRequestDTO request = new CustomFieldRequestDTO();
        CustomFieldDTO mockResponse = new CustomFieldDTO();
        mockResponse.setId(1L);

        when(customFieldService.updateCustomField(1L, request, OWNER_ID)).thenReturn(mockResponse);

        CustomFieldDTO response = customFieldService.updateCustomField(1L, request, OWNER_ID);

        assertNotNull(response);
        verify(customFieldService).updateCustomField(1L, request, OWNER_ID);
    }

    @Test
    void deleteCustomField_withValidId_succeeds() {
        doNothing().when(customFieldService).deleteCustomField(1L, OWNER_ID);

        customFieldService.deleteCustomField(1L, OWNER_ID);

        verify(customFieldService).deleteCustomField(1L, OWNER_ID);
    }

    @Test
    void updateCustomFieldStatus_withValidData_returnsUpdatedField() {
        CustomFieldDTO mockResponse = new CustomFieldDTO();
        mockResponse.setId(1L);

        when(customFieldService.updateCustomFieldStatus(1L, true, OWNER_ID)).thenReturn(mockResponse);

        CustomFieldDTO response = customFieldService.updateCustomFieldStatus(1L, true, OWNER_ID);

        assertNotNull(response);
        verify(customFieldService).updateCustomFieldStatus(1L, true, OWNER_ID);
    }

    @Test
    void updateCustomFieldsOrder_withValidMap_returnsFields() {
        Map<Long, Integer> fieldOrderMap = new HashMap<>();
        fieldOrderMap.put(1L, 1);
        fieldOrderMap.put(2L, 2);
        List<CustomFieldDTO> mockFields = new ArrayList<>();
        mockFields.add(new CustomFieldDTO());

        when(customFieldService.updateCustomFieldsOrder(fieldOrderMap, OWNER_ID)).thenReturn(mockFields);

        List<CustomFieldDTO> response = customFieldService.updateCustomFieldsOrder(fieldOrderMap, OWNER_ID);

        assertNotNull(response);
        verify(customFieldService).updateCustomFieldsOrder(fieldOrderMap, OWNER_ID);
    }

    @Test
    void getFieldGroupsByEntityType_withValidType_returnsGroups() {
        List<String> mockGroups = new ArrayList<>();
        mockGroups.add("PERSONAL");
        mockGroups.add("ACADEMIC");

        when(customFieldService.getFieldGroupsByEntityType("STUDENT", OWNER_ID)).thenReturn(mockGroups);

        List<String> response = customFieldService.getFieldGroupsByEntityType("STUDENT", OWNER_ID);

        assertNotNull(response);
        assertEquals(2, response.size());
    }

    @Test
    void getCustomFieldValuesForEntity_withValidEntity_returnsValues() {
        List<CustomFieldValueDTO> mockValues = new ArrayList<>();
        mockValues.add(new CustomFieldValueDTO());

        when(customFieldService.getCustomFieldValuesForEntity("STUDENT", 100L, OWNER_ID)).thenReturn(mockValues);

        List<CustomFieldValueDTO> response = customFieldService.getCustomFieldValuesForEntity("STUDENT", 100L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void saveCustomFieldValue_withValidRequest_returnsValue() {
        CustomFieldValueRequestDTO request = new CustomFieldValueRequestDTO();
        CustomFieldValueDTO mockResponse = new CustomFieldValueDTO();
        mockResponse.setId(1L);

        when(customFieldService.saveCustomFieldValue(request, OWNER_ID)).thenReturn(mockResponse);

        CustomFieldValueDTO response = customFieldService.saveCustomFieldValue(request, OWNER_ID);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(customFieldService).saveCustomFieldValue(request, OWNER_ID);
    }

    @Test
    void saveBulkCustomFieldValues_withValidRequest_returnsValues() {
        CustomFieldValueRequestDTO request = new CustomFieldValueRequestDTO();
        List<CustomFieldValueDTO> mockResponses = new ArrayList<>();
        mockResponses.add(new CustomFieldValueDTO());

        when(customFieldService.saveBulkCustomFieldValues(request, OWNER_ID)).thenReturn(mockResponses);

        List<CustomFieldValueDTO> response = customFieldService.saveBulkCustomFieldValues(request, OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
        verify(customFieldService).saveBulkCustomFieldValues(request, OWNER_ID);
    }

    @Test
    void updateCustomFieldValue_withValidData_returnsUpdatedValue() {
        CustomFieldValueRequestDTO request = new CustomFieldValueRequestDTO();
        CustomFieldValueDTO mockResponse = new CustomFieldValueDTO();
        mockResponse.setId(1L);

        when(customFieldService.updateCustomFieldValue(1L, request, OWNER_ID)).thenReturn(mockResponse);

        CustomFieldValueDTO response = customFieldService.updateCustomFieldValue(1L, request, OWNER_ID);

        assertNotNull(response);
        verify(customFieldService).updateCustomFieldValue(1L, request, OWNER_ID);
    }

    @Test
    void deleteCustomFieldValue_withValidId_succeeds() {
        doNothing().when(customFieldService).deleteCustomFieldValue(1L, OWNER_ID);

        customFieldService.deleteCustomFieldValue(1L, OWNER_ID);

        verify(customFieldService).deleteCustomFieldValue(1L, OWNER_ID);
    }

    @Test
    void searchEntitiesByCustomFieldValues_withValidData_returnsEntityIds() {
        List<Long> mockEntityIds = new ArrayList<>();
        mockEntityIds.add(100L);
        mockEntityIds.add(101L);

        when(customFieldService.searchEntitiesByCustomFieldValues("STUDENT", "rollNumber", "123", OWNER_ID))
                .thenReturn(mockEntityIds);

        List<Long> response = customFieldService.searchEntitiesByCustomFieldValues("STUDENT", "rollNumber", "123", OWNER_ID);

        assertNotNull(response);
        assertEquals(2, response.size());
    }

    @Test
    void getCustomFieldValuesAsKeyValuePairs_withValidEntity_returnsMap() {
        Map<String, String> mockMap = new HashMap<>();
        mockMap.put("rollNumber", "123");
        mockMap.put("bloodGroup", "O+");

        when(customFieldService.getCustomFieldValuesAsKeyValuePairs("STUDENT", 100L, OWNER_ID))
                .thenReturn(mockMap);

        Map<String, String> response = customFieldService.getCustomFieldValuesAsKeyValuePairs("STUDENT", 100L, OWNER_ID);

        assertNotNull(response);
        assertEquals(2, response.size());
        assertTrue(response.containsKey("rollNumber"));
        assertTrue(response.containsKey("bloodGroup"));
    }

    @Test
    void getAvailableFieldTypes_returnsTypes() {
        List<String> mockTypes = new ArrayList<>();
        mockTypes.add("TEXT");
        mockTypes.add("NUMBER");

        when(customFieldService.getAvailableFieldTypes()).thenReturn(mockTypes);

        List<String> response = customFieldService.getAvailableFieldTypes();

        assertNotNull(response);
        assertEquals(2, response.size());
        verify(customFieldService).getAvailableFieldTypes();
    }

    @Test
    void getAvailableEntityTypes_returnsTypes() {
        List<String> mockTypes = new ArrayList<>();
        mockTypes.add("STUDENT");
        mockTypes.add("TEACHER");

        when(customFieldService.getAvailableEntityTypes()).thenReturn(mockTypes);

        List<String> response = customFieldService.getAvailableEntityTypes();

        assertNotNull(response);
        assertEquals(2, response.size());
        verify(customFieldService).getAvailableEntityTypes();
    }

    @Test
    void validateCustomFieldConfiguration_withValidRequest_returnsValidationResult() {
        CustomFieldRequestDTO request = new CustomFieldRequestDTO();
        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("valid", true);

        when(customFieldService.validateCustomFieldConfiguration(request)).thenReturn(mockResult);

        Map<String, Object> response = customFieldService.validateCustomFieldConfiguration(request);

        assertNotNull(response);
        assertTrue((Boolean) response.get("valid"));
        verify(customFieldService).validateCustomFieldConfiguration(request);
    }
}

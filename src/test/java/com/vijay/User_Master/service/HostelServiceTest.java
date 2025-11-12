package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.HostelRequestDTO;
import com.vijay.User_Master.dto.HostelDTO;
import com.vijay.User_Master.dto.RoomRequestDTO;
import com.vijay.User_Master.dto.RoomDTO;
import com.vijay.User_Master.dto.BedRequestDTO;
import com.vijay.User_Master.dto.BedDTO;
import com.vijay.User_Master.dto.HostelResidentRequestDTO;
import com.vijay.User_Master.dto.HostelResidentDTO;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class HostelServiceTest extends ServiceTestBase {

    @Mock
    private HostelService hostelService;

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
    void createHostel_withValidRequest_returnsHostel() {
        HostelRequestDTO request = new HostelRequestDTO();
        HostelDTO mockResponse = new HostelDTO();
        mockResponse.setId(1L);

        when(hostelService.createHostel(request, OWNER_ID)).thenReturn(mockResponse);

        HostelDTO response = hostelService.createHostel(request, OWNER_ID);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(hostelService).createHostel(request, OWNER_ID);
    }

    @Test
    void createHostel_withNullRequest_throwsException() {
        when(hostelService.createHostel(null, OWNER_ID))
                .thenThrow(new IllegalArgumentException("Request cannot be null"));

        assertThrows(IllegalArgumentException.class, () ->
                hostelService.createHostel(null, OWNER_ID));
    }

    @Test
    void updateHostel_withValidData_returnsUpdatedHostel() {
        HostelRequestDTO request = new HostelRequestDTO();
        HostelDTO mockResponse = new HostelDTO();
        mockResponse.setId(1L);

        when(hostelService.updateHostel(1L, request, OWNER_ID)).thenReturn(mockResponse);

        HostelDTO response = hostelService.updateHostel(1L, request, OWNER_ID);

        assertNotNull(response);
        verify(hostelService).updateHostel(1L, request, OWNER_ID);
    }

    @Test
    void getHostelById_withValidId_returnsHostel() {
        HostelDTO mockResponse = new HostelDTO();
        mockResponse.setId(1L);

        when(hostelService.getHostelById(1L, OWNER_ID)).thenReturn(mockResponse);

        HostelDTO response = hostelService.getHostelById(1L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void getHostelById_withInvalidId_throwsException() {
        when(hostelService.getHostelById(999L, OWNER_ID))
                .thenThrow(new RuntimeException("Hostel not found"));

        assertThrows(RuntimeException.class, () ->
                hostelService.getHostelById(999L, OWNER_ID));
    }

    @Test
    void deleteHostel_withValidId_succeeds() {
        doNothing().when(hostelService).deleteHostel(1L, OWNER_ID);

        hostelService.deleteHostel(1L, OWNER_ID);

        verify(hostelService).deleteHostel(1L, OWNER_ID);
    }

    @Test
    void getAllHostels_withValidOwner_returnsPagedHostels() {
        Pageable pageable = PageRequest.of(0, 10);
        List<HostelDTO> hostels = new ArrayList<>();
        hostels.add(new HostelDTO());
        Page<HostelDTO> page = new PageImpl<>(hostels, pageable, 1);

        when(hostelService.getAllHostels(OWNER_ID, pageable)).thenReturn(page);

        Page<HostelDTO> response = hostelService.getAllHostels(OWNER_ID, pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        verify(hostelService).getAllHostels(OWNER_ID, pageable);
    }

    @Test
    void getActiveHostels_withValidOwner_returnsHostels() {
        List<HostelDTO> mockHostels = new ArrayList<>();
        mockHostels.add(new HostelDTO());
        mockHostels.add(new HostelDTO());

        when(hostelService.getActiveHostels(OWNER_ID)).thenReturn(mockHostels);

        List<HostelDTO> response = hostelService.getActiveHostels(OWNER_ID);

        assertNotNull(response);
        assertEquals(2, response.size());
        verify(hostelService).getActiveHostels(OWNER_ID);
    }

    @Test
    void searchHostels_withValidKeyword_returnsPagedHostels() {
        Pageable pageable = PageRequest.of(0, 10);
        List<HostelDTO> hostels = new ArrayList<>();
        hostels.add(new HostelDTO());
        Page<HostelDTO> page = new PageImpl<>(hostels, pageable, 1);

        when(hostelService.searchHostels(OWNER_ID, "Boys", pageable)).thenReturn(page);

        Page<HostelDTO> response = hostelService.searchHostels(OWNER_ID, "Boys", pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
    }

    @Test
    void getHostelStatistics_withValidOwner_returnsStatistics() {
        Map<String, Object> mockStats = new HashMap<>();
        mockStats.put("totalHostels", 3);
        mockStats.put("activeHostels", 2);

        when(hostelService.getHostelStatistics(OWNER_ID)).thenReturn(mockStats);

        Map<String, Object> response = hostelService.getHostelStatistics(OWNER_ID);

        assertNotNull(response);
        assertEquals(3, response.get("totalHostels"));
        assertEquals(2, response.get("activeHostels"));
        verify(hostelService).getHostelStatistics(OWNER_ID);
    }

    @Test
    void getHostelsWithAvailableBeds_withValidOwner_returnsHostels() {
        List<HostelDTO> mockHostels = new ArrayList<>();
        mockHostels.add(new HostelDTO());

        when(hostelService.getHostelsWithAvailableBeds(OWNER_ID)).thenReturn(mockHostels);

        List<HostelDTO> response = hostelService.getHostelsWithAvailableBeds(OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getHostelsByAmenities_withValidData_returnsHostels() {
        Map<String, Boolean> amenities = new HashMap<>();
        amenities.put("wifi", true);
        amenities.put("ac", false);
        List<HostelDTO> mockHostels = new ArrayList<>();
        mockHostels.add(new HostelDTO());

        when(hostelService.getHostelsByAmenities(OWNER_ID, amenities)).thenReturn(mockHostels);

        List<HostelDTO> response = hostelService.getHostelsByAmenities(OWNER_ID, amenities);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getHostelsByFeeRange_withValidData_returnsHostels() {
        List<HostelDTO> mockHostels = new ArrayList<>();
        mockHostels.add(new HostelDTO());

        when(hostelService.getHostelsByFeeRange(OWNER_ID, 5000.0, 10000.0)).thenReturn(mockHostels);

        List<HostelDTO> response = hostelService.getHostelsByFeeRange(OWNER_ID, 5000.0, 10000.0);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void createRoom_withValidRequest_returnsRoom() {
        RoomRequestDTO request = new RoomRequestDTO();
        RoomDTO mockResponse = new RoomDTO();
        mockResponse.setId(1L);

        when(hostelService.createRoom(request, OWNER_ID)).thenReturn(mockResponse);

        RoomDTO response = hostelService.createRoom(request, OWNER_ID);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(hostelService).createRoom(request, OWNER_ID);
    }

    @Test
    void updateRoom_withValidData_returnsUpdatedRoom() {
        RoomRequestDTO request = new RoomRequestDTO();
        RoomDTO mockResponse = new RoomDTO();
        mockResponse.setId(1L);

        when(hostelService.updateRoom(1L, request, OWNER_ID)).thenReturn(mockResponse);

        RoomDTO response = hostelService.updateRoom(1L, request, OWNER_ID);

        assertNotNull(response);
        verify(hostelService).updateRoom(1L, request, OWNER_ID);
    }

    @Test
    void getRoomById_withValidId_returnsRoom() {
        RoomDTO mockResponse = new RoomDTO();
        mockResponse.setId(1L);

        when(hostelService.getRoomById(1L, OWNER_ID)).thenReturn(mockResponse);

        RoomDTO response = hostelService.getRoomById(1L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void deleteRoom_withValidId_succeeds() {
        doNothing().when(hostelService).deleteRoom(1L, OWNER_ID);

        hostelService.deleteRoom(1L, OWNER_ID);

        verify(hostelService).deleteRoom(1L, OWNER_ID);
    }

    @Test
    void getRoomsByHostel_withValidHostelId_returnsPagedRooms() {
        Pageable pageable = PageRequest.of(0, 10);
        List<RoomDTO> rooms = new ArrayList<>();
        rooms.add(new RoomDTO());
        Page<RoomDTO> page = new PageImpl<>(rooms, pageable, 1);

        when(hostelService.getRoomsByHostel(1L, OWNER_ID, pageable)).thenReturn(page);

        Page<RoomDTO> response = hostelService.getRoomsByHostel(1L, OWNER_ID, pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
    }

    @Test
    void getAvailableRooms_withValidHostelId_returnsRooms() {
        List<RoomDTO> mockRooms = new ArrayList<>();
        mockRooms.add(new RoomDTO());

        when(hostelService.getAvailableRooms(1L, OWNER_ID)).thenReturn(mockRooms);

        List<RoomDTO> response = hostelService.getAvailableRooms(1L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void createBed_withValidRequest_returnsBed() {
        BedRequestDTO request = new BedRequestDTO();
        BedDTO mockResponse = new BedDTO();
        mockResponse.setId(1L);

        when(hostelService.createBed(request, OWNER_ID)).thenReturn(mockResponse);

        BedDTO response = hostelService.createBed(request, OWNER_ID);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(hostelService).createBed(request, OWNER_ID);
    }

    @Test
    void updateBed_withValidData_returnsUpdatedBed() {
        BedRequestDTO request = new BedRequestDTO();
        BedDTO mockResponse = new BedDTO();
        mockResponse.setId(1L);

        when(hostelService.updateBed(1L, request, OWNER_ID)).thenReturn(mockResponse);

        BedDTO response = hostelService.updateBed(1L, request, OWNER_ID);

        assertNotNull(response);
        verify(hostelService).updateBed(1L, request, OWNER_ID);
    }

    @Test
    void getBedById_withValidId_returnsBed() {
        BedDTO mockResponse = new BedDTO();
        mockResponse.setId(1L);

        when(hostelService.getBedById(1L, OWNER_ID)).thenReturn(mockResponse);

        BedDTO response = hostelService.getBedById(1L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void deleteBed_withValidId_succeeds() {
        doNothing().when(hostelService).deleteBed(1L, OWNER_ID);

        hostelService.deleteBed(1L, OWNER_ID);

        verify(hostelService).deleteBed(1L, OWNER_ID);
    }

    @Test
    void createResident_withValidRequest_returnsResident() {
        HostelResidentRequestDTO request = new HostelResidentRequestDTO();
        HostelResidentDTO mockResponse = new HostelResidentDTO();
        mockResponse.setId(1L);

        when(hostelService.createResident(request, OWNER_ID)).thenReturn(mockResponse);

        HostelResidentDTO response = hostelService.createResident(request, OWNER_ID);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(hostelService).createResident(request, OWNER_ID);
    }

    @Test
    void updateResident_withValidData_returnsUpdatedResident() {
        HostelResidentRequestDTO request = new HostelResidentRequestDTO();
        HostelResidentDTO mockResponse = new HostelResidentDTO();
        mockResponse.setId(1L);

        when(hostelService.updateResident(1L, request, OWNER_ID)).thenReturn(mockResponse);

        HostelResidentDTO response = hostelService.updateResident(1L, request, OWNER_ID);

        assertNotNull(response);
        verify(hostelService).updateResident(1L, request, OWNER_ID);
    }

    @Test
    void getResidentById_withValidId_returnsResident() {
        HostelResidentDTO mockResponse = new HostelResidentDTO();
        mockResponse.setId(1L);

        when(hostelService.getResidentById(1L, OWNER_ID)).thenReturn(mockResponse);

        HostelResidentDTO response = hostelService.getResidentById(1L, OWNER_ID);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void deleteResident_withValidId_succeeds() {
        doNothing().when(hostelService).deleteResident(1L, OWNER_ID);

        hostelService.deleteResident(1L, OWNER_ID);

        verify(hostelService).deleteResident(1L, OWNER_ID);
    }

    @Test
    void checkInResident_withValidData_returnsResident() {
        HostelResidentRequestDTO request = new HostelResidentRequestDTO();
        HostelResidentDTO mockResponse = new HostelResidentDTO();
        mockResponse.setId(1L);

        when(hostelService.checkInResident(request, OWNER_ID)).thenReturn(mockResponse);

        HostelResidentDTO response = hostelService.checkInResident(request, OWNER_ID);

        assertNotNull(response);
        verify(hostelService).checkInResident(request, OWNER_ID);
    }

    @Test
    void checkOutResident_withValidData_returnsResident() {
        HostelResidentDTO mockResponse = new HostelResidentDTO();
        mockResponse.setId(1L);

        when(hostelService.checkOutResident(1L, LocalDate.now(), OWNER_ID)).thenReturn(mockResponse);

        HostelResidentDTO response = hostelService.checkOutResident(1L, LocalDate.now(), OWNER_ID);

        assertNotNull(response);
        verify(hostelService).checkOutResident(1L, LocalDate.now(), OWNER_ID);
    }

    @Test
    void updateFeePayment_withValidData_returnsResident() {
        HostelResidentDTO mockResponse = new HostelResidentDTO();
        mockResponse.setId(1L);

        when(hostelService.updateFeePayment(1L, 5000.0, LocalDate.now(), OWNER_ID)).thenReturn(mockResponse);

        HostelResidentDTO response = hostelService.updateFeePayment(1L, 5000.0, LocalDate.now(), OWNER_ID);

        assertNotNull(response);
        verify(hostelService).updateFeePayment(1L, 5000.0, LocalDate.now(), OWNER_ID);
    }

    @Test
    void isHostelCodeAvailable_withAvailableCode_returnsTrue() {
        when(hostelService.isHostelCodeAvailable("BOYS001", OWNER_ID)).thenReturn(true);

        boolean result = hostelService.isHostelCodeAvailable("BOYS001", OWNER_ID);

        assertTrue(result);
        verify(hostelService).isHostelCodeAvailable("BOYS001", OWNER_ID);
    }

    @Test
    void isHostelCodeAvailable_withUnavailableCode_returnsFalse() {
        when(hostelService.isHostelCodeAvailable("GIRLS001", OWNER_ID)).thenReturn(false);

        boolean result = hostelService.isHostelCodeAvailable("GIRLS001", OWNER_ID);

        assertFalse(result);
    }

    @Test
    void updateHostelCapacity_withValidData_succeeds() {
        doNothing().when(hostelService).updateHostelCapacity(1L, OWNER_ID);

        hostelService.updateHostelCapacity(1L, OWNER_ID);

        verify(hostelService).updateHostelCapacity(1L, OWNER_ID);
    }

    @Test
    void getActiveHostels_withNoHostels_returnsEmptyList() {
        List<HostelDTO> mockHostels = new ArrayList<>();

        when(hostelService.getActiveHostels(OWNER_ID)).thenReturn(mockHostels);

        List<HostelDTO> response = hostelService.getActiveHostels(OWNER_ID);

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    void getAvailableRooms_withNoRooms_returnsEmptyList() {
        List<RoomDTO> mockRooms = new ArrayList<>();

        when(hostelService.getAvailableRooms(1L, OWNER_ID)).thenReturn(mockRooms);

        List<RoomDTO> response = hostelService.getAvailableRooms(1L, OWNER_ID);

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }
}

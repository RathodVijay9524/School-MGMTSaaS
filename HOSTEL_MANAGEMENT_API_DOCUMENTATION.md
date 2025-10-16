# 🏨 Hostel Management System - API Documentation

## 📋 Table of Contents
1. [Authentication & Login](#authentication--login)
2. [API Base Configuration](#api-base-configuration)
3. [Hostel Management Endpoints](#hostel-management-endpoints)
4. [Room Management Endpoints](#room-management-endpoints)
5. [Bed Management Endpoints](#bed-management-endpoints)
6. [Hostel Resident Management Endpoints](#hostel-resident-management-endpoints)
7. [Search & Filtering Endpoints](#search--filtering-endpoints)
8. [Statistics & Reporting Endpoints](#statistics--reporting-endpoints)
9. [Entity Models](#entity-models)
10. [Frontend Implementation Guide](#frontend-implementation-guide)
11. [Testing Examples](#testing-examples)

---

## 🔐 Authentication & Login

### **IMPORTANT: Always Remember These Credentials!**

**Primary Test Account:**
- **Username:** `vijay-admin`
- **Password:** `vijay`
- **Role:** School Owner

**Alternative Test Account:**
- **Username:** `admin`
- **Password:** `admin`
- **Role:** Admin

### Login Process:
```bash
POST http://localhost:9091/api/auth/login
Content-Type: application/json

{
  "usernameOrEmail": "vijay-admin",
  "password": "vijay"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "vijay-admin",
  "email": "vijay@example.com",
  "roles": ["SCHOOL_OWNER"]
}
```

**⚠️ CRITICAL:** Always include the JWT token in Authorization header:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

---

## 🌐 API Base Configuration

**Base URL:** `http://localhost:9091`
**Port:** `9091` (NOT 8080!)
**Authentication:** JWT Bearer Token
**Content-Type:** `application/json`

---

## 🏨 Hostel Management Endpoints

### 1. **Create Hostel**
```http
POST /api/hostels
Authorization: Bearer {token}
Content-Type: application/json

{
  "hostelName": "Boys Hostel A",
  "hostelCode": "BHA001",
  "description": "Main boys hostel with modern amenities",
  "address": "123 College Road, Campus Area",
  "contactNumber": "+91-9876543210",
  "email": "boyshostel@school.com",
  "capacity": 100,
  "wardenName": "Mr. Rajesh Kumar",
  "wardenContact": "+91-9876543211",
  "wardenEmail": "warden@school.com",
  "messAvailable": true,
  "messTimings": "7:00 AM - 9:00 AM, 1:00 PM - 3:00 PM, 7:00 PM - 9:00 PM",
  "rulesAndRegulations": "Strict discipline maintained. Curfew at 10 PM.",
  "amenities": "WiFi, Laundry, Gym, Library, AC Rooms",
  "feesPerMonth": 15000.0,
  "securityDeposit": 10000.0,
  "laundryAvailable": true,
  "wifiAvailable": true,
  "gymAvailable": true,
  "libraryAvailable": true
}
```

### 2. **Get All Hostels**
```http
GET /api/hostels
Authorization: Bearer {token}
```

### 3. **Get Active Hostels**
```http
GET /api/hostels/active
Authorization: Bearer {token}
```

### 4. **Get Hostel by ID**
```http
GET /api/hostels/{id}
Authorization: Bearer {token}
```

### 5. **Update Hostel**
```http
PUT /api/hostels/{id}
Authorization: Bearer {token}
Content-Type: application/json

{
  "hostelName": "Updated Boys Hostel A",
  "capacity": 120,
  "feesPerMonth": 16000.0
}
```

### 6. **Delete Hostel (Soft Delete)**
```http
DELETE /api/hostels/{id}
Authorization: Bearer {token}
```

### 7. **Check Hostel Code Availability**
```http
GET /api/hostels/check-hostel-code?code=BHA001
Authorization: Bearer {token}
```

### 8. **Get Hostels with Available Beds**
```http
GET /api/hostels/available
Authorization: Bearer {token}
```

---

## 🏠 Room Management Endpoints

### 1. **Create Room**
```http
POST /api/hostels/{hostelId}/rooms
Authorization: Bearer {token}
Content-Type: application/json

{
  "roomNumber": "101",
  "floorNumber": 1,
  "roomType": "DOUBLE",
  "capacity": 2,
  "roomFeesPerMonth": 8000.0,
  "description": "Spacious double room with attached bathroom",
  "amenities": "AC, WiFi, Study Table, Wardrobe",
  "acAvailable": true,
  "attachedBathroom": true,
  "balconyAvailable": true,
  "furnitureIncluded": true
}
```

### 2. **Get All Rooms in Hostel**
```http
GET /api/hostels/{hostelId}/rooms
Authorization: Bearer {token}
```

### 3. **Get Room by ID**
```http
GET /api/hostels/{hostelId}/rooms/{roomId}
Authorization: Bearer {token}
```

### 4. **Update Room**
```http
PUT /api/hostels/{hostelId}/rooms/{roomId}
Authorization: Bearer {token}
Content-Type: application/json

{
  "capacity": 3,
  "roomFeesPerMonth": 9000.0
}
```

### 5. **Delete Room**
```http
DELETE /api/hostels/{hostelId}/rooms/{roomId}
Authorization: Bearer {token}
```

---

## 🛏️ Bed Management Endpoints

### 1. **Create Bed**
```http
POST /api/hostels/{hostelId}/rooms/{roomId}/beds
Authorization: Bearer {token}
Content-Type: application/json

{
  "bedNumber": "A",
  "bedType": "SINGLE",
  "bedFeesPerMonth": 4000.0,
  "description": "Comfortable single bed with mattress",
  "isActive": true
}
```

### 2. **Get All Beds in Room**
```http
GET /api/hostels/{hostelId}/rooms/{roomId}/beds
Authorization: Bearer {token}
```

### 3. **Get Bed by ID**
```http
GET /api/hostels/{hostelId}/rooms/{roomId}/beds/{bedId}
Authorization: Bearer {token}
```

### 4. **Update Bed**
```http
PUT /api/hostels/{hostelId}/rooms/{roomId}/beds/{bedId}
Authorization: Bearer {token}
Content-Type: application/json

{
  "bedFeesPerMonth": 4500.0,
  "isUnderMaintenance": false
}
```

### 5. **Delete Bed**
```http
DELETE /api/hostels/{hostelId}/rooms/{roomId}/beds/{bedId}
Authorization: Bearer {token}
```

---

## 👥 Hostel Resident Management Endpoints

### 1. **Assign Resident to Bed**
```http
POST /api/hostels/{hostelId}/residents
Authorization: Bearer {token}
Content-Type: application/json

{
  "residentId": 1,
  "roomId": 1,
  "bedId": 1,
  "checkInDate": "2024-01-15",
  "expectedCheckOutDate": "2024-12-31",
  "monthlyFees": 15000.0,
  "messFees": 3000.0,
  "securityDepositPaid": 10000.0,
  "messSubscribed": true,
  "emergencyContactName": "Mr. Ram Singh",
  "emergencyContactNumber": "+91-9876543212",
  "emergencyContactRelation": "Father",
  "notes": "Vegetarian student"
}
```

### 2. **Get All Residents in Hostel**
```http
GET /api/hostels/{hostelId}/residents
Authorization: Bearer {token}
```

### 3. **Get Current Active Residents**
```http
GET /api/hostels/{hostelId}/residents/current
Authorization: Bearer {token}
```

### 4. **Get Resident by ID**
```http
GET /api/hostels/{hostelId}/residents/{residentId}
Authorization: Bearer {token}
```

### 5. **Update Resident Details**
```http
PUT /api/hostels/{hostelId}/residents/{residentId}
Authorization: Bearer {token}
Content-Type: application/json

{
  "monthlyFees": 16000.0,
  "messSubscribed": false
}
```

### 6. **Check Out Resident**
```http
PUT /api/hostels/{hostelId}/residents/{residentId}/checkout
Authorization: Bearer {token}
Content-Type: application/json

{
  "checkOutDate": "2024-06-30",
  "notes": "Completed academic year"
}
```

### 7. **Delete Resident Record**
```http
DELETE /api/hostels/{hostelId}/residents/{residentId}
Authorization: Bearer {token}
```

---

## 🔍 Search & Filtering Endpoints

### 1. **Search Hostels**
```http
GET /api/hostels/search?query=boys&page=0&size=10
Authorization: Bearer {token}
```

### 2. **Filter by Amenities**
```http
GET /api/hostels/by-amenities?wifi=true&gym=true&laundry=true
Authorization: Bearer {token}
```

### 3. **Filter by Fee Range**
```http
GET /api/hostels/by-fee-range?minFee=10000&maxFee=20000
Authorization: Bearer {token}
```

### 4. **Search Residents**
```http
GET /api/hostels/{hostelId}/residents/search?query=john&page=0&size=10
Authorization: Bearer {token}
```

---

## 📊 Statistics & Reporting Endpoints

### 1. **Get Hostel Statistics**
```http
GET /api/hostels/statistics
Authorization: Bearer {token}
```

### 2. **Get Statistics for Specific Hostel**
```http
GET /api/hostels/{hostelId}/statistics
Authorization: Bearer {token}
```

### 3. **Get Resident Demographics**
```http
GET /api/hostels/{hostelId}/residents/demographics
Authorization: Bearer {token}
```

### 4. **Get Fee Collection Statistics**
```http
GET /api/hostels/{hostelId}/residents/fee-statistics
Authorization: Bearer {token}
```

### 5. **Get Monthly Revenue Statistics**
```http
GET /api/hostels/{hostelId}/residents/revenue-statistics?year=2024&month=1
Authorization: Bearer {token}
```

---

## 📋 Entity Models

### Hostel Entity
```json
{
  "id": 1,
  "hostelName": "Boys Hostel A",
  "hostelCode": "BHA001",
  "description": "Main boys hostel",
  "address": "123 College Road",
  "contactNumber": "+91-9876543210",
  "email": "boyshostel@school.com",
  "capacity": 100,
  "occupiedBeds": 75,
  "availableBeds": 25,
  "totalRooms": 50,
  "totalBeds": 100,
  "wardenName": "Mr. Rajesh Kumar",
  "wardenContact": "+91-9876543211",
  "wardenEmail": "warden@school.com",
  "messAvailable": true,
  "messTimings": "7:00 AM - 9:00 AM, 1:00 PM - 3:00 PM, 7:00 PM - 9:00 PM",
  "rulesAndRegulations": "Strict discipline maintained",
  "amenities": "WiFi, Laundry, Gym, Library",
  "isActive": true,
  "feesPerMonth": 15000.0,
  "securityDeposit": 10000.0,
  "laundryAvailable": true,
  "wifiAvailable": true,
  "gymAvailable": true,
  "libraryAvailable": true,
  "createdOn": "2024-01-01T00:00:00",
  "updatedOn": "2024-01-15T10:30:00"
}
```

### Room Entity
```json
{
  "id": 1,
  "roomNumber": "101",
  "floorNumber": 1,
  "roomType": "DOUBLE",
  "capacity": 2,
  "occupiedBeds": 1,
  "availableBeds": 1,
  "roomFeesPerMonth": 8000.0,
  "description": "Spacious double room",
  "amenities": "AC, WiFi, Study Table",
  "isActive": true,
  "isAvailable": true,
  "acAvailable": true,
  "attachedBathroom": true,
  "balconyAvailable": true,
  "furnitureIncluded": true,
  "createdOn": "2024-01-01T00:00:00",
  "updatedOn": "2024-01-15T10:30:00"
}
```

### Bed Entity
```json
{
  "id": 1,
  "bedNumber": "A",
  "bedType": "SINGLE",
  "isOccupied": false,
  "isAvailable": true,
  "isActive": true,
  "bedFeesPerMonth": 4000.0,
  "description": "Comfortable single bed",
  "lastMaintenanceDate": "2024-01-01",
  "nextMaintenanceDate": "2024-07-01",
  "maintenanceNotes": "Regular maintenance",
  "isUnderMaintenance": false,
  "createdOn": "2024-01-01T00:00:00",
  "updatedOn": "2024-01-15T10:30:00"
}
```

### HostelResident Entity
```json
{
  "id": 1,
  "residentId": 1,
  "checkInDate": "2024-01-15",
  "checkOutDate": null,
  "expectedCheckOutDate": "2024-12-31",
  "status": "ACTIVE",
  "monthlyFees": 15000.0,
  "messFees": 3000.0,
  "laundryFees": 1000.0,
  "wifiFees": 500.0,
  "gymFees": 800.0,
  "totalFeesPaid": 45000.0,
  "totalFeesDue": 15000.0,
  "securityDepositPaid": 10000.0,
  "messSubscribed": true,
  "laundrySubscribed": true,
  "wifiSubscribed": true,
  "gymSubscribed": true,
  "emergencyContactName": "Mr. Ram Singh",
  "emergencyContactNumber": "+91-9876543212",
  "emergencyContactRelation": "Father",
  "notes": "Vegetarian student",
  "isActive": true,
  "lastFeePaymentDate": "2024-01-15",
  "nextFeeDueDate": "2024-02-15",
  "outstandingFees": 15000.0,
  "lateFeeCharges": 0.0,
  "penaltyCharges": 0.0,
  "createdOn": "2024-01-15T00:00:00",
  "updatedOn": "2024-01-15T10:30:00"
}
```

---

## 🎨 Frontend Implementation Guide

### 1. **Required Frontend Components**

#### Hostel Management Dashboard
```typescript
// components/hostel/HostelDashboard.tsx
interface HostelDashboardProps {
  hostels: Hostel[];
  statistics: HostelStatistics;
  onHostelSelect: (hostel: Hostel) => void;
}
```

#### Hostel List Component
```typescript
// components/hostel/HostelList.tsx
interface HostelListProps {
  hostels: Hostel[];
  onEdit: (hostel: Hostel) => void;
  onDelete: (id: number) => void;
  onViewDetails: (hostel: Hostel) => void;
}
```

#### Hostel Form Component
```typescript
// components/hostel/HostelForm.tsx
interface HostelFormProps {
  hostel?: Hostel;
  onSubmit: (data: HostelRequestDTO) => void;
  onCancel: () => void;
}
```

#### Room Management Component
```typescript
// components/hostel/RoomManagement.tsx
interface RoomManagementProps {
  hostel: Hostel;
  rooms: Room[];
  onRoomAdd: (room: RoomRequestDTO) => void;
  onRoomEdit: (room: Room) => void;
  onRoomDelete: (id: number) => void;
}
```

#### Resident Management Component
```typescript
// components/hostel/ResidentManagement.tsx
interface ResidentManagementProps {
  hostel: Hostel;
  residents: HostelResident[];
  onResidentAssign: (data: HostelResidentRequestDTO) => void;
  onResidentCheckout: (id: number) => void;
}
```

### 2. **Redux Store Structure**

```typescript
// store/slices/hostelSlice.ts
interface HostelState {
  hostels: Hostel[];
  selectedHostel: Hostel | null;
  rooms: Room[];
  beds: Bed[];
  residents: HostelResident[];
  statistics: HostelStatistics;
  loading: boolean;
  error: string | null;
}
```

### 3. **API Service Layer**

```typescript
// services/hostelApi.ts
class HostelApiService {
  // Hostel CRUD
  async createHostel(data: HostelRequestDTO): Promise<Hostel>;
  async getAllHostels(): Promise<Hostel[]>;
  async getHostelById(id: number): Promise<Hostel>;
  async updateHostel(id: number, data: HostelRequestDTO): Promise<Hostel>;
  async deleteHostel(id: number): Promise<void>;
  
  // Room Management
  async createRoom(hostelId: number, data: RoomRequestDTO): Promise<Room>;
  async getRooms(hostelId: number): Promise<Room[]>;
  async updateRoom(hostelId: number, roomId: number, data: RoomRequestDTO): Promise<Room>;
  async deleteRoom(hostelId: number, roomId: number): Promise<void>;
  
  // Bed Management
  async createBed(hostelId: number, roomId: number, data: BedRequestDTO): Promise<Bed>;
  async getBeds(hostelId: number, roomId: number): Promise<Bed[]>;
  async updateBed(hostelId: number, roomId: number, bedId: number, data: BedRequestDTO): Promise<Bed>;
  async deleteBed(hostelId: number, roomId: number, bedId: number): Promise<void>;
  
  // Resident Management
  async assignResident(hostelId: number, data: HostelResidentRequestDTO): Promise<HostelResident>;
  async getResidents(hostelId: number): Promise<HostelResident[]>;
  async checkOutResident(hostelId: number, residentId: number): Promise<void>;
  
  // Statistics & Reporting
  async getHostelStatistics(): Promise<HostelStatistics>;
  async getHostelStatisticsById(id: number): Promise<HostelStatistics>;
  async searchHostels(query: string): Promise<Hostel[]>;
}
```

### 4. **Routing Structure**

```typescript
// App.tsx routes
const routes = [
  {
    path: '/hostel-management',
    element: <HostelDashboard />,
    children: [
      { path: '', element: <HostelList /> },
      { path: 'create', element: <HostelForm /> },
      { path: ':id', element: <HostelDetails /> },
      { path: ':id/edit', element: <HostelForm /> },
      { path: ':id/rooms', element: <RoomManagement /> },
      { path: ':id/residents', element: <ResidentManagement /> },
      { path: ':id/statistics', element: <HostelStatistics /> }
    ]
  }
];
```

---

## 🧪 Testing Examples

### 1. **PowerShell API Testing Script**

```powershell
# test-hostel-complete.ps1
$baseUrl = "http://localhost:9091"
$username = "vijay-admin"
$password = "vijay"

# Login and get token
$loginResponse = Invoke-RestMethod -Uri "$baseUrl/api/auth/login" -Method POST -Body (@{
    usernameOrEmail = $username
    password = $password
} | ConvertTo-Json) -ContentType "application/json"

$token = $loginResponse.token
$headers = @{ "Authorization" = "Bearer $token" }

# Test all endpoints
Write-Host "Testing Hostel Management API..." -ForegroundColor Green

# 1. Create Hostel
$hostelData = @{
    hostelName = "Test Hostel"
    hostelCode = "TH001"
    capacity = 50
    feesPerMonth = 12000.0
} | ConvertTo-Json

$hostel = Invoke-RestMethod -Uri "$baseUrl/api/hostels" -Method POST -Body $hostelData -Headers $headers -ContentType "application/json"
Write-Host "✅ Created Hostel: $($hostel.hostelName)" -ForegroundColor Green

# 2. Get All Hostels
$hostels = Invoke-RestMethod -Uri "$baseUrl/api/hostels" -Method GET -Headers $headers
Write-Host "✅ Retrieved $($hostels.Count) hostels" -ForegroundColor Green

# 3. Get Statistics
$stats = Invoke-RestMethod -Uri "$baseUrl/api/hostels/statistics" -Method GET -Headers $headers
Write-Host "✅ Retrieved statistics" -ForegroundColor Green

Write-Host "All tests completed successfully!" -ForegroundColor Green
```

### 2. **JavaScript/TypeScript Testing**

```typescript
// test-hostel-api.ts
import axios from 'axios';

const API_BASE = 'http://localhost:9091';

class HostelApiTester {
  private token: string = '';
  
  async login(username: string = 'vijay-admin', password: string = 'vijay') {
    const response = await axios.post(`${API_BASE}/api/auth/login`, {
      usernameOrEmail: username,
      password: password
    });
    this.token = response.data.token;
    return this.token;
  }
  
  get headers() {
    return {
      'Authorization': `Bearer ${this.token}`,
      'Content-Type': 'application/json'
    };
  }
  
  async testAllEndpoints() {
    console.log('🧪 Testing Hostel Management API...');
    
    // Test create hostel
    const hostel = await this.createHostel();
    console.log('✅ Created hostel:', hostel.hostelName);
    
    // Test get all hostels
    const hostels = await this.getAllHostels();
    console.log('✅ Retrieved hostels:', hostels.length);
    
    // Test statistics
    const stats = await this.getStatistics();
    console.log('✅ Retrieved statistics:', stats);
    
    console.log('🎉 All tests passed!');
  }
  
  async createHostel() {
    const response = await axios.post(`${API_BASE}/api/hostels`, {
      hostelName: 'Test Hostel',
      hostelCode: 'TH001',
      capacity: 50,
      feesPerMonth: 12000.0
    }, { headers: this.headers });
    return response.data;
  }
  
  async getAllHostels() {
    const response = await axios.get(`${API_BASE}/api/hostels`, { headers: this.headers });
    return response.data;
  }
  
  async getStatistics() {
    const response = await axios.get(`${API_BASE}/api/hostels/statistics`, { headers: this.headers });
    return response.data;
  }
}

// Usage
const tester = new HostelApiTester();
tester.login().then(() => tester.testAllEndpoints());
```

---

## 📝 Implementation Notes

### **Critical Points for Frontend Development:**

1. **Always use port 9091** (not 8080)
2. **Always include JWT token** in Authorization header
3. **Use the correct login credentials:** `vijay-admin/vijay`
4. **Handle multi-tenancy** - all data is filtered by owner_id
5. **Implement soft delete** - deleted records are marked as `isDeleted: true`
6. **Use pagination** for large data sets
7. **Implement real-time updates** for statistics
8. **Handle error responses** gracefully
9. **Validate all form inputs** before submission
10. **Use proper loading states** for better UX

### **Database Schema Notes:**
- All entities extend `BaseModel` with `createdOn`, `updatedOn`, `createdBy`, `updatedBy`
- All entities have `isDeleted` field for soft delete
- All entities have `owner_id` for multi-tenancy
- Use proper foreign key relationships
- Implement cascade delete where appropriate

### **Security Considerations:**
- All endpoints require authentication
- Use `@PreAuthorize` for role-based access
- Validate all input data
- Implement rate limiting for API calls
- Use HTTPS in production
- Sanitize all user inputs

---

## 🚀 Deployment Checklist

- [ ] Backend server running on port 9091
- [ ] Database connection established
- [ ] JWT token generation working
- [ ] All endpoints tested and working
- [ ] CORS configured for frontend
- [ ] Error handling implemented
- [ ] Logging configured
- [ ] Security headers set
- [ ] Rate limiting implemented
- [ ] API documentation updated

---

**🎉 This documentation provides everything needed to implement the frontend for the Hostel Management System!**

**Remember:** Always test with the provided credentials (`vijay-admin/vijay`) and use port 9091 for all API calls.

package com.vijay.User_Master.service.impl;

import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.Helper.Helper;
import com.vijay.User_Master.config.security.CustomUserDetails;
import com.vijay.User_Master.dto.FavouriteEntryResponse;
import com.vijay.User_Master.dto.PageableResponse;
import com.vijay.User_Master.dto.UserResponse;
import com.vijay.User_Master.dto.WorkerRequest;
import com.vijay.User_Master.dto.WorkerResponse;
import org.springframework.ai.tool.annotation.Tool;
import com.vijay.User_Master.entity.AccountStatus;
import com.vijay.User_Master.entity.Role;
import com.vijay.User_Master.entity.SchoolClass;
import com.vijay.User_Master.repository.WorkerRepository;
import com.vijay.User_Master.repository.SchoolClassRepository;
import com.vijay.User_Master.entity.Worker;
import com.vijay.User_Master.exceptions.ResourceNotFoundException;
import com.vijay.User_Master.repository.FavouriteEntryRepo;
import com.vijay.User_Master.repository.UserRepository;
import com.vijay.User_Master.service.WorkerUserService;
import jakarta.persistence.EntityNotFoundException;
import com.vijay.User_Master.entity.FavouriteEntry;
import com.vijay.User_Master.entity.User;
import com.vijay.User_Master.repository.RoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;


import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class WorkerUserServiceImpl implements WorkerUserService {

    private final WorkerRepository workerRepository;
    private final ModelMapper mapper;
    private final FavouriteEntryRepo favouriteEntryRepo;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final SchoolClassRepository schoolClassRepository;

    @Override
    @Tool(name = "createWorker", description = "Create a new student or teacher with username, email, name and assigned roles (ROLE_STUDENT or ROLE_TEACHER)")
    public WorkerResponse create(WorkerRequest request) {
        log.info("Creating worker with username: {}", request.getUsername());
        
        // Get the logged-in user (owner)
        CustomUserDetails loggedInUser = CommonUtils.getLoggedInUser();
        User owner = userRepository.findById(loggedInUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", loggedInUser.getId()));
        
        // Get roles
        Set<Role> roles = new HashSet<>();
        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            roles = request.getRoles().stream()
                    .map(roleName -> roleRepository.findByName(roleName)
                            .orElseThrow(() -> new ResourceNotFoundException("Role", "name", roleName)))
                    .collect(Collectors.toSet());
        } else {
            // Default to ROLE_WORKER if no roles specified
            Role defaultRole = roleRepository.findByName("ROLE_WORKER")
                    .orElseThrow(() -> new ResourceNotFoundException("Role", "name", "ROLE_WORKER"));
            roles.add(defaultRole);
        }
        
        // Create AccountStatus
        AccountStatus accountStatus = new AccountStatus();
        accountStatus.setIsActive(true);
        accountStatus.setCreatedBy(loggedInUser.getId().intValue());
        accountStatus.setUpdatedBy(loggedInUser.getId().intValue());
        accountStatus.setCreatedOn(new Date());
        accountStatus.setUpdatedOn(new Date());
        
        // Create Worker
        Worker worker = Worker.builder()
                .name(request.getName())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoNo(request.getPhoNo())
                .about(request.getAbout())
                .roles(roles)
                .user(owner)
                .owner(owner) // Set the owner for multi-tenancy
                .accountStatus(accountStatus)
                .build();
        
        // Set audit fields
        worker.setCreatedBy(loggedInUser.getId().intValue());
        worker.setUpdatedBy(loggedInUser.getId().intValue());
        worker.setCreatedOn(new Date());
        worker.setUpdatedOn(new Date());
        
        Worker savedWorker = workerRepository.save(worker);
        log.info("Worker created successfully with ID: {}", savedWorker.getId());
        
        return mapper.map(savedWorker, WorkerResponse.class);
    }

    // find user by id ... for Worker Entity
    @Override
    @Tool(name = "getWorkerById", description = "Get worker (student/teacher) details by ID")
    public WorkerResponse findById(Long id) throws Exception {
        Worker worker = workerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Worker", "ID", id));
        return mapper.map(worker, WorkerResponse.class);
    }

    // You can delete Item ... it saves at recycle bin.
    @Override
    @Tool(name = "softDeleteWorker", description = "Soft delete worker (student/teacher) - move to recycle bin")
    public void softDelete(Long id) throws Exception {
        Worker worker = workerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Worker", "ID", id));
        if (worker.isDeleted()) {
            throw new IllegalArgumentException("Worker with ID " + id + " is already deleted.");
        }
        worker.setDeleted(true);
        worker.setDeletedOn(LocalDateTime.now());
        // Set accountStatus to inactive
        AccountStatus accountStatus = worker.getAccountStatus();
        accountStatus.setIsActive(false);
        worker.setAccountStatus(accountStatus);
        workerRepository.save(worker);
    }

    // You can restore Item form recycle bin
    @Override
    @Tool(name = "restoreWorker", description = "Restore soft deleted worker from recycle bin")
    public void restore(Long id) throws Exception {
        Worker worker = workerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Worker", "ID", id));
        if (worker.isDeleted()) { // Worker is deleted, so restore it
            worker.setDeleted(false);
            worker.setDeletedOn(null);

            AccountStatus accountStatus = worker.getAccountStatus();
            accountStatus.setIsActive(true); // Set accountStatus to Active
            worker.setAccountStatus(accountStatus);

            workerRepository.save(worker); // Save the restored worker
        } else {
            throw new IllegalArgumentException("Worker with ID " + id + " is already present.");
        }
    }

    // You can delete Item form recycle bin - deleting permanently
    @Override
    @Tool(name = "hardDeleteWorker", description = "Permanently delete worker from database (from recycle bin)")
    public void hardDelete(Long id) throws Exception {
        Worker worker = workerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Worker", "ID", id));
        if (worker.isDeleted()) {
            workerRepository.delete(worker); // deleting form recycle bin
        } else {
            throw new IllegalArgumentException("Sorry You can't hard delete Directly");
        }
    }

    @Override
    public WorkerResponse copy(Long aLong) throws Exception {
        return null;
    }

    // find all User from Worker user Entity
    @Override
    @Tool(name = "getAllWorkers", description = "Get all workers (students/teachers) with pagination")
    public PageableResponse<WorkerResponse> findAll(Pageable pageable) {
        CustomUserDetails loggedInUser = CommonUtils.getLoggedInUser();
        Long loggedInUserId = loggedInUser.getId();
        Page<Worker> pages = workerRepository.findByOwner_Id(loggedInUserId, pageable);
        return Helper.getPageableResponse(pages, WorkerResponse.class);
    }

    @Override
    @Tool(name = "searchWorkersWithDynamicFields", description = "Search workers with dynamic field matching")
    public PageableResponse<WorkerResponse> searchItemsWithDynamicFields(String query, Pageable pageable) {
        CustomUserDetails loggedInUser = CommonUtils.getLoggedInUser();
        Long loggedInUserId = loggedInUser.getId();
        
        Specification<Worker> spec = (root, criteriaQuery, criteriaBuilder) -> {
            String likePattern = "%" + query + "%";
            return criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("user").get("id"), loggedInUserId),
                    criteriaBuilder.equal(root.get("isDeleted"), false),
                    criteriaBuilder.or(
                            criteriaBuilder.like(root.get("name"), likePattern),
                            criteriaBuilder.like(root.get("username"), likePattern),
                            criteriaBuilder.like(root.get("email"), likePattern),
                            criteriaBuilder.like(root.get("phoNo"), likePattern),
                            criteriaBuilder.like(root.get("accountStatus").get("isActive").as(String.class), likePattern)
                    )
            );
        };
        Page<Worker> workerPage = workerRepository.findAll(spec, pageable);
        return Helper.getPageableResponse(workerPage, WorkerResponse.class);
    }

    @Override
    @Tool(name = "getAllActiveWorkersWithSorting", description = "Get all active workers with sorting and pagination")
    public PageableResponse<WorkerResponse> getAllActiveUserWithSortingSearching(int pageNumber, int pageSize, String sortBy, String sortDir) {
        CustomUserDetails loggedInUser = CommonUtils.getLoggedInUser();
        Long loggedInUserId = loggedInUser.getId();
        log.info("Logged in user ID: {}", loggedInUserId);
        Sort sort = (sortDir.equalsIgnoreCase("desc")) ? (Sort.by(sortBy).descending()) : (Sort.by(sortBy).ascending());
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Worker> allPages = workerRepository.findByCreatedByAndIsDeletedFalse(loggedInUserId, pageable);
        return Helper.getPageableResponse(allPages, WorkerResponse.class);
    }


    @Override
    @Tool(name = "emptyWorkerRecycleBin", description = "Permanently delete all workers from recycle bin")
    public void emptyRecycleBin(Pageable pageable) {
        CustomUserDetails loggedInUser = CommonUtils.getLoggedInUser();
        Page<Worker> pages = workerRepository.findByCreatedByAndIsDeletedTrue(loggedInUser.getId(), pageable);
        if (pages.isEmpty()) {
            throw new ResourceNotFoundException("Recycle Bin", "Workers", "No deleted workers found for the current user.");
        }
        if (!ObjectUtils.isEmpty(pages)) {
            workerRepository.deleteAll(pages);
        }
    }

    // find all only Active users by superuser id or loggedInUser userId
    @Override
    @Tool(description = "Get all active students and teachers for the current school")
    public List<WorkerResponse> findAllActiveUsers() {
        CustomUserDetails loggedInUser = CommonUtils.getLoggedInUser();
        Long loggedInUserId = loggedInUser.getId();
        List<Worker> userLists = workerRepository.findByOwner_IdAndIsDeletedFalseAndAccountStatus_IsActiveTrue(loggedInUserId, Pageable.unpaged()).getContent();
        return userLists.stream()
                .map((worker -> mapper.map(worker, WorkerResponse.class)))
                .collect(Collectors.toList());
    }


    // find all only Deleted users by superuser id or loggedInUser userId
    @Override
    @Tool(name = "getWorkerRecycleBin", description = "Get all soft deleted workers from recycle bin")
    public PageableResponse<WorkerResponse> getRecycleBin(Pageable pageable) { // restore delete item from RecycleBin
        CustomUserDetails loggedInUser = CommonUtils.getLoggedInUser();
        Page<Worker> users = workerRepository.findByCreatedByAndIsDeletedTrue(loggedInUser.getId(), pageable);

        if (workerRepository.findByCreatedByAndIsDeletedTrue(loggedInUser.getId(), pageable).isEmpty()) {
            throw new ResourceNotFoundException("Recycle Bin", "Workers", "No deleted workers found for the current user.");
        }
        return Helper.getPageableResponse(users, WorkerResponse.class);
    }

    @Override
    @Tool(name = "favoriteWorkerUser", description = "Add worker to favorites list")
    public void favoriteWorkerUser(Long workerId) throws Exception {
        CustomUserDetails loggedInUser = CommonUtils.getLoggedInUser();
        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() -> new ResourceNotFoundException("Worker", "ID", workerId));
        FavouriteEntry favouriteEntry = FavouriteEntry.builder()
                .worker(worker)
                .userId(loggedInUser.getId())
                .build(); // Assuming the user who favorites is the worker's associated user .build();
        favouriteEntryRepo.save(favouriteEntry);
    }

    @Override
    @Tool(name = "unFavoriteWorkerUser", description = "Remove worker from favorites list")
    public void unFavoriteWorkerUser(Long id) throws Exception {
        FavouriteEntry favouriteEntry = favouriteEntryRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Favorite", "ID", id));
        favouriteEntryRepo.delete(favouriteEntry);
    }

    @Override
    @Tool(name = "getUserFavoriteWorkers", description = "Get all favorite workers for current user")
    public List<FavouriteEntryResponse> getUserFavoriteWorkerUsers() throws Exception {
        CustomUserDetails loggedInUser = CommonUtils.getLoggedInUser();
        List<FavouriteEntry> favouriteWorkers = favouriteEntryRepo.findByUserId(loggedInUser.getId());
        return favouriteWorkers.stream()
                .map((worker) -> mapper.map(worker, FavouriteEntryResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    @Tool(description = "Get workers (students/teachers) for a specific school owner with pagination")
    public PageableResponse<WorkerResponse> getWorkersBySuperUserId(Long superUserId, int pageNumber, int pageSize, String sortBy, String sortDir) {
        CustomUserDetails loggedInUser = CommonUtils.getLoggedInUser();

        if (!superUserId.equals(loggedInUser.getId())) {
            throw new IllegalArgumentException("You are not authorized to access this user's workers.");
        }

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        Page<Worker> workerPage = workerRepository.findByUser_Id(superUserId, pageable);

        return Helper.getPageableResponse(workerPage, WorkerResponse.class);
    }

    @Override
    @Tool(name = "getWorkersBySuperUserWithFilter", description = "Get workers by super user with status filter (active/deleted/expired)")
    public PageableResponse<WorkerResponse> getWorkersBySuperUserWithFilter(Long superUserId, String filter, Pageable pageable) {
        Page<Worker> page = switch (filter.toLowerCase()) {
            case "active" ->
                    workerRepository.findByUser_IdAndIsDeletedFalseAndAccountStatus_IsActiveTrue(superUserId, pageable);
            case "deleted" -> workerRepository.findByUser_IdAndIsDeletedTrue(superUserId, pageable);
            case "expired" ->
                    workerRepository.findByUser_IdAndIsDeletedFalseAndAccountStatus_IsActiveFalse(superUserId, pageable);
            default -> workerRepository.findByUser_Id(superUserId, pageable);
        };

        return Helper.getPageableResponse(page, WorkerResponse.class);
    }

    @Override
    @Tool(name = "getWorkersWithAdvancedFilter", description = "Get workers with advanced filtering options")
    public Page<WorkerResponse> getWorkersWithFilter(Long superUserId, Boolean isDeleted, Boolean isActive, String keyword, Pageable pageable) {
        Page<Worker> workers;

        if (StringUtils.hasText(keyword)) {
            workers = workerRepository.searchWorkersBySuperUserWithKeyword(keyword, isDeleted, isActive, superUserId, pageable);
        } else {
            if (isDeleted != null && isActive != null) {
                workers = workerRepository.findByUser_IdAndIsDeletedAndAccountStatus_IsActive(superUserId, isDeleted, isActive, pageable);
            } else if (isDeleted != null) {
                workers = workerRepository.findByUser_IdAndIsDeleted(superUserId, isDeleted, pageable);
            } else if (isActive != null) {
                workers = workerRepository.findByUser_Id(superUserId, pageable);
            } else {
                workers = workerRepository.findByUser_Id(superUserId, pageable);
            }
        }

        return workers.map(worker -> mapper.map(worker, WorkerResponse.class));
    }

    @Override
    @Tool(name = "updateWorkerAccountStatus", description = "Update worker account status (activate/deactivate)")
    public void updateAccountStatus(Long userId, Boolean isActive) {

        Worker worker = workerRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        AccountStatus accountStatus = worker.getAccountStatus();
        if (accountStatus == null) {
            accountStatus = new AccountStatus(); // New status
        }

        accountStatus.setIsActive(isActive); // update the status
        worker.setAccountStatus(accountStatus); // assign to user

        workerRepository.save(worker); // cascade should handle persist/update

    }

    @Override
    @Tool(name = "updateWorker", description = "Update worker (student/teacher) details")
    public WorkerResponse update(Long id, WorkerRequest request) throws Exception {
        log.info("Updating worker with ID: {}", id);
        
        // Get the logged-in user (owner)
        CustomUserDetails loggedInUser = CommonUtils.getLoggedInUser();
        User owner = userRepository.findById(loggedInUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", loggedInUser.getId()));
        
        // Find existing worker
        Worker existingWorker = workerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Worker", "ID", id));
        
        // Check if worker belongs to the current owner (multi-tenancy)
        if (!existingWorker.getOwner().getId().equals(owner.getId())) {
            throw new RuntimeException("Worker does not belong to current user");
        }
        
        // Update basic fields
        existingWorker.setName(request.getName());
        existingWorker.setEmail(request.getEmail());
        existingWorker.setPhoNo(request.getPhoNo());
        existingWorker.setAbout(request.getAbout());
        
        // Update image name if provided
        if (request.getImageName() != null) {
            existingWorker.setImageName(request.getImageName());
        }
        
        // Update current class if provided
        if (request.getCurrentClassId() != null) {
            SchoolClass schoolClass = schoolClassRepository.findById(request.getCurrentClassId())
                    .orElseThrow(() -> new ResourceNotFoundException("SchoolClass", "ID", request.getCurrentClassId()));
            existingWorker.setCurrentClass(schoolClass);
        }
        
        // Update password if provided
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            existingWorker.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        
        // Update roles if provided
        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            Set<Role> roles = request.getRoles().stream()
                    .map(roleName -> roleRepository.findByName(roleName)
                            .orElseThrow(() -> new ResourceNotFoundException("Role", "name", roleName)))
                    .collect(Collectors.toSet());
            existingWorker.setRoles(roles);
        }
        
        // Update audit fields
        existingWorker.setUpdatedBy(loggedInUser.getId().intValue());
        existingWorker.setUpdatedOn(new Date());
        
        Worker updatedWorker = workerRepository.save(existingWorker);
        log.info("Worker updated successfully with ID: {}", updatedWorker.getId());
        
        WorkerResponse response = mapper.map(updatedWorker, WorkerResponse.class);
        
        // Set class information in response
        if (updatedWorker.getCurrentClass() != null) {
            response.setCurrentClassId(updatedWorker.getCurrentClass().getId());
            response.setCurrentClassName(updatedWorker.getCurrentClass().getClassName());
        }
        
        return response;
    }


}
                   
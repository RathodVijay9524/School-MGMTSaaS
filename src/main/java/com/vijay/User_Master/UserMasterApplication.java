package com.vijay.User_Master;

import com.vijay.User_Master.entity.AccountStatus;
import com.vijay.User_Master.entity.Role;
import com.vijay.User_Master.entity.User;
import com.vijay.User_Master.entity.Worker;
import com.vijay.User_Master.repository.RoleRepository;
import com.vijay.User_Master.repository.UserRepository;
import com.vijay.User_Master.repository.WorkerRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
@EnableScheduling
public class UserMasterApplication {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WorkerRepository workerRepository;

    @Autowired
    private RoleRepository roleRepository;

    public static void main(String[] args) {
        SpringApplication.run(UserMasterApplication.class, args);
    }

    @PostConstruct
    protected void init() {
// you are running first time application then this method un-comments this.
        // getCurrentAuditor() and comment another
        if (userRepository.count() == 0 && workerRepository.count() == 0) {

            // Ensure roles are created and saved before assigning
            Role adminRole = createAndSaveRole("ROLE_ADMIN");
            Role superUserRole = createAndSaveRole("ROLE_SUPER_USER");
            Role normalRole = createAndSaveRole("ROLE_NORMAL");
            Role workerRole = createAndSaveRole("ROLE_WORKER");
            Role teacherRole = createAndSaveRole("ROLE_TEACHER");
            Role studentRole = createAndSaveRole("ROLE_STUDENT");
            Role managerRole = createAndSaveRole("ROLE_MANAGER");

            // Add roles to sets
            Set<Role> adminRoles = new HashSet<>(Arrays.asList(adminRole));
            Set<Role> userRoles = new HashSet<>(Arrays.asList(superUserRole));
            Set<Role> normalRoles = new HashSet<>(Arrays.asList(normalRole));
            Set<Role> workerRoles = new HashSet<>(Arrays.asList(workerRole));

            // Create users
            User admin = User.builder()
                    .name("Vimal Kumar")
                    .email("admin@gmail.com")
                    .username("admin")
                    .password(passwordEncoder.encode("admin"))
                    .roles(adminRoles)
                    .build();
            admin.setCreatedBy(1);
            admin.setUpdatedBy(1);
            admin.setCreatedOn(new Date());
            admin.setUpdatedOn(new Date());

            User user = User.builder()
                    .name("Ajay Rawat")
                    .email("user@gmail.com")
                    .username("user")
                    .password(passwordEncoder.encode("user"))
                    .roles(userRoles)
                    .build();
            user.setCreatedBy(1);
            user.setUpdatedBy(1);
            user.setCreatedOn(new Date());
            user.setUpdatedOn(new Date());

            User normalUser = User.builder()
                    .name("Vijay Rathod")
                    .email("normal@gmail.com")
                    .username("normal")
                    .password(passwordEncoder.encode("normal"))
                    .roles(normalRoles)
                    .build();
            normalUser.setCreatedBy(1);
            normalUser.setUpdatedBy(1);
            normalUser.setCreatedOn(new Date());
            normalUser.setUpdatedOn(new Date());

            User karina = User.builder()
                    .name("Karina Admin")
                    .email("karina@example.com")
                    .username("karina")
                    .password(passwordEncoder.encode("karina"))
                    .roles(adminRoles)
                    .build();
            karina.setCreatedBy(1);
            karina.setUpdatedBy(1);
            karina.setCreatedOn(new Date());
            karina.setUpdatedOn(new Date());

            // Save users first
            userRepository.saveAll(Arrays.asList(admin, user, normalUser, karina));

            // Create worker with AccountStatus
            Worker worker = Worker.builder()
                    .name("Salman Khan")
                    .email("worker@gmail.com")
                    .username("worker")
                    .password(passwordEncoder.encode("worker"))
                    .roles(workerRoles)
                    .user(karina) // Use karina as the user
                    .owner(karina) // Set karina as the owner for multi-tenancy
                    .accountStatus(createDefaultAccountStatus())
                    .build();
            worker.setCreatedBy(1);
            worker.setUpdatedBy(1);
            worker.setCreatedOn(new Date());
            worker.setUpdatedOn(new Date());
            workerRepository.save(worker);
        }

    }

    private AccountStatus createDefaultAccountStatus() {
        AccountStatus accountStatus = new AccountStatus();
        accountStatus.setIsActive(true);
        accountStatus.setCreatedBy(1);
        accountStatus.setUpdatedBy(1);
        accountStatus.setCreatedOn(new Date());
        accountStatus.setUpdatedOn(new Date());
        return accountStatus;
    }

    private Role createAndSaveRole(String roleName) {
        Role role = new Role();
        role.setName(roleName);
        role.setActive(true);
        role.setDeleted(false);
        // Set manual audit fields to bypass authentication requirement
        role.setCreatedBy(1); // Default system user
        role.setUpdatedBy(1); // Default system user
        role.setCreatedOn(new Date());
        role.setUpdatedOn(new Date());
        return roleRepository.save(role);
    }
}

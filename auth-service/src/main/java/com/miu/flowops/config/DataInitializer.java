package com.miu.flowops.config;

import com.miu.flowops.model.Role;
import com.miu.flowops.model.User;
import com.miu.flowops.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    @Profile("!test")
    CommandLineRunner initUserData() {
        return args -> {
            if (userRepository.count() > 0) {
                log.info("User data already exists, skipping initialization");
                return;
            }

            log.info("Initializing test user data...");

            String defaultPassword = passwordEncoder.encode("password123");

            List<User> users = List.of(
                    // Admins / Project Managers
                    User.builder()
                            .id("user-101")
                            .email("sarah.chen@opsflow.com")
                            .username("sarah.chen")
                            .fullName("Sarah Chen")
                            .password(defaultPassword)
                            .verified(true)
                            .avatarUrl("https://api.dicebear.com/7.x/avataaars/svg?seed=sarah")
                            .roles(Set.of(Role.ADMIN))
                            .build(),

                    // Developers
                    User.builder()
                            .id("user-102")
                            .email("michael.johnson@opsflow.com")
                            .username("michael.johnson")
                            .fullName("Michael Johnson")
                            .password(defaultPassword)
                            .verified(true)
                            .avatarUrl("https://api.dicebear.com/7.x/avataaars/svg?seed=michael")
                            .roles(Set.of(Role.DEVELOPER))
                            .build(),

                    User.builder()
                            .id("user-103")
                            .email("emily.davis@opsflow.com")
                            .username("emily.davis")
                            .fullName("Emily Davis")
                            .password(defaultPassword)
                            .verified(true)
                            .avatarUrl("https://api.dicebear.com/7.x/avataaars/svg?seed=emily")
                            .roles(Set.of(Role.DEVELOPER))
                            .build(),

                    User.builder()
                            .id("user-104")
                            .email("david.wilson@opsflow.com")
                            .username("david.wilson")
                            .fullName("David Wilson")
                            .password(defaultPassword)
                            .verified(true)
                            .avatarUrl("https://api.dicebear.com/7.x/avataaars/svg?seed=david")
                            .roles(Set.of(Role.DEVELOPER))
                            .build(),

                    User.builder()
                            .id("user-105")
                            .email("jessica.martinez@opsflow.com")
                            .username("jessica.martinez")
                            .fullName("Jessica Martinez")
                            .password(defaultPassword)
                            .verified(true)
                            .avatarUrl("https://api.dicebear.com/7.x/avataaars/svg?seed=jessica")
                            .roles(Set.of(Role.DEVELOPER))
                            .build(),

                    User.builder()
                            .id("user-106")
                            .email("robert.brown@opsflow.com")
                            .username("robert.brown")
                            .fullName("Robert Brown")
                            .password(defaultPassword)
                            .verified(true)
                            .avatarUrl("https://api.dicebear.com/7.x/avataaars/svg?seed=robert")
                            .roles(Set.of(Role.DEVELOPER))
                            .build(),

                    User.builder()
                            .id("user-107")
                            .email("amanda.taylor@opsflow.com")
                            .username("amanda.taylor")
                            .fullName("Amanda Taylor")
                            .password(defaultPassword)
                            .verified(true)
                            .avatarUrl("https://api.dicebear.com/7.x/avataaars/svg?seed=amanda")
                            .roles(Set.of(Role.DEVELOPER))
                            .build(),

                    User.builder()
                            .id("user-108")
                            .email("christopher.lee@opsflow.com")
                            .username("christopher.lee")
                            .fullName("Christopher Lee")
                            .password(defaultPassword)
                            .verified(true)
                            .avatarUrl("https://api.dicebear.com/7.x/avataaars/svg?seed=christopher")
                            .roles(Set.of(Role.DEVELOPER))
                            .build(),

                    User.builder()
                            .id("user-109")
                            .email("michelle.garcia@opsflow.com")
                            .username("michelle.garcia")
                            .fullName("Michelle Garcia")
                            .password(defaultPassword)
                            .verified(true)
                            .avatarUrl("https://api.dicebear.com/7.x/avataaars/svg?seed=michelle")
                            .roles(Set.of(Role.DEVELOPER))
                            .build(),

                    User.builder()
                            .id("user-110")
                            .email("daniel.anderson@opsflow.com")
                            .username("daniel.anderson")
                            .fullName("Daniel Anderson")
                            .password(defaultPassword)
                            .verified(true)
                            .avatarUrl("https://api.dicebear.com/7.x/avataaars/svg?seed=daniel")
                            .roles(Set.of(Role.DEVELOPER))
                            .build(),

                    User.builder()
                            .id("user-111")
                            .email("jennifer.white@opsflow.com")
                            .username("jennifer.white")
                            .fullName("Jennifer White")
                            .password(defaultPassword)
                            .verified(true)
                            .avatarUrl("https://api.dicebear.com/7.x/avataaars/svg?seed=jennifer")
                            .roles(Set.of(Role.DEVELOPER))
                            .build(),

                    User.builder()
                            .id("user-112")
                            .email("kevin.thompson@opsflow.com")
                            .username("kevin.thompson")
                            .fullName("Kevin Thompson")
                            .password(defaultPassword)
                            .verified(true)
                            .avatarUrl("https://api.dicebear.com/7.x/avataaars/svg?seed=kevin")
                            .roles(Set.of(Role.DEVELOPER))
                            .build(),

                    User.builder()
                            .id("user-113")
                            .email("lisa.robinson@opsflow.com")
                            .username("lisa.robinson")
                            .fullName("Lisa Robinson")
                            .password(defaultPassword)
                            .verified(true)
                            .avatarUrl("https://api.dicebear.com/7.x/avataaars/svg?seed=lisa")
                            .roles(Set.of(Role.DEVELOPER))
                            .build(),

                    User.builder()
                            .id("user-114")
                            .email("nicole.harris@opsflow.com")
                            .username("nicole.harris")
                            .fullName("Nicole Harris")
                            .password(defaultPassword)
                            .verified(true)
                            .avatarUrl("https://api.dicebear.com/7.x/avataaars/svg?seed=nicole")
                            .roles(Set.of(Role.DEVELOPER))
                            .build(),

                    User.builder()
                            .id("user-115")
                            .email("steven.clark@opsflow.com")
                            .username("steven.clark")
                            .fullName("Steven Clark")
                            .password(defaultPassword)
                            .verified(true)
                            .avatarUrl("https://api.dicebear.com/7.x/avataaars/svg?seed=steven")
                            .roles(Set.of(Role.DEVELOPER))
                            .build(),

                    User.builder()
                            .id("user-116")
                            .email("ryan.martinez@opsflow.com")
                            .username("ryan.martinez")
                            .fullName("Ryan Martinez")
                            .password(defaultPassword)
                            .verified(true)
                            .avatarUrl("https://api.dicebear.com/7.x/avataaars/svg?seed=ryan")
                            .roles(Set.of(Role.DEVELOPER))
                            .build(),

                    User.builder()
                            .id("user-117")
                            .email("patricia.lewis@opsflow.com")
                            .username("patricia.lewis")
                            .fullName("Patricia Lewis")
                            .password(defaultPassword)
                            .verified(true)
                            .avatarUrl("https://api.dicebear.com/7.x/avataaars/svg?seed=patricia")
                            .roles(Set.of(Role.ADMIN))
                            .build(),

                    User.builder()
                            .id("user-118")
                            .email("andrew.walker@opsflow.com")
                            .username("andrew.walker")
                            .fullName("Andrew Walker")
                            .password(defaultPassword)
                            .verified(true)
                            .avatarUrl("https://api.dicebear.com/7.x/avataaars/svg?seed=andrew")
                            .roles(Set.of(Role.DEVELOPER))
                            .build(),

                    User.builder()
                            .id("user-119")
                            .email("james.scott@opsflow.com")
                            .username("james.scott")
                            .fullName("James Scott")
                            .password(defaultPassword)
                            .verified(true)
                            .avatarUrl("https://api.dicebear.com/7.x/avataaars/svg?seed=james")
                            .roles(Set.of(Role.DEVELOPER))
                            .build(),

                    User.builder()
                            .id("user-120")
                            .email("elizabeth.young@opsflow.com")
                            .username("elizabeth.young")
                            .fullName("Elizabeth Young")
                            .password(defaultPassword)
                            .verified(true)
                            .avatarUrl("https://api.dicebear.com/7.x/avataaars/svg?seed=elizabeth")
                            .roles(Set.of(Role.DEVELOPER))
                            .build()
            );

            userRepository.saveAll(users);

            log.info("Successfully initialized {} test users (password: password123)", users.size());
        };
    }
}

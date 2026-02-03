package com.miu.flowops.util;

import com.miu.flowops.model.*;
import com.miu.flowops.repository.DiscussionRepository;
import com.miu.flowops.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DiscussionRepository discussionRepository;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            log.info("Initializing test data...");
            
            // Create test users
            User admin = User.builder()
                    .id("user-001")
                    .email("admin@flowops.com")
                    .username("admin")
                    .fullName("Admin User")
                    .password("password123")
                    .verified(true)
                    .roles(Set.of(Role.ADMIN))
                    .build();

            User dev1 = User.builder()
                    .id("user-002")
                    .email("john.doe@flowops.com")
                    .username("johndoe")
                    .fullName("John Doe")
                    .password("password123")
                    .verified(true)
                    .roles(Set.of(Role.DEVELOPER))
                    .build();

            User dev2 = User.builder()
                    .id("user-003")
                    .email("jane.smith@flowops.com")
                    .username("janesmith")
                    .fullName("Jane Smith")
                    .password("password123")
                    .verified(true)
                    .roles(Set.of(Role.DEVELOPER))
                    .build();

            userRepository.saveAll(List.of(admin, dev1, dev2));
            log.info("Created 3 test users");

            // Create test discussions
            Discussion discussion1 = Discussion.builder()
                    .id("disc-001")
                    .title("API Design for User Authentication")
                    .content("We need to discuss the best approach for implementing JWT authentication in our microservices.")
                    .authorId("user-002")
                    .authorName("John Doe")
                    .releaseId("release-001")
                    .taskId("task-001")
                    .type(DiscussionType.QUESTION)
                    .status(DiscussionStatus.OPEN)
                    .comments(new ArrayList<>(List.of(
                            Comment.builder()
                                    .id("comment-001")
                                    .content("I suggest using Spring Security with OAuth2.")
                                    .authorId("user-003")
                                    .authorName("Jane Smith")
                                    .createdAt(LocalDateTime.now().minusHours(2))
                                    .updatedAt(LocalDateTime.now().minusHours(2))
                                    .isEdited(false)
                                    .build(),
                            Comment.builder()
                                    .id("comment-002")
                                    .content("Agreed. We should also implement refresh tokens.")
                                    .authorId("user-001")
                                    .authorName("Admin User")
                                    .createdAt(LocalDateTime.now().minusHours(1))
                                    .updatedAt(LocalDateTime.now().minusHours(1))
                                    .isEdited(false)
                                    .build()
                    )))
                    .createdAt(LocalDateTime.now().minusDays(1))
                    .updatedAt(LocalDateTime.now().minusHours(1))
                    .build();

            Discussion discussion2 = Discussion.builder()
                    .id("disc-002")
                    .title("Database Migration Strategy")
                    .content("What's the best approach to migrate from MySQL to MongoDB for the discussion service?")
                    .authorId("user-003")
                    .authorName("Jane Smith")
                    .releaseId("release-001")
                    .type(DiscussionType.ISSUE)
                    .status(DiscussionStatus.OPEN)
                    .comments(new ArrayList<>())
                    .createdAt(LocalDateTime.now().minusHours(5))
                    .updatedAt(LocalDateTime.now().minusHours(5))
                    .build();

            Discussion discussion3 = Discussion.builder()
                    .id("disc-003")
                    .title("Sprint Planning - Release 2.0")
                    .content("Let's discuss the priorities for the upcoming sprint.")
                    .authorId("user-001")
                    .authorName("Admin User")
                    .releaseId("release-002")
                    .type(DiscussionType.GENERAL)
                    .status(DiscussionStatus.RESOLVED)
                    .comments(new ArrayList<>(List.of(
                            Comment.builder()
                                    .id("comment-003")
                                    .content("I think we should focus on performance improvements.")
                                    .authorId("user-002")
                                    .authorName("John Doe")
                                    .createdAt(LocalDateTime.now().minusDays(2))
                                    .updatedAt(LocalDateTime.now().minusDays(2))
                                    .isEdited(false)
                                    .build()
                    )))
                    .createdAt(LocalDateTime.now().minusDays(3))
                    .updatedAt(LocalDateTime.now().minusDays(2))
                    .resolvedAt(LocalDateTime.now().minusDays(1))
                    .resolvedBy("Admin User")
                    .build();

            discussionRepository.saveAll(List.of(discussion1, discussion2, discussion3));
            log.info("Created 3 test discussions");
            
            log.info("Test data initialization complete!");
        } else {
            log.info("Test data already exists, skipping initialization.");
        }
    }
}

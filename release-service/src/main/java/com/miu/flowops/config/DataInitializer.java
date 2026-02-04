package com.miu.flowops.config;

import com.miu.flowops.model.Release;
import com.miu.flowops.model.Task;
import com.miu.flowops.model.TaskStatus;
import com.miu.flowops.repository.ReleaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final ReleaseRepository releaseRepository;

    @Bean
    @Profile("!test")
    CommandLineRunner initReleaseData() {
        return args -> {
            if (releaseRepository.count() > 0) {
                log.info("Release data already exists, skipping initialization");
                return;
            }

            log.info("Initializing release test data...");

            List<Release> releases = new ArrayList<>();

            // Release 1: v2.5.0 - In Progress
            Release release1 = Release.builder()
                    .id("release-001")
                    .title("OpsFlow v2.5.0 - Authentication & Performance")
                    .isCompleted(false)
                    .tasks(List.of(
                            Task.builder()
                                    .id("task-001")
                                    .title("Implement OAuth2 Authentication")
                                    .description("Add support for OAuth2/OpenID Connect authentication flow with support for Google, GitHub, and Microsoft providers")
                                    .status(TaskStatus.IN_PROCESS)
                                    .developerId("user-103")
                                    .orderIndex(1)
                                    .startedAt(LocalDateTime.now().minusDays(5))
                                    .build(),
                            Task.builder()
                                    .id("task-002")
                                    .title("Optimize Dashboard Query Performance")
                                    .description("Reduce dashboard load time by optimizing MongoDB aggregation queries and adding appropriate indexes")
                                    .status(TaskStatus.IN_PROCESS)
                                    .developerId("user-105")
                                    .orderIndex(2)
                                    .startedAt(LocalDateTime.now().minusDays(3))
                                    .build(),
                            Task.builder()
                                    .id("task-003")
                                    .title("Add Redis Caching Layer")
                                    .description("Implement Redis caching for frequently accessed data to improve response times")
                                    .status(TaskStatus.TODO)
                                    .developerId("user-104")
                                    .orderIndex(3)
                                    .build(),
                            Task.builder()
                                    .id("task-004")
                                    .title("Two-Factor Authentication")
                                    .description("Add TOTP-based two-factor authentication for enhanced security")
                                    .status(TaskStatus.TODO)
                                    .developerId("user-102")
                                    .orderIndex(4)
                                    .build(),
                            Task.builder()
                                    .id("task-005")
                                    .title("Session Management Improvements")
                                    .description("Implement secure session handling with device tracking and forced logout capabilities")
                                    .status(TaskStatus.TODO)
                                    .developerId("user-107")
                                    .orderIndex(5)
                                    .build()
                    ))
                    .build();
            releases.add(release1);

            // Release 2: v2.6.0 - Feature Release (Not Started)
            Release release2 = Release.builder()
                    .id("release-002")
                    .title("OpsFlow v2.6.0 - Reporting & Analytics")
                    .isCompleted(false)
                    .tasks(List.of(
                            Task.builder()
                                    .id("task-006")
                                    .title("Custom Report Builder")
                                    .description("Create a drag-and-drop report builder interface for users to create custom reports")
                                    .status(TaskStatus.TODO)
                                    .developerId("user-110")
                                    .orderIndex(1)
                                    .build(),
                            Task.builder()
                                    .id("task-007")
                                    .title("Export Reports to PDF/Excel")
                                    .description("Add functionality to export reports in PDF and Excel formats")
                                    .status(TaskStatus.TODO)
                                    .developerId("user-111")
                                    .orderIndex(2)
                                    .build(),
                            Task.builder()
                                    .id("task-008")
                                    .title("Real-time Analytics Dashboard")
                                    .description("Build a real-time analytics dashboard with WebSocket updates for live metrics")
                                    .status(TaskStatus.TODO)
                                    .developerId("user-112")
                                    .orderIndex(3)
                                    .build(),
                            Task.builder()
                                    .id("task-009")
                                    .title("Scheduled Report Delivery")
                                    .description("Allow users to schedule automatic report generation and email delivery")
                                    .status(TaskStatus.TODO)
                                    .developerId("user-114")
                                    .orderIndex(4)
                                    .build(),
                            Task.builder()
                                    .id("task-101")
                                    .title("Database Migration for Reporting")
                                    .description("Create necessary database schema changes and migrations for the reporting module")
                                    .status(TaskStatus.TODO)
                                    .developerId("user-105")
                                    .orderIndex(5)
                                    .build()
                    ))
                    .build();
            releases.add(release2);

            // Release 3: v2.4.0 - Completed Release
            Release release3 = Release.builder()
                    .id("release-003")
                    .title("OpsFlow v2.4.0 - UI/UX Improvements")
                    .isCompleted(true)
                    .tasks(List.of(
                            Task.builder()
                                    .id("task-010")
                                    .title("Redesign Navigation Menu")
                                    .description("Implement new collapsible sidebar navigation with improved UX")
                                    .status(TaskStatus.COMPLETED)
                                    .developerId("user-114")
                                    .orderIndex(1)
                                    .startedAt(LocalDateTime.now().minusDays(20))
                                    .completedAt(LocalDateTime.now().minusDays(15))
                                    .build(),
                            Task.builder()
                                    .id("task-011")
                                    .title("Responsive Mobile Layout")
                                    .description("Make all pages fully responsive for mobile and tablet devices")
                                    .status(TaskStatus.COMPLETED)
                                    .developerId("user-112")
                                    .orderIndex(2)
                                    .startedAt(LocalDateTime.now().minusDays(18))
                                    .completedAt(LocalDateTime.now().minusDays(12))
                                    .build(),
                            Task.builder()
                                    .id("task-012")
                                    .title("Accessibility Improvements")
                                    .description("Implement WCAG 2.1 AA compliance across the application")
                                    .status(TaskStatus.COMPLETED)
                                    .developerId("user-115")
                                    .orderIndex(3)
                                    .startedAt(LocalDateTime.now().minusDays(15))
                                    .completedAt(LocalDateTime.now().minusDays(8))
                                    .build(),
                            Task.builder()
                                    .id("task-013")
                                    .title("Loading State Indicators")
                                    .description("Add skeleton loaders and progress indicators throughout the app")
                                    .status(TaskStatus.COMPLETED)
                                    .developerId("user-110")
                                    .orderIndex(4)
                                    .startedAt(LocalDateTime.now().minusDays(10))
                                    .completedAt(LocalDateTime.now().minusDays(6))
                                    .build()
                    ))
                    .build();
            releases.add(release3);

            // Release 4: Hotfix Release - In Progress
            Release release4 = Release.builder()
                    .id("release-004")
                    .title("Hotfix v2.4.1 - Critical Bug Fixes")
                    .isCompleted(false)
                    .tasks(List.of(
                            Task.builder()
                                    .id("task-042")
                                    .title("Fix Payment Processing for International Transactions")
                                    .description("Fix currency conversion API integration that broke after third-party API changes")
                                    .status(TaskStatus.IN_PROCESS)
                                    .developerId("user-107")
                                    .orderIndex(1)
                                    .startedAt(LocalDateTime.now().minusDays(2))
                                    .build(),
                            Task.builder()
                                    .id("task-043")
                                    .title("Fix Memory Leak in WebSocket Handler")
                                    .description("Address memory leak causing service degradation after prolonged use")
                                    .status(TaskStatus.COMPLETED)
                                    .developerId("user-108")
                                    .orderIndex(2)
                                    .startedAt(LocalDateTime.now().minusDays(3))
                                    .completedAt(LocalDateTime.now().minusDays(2))
                                    .build(),
                            Task.builder()
                                    .id("task-044")
                                    .title("Fix Timezone Display Bug")
                                    .description("Correct timezone conversion issues in scheduled reports")
                                    .status(TaskStatus.TODO)
                                    .developerId("user-106")
                                    .orderIndex(3)
                                    .build()
                    ))
                    .build();
            releases.add(release4);

            // Release 5: Infrastructure Release
            Release release5 = Release.builder()
                    .id("release-005")
                    .title("OpsFlow v2.7.0 - Infrastructure & DevOps")
                    .isCompleted(false)
                    .tasks(List.of(
                            Task.builder()
                                    .id("task-089")
                                    .title("Implement Notification Service Messaging")
                                    .description("Set up Kafka-based async messaging for the notification service with delivery confirmation")
                                    .status(TaskStatus.COMPLETED)
                                    .developerId("user-116")
                                    .orderIndex(1)
                                    .startedAt(LocalDateTime.now().minusDays(10))
                                    .completedAt(LocalDateTime.now().minusDays(9))
                                    .build(),
                            Task.builder()
                                    .id("task-090")
                                    .title("Kubernetes Migration")
                                    .description("Migrate services from Docker Compose to Kubernetes with Helm charts")
                                    .status(TaskStatus.TODO)
                                    .developerId("user-118")
                                    .orderIndex(2)
                                    .build(),
                            Task.builder()
                                    .id("task-091")
                                    .title("Implement Distributed Tracing")
                                    .description("Add OpenTelemetry tracing across all microservices for better observability")
                                    .status(TaskStatus.TODO)
                                    .developerId("user-117")
                                    .orderIndex(3)
                                    .build(),
                            Task.builder()
                                    .id("task-092")
                                    .title("Set Up CI/CD Pipeline")
                                    .description("Create GitHub Actions workflows for automated testing and deployment")
                                    .status(TaskStatus.TODO)
                                    .developerId("user-119")
                                    .orderIndex(4)
                                    .build(),
                            Task.builder()
                                    .id("task-093")
                                    .title("Database Backup Automation")
                                    .description("Implement automated MongoDB backups with point-in-time recovery")
                                    .status(TaskStatus.TODO)
                                    .developerId("user-120")
                                    .orderIndex(5)
                                    .build()
                    ))
                    .build();
            releases.add(release5);

            releaseRepository.saveAll(releases);

            log.info("Successfully initialized {} releases with {} total tasks", 
                    releases.size(), 
                    releases.stream().mapToInt(r -> r.getTasks().size()).sum());
        };
    }
}

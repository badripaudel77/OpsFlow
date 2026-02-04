package com.miu.flowops.config;

import com.miu.flowops.model.Comment;
import com.miu.flowops.model.Discussion;
import com.miu.flowops.model.DiscussionStatus;
import com.miu.flowops.model.DiscussionType;
import com.miu.flowops.repository.DiscussionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final DiscussionRepository discussionRepository;

    @Bean
    @Profile("!test")
    CommandLineRunner initDiscussionData() {
        return args -> {
            if (discussionRepository.count() > 0) {
                log.info("Discussion data already exists, skipping initialization");
                return;
            }

            log.info("Initializing discussion test data...");

            List<Discussion> discussions = new ArrayList<>();

            // Discussion 1: Release Planning Discussion with nested comments
            Discussion discussion1 = createDiscussion(
                    "Release v2.5.0 Planning Discussion",
                    "We need to discuss the scope and timeline for the upcoming v2.5.0 release. Key features to consider:\n\n" +
                    "1. User authentication improvements\n" +
                    "2. Performance optimizations for the dashboard\n" +
                    "3. New reporting module\n\n" +
                    "Please share your thoughts on priorities and timeline.",
                    "release-001",
                    null,
                    "user-101",
                    "Sarah Chen",
                    DiscussionType.GENERAL,
                    DiscussionStatus.OPEN,
                    LocalDateTime.now().minusDays(5)
            );

            // Nested comments for Discussion 1
            Comment c1_1 = createComment("comment-1-1", "I think we should prioritize the authentication improvements. " +
                    "We've had several security audit findings that need to be addressed before the release.", 
                    "user-102", "Michael Johnson", null, 0, LocalDateTime.now().minusDays(5).plusHours(2));
            
            Comment c1_1_1 = createComment("comment-1-1-1", "Agreed. The OAuth2 implementation has been on the backlog for too long. " +
                    "I can take the lead on this if we prioritize it.", 
                    "user-103", "Emily Davis", "comment-1-1", 1, LocalDateTime.now().minusDays(5).plusHours(3));
            
            Comment c1_1_2 = createComment("comment-1-1-2", "@Emily that would be great! Can you estimate the effort needed?", 
                    "user-101", "Sarah Chen", "comment-1-1", 1, LocalDateTime.now().minusDays(5).plusHours(4));
            
            Comment c1_1_2_1 = createComment("comment-1-1-2-1", "Based on my initial analysis, I'd estimate 2-3 sprints for full implementation including testing.", 
                    "user-103", "Emily Davis", "comment-1-1-2", 2, LocalDateTime.now().minusDays(4).plusHours(1));
            
            c1_1_2.setReplies(List.of(c1_1_2_1));
            c1_1.setReplies(List.of(c1_1_1, c1_1_2));

            Comment c1_2 = createComment("comment-1-2", "The dashboard performance is critical for our enterprise clients. " +
                    "They've been complaining about slow load times with large datasets.", 
                    "user-104", "David Wilson", null, 0, LocalDateTime.now().minusDays(4).plusHours(5));
            
            Comment c1_2_1 = createComment("comment-1-2-1", "I've done some profiling and identified the main bottlenecks. " +
                    "Most issues are related to inefficient database queries.", 
                    "user-105", "Jessica Martinez", "comment-1-2", 1, LocalDateTime.now().minusDays(4).plusHours(6));
            
            Comment c1_2_1_1 = createComment("comment-1-2-1-1", "Can you share the profiling results? We should discuss this in the next standup.", 
                    "user-104", "David Wilson", "comment-1-2-1", 2, LocalDateTime.now().minusDays(4).plusHours(7));
            
            Comment c1_2_1_1_1 = createComment("comment-1-2-1-1-1", "Sure! I'll prepare a summary document and share it before tomorrow's standup.", 
                    "user-105", "Jessica Martinez", "comment-1-2-1-1", 3, LocalDateTime.now().minusDays(4).plusHours(8));
            
            c1_2_1_1.setReplies(List.of(c1_2_1_1_1));
            c1_2_1.setReplies(List.of(c1_2_1_1));
            c1_2.setReplies(List.of(c1_2_1));

            discussion1.setComments(List.of(c1_1, c1_2));
            discussions.add(discussion1);

            // Discussion 2: Bug Report Discussion
            Discussion discussion2 = createDiscussion(
                    "Critical: Payment Processing Failing for International Transactions",
                    "We're seeing a high failure rate for international payment transactions since last week's deployment.\n\n" +
                    "**Error details:**\n" +
                    "- Error code: CURRENCY_CONVERSION_FAILED\n" +
                    "- Affected currencies: EUR, GBP, JPY\n" +
                    "- Success rate dropped from 98% to 67%\n\n" +
                    "This needs immediate attention as it's impacting revenue.",
                    "release-001",
                    "task-042",
                    "user-106",
                    "Robert Brown",
                    DiscussionType.ISSUE,
                    DiscussionStatus.OPEN,
                    LocalDateTime.now().minusDays(2)
            );

            Comment c2_1 = createComment("comment-2-1", "I've looked at the logs and it seems related to the new currency conversion API. " +
                    "The third-party service is returning different response formats for some currencies.", 
                    "user-107", "Amanda Taylor", null, 0, LocalDateTime.now().minusDays(2).plusHours(1));
            
            Comment c2_1_1 = createComment("comment-2-1-1", "You're right! I just checked the API changelog and they made breaking changes on their end " +
                    "without proper versioning. We need to update our parser.", 
                    "user-108", "Christopher Lee", "comment-2-1", 1, LocalDateTime.now().minusDays(2).plusHours(2));
            
            Comment c2_1_1_1 = createComment("comment-2-1-1-1", "I've created a hotfix branch. Can someone review PR #1247?", 
                    "user-107", "Amanda Taylor", "comment-2-1-1", 2, LocalDateTime.now().minusDays(2).plusHours(4));
            
            Comment c2_1_1_1_1 = createComment("comment-2-1-1-1-1", "Reviewing now. Initial look seems good but we should add more test cases for edge cases.", 
                    "user-108", "Christopher Lee", "comment-2-1-1-1", 3, LocalDateTime.now().minusDays(2).plusHours(5));
            
            Comment c2_1_1_1_2 = createComment("comment-2-1-1-1-2", "I've added the test cases. Ready for final review.", 
                    "user-107", "Amanda Taylor", "comment-2-1-1-1", 3, LocalDateTime.now().minusDays(1).plusHours(2));
            
            c2_1_1_1.setReplies(List.of(c2_1_1_1_1, c2_1_1_1_2));
            c2_1_1.setReplies(List.of(c2_1_1_1));
            c2_1.setReplies(List.of(c2_1_1));

            Comment c2_2 = createComment("comment-2-2", "We should also notify our enterprise clients about this issue and the expected resolution time.", 
                    "user-101", "Sarah Chen", null, 0, LocalDateTime.now().minusDays(2).plusHours(3));
            
            Comment c2_2_1 = createComment("comment-2-2-1", "I'll draft a communication for the affected clients. @Robert can you provide the list of impacted accounts?", 
                    "user-109", "Michelle Garcia", "comment-2-2", 1, LocalDateTime.now().minusDays(2).plusHours(4));
            
            Comment c2_2_1_1 = createComment("comment-2-2-1-1", "Sent you the list via email. 47 accounts were affected.", 
                    "user-106", "Robert Brown", "comment-2-2-1", 2, LocalDateTime.now().minusDays(2).plusHours(5));
            
            c2_2_1.setReplies(List.of(c2_2_1_1));
            c2_2.setReplies(List.of(c2_2_1));

            discussion2.setComments(List.of(c2_1, c2_2));
            discussions.add(discussion2);

            // Discussion 3: Feature Request Discussion
            Discussion discussion3 = createDiscussion(
                    "Feature Request: Dark Mode Support",
                    "Multiple customers have requested dark mode support for the web application. This would improve:\n\n" +
                    "- User experience for late-night usage\n" +
                    "- Accessibility for users with light sensitivity\n" +
                    "- Battery life on OLED screens\n\n" +
                    "Should we prioritize this for the next release?",
                    "release-002",
                    null,
                    "user-110",
                    "Daniel Anderson",
                    DiscussionType.FEEDBACK,
                    DiscussionStatus.OPEN,
                    LocalDateTime.now().minusDays(7)
            );

            Comment c3_1 = createComment("comment-3-1", "This is a great idea! Our competitor just launched dark mode and customers are asking about it.", 
                    "user-111", "Jennifer White", null, 0, LocalDateTime.now().minusDays(7).plusHours(3));
            
            Comment c3_1_1 = createComment("comment-3-1-1", "I've done some research and we can use CSS custom properties to implement this efficiently. " +
                    "The main challenge will be ensuring all components look good in both modes.", 
                    "user-112", "Kevin Thompson", "comment-3-1", 1, LocalDateTime.now().minusDays(7).plusHours(5));
            
            Comment c3_1_1_1 = createComment("comment-3-1-1-1", "We should also consider user preferences persistence and system-level dark mode detection.", 
                    "user-113", "Lisa Robinson", "comment-3-1-1", 2, LocalDateTime.now().minusDays(6).plusHours(2));
            
            Comment c3_1_1_1_1 = createComment("comment-3-1-1-1-1", "Good point! The `prefers-color-scheme` media query can help with system detection.", 
                    "user-112", "Kevin Thompson", "comment-3-1-1-1", 3, LocalDateTime.now().minusDays(6).plusHours(4));
            
            c3_1_1_1.setReplies(List.of(c3_1_1_1_1));
            c3_1_1.setReplies(List.of(c3_1_1_1));
            c3_1.setReplies(List.of(c3_1_1));

            Comment c3_2 = createComment("comment-3-2", "From a design perspective, I can start working on the dark mode color palette this sprint.", 
                    "user-114", "Nicole Harris", null, 0, LocalDateTime.now().minusDays(6).plusHours(1));
            
            Comment c3_2_1 = createComment("comment-3-2-1", "That would be helpful! Please make sure to maintain proper contrast ratios for accessibility.", 
                    "user-115", "Steven Clark", "comment-3-2", 1, LocalDateTime.now().minusDays(6).plusHours(3));
            
            c3_2.setReplies(List.of(c3_2_1));

            Comment c3_3 = createComment("comment-3-3", "I suggest we create a feature flag so we can gradually roll this out and get feedback.", 
                    "user-102", "Michael Johnson", null, 0, LocalDateTime.now().minusDays(5).plusHours(2));
            
            discussion3.setComments(List.of(c3_1, c3_2, c3_3));
            discussions.add(discussion3);

            // Discussion 4: Architecture Question
            Discussion discussion4 = createDiscussion(
                    "Question: Microservices Communication Pattern",
                    "I'm working on the new notification service and need guidance on the communication pattern.\n\n" +
                    "**Options:**\n" +
                    "1. Synchronous REST calls\n" +
                    "2. Async messaging via Kafka\n" +
                    "3. Hybrid approach\n\n" +
                    "What's our preferred approach for this use case?",
                    null,
                    "task-089",
                    "user-116",
                    "Ryan Martinez",
                    DiscussionType.QUESTION,
                    DiscussionStatus.RESOLVED,
                    LocalDateTime.now().minusDays(10)
            );

            Comment c4_1 = createComment("comment-4-1", "For notifications, I'd recommend async messaging. Notifications don't need immediate consistency " +
                    "and we don't want to block the main transaction flow.", 
                    "user-117", "Patricia Lewis", null, 0, LocalDateTime.now().minusDays(10).plusHours(2));
            
            Comment c4_1_1 = createComment("comment-4-1-1", "Agreed. Plus with Kafka we get built-in retry mechanisms and message persistence.", 
                    "user-118", "Andrew Walker", "comment-4-1", 1, LocalDateTime.now().minusDays(10).plusHours(3));
            
            Comment c4_1_1_1 = createComment("comment-4-1-1-1", "What about cases where we need to verify the notification was sent (e.g., for compliance)?", 
                    "user-116", "Ryan Martinez", "comment-4-1-1", 2, LocalDateTime.now().minusDays(10).plusHours(4));
            
            Comment c4_1_1_1_1 = createComment("comment-4-1-1-1-1", "We can implement a delivery confirmation topic where the notification service publishes " +
                    "acknowledgments after successful delivery.", 
                    "user-117", "Patricia Lewis", "comment-4-1-1-1", 3, LocalDateTime.now().minusDays(10).plusHours(5));
            
            Comment c4_1_1_1_1_1 = createComment("comment-4-1-1-1-1-1", "That makes sense! I'll implement it with the confirmation pattern. Thanks everyone!", 
                    "user-116", "Ryan Martinez", "comment-4-1-1-1-1", 4, LocalDateTime.now().minusDays(9).plusHours(1));
            
            c4_1_1_1_1.setReplies(List.of(c4_1_1_1_1_1));
            c4_1_1_1.setReplies(List.of(c4_1_1_1_1));
            c4_1_1.setReplies(List.of(c4_1_1_1));
            c4_1.setReplies(List.of(c4_1_1));

            discussion4.setComments(List.of(c4_1));
            discussion4.setResolvedAt(LocalDateTime.now().minusDays(9).plusHours(1));
            discussion4.setResolvedBy("user-116");
            discussions.add(discussion4);

            // Discussion 5: Announcement
            Discussion discussion5 = createDiscussion(
                    "Announcement: Code Freeze for v2.4.0 Starting Monday",
                    "Team,\n\n" +
                    "This is a reminder that the code freeze for release v2.4.0 begins on Monday.\n\n" +
                    "**Key dates:**\n" +
                    "- Code freeze: Monday, Feb 3rd\n" +
                    "- QA testing: Feb 3rd - Feb 7th\n" +
                    "- Production deployment: Feb 10th\n\n" +
                    "Please ensure all your PRs are merged by EOD Friday. Only critical bug fixes will be allowed during the freeze period.",
                    "release-003",
                    null,
                    "user-101",
                    "Sarah Chen",
                    DiscussionType.ANNOUNCEMENT,
                    DiscussionStatus.OPEN,
                    LocalDateTime.now().minusDays(3)
            );

            Comment c5_1 = createComment("comment-5-1", "Thanks for the heads up! I have two PRs pending review - #1289 and #1291. Can someone help review?", 
                    "user-119", "James Scott", null, 0, LocalDateTime.now().minusDays(3).plusHours(2));
            
            Comment c5_1_1 = createComment("comment-5-1-1", "I can review #1289 today. @Michelle can you take #1291?", 
                    "user-104", "David Wilson", "comment-5-1", 1, LocalDateTime.now().minusDays(3).plusHours(3));
            
            Comment c5_1_1_1 = createComment("comment-5-1-1-1", "Sure, I'll review it this afternoon.", 
                    "user-109", "Michelle Garcia", "comment-5-1-1", 2, LocalDateTime.now().minusDays(3).plusHours(4));
            
            Comment c5_1_1_2 = createComment("comment-5-1-1-2", "Thanks both! Really appreciate the quick turnaround.", 
                    "user-119", "James Scott", "comment-5-1-1", 2, LocalDateTime.now().minusDays(3).plusHours(5));
            
            c5_1_1.setReplies(List.of(c5_1_1_1, c5_1_1_2));
            c5_1.setReplies(List.of(c5_1_1));

            Comment c5_2 = createComment("comment-5-2", "Will there be any database migrations in this release? Need to coordinate with the DBA team.", 
                    "user-120", "Elizabeth Young", null, 0, LocalDateTime.now().minusDays(3).plusHours(4));
            
            Comment c5_2_1 = createComment("comment-5-2-1", "Yes, we have 3 migration scripts. I'll share the details in the deployment runbook.", 
                    "user-101", "Sarah Chen", "comment-5-2", 1, LocalDateTime.now().minusDays(3).plusHours(5));
            
            c5_2.setReplies(List.of(c5_2_1));

            discussion5.setComments(List.of(c5_1, c5_2));
            discussions.add(discussion5);

            // Discussion 6: Deleted comment example
            Discussion discussion6 = createDiscussion(
                    "Database Migration Strategy for Q1",
                    "We need to plan the database schema changes for the upcoming features in Q1.\n\n" +
                    "Main changes needed:\n" +
                    "- New audit logging tables\n" +
                    "- User preferences schema update\n" +
                    "- Performance indexes for reporting queries",
                    "release-002",
                    "task-101",
                    "user-105",
                    "Jessica Martinez",
                    DiscussionType.GENERAL,
                    DiscussionStatus.OPEN,
                    LocalDateTime.now().minusDays(4)
            );

            Comment c6_1 = createComment("comment-6-1", "For audit logging, I suggest using a separate database to avoid impacting main DB performance.", 
                    "user-117", "Patricia Lewis", null, 0, LocalDateTime.now().minusDays(4).plusHours(3));
            
            Comment c6_1_1 = createComment("comment-6-1-1", "[deleted]", 
                    "[deleted]", "[deleted]", "comment-6-1", 1, LocalDateTime.now().minusDays(4).plusHours(4));
            c6_1_1.setIsDeleted(true);
            
            Comment c6_1_1_1 = createComment("comment-6-1-1-1", "That's a valid concern. We could use TimescaleDB for the audit logs - it's optimized for time-series data.", 
                    "user-118", "Andrew Walker", "comment-6-1-1", 2, LocalDateTime.now().minusDays(4).plusHours(6));
            
            Comment c6_1_1_1_1 = createComment("comment-6-1-1-1-1", "Good suggestion! I've used TimescaleDB before and it handles high-volume inserts very well.", 
                    "user-105", "Jessica Martinez", "comment-6-1-1-1", 3, LocalDateTime.now().minusDays(3).plusHours(1));
            
            c6_1_1_1.setReplies(List.of(c6_1_1_1_1));
            c6_1_1.setReplies(List.of(c6_1_1_1));
            c6_1.setReplies(List.of(c6_1_1));

            discussion6.setComments(List.of(c6_1));
            discussions.add(discussion6);

            // Save all discussions
            discussionRepository.saveAll(discussions);

            log.info("Successfully initialized {} discussions with nested comments", discussions.size());
        };
    }

    private Discussion createDiscussion(String title, String content, String releaseId, String taskId,
                                        String authorId, String authorName, DiscussionType type,
                                        DiscussionStatus status, LocalDateTime createdAt) {
        return Discussion.builder()
                .id(UUID.randomUUID().toString())
                .title(title)
                .content(content)
                .releaseId(releaseId)
                .taskId(taskId)
                .authorId(authorId)
                .authorName(authorName)
                .type(type)
                .status(status)
                .comments(new ArrayList<>())
                .createdAt(createdAt)
                .updatedAt(createdAt)
                .build();
    }

    private Comment createComment(String id, String content, String authorId, String authorName,
                                  String parentId, int depth, LocalDateTime createdAt) {
        return Comment.builder()
                .id(id)
                .content(content)
                .authorId(authorId)
                .authorName(authorName)
                .parentId(parentId)
                .replies(new ArrayList<>())
                .depth(depth)
                .createdAt(createdAt)
                .updatedAt(createdAt)
                .isEdited(false)
                .isDeleted(false)
                .build();
    }
}

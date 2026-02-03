// MongoDB Seed Data for OpsFlow
// This script runs automatically when the MongoDB container starts for the first time

db = db.getSiblingDB("flow_ops_db");

print("🌱 Starting OpsFlow database seeding...");

// ============================================
// USERS COLLECTION
// ============================================
print("📦 Seeding users collection...");

const users = [
  {
    _id: ObjectId("698264248e8e07db574f1001"),
    email: "admin@opsflow.com",
    username: "admin",
    fullName: "System Administrator",
    password: "$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrqI6v5y6F5F5b5F5F5F5F5F5F5F5F", // bcrypt hash
    verified: true,
    avatarUrl: "https://api.dicebear.com/7.x/avataaars/svg?seed=admin",
    roles: ["ADMIN"],
  },
  {
    _id: ObjectId("698264248e8e07db574f1002"),
    email: "john.doe@opsflow.com",
    username: "johndoe",
    fullName: "John Doe",
    password: "$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrqI6v5y6F5F5b5F5F5F5F5F5F5F5F",
    verified: true,
    avatarUrl: "https://api.dicebear.com/7.x/avataaars/svg?seed=john",
    roles: ["DEVELOPER"],
  },
  {
    _id: ObjectId("698264248e8e07db574f1003"),
    email: "jane.smith@opsflow.com",
    username: "janesmith",
    fullName: "Jane Smith",
    password: "$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrqI6v5y6F5F5b5F5F5F5F5F5F5F5F",
    verified: true,
    avatarUrl: "https://api.dicebear.com/7.x/avataaars/svg?seed=jane",
    roles: ["DEVELOPER"],
  },
  {
    _id: ObjectId("698264248e8e07db574f1004"),
    email: "bob.wilson@opsflow.com",
    username: "bobwilson",
    fullName: "Bob Wilson",
    password: "$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrqI6v5y6F5F5b5F5F5F5F5F5F5F5F",
    verified: true,
    avatarUrl: "https://api.dicebear.com/7.x/avataaars/svg?seed=bob",
    roles: ["DEVELOPER"],
  },
  {
    _id: ObjectId("698264248e8e07db574f1005"),
    email: "alice.johnson@opsflow.com",
    username: "alicejohnson",
    fullName: "Alice Johnson",
    password: "$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrqI6v5y6F5F5b5F5F5F5F5F5F5F5F",
    verified: true,
    avatarUrl: "https://api.dicebear.com/7.x/avataaars/svg?seed=alice",
    roles: ["ADMIN", "DEVELOPER"],
  },
];

db.users.insertMany(users);
print("✅ Inserted " + users.length + " users");

// ============================================
// RELEASES COLLECTION
// ============================================
print("📦 Seeding releases collection...");

const releases = [
  {
    _id: ObjectId("698264248e8e07db574f2001"),
    title: "OpsFlow v1.0.0 - Initial Release",
    isCompleted: true,
    tasks: [
      {
        _id: ObjectId("698264248e8e07db574f3001"),
        title: "Setup project structure",
        description:
          "Initialize Spring Boot microservices architecture with Maven multi-module setup",
        status: "COMPLETED",
        developerId: "698264248e8e07db574f1002",
        orderIndex: 1,
        startedAt: new Date("2026-01-05T09:00:00Z"),
        completedAt: new Date("2026-01-06T17:00:00Z"),
      },
      {
        _id: ObjectId("698264248e8e07db574f3002"),
        title: "Implement user authentication",
        description:
          "Create JWT-based authentication with login, register, and token refresh endpoints",
        status: "COMPLETED",
        developerId: "698264248e8e07db574f1003",
        orderIndex: 2,
        startedAt: new Date("2026-01-07T09:00:00Z"),
        completedAt: new Date("2026-01-10T17:00:00Z"),
      },
      {
        _id: ObjectId("698264248e8e07db574f3003"),
        title: "Create API Gateway",
        description:
          "Setup Spring Cloud Gateway with route configuration and security filters",
        status: "COMPLETED",
        developerId: "698264248e8e07db574f1002",
        orderIndex: 3,
        startedAt: new Date("2026-01-11T09:00:00Z"),
        completedAt: new Date("2026-01-13T17:00:00Z"),
      },
      {
        _id: ObjectId("698264248e8e07db574f3004"),
        title: "Setup MongoDB integration",
        description:
          "Configure MongoDB repositories and data models for all services",
        status: "COMPLETED",
        developerId: "698264248e8e07db574f1004",
        orderIndex: 4,
        startedAt: new Date("2026-01-08T09:00:00Z"),
        completedAt: new Date("2026-01-09T17:00:00Z"),
      },
    ],
  },
  {
    _id: ObjectId("698264248e8e07db574f2002"),
    title: "OpsFlow v1.1.0 - Release Management",
    isCompleted: true,
    tasks: [
      {
        _id: ObjectId("698264248e8e07db574f3005"),
        title: "Create Release CRUD endpoints",
        description:
          "Implement REST API for creating, reading, updating, and deleting releases",
        status: "COMPLETED",
        developerId: "698264248e8e07db574f1002",
        orderIndex: 1,
        startedAt: new Date("2026-01-15T09:00:00Z"),
        completedAt: new Date("2026-01-17T17:00:00Z"),
      },
      {
        _id: ObjectId("698264248e8e07db574f3006"),
        title: "Implement Task management",
        description:
          "Add task creation, assignment, and status update functionality within releases",
        status: "COMPLETED",
        developerId: "698264248e8e07db574f1003",
        orderIndex: 2,
        startedAt: new Date("2026-01-18T09:00:00Z"),
        completedAt: new Date("2026-01-21T17:00:00Z"),
      },
      {
        _id: ObjectId("698264248e8e07db574f3007"),
        title: "Add task reordering",
        description:
          "Implement drag-and-drop task reordering with order index persistence",
        status: "COMPLETED",
        developerId: "698264248e8e07db574f1004",
        orderIndex: 3,
        startedAt: new Date("2026-01-22T09:00:00Z"),
        completedAt: new Date("2026-01-23T17:00:00Z"),
      },
    ],
  },
  {
    _id: ObjectId("698264248e8e07db574f2003"),
    title: "OpsFlow v1.2.0 - Notifications & Events",
    isCompleted: false,
    tasks: [
      {
        _id: ObjectId("698264248e8e07db574f3008"),
        title: "Setup Kafka event streaming",
        description:
          "Configure Apache Kafka for inter-service communication and event publishing",
        status: "COMPLETED",
        developerId: "698264248e8e07db574f1002",
        orderIndex: 1,
        startedAt: new Date("2026-01-25T09:00:00Z"),
        completedAt: new Date("2026-01-27T17:00:00Z"),
      },
      {
        _id: ObjectId("698264248e8e07db574f3009"),
        title: "Create notification service",
        description:
          "Build notification microservice with email and in-app notification support",
        status: "IN_PROCESS",
        developerId: "698264248e8e07db574f1003",
        orderIndex: 2,
        startedAt: new Date("2026-01-28T09:00:00Z"),
        completedAt: null,
      },
      {
        _id: ObjectId("698264248e8e07db574f3010"),
        title: "Implement email templates",
        description:
          "Design and implement HTML email templates for various notification types",
        status: "TODO",
        developerId: "698264248e8e07db574f1004",
        orderIndex: 3,
        startedAt: null,
        completedAt: null,
      },
      {
        _id: ObjectId("698264248e8e07db574f3011"),
        title: "Add WebSocket support",
        description:
          "Implement real-time notifications using WebSocket connections",
        status: "TODO",
        developerId: "698264248e8e07db574f1005",
        orderIndex: 4,
        startedAt: null,
        completedAt: null,
      },
    ],
  },
  {
    _id: ObjectId("698264248e8e07db574f2004"),
    title: "OpsFlow v1.3.0 - AI Chatbot Integration",
    isCompleted: false,
    tasks: [
      {
        _id: ObjectId("698264248e8e07db574f3012"),
        title: "Setup Ollama integration",
        description:
          "Configure Ollama LLM service with llama3.2 model for AI-powered responses",
        status: "COMPLETED",
        developerId: "698264248e8e07db574f1005",
        orderIndex: 1,
        startedAt: new Date("2026-01-30T09:00:00Z"),
        completedAt: new Date("2026-02-01T17:00:00Z"),
      },
      {
        _id: ObjectId("698264248e8e07db574f3013"),
        title: "Create chat session management",
        description:
          "Implement chat session creation, history retrieval, and message persistence",
        status: "COMPLETED",
        developerId: "698264248e8e07db574f1002",
        orderIndex: 2,
        startedAt: new Date("2026-02-01T09:00:00Z"),
        completedAt: new Date("2026-02-02T17:00:00Z"),
      },
      {
        _id: ObjectId("698264248e8e07db574f3014"),
        title: "Implement context-aware responses",
        description:
          "Add conversation context to AI prompts for more coherent multi-turn conversations",
        status: "IN_PROCESS",
        developerId: "698264248e8e07db574f1003",
        orderIndex: 3,
        startedAt: new Date("2026-02-03T09:00:00Z"),
        completedAt: null,
      },
      {
        _id: ObjectId("698264248e8e07db574f3015"),
        title: "Add streaming responses",
        description:
          "Implement Server-Sent Events for streaming AI responses to the frontend",
        status: "TODO",
        developerId: "698264248e8e07db574f1004",
        orderIndex: 4,
        startedAt: null,
        completedAt: null,
      },
    ],
  },
  {
    _id: ObjectId("698264248e8e07db574f2005"),
    title: "OpsFlow v2.0.0 - Discussion Forum",
    isCompleted: false,
    tasks: [
      {
        _id: ObjectId("698264248e8e07db574f3016"),
        title: "Design forum data models",
        description:
          "Create MongoDB schemas for threads, posts, comments, and reactions",
        status: "TODO",
        developerId: "698264248e8e07db574f1002",
        orderIndex: 1,
        startedAt: null,
        completedAt: null,
      },
      {
        _id: ObjectId("698264248e8e07db574f3017"),
        title: "Implement thread management",
        description:
          "Build CRUD operations for discussion threads with category support",
        status: "TODO",
        developerId: "698264248e8e07db574f1003",
        orderIndex: 2,
        startedAt: null,
        completedAt: null,
      },
      {
        _id: ObjectId("698264248e8e07db574f3018"),
        title: "Add commenting system",
        description:
          "Implement nested comments with reply functionality and markdown support",
        status: "TODO",
        developerId: "698264248e8e07db574f1004",
        orderIndex: 3,
        startedAt: null,
        completedAt: null,
      },
      {
        _id: ObjectId("698264248e8e07db574f3019"),
        title: "Implement search functionality",
        description:
          "Add full-text search for threads and posts using MongoDB text indexes",
        status: "TODO",
        developerId: "698264248e8e07db574f1005",
        orderIndex: 4,
        startedAt: null,
        completedAt: null,
      },
      {
        _id: ObjectId("698264248e8e07db574f3020"),
        title: "Add moderation features",
        description:
          "Implement post flagging, admin moderation queue, and content policies",
        status: "TODO",
        developerId: "698264248e8e07db574f1001",
        orderIndex: 5,
        startedAt: null,
        completedAt: null,
      },
    ],
  },
];

db.releases.insertMany(releases);
print("✅ Inserted " + releases.length + " releases");

// ============================================
// CREATE INDEXES
// ============================================
print("📦 Creating indexes...");

db.users.createIndex({ email: 1 }, { unique: true });
db.users.createIndex({ username: 1 }, { unique: true });
db.releases.createIndex({ title: 1 });
db.releases.createIndex({ isCompleted: 1 });
db.releases.createIndex({ "tasks.developerId": 1 });
db.releases.createIndex({ "tasks.status": 1 });

print("✅ Indexes created");

// ============================================
// SUMMARY
// ============================================
print("");
print("🎉 OpsFlow database seeding completed!");
print("📊 Summary:");
print("   - Users: " + db.users.countDocuments());
print("   - Releases: " + db.releases.countDocuments());
print("");

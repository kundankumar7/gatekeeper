# Gatekeeper

A configurable API gateway and API management platform built around three independent Spring Boot services.

> **Status: initial scaffolding.** The architecture has been defined; product features are pending implementation. The current code contains application entry points, basic Spring Security configuration, `/health` endpoints, and application-context test skeletons.

## Project structure

```text
gatekeeper/
├── smart-gateway-service/       # Runtime API traffic
├── api-management-service/      # Management APIs and configuration
├── analytics-service/           # Asynchronous request analytics
├── .gitignore
└── README.md
```

Each service has its own `pom.xml`, Maven wrapper, `src/main/java`, `src/main/resources`, and `src/test/java`. There is no root Maven aggregator. Build and run each service separately.

The React Management Portal is planned and has not been scaffolded yet.

## Planned architecture

| Component | Responsibility | Owned datastore |
| --- | --- | --- |
| Smart Gateway Service | Routing, runtime JWT validation, authorization, rate limiting, and idempotency | Redis |
| API Management Service | Tenants, management identity, routes, policies, and versioned configuration | MySQL |
| Analytics Service | Consume request events and expose usage analytics | MongoDB |
| React Management Portal | Administration and dashboards | Access through backend APIs |

Kafka provides shared messaging for configuration notifications and asynchronous request analytics.

The architecture baseline requires:

- Runtime requests use locally compiled gateway configuration. They do not synchronously depend on API Management, Analytics, MySQL, or MongoDB.
- Configuration changes use a transactional outbox, versioned notifications, and atomic snapshot replacement. Gateways retain their last-known-good configuration when refresh fails.
- Management identity and customer runtime identity have separate trust boundaries.
- Analytics processing remains asynchronous and must not block runtime traffic.
- Redis failure causes rate limiting to fail open with emergency local protection; protected idempotent mutations fail closed.
- Each service accesses its own datastore.

These describe the target architecture, not completed functionality. The detailed architecture package is maintained separately as `Gatekeeper_Architecture_v1_FINAL/Gatekeeper_Architecture`; its Architecture Decision Blueprint is the primary authority.

## Technology baseline

- Java 21
- Spring Boot 4.1.1, as declared in the service POMs
- Maven wrappers (Maven 3.9.16)
- Spring MVC, Spring Security, validation, and Lombok
- Planned integrations: MySQL, Redis, MongoDB, Kafka, and a React frontend

Dependencies are declared in the service POMs; their presence does not mean the corresponding integrations are implemented.

## Local development

Install JDK 21 and set `JAVA_HOME`. The Maven wrappers download Maven and dependencies when needed.

Run these commands from a service directory, for example:

```powershell
cd smart-gateway-service
.\mvnw.cmd verify
.\mvnw.cmd spring-boot:run
```

On macOS/Linux, use `sh ./mvnw verify` and `sh ./mvnw spring-boot:run`.

To run all three services, start each in its own terminal.

| Service directory | Configured port | Health path |
| --- | --- | --- |
| `smart-gateway-service` | 8080 | `GET /health` |
| `api-management-service` | 8081 | `GET /health` |
| `analytics-service` | 8082 | `GET /health` |

The health endpoints report a basic application response; they do not verify infrastructure readiness. Database and messaging configuration is incomplete, and API Management currently excludes datasource auto-configuration. There is no Docker Compose setup or deployment automation yet.

Run `verify` separately in each service directory. Existing tests only load the application context; feature and integration coverage will be added alongside implementation.

## Branching and contribution workflow

```text
feature/<short-description> → develop → main
```

| Branch | Purpose |
| --- | --- |
| `main` | Stable, tested release baseline; initially the project scaffold |
| `develop` | Integration branch for upcoming work |
| `feature/<short-description>` | One focused feature, created from `develop` |
| `fix/<short-description>` | One focused fix, created from `develop` |
| `docs/<short-description>` | Documentation changes, created from `develop` |

1. Update `develop` and create a working branch:

   ```shell
   git switch develop
   git pull --ff-only origin develop
   git switch -c feature/route-management
   ```

2. Implement the change and run relevant builds and tests.
3. Commit and push the working branch, then open a pull request into `develop`.
4. Review and merge the pull request after its checks pass.
5. Test the integrated application on `develop`.
6. Open a release pull request from `develop` into `main` after integration testing passes.

Use a merge commit for `develop` → `main` releases so both long-lived branches retain shared history. Keep completed working branches short-lived and delete them after merging.

Use pull requests for ongoing changes to `main` and `develop`. The initial repository setup creates both branches from the same scaffold commit. Branch protection and automated CI are separate GitHub settings; documenting this workflow does not enable them.

## Implementation status

- [x] Three independent Spring Boot service skeletons
- [x] Basic health endpoints and security configuration
- [ ] Management authentication, tenancy, and configuration APIs
- [ ] Dynamic routing and runtime JWT validation
- [ ] Redis rate limiting and idempotency
- [ ] Versioned configuration snapshots and outbox publication
- [ ] Kafka event processing and MongoDB analytics
- [ ] React Management Portal
- [ ] Integration tests, CI, and deployment tooling

# Architecture

## System View

```mermaid
flowchart LR
  Browser["React bilingual FTMS UI"] --> Nginx["Nginx reverse proxy"]
  Nginx --> API["Spring Boot API"]
  API --> Postgres["PostgreSQL transactional schema"]
  API --> Mongo["MongoDB logs, notifications, analytics, documents"]
  API --> Redis["Redis cache/session-ready store"]
  API --> Rabbit["RabbitMQ domain events"]
  API --> MockPorts["Mock integration ports"]
  MockPorts --> Treasury["Treasury IFMS"]
  MockPorts --> Banks["Banking APIs"]
  MockPorts --> Aadhaar["Aadhaar/eKYC"]
  MockPorts --> DigiLocker["DigiLocker/eSign/SSO/SMS/Email"]
```

## Transaction Lifecycle

```mermaid
sequenceDiagram
  participant Maker as Finance Officer/Data Entry
  participant API as Spring Boot API
  participant DB as PostgreSQL
  participant Approver as Approver
  participant Bank as Mock Banking Port
  Maker->>API: Create transaction
  API->>DB: Validate beneficiary, duplicate invoice, budget availability
  API->>DB: Save PENDING_APPROVAL transaction
  API->>Approver: Queue approval task
  Approver->>API: Approve or reject
  API->>DB: Update approval decision
  API->>DB: Update transaction status and budget utilization
  API->>Bank: Publish/mock bank or treasury event
```

## Core ERD

```mermaid
erDiagram
  ROLES ||--o{ USERS : grants
  ROLES ||--o{ ROLE_PERMISSIONS : contains
  DEPARTMENTS ||--o{ USERS : owns
  DEPARTMENTS ||--o{ BUDGETS : allocates
  BUDGETS ||--o{ FINANCIAL_TRANSACTIONS : funds
  BENEFICIARIES ||--o{ FINANCIAL_TRANSACTIONS : receives
  USERS ||--o{ FINANCIAL_TRANSACTIONS : creates
  FINANCIAL_TRANSACTIONS ||--o{ APPROVALS : requires
  USERS ||--o{ APPROVALS : decides
  FINANCIAL_TRANSACTIONS ||--o{ RECONCILIATION_RECORDS : reconciles
  AUDIT_TRAILS }o--|| USERS : actor
```

## Clean Architecture Shape

- `domain`: JPA and Mongo domain models
- `repository`: Spring Data persistence interfaces
- `service`: business rules, audit, reports, auth
- `controller`: REST API layer
- `security`: JWT and Spring Security configuration
- `integration`: mock implementations for external government systems
- `events`: in-memory and RabbitMQ domain event publishers


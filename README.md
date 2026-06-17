# MP Government FTMS

Production-grade demo Financial Transaction Management System for the Government of Madhya Pradesh.

## Stack

- Frontend: React 19, TypeScript, Vite, Material UI, Redux Toolkit, React Query, React Hook Form, Zod, Framer Motion, i18next, Recharts, MUI Data Grid
- Backend: Java 21, Spring Boot 3, Spring Security, JWT, JPA/Hibernate, Swagger/OpenAPI
- Data: PostgreSQL for transactional data, MongoDB for logs/notifications/analytics/document metadata, Redis-ready caching, RabbitMQ event publishing
- Deployment: Docker, Docker Compose, Nginx

## Quick Start

```bash
cd DemoBelProject
docker compose up --build
```

Open:

- Web app: http://localhost:8081
- Backend API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- RabbitMQ console: http://localhost:15672

## Demo Login

All demo users use password `Admin@123`.

| Role | Email |
| --- | --- |
| Super Admin | `super.admin@mp.gov.in` |
| State Admin | `state.admin@mp.gov.in` |
| Department Admin | `dept.admin@mp.gov.in` |
| Finance Officer | `finance.officer@mp.gov.in` |
| Approver | `approver@mp.gov.in` |
| Auditor | `auditor@mp.gov.in` |
| Data Entry Operator | `data.entry@mp.gov.in` |
| Read Only | `readonly@mp.gov.in` |

## Local Development

Backend needs Java 21 and PostgreSQL. Docker is the easiest path because it also starts MongoDB, Redis and RabbitMQ.

```bash
cd DemoBelProject/backend
mvn spring-boot:run
```

Frontend:

```bash
cd DemoBelProject/frontend
npm install
npm run dev
```

Vite proxies `/api` to `http://localhost:8080`.

## Implemented Modules

- Secure login, JWT access and refresh token flow
- RBAC roles: Super Admin, State Admin, Department Admin, Finance Officer, Approver, Auditor, Data Entry Operator, Read Only
- Bilingual English/Hindi UI with persistent preference
- Dark/light mode
- Dashboard analytics, charts and recent transactions
- Beneficiary management with validation
- Budget listing and utilization tracking
- Transaction creation with duplicate detection and fund availability checks
- Maker-checker approval workflow
- Reconciliation records
- Audit trail
- Mock integration status for treasury, banking, Aadhaar/eKYC, eSign, DigiLocker, SMS, email and SSO
- PDF, Excel and CSV transaction reports
- Swagger/OpenAPI documentation

## Repository Structure

```text
DemoBelProject/
  backend/                 Spring Boot service
  frontend/                React/Vite application
  deployment/nginx/        Edge reverse proxy config
  docs/                    Architecture, security and API docs
  docker-compose.yml       Full local stack
```

More detail is in `docs/ARCHITECTURE.md`, `docs/API.md`, `docs/SECURITY.md` and `docs/DEPLOYMENT.md`.


# API Guide

Swagger is available at `/swagger-ui.html`.

## Authentication

- `POST /api/auth/login`
- `POST /api/auth/refresh`
- `GET /api/auth/me`
- `POST /api/auth/password-reset/request`

## Core APIs

- `GET /api/dashboard/summary`
- `GET /api/beneficiaries?q=&page=&size=`
- `POST /api/beneficiaries`
- `GET /api/budgets?q=&page=&size=`
- `GET /api/transactions?q=&status=&page=&size=`
- `POST /api/transactions`
- `GET /api/approvals/pending`
- `PATCH /api/approvals/{approvalId}/decision`
- `GET /api/reconciliation`
- `GET /api/audit`
- `GET /api/integrations/status`

## Reports

- `GET /api/reports/transactions.csv`
- `GET /api/reports/transactions.xlsx`
- `GET /api/reports/transactions.pdf`

All protected APIs require:

```http
Authorization: Bearer <access-token>
```

## Pagination and Filtering

List endpoints use `page`, `size` and `q` query parameters. Transactions also accept `status`.


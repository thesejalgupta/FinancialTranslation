# Deployment Guide

## Local Docker Deployment

```bash
docker compose up --build
```

The edge URL is `http://localhost:8081`.

## Environment Variables

Backend:

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `MONGO_URI`
- `REDIS_HOST`
- `SPRING_CACHE_TYPE`
- `RABBITMQ_HOST`
- `JWT_SECRET`
- `CORS_ORIGINS`
- `DEMO_SEED_ENABLED`
- `FLYWAY_ENABLED`

Frontend:

- `VITE_API_BASE_URL`; leave empty when served behind Nginx so `/api` resolves to the same origin.

## Production Notes

- Run PostgreSQL with backups, PITR and encrypted storage.
- Use MongoDB for notification/log/analytics/document metadata collections.
- Use Redis for distributed caching and session-ready token state.
- Use RabbitMQ/Kafka for durable domain events.
- Enable `FLYWAY_ENABLED=true` after reviewing migrations.
- Place Nginx or an API gateway behind TLS.


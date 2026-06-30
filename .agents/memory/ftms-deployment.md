---
name: FTMS deployment
description: Full-stack deployment strategy for the MP FTMS Spring Boot + React app.
---

# FTMS Deployment

## Rule
Build script `build.sh` handles the full pipeline. Deployment is autoscale with the Spring Boot JAR serving both API and frontend static files.

**Why:** Replit only allows one deployment config per repl. The React frontend is built and copied into `backend/src/main/resources/static/` before the backend JAR is built. Spring Boot serves the SPA with a fallback controller for client-side routing.

**How to apply:**
- Build: `bash build.sh` → builds frontend → copies to backend static → builds JAR
- Run: `java -jar backend/target/ftms-backend-1.0.0.jar --server.port=5000`
- SPA fallback: `SpaFallbackController.java` handles all non-API routes → `index.html`
- MongoDB entities converted to JPA with `JsonMapConverter` / `JsonStringMapConverter` for Map fields

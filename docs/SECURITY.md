# Security Notes

Implemented:

- Stateless JWT access tokens and refresh tokens
- BCrypt password hashing
- Account lockout after repeated failed login attempts
- Spring Security RBAC with method-level authorization
- Secure headers in Spring Security and Nginx
- CORS limited to local frontend origins by environment
- Validation with Jakarta Bean Validation and Zod on the frontend
- SQL injection resistance through JPA repositories/specifications
- XSS risk reduction through React escaping and CSP
- Audit logging for auth, transaction and approval actions
- MFA-ready user model with `mfaEnabled`
- Mock password reset request flow for notification integration

Production hardening checklist:

- Replace demo JWT secret and database passwords
- Enable HTTPS/TLS termination
- Configure real government SSO and MFA provider
- Move refresh tokens to a persistent revocation/session table
- Add rate limiting at Nginx/API gateway
- Add centralized logging/SIEM export
- Run dependency and container image scanning in CI


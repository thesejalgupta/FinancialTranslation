---
name: FTMS database setup
description: Why MariaDB/MySQL server can't run in Replit sandbox and what we use instead.
---

# FTMS Database Setup

## Rule
Use H2 in MySQL compatibility mode as the default database. MySQL connector-j is also present for real MySQL connections via env var.

**Why:** The Replit sandbox blocks certain syscalls (`openat` on `/proc/<pid>/fd/-1`) that MariaDB's initialization scripts (`mariadb-install-db`, `mysql_install_db`) require. Starting `mariadbd` server process also fails with "run_parent" sandbox errors.

**How to apply:**
- Default DB_URL uses H2 MySQL mode: `jdbc:h2:file:./data/mp_ftms;MODE=MySQL;...`
- To use a real MySQL: set `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `DB_DRIVER=com.mysql.cj.jdbc.Driver` env vars
- H2 console available at `/h2-console`

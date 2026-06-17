create extension if not exists "pgcrypto";

create table if not exists roles (
    id uuid primary key,
    name varchar(80) not null unique,
    description varchar(160) not null
);

create table if not exists role_permissions (
    role_id uuid not null references roles(id) on delete cascade,
    permission varchar(120) not null,
    primary key (role_id, permission)
);

create table if not exists departments (
    id uuid primary key,
    code varchar(30) not null unique,
    name_en varchar(180) not null,
    name_hi varchar(180) not null,
    district varchar(80) not null,
    active boolean not null
);

create table if not exists users (
    id uuid primary key,
    email varchar(160) not null unique,
    full_name varchar(160) not null,
    password_hash varchar(255) not null,
    designation varchar(120) not null,
    role_id uuid not null references roles(id),
    department_id uuid references departments(id),
    enabled boolean not null,
    locked boolean not null,
    failed_attempts integer not null,
    mfa_enabled boolean not null,
    last_login_at timestamp with time zone
);

create table if not exists budgets (
    id uuid primary key,
    department_id uuid not null references departments(id),
    fiscal_year varchar(40) not null,
    scheme_code varchar(40) not null,
    scheme_name_en varchar(220) not null,
    scheme_name_hi varchar(220) not null,
    allocated_amount numeric(18,2) not null,
    utilized_amount numeric(18,2) not null,
    status varchar(40) not null
);

create table if not exists beneficiaries (
    id uuid primary key,
    beneficiary_code varchar(40) not null unique,
    name_en varchar(180) not null,
    name_hi varchar(180) not null,
    aadhaar_masked varchar(20) not null,
    mobile_number varchar(20) not null,
    upi_id varchar(120) not null,
    bank_name varchar(120) not null,
    ifsc_code varchar(20) not null,
    account_masked varchar(40) not null,
    district varchar(80) not null,
    status varchar(40) not null
);

create table if not exists financial_transactions (
    id uuid primary key,
    transaction_no varchar(60) not null unique,
    type varchar(40) not null,
    status varchar(40) not null,
    amount numeric(18,2) not null,
    channel varchar(80) not null,
    upi_id varchar(120) not null,
    bank_reference varchar(120),
    invoice_no varchar(80) not null,
    narrative varchar(500) not null,
    budget_id uuid not null references budgets(id),
    beneficiary_id uuid not null references beneficiaries(id),
    created_by uuid not null references users(id),
    created_at timestamp with time zone not null,
    updated_at timestamp with time zone not null,
    approved_at timestamp with time zone,
    checksum varchar(128) not null
);

create table if not exists approvals (
    id uuid primary key,
    transaction_id uuid not null references financial_transactions(id),
    approver_id uuid not null references users(id),
    approval_level integer not null,
    decision varchar(40) not null,
    remarks varchar(500),
    decided_at timestamp with time zone
);

create table if not exists reconciliation_records (
    id uuid primary key,
    transaction_id uuid not null references financial_transactions(id),
    bank_name varchar(120) not null,
    settlement_date date not null,
    bank_reference varchar(120) not null,
    amount numeric(18,2) not null,
    difference_amount numeric(18,2) not null,
    status varchar(40) not null
);

create table if not exists audit_trails (
    id uuid primary key,
    actor_email varchar(160) not null,
    action varchar(120) not null,
    entity_name varchar(120) not null,
    entity_id varchar(80) not null,
    ip_address varchar(80) not null,
    details varchar(1200) not null,
    created_at timestamp with time zone not null
);

create index if not exists idx_transactions_status on financial_transactions(status);
create index if not exists idx_transactions_invoice on financial_transactions(invoice_no);
create index if not exists idx_audit_actor_created on audit_trails(actor_email, created_at desc);
create index if not exists idx_budgets_department on budgets(department_id);


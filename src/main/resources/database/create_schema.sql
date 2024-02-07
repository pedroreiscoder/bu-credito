CREATE SCHEMA IF NOT EXISTS bu_credit_schema;

CREATE TABLE IF NOT EXISTS bu_credit_schema.status (
    id INTEGER PRIMARY KEY,
    description VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS bu_credit_schema.debts (
    id BIGSERIAL PRIMARY KEY,
    creditor_name VARCHAR(100) NOT NULL,
    total_value DECIMAL(9, 2) NOT NULL,
    number_of_installments INTEGER NOT NULL,
    due_date DATE NOT NULL,
    balance_due DECIMAL(9, 2) NOT NULL,
    status_id INTEGER REFERENCES bu_credit_schema.status(id),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS bu_credit_schema.installments(
    id BIGSERIAL PRIMARY KEY,
    value DECIMAL(9, 2) NOT NULL,
    interest_rate DECIMAL(3, 2) NOT NULL,
    debt_id BIGINT REFERENCES bu_credit_schema.debts(id),
    created_at TIMESTAMP NOT NULL
);

DO $$
BEGIN
   IF NOT EXISTS (
      SELECT FROM pg_database WHERE datname = 'app'
   ) THEN
      PERFORM dblink_exec('dbname=postgres', 'CREATE DATABASE app WITH ENCODING = ''UTF8'' LC_COLLATE = ''en_US.utf8'' LC_CTYPE = ''en_US.utf8'' TEMPLATE template0');
END IF;
END
$$;


CREATE TABLE IF NOT EXISTS payment_entity (
                                              id BYTEA NOT NULL,
                                              status VARCHAR(255) DEFAULT NULL,
    amount BIGINT DEFAULT NULL,
    created TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    transaction_id VARCHAR(255) DEFAULT NULL,
    PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS credit_request_entity (
                                                     id BYTEA NOT NULL,
                                                     status VARCHAR(255) DEFAULT NULL,
    bank_id VARCHAR(255) DEFAULT NULL,
    created TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
    );
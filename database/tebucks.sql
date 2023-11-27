BEGIN TRANSACTION;

DROP TABLE IF EXISTS users, transfer, account CASCADE;

CREATE TABLE users (
	user_id serial NOT NULL,
	username VARCHAR(50) NOT NULL,
	password_hash VARCHAR(200) NOT NULL,
	first_name VARCHAR(50),
    last_name VARCHAR(50),
    email VARCHAR(50),
	role VARCHAR(20),
	CONSTRAINT pk_users PRIMARY KEY (user_id),
	CONSTRAINT uq_username UNIQUE (username)
);

CREATE TABLE account(
    account_id serial,
    user_id INT,
    balance FLOAT,

    CONSTRAINT pk_account PRIMARY KEY(account_id),
    CONSTRAINT fk_account_user_id FOREIGN KEY (user_id) REFERENCES users (user_id)
);

CREATE TABLE transfer (
    transfer_id serial,
    transfer_type VARCHAR (20),
    user_from INT,
    user_to INT,
    amount FLOAT,
    transfer_status VARCHAR (20) default ('Pending'),

    CONSTRAINT pk_transfer PRIMARY KEY (transfer_id),
    CONSTRAINT fk_user_to FOREIGN KEY (user_to) REFERENCES users (user_id),
    CONSTRAINT fk_user_from FOREIGN KEY (user_from) REFERENCES users (user_id),
    CONSTRAINT chk_amount_is_positive CHECK (amount > 0),
    CONSTRAINT chk_transfer_type CHECK (transfer_type in ('Send', 'Request')),
    CONSTRAINT chk_transfer_status CHECK (transfer_status in ('Pending', 'Approved', 'Rejected'))
);

COMMIT TRANSACTION;

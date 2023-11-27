BEGIN TRANSACTION;

DROP TABLE IF EXISTS users, account, transfer CASCADE;
CREATE TABLE users (
	user_id serial NOT NULL,
	username varchar(50) NOT NULL,
	password_hash varchar(200) NOT NULL,
	first_name VARCHAR(50),
    last_name VARCHAR(50),
    email VARCHAR(50),
	role varchar(20),
	CONSTRAINT pk_users PRIMARY KEY (user_id),
	CONSTRAINT uq_username UNIQUE (username)
);

CREATE TABLE account (
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

INSERT INTO users (username,password_hash,role) VALUES ('user1','user1','ROLE_USER'); -- 1
INSERT INTO users (username,password_hash,role) VALUES ('user2','user2','ROLE_USER'); -- 2
INSERT INTO users (username,password_hash,role) VALUES ('user3','user3','ROLE_USER'); -- 3
INSERT INTO users (username,password_hash,role) VALUES ('user4','user4','ROLE_USER'); -- 4

COMMIT TRANSACTION;

INSERT INTO account (user_id, balance) VALUES (1, 1000); -- 1
INSERT INTO account (user_id, balance) VALUES (2, 2000); -- 2
INSERT INTO account (user_id, balance) VALUES (3, 3000); -- 3

COMMIT TRANSACTION;

INSERT INTO transfer (transfer_type, user_to, user_from, amount, transfer_status)
    VALUES ('Send', 1, 2, 200, 'Approved'); -- 1
INSERT INTO transfer (transfer_type, user_to, user_from, amount, transfer_status)
    VALUES ('Request', 2, 3, 400, 'Pending'); -- 2
INSERT INTO transfer (transfer_type, user_to, user_from, amount, transfer_status)
    VALUES ('Request', 3, 1, 600, 'Approved'); -- 3

COMMIT TRANSACTION;
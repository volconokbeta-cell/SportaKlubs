-- SportaKlubs: Apache Derby datu bāzes sportaklubsDB struktūra
-- Savienojums (Embedded): jdbc:derby:sportaklubsDB;create=true
-- Tabulas automātiski izveido klase DBConnection pirmajā palaišanas reizē.

CREATE TABLE USERS (
    id            INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username      VARCHAR(30) NOT NULL UNIQUE,
    password_hash VARCHAR(64) NOT NULL,
    role          VARCHAR(10) NOT NULL CHECK (role IN ('ADMIN', 'CLIENT'))
);

CREATE TABLE CLIENTS (
    id         INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id    INT REFERENCES USERS(id) ON DELETE SET NULL,
    first_name VARCHAR(50)  NOT NULL,
    last_name  VARCHAR(50)  NOT NULL,
    phone      VARCHAR(15)  NOT NULL,
    email      VARCHAR(100) NOT NULL UNIQUE,
    birth_date DATE
);

CREATE TABLE TRAINERS (
    id             INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    first_name     VARCHAR(50) NOT NULL,
    last_name      VARCHAR(50) NOT NULL,
    specialization VARCHAR(60) NOT NULL,
    phone          VARCHAR(15)
);

CREATE TABLE MEMBERSHIPS (
    id         INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    client_id  INT NOT NULL REFERENCES CLIENTS(id) ON DELETE CASCADE,
    type       VARCHAR(20)  NOT NULL,
    start_date DATE         NOT NULL,
    end_date   DATE         NOT NULL,
    price      DECIMAL(7,2) NOT NULL CHECK (price > 0),
    status     VARCHAR(10)  DEFAULT 'AKTĪVS' NOT NULL,
    CHECK (end_date >= start_date)
);

CREATE TABLE TRAININGS (
    id               INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name             VARCHAR(60) NOT NULL,
    trainer_id       INT NOT NULL REFERENCES TRAINERS(id),
    start_time       TIMESTAMP   NOT NULL,
    duration_min     INT NOT NULL CHECK (duration_min > 0),
    hall             VARCHAR(20) NOT NULL,
    max_participants INT NOT NULL CHECK (max_participants > 0)
);

CREATE TABLE BOOKINGS (
    id          INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    client_id   INT NOT NULL REFERENCES CLIENTS(id) ON DELETE CASCADE,
    training_id INT NOT NULL REFERENCES TRAININGS(id) ON DELETE CASCADE,
    booked_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    UNIQUE (client_id, training_id)
);

CREATE TABLE PAYMENTS (
    id            INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    client_id     INT NOT NULL REFERENCES CLIENTS(id) ON DELETE CASCADE,
    membership_id INT NOT NULL REFERENCES MEMBERSHIPS(id) ON DELETE CASCADE,
    amount        DECIMAL(7,2) NOT NULL CHECK (amount > 0),
    pay_date      DATE        NOT NULL,
    method        VARCHAR(20) NOT NULL
);

-- Administratora konts (parole admin123, glabāta SHA-256 jaucējkoda veidā)
INSERT INTO USERS (username, password_hash, role) VALUES
('admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'ADMIN');

-- Piemēra vaicājums: klienti ar aktīvu abonementu šodien
-- SELECT c.* FROM CLIENTS c WHERE EXISTS (SELECT 1 FROM MEMBERSHIPS m
--     WHERE m.client_id = c.id AND m.status = 'AKTĪVS'
--     AND CURRENT_DATE BETWEEN m.start_date AND m.end_date);

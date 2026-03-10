CREATE TABLE IF NOT EXISTS tasks (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    title      VARCHAR(255) NOT NULL,
    active     BOOLEAN      NOT NULL,
    created_at TIMESTAMP    NOT NULL
);

CREATE TABLE IF NOT EXISTS trackings (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    title      VARCHAR(255) NOT NULL,
    start_date DATE,
    end_date   DATE,
    created_at TIMESTAMP    NOT NULL
);

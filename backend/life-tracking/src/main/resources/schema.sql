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

CREATE TABLE IF NOT EXISTS tracking_tasks (
    tracking_id BIGINT NOT NULL,
    task_id     BIGINT NOT NULL,
    PRIMARY KEY (tracking_id, task_id),
    FOREIGN KEY (tracking_id) REFERENCES trackings (id) ON DELETE CASCADE,
    FOREIGN KEY (task_id) REFERENCES tasks (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS task_statuses (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    tracking_id BIGINT       NOT NULL,
    task_id     BIGINT       NOT NULL,
    status      VARCHAR(50)  NOT NULL,
    date        DATE         NOT NULL,
    FOREIGN KEY (tracking_id, task_id) REFERENCES tracking_tasks (tracking_id, task_id) ON DELETE CASCADE,
    UNIQUE (tracking_id, task_id, date)
);

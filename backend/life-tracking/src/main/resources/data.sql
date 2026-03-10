INSERT INTO tasks (title, active, created_at) VALUES
    ('Утренняя пробежка',   TRUE,  NOW()),
    ('Чтение книги',        TRUE,  NOW()),
    ('Медитация',           FALSE, NOW()),
    ('Занятие в спортзале', TRUE,  NOW()),
    ('Изучение английского',FALSE, NOW());

INSERT INTO trackings (title, start_date, end_date, created_at) VALUES
    ('Марафонская подготовка', '2026-04-01', '2026-06-30', NOW()),
    ('Курс по Java',           '2026-03-01', '2026-05-31', NOW()),
    ('Ежедневная медитация',   NULL,         NULL,         NOW()),
    ('Похудение к лету',       '2026-03-10', '2026-06-01', NOW()),
    ('Чтение 12 книг за год',  NULL,         NULL,         NOW());

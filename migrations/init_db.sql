CREATE TABLE IF NOT EXISTS student
(
    student_id INTEGER NOT NULL,
    role       VARCHAR(30),

    UNIQUE (student_id),
    PRIMARY KEY (student_id)
);

CREATE TABLE IF NOT EXISTS employee
(
    employee_id   BIGINT GENERATED ALWAYS AS IDENTITY,
    employee_name VARCHAR(255) NOT NULL,
    description   TEXT         NOT NULL,
    phone         VARCHAR(255) NOT NULL,
    email         VARCHAR(255) NOT NULL,

    PRIMARY KEY (employee_id)
);
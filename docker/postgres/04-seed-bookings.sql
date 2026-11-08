\c bookings_db;

CREATE TABLE bookings (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL,
    teacher_id BIGINT NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL
);
CREATE UNIQUE INDEX uq_bookings_teacher_slot
    ON bookings (teacher_id, start_time)
    WHERE status IN ('PENDING', 'CONFIRMED');

CREATE UNIQUE INDEX uq_bookings_student_slot
    ON bookings (student_id, start_time)
    WHERE status IN ('PENDING', 'CONFIRMED');

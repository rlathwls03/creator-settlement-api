INSERT INTO creators (id, name) VALUES
       ('creator-1', '김강사'),
       ('creator-2', '이강사'),
       ('creator-3', '박강사');

INSERT INTO courses (id, creator_id, title) VALUES
       ('course-1', 'creator-1', 'Spring Boot 입문'),
       ('course-2', 'creator-1', 'JPA 실전'),
       ('course-3', 'creator-2', 'Kotlin 기초'),
       ('course-4', 'creator-3', 'MSA 설계');

INSERT INTO sale_records (id, course_id, student_id, amount, paid_at) VALUES
         ('sale-1', 'course-1', 'student-1', 50000, '2025-03-05 10:00:00'),
         ('sale-2', 'course-1', 'student-2', 50000, '2025-03-15 14:30:00'),
         ('sale-3', 'course-2', 'student-3', 80000, '2025-03-20 09:00:00'),
         ('sale-4', 'course-2', 'student-4', 80000, '2025-03-22 11:00:00'),
         ('sale-5', 'course-3', 'student-5', 60000, '2025-01-31 23:30:00'),
         ('sale-6', 'course-3', 'student-6', 60000, '2025-03-10 16:00:00'),
         ('sale-7', 'course-4', 'student-7', 120000, '2025-02-14 10:00:00');

INSERT INTO sale_records (id, course_id, student_id, amount, paid_at) VALUES
         ('sale-8',  'course-1', 'student-8',  100000, '2025-04-05 10:00:00'),
         ('sale-9',  'course-2', 'student-9',  100000, '2025-04-10 12:00:00'),
         ('sale-10', 'course-1', 'student-10',  50000, '2025-04-15 09:00:00');

INSERT INTO cancel_records (id, sale_record_id, refund_amount, canceled_at) VALUES
       ('cancel-1', 'sale-3', 80000, '2025-03-25 10:00:00'),
       ('cancel-2', 'sale-4', 30000, '2025-03-26 10:00:00'),
       ('cancel-3', 'sale-5', 60000, '2025-02-01 10:00:00'),
       ('cancel-4', 'sale-8',  100000, '2025-04-20 10:00:00'),
       ('cancel-5', 'sale-9',   50000, '2025-04-25 14:00:00');

INSERT INTO fee_policy (id, fee_rate, effective_from, effective_to)
VALUES ('fee-2025-01', 20, '2025-01-01', '2025-03-31');

INSERT INTO fee_policy (id, fee_rate, effective_from, effective_to)
VALUES ('fee-2025-04', 15, '2025-04-01', '2025-12-31');
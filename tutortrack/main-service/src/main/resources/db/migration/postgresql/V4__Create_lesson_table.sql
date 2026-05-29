create table lesson(
    id bigserial primary key,
    lesson_date timestamp not null,
    attendance varchar(20) check (attendance IN ('PRESENT', 'ABSENT', 'LATE')),
    student_id bigint not null references student(id) on delete cascade,
    subject_id bigint not null references subject(id) on delete cascade
);
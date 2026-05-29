create table topic(
    id bigserial primary key,
    name varchar(255) not null,
    subject_id bigint not null references subject(id) on delete cascade
);
create schema if not exists auth_schema;
grant all on schema auth_schema to postgres;
grant all on schema auth_schema to public;

create table auth_schema.app_user (
    id bigserial primary key,
    email varchar(255) not null unique,
    password varchar(255) not null,
    role varchar(50) not null check (role IN ('TEACHER', 'USER')),
    name varchar(255),
    phone varchar(255),
    created_at timestamp default current_timestamp,
    updated_at timestamp default current_timestamp
);

create index idx_users_email on app_user(email);
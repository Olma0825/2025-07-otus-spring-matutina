create schema if not exists main_schema;

grant all on schema main_schema to postgres;
grant all on schema main_schema to public;

create table subject(
    id bigserial primary key,
    name varchar(255) not null
);
create table comments (
    id bigserial,
    body varchar(255),
    created_at timestamp default current_timestamp,
    book_id bigint,
    primary key (id)
);
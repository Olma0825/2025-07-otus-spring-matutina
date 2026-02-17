insert into authors(full_name)
values ('Author_1'), ('Author_2'), ('Author_3');

insert into genres(name)
values ('Genre_1'), ('Genre_2'), ('Genre_3'), ('Genre_4'), ('Genre_5'), ('Genre_6');

insert into books(title, author_id, genre_id)
values ('BookTitle_1', 1, 1), ('BookTitle_2', 2, 2), ('BookTitle_3', 3, 3);

insert into comments(id, body, book_id)
values (nextval('comment_sequence'), 'comment_1_1', 1), (nextval('comment_sequence'), 'comment_1_2', 1),
(nextval('comment_sequence'), 'comment_2_1', 2);

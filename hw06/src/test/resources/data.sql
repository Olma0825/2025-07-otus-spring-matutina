delete from comments;
delete from books;
delete from genres;
delete from authors;

insert into authors(id, full_name)
values (1, 'Author_1'), (2, 'Author_2'), (3, 'Author_3');

insert into genres(id, name)
values (1, 'Genre_1'), (2, 'Genre_2'), (3, 'Genre_3');

insert into books(id, title, author_id, genre_id)
values (100, 'BookTitle_1', 1, 1), (101, 'BookTitle_2', 2, 2), (102, 'BookTitle_3', 3, 3);

insert into comments(id, body, book_id)
values (nextval('comment_sequence'), 'comment_1_1', 100), (nextval('comment_sequence'), 'comment_1_2', 100),
(nextval('comment_sequence'), 'comment_2_1', 101);
-- Инициализация рейтингов (ratings)
MERGE INTO ratings (id, name, description) KEY(id)
VALUES (1, 'G', 'Нет возрастных ограничений');

MERGE INTO ratings (id, name, description) KEY(id)
VALUES (2, 'PG', 'Рекомендуется присутствие родителей');

MERGE INTO ratings (id, name, description) KEY(id)
VALUES (3, 'PG13', 'Детям до 13 лет просмотр не желателен');

MERGE INTO ratings (id, name, description) KEY(id)
VALUES (4, 'R', 'Лицам до 17 лет обязательно присутствие взрослого');

MERGE INTO ratings (id, name, description) KEY(id)
VALUES (5, 'NC17', 'Лицам до 18 лет просмотр запрещён');

-- Инициализация жанров (genres)
MERGE INTO genres (id, name) KEY(id)
VALUES (1, 'COMEDY');

MERGE INTO genres (id, name) KEY(id)
VALUES (2, 'DRAMA');

MERGE INTO genres (id, name) KEY(id)
VALUES (3, 'CARTOON');

MERGE INTO genres (id, name) KEY(id)
VALUES (4, 'THRILLER');

MERGE INTO genres (id, name) KEY(id)
VALUES (5, 'DOCUMENTARY');

MERGE INTO genres (id, name) KEY(id)
VALUES (6, 'ACTION');

-- Инициализация статусов дружбы (friendship_statuses)
MERGE INTO friendship_statuses (id, status) KEY(id)
VALUES (1, 'FS_REQUEST');

MERGE INTO friendship_statuses (id, status) KEY(id)
VALUES (2, 'FRIENDSHIP');
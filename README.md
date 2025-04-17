# java-filmorate
Template repository for Filmorate project.

# Схема базы данных (3NF)

## Таблицы
![Untitled](https://github.com/user-attachments/assets/b71421e5-55a8-42e3-89b7-add4b030af2c)

## Описание структуры базы данных

База данных состоит из следующих основных таблиц:

1. **users** - хранит информацию о пользователях
2. **friendships** - содержит связи между пользователями с указанием статуса дружбы
3. **films** - основная информация о фильмах
4. **ratings** - справочник рейтингов (MPAA)
5. **genres** - справочник жанров фильмов
6. **film_genres** - связь фильмов с жанрами (многие-ко-многим)
7. **likes** - информация о лайках пользователей

База данных соответствует 3-й нормальной форме (3NF), что обеспечивает:
- Минимизацию избыточности данных
- Эффективное хранение информации
- Целостность данных через внешние ключи

## Примеры SQL-запросов

### 1. Получение информации о фильме с его рейтингом и жанрами
```sql
SELECT f.id, f.name, f.description, f.release_date, f.duration, 
       r.name AS rating, 
       STRING_AGG(g.name, ', ') AS genres
FROM films f
JOIN ratings r ON f.rating_id = r.id
LEFT JOIN film_genres fg ON f.id = fg.film_id
LEFT JOIN genres g ON fg.genre_id = g.id
WHERE f.id = 1
GROUP BY f.id, r.name;
```
### 2. Получение списка друзей пользователя
```sql
SELECT 
    u.id, 
    u.name, 
    u.email
FROM friendships f
JOIN users u ON f.friend_id = u.id
WHERE f.user_id = 1 AND f.status_id = 2;
```

3. Топ популярных фильмов
```sql
SELECT 
    f.id, 
    f.name, 
    COUNT(l.user_id) AS likes_count
FROM films f
LEFT JOIN likes l ON f.id = l.film_id
GROUP BY f.id
ORDER BY likes_count DESC
LIMIT 10;
```

### 4. Добавление нового фильма
```sql
INSERT INTO films (name, description, release_date, duration, rating_id)
VALUES (
    'Новый фильм', 
    'Описание нового фильма', 
    '2023-01-01', 
    120, 
    3
);
```

### 5. Добавление лайка
```sql
INSERT INTO likes (film_id, user_id)
VALUES (1, 2);
```

### 6. Обновление статуса дружбы
```sql
UPDATE friendships
SET status_id = 2
WHERE user_id = 1 AND friend_id = 3;
```

### 7. Получение общих друзей
```sql
SELECT 
    u.id, 
    u.name
FROM friendships f1
JOIN friendships f2 ON f1.friend_id = f2.friend_id
JOIN users u ON f1.friend_id = u.id
WHERE f1.user_id = 1 
  AND f2.user_id = 2 
  AND f1.status_id = 2 
  AND f2.status_id = 2;
```

### 8. Поиск фильмов по жанру
```sql
SELECT 
    f.id,
    f.name,
    f.description
FROM films f
JOIN film_genres fg ON f.id = fg.film_id
JOIN genres g ON fg.genre_id = g.id
WHERE g.name = 'Комедия';
```

### 9. Получение пользователей, лайкнувших фильм
```sql
SELECT 
    u.id,
    u.name,
    u.email
FROM likes l
JOIN users u ON l.user_id = u.id
WHERE l.film_id = 1;
```

### 10. Удаление лайка
```sql
DELETE FROM likes
WHERE film_id = 1 AND user_id = 2;
```


### Ссылка на базу данных
https://dbdiagram.io/d/680042921ca52373f54e05ed


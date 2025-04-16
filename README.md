# java-filmorate
Template repository for Filmorate project.

# Схема базы данных (3NF)

## Таблицы

### Пользователи
```dbml
Table users {
  id BIGINT [primary key]
  email VARCHAR(30) [not null, unique]
  login VARCHAR(30) [not null, unique]
  name VARCHAR(50)
  birthday DATE
}

Table friendship_statuses {
  id BIGINT [primary key]
  status VARCHAR(20) [not null, unique]
}

Table friendships {
  user_id BIGINT [not null, ref: > users.id]
  friend_id BIGINT [not null, ref: > users.id]
  status_id INT [not null, ref: > friendship_statuses.id]
  
  indexes {
    (user_id, friend_id) [pk]
    user_id
    friend_id
    status_id
  }
}

Table films {
  id BIGINT [primary key]
  name VARCHAR(100) [not null]
  description VARCHAR(200)
  release_date DATE [not null]
  duration BIGINT [not null]
  rating_id BIGINT [not null, ref: > ratings.id]
}

Table ratings {
  id BIGINT [primary key]
  name VARCHAR(50) [not null, unique]
  description VARCHAR(200)
}

Table genres {
  id BIGINT [primary key]
  name VARCHAR(50) [not null, unique]
}

Table film_genres {
  film_id BIGINT [not null, ref: > films.id]
  genre_id BIGINT [not null, ref: > genres.id]
  
  indexes {
    (film_id, genre_id) [pk]
  }
}

Table likes {
  film_id BIGINT [not null, ref: > films.id]
  user_id BIGINT [not null, ref: > users.id]
  
  indexes {
    (film_id, user_id) [pk]
    film_id
    user_id
  }
}

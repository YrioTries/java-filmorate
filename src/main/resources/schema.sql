-- Таблица пользователей
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(30) NOT NULL UNIQUE,
    login VARCHAR(30) NOT NULL UNIQUE,
    name VARCHAR(50),
    birthday DATE
);


-- Таблица статусов дружбы
CREATE TABLE IF NOT EXISTS friendship_statuses (
    id BIGINT PRIMARY KEY,
    status VARCHAR(20) NOT NULL UNIQUE
);

-- Таблица дружеских связей
CREATE TABLE IF NOT EXISTS friendships (
    user_id BIGINT NOT NULL,
    friend_id BIGINT NOT NULL,
    status_id INT NOT NULL,
    PRIMARY KEY (user_id, friend_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (friend_id) REFERENCES users(id),
    FOREIGN KEY (status_id) REFERENCES friendship_statuses(id)
);

-- Таблица рейтингов
CREATE TABLE IF NOT EXISTS ratings (
    id BIGINT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(200)
);

-- Таблица фильмов
CREATE TABLE IF NOT EXISTS films (
    id BIGINT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(200),
    release_date DATE NOT NULL,
    duration BIGINT NOT NULL,
    rating_id BIGINT NOT NULL,
    FOREIGN KEY (rating_id) REFERENCES ratings(id)
);

-- Таблица жанров
CREATE TABLE IF NOT EXISTS genres (
    id BIGINT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- Таблица связи фильмов и жанров
CREATE TABLE IF NOT EXISTS film_genres (
    film_id BIGINT NOT NULL,
    genre_id BIGINT NOT NULL,
    PRIMARY KEY (film_id, genre_id),
    FOREIGN KEY (film_id) REFERENCES films(id),
    FOREIGN KEY (genre_id) REFERENCES genres(id)
);

-- Таблица лайков
CREATE TABLE IF NOT EXISTS likes (
    film_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (film_id, user_id),
    FOREIGN KEY (film_id) REFERENCES films(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Создаем индексы (для H2 не нужна проверка на существование)
CREATE INDEX IF NOT EXISTS idx_friendships_user_id ON friendships(user_id);
CREATE INDEX IF NOT EXISTS idx_friendships_friend_id ON friendships(friend_id);
CREATE INDEX IF NOT EXISTS idx_friendships_status_id ON friendships(status_id);
CREATE INDEX IF NOT EXISTS idx_likes_film_id ON likes(film_id);
CREATE INDEX IF NOT EXISTS idx_likes_user_id ON likes(user_id);
package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.repositories.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.repositories.GenreDbStorage;
import ru.yandex.practicum.filmorate.dao.repositories.RatingDbStorage;
import ru.yandex.practicum.filmorate.dao.repositories.UserDbStorage;
import ru.yandex.practicum.filmorate.dao.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.dao.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.dao.mappers.RatingRowMapper;
import ru.yandex.practicum.filmorate.dao.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.RatingService;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class, UserRowMapper.class, GenreDbStorage.class, GenreRowMapper.class,
        RatingDbStorage.class, RatingRowMapper.class, FilmDbStorage.class, FilmRowMapper.class,
        RatingService.class})
class FilmorateApplicationTests {
    private final UserDbStorage userDbStorage;
    private final FilmDbStorage filmDbStorage;
    private final GenreDbStorage genreDbStorage;
    private final RatingDbStorage ratingDbStorage;

    @BeforeEach
    void updateDb() {
        User user = new User(
                0L,
                "User name",
                "Email@mail.com",
                "qwerty",
                LocalDate.of(1990, 12, 12)
        );
        userDbStorage.create(user);

        Rating mpa = new Rating(1, "G");
        Film film = new Film(
                0L,
                "Film name",
                "asdfasdfsad sadfasdfsadf",
                LocalDate.of(2001, 4, 5),
                100,
                Collections.unmodifiableSequencedSet(new LinkedHashSet<>()),
                mpa
        );
        filmDbStorage.create(film);
    }

    @Test
    @DirtiesContext
    public void testFindUserById() {
        User gUser = userDbStorage.getUserById(1L);
        assertThat(gUser).hasFieldOrPropertyWithValue("id", 1L);
        assertThat(gUser).hasFieldOrPropertyWithValue("name", "User name");
        assertThat(gUser).hasFieldOrPropertyWithValue("email", "Email@mail.com");
        assertThat(gUser).hasFieldOrPropertyWithValue("login", "qwerty");
        assertThat(gUser).hasFieldOrPropertyWithValue("birthday", LocalDate.of(1990, 12, 12));
    }

    @Test
    @DirtiesContext
    public void testUpdateUser() {
        User newUser = new User(
                1L,
                "User name 2",
                "Email123@mail.com",
                "qwerty123",
                LocalDate.of(1992, 1, 11)
                //Set.of()
        );
        User upUser = userDbStorage.update(newUser);
        assertThat(upUser).hasFieldOrPropertyWithValue("id", 1L);
        assertThat(upUser).hasFieldOrPropertyWithValue("name", "User name 2");
        assertThat(upUser).hasFieldOrPropertyWithValue("email", "Email123@mail.com");
        assertThat(upUser).hasFieldOrPropertyWithValue("login", "qwerty123");
        assertThat(upUser).hasFieldOrPropertyWithValue("birthday", LocalDate.of(1992, 1, 11));
    }

    @Test
    @DirtiesContext
    public void testGetAllUsers() {
        List<User> users = userDbStorage.getAllUsers();
        assertFalse(users.isEmpty());
    }

    @Test
    @DirtiesContext
    public void testAddFriendAndDeleteFriend() {
        User user = new User(
                0L,
                "User name 5",
                "Email4523@mail.com",
                "123qwerty123",
                LocalDate.of(1988, 2, 4)
        );
        User aUser = userDbStorage.create(user);
        userDbStorage.addFriend(1L, aUser.getId());
        List<User> friends = userDbStorage.getAllFriendsById(1L);
        assertEquals(1, friends.size());
        assertThat(friends.get(0)).hasFieldOrPropertyWithValue("id", aUser.getId());

        userDbStorage.removeFriend(1L, aUser.getId());
        List<User> emptyFriends = userDbStorage.getAllFriendsById(1L);
        assertTrue(emptyFriends.isEmpty());
    }

    @Test
    @DirtiesContext
    public void testGetAllGenres() {
        List<Genre> genres = genreDbStorage.getAllGenres();
        assertEquals(6, genres.size());
        assertEquals("Комедия", genres.get(0).getName());
        assertEquals("Драма", genres.get(1).getName());
        assertEquals("Мультфильм", genres.get(2).getName());
        assertEquals("Триллер", genres.get(3).getName());
        assertEquals("Документальный", genres.get(4).getName());
        assertEquals("Боевик", genres.get(5).getName());
    }

    @Test
    @DirtiesContext
    public void testGetGenreById() {
        Genre genre = genreDbStorage.getGenreById(1);
        assertEquals("Комедия", genre.getName());
        genre = genreDbStorage.getGenreById(2);
        assertEquals("Драма", genre.getName());
        genre = genreDbStorage.getGenreById(3);
        assertEquals("Мультфильм", genre.getName());
        genre = genreDbStorage.getGenreById(4);
        assertEquals("Триллер", genre.getName());
        genre = genreDbStorage.getGenreById(5);
        assertEquals("Документальный", genre.getName());
        genre = genreDbStorage.getGenreById(6);
        assertEquals("Боевик", genre.getName());
    }

    @Test
    @DirtiesContext
    public void testGetAllMpaRatings() {
        List<Rating> mpas = ratingDbStorage.getAllRatings();
        assertEquals(5, mpas.size());
        assertEquals("G", mpas.get(0).getName());
        assertEquals("PG", mpas.get(1).getName());
        assertEquals("PG-13", mpas.get(2).getName());
        assertEquals("R", mpas.get(3).getName());
        assertEquals("NC-17", mpas.get(4).getName());
    }

    @Test
    @DirtiesContext
    public void testGetMpaRatingById() {
        Rating mpa = ratingDbStorage.getRatingById(1);
        assertEquals("G", mpa.getName());
        mpa = ratingDbStorage.getRatingById(2);
        assertEquals("PG", mpa.getName());
        mpa = ratingDbStorage.getRatingById(3);
        assertEquals("PG-13", mpa.getName());
        mpa = ratingDbStorage.getRatingById(4);
        assertEquals("R", mpa.getName());
        mpa = ratingDbStorage.getRatingById(5);
        assertEquals("NC-17", mpa.getName());
    }

    @Test
    @DirtiesContext
    public void testFindFilmById() {
        Film gFilm = filmDbStorage.getFilmById(1L);
        assertThat(gFilm).hasFieldOrPropertyWithValue("id", 1L);
        assertThat(gFilm).hasFieldOrPropertyWithValue("name", "Film name");
        assertThat(gFilm).hasFieldOrPropertyWithValue("description", "asdfasdfsad sadfasdfsadf");
        assertThat(gFilm).hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(2001, 4, 5));
        assertThat(gFilm).hasFieldOrPropertyWithValue("duration", 100);
        assertEquals(1, gFilm.getMpa().getId());
    }

    @Test
    @DirtiesContext
    public void testUpdateFilm() {
        Film newFilm = new Film(
                1L,
                "Film name 2",
                "asdfasdfsad sadfasdfsadf asdadadas",
                LocalDate.of(2011, 3, 2),
                110,
                Collections.unmodifiableSequencedSet(new LinkedHashSet<>()),
                new Rating(2, "PG")
        );
        filmDbStorage.update(newFilm);
        Film upFilm = filmDbStorage.getFilmById(1L);
        assertThat(upFilm).hasFieldOrPropertyWithValue("id", 1L);
        assertThat(upFilm).hasFieldOrPropertyWithValue("name", "Film name 2");
        assertThat(upFilm).hasFieldOrPropertyWithValue("description", "asdfasdfsad sadfasdfsadf asdadadas");
        assertThat(upFilm).hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(2011, 3, 2));
        assertThat(upFilm).hasFieldOrPropertyWithValue("duration", 110);
        assertEquals(2, upFilm.getMpa().getId());
    }

    @Test
    @DirtiesContext
    public void testGetAllFilms() {
        List<Film> films = filmDbStorage.getAllFilms();
        assertFalse(films.isEmpty());
    }

    @Test
    @DirtiesContext
    public void testGetTopFilms() {
        Film newFilm = new Film(
                0L,
                "Film name 2",
                "asdfasdfsad sadfasdfsadf asdadadas",
                LocalDate.of(2012, 2, 5),
                90,
                Collections.unmodifiableSequencedSet(new LinkedHashSet<>()),
                new Rating(2, "PG")
        );
        filmDbStorage.create(newFilm);
        newFilm = new Film(
                0L,
                "Film name 3",
                "asdfasdfsad sadfasdfsadf asdadadas",
                LocalDate.of(2000, 1, 22),
                80,
                Collections.unmodifiableSequencedSet(new LinkedHashSet<>()),
                new Rating(2, "PG")
        );
        filmDbStorage.create(newFilm);

        User newUser = new User(
                0L,
                "User name 2",
                "Email64543@mail.com",
                "5645654qwerty123",
                LocalDate.of(1984, 2, 4)
        );
        userDbStorage.create(newUser);
        newUser = new User(
                0L,
                "User name 3",
                "Email65465443@mail.com",
                "5645654qwerty4353",
                LocalDate.of(1994, 1, 4)
        );
        userDbStorage.create(newUser);

        filmDbStorage.addLike(1L, 1L);
        filmDbStorage.addLike(2L, 1L);
        filmDbStorage.addLike(2L, 2L);
        filmDbStorage.addLike(3L, 1L);
        filmDbStorage.addLike(3L, 2L);
        filmDbStorage.addLike(3L, 3L);

        List<Film> topFilms = filmDbStorage.getTopFilms(10);
        assertEquals(3, topFilms.size());
        assertEquals(3, topFilms.get(0).getId());
        assertEquals(2, topFilms.get(1).getId());
        assertEquals(1, topFilms.get(2).getId());
    }
}

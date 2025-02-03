package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.*;
import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilmorateApplicationTests {

	@Autowired
	private FilmController filmController;

	@Autowired
	private UserController userController;

	@Test
	public void testFilmReleaseDate() {
		LocalDate date = LocalDate.of(1590, 6, 15);
		LocalDateTime dateTime = date.atStartOfDay();
		Instant bornOfFilms = dateTime.atZone(ZoneId.systemDefault()).toInstant();

		Film film = new Film();
		film.setName("");
		film.setDescription("Description");
		film.setReleaseDate(bornOfFilms);
		film.setDuration(120);

		assertThrows(ConditionsNotMetException.class, () -> filmController.create(film));
	}

	@Test
	public void testUserValidation() {
		User user = new User();
		user.setEmail("invalid-email");
		user.setLogin("login");
		user.setName("name");
		user.setBirthday(new Date());

		assertThrows(ConditionsNotMetException.class, () -> userController.create(user));
	}


	@Test
	public void testUserBirthdayInFuture() {
		User user = new User();
		user.setEmail("test@example.com");
		user.setLogin("login");
		user.setName("name");
		user.setBirthday(Date.from(Instant.now().plusSeconds(1)));

		assertThrows(ConditionsNotMetException.class, () -> userController.create(user));
	}


	@Test
	public void testUserUpdateNotFound() {
		User user = new User();
		user.setId(1L);
		user.setEmail("test@example.com");
		user.setLogin("login");
		user.setName("name");
		user.setBirthday(new Date());

		assertThrows(NotFoundException.class, () -> userController.update(user));
	}

	@Test
	public void testFilmUpdateNotFound() {
		Film film = new Film();
		film.setId(1L);
		film.setDescription("Description");
		film.setDuration(12);
		film.setName("name");
		film.setReleaseDate(new Date(2000, Calendar.APRIL, 12).toInstant());

		assertThrows(NotFoundException.class, () -> filmController.update(film.getId()));
	}

}

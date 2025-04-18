package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilmorateApplicationTests {

	@Autowired
	private FilmController filmController;

	@Autowired
	private UserController userController;

	@Test
	public void testFilmReleaseDate() {
		LocalDate bornOfFilms = LocalDate.of(1590, Calendar.JULY, 15);


		Film film = new Film();
		film.setName("Test Film");
		film.setDescription("Description");
		film.setReleaseDate(bornOfFilms);
		film.setDuration(120);
		film.setLikesFrom(new HashSet<>());

		assertThrows(ValidationException.class, () -> filmController.create(film));
	}

	@Test
	public void testUserUpdateNotFound() {
		User user = new User();
		user.setEmail("test@example.com");
		user.setLogin("login");
		user.setName("name");
		user.setBirthday(LocalDate.of(2000, Calendar.FEBRUARY, 15));

		assertThrows(NotFoundException.class, () -> userController.update(user));
	}

	@Test
	public void testFilmUpdateNotFound() {
		Film film = new Film();
		film.setId(1L);
		film.setDescription("Description");
		film.setDuration(12);
		film.setName("name");
		film.setReleaseDate(LocalDate.of(2000, Calendar.APRIL, 12));

		assertThrows(NotFoundException.class, () -> filmController.update(film));
	}

	@Test
	public void testCreateUserSuccessfully() {
		User user1 = new User();
		user1.setEmail("valid-email123@example.com");
		user1.setLogin("validLogin");
		user1.setName("Valid Name");
		user1.setBirthday(LocalDate.of(2000, Calendar.APRIL, 23));

		User createdUser = userController.create(user1);

		assertNotNull(createdUser);
		assertEquals("valid-email123@example.com", createdUser.getEmail());
		assertEquals("validLogin", createdUser.getLogin());
		assertEquals("Valid Name", createdUser.getName());
		assertEquals(LocalDate.of(2000, Calendar.APRIL, 23), createdUser.getBirthday());
	}

	@Test
	public void testUpdateUserSuccessfully() {
		// Создаем пользователя
		User user2 = new User();
		user2.setEmail("valid-email456@example.com");
		user2.setLogin("validLogin");
		user2.setName("Valid Name");
		user2.setBirthday(LocalDate.of(2000, Calendar.APRIL, 23));
		User createdUser = userController.create(user2);

		// Обновляем данные пользователя
		createdUser.setName("Updated Name");
		createdUser.setEmail("updated-email@example.com");

		User updatedUser = userController.update(createdUser);

		assertNotNull(updatedUser);
		assertEquals("updated-email@example.com", updatedUser.getEmail());
		assertEquals("Updated Name", updatedUser.getName());
	}

	@Test
	public void testFindAllUsers() {
		Collection<User> users;

		// Создаем пользователя
		User user = new User();
		user.setEmail("valid-email@example.com");
		user.setLogin("validLogin");
		user.setName("Valid Name");
		user.setBirthday(LocalDate.of(2000, Calendar.APRIL, 23));
		userController.create(user);

		users = userController.findAll();
		assertEquals(1, users.size());
	}

	@Test
	void testCreateUserWithInvalidBirthday() {
		User invalidUser = new User();
		invalidUser.setLogin("dolore ullamco");
		invalidUser.setEmail("yandex@mail.ru");
		invalidUser.setBirthday(LocalDate.of(2446, 8, 20)); // Некорректная дата

		assertThrows(ConstraintViolationException.class, () -> userController.create(invalidUser));

	}
}

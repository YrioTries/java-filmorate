package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = {"classpath:test-schema.sql", "classpath:test-data.sql"})
class FilmorateApplicationTests {

	@Autowired
	private FilmController filmController;

	@Autowired
	private UserController userController;

	private User createTestUser(String suffix) {
		User user = new User();
		user.setEmail("test" + suffix + "@example.com");
		user.setLogin("testLogin" + suffix);
		user.setName("Test User");
		user.setBirthday(LocalDate.of(1990, 1, 1));
		return user;
	}

	private Film createTestFilm() {
		Film film = new Film();
		film.setName("Test Film");
		film.setDescription("Test Description");
		film.setReleaseDate(LocalDate.of(2000, 1, 1));
		film.setDuration(120);
		film.setLikesFrom(new HashSet<>());
		return film;
	}

	@Test
	void createUserWithValidDataShouldReturnCreatedUser() {
		User testUser = createTestUser("pep");
		User createdUser = userController.create(testUser);

		assertNotNull(createdUser.getId());
		assertEquals("testpep@example.com", createdUser.getEmail());
		assertEquals("testLoginpep", createdUser.getLogin());
	}

	@Test
	void createUserWithInvalidBirthdayShouldThrowException() {
		User invalidUser = createTestUser("34");
		invalidUser.setBirthday(LocalDate.of(2446, 8, 20));

		assertThrows(ConstraintViolationException.class, () -> userController.create(invalidUser));
	}

	@Test
	void updateNonExistingUserShouldThrowNotFoundException() {
		User nonExistingUser = createTestUser("efs");
		nonExistingUser.setId(999L);

		assertThrows(NotFoundException.class, () -> userController.update(nonExistingUser));
	}

	@Test
	void updateExistingUserShouldUpdateSuccessfully() {
		User originalUser = userController.create(createTestUser("eww"));
		User updatedUser = new User(
				originalUser.getId(),
				"updated@example.com",
				originalUser.getLogin(),
				"Updated Name",
				originalUser.getBirthday()
		);

		User result = userController.update(updatedUser);

		assertEquals("Updated Name", result.getName());
		assertEquals("updated@example.com", result.getEmail());
	}

	@Test
	void createFilmWithInvalidReleaseDateShouldThrowValidationException() {
		Film invalidFilm = createTestFilm();
		invalidFilm.setReleaseDate(LocalDate.of(1590, 7, 15));

		assertThrows(ValidationException.class, () -> filmController.create(invalidFilm));
	}

	@Test
	void updateNonExistingFilmShouldThrowNotFoundException() {
		Film nonExistingFilm = createTestFilm();
		nonExistingFilm.setId(999L);

		assertThrows(NotFoundException.class, () -> filmController.update(nonExistingFilm));
	}

	@Test
	void findAllUsersShouldReturnCorrectCount() {
		int initialCount = userController.findAll().size();

		userController.create(createTestUser("ewewe"));
		User secondUser = createTestUser("dv");
		secondUser.setEmail("test2@example.com");
		secondUser.setLogin("testLogin2");
		userController.create(secondUser);

		Collection<User> users = userController.findAll();

		assertEquals(initialCount + 2, users.size());
	}

	@Test
	void createUserWithEmptyNameShouldUseLoginAsName() {
		User user = createTestUser("sx");
		user.setName("");

		User createdUser = userController.create(user);

		assertEquals(user.getLogin(), createdUser.getName());
	}
}

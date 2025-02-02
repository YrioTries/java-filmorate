package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class FilmorateApplicationTests {

//	@Test
//	void contextLoads() {
//	}

	@Autowired
	private FilmController filmController;

	@Autowired
	private UserController userController;

	@Test
	public void testFilmValidation() {
		Film film = new Film();
		film.setName("");
		film.setDescription("Description");
		film.setReleaseDate(Instant.now());
		film.setDuration(Duration.ofMinutes(120));

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

}

package ru.yandex.practicum.filmorate;

import org.assertj.core.api.Assertions;
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

		Assertions.assertThatThrownBy(() -> userController.create(invalidUser))
				.isInstanceOf(ValidationException.class)
				.hasMessageContaining("Некорректный дата рождения пользователя");
	}

	@Test
	void testGetPopularFilms() {
		// Создание фильмов
		Film film1 = new Film(0L, "Film 1", "Description 1", LocalDate.now(), 100, new HashSet<>());
		Film film2 = new Film(0L, "Film 2", "Description 2", LocalDate.now(), 100, new HashSet<>());

		Film createdFilm1 = filmController.create(film1);
		Film createdFilm2 = filmController.create(film2);

		// Добавление лайков
		filmController.userLike(createdFilm1.getId(), 1L);
		filmController.userLike(createdFilm1.getId(), 2L);
		filmController.userLike(createdFilm1.getId(), 3L); // Добавляем больше лайков для film1
		filmController.userLike(createdFilm2.getId(), 4L);

		// Получение популярных фильмов
		Collection<Film> popularFilms = filmController.getPopularFilms(10);

		assertEquals(2, popularFilms.size());

		// Преобразуем коллекцию в список для индексированного доступа
		List<Film> popularFilmsList = new ArrayList<>(popularFilms);
		assertEquals(createdFilm1.getId(), popularFilmsList.getFirst().getId());
	}

	@Test
	void testAddFriend() {
		// Убедитесь, что пользователи 4 и 5 существуют
		if (userController.getUser(4L) == null) {
			userController.create(new User(4L, "user4@example.com", "login4", "name4", LocalDate.now()));
		}
		if (userController.getUser(5L) == null) {
			userController.create(new User(5L, "user5@example.com", "login5", "name5", LocalDate.now()));
		}

		// Вызываем контроллер, а НЕ напрямую storage
		userController.addFriend(4L, 5L);

		// Получаем пользователей из хранилища (после добавления в друзья)
		User user4 = userController.getUser(4L);
		User user5 = userController.getUser(5L);

		// Проверяем, что они существуют (это хорошая практика)
		assertNotNull(user4, "User 4 should exist");
		assertNotNull(user5, "User 5 should exist");

		// Проверяем, что они *действительно* друзья
		assertTrue(user4.getFriends().contains(5L), "User 4 should have User 5 as a friend");
		assertTrue(user5.getFriends().contains(4L), "User 5 should have User 4 as a friend");
	}

}

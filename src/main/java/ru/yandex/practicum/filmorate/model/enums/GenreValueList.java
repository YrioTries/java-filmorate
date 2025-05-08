package ru.yandex.practicum.filmorate.model.enums;

import java.util.List;

public enum GenreValueList {
    COMEDY(GenreType.genreList.get(0)),
    DRAMA(GenreType.genreList.get(1)),
    CARTOON(GenreType.genreList.get(2)),
    THRILLER(GenreType.genreList.get(3)),
    DOCUMENTARY(GenreType.genreList.get(4)),
    ACTION(GenreType.genreList.get(5));

    private final String genre;

    GenreValueList(String genre) {
        this.genre = genre;
    }

    public String getGenre() {
        return genre;
    }

    public static boolean isCorrectGenre(String gn) {
        return GenreType.genreList.contains(gn);
    }

    private static class GenreType {
        public static final List<String> genreList = List.of("Комедия", "Драма", "Мультфильм", "Триллер",
                "Документальный", "Боевик");
    }
}

package ru.yandex.practicum.filmorate.model.enums;

import java.util.List;

public enum RatingValueList {
    G(RatingType.ratingList.get(0)),
    PG(RatingType.ratingList.get(1)),
    PG13(RatingType.ratingList.get(2)),
    R(RatingType.ratingList.get(3)),
    NC17(RatingType.ratingList.get(4));

    private final String rating;

    RatingValueList(String rating) {
        this.rating = rating;
    }

    public String getRating() {
        return rating;
    }

    public static boolean isCorrectRating(String testedValue) {
        return RatingType.ratingList.contains(testedValue);
    }

    private static class RatingType {
        public static final List<String> ratingList = List.of("G", "PG", "PG-13", "R", "NC-17");
    }
}

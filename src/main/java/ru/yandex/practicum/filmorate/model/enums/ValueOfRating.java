package ru.yandex.practicum.filmorate.model.enums;

import java.util.List;

public enum ValueOfRating {
    G(RatingTypes.ratingList.get(0)),
    PG(RatingTypes.ratingList.get(1)),
    PG13(RatingTypes.ratingList.get(2)),
    R(RatingTypes.ratingList.get(3)),
    NC17(RatingTypes.ratingList.get(4));

    private final String value;

    ValueOfRating(String v) {
        value = v;
    }

    public String getVal() {
        return value;
    }

    public static boolean isCorrect(String testedValue) {
        return RatingTypes.ratingList.contains(testedValue);
    }

    private static class RatingTypes {
        public static final List<String> ratingList = List.of("G", "PG", "PG-13", "R", "NC-17");
    }
}

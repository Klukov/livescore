package org.klukov.example.livescore.dto;

public record Score(Integer value) {

    public static Score of(Integer value) {
        return new Score(value);
    }

    public Score {
        if (value < 0) {
            throw new IllegalArgumentException("Score cannot be less than 0");
        }
    }

    private static final Score startScore = new Score(0);

    public static Score startScore() {
        return startScore;
    }

    public Score add(Score score) {
        return Score.of(this.value() + score.value());
    }
}

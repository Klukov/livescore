package org.klukov.example.livescore.dto;

public record Score(int value) implements Comparable<Score> {

    public static Score of(int value) {
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

    public boolean isLessOrEqualThan(Score other) {
        return !this.isHigherThan(other);
    }

    public boolean isHigherThan(Score other) {
        return this.value > other.value;
    }

    public Score add(Score score) {
        return Score.of(this.value() + score.value());
    }

    @Override
    public int compareTo(Score o) {
        return Integer.compare(this.value, o.value);
    }
}

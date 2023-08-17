package org.klukov.example.livescore.dto;

import lombok.ToString;
import lombok.Value;

@Value
public class Match implements Comparable<Match> {
    Team homeTeam;
    Team awayTeam;
    Score homeTeamScore;
    Score awayTeamScore;
    Score totalScore;
    long startTimeInEpochMillis;

    public Match(
            Team homeTeam,
            Team awayTeam,
            Score homeTeamScore,
            Score awayTeamScore,
            long startTimeInEpochMillis) {
        if (homeTeam == null
                || awayTeam == null
                || homeTeamScore == null
                || awayTeamScore == null) {
            throw new IllegalArgumentException("Teams and scores values cannot be null");
        }
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.homeTeamScore = homeTeamScore;
        this.awayTeamScore = awayTeamScore;
        this.totalScore = homeTeamScore.add(awayTeamScore);
        this.startTimeInEpochMillis = startTimeInEpochMillis;
    }

    @Override
    public int compareTo(Match o) {
        var scoreComparison = -this.totalScore.compareTo(o.totalScore);
        return scoreComparison != 0
                ? scoreComparison
                : -Long.compare(this.startTimeInEpochMillis, o.startTimeInEpochMillis);
    }

    public Match withUpdatedStartTime(long startTimeInEpochMillis) {
        return Match.builder()
                .homeTeam(this.homeTeam)
                .awayTeam(this.awayTeam)
                .homeTeamScore(this.homeTeamScore)
                .awayTeamScore(this.awayTeamScore)
                .startTimeInEpochMillis(startTimeInEpochMillis)
                .build();
    }

    public Match withUpdatedScore(Score homeTeamScore, Score awayTeamScore) {
        return Match.builder()
                .homeTeam(this.homeTeam)
                .awayTeam(this.awayTeam)
                .homeTeamScore(homeTeamScore)
                .awayTeamScore(awayTeamScore)
                .startTimeInEpochMillis(this.startTimeInEpochMillis)
                .build();
    }

    public static MatchBuilder builder() {
        return new MatchBuilder();
    }

    @ToString
    @SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
    public static class MatchBuilder {
        private Team homeTeam;
        private Team awayTeam;
        private Score homeTeamScore;
        private Score awayTeamScore;
        private long startTimeInEpochMillis;

        MatchBuilder() {}

        public MatchBuilder homeTeam(Team homeTeam) {
            this.homeTeam = homeTeam;
            return this;
        }

        public MatchBuilder awayTeam(Team awayTeam) {
            this.awayTeam = awayTeam;
            return this;
        }

        public MatchBuilder homeTeamScore(Score homeTeamScore) {
            this.homeTeamScore = homeTeamScore;
            return this;
        }

        public MatchBuilder awayTeamScore(Score awayTeamScore) {
            this.awayTeamScore = awayTeamScore;
            return this;
        }

        public MatchBuilder startTimeInEpochMillis(long startTimeInEpochMillis) {
            this.startTimeInEpochMillis = startTimeInEpochMillis;
            return this;
        }

        public Match build() {
            return new Match(
                    this.homeTeam,
                    this.awayTeam,
                    this.homeTeamScore,
                    this.awayTeamScore,
                    this.startTimeInEpochMillis);
        }
    }
}

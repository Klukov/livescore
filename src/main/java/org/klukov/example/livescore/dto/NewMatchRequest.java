package org.klukov.example.livescore.dto;

import lombok.Builder;

@Builder
public record NewMatchRequest(Team homeTeam, Team awayTeam, long startTimeInEpochMillis) {

    public NewMatchRequest {
        if (homeTeam == null || awayTeam == null) {
            throw new IllegalArgumentException("Any team cannot be null");
        }
    }

    public static NewMatchRequest of(Team homeTeam, Team awayTeam, long startTimeInEpochMillis) {
        return NewMatchRequest.builder()
                .homeTeam(homeTeam)
                .awayTeam(awayTeam)
                .startTimeInEpochMillis(startTimeInEpochMillis)
                .build();
    }

    public Match toMatch() {
        return Match.builder()
                .homeTeam(homeTeam())
                .awayTeam(awayTeam())
                .homeTeamScore(Score.startScore())
                .awayTeamScore(Score.startScore())
                .startTimeInEpochMillis(startTimeInEpochMillis)
                .build();
    }
}

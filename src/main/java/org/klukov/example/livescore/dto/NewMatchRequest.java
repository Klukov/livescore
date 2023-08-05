package org.klukov.example.livescore.dto;

import lombok.Builder;

@Builder
public record NewMatchRequest(Team homeTeam, Team awayTeam) {

    public static NewMatchRequest of(Team homeTeam, Team awayTeam) {
        return NewMatchRequest.builder().homeTeam(homeTeam).awayTeam(awayTeam).build();
    }

    public Match toMatch() {
        return Match.builder()
                .homeTeam(homeTeam())
                .awayTeam(awayTeam())
                .homeTeamScore(Score.startScore())
                .awayTeamScore(Score.startScore())
                .build();
    }
}

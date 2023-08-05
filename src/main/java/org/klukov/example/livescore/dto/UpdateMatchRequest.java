package org.klukov.example.livescore.dto;

import lombok.Builder;

@Builder
public record UpdateMatchRequest(
        Team homeTeam, Team awayTeam, Score homeTeamScore, Score awayTeamScore) {

    public Match toMatch() {
        return Match.builder()
                .homeTeam(homeTeam())
                .awayTeam(awayTeam())
                .homeTeamScore(homeTeamScore())
                .awayTeamScore(awayTeamScore())
                .build();
    }
}

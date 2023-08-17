package org.klukov.example.livescore.dto;

import lombok.Builder;

@Builder
public record UpdateMatchRequest(
        Team homeTeam, Team awayTeam, Score homeTeamScore, Score awayTeamScore) {

    public UpdateMatchRequest {
        if (homeTeam == null
                || awayTeam == null
                || homeTeamScore == null
                || awayTeamScore == null) {
            throw new IllegalArgumentException("Any value cannot be null");
        }
    }

    public Score getTotalScore() {
        return homeTeamScore.add(awayTeamScore);
    }
}

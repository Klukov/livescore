package org.klukov.example.livescore.dto;

import lombok.Builder;

@Builder
public record Match(Team homeTeam, Team awayTeam, Score homeTeamScore, Score awayTeamScore) {

    public Score totalScore() {
        return homeTeamScore().add(awayTeamScore());
    }
}

package org.klukov.example.livescore.dto;

import lombok.Builder;

@Builder
public record FinishMatchRequest(Team homeTeam, Team awayTeam) {

    public FinishMatchRequest {
        if (homeTeam == null || awayTeam == null) {
            throw new IllegalArgumentException("Team cannot be null");
        }
    }

    public static FinishMatchRequest of(Team homeTeam, Team awayTeam) {
        return FinishMatchRequest.builder().homeTeam(homeTeam).awayTeam(awayTeam).build();
    }
}

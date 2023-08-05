package org.klukov.example.livescore.dto;

import lombok.Builder;

@Builder
public record FinishMatchRequest(Team homeTeam, Team awayTeam) {

    public static FinishMatchRequest of(Team homeTeam, Team awayTeam) {
        return FinishMatchRequest.builder().homeTeam(homeTeam).awayTeam(awayTeam).build();
    }
}

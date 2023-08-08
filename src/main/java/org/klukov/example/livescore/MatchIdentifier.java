package org.klukov.example.livescore;

import lombok.Builder;
import org.klukov.example.livescore.dto.Match;
import org.klukov.example.livescore.dto.Team;

@Builder
record MatchIdentifier(Team homeTeam, Team awayTeam) {

    public static MatchIdentifier of(Match match) {
        return MatchIdentifier.of(match.getHomeTeam(), match.getAwayTeam());
    }

    public static MatchIdentifier of(Team homeTeam, Team awayTeam) {
        return MatchIdentifier.builder().homeTeam(homeTeam).awayTeam(awayTeam).build();
    }
}

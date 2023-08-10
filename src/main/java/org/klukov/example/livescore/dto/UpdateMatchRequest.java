package org.klukov.example.livescore.dto;

import lombok.Builder;

@Builder
public record UpdateMatchRequest(
        Team homeTeam, Team awayTeam, Score homeTeamScore, Score awayTeamScore) {}

package org.klukov.example.livescore.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record ScoreBoard(List<Match> matchList) {}

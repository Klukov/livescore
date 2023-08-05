package org.klukov.example.livescore.dto;

import java.util.List;

public record ScoreBoard(List<Match> matchList) {}

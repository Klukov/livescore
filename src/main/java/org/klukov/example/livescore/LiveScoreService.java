package org.klukov.example.livescore;

import java.time.Clock;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.klukov.example.livescore.dto.FinishMatchRequest;
import org.klukov.example.livescore.dto.Match;
import org.klukov.example.livescore.dto.NewMatchRequest;
import org.klukov.example.livescore.dto.ScoreBoard;
import org.klukov.example.livescore.dto.UpdateMatchRequest;

public final class LiveScoreService {

    private final Clock clock;
    private final ConcurrentHashMap<MatchIdentifier, Match> matchesMap;
    private final Object writeLock;
    private transient List<Match> liveScores;

    public LiveScoreService(Clock clock) {
        this.clock = clock;
        this.matchesMap = new ConcurrentHashMap<>();
        this.writeLock = new Object();
        this.liveScores = Collections.emptyList();
    }

    public void startNewMatch(NewMatchRequest request) {
        synchronized (writeLock) {
            var identifier = MatchIdentifier.of(request.homeTeam(), request.awayTeam());
            matchesMap.compute(identifier, (key, value) -> computeStartNewMatch(value, request));
            this.recalculateLiveScores();
        }
    }

    public void updateMatch(UpdateMatchRequest request) {
        synchronized (writeLock) {
            var identifier = MatchIdentifier.of(request.homeTeam(), request.awayTeam());
            matchesMap.compute(identifier, (key, value) -> computeUpdateMatch(value, request));
            this.recalculateLiveScores();
        }
    }

    public void finishMatch(FinishMatchRequest request) {
        synchronized (writeLock) {
            matchesMap.remove(MatchIdentifier.of(request.homeTeam(), request.awayTeam()));
            this.recalculateLiveScores();
        }
    }

    public ScoreBoard getScoreBoard() {
        return ScoreBoard.builder().matchList(liveScores.stream().toList()).build();
    }

    private Match computeStartNewMatch(Match value, NewMatchRequest request) {
        return Optional.ofNullable(value)
                .map(v -> updateTime(v, request))
                .orElseGet(request::toMatch);
    }

    private Match computeUpdateMatch(Match value, UpdateMatchRequest request) {
        return Optional.ofNullable(value)
                .map(v -> updateScore(v, request))
                .orElseGet(() -> generateMatchWithTimeFromClock(request));
    }

    private void recalculateLiveScores() {
        this.liveScores = matchesMap.values().stream().sorted().toList();
    }

    private Match generateMatchWithTimeFromClock(UpdateMatchRequest updateMatchRequest) {
        return Match.builder()
                .homeTeam(updateMatchRequest.homeTeam())
                .awayTeam(updateMatchRequest.awayTeam())
                .homeTeamScore(updateMatchRequest.homeTeamScore())
                .awayTeamScore(updateMatchRequest.awayTeamScore())
                .startTimeInEpochMillis(clock.millis())
                .build();
    }

    private static Match updateTime(Match match, NewMatchRequest request) {
        return match.withUpdatedStartTime(request.startTimeInEpochMillis());
    }

    private static Match updateScore(Match match, UpdateMatchRequest request) {
        return match.withUpdatedScore(request.homeTeamScore(), request.awayTeamScore());
    }
}

package org.klukov.example.livescore;

import java.time.Clock;
import java.util.Collections;
import java.util.List;
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

    public void startNewMatch(NewMatchRequest newMatchRequest) {
        synchronized (writeLock) {
            var newMatch = newMatchRequest.toMatch();
            var newMatchIdentifier = MatchIdentifier.of(newMatch);
            matchesMap.putIfAbsent(newMatchIdentifier, newMatch);
            matchesMap.computeIfPresent(
                    newMatchIdentifier, (key, value) -> generateNewMatch(key, newMatch, value));
            this.recalculateLiveScores();
        }
    }

    public void updateMatch(UpdateMatchRequest updateMatchRequest) {
        synchronized (writeLock) {
            var updatedMatchIdentifier =
                    MatchIdentifier.of(
                            updateMatchRequest.homeTeam(), updateMatchRequest.awayTeam());
            matchesMap.putIfAbsent(
                    updatedMatchIdentifier, generateMatchWithTimeFromClock(updateMatchRequest));
            matchesMap.computeIfPresent(
                    updatedMatchIdentifier,
                    (key, value) -> generateNewMatch(key, value, updateMatchRequest.toMatch()));
            this.recalculateLiveScores();
        }
    }

    public void finishMatch(FinishMatchRequest finishMatchRequest) {
        synchronized (writeLock) {
            var finishedMatch = finishMatchRequest.toMatch();
            var finishedMatchIdentifier = MatchIdentifier.of(finishedMatch);
            matchesMap.remove(finishedMatchIdentifier);
            this.recalculateLiveScores();
        }
    }

    public ScoreBoard getScoreBoard() {
        return ScoreBoard.builder().matchList(liveScores.stream().toList()).build();
    }

    private void recalculateLiveScores() {
        this.liveScores = matchesMap.values().stream().sorted().toList();
    }

    private static Match generateNewMatch(MatchIdentifier key, Match timeData, Match scoreData) {
        return Match.builder()
                .homeTeam(key.homeTeam())
                .awayTeam(key.awayTeam())
                .homeTeamScore(scoreData.getHomeTeamScore())
                .awayTeamScore(scoreData.getAwayTeamScore())
                .startTimeInEpochMillis(timeData.getStartTimeInEpochMillis())
                .build();
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
}

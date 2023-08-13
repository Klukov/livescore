package org.klukov.example.livescore

import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import org.klukov.example.livescore.dto.FinishMatchRequest
import org.klukov.example.livescore.dto.Match
import org.klukov.example.livescore.dto.NewMatchRequest
import org.klukov.example.livescore.dto.Score
import org.klukov.example.livescore.dto.Team
import org.klukov.example.livescore.dto.UpdateMatchRequest
import spock.lang.Specification

class LiveScoreServiceTest extends Specification {

    private final static long CURRENT_CLOCK_EPOCH_MILLIS = 999999L

    private Clock clock = Clock.fixed(Instant.ofEpochMilli(CURRENT_CLOCK_EPOCH_MILLIS), ZoneId.systemDefault())

    private LiveScoreService liveScoreService

    def setup() {
        liveScoreService = new LiveScoreService(clock)
    }

    def "should generate start dashboard and all matches should be visible"() {
        given:
        generateStartDashBoard()

        when:
        def result = liveScoreService.getScoreBoard()

        then:
        result.matchList().size() == 3
        assertMatch(result.matchList()[0], "E", "F", 0, 0, 100)
        assertMatch(result.matchList()[1], "C", "D", 0, 0, 10)
        assertMatch(result.matchList()[2], "A", "B", 0, 0, 1)
    }

    def "should add new match and this match should be visible on dashboard"() {
        given:
        generateStartDashBoard()

        when:
        addNewMatch("X", "Y", 50)
        addNewMatch("P", "R", 5)
        def result = liveScoreService.getScoreBoard()

        then:
        result.matchList().size() == 5
        assertMatch(result.matchList()[0], "E", "F", 0, 0, 100)
        assertMatch(result.matchList()[1], "X", "Y", 0, 0, 50)
        assertMatch(result.matchList()[2], "C", "D", 0, 0, 10)
        assertMatch(result.matchList()[3], "P", "R", 0, 0, 5)
        assertMatch(result.matchList()[4], "A", "B", 0, 0, 1)
    }

    def "should update existing match and result should be visible on dashboard"() {
        given:
        generateStartDashBoard()

        when:
        updateMatch("C", "D", 2, 1)
        def result = liveScoreService.getScoreBoard()

        then:
        result.matchList().size() == 3
        assertMatch(result.matchList()[0], "C", "D", 2, 1, 10)
        assertMatch(result.matchList()[1], "E", "F", 0, 0, 100)
        assertMatch(result.matchList()[2], "A", "B", 0, 0, 1)
    }

    def "should update do not happen when update message came with older data - lower score equals older data"() {
        given:
        generateStartDashBoard()

        when:
        updateMatch("C", "D", 2, 1)
        updateMatch("C", "D", 1, 1)
        def result = liveScoreService.getScoreBoard()

        then:
        result.matchList().size() == 3
        assertMatch(result.matchList()[0], "C", "D", 2, 1, 10)
        assertMatch(result.matchList()[1], "E", "F", 0, 0, 100)
        assertMatch(result.matchList()[2], "A", "B", 0, 0, 1)
    }

    def "should update many matches and result should be visible on dashboard and result should be ordered"() {
        given:
        generateUpdatedDashBoard()

        when:
        def result = liveScoreService.getScoreBoard()

        then:
        result.matchList().size() == 5
        assertMatch(result.matchList()[0], "C", "D", 5, 5, 10)
        assertMatch(result.matchList()[1], "A", "B", 5, 4, 1)
        assertMatch(result.matchList()[2], "G", "H", 3, 1, 200)
        assertMatch(result.matchList()[3], "I", "J", 2, 0, 300)
        assertMatch(result.matchList()[4], "E", "F", 0, 0, 100)
    }

    def "should finish existing match and match should disappear from dashboard"() {
        given:
        generateStartDashBoard()

        when:
        liveScoreService.finishMatch(FinishMatchRequest.of(Team.of("C"), Team.of("D")))
        def result = liveScoreService.getScoreBoard()

        then:
        result.matchList().size() == 2
        assertMatch(result.matchList()[0], "E", "F", 0, 0, 100)
        assertMatch(result.matchList()[1], "A", "B", 0, 0, 1)
    }

    def "should add new match when match does not exist and update is called"() {
        given:
        generateStartDashBoard()

        when:
        updateMatch("X", "Y", 2, 1)
        def result = liveScoreService.getScoreBoard()

        then:
        result.matchList().size() == 4
        assertMatch(result.matchList()[0], "X", "Y", 2, 1, CURRENT_CLOCK_EPOCH_MILLIS)
        assertMatch(result.matchList()[1], "E", "F", 0, 0, 100)
        assertMatch(result.matchList()[2], "C", "D", 0, 0, 10)
        assertMatch(result.matchList()[3], "A", "B", 0, 0, 1)
    }

    def "should start time update happen when new match call happen and match already existed on dashboard"() {
        given:
        generateUpdatedDashBoard()

        when:
        addNewMatch("G", "H", 999)
        def result = liveScoreService.getScoreBoard()

        then:
        result.matchList().size() == 5
        assertMatch(result.matchList()[0], "C", "D", 5, 5, 10)
        assertMatch(result.matchList()[1], "A", "B", 5, 4, 1)
        assertMatch(result.matchList()[2], "G", "H", 3, 1, 999)
        assertMatch(result.matchList()[3], "I", "J", 2, 0, 300)
        assertMatch(result.matchList()[4], "E", "F", 0, 0, 100)
    }

    def "should nothing happen when match does not exists and finish match is called"() {
        given:
        generateStartDashBoard()

        when:
        liveScoreService.finishMatch(FinishMatchRequest.of(Team.of("X"), Team.of("Y")))
        def result = liveScoreService.getScoreBoard()

        then:
        result.matchList().size() == 3
        assertMatch(result.matchList()[0], "E", "F", 0, 0, 100)
        assertMatch(result.matchList()[1], "C", "D", 0, 0, 10)
        assertMatch(result.matchList()[2], "A", "B", 0, 0, 1)
    }

    def "should start time be updated when create is called after update"() {
        given:
        generateStartDashBoard()

        when:
        updateMatch("X", "Y", 2, 1)
        addNewMatch("X", "Y", 999)
        def result = liveScoreService.getScoreBoard()

        then:
        result.matchList().size() == 4
        assertMatch(result.matchList()[0], "X", "Y", 2, 1, 999)
        assertMatch(result.matchList()[1], "E", "F", 0, 0, 100)
        assertMatch(result.matchList()[2], "C", "D", 0, 0, 10)
        assertMatch(result.matchList()[3], "A", "B", 0, 0, 1)
    }

    private void assertMatch(Match match, String homeTeam, String awayTeam, int homeTeamScore, int awayTeamScore, long startTimeInEpochMillis) {
        assert match.getHomeTeam() == Team.of(homeTeam)
        assert match.getAwayTeam() == Team.of(awayTeam)
        assert match.getHomeTeamScore() == Score.of(homeTeamScore)
        assert match.getAwayTeamScore() == Score.of(awayTeamScore)
        assert match.getStartTimeInEpochMillis() == startTimeInEpochMillis
    }


    private void generateStartDashBoard() {
        addNewMatch("A", "B", 1)
        addNewMatch("E", "F", 100)
        addNewMatch("C", "D", 10)
    }

    private void generateUpdatedDashBoard() {
        generateStartDashBoard()
        addNewMatch("G", "H", 200)
        addNewMatch("I", "J", 300)

        updateMatch("C", "D", 5, 5)
        updateMatch("A", "B", 1, 0)
        // E-F not updated
        updateMatch("G", "H", 3, 1)
        updateMatch("I", "J", 2, 0)

        updateMatch("A", "B", 5, 4)
    }

    private void addNewMatch(String homeTeam, String awayTeam, long startTimeInEpochMillis) {
        liveScoreService.startNewMatch(NewMatchRequest.of(Team.of(homeTeam), Team.of(awayTeam), startTimeInEpochMillis))
    }

    private void updateMatch(String homeTeam, String awayTeam, int homeTeamScore, int awayTeamScore) {
        liveScoreService.updateMatch(
                UpdateMatchRequest.builder()
                        .homeTeam(Team.of(homeTeam))
                        .awayTeam(Team.of(awayTeam))
                        .homeTeamScore(Score.of(homeTeamScore))
                        .awayTeamScore(Score.of(awayTeamScore))
                        .build())
    }
}

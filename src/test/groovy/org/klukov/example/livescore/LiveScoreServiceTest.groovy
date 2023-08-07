package org.klukov.example.livescore

import org.klukov.example.livescore.dto.FinishMatchRequest
import org.klukov.example.livescore.dto.Match
import org.klukov.example.livescore.dto.NewMatchRequest
import org.klukov.example.livescore.dto.Score
import org.klukov.example.livescore.dto.Team
import org.klukov.example.livescore.dto.UpdateMatchRequest
import spock.lang.Specification

class LiveScoreServiceTest extends Specification {

    private LiveScoreService liveScoreService

    def setup() {
        liveScoreService = new LiveScoreService()
    }

    def "should generate start dashboard and all matches should be visible"() {
        given:
        generateStartDashBoard()

        when:
        def result = liveScoreService.getScoreBoard()

        then:
        result.matchList().size() == 3
        assertMatch(result.matchList()[0], "E", "F", 0, 0)
        assertMatch(result.matchList()[1], "C", "D", 0, 0)
        assertMatch(result.matchList()[2], "A", "B", 0, 0)
    }

    def "should add new match and this match should be visible on dashboard"() {
        given:
        generateStartDashBoard()

        when:
        addNewMatch("X", "Y")
        def result = liveScoreService.getScoreBoard()

        then:
        result.matchList().size() == 4
        assertMatch(result.matchList()[0], "X", "Y", 0, 0)
        assertMatch(result.matchList()[1], "E", "F", 0, 0)
        assertMatch(result.matchList()[2], "C", "D", 0, 0)
        assertMatch(result.matchList()[3], "A", "B", 0, 0)
    }

    def "should update existing match and result should be visible on dashboard"() {
        given:
        generateStartDashBoard()

        when:
        addNewMatch("X", "Y")
        def result = liveScoreService.getScoreBoard()

        then:
        result.matchList().size() == 4
        assertMatch(result.matchList()[0], "X", "Y", 0, 0)
        assertMatch(result.matchList()[1], "E", "F", 0, 0)
        assertMatch(result.matchList()[2], "C", "D", 0, 0)
        assertMatch(result.matchList()[3], "A", "B", 0, 0)
    }

    def "should update many matches and result should be visible on dashboard and result should be ordered"() {
        given:
        generateUpdatedDashBoard()

        when:
        def result = liveScoreService.getScoreBoard()

        then:
        result.matchList().size() == 5
        assertMatch(result.matchList()[0], "C", "D", 5, 5)
        assertMatch(result.matchList()[1], "A", "B", 5, 4)
        assertMatch(result.matchList()[2], "G", "H", 3, 1)
        assertMatch(result.matchList()[3], "I", "J", 2, 0)
        assertMatch(result.matchList()[4], "E", "F", 0, 0)
    }

    def "should finish existing match and match should disappear from dashboard"() {
        given:
        generateStartDashBoard()

        when:
        def result = liveScoreService.finishMatch(FinishMatchRequest.of(Team.of("C"), Team.of("D")))

        then:
        result.matchList().size() == 2
        assertMatch(result.matchList()[0], "E", "F", 0, 0)
        assertMatch(result.matchList()[1], "A", "B", 0, 0)
    }

    def "should add new match when match does not exist and update is called"() {
        given:
        generateStartDashBoard()

        when:
        updateMatch("X", "Y", 2, 1)
        def result = liveScoreService.getScoreBoard()

        then:
        result.matchList().size() == 4
        assertMatch(result.matchList()[0], "X", "Y", 2, 1)
        assertMatch(result.matchList()[1], "E", "F", 0, 0)
        assertMatch(result.matchList()[2], "C", "D", 0, 0)
        assertMatch(result.matchList()[3], "A", "B", 0, 0)
    }

    def "should nothing happen when new match call happen and match already existed on dashboard"() {
        given:
        generateUpdatedDashBoard()

        when:
        addNewMatch("G", "H")
        def result = liveScoreService.getScoreBoard()

        then:
        result.matchList().size() == 5
        assertMatch(result.matchList()[0], "C", "D", 5, 5)
        assertMatch(result.matchList()[1], "A", "B", 5, 4)
        assertMatch(result.matchList()[2], "G", "H", 3, 1)
        assertMatch(result.matchList()[3], "I", "J", 2, 0)
        assertMatch(result.matchList()[4], "E", "F", 0, 0)
    }

    def "should nothing happen when match does not exists and finish match is called"() {
        given:
        generateStartDashBoard()

        when:
        def result = liveScoreService.finishMatch(FinishMatchRequest.of(Team.of("X"), Team.of("Y")))

        then:
        result.matchList().size() == 3
        assertMatch(result.matchList()[0], "E", "F", 0, 0)
        assertMatch(result.matchList()[1], "C", "D", 0, 0)
        assertMatch(result.matchList()[2], "A", "B", 0, 0)
    }

    private void assertMatch(Match match, String homeTeam, String awayTeam, int homeTeamScore, int awayTeamScore) {
        assert match.homeTeam() == Team.of(homeTeam)
        assert match.awayTeam() == Team.of(awayTeam)
        assert match.homeTeamScore() == Score.of(homeTeamScore)
        assert match.awayTeamScore() == Score.of(awayTeamScore)
    }


    private void generateStartDashBoard() {
        addNewMatch("A", "B")
        addNewMatch("C", "D")
        addNewMatch("E", "F")
    }

    private void generateUpdatedDashBoard() {
        generateStartDashBoard()
        addNewMatch("G", "H")
        addNewMatch("I", "J")

        updateMatch("C", "D", 5, 5)
        updateMatch("A", "B", 1, 0)
        // E-F not updated
        updateMatch()
        updateMatch("G", "H", 3, 1)
        updateMatch("I", "J", 2, 0)

        // A-B updated at the end
        updateMatch("A", "B", 5, 4)
    }

    private void addNewMatch(String homeTeam, String awayTeam) {
        liveScoreService.startNewMatch(NewMatchRequest.of(Team.of(homeTeam), Team.of(awayTeam)))
    }

    private void updateMatch(String homeTeam, String awayTeam, int homeTeamScore, int awayTeamScore) {
        liveScoreService.updateMatch(
                UpdateMatchRequest.builder()
                        .homeTeam(Team.of(homeTeam))
                        .awayTeam(Team.of(awayTeam))
                        .homeTeamScore(Score.of(homeTeamScore))
                        .awayTeamScore(Score.of(awayTeamScore)))
    }
}

package org.klukov.example.livescore

import org.klukov.example.livescore.dto.NewMatchRequest
import org.klukov.example.livescore.dto.Team
import spock.lang.Specification

class LiveScoreServiceTest extends Specification {

    private LiveScoreService liveScoreService

    def "should generate start dashboard and all matches should be visible"() {
        when:
        false

        then:
        false
    }

    def "should add new match and this match should be visible on dashboard"() {
        when:
        false

        then:
        false
    }

    def "should update existing match and result should be visible on dashboard"() {
        when:
        false

        then:
        false
    }

    def "should finish existing match and match should disappear from dashboard"() {
        when:
        false

        then:
        false
    }

    def "should add new match when match does not exist and update is called"() {
        when:
        false

        then:
        false
    }

    def "should nothing happen when new match call happen and match already existed on dashboard"() {
        when:
        false

        then:
        false
    }

    def "should nothing happen when match does not exists and finish match is called"() {

    }


    def generateStartDashBoard() {
        addNewMatch("A", "B")
        addNewMatch("C", "D")
        addNewMatch("E", "F")
    }

    def addNewMatch(String homeTeam, String awayTeam) {
        liveScoreService.startNewMatch(NewMatchRequest.of(Team.of(homeTeam), Team.of(awayTeam)))
    }
}

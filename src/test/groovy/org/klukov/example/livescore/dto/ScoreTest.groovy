package org.klukov.example.livescore.dto

import spock.lang.Specification

class ScoreTest extends Specification {

    def "should create score with value"() {
        when:
        Score.of(value)

        then:
        noExceptionThrown()

        where:
        value             || _
        0                 || _
        1                 || _
        99                || _
        Integer.MAX_VALUE || _
    }

    def "should throw exception when invalid value is inserted to score"() {
        when:
        Score.of(value)

        then:
        thrown(IllegalArgumentException.class)

        where:
        value             || _
        -1                || _
        -11               || _
        Integer.MIN_VALUE || _
    }
}

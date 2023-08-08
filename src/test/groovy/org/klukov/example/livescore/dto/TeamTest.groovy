package org.klukov.example.livescore.dto

import spock.lang.Specification

class TeamTest extends Specification {

    def "should create team with name"() {
        when:
        Team.of(teamName)

        then:
        noExceptionThrown()

        where:
        teamName                 || _
        "A"                      || _
        "123"                    || _
        "!@#\$%^&*()_+[];',./\\" || _
    }

    def "should throw exception when invalid teamName is inserted"() {
        when:
        Team.of(teamName)

        then:
        thrown(IllegalArgumentException.class)

        where:
        teamName || _
        null     || _
        ""       || _
        "  "     || _
        "\n"     || _
        "\t"     || _
    }
}

package org.klukov.example.livescore.dto;

public record Team(String name) {

    public Team {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Team name cannot be empty");
        }
    }

    public static Team of(String name) {
        return new Team(name);
    }
}

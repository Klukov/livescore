package org.klukov.example.livescore.dto;

public record Team(String name) {
    public static Team of(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Team name cannot be empty");
        }
        return new Team(name);
    }
}

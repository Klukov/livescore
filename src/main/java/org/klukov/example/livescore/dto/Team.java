package org.klukov.example.livescore.dto;

public record Team(String name) {
    public static Team of(String name) {
        return new Team(name);
    }
}

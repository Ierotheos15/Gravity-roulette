package io.github.ierotheos15.gravityroulette;

public enum GravityDirection {
    DOWN, UP, NORTH, SOUTH, EAST, WEST;

    public String getEmoji() {
        return switch (this) {
            case DOWN  -> "⬇ DOWN";
            case UP    -> "⬆ UP";
            case NORTH -> "⬅ NORTH";
            case SOUTH -> "➡ SOUTH";
            case EAST  -> "↗ EAST";
            case WEST  -> "↙ WEST";
        };
    }
}
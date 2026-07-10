package io.github.ierotheos15.gravityroulette;

public class GravityState {
    private static GravityDirection current = GravityDirection.DOWN;

    public static GravityDirection getCurrent() {
        return current;
    }

    public static void setCurrent(GravityDirection direction) {
        current = direction;
    }
}

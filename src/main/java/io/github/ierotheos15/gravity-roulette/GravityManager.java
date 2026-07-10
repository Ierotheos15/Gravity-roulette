package io.github.ierotheos15.gravityroulette;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.Arrays;
import java.util.Random;

public class GravityManager {
    private static final Random RANDOM = new Random();
    private static final int SPIN_INTERVAL_TICKS = 20 * 60; // 1 minute
    private static final int HEADSTART_TICKS = 20 * 60; // 1 minute headstart
    private static int tickCounter = -HEADSTART_TICKS;

    public static void onServerTick(MinecraftServer server) {
        tickCounter++;

        if (tickCounter <= 0) {
            int secondsLeft = (-tickCounter / 20) + 1;
            sendActionBar(server, "⚠ Gravity roulette starts in §c" + secondsLeft + "s");
            return;
        }

        if (tickCounter % SPIN_INTERVAL_TICKS == 1) {
            spin(server);
        }

        int ticksLeft = SPIN_INTERVAL_TICKS - (tickCounter % SPIN_INTERVAL_TICKS);
        int secondsLeft = (ticksLeft / 20) + 1;
        int minutes = secondsLeft / 60;
        int seconds = secondsLeft % 60;
        sendActionBar(server, "🎲 Next spin in §e" + minutes + ":" + String.format("%02d", seconds)
                + " §7| Current: §b" + GravityState.getCurrent().getEmoji());
    }

    public static void resetHeadstart() {
        tickCounter = -HEADSTART_TICKS;
        GravityState.setCurrent(GravityDirection.DOWN);
    }

    private static void spin(MinecraftServer server) {
        GravityDirection[] all = GravityDirection.values();
        GravityDirection current = GravityState.getCurrent();

        // Filter out current direction so it never repeats
        GravityDirection[] options = Arrays.stream(all)
                .filter(d -> d != current)
                .toArray(GravityDirection[]::new);

        GravityDirection newDir = options[RANDOM.nextInt(options.length)];
        GravityState.setCurrent(newDir);

        Component title    = Component.literal("🎲 GRAVITY ROULETTE!");
        Component subtitle = Component.literal(newDir.getEmoji());

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            player.connection.send(new ClientboundSetTitleTextPacket(title));
            player.connection.send(new ClientboundSetSubtitleTextPacket(subtitle));
        }
    }

    private static void sendActionBar(MinecraftServer server, String text) {
        Component msg = Component.literal(text);
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            player.connection.send(new ClientboundSetActionBarTextPacket(msg));
        }
    }
}
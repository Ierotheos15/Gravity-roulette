package io.github.ierotheos15.gravityroulette;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class Main implements ModInitializer {
	@Override
	public void onInitialize() {
		ServerTickEvents.END_SERVER_TICK.register(GravityManager::onServerTick);

		ServerTickEvents.END_SERVER_TICK.register(server -> {
			for (ServerPlayer player : server.getPlayerList().getPlayers()) {
				GravityPlayerTick.onPlayerTick(player);
			}
			for (ServerLevel level : server.getAllLevels()) {
				for (Entity entity : level.getAllEntities()) {
					if (entity == null) continue;
					if (entity instanceof Player) continue;
					GravityPlayerTick.onEntityTick(entity);
				}
			}
		});

		ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
			if (entity instanceof ServerPlayer) {
				GravityManager.resetHeadstart();
			}
		});

		System.out.println("[GravityRoulette] Mod loaded! Prepare for chaos.");
	}
}
package de.nr137.lib.permission;

import java.util.function.BooleanSupplier;
import net.minecraft.server.level.ServerPlayer;
import de.nr137.lib.LibPlatform;

/**
 * A permission node, {@code <mod_id>.<name>} (e.g. player_titles.use), managed by permission mods through Fabric's or
 * NeoForge's permission API. Without a permission mod: everyone, or operators (permission level 2), as
 * {@code forEveryone} says at that moment (mods read it from their config). Make them with {@link Permissions#register}.
 */
public record Permission(String modId, String name, BooleanSupplier forEveryone) {
	public String node() {
		return modId + "." + name;
	}

	public boolean check(ServerPlayer player) {
		return LibPlatform.get().hasPermission(player, this);
	}
}

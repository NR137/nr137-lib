package de.nr137.lib.fabric;

import java.nio.file.Path;
import net.fabricmc.fabric.api.permission.v1.PermissionContextOwner;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.PermissionLevel;
import de.nr137.lib.LibPlatform;
import de.nr137.lib.permission.Permission;

/** Fabric: Fabric API's permission API, node {@code <mod_id>:<name>}; permission mods answer it. */
public final class FabricLibPlatform implements LibPlatform {
	@Override
	public Path configDir() {
		return FabricLoader.getInstance().getConfigDir();
	}

	@Override
	public String modVersion(String modId) {
		return FabricLoader.getInstance().getModContainer(modId)
			.map(container -> container.getMetadata().getVersion().getFriendlyString()).orElse("unknown");
	}

	@Override
	public boolean hasPermission(ServerPlayer player, Permission permission) {
		PermissionContextOwner owner = (PermissionContextOwner) player;
		Identifier node = Identifier.fromNamespaceAndPath(permission.modId(), permission.name());
		return permission.forEveryone().getAsBoolean() ? owner.checkPermission(node, true)
			: owner.checkPermission(node, PermissionLevel.GAMEMASTERS);
	}
}

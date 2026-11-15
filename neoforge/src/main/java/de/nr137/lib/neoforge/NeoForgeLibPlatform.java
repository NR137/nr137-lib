package de.nr137.lib.neoforge;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.server.permission.PermissionAPI;
import net.neoforged.neoforge.server.permission.events.PermissionGatherEvent;
import net.neoforged.neoforge.server.permission.nodes.PermissionNode;
import net.neoforged.neoforge.server.permission.nodes.PermissionTypes;
import de.nr137.lib.LibPlatform;
import de.nr137.lib.permission.Permission;

/** NeoForge: its PermissionAPI, node {@code <mod_id>.<name>}; the nodes are registered when the server starts. */
public final class NeoForgeLibPlatform implements LibPlatform {
	private static final Map<String, PermissionNode<Boolean>> NODES = new ConcurrentHashMap<>();

	static void registerNodes(PermissionGatherEvent.Nodes event) {
		for (Permission permission : de.nr137.lib.permission.Permissions.all()) {
			PermissionNode<Boolean> node = new PermissionNode<>(permission.modId(), permission.name(), PermissionTypes.BOOLEAN,
				(player, uuid, context) -> player != null && (permission.forEveryone().getAsBoolean()
					|| player.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER)));
			NODES.put(permission.node(), node);
			event.addNodes(node);
		}
	}

	@Override
	public Path configDir() {
		return FMLPaths.CONFIGDIR.get();
	}

	@Override
	public String modVersion(String modId) {
		return ModList.get().getModContainerById(modId).map(container -> container.getModInfo().getVersion().toString())
			.orElse("unknown");
	}

	@Override
	public boolean hasPermission(ServerPlayer player, Permission permission) {
		PermissionNode<Boolean> node = NODES.get(permission.node());
		if (node == null) {
			// Registered after the server started: only the default.
			return permission.forEveryone().getAsBoolean() || player.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER);
		}
		return PermissionAPI.getPermission(player, node);
	}
}

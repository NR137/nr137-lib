package de.nr137.lib;

import java.nio.file.Path;
import net.minecraft.server.level.ServerPlayer;
import de.nr137.lib.permission.Permission;

/**
 * What only the loader knows. fabric/ and neoforge/ each implement it; found by the loader that is present, so it
 * works no matter in which order mods start.
 */
public interface LibPlatform {
	Path configDir();

	String modVersion(String modId);

	/** Asks the loader's permission API (permission mods like LuckPerms answer it); without one, the default. */
	boolean hasPermission(ServerPlayer player, Permission permission);

	static LibPlatform get() {
		return Holder.INSTANCE;
	}

	final class Holder {
		private static final LibPlatform INSTANCE = load();

		private Holder() {
		}

		private static LibPlatform load() {
			String name = classExists("net.fabricmc.loader.api.FabricLoader") ? "de.nr137.lib.fabric.FabricLibPlatform"
				: "de.nr137.lib.neoforge.NeoForgeLibPlatform";
			try {
				return (LibPlatform) Class.forName(name).getDeclaredConstructor().newInstance();
			} catch (ReflectiveOperationException e) {
				throw new IllegalStateException("NR137 Lib has no platform for this loader (" + name + ")", e);
			}
		}

		private static boolean classExists(String name) {
			try {
				Class.forName(name, false, LibPlatform.class.getClassLoader());
				return true;
			} catch (ClassNotFoundException e) {
				return false;
			}
		}
	}
}

package de.nr137.lib;

import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * NR137 Lib: what NR137's server-side mods share (permissions, config files, server-side translations, chest screens,
 * text widths). Packed into each mod's JAR; the loaders load the newest copy once.
 */
public final class Nr137Lib {
	public static final String MOD_ID = "nr137_lib";
	public static final String MOD_NAME = "NR137 Lib";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

	private Nr137Lib() {
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}

	public static void onServerStarted(MinecraftServer server) {
		// The smoke test waits for this line (scripts/smoke_test/smoke_test.py).
		LOGGER.info("{} {} is running on Minecraft {}", MOD_NAME, LibPlatform.get().modVersion(MOD_ID),
			LibPlatform.get().modVersion("minecraft"));
	}
}

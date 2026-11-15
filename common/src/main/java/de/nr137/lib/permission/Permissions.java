package de.nr137.lib.permission;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BooleanSupplier;

/** All permissions the mods registered; NeoForge needs them before the server starts (when mods initialise). */
public final class Permissions {
	private static final List<Permission> ALL = new CopyOnWriteArrayList<>();

	private Permissions() {
	}

	/** Registers a permission; call while the mod initialises. */
	public static Permission register(String modId, String name, BooleanSupplier forEveryone) {
		Permission permission = new Permission(modId, name, forEveryone);
		ALL.add(permission);
		return permission;
	}

	public static List<Permission> all() {
		return List.copyOf(ALL);
	}
}

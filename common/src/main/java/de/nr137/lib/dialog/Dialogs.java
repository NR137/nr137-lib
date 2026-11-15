package de.nr137.lib.dialog;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.dialog.Dialog;
import net.minecraft.server.level.ServerPlayer;

/**
 * Dialogs (the game's own screens with text fields, options and buttons, which vanilla clients show) sent by the server
 * inline, without a data pack. A button with a {@code CustomAll} action of a registered id sends the dialog's inputs
 * back; the handler gets the player and those inputs (key → value) on the server thread.
 */
public final class Dialogs {
	private static final Map<Identifier, BiConsumer<ServerPlayer, CompoundTag>> HANDLERS = new HashMap<>();

	private Dialogs() {
	}

	public static void register(Identifier id, BiConsumer<ServerPlayer, CompoundTag> handler) {
		HANDLERS.put(id, handler);
	}

	public static void open(ServerPlayer player, Dialog dialog) {
		player.openDialog(Holder.direct(dialog));
	}

	public static boolean handles(Identifier id) {
		return HANDLERS.containsKey(id);
	}

	/** A button of one of our dialogs was pressed (the library's mixin, on the server thread). */
	public static void handle(ServerPlayer player, Identifier id, Tag payload) {
		BiConsumer<ServerPlayer, CompoundTag> handler = HANDLERS.get(id);
		if (handler != null) {
			handler.accept(player, payload instanceof CompoundTag compound ? compound : new CompoundTag());
		}
	}
}

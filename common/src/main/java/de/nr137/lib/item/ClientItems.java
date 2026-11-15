package de.nr137.lib.item;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

/**
 * Items as clients see them: mods change a stack only in the packets (a name, lore, a glint), so what is saved stays a
 * vanilla item and removing the mod leaves nothing behind. Every item stack the server sends goes through the
 * registered changes (the library's ItemStackMixin), and the client's slot hashes are checked against the changed
 * stack (RemoteSlotMixin; otherwise every click sends the slots again).
 * <p>
 * A change learns which player the stack is for (to write in their language): the one whose packet is being written
 * (PacketEncoderMixin), or whose screen is being compared (AbstractContainerMenuMixin); null where nobody is known.
 */
public final class ClientItems {
	private static final List<BiFunction<ItemStack, @Nullable ServerPlayer, ItemStack>> CHANGES = new CopyOnWriteArrayList<>();
	private static final ThreadLocal<@Nullable ServerPlayer> VIEWER = new ThreadLocal<>();
	/** Each open screen's player (the game's menus don't know theirs). */
	private static final Map<AbstractContainerMenu, ServerPlayer> MENUS = java.util.Collections.synchronizedMap(new WeakHashMap<>());

	private ClientItems() {
	}

	/** A change: returns the stack itself when it doesn't apply, else a changed copy (never changes the stack). */
	public static void register(UnaryOperator<ItemStack> change) {
		CHANGES.add((stack, viewer) -> change.apply(stack));
	}

	/** A change that depends on the player it is for (null if unknown). */
	public static void register(BiFunction<ItemStack, @Nullable ServerPlayer, ItemStack> change) {
		CHANGES.add(change);
	}

	public static ItemStack forClient(ItemStack stack) {
		ItemStack result = stack;
		ServerPlayer viewer = VIEWER.get();
		for (BiFunction<ItemStack, @Nullable ServerPlayer, ItemStack> change : CHANGES) {
			result = change.apply(result, viewer);
		}
		return result;
	}

	/** Who the stacks being written or compared now are for (the library's mixins); null when done. */
	public static void setViewer(@Nullable ServerPlayer viewer) {
		if (viewer == null) {
			VIEWER.remove();
		} else {
			VIEWER.set(viewer);
		}
	}

	public static void openedBy(AbstractContainerMenu menu, ServerPlayer player) {
		MENUS.put(menu, player);
	}

	public static @Nullable ServerPlayer playerOf(AbstractContainerMenu menu) {
		return MENUS.get(menu);
	}
}

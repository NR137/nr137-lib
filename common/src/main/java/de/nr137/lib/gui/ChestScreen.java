package de.nr137.lib.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

/**
 * A screen made of a chest (1 to 6 rows) whose items are buttons: vanilla clients show it as a chest, nothing can be
 * taken out or put in, clicks go to the handlers. Build it, then {@link #open}. Redraw by setting the slots again.
 */
public final class ChestScreen {
	/** What a click was: the slot, left or right button, with shift. */
	public record Click(ServerPlayer player, int slot, boolean right, boolean shift) {
	}

	private final int rows;
	private final SimpleContainer slots;
	private final Map<Integer, Consumer<Click>> handlers = new HashMap<>();
	private Component title;

	public ChestScreen(int rows, Component title) {
		if (rows < 1 || rows > 6) {
			throw new IllegalArgumentException("A chest screen has 1 to 6 rows, not " + rows);
		}
		this.rows = rows;
		this.slots = new SimpleContainer(rows * 9);
		this.title = title;
	}

	public int size() {
		return rows * 9;
	}

	/** Puts an item into a slot; clicking it calls {@code onClick} (null: nothing happens). */
	public ChestScreen set(int slot, ItemStack item, Consumer<Click> onClick) {
		slots.setItem(slot, item);
		if (onClick == null) {
			handlers.remove(slot);
		} else {
			handlers.put(slot, onClick);
		}
		return this;
	}

	public ChestScreen clear() {
		slots.clearContent();
		handlers.clear();
		return this;
	}

	public ChestScreen title(Component newTitle) {
		this.title = newTitle;
		return this;
	}

	public void open(ServerPlayer player) {
		player.openMenu(new SimpleMenuProvider((id, inventory, opener) -> new Menu(this, id, inventory), title));
	}

	private MenuType<ChestMenu> type() {
		return switch (rows) {
			case 1 -> MenuType.GENERIC_9x1;
			case 2 -> MenuType.GENERIC_9x2;
			case 3 -> MenuType.GENERIC_9x3;
			case 4 -> MenuType.GENERIC_9x4;
			case 5 -> MenuType.GENERIC_9x5;
			default -> MenuType.GENERIC_9x6;
		};
	}

	private static final class Menu extends ChestMenu {
		private final ChestScreen screen;

		Menu(ChestScreen screen, int id, net.minecraft.world.entity.player.Inventory inventory) {
			super(screen.type(), id, inventory, screen.slots, screen.rows);
			this.screen = screen;
		}

		@Override
		public void clicked(int slotIndex, int button, ContainerInput input, Player player) {
			// Nothing moves: every click is only a button press, then the client is told the slots again.
			if (player instanceof ServerPlayer serverPlayer && slotIndex >= 0 && slotIndex < screen.size()
					&& (input == ContainerInput.PICKUP || input == ContainerInput.QUICK_MOVE)) {
				Consumer<Click> handler = screen.handlers.get(slotIndex);
				if (handler != null) {
					handler.accept(new Click(serverPlayer, slotIndex, button == 1, input == ContainerInput.QUICK_MOVE));
				}
			}
			sendAllDataToRemote();
		}

		@Override
		public ItemStack quickMoveStack(Player player, int slot) {
			return ItemStack.EMPTY;
		}

		@Override
		public boolean stillValid(Player player) {
			return true;
		}
	}
}

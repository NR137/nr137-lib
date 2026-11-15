package de.nr137.lib.gametest;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import de.nr137.lib.gui.ChestScreen;

/**
 * Game tests on a real server, shared by both loaders: fabric/src/gametest and neoforge/src/gametest register each
 * entry. Run with ./tasks.sh gametest.
 */
public final class GameTests {
	private GameTests() {
	}

	/** Test name -> test, in the order they run. */
	public static Map<String, Consumer<GameTestHelper>> all() {
		Map<String, Consumer<GameTestHelper>> tests = new LinkedHashMap<>();
		tests.put("chest_screen_buttons_click_and_nothing_moves", GameTests::chestScreen);
		return tests;
	}

	private static void chestScreen(GameTestHelper helper) {
		ServerPlayer player = helper.makeMockServerPlayerInLevel();
		AtomicInteger clicks = new AtomicInteger();
		ChestScreen screen = new ChestScreen(3, Component.literal("Test"))
			.set(4, new ItemStack(Items.DIAMOND), click -> clicks.incrementAndGet());
		screen.open(player);
		var menu = player.containerMenu;
		menu.clicked(4, 0, ContainerInput.PICKUP, player);
		menu.clicked(4, 0, ContainerInput.QUICK_MOVE, player);
		menu.clicked(4, 0, ContainerInput.THROW, player);
		helper.assertValueEqual(clicks.get(), 2, "button clicks (pick up and shift-click, not throw)");
		helper.assertTrue(menu.getSlot(4).getItem().is(Items.DIAMOND), "the button stays in its slot");
		helper.assertTrue(menu.getCarried().isEmpty(), "nothing on the cursor");
		helper.assertTrue(player.getInventory().getNonEquipmentItems().stream().noneMatch(stack -> stack.is(Items.DIAMOND)),
			"nothing in the player's inventory");
		player.closeContainer();
		helper.getLevel().getServer().getPlayerList().remove(player);
		helper.succeed();
	}
}

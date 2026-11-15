package de.nr137.lib.fabric.gametest;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import de.nr137.lib.gametest.GameTests;

/** Registers the shared {@link GameTests} with Fabric's game test API, one method per test. */
public class FabricGameTests {
	private static final int MAX_TICKS = 200;

	@GameTest(maxTicks = MAX_TICKS)
	public void chestScreenButtonsClickAndNothingMoves(GameTestHelper helper) {
		GameTests.all().get("chest_screen_buttons_click_and_nothing_moves").accept(helper);
	}
}

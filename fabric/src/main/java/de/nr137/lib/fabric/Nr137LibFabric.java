package de.nr137.lib.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import de.nr137.lib.Nr137Lib;

public class Nr137LibFabric implements ModInitializer {
	@Override
	public void onInitialize() {
		ServerLifecycleEvents.SERVER_STARTED.register(Nr137Lib::onServerStarted);
	}
}

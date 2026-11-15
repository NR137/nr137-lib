package de.nr137.lib.neoforge;

import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.server.permission.events.PermissionGatherEvent;
import de.nr137.lib.Nr137Lib;

@Mod(Nr137Lib.MOD_ID)
public class Nr137LibNeoForge {
	public Nr137LibNeoForge() {
		NeoForge.EVENT_BUS.addListener(ServerStartedEvent.class, event -> Nr137Lib.onServerStarted(event.getServer()));
		NeoForge.EVENT_BUS.addListener(PermissionGatherEvent.Nodes.class, NeoForgeLibPlatform::registerNodes);
	}
}

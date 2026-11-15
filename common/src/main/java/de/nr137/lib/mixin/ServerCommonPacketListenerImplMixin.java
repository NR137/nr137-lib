package de.nr137.lib.mixin;

import net.minecraft.network.protocol.common.ServerboundCustomClickActionPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import de.nr137.lib.dialog.Dialogs;

/**
 * Dialog buttons of the library's mods, with the player who pressed them (the game only passes the id on). At the start
 * of the method, because NeoForge changes the rest; the handlers run on the server thread.
 */
@Mixin(ServerCommonPacketListenerImpl.class)
abstract class ServerCommonPacketListenerImplMixin {
	@Shadow
	@Final
	protected MinecraftServer server;

	@Inject(method = "handleCustomClickAction", at = @At("HEAD"), cancellable = true)
	private void nr137Lib$dialog(ServerboundCustomClickActionPacket packet, CallbackInfo ci) {
		if ((Object) this instanceof ServerGamePacketListenerImpl game && Dialogs.handles(packet.id())) {
			ci.cancel();
			server.execute(() -> {
				if (game.player != null) {
					Dialogs.handle(game.player, packet.id(), packet.payload().orElse(null));
				}
			});
		}
	}
}

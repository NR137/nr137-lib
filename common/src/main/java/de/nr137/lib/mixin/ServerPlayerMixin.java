package de.nr137.lib.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import de.nr137.lib.item.ClientItems;

/** Which player each screen belongs to (ClientItems): every screen a player opens goes through initMenu. */
@Mixin(ServerPlayer.class)
abstract class ServerPlayerMixin {
	@Inject(method = "initMenu", at = @At("HEAD"))
	private void nr137Lib$menu(AbstractContainerMenu menu, CallbackInfo ci) {
		ClientItems.openedBy(menu, (ServerPlayer) (Object) this);
	}
}

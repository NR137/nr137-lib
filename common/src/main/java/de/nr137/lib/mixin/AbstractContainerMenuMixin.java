package de.nr137.lib.mixin;

import java.util.function.Supplier;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import de.nr137.lib.item.ClientItems;

/** While a screen's slots are compared with what its player's client saw, ClientItems knows that player. */
@Mixin(AbstractContainerMenu.class)
abstract class AbstractContainerMenuMixin {
	@Inject(method = "synchronizeSlotToRemote", at = @At("HEAD"))
	private void nr137Lib$viewer(int slot, ItemStack current, Supplier<ItemStack> copy, CallbackInfo ci) {
		ClientItems.setViewer(ClientItems.playerOf((AbstractContainerMenu) (Object) this));
	}

	@Inject(method = "synchronizeSlotToRemote", at = @At("RETURN"))
	private void nr137Lib$done(int slot, ItemStack current, Supplier<ItemStack> copy, CallbackInfo ci) {
		ClientItems.setViewer(null);
	}

	@Inject(method = "synchronizeCarriedToRemote", at = @At("HEAD"))
	private void nr137Lib$carriedViewer(CallbackInfo ci) {
		ClientItems.setViewer(ClientItems.playerOf((AbstractContainerMenu) (Object) this));
	}

	@Inject(method = "synchronizeCarriedToRemote", at = @At("RETURN"))
	private void nr137Lib$carriedDone(CallbackInfo ci) {
		ClientItems.setViewer(null);
	}
}

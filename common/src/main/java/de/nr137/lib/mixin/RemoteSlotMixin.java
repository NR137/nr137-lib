package de.nr137.lib.mixin;

import net.minecraft.world.inventory.RemoteSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import de.nr137.lib.item.ClientItems;

/**
 * After a click, the client reports a hash of each slot as it sees it, that is with the changes of ClientItems. The
 * server compares that with its own stack; without this the two never match and every click sends the slots again.
 */
@Mixin(RemoteSlot.Synchronized.class)
abstract class RemoteSlotMixin {
	@ModifyArg(method = "matches", at = @At(value = "INVOKE",
		target = "Lnet/minecraft/network/HashedStack;matches(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/network/HashedPatchMap$HashGenerator;)Z"),
		index = 0)
	private ItemStack nr137Lib$asTheClientSeesIt(ItemStack local) {
		return ClientItems.forClient(local);
	}
}

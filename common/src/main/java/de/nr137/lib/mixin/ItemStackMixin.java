package de.nr137.lib.mixin;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import de.nr137.lib.item.ClientItems;

/** Item stacks sent over the network, as clients see them (ClientItems). Wraps the codecs vanilla builds for them. */
@Mixin(ItemStack.class)
abstract class ItemStackMixin {
	@Inject(method = "createOptionalStreamCodec", at = @At("RETURN"), cancellable = true)
	private static void nr137Lib$forClient(StreamCodec<RegistryFriendlyByteBuf, DataComponentPatch> patchCodec,
			CallbackInfoReturnable<StreamCodec<RegistryFriendlyByteBuf, ItemStack>> cir) {
		StreamCodec<RegistryFriendlyByteBuf, ItemStack> vanilla = cir.getReturnValue();
		cir.setReturnValue(new StreamCodec<>() {
			@Override
			public ItemStack decode(RegistryFriendlyByteBuf input) {
				return vanilla.decode(input);
			}

			@Override
			public void encode(RegistryFriendlyByteBuf output, ItemStack stack) {
				vanilla.encode(output, ClientItems.forClient(stack));
			}
		});
	}
}

package de.nr137.lib.mixin;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketEncoder;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import de.nr137.lib.item.ClientItems;

/** While a packet is written, ClientItems knows the player it is for (from the connection it goes through). */
@Mixin(PacketEncoder.class)
abstract class PacketEncoderMixin {
	@Inject(method = "encode(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/protocol/Packet;Lio/netty/buffer/ByteBuf;)V",
		at = @At("HEAD"))
	private void nr137Lib$viewer(ChannelHandlerContext ctx, Packet<?> packet, ByteBuf output, CallbackInfo ci) {
		ClientItems.setViewer(ctx.pipeline().get("packet_handler") instanceof Connection connection
			&& ((ConnectionAccessor) connection).nr137Lib$listener() instanceof ServerGamePacketListenerImpl game ? game.player : null);
	}

	@Inject(method = "encode(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/protocol/Packet;Lio/netty/buffer/ByteBuf;)V",
		at = @At("RETURN"))
	private void nr137Lib$done(ChannelHandlerContext ctx, Packet<?> packet, ByteBuf output, CallbackInfo ci) {
		ClientItems.setViewer(null);
	}
}

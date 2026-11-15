package de.nr137.lib.mixin;

import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** A connection's listener (the game has no getter): for the player a packet is written for (PacketEncoderMixin). */
@Mixin(Connection.class)
public interface ConnectionAccessor {
	@Accessor("packetListener")
	PacketListener nr137Lib$listener();
}

package de.knito.hushmortem.mixin;

import de.knito.hushmortem.SoundTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {
	/** Entity event id the server sends when a living entity dies. */
	private static final byte DEATH_EVENT = 3;

	@Shadow
	private ClientLevel level;

	// Hooked here rather than in LivingEntity.handleEntityEvent, because mobs override that
	// method and not all of them pass every event on to super.
	@Inject(
		method = "handleEntityEvent",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/network/PacketProcessor;)V", shift = At.Shift.AFTER)
	)
	private void hushmortem$onEntityEvent(ClientboundEntityEventPacket packet, CallbackInfo ci) {
		if (packet.getEventId() != DEATH_EVENT) {
			return;
		}
		Entity entity = packet.getEntity(this.level);
		if (entity instanceof LivingEntity living) {
			SoundTracker.onEntityDied(living);
		}
	}

	@Inject(method = {"handleLogin", "handleRespawn"}, at = @At("TAIL"))
	private void hushmortem$onWorldChange(CallbackInfo ci) {
		SoundTracker.clear();
	}
}

package de.knito.hushmortem.mixin;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntity.class)
public interface LivingEntityInvoker {
	@Invoker("getDeathSound")
	@Nullable SoundEvent hushmortem$getDeathSound();
}

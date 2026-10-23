package de.knito.hushmortem.mixin;

import de.knito.hushmortem.SoundTracker;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SoundEngine.class)
public class SoundEngineMixin {
	@Inject(method = "play", at = @At("RETURN"))
	private void hushmortem$trackStartedSound(SoundInstance instance, CallbackInfoReturnable<SoundEngine.PlayResult> cir) {
		if (cir.getReturnValue() != SoundEngine.PlayResult.NOT_STARTED) {
			SoundTracker.onSoundStarted(instance);
		}
	}
}

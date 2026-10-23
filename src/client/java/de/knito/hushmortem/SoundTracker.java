package de.knito.hushmortem;

import de.knito.hushmortem.mixin.EntityBoundSoundInstanceAccessor;
import de.knito.hushmortem.mixin.LivingEntityInvoker;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Remembers which mob started which sound, so the sounds can be stopped when that mob dies.
 * Everything here runs on the client main thread (sound playback and packet handling both do).
 */
public final class SoundTracker {
	/** How far outside its hit box a sound may start and still count as the mob's own. */
	private static final double ATTRIBUTION_MARGIN = 1.0;
	/** Prune finished sounds once the list grows past this. */
	private static final int PRUNE_THRESHOLD = 64;

	private record Tracked(int entityId, SoundInstance sound) {
	}

	private static final List<Tracked> TRACKED = new ArrayList<>();

	private SoundTracker() {
	}

	/** Called after the sound engine has started a sound. */
	public static void onSoundStarted(SoundInstance sound) {
		SoundSource source = sound.getSource();
		if (source != SoundSource.HOSTILE && source != SoundSource.NEUTRAL) {
			return;
		}

		LivingEntity owner = findOwner(sound);
		if (owner == null) {
			return;
		}

		// A mob stays in the world for about a second while its death animation plays.
		// Anything it starts in that time is cut right away.
		if (owner.isDeadOrDying() && !isKeptDeathSound((LivingEntity) owner, sound)) {
			Minecraft.getInstance().getSoundManager().stop(sound);
			return;
		}

		if (TRACKED.size() >= PRUNE_THRESHOLD) {
			prune();
		}
		TRACKED.add(new Tracked(owner.getId(), sound));
	}

	/** Called when the server tells us a mob died (entity event 3). */
	public static void onEntityDied(LivingEntity entity) {
		if (entity instanceof Player) {
			return;
		}

		SoundManager soundManager = Minecraft.getInstance().getSoundManager();
		int id = entity.getId();

		Iterator<Tracked> it = TRACKED.iterator();
		while (it.hasNext()) {
			Tracked tracked = it.next();
			if (tracked.entityId() != id) {
				continue;
			}
			if (isKeptDeathSound(entity, tracked.sound())) {
				continue;
			}
			soundManager.stop(tracked.sound());
			it.remove();
		}
	}

	private static boolean isKeptDeathSound(LivingEntity entity, SoundInstance sound) {
		if (!HushMortemConfig.keepDeathSound) {
			return false;
		}
		SoundEvent deathSound = ((LivingEntityInvoker) entity).hushmortem$getDeathSound();
		return deathSound != null && deathSound.location().equals(sound.getIdentifier());
	}

	private static LivingEntity findOwner(SoundInstance sound) {
		if (sound instanceof EntityBoundSoundInstance bound) {
			Entity entity = ((EntityBoundSoundInstanceAccessor) bound).hushmortem$getEntity();
			return entity instanceof LivingEntity living && !(entity instanceof Player) ? living : null;
		}

		ClientLevel level = Minecraft.getInstance().level;
		if (level == null || sound.isRelative()) {
			return null;
		}

		// Positional sounds carry no entity. Mobs play them at their feet or eyes, so pick the
		// nearest mob whose (slightly grown) hit box contains the sound's position.
		Vec3 pos = new Vec3(sound.getX(), sound.getY(), sound.getZ());
		AABB search = new AABB(pos, pos).inflate(4.0);
		LivingEntity best = null;
		double bestDist = Double.MAX_VALUE;
		for (LivingEntity candidate : level.getEntitiesOfClass(LivingEntity.class, search, e -> !(e instanceof Player))) {
			if (!candidate.getBoundingBox().inflate(ATTRIBUTION_MARGIN).contains(pos)) {
				continue;
			}
			double dist = candidate.getBoundingBox().getCenter().distanceToSqr(pos);
			if (dist < bestDist) {
				bestDist = dist;
				best = candidate;
			}
		}
		return best;
	}

	private static void prune() {
		SoundManager soundManager = Minecraft.getInstance().getSoundManager();
		TRACKED.removeIf(tracked -> !soundManager.isActive(tracked.sound()));
	}

	/** Forget everything, e.g. when the world changes. */
	public static void clear() {
		TRACKED.clear();
	}
}

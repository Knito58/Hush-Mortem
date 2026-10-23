package de.knito.hushmortem;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HushMortemClient implements ClientModInitializer {
	public static final String MOD_ID = "hushmortem";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
		HushMortemConfig.load();
		LOGGER.info("Hush Mortem loaded (keep_death_sound={})", HushMortemConfig.keepDeathSound);
	}
}

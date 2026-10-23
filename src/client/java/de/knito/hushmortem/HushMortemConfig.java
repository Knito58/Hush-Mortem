package de.knito.hushmortem;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import net.fabricmc.loader.api.FabricLoader;

/**
 * Plain properties file in config/hushmortem.properties, so no config library is needed.
 */
public final class HushMortemConfig {
	/** Let the mob's own death sound finish; only its other sounds are cut. */
	public static boolean keepDeathSound = true;

	private HushMortemConfig() {
	}

	public static void load() {
		Path file = FabricLoader.getInstance().getConfigDir().resolve(HushMortemClient.MOD_ID + ".properties");
		Properties props = new Properties();
		if (Files.exists(file)) {
			try (Reader reader = Files.newBufferedReader(file)) {
				props.load(reader);
			} catch (IOException e) {
				HushMortemClient.LOGGER.warn("Could not read {}, using defaults", file, e);
			}
		}

		keepDeathSound = Boolean.parseBoolean(props.getProperty("keep_death_sound", Boolean.toString(keepDeathSound)));

		props.setProperty("keep_death_sound", Boolean.toString(keepDeathSound));
		try (Writer writer = Files.newBufferedWriter(file)) {
			props.store(writer, "Hush Mortem: keep_death_sound=true lets the death sound play, false silences it too");
		} catch (IOException e) {
			HushMortemClient.LOGGER.warn("Could not write {}", file, e);
		}
	}
}

package de.nr137.lib.lang;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import de.nr137.lib.Nr137Lib;

/**
 * A mod's texts in the player's language, written by the server (vanilla clients don't have the mod's language files).
 * Read from the mod's {@code assets/<mod_id>/lang/<language>.json} (en_us, de_de), as the game's own language files;
 * missing languages and keys fall back to English, missing English to the key.
 */
public final class Translations {
	public static final String FALLBACK = "en_us";
	public static final List<String> LANGUAGES = List.of("en_us", "de_de");

	private final Map<String, Map<String, String>> byLanguage = new HashMap<>();

	private Translations() {
	}

	/** Loads a mod's language files through a class of that mod (its own JAR). */
	public static Translations load(Class<?> modClass, String modId) {
		Translations translations = new Translations();
		for (String language : LANGUAGES) {
			String path = "/assets/" + modId + "/lang/" + language + ".json";
			try (InputStream in = modClass.getResourceAsStream(path)) {
				if (in == null) {
					continue;
				}
				JsonObject json = JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
				Map<String, String> texts = new HashMap<>();
				json.entrySet().forEach(entry -> texts.put(entry.getKey(), entry.getValue().getAsString()));
				translations.byLanguage.put(language, texts);
			} catch (IOException | RuntimeException e) {
				Nr137Lib.LOGGER.error("Can't read {} of {}: {}", path, modId, e.toString());
			}
		}
		return translations;
	}

	/** The language a player's client uses, as one of ours (de_at, de_ch → de_de); English otherwise. */
	public static String languageOf(ServerPlayer player) {
		return supported(player.clientInformation().language());
	}

	public static String supported(String language) {
		return supported(language, FALLBACK);
	}

	/** One of our languages for a game language (de_at, de_ch → de_de), or {@code fallback} if there is none. */
	public static String supported(String language, String fallback) {
		String lower = language == null ? fallback : language.toLowerCase(Locale.ROOT);
		if (LANGUAGES.contains(lower)) {
			return lower;
		}
		String prefix = lower.contains("_") ? lower.substring(0, lower.indexOf('_')) : lower;
		return LANGUAGES.stream().filter(code -> code.startsWith(prefix + "_")).findFirst().orElse(fallback);
	}

	/** The text for a key in a language, with %s (and %1$s) filled in. */
	public String text(String language, String key, Object... args) {
		String pattern = byLanguage.getOrDefault(supported(language), Map.of()).get(key);
		if (pattern == null) {
			pattern = byLanguage.getOrDefault(FALLBACK, Map.of()).getOrDefault(key, key);
		}
		try {
			return args.length == 0 ? pattern : String.format(Locale.ROOT, pattern, args);
		} catch (java.util.IllegalFormatException e) {
			return pattern;
		}
	}

	public MutableComponent component(String language, String key, Object... args) {
		return Component.literal(text(language, key, args));
	}

	/** The text in the player's language. */
	public MutableComponent of(ServerPlayer player, String key, Object... args) {
		return component(languageOf(player), key, args);
	}
}

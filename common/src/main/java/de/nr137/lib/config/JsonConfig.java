package de.nr137.lib.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import de.nr137.lib.Nr137Lib;

/**
 * A config file in JSON for a record type. Options missing from the file keep their defaults (not Gson's false and 0),
 * objects are merged key by key, and the file is written again with every option, so new options show up in it.
 */
public final class JsonConfig {
	public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

	private JsonConfig() {
	}

	/** The config in {@code file}, or {@code defaults} (written to the file) if there is none or it can't be read. */
	public static <T> T load(Path file, Class<T> type, T defaults) {
		if (!Files.isRegularFile(file)) {
			save(file, defaults);
			return defaults;
		}
		try {
			JsonObject merged = GSON.toJsonTree(defaults).getAsJsonObject();
			JsonObject read = JsonParser.parseString(Files.readString(file)).getAsJsonObject();
			merge(merged, read);
			T config = GSON.fromJson(merged, type);
			if (!merged.equals(read)) {
				save(file, config);
			}
			return config;
		} catch (IOException | JsonParseException | IllegalStateException e) {
			Nr137Lib.LOGGER.error("Can't read {}, using the defaults: {}", file, e.toString());
			return defaults;
		}
	}

	private static void merge(JsonObject into, JsonObject from) {
		for (var entry : from.entrySet()) {
			JsonElement existing = into.get(entry.getKey());
			if (existing != null && existing.isJsonObject() && entry.getValue().isJsonObject()) {
				merge(existing.getAsJsonObject(), entry.getValue().getAsJsonObject());
			} else if (existing != null) {
				into.add(entry.getKey(), entry.getValue());
			}
		}
	}

	public static void save(Path file, Object config) {
		try {
			Files.createDirectories(file.getParent());
			Files.writeString(file, GSON.toJson(config) + "\n");
		} catch (IOException e) {
			Nr137Lib.LOGGER.warn("Can't save {}: {}", file, e.toString());
		}
	}
}

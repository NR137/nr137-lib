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
 * nested records are merged option by option, maps are taken from the file as they are (so entries can be added and
 * removed), and the file is written again with every option, so new options show up in it.
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
			merge(merged, read, type);
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

	/** Merges {@code from} into {@code into} (the defaults of {@code type}): records option by option, the rest whole. */
	private static void merge(JsonObject into, JsonObject from, java.lang.reflect.Type type) {
		Class<?> raw = com.google.gson.reflect.TypeToken.get(type).getRawType();
		if (!raw.isRecord()) {
			from.entrySet().forEach(entry -> into.add(entry.getKey(), entry.getValue()));
			return;
		}
		for (var component : raw.getRecordComponents()) {
			JsonElement value = from.get(component.getName());
			if (value == null) {
				continue;
			}
			JsonElement existing = into.get(component.getName());
			if (component.getType().isRecord() && existing != null && existing.isJsonObject() && value.isJsonObject()) {
				merge(existing.getAsJsonObject(), value.getAsJsonObject(), component.getGenericType());
			} else {
				into.add(component.getName(), value);
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

package de.nr137.lib;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import de.nr137.lib.config.JsonConfig;
import de.nr137.lib.lang.Translations;
import de.nr137.lib.text.TextWidth;

class LibTest {
	@Test
	void textsAreShortenedToFitAndKeepTheirSuffix() {
		assertEquals("Base · K7QF", TextWidth.fit("Base", " · K7QF", TextWidth.CONTAINER_TITLE));
		String fitted = TextWidth.fit("A very long name for my storage of everything", " · K7QF", TextWidth.CONTAINER_TITLE);
		assertTrue(fitted.endsWith("… · K7QF"), fitted);
		assertTrue(TextWidth.width(fitted) <= TextWidth.CONTAINER_TITLE, fitted);
	}

	@Test
	void longTextsWrapAtSpaces() {
		String text = "Took Honey from a Beehive over a Campfire without angering the Bees.";
		java.util.List<String> lines = TextWidth.wrap(text, TextWidth.TOOLTIP);
		assertTrue(lines.size() > 1, lines.toString());
		lines.forEach(line -> assertTrue(TextWidth.width(line) <= TextWidth.TOOLTIP, line));
		assertEquals(text, String.join(" ", lines), "no word is lost");
		assertEquals(java.util.List.of("Short."), TextWidth.wrap("Short.", TextWidth.TOOLTIP));
	}

	@Test
	void translationsUseThePlayersLanguageAndFallBackToEnglish() {
		Translations texts = Translations.load(LibTest.class, "nr137_lib_test");
		assertEquals("Hallo Steve", texts.text("de_de", "greeting", "Steve"));
		assertEquals("Hallo Steve", texts.text("de_AT", "greeting", "Steve"), "Austrian German is German");
		assertEquals("Hello Steve", texts.text("fr_fr", "greeting", "Steve"), "other languages: English");
		assertEquals("English", texts.text("de_de", "only.english"), "missing in German: English");
		assertEquals("missing.key", texts.text("de_de", "missing.key"), "missing everywhere: the key");
	}

	record Sample(int size, boolean on, Inner inner) {
		record Inner(String a, String b) {
		}
	}

	@Test
	void configsKeepDefaultsForMissingOptionsAndWriteThemIn(@TempDir Path folder) throws Exception {
		Path file = folder.resolve("sample.json");
		Files.writeString(file, "{\"size\": 3, \"inner\": {\"a\": \"x\"}}");
		Sample read = JsonConfig.load(file, Sample.class, new Sample(8, true, new Sample.Inner("a", "b")));
		assertEquals(new Sample(3, true, new Sample.Inner("x", "b")), read);
		assertTrue(Files.readString(file).contains("\"on\": true"), "the file now has every option");
	}
}

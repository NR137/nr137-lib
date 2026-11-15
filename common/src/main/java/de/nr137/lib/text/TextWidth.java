package de.nr137.lib.text;

/**
 * Text widths in Minecraft's default font, so the server can fit texts it can't measure on the client: most characters
 * are 6 pixels (5 and a space), a few narrower; unknown ones count as 8, so a guess errs on the short side.
 */
public final class TextWidth {
	/** The room for the title in a chest or shulker box screen: 176 pixels wide, the title starts 8 in. */
	public static final int CONTAINER_TITLE = 160;

	/** A comfortable width for item tooltips in chest screens: about as wide as the screen, so they cover little. */
	public static final int TOOLTIP = 150;

	private TextWidth() {
	}

	/**
	 * The text broken into lines no wider than {@code maxWidth}, at spaces (a word wider than that gets a line of its
	 * own). Tooltips are drawn by the client wherever they fit; short lines keep them narrow.
	 */
	public static java.util.List<String> wrap(String text, int maxWidth) {
		java.util.List<String> lines = new java.util.ArrayList<>();
		StringBuilder line = new StringBuilder();
		for (String word : text.split(" ")) {
			if (word.isEmpty()) {
				continue;
			}
			if (!line.isEmpty() && width(line + " " + word) > maxWidth) {
				lines.add(line.toString());
				line.setLength(0);
			}
			if (!line.isEmpty()) {
				line.append(' ');
			}
			line.append(word);
		}
		if (!line.isEmpty() || lines.isEmpty()) {
			lines.add(line.toString());
		}
		return lines;
	}

	/** The text, shortened with "…" if needed, so that text + suffix fits {@code maxWidth}. */
	public static String fit(String text, String suffix, int maxWidth) {
		int room = maxWidth - width(suffix);
		if (width(text) <= room) {
			return text + suffix;
		}
		int ellipsis = width("…");
		StringBuilder shortened = new StringBuilder();
		int used = 0;
		for (int i = 0; i < text.length(); ) {
			int codePoint = text.codePointAt(i);
			int next = advance(codePoint);
			if (used + next + ellipsis > room) {
				break;
			}
			shortened.appendCodePoint(codePoint);
			used += next;
			i += Character.charCount(codePoint);
		}
		return shortened.toString().stripTrailing() + "…" + suffix;
	}

	public static int width(String text) {
		return text.codePoints().map(TextWidth::advance).sum();
	}

	/** A character's advance in pixels in the default font (glyph width plus one pixel of spacing). */
	public static int advance(int codePoint) {
		return switch (codePoint) {
			case 'i', '!', ',', '.', ':', ';', '|', '\'', '·' -> 2;
			case 'l', '`' -> 3;
			case 't', 'I', ' ', '[', ']', '(', ')', '{', '}', '*', '"' -> 4;
			case 'f', 'k', '<', '>' -> 5;
			case '@', '~', '…' -> 7;
			default -> codePoint < 128 ? 6 : 8;
		};
	}
}

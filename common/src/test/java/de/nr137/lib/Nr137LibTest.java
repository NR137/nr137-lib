package de.nr137.lib;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Unit tests of the loader-independent code: ./gradlew :common:test (./tasks.sh test-java). */
class Nr137LibTest {
	@Test
	void idsUseTheModNamespace() {
		assertEquals("nr137_lib:thing", Nr137Lib.id("thing").toString());
	}
}

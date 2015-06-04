package ch.hsr.ogv.util;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

public class TextUtilTest {

	@Test
	public void testCountUpTrailingWithoutNo() {
		String str = "bla";
		String result = TextUtil.countUpTrailing(str, 10);
		assertEquals("bla10", result);
	}

	@Test
	public void testCountUpTrailingWithHigherStartValue() {
		String str = "bla10";
		String result = TextUtil.countUpTrailing(str, 42);
		assertEquals("bla42", result);
	}

	@Test
	public void testCountUpTrailingWithSmallerStartValue() {
		String str = "bla42";
		String result = TextUtil.countUpTrailing(str, 10);
		assertEquals("bla43", result);
	}

	@Test
	public void testJoin() {
		ArrayList<String> strings = new ArrayList<>();
		strings.add("Donald");
		strings.add("Dagobert");
		strings.add("Mickey");
		String delimiter = "-";
		String result = TextUtil.join(strings, delimiter);
		assertEquals(strings.get(0) + "-" + strings.get(1) + "-" + strings.get(2), result);
	}
}

package ch.hsr.ogv.util;

import static org.junit.Assert.assertEquals;

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
}

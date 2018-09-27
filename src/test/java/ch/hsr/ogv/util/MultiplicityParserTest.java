package ch.hsr.ogv.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MultiplicityParserTest {

	@Test
	public void testGetParsed() {
		String singleDigitMultip = "5";		
		String rangeMultiplicity = "1..5";
		String reversedRangeMultip = "5..1";
		String doubleDigitMultip = "2,4";
		String trippleDigitMultip = "2,4,6";
		String rangeWithComma = "2..6,8";
		String rangeWithCommaAndAsteriks = "2..6,*";
		String rangeWithOverlapMultip = "1..5,2..7";
		String asteriksMultip = "*";
		String rangeMultipWithAsteriks = "2..*";
		String reversedRangeMultipWithAsteriks = "*..*";
		
		String singleCharMultip = "a";
		String letterMultipl = "abc";
		String emptyMultip = "";
		String doublePointsMultip = "";
		String numberWithLettersMultip = "ab123c";
		String stupidRangesMultip = "1..5,2..7";
		String stupidRangesWithAsteriksMultip = "1..5,2..*";
		String doubleDoublePointsMultip = "1....5";
		String doubleCommaMultip = "1,,3";

		assertEquals("5", MultiplicityParser.getParsed(singleDigitMultip));
		assertEquals("1..5", MultiplicityParser.getParsed(rangeMultiplicity));
		assertNull(MultiplicityParser.getParsed(reversedRangeMultip));
		assertEquals("2,4", MultiplicityParser.getParsed(doubleDigitMultip));
		assertEquals("2,4,6", MultiplicityParser.getParsed(trippleDigitMultip));
		assertEquals("2..6,8", MultiplicityParser.getParsed(rangeWithComma));
		assertEquals("2..6,*", MultiplicityParser.getParsed(rangeWithCommaAndAsteriks));
		assertEquals("1..5,2..7", MultiplicityParser.getParsed(rangeWithOverlapMultip));
		assertEquals("*", MultiplicityParser.getParsed(asteriksMultip));
		assertEquals("2..*", MultiplicityParser.getParsed(rangeMultipWithAsteriks));
		assertNull(MultiplicityParser.getParsed(reversedRangeMultipWithAsteriks));
		
		assertNull(MultiplicityParser.getParsed(singleCharMultip));		
		assertNull(MultiplicityParser.getParsed(letterMultipl));
		assertNull(MultiplicityParser.getParsed(emptyMultip));
		assertNull(MultiplicityParser.getParsed(doublePointsMultip));
		assertNull(MultiplicityParser.getParsed(numberWithLettersMultip));
		assertEquals("1..5,2..7", MultiplicityParser.getParsed(stupidRangesMultip));
		assertEquals("1..5,2..*", MultiplicityParser.getParsed(stupidRangesWithAsteriksMultip));
		assertNull(MultiplicityParser.getParsed(doubleDoublePointsMultip));
		assertEquals("1,3", MultiplicityParser.getParsed(doubleCommaMultip));
	}

}

package ch.hsr.ogv.util;

import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Simon Gwerder
 *
 */
public class MultiplicityParser {

	private static final String ASTERISK = "*";

	// ^(\d+|\*)(\.\.(\d+|\*))?(,(\d+|\*)(\.\.(\d+|\*))?)*$
	private static final String MUTLIP_REGEX = "^(\\d+|\\*)(\\.\\.(\\d+|\\*))?(,(\\d+|\\*)(\\.\\.(\\d+|\\*))?)*$";
	private static final Pattern MUTLIP_PATTERN = Pattern.compile(MUTLIP_REGEX);

	public static String getParsedMultiplicity(String multiString) {
		Matcher matcher = MUTLIP_PATTERN.matcher(multiString);
		if (matcher.matches()) {
			return multiString;
		}
		else {
			return null;
		}
	}

	public static String deleteLeadingZeros(String multiString) {
		StringBuffer sb = new StringBuffer();

		String[] split = multiString.split("\\.\\.|,");

		for (int i = 0; i < split.length; i++) {
			System.out.println(split[i]);
			if (split[i] != "*") {
				sb.append(split[i]);
			}
		}
		return sb.toString();
	}

	private static boolean isNForm(String multiString) {
		if (isAsterisk(multiString)) {
			return true;
		}
		Integer parsedInt = toInteger(multiString);
		if (parsedInt != null) {
			return parsedInt >= 1;
		}
		BigInteger parsedBigInt = toBigInteger(multiString);
		if (parsedBigInt != null) {
			return bigIntPositive(parsedBigInt) && !parsedBigInt.equals(new BigInteger("0"));
		}
		return false;
	}

	private static boolean bigIntPositive(BigInteger bigInt) {
		return bigInt.equals(bigInt.abs());
	}

	private static boolean isNMForm(String multiString) {
		try {
			if (multiString.contains("..") && multiString.length() >= 4) {
				String firstPart = multiString.split("\\.\\.")[0];
				String secondPart = multiString.split("\\.\\.")[1];
				if (firstPart.isEmpty() || secondPart.isEmpty()) {
					return false;
				}
				BigInteger firstPartBigInt = toBigInteger(firstPart);
				BigInteger secondPartBigInt = toBigInteger(secondPart);
				boolean firstPartStar = isAsterisk(firstPart);
				boolean secondPartStar = isAsterisk(secondPart);

				if (firstPartStar) {
					return false;
				}
				if (secondPartStar && firstPartBigInt != null) {
					return true;
				}
				if (firstPartBigInt != null && !bigIntPositive(firstPartBigInt) && !firstPartBigInt.equals(new BigInteger("0"))) {
					return false;
				}
				if (secondPartBigInt != null && !bigIntPositive(secondPartBigInt) && secondPartBigInt.equals(new BigInteger("0"))) {
					return false;
				}
				if (firstPartBigInt != null && secondPartBigInt != null && firstPartBigInt.compareTo(secondPartBigInt) < 0) {
					return true;
				}
			}
		}
		catch (ArrayIndexOutOfBoundsException aioobe) {
			return false;
		}
		return false;
	}

	private static boolean isAsterisk(String multiString) {
		return multiString.equals(ASTERISK);
	}

	public static String getParsedMultiplicity_old(String multiString) {
		if (isNForm(multiString)) {
			if (multiString.length() > 1) {
				multiString = multiString.replaceAll("^0+", ""); // remove leading zeros
			}
			return multiString;
		}
		String n = getNInNMForm(multiString);
		String m = getMInNMForm(multiString);
		if (n != null && m != null) {
			return n + ".." + m;
		}
		return null;
	}

	private static String getNInNMForm(String multiString) {
		if (isNMForm(multiString)) {
			String n = multiString.split("[..]")[0];
			if (n.length() > 1) {
				n = n.replaceAll("^0+", ""); // remove leading zeros
			}
			return n;
		}
		return null;
	}

	private static String getMInNMForm(String multiString) {
		if (isNMForm(multiString)) {
			String m = multiString.split("[..]")[2];
			if (m.length() > 1) {
				m = m.replaceAll("^0+", ""); // remove leading zeros
			}
			return m;
		}
		return null;
	}

	// by Jonas Klemming
	public static boolean isInteger(String str) {
		if (str == null || str.isEmpty()) {
			return false;
		}
		int length = str.length();
		if (length == 0) {
			return false;
		}
		int i = 0;
		if (str.charAt(0) == '-') {
			if (length == 1) {
				return false;
			}
			i = 1;
		}
		for (; i < length; i++) {
			char c = str.charAt(i);
			if (c <= '/' || c >= ':') {
				return false;
			}
		}
		return true;
	}

	public static Integer toInteger(String str) {
		try {
			return Integer.parseInt(str);
		}
		catch (NumberFormatException e) {
			return null;
		}
	}

	private static BigInteger toBigInteger(String str) {
		try {
			return new BigInteger(str);
		}
		catch (NumberFormatException e) {
			return null;
		}
	}

}

package ch.hsr.ogv.util;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Simon Gwerder
 *
 */
public class MultiplicityParser {

	public static final String ASTERISK = "*";

	// ^(\d+|\*)(\.\.(\d+|\*))?(,(\d+|\*)(\.\.(\d+|\*))?)*$
	private static final String MUTLIP_REGEX = "^(\\d+|\\*)(\\.\\.(\\d+|\\*))?(,(\\d+|\\*)(\\.\\.(\\d+|\\*))?)*$";
	private static final Pattern MUTLIP_PATTERN = Pattern.compile(MUTLIP_REGEX);

	public static String getSimpleParsed(String multiString) {
		if (multiString.equals("0")) {
			return null;
		}
		Matcher matcher = MUTLIP_PATTERN.matcher(multiString);
		if (matcher.matches()) {
			return multiString;
		}
		else {
			return null;
		}
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

	public static String getParsed(String multiString) {
		Set<String> retContainer = new LinkedHashSet<String>();
		List<String> separations = new ArrayList<String>(Arrays.asList(multiString.split(",")));
		for (String part : separations) {
			if (isNForm(part)) {
				if (part.length() > 1) {
					part = part.replaceAll("^0+", ""); // remove leading zeros
				}
				retContainer.add(part);
			}
			else {
				String n = getNInNMForm(part);
				String m = getMInNMForm(part);
				if (n != null && m != null) {
					retContainer.add(n + ".." + m);
				}
			}
		}
		if (retContainer.isEmpty()) {
			return null;
		}

		return sort(new ArrayList<String>(retContainer));
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

	private static String sort(List<String> multiParts) {
		Collections.sort(multiParts, new Comparator<String>() {
			@Override
			public int compare(String partThis, String partOther) {
				String upperPartThis = getUppermostBound(partThis);
				String upperPartOther = getUppermostBound(partOther);
				if (upperPartThis.equals(upperPartOther)) {
					return 0;
				}
				else if (isAsterisk(upperPartThis)) {
					return 1;
				}
				else if (isAsterisk(upperPartOther)) {
					return -1;
				}
				BigInteger partThisBigInt = new BigInteger(upperPartThis);
				BigInteger partOtherBigInt = new BigInteger(upperPartOther);
				return partThisBigInt.compareTo(partOtherBigInt);
			}
		});
		return TextUtil.join(multiParts, ",");
	}

	public static String sort(String multiString) {
		List<String> separations = new ArrayList<String>(Arrays.asList(multiString.split(",")));
		return sort(separations);
	}

	public static String getUppermostBound(String multiString) {
		if (multiString == null || multiString.isEmpty()) {
			return null;
		}
		List<String> separations = new ArrayList<String>(Arrays.asList(multiString.split("\\.\\.|,")));
		String retString = null;
		BigInteger currentBiggest = new BigInteger("0");
		for (String part : separations) {
			if (isAsterisk(part)) {
				return ASTERISK;
			}
			else if (isInteger(part) && toBigInteger(part).compareTo(currentBiggest) > 0) {
				currentBiggest = toBigInteger(part);
			}
		}
		if (!currentBiggest.equals(new BigInteger("0"))) {
			retString = currentBiggest.toString();
		}
		return retString;
	}

	// by Jonas Klemming, this is faster for simple String > Integer check than test by NumberFormatException
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
		if(str == null || str.isEmpty()) {
			return null;
		}
		try {
			return Integer.parseInt(str);
		}
		catch (NumberFormatException e) {
			return null;
		}
	}

	private static BigInteger toBigInteger(String str) {
		if(str == null || str.isEmpty()) {
			return null;
		}
		try {
			return new BigInteger(str);
		}
		catch (NumberFormatException e) {
			return null;
		}
	}

}

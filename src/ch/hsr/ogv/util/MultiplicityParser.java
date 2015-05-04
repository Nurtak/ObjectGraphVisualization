package ch.hsr.ogv.util;

import java.math.BigInteger;

/**
 * 
 * @author Simon Gwerder
 *
 */
public class MultiplicityParser {
	
	private static final String ASTERISK = "*";
	
	public static boolean isNForm(String multiString) {
		if(isAsterisk(multiString)) {
			return true;
		}
		Integer parsedInt = toInteger(multiString);
		if(parsedInt != null) {
			return parsedInt >= 1;
		}
		BigInteger parsedBigInt = toBigInteger(multiString);
		if(parsedBigInt != null) {
			return bigIntPositive(parsedBigInt) && !parsedBigInt.equals(new BigInteger("0"));
		}
		return false;
	}
	
	private static boolean bigIntPositive(BigInteger bigInt) {
		return bigInt.equals(bigInt.abs());
	}
	
	public static boolean isNMForm(String multiString) {
		try {
			if(multiString.contains("..") && multiString.length() >= 4) {
				String firstPart = multiString.split("[..]")[0];
				String secondPart = multiString.split("[..]")[2];
				if(firstPart.isEmpty() || secondPart.isEmpty()) return false;
				BigInteger firstPartBigInt = toBigInteger(firstPart);
				BigInteger secondPartBigInt = toBigInteger(secondPart);
				boolean firstPartStar = isAsterisk(firstPart);
				boolean secondPartStar = isAsterisk(secondPart);
				
				if(firstPartStar) return false;
				if(secondPartStar && firstPartBigInt != null) return true;
				if(firstPartBigInt != null && !bigIntPositive(firstPartBigInt) && !firstPartBigInt.equals(new BigInteger("0"))) return false;
				if(secondPartBigInt != null && !bigIntPositive(secondPartBigInt) && secondPartBigInt.equals(new BigInteger("0"))) return false;
				if(firstPartBigInt != null && secondPartBigInt != null && firstPartBigInt.compareTo(secondPartBigInt) < 0) return true;
			}
		} catch(ArrayIndexOutOfBoundsException aioobe) {
			return false;
		}
		return false;
	}
	
	public static boolean isAsterisk(String multiString) {
		return multiString.equals(ASTERISK);
	}
	
	public static String getParsedMultiplicity(String multiString) {
		if(isNForm(multiString)) {
			if(multiString.length() > 1) {
				multiString = multiString.replaceAll("^0+", ""); // remove leading zeros
			}
			return multiString;
		}
		String n = getNInNMForm(multiString);
		String m = getMInNMForm(multiString);
		if(n != null && m != null) {
			return n + ".." + m;
		}
		return null;
	}
	
	public static String getNInNMForm(String multiString) {
		if(isNMForm(multiString)) {
			String n = multiString.split("[..]")[0];
			if(n.length() > 1) {
				n = n.replaceAll("^0+", ""); // remove leading zeros
			}
			return n;
		}
		return null;
	}
	
	public static String getMInNMForm(String multiString) {
		if(isNMForm(multiString)) {
			String m = multiString.split("[..]")[2];
			if(m.length() > 1) {
				m = m.replaceAll("^0+", ""); // remove leading zeros
			}
			return m;
		}
		return null;
	}
	
	public static boolean isWellformed(String multiString) {
		if(isNForm(multiString)) {
			return true;
		}
		else if(isNMForm(multiString)) {
			return true;
		}
		return false;
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
	    catch(NumberFormatException e) {
	        return null;
	    }
	}
	
	public static BigInteger toBigInteger(String str) {
		try {
			return new BigInteger(str);
		}
		catch(NumberFormatException e) {
	        return null;
	    }
	}

}

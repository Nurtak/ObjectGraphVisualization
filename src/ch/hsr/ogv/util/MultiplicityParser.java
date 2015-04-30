package ch.hsr.ogv.util;

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
		return false;
	}
	
	public static boolean isNMForm(String multiString) {
		try {
			if(multiString.contains("..") && multiString.length() >= 4) {
				String firstPart = multiString.split("[..]")[0];
				String secondPart = multiString.split("[..]")[2];
				if(firstPart.isEmpty() || secondPart.isEmpty()) return false;
				Integer firstPartInteger = toInteger(firstPart);
				Integer secondPartInteger = toInteger(secondPart);
				boolean firstPartStar = isAsterisk(firstPart);
				boolean secondPartStar = isAsterisk(secondPart);
				
				if(firstPartStar) return false;
				if(secondPartStar && firstPartInteger != null) return true;
				if(firstPartInteger != null && firstPartInteger < 0) return false;
				if(secondPartInteger != null && secondPartInteger <= 0) return false;
				if(firstPartInteger != null && secondPartInteger != null && firstPartInteger < secondPartInteger) return true;
			}
		} catch(ArrayIndexOutOfBoundsException aioobe) {
			return false;
		}
		return false;
	}
	
	public static boolean isAsterisk(String multiString) {
		return multiString.equals(ASTERISK);
	}
	
	public static String getNInNMForm(String multiString) {
		if(isNMForm(multiString)) {
			return multiString.split("[..]")[0];
		}
		return null;
	}
	
	public static String getMInNMForm(String multiString) {
		if(isNMForm(multiString)) {
			return multiString.split("[..]")[2];
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
	        return Integer.parseInt( str );
	    }
	    catch(NumberFormatException e) {
	        return null;
	    }
	}

}

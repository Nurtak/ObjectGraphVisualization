package ch.hsr.ogv.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * 
 * @author Simon Gwerder
 *
 */
public class TextUtil {

	private static final Text helper = new Text();
	private static final double DEFAULT_WRAPPING_WIDTH = helper.getWrappingWidth();
	private static final double DEFAULT_LINE_SPACING = helper.getLineSpacing();
	private static final String DEFAULT_TEXT = helper.getText();

	// private static final TextBoundsType DEFAULT_BOUNDS_TYPE = helper.getBoundsType();

	public static double computeTextWidth(Font font, String text, double help) {
		helper.setText(text);
		helper.setFont(font);

		helper.setWrappingWidth(0.0D);
		helper.setLineSpacing(0.0D);
		double d = Math.min(helper.prefWidth(-1.0D), help);
		helper.setWrappingWidth((int) Math.ceil(d));
		d = Math.ceil(helper.getLayoutBounds().getWidth());

		helper.setWrappingWidth(DEFAULT_WRAPPING_WIDTH);
		helper.setLineSpacing(DEFAULT_LINE_SPACING);
		helper.setText(DEFAULT_TEXT);
		return d;
	}

	public static String countUpTrailing(String str, int startValue) {
		Pattern p = Pattern.compile("[0-9]+$");
		Matcher m = p.matcher(str);
		if (m.find()) {
			String trailingNumber = m.group();
			if (trailingNumber != null) {
				try {
					int parsedNumber = Integer.parseInt(trailingNumber);
					int retNumber = 0;
					if (parsedNumber < startValue) {
						retNumber = startValue;
					}
					else {
						retNumber = parsedNumber += 1;
					}
					int digitCount = ("" + parsedNumber).length();
					return str.substring(0, str.length() - digitCount) + retNumber;
				}
				catch (NumberFormatException nfe) {
					return str + startValue;
				}
			}
		}
		return str + startValue;
	}
	
	public static String replaceLast(String text, String regex, String replacement) {
        return text.replaceFirst("(?s)(.*)" + regex, "$1" + replacement);
    }
	
	public static String join(Collection<String> s, String delimiter) {
	    StringBuffer buffer = new StringBuffer();
	    Iterator<String> iter = s.iterator();
	    while (iter.hasNext()) {
	        buffer.append(iter.next());
	        if (iter.hasNext()) {
	            buffer.append(delimiter);
	        }
	    }
	    return buffer.toString();
	}
	
}

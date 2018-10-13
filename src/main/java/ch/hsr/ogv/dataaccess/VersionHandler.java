package ch.hsr.ogv.dataaccess;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This class read the XMI Version of a File.
 *
 * @author Dario Vonaesch
 * @version 3.0, May 2007
 */
public class VersionHandler extends DefaultHandler {
    private StringBuffer mChars;
    private String mVersion;

    /**
     * Constructor - initialises variables.
     */
    public VersionHandler() {
        mChars = new StringBuffer();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
     */
    public void characters(char[] pCH, int pStart, int pLength) {
        mChars.append(pCH, pStart, pLength);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
     */
    public void endElement(String pUri, String pLName, String pQName) {
        if (pQName.equals("XMI")) {
            mChars.delete(0, mChars.length());
        }

    }

    /**
     * Returns the Version read or null if no Version is recognised.
     *
     * @return the Version or null if no Version is recognised
     */
    public String getVersion() {
        return mVersion;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(String pUri, String pLName, String pQName, Attributes pAtts) {
        mChars.delete(0, mChars.length());

        if (pQName.equals("XMI")) {
            mVersion = pAtts.getValue("xmi.version");
        }

        else if (pQName.equals("xmi:XMI")) {
            mVersion = pAtts.getValue("xmi:version");

        }

    }

}

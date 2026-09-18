package net.sourceforge.jwap.wsp.multipart;

import junit.framework.TestCase;

import net.sourceforge.jwap.util.Utils;
import net.sourceforge.jwap.wsp.header.HeaderParseException;

import java.io.IOException;

import java.util.Enumeration;


public class MultiPartDataTest extends TestCase {
    public MultiPartDataTest(String arg0) {
        super(arg0);
    }

    public void testDecode() throws IOException, HeaderParseException {
        MultiPartData dt = new MultiPartData();
        MultiPartEntry et = new MultiPartEntry("text/plain", "hello".getBytes());

        // dt.addPart(et);
        et = new MultiPartEntry("application/unknown", "hello again".getBytes());
        et.addHeader("accept-language", "de, fr, it");
        et.addHeader("MyHeader", "12");
        dt.addPart(et);

        byte[] data = dt.getBytes();
        System.out.println(Utils.hexDump(data));
        dt = new MultiPartData(data);

        for (Enumeration e = dt.elements(); e.hasMoreElements();) {
            et = (MultiPartEntry) e.nextElement();
            System.out.println(et);
        }
    }
}

/**
 * JWAP - A Java Implementation of the WAP Protocols
 * Copyright (C) 2001-2004 Niko Bender
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package net.sourceforge.jwap.wsp.header;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

import net.sourceforge.jwap.util.Logger;
import net.sourceforge.jwap.util.TransTable;
import net.sourceforge.jwap.wsp.WSPDecoder;



/**
 * This class implements the WAP default codepage for Header encoding/decoding
 *
 * @author Michel Marti
 */
public class WAPCodePage extends CodePage {
  
    private static final String TT_CTENC = "content-encoding";
    private static final String TT_WKPARMS = "wk-params-1.3";
    private static final String TT_CHARSETS = "charsets";
    private static final String TT_LNG = "languages";
    private static final String TT_CCONTROL = "cache-control";
    private static final String TT_CTYPES = "content-types-1.3";
    
    private static final Logger log = Logger.getLogger(WAPCodePage.class);
    private static SimpleDateFormat fmt = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss zzz");
    private TransTable transTable;

    /**
     * Construct a new WAP Code Page. Encoding is done according to WAP encoding
     * version 1.1
     * @throws IOException if the code-page translation table cannot be found
     */
    protected WAPCodePage() throws IOException {
        this(1, 1);
    }

    /**
     * Construct a new WAP Code Page
     * @param major WAP major version
     * @param minor WAP minor version
     * @throws IOException if the code-page translation table cannot be found
     */
    protected WAPCodePage(int major, int minor) throws IOException {
        super(1, true, "default");

        StringBuffer rn = new StringBuffer("wsp-headers-").append(major)
                                                          .append(".").append(minor);
        transTable = TransTable.getTable(rn.toString());
    }

    /**
     * Returns an instance of the WAP Codepage. Encoding is done according
     * to encoding version 1.1
     */
    public static WAPCodePage getInstance() {
        try {
            return new WAPCodePage(1,1);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Returns an instance of the WAP Codepage. Encoding is done according
     * to the specified encoding version.
     * @param major the major encoding version
     * @param minor the minor encoding version
     * @throws IllegalArgumentException if no encoding exists for the
     *   specified major/minor version
     */
    public static WAPCodePage getInstance(int major, int minor)
        throws IllegalArgumentException {
        try {
            return new WAPCodePage(major, minor);
        } catch (IOException e) {
            throw new IllegalArgumentException(major + "." + minor +
                ": No encoding available");
        }
    }

    public byte[] encode(String key, String value) throws HeaderParseException {
        String lowKey = key.toLowerCase().trim();
        Object o = transTable.str2code(lowKey);

        if (log.isDebugEnabled()) {
            log.debug("encode: '" + key + "' -> " + o);
        }

        // Not a Well-Known value? 
        if (o == null) {
            if (log.isDebugEnabled()) {
                log.debug(key +
                    ": Not a well known value, using text-string encoding");
            }

            return Encoding.encodeHeader(key, Encoding.textString(value));
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        short wk = ((Integer) o).shortValue();

        try {
            if ("content-base".equals(lowKey)) {
                throw new HeaderParseException(key +
                    ": Deprecated Header field");
            }

            try {
                // Invoke handler using reflection..
                encodeHeader(lowKey, out, wk, value);
            } catch (NoSuchMethodException nsme) {
                log.warn(key +
                    ": No handler for this header, using text-string encoding");
                out.write(Encoding.encodeHeader(wk, Encoding.textString(value)));
            }
        } catch (HeaderParseException hpe) {
            throw hpe;
        } catch (IOException ex) {
            throw new HeaderParseException("I/O Error while encoding.", ex);
        } catch (Exception uex) {
            log.warn("Unexpected exception while encoding "+key+"="+value, uex);
            throw new HeaderParseException("Unexpected exception while encoding.",
                uex);
        }

        return out.toByteArray();
    }

    public byte[] encode(String key, Date value) {
        Object o = transTable.str2code(key.trim());

        long secs = (value == null) ? 0 : (value.getTime() / 1000);
        byte[] hv = Encoding.longInteger(secs);

        if (o == null) {
            return Encoding.encodeHeader(key, hv);
        } else {
            return Encoding.encodeHeader(((Integer) o).shortValue(), hv);
        }
    }

    public byte[] encode(String key, long value) throws HeaderParseException {
        Object o = transTable.str2code(key.trim());
        short wk = ((o == null) ? (-1) : ((Integer) o).shortValue());

        if (value < 0) {
            throw new HeaderParseException(value +
                ": negative integer values not accepted");
        }

        byte[] hv = Encoding.integerValue(value);

        if (o == null) {
            return Encoding.encodeHeader(key, hv);
        } else {
            return Encoding.encodeHeader(wk, hv);
        }
    }

    public void encodeAccept(OutputStream hdrs, short wk, String value)
        throws IOException {
        for (Enumeration e = HeaderToken.tokenize(value); e.hasMoreElements();) {
            HeaderToken ht = (HeaderToken) e.nextElement();
            String token = ht.getToken();

            // Lookup content-type
            Integer code = TransTable.getTable(TT_CTYPES).str2code(token);

            if (code == null) {
                hdrs.write(Encoding.encodeHeader(wk, Encoding.textString(token)));
            } else {
                hdrs.write(Encoding.encodeHeader(wk,
                        Encoding.shortInteger(code.shortValue())));
            }
        }
    }

    public void encodeAcceptCharset(OutputStream hdrs, short wk, String value)
        throws IOException {
        for (Enumeration e = HeaderToken.tokenize(value); e.hasMoreElements();) {
            HeaderToken ht = (HeaderToken) e.nextElement();
            String token = ht.getToken();

            if ("*".equals(token)) {
                hdrs.write(new byte[] { (byte) wk, (byte) 0xff });

                continue;
            }

            Integer wkl = TransTable.getTable(TT_CHARSETS).str2code(token);
            String qf = ht.getParameter("q");

            if (qf == null) {
                if (wkl != null) {
                    hdrs.write(Encoding.encodeHeader(wk,
                            Encoding.integerValue(wkl.longValue())));
                } else {
                    hdrs.write(Encoding.encodeHeader(wk,
                            Encoding.extensionMedia(token)));
                }
            } else {
                float q = Float.parseFloat(qf);
                byte[] qb = Encoding.qualityFactor(q);
                byte[] lng = null;

                if (wkl != null) {
                    lng = Encoding.shortInteger(wkl.shortValue());
                } else {
                    lng = Encoding.textString(value);
                }

                byte[] dt = new byte[qb.length + lng.length + 1];
                dt[0] = (byte) (qb.length + lng.length);
                System.arraycopy(lng, 0, dt, 1, lng.length);
                System.arraycopy(qb, 0, dt, lng.length + 1, qb.length);
                hdrs.write(Encoding.encodeHeader(wk, dt));
            }
        }
    }

    public void encodeAcceptEncoding(OutputStream hdrs, short wk, String value)
        throws IOException {
        for (Enumeration e = HeaderToken.tokenize(value); e.hasMoreElements();) {
            HeaderToken ht = (HeaderToken) e.nextElement();
            String token = ht.getToken();
            byte[] encoding = null;
            byte[] params = null;

            Integer code = TransTable.getTable(TT_CTENC).str2code(token);

            if (code != null) {
                encoding = new byte[] { (byte) (code.intValue() & 0xff) };
            } else {
                encoding = Encoding.textString(token);
            }

            // Q-Factor?
            String qFactor = ht.getParameter("q");

            if (qFactor != null) {
                float qf = Float.parseFloat(qFactor);
                params = Encoding.qualityFactor(qf);
            }

            hdrs.write(Encoding.shortInteger(wk));

            if ((params != null) || "*".equals(token)) {
                hdrs.write(Encoding.valueLength(encoding.length +
                        ((params == null) ? 0 : params.length)));
            }

            hdrs.write(encoding);

            if (params != null) {
                hdrs.write(params);
            }
        }
    }

    public void encodeAcceptLanguage(OutputStream hdrs, short wk, String value)
        throws IOException {
        for (Enumeration e = HeaderToken.tokenize(value); e.hasMoreElements();) {
            HeaderToken ht = (HeaderToken) e.nextElement();
            String token = ht.getToken();

            if ("*".equals(token)) {
                hdrs.write(new byte[] { (byte) wk, (byte) 0xff });

                continue;
            }

            Integer wkl = TransTable.getTable(TT_LNG).str2code(token);
            String qf = ht.getParameter("q");

            if (qf == null) {
                if (wkl != null) {
                    hdrs.write(Encoding.encodeHeader(wk,
                            Encoding.shortInteger(wkl.shortValue())));
                } else {
                    hdrs.write(Encoding.encodeHeader(wk,
                            Encoding.extensionMedia(token)));
                }
            } else {
                float q = Float.parseFloat(qf);
                byte[] qb = Encoding.qualityFactor(q);
                byte[] lng = null;

                if (wkl != null) {
                    lng = Encoding.shortInteger(wkl.shortValue());
                } else {
                    lng = Encoding.textString(value);
                }

                byte[] dt = new byte[qb.length + lng.length + 1];
                dt[0] = (byte) (qb.length + lng.length);
                System.arraycopy(lng, 0, dt, 1, lng.length);
                System.arraycopy(qb, 0, dt, lng.length + 1, qb.length);
                hdrs.write(Encoding.encodeHeader(wk, dt));
            }
        }
    }

    public void encodeAcceptRanges(OutputStream hdrs, short wk, String value)
        throws IOException {
        if (value == null) {
            return;
        }

        String nv = value.toLowerCase().trim();

        if ("none".equals(nv)) {
            hdrs.write(Encoding.encodeHeader(wk, new byte[] { (byte) 128 }));
        } else if ("bytes".equals(nv)) {
            hdrs.write(Encoding.encodeHeader(wk, new byte[] { (byte) 129 }));
        } else {
            hdrs.write(Encoding.encodeHeader(wk, Encoding.tokenText(value)));
        }
    }

    public void encodeCacheControl(OutputStream hdrs, short wk, String value)
        throws IOException 
    {
        if(value==null) {
            return;
        }
        value = value.trim();
        if( "".equals(value) ) {
            return;
        }
        String nv = value.toLowerCase();
        
        int epos = nv.indexOf('=');
        String arg = null;
        if( epos > 0 ) {
            arg = nv.substring(epos+1);
            nv  = nv.substring(0,epos);
        }
        if( "no-cache".equals(nv) ) {
            hdrs.write(Encoding.encodeHeader(wk, new byte[]{(byte) 128}));
        } else if( "no-store".equals(nv) ) {
            hdrs.write(Encoding.encodeHeader(wk, new byte[]{(byte) 129}));
        } else if( "only-if-cached".equals(nv) ) {
            hdrs.write(Encoding.encodeHeader(wk, new byte[]{(byte) 133}));
        } else if( "public".equals(nv) ) {
            hdrs.write(Encoding.encodeHeader(wk, new byte[]{(byte) 134}));
        } else if( "private".equals(nv) ) {
            hdrs.write(Encoding.encodeHeader(wk, new byte[]{(byte) 135}));
        } else if( "no-transform".equals(nv) ) {
            hdrs.write(Encoding.encodeHeader(wk, new byte[]{(byte) 136}));
        } else if( "must-revalidate".equals(nv) ) {
            hdrs.write(Encoding.encodeHeader(wk, new byte[]{(byte) 137}));
        } else if( "proxy-revalidate".equals(nv) ) {
            hdrs.write(Encoding.encodeHeader(wk, new byte[]{(byte) 138}));
        } else if( "max-age".equals(nv) ) {
            hdrs.write(Encoding.encodeHeader(wk, deltaSeconds((byte) 130,arg)));
        } else if( "max-stale".equals(nv) ) {
            hdrs.write(Encoding.encodeHeader(wk, deltaSeconds((byte) 131,arg)));
        } else if( "min-fresh".equals(nv) ) {
            hdrs.write(Encoding.encodeHeader(wk, deltaSeconds((byte) 132,arg)));
        } else if( "s-maxage".equals(nv) ) {
            hdrs.write(Encoding.encodeHeader(wk, deltaSeconds((byte) 139,arg)));
        } else {
          hdrs.write(Encoding.encodeHeader(wk, Encoding.tokenText(value)));
        }
    }
    
    private byte[] deltaSeconds(byte token, String arg) {
      long ds = Long.parseLong(arg);
      byte[] iv = Encoding.integerValue(ds);
      byte[] bytes = new byte[iv.length+1];
      System.arraycopy(bytes,0, bytes,1,iv.length);
      bytes[0]=token;
      return bytes;
    }
    
    public void encodeConnection(OutputStream hdrs, short wk, String value)
        throws IOException {
        if ( value != null && "CLOSE".equalsIgnoreCase(value.trim())) {
            hdrs.write(Encoding.encodeHeader(wk, new byte[] { (byte) 128 }));
        } else {
            hdrs.write(Encoding.encodeHeader(wk, Encoding.tokenText(value)));
        }
    }

    public void encodeContentEncoding(OutputStream hdrs, short wk, String value)
        throws IOException {
        if (value == null) {
            return;
        }

        Integer code = TransTable.getTable(TT_CTENC).str2code(value.trim());

        if (code != null) {
            hdrs.write(Encoding.encodeHeader(wk,
                    new byte[] { (byte) (code.intValue() & 0xff) }));
        } else {
            hdrs.write(Encoding.encodeHeader(wk, Encoding.tokenText(value)));
        }
    }

    
    public void encodeContentId(OutputStream hdrs, short wk, String value)
    throws IOException {
        hdrs.write(Encoding.encodeHeader(wk, Encoding.quotedString(value)));
    }
    
    public void encodeContentLanguage(OutputStream hdrs, short wk, String value)
        throws IOException {
        if (value == null) {
            return;
        }

        Integer code = TransTable.getTable(TT_LNG).str2code(value.trim());

        if (code != null) {
            hdrs.write(Encoding.encodeHeader(wk,
                    Encoding.shortInteger(code.shortValue())));
        } else {
            hdrs.write(Encoding.encodeHeader(wk, Encoding.tokenText(value)));
        }
    }

    public void encodeContentLength(OutputStream hdrs, short wk, String value)
        throws IOException {
        hdrs.write(Encoding.encodeHeader(wk,
                Encoding.integerValue(Long.parseLong(value))));
    }

    public void encodeContentLocation(OutputStream hdrs, short wk, String value)
        throws IOException {
        hdrs.write(Encoding.encodeHeader(wk, Encoding.textString(value)));
    }

    public void encodeContentType(OutputStream hdrs, short wk, String value)
        throws IOException {
      
        TransTable wkp = TransTable.getTable(TT_WKPARMS);
        // tokenize header-value
        Enumeration e = HeaderToken.tokenize(value);
        HeaderToken token = (HeaderToken) e.nextElement();
        String contentType = token.getToken();
        Integer code = TransTable.getTable(TT_CTYPES).str2code(contentType);

        // set primary value
        byte[] ctv = null;

        if (code == null) {
            ctv = Encoding.textString(contentType);            
        } else {
            ctv = Encoding.shortInteger(code.shortValue());
        }

        // handle parameters
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        String type = token.getParameter("type");
        if (type == null) { 
            type = token.getParameter("type[mpr]");
        }

        if (type != null) {
            // If the content-type is application/vnd.wap.multipart.related,
            // type encoding differs...
            StringBuffer sb = new StringBuffer("type");
            if( "application/vnd.wap.multipart.related".equalsIgnoreCase(contentType) ) {
                sb.append("[MPR]");   
            }
            // TODO: contentType parameters are not always encoded as String 
            // (might be Constrained-encoding)
            out.write(Encoding.encodeHeader(wkp.str2code(sb.toString()).shortValue(),
                Encoding.textString(type)));
        }

        String start = token.getParameter("start");

        if (start != null) {
            out.write(Encoding.encodeHeader(wkp.str2code("start").shortValue(),
                Encoding.textString(start)));
        }

        String name = token.getParameter("name");

        if (name != null) {
            out.write(Encoding.encodeHeader(wkp.str2code("name").shortValue(),
                    Encoding.textString(name)));
        }

        String charset = token.getParameter("charset");

        if (charset != null) {
            // Replace _ with -
            String cset = charset.replace('_','-');
            Integer wkl = TransTable.getTable(TT_CHARSETS).str2code(cset);
            short cwk = wkp.str2code("charset").shortValue();
            if( wkl != null ) {
                out.write(Encoding.encodeHeader(cwk, Encoding.shortInteger(wkl.shortValue())));
            } else {
                log.warn(charset+": Ignoring unknown charset");
            }
        }

        // create header
        byte[] params = out.toByteArray();

        if (params.length == 0) {
            hdrs.write(Encoding.encodeHeader(wk, ctv));
        } else {
            byte[] length = Encoding.uintVar(params.length + ctv.length);
            byte[] head = new byte[length.length + params.length + ctv.length];

            System.arraycopy(length, 0, head, 0, length.length);
            System.arraycopy(ctv, 0, head, length.length, ctv.length);
            System.arraycopy(params, 0, head, (ctv.length + length.length),
                params.length);

            hdrs.write(Encoding.encodeHeader(wk, head));
        }
    }

    public void encodeContentDisposition(OutputStream hdrs, short wk,
        String value) throws IOException {
        if (value != null) {
            if (value.toUpperCase().equals("FORM-DATA")) {
                hdrs.write(Encoding.encodeHeader(wk,
                        new byte[] { 1, (byte) 128 }));
            } else if (value.toUpperCase().equals("ATTACHMENT")) {
                hdrs.write(Encoding.encodeHeader(wk,
                        new byte[] { 1, (byte) 129 }));
            } else if (value.toUpperCase().equals("INLINE")) {
                hdrs.write(Encoding.encodeHeader(wk,
                        new byte[] { 1, (byte) 130 }));
            } else {
                // Token-Text
                byte[] tt = Encoding.tokenText(value);
                byte[] ln = Encoding.valueLength(tt);
                byte[] lt = new byte[ln.length + tt.length];
                System.arraycopy(ln, 0, lt, 0, ln.length);
                System.arraycopy(tt, 0, lt, ln.length, tt.length);

                hdrs.write(Encoding.encodeHeader(wk, lt));
            }
        }
    }

    public void encodeFrom(OutputStream hdrs, short wk, String value)
        throws IOException {
        if (value == null) {
            return;
        }

        hdrs.write(Encoding.encodeHeader(wk, Encoding.textString(value.trim())));
    }

    public void encodeHost(OutputStream hdrs, short wk, String value)
        throws IOException {
        if (value == null) {
            return;
        }

        hdrs.write(Encoding.encodeHeader(wk, Encoding.textString(value.trim())));
    }

    public void encodeIfMatch(OutputStream hdrs, short wk, String value)
        throws IOException {
        if (value == null) {
            return;
        }

        hdrs.write(Encoding.encodeHeader(wk, Encoding.textString(value.trim())));
    }

    public void encodeIfNonMatch(OutputStream hdrs, short wk, String value)
        throws IOException {
        if (value == null) {
            return;
        }

        hdrs.write(Encoding.encodeHeader(wk, Encoding.textString(value.trim())));
    }

    public void encodeIfRange(OutputStream hdrs, short wk, String value)
        throws IOException {
        if (value == null) {
            return;
        }

        hdrs.write(Encoding.encodeHeader(wk, Encoding.textString(value.trim())));
    }

    public void encodeMaxForwards(OutputStream hdrs, short wk, String value)
        throws IOException {
        if (value == null) {
            return;
        }

        hdrs.write(Encoding.encodeHeader(wk,
                Encoding.integerValue(Long.parseLong(value))));
    }

    public void encodeReferer(OutputStream hdrs, short wk, String value)
        throws IOException {
        if (value == null) {
            return;
        }

        hdrs.write(Encoding.encodeHeader(wk, Encoding.uriValue(value.trim())));
    }

    public void encodeUserAgent(OutputStream hdrs, short wk, String value)
        throws IOException {
        if (value == null) {
            return;
        }

        hdrs.write(Encoding.encodeHeader(wk, Encoding.textString(value.trim())));
    }

    public Header decode(byte[] data) throws HeaderParseException, IOException {
        WSPDecoder d = new WSPDecoder(data);
        int c1 = d.getUint8();
        String key = null;
        String val = null;

        if ((c1 & 0x80) != 0) {
            c1 = c1 & 0x7f;
            key = transTable.code2str(c1);
            if(log.isDebugEnabled()) {
              log.debug("code2str(0x"+Integer.toHexString(c1)+")="+key);
            }

            byte[] fieldValue = d.getBytes(data.length - 1);
             
            if(key == null ) {
                key="0x"+Integer.toHexString(c1);
                log.warn(key+": unknown header");
            } else {
                try {
                  val = decodeHeaderField(key, fieldValue);
                } catch( NoSuchMethodException nsme ) {
                    log.warn("'"+key+"': Header decoding not yet implemented :-(");
                } catch (Exception e) {
                    log.warn("Unable to decode header " + key, e);
                }
            }
        } else {
            d.seek(-1);
            key = d.getCString();
            val = d.getCString();
        }

        return new Header(key, val);
    }

    public String decodeAcceptLanguage(byte[] data) throws IOException {
        WSPDecoder d = new WSPDecoder(data);
        String retval = null;

        int c1 = d.getUint8();

        if (c1 > 31) {
            // Constrained encoding
            if ((c1 & 0x80) != 0) {
                // Short integer
                c1 = c1 & 0x7f;
                retval = TransTable.getTable(TT_LNG).code2str(c1);
            } else {
                d.seek(-1);
                retval = d.getCString();
            }
        } else {
            // General form 
        }

        return retval;
    }
    
    public String decodeDate(byte[] data) {
        WSPDecoder d = new WSPDecoder(data);
        Date dt = d.getDateValue();
        return fmt.format(dt);
    }
    
    public String decodeServer(byte[] data) {
        WSPDecoder d = new WSPDecoder(data);
        return d.getTextString();
    }
    
    public String decodeContentId(byte[] data) {
        WSPDecoder d = new WSPDecoder(data);
        // remove quote
        d.seek(1);
        return d.getCString();
    }
    
    public String decodeContentDisposition(byte[] data) {
        StringBuffer sb = new StringBuffer();
        WSPDecoder d = new WSPDecoder(data);
        long l = d.getValueLength();
        int o = d.getUint8();
        switch(o) {
            case 128:
                sb.append("Form-Data");
                break;
            case 129:
                sb.append("Attachment");
                break;
            case 130:
                sb.append("Inline");
                break;
            default:
                d.seek(-1);
                sb.append(d.getTextString());
                break;
        }
        // TODO Parse parameters...
        byte[] parms = d.getBytes(d.getRemainingOctets());
        return sb.toString();
    }
    
    public String decodeContentLength(byte[] data) {
        WSPDecoder d = new WSPDecoder(data);
        return Long.toString(d.getIntegerValue());
    }
    
    public String decodeContentLocation(byte[] data) {
        WSPDecoder d = new WSPDecoder(data);
        return d.getCString();
    }
    
    public String decodeContentType(byte[] data) {
        String retval = null;
        WSPDecoder d = new WSPDecoder(data);
        int o = d.getUint8();
        if( o <= 31 ) { // General Form
          d.seek(-1);
          long len = d.getValueLength();
          if( len == 0 ) {
            	return "";
          }
          o = d.getUint8();
          if( (o & 0x80) != 0 ) {
              // Short-Integer
              short wk = (short) (o&0x7f);
              retval = TransTable.getTable(TT_CTYPES).code2str(wk);            
          } else if( o <= 30 ) { // long-integer
              d.seek(-1);
              int wk = (int) d.getLongInteger();
              retval = TransTable.getTable(TT_CTYPES).code2str(wk);            
          } else { // *TEXT EOF
              d.seek(-1);
              retval = d.getTextString();
          }
          byte[] params = d.getBytes(d.getRemainingOctets());
          if( params.length > 0 ) {
              WSPDecoder param = new WSPDecoder(params);
              StringBuffer buf = new StringBuffer();
              buf.append(retval);
              while( !param.isEOF() ) {
                  String code = null;
                  try {
                      int c = (int) param.getIntegerValue();
                      TransTable wkp = TransTable.getTable(TT_WKPARMS);
                      code = wkp.code2str((int)c);
                      String val= null;
                      switch(c){
                          case 0x01: // charset
                            int charset = param.getUint8();
                            if( (charset & 0x80) != 0 ) { // Short integer
                                charset = charset & 0x7f;
                                // Lookup charset
                                val = TransTable.getTable(TT_CHARSETS).code2str(charset);
                            } 
                            break;
                          case 0x05: // name version 1.1
                          case 0x06: // fileName version 1.1
                          case 0x07: // name version 1.4
                          case 0x08: // fileName version 1.4
                          case 0x09: // type
                          case 0x0A: // start
                          case 0x0B: // start info
                          case 0x0C: // comment
                          case 0x0D: // domain
                          case 0x0F: // path
                          case 0x12: // MAC
                            val = param.getCString();
                            break;
                      }
                      if( val!=null ) {
                        buf.append("; ").append(code).append("=").append(val);
                      }
                  } catch (Exception e){
                      log.info("decodeContentType: parameter decoding failed, ignoring parameter "+code,e);
                  }
              }
              retval = buf.toString();
          }
        } else { // Constrained encoding
          if( (o & 0x80) != 0 ) { // Short integer
            short wk = (short) (o&0x7f);
            retval = TransTable.getTable(TT_CTYPES).code2str(wk);            
          } else { // *Text EOF 
            d.seek(-1);
            retval = d.getTextString();
          }
        }
        return retval;
    }
    
    public String decodeConnection(byte[] data) {
        WSPDecoder d = new WSPDecoder(data);
        int o = d.getUint8();
        if( o == 128 ) {
            return "CLOSE";   
        }
        d.seek(-1);
        return d.getCString();
    }
    
    public String decodeVia(byte[] data) {
        WSPDecoder d = new WSPDecoder(data);
        return d.getTextString();
    }    

    public String decodeWarning(byte[] data) {
        WSPDecoder d = new WSPDecoder(data);
        int o = d.getUint8(), code=0;
        String agent = null, txt = null;
        
        if( (o & 0x80) != 0  ) {
            // Warn-Code
            code = o & 0x7f;
        } else {
            d.getValueLength();
            code = d.getShortInteger();
            agent = d.getTextString();
            txt = d.getTextString();   
        }
        String cTxt = null;
        switch( code ) {
            case 10: cTxt = "110 Response is stale"; break;
            case 11: cTxt = "111 Revalidation failed"; break;
            case 12: cTxt = "112 Disconnected operation"; break;
            case 13: cTxt = "113 Heuristic expiration"; break;
            case 99: cTxt = "199 Miscellaneous (persistent) warning"; break;
            case 14: cTxt = "214 Transformation applied"; break;
            default: cTxt = Integer.toString(code);
        }
        StringBuffer sb = new StringBuffer(cTxt);
        if( agent != null ) {
            sb.append("; agent=\"").append(agent).append('"');
        }
        if( txt != null ) {
            sb.append("; text=").append(txt);
        }
        return sb.toString();
    }    
    
    public String decodeExpires(byte[] data) {
        WSPDecoder d = new WSPDecoder(data);
        Date dt = d.getDateValue();
        return fmt.format(dt); 
    }
    
    public String decodeCacheControl(byte[] data) {
        WSPDecoder d = new WSPDecoder(data);
        String ret = null;

        int o = d.getUint8();
        if( o  <= 31 ) { // value-length cache-directive
            log.warn("decoding cache-control (value-length cache-directive) not yet implemented");
        } else  if( (o & 0x80) != 0 ) {
            ret = TransTable.getTable(TT_CCONTROL).code2str(o);
        } else { // Token-Text
            ret = d.getCString();
        }
        return ret;
    }

    public String decodeLocation(byte[] data) {
        WSPDecoder d = new WSPDecoder(data);
        return d.getTextString();
    }
}

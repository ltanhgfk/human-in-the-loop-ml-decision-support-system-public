package mmslib.mms;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class HexStringConverter {

	/**
	 * @param args
	 */
	private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();
    private static HexStringConverter hexStringConverter = null;
 
    private HexStringConverter()
    {    	
    }
 
    public static HexStringConverter getHexStringConverterInstance()
    {
        if (hexStringConverter==null) hexStringConverter = new HexStringConverter();
        return hexStringConverter;
    }
 
    public String stringToHex(String input) throws UnsupportedEncodingException
    {
        if (input == null) throw new NullPointerException();
        return asHex(input.getBytes());
    }
 
    /*public String hexToString(String txtInHex)
    {
        byte [] txtInByte = new byte [txtInHex.length() / 2];
        int j = 0;
        for (int i = 0; i < txtInHex.length(); i += 2)
        {
                txtInByte[j++] = Byte.parseByte(txtInHex.substring(i, i + 2), 16);        	
        }
        return new String(txtInByte);
    }*/
    
    public String hexToString(String hex) throws  IOException{
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        byte[] buffer = new byte[512];
        int _start=0;
        for (int i = 0; i < hex.length(); i+=2) {
            buffer[_start++] = (byte)Integer.parseInt(hex.substring(i, i + 2), 16);
            if (_start >=buffer.length || i+2>=hex.length()) {
                bout.write(buffer);
                Arrays.fill(buffer, 0, buffer.length, (byte)0);
                _start  = 0;
            }
        }
        return  bout.toString();
    }
 
    private String asHex(byte[] buf)
    {
        char[] chars = new char[2 * buf.length];
        for (int i = 0; i < buf.length; ++i)
        {
            chars[2 * i] = HEX_CHARS[(buf[i] & 0xF0) >>> 4];
            chars[2 * i + 1] = HEX_CHARS[buf[i] & 0x0F];
        }
        return new String(chars);
    }
}

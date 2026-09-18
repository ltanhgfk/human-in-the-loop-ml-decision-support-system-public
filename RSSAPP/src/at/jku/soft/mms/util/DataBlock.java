/**
 * MMSLIB - A Java Implementation of the MMS Protocol
 * Copyright (C) 2004 Simon Vogl 
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
package at.jku.soft.mms.util;

import net.sourceforge.jwap.wsp.*;
import net.sourceforge.jwap.wsp.pdu.*;
import net.sourceforge.jwap.util.*;
import java.util.*;
import org.apache.log4j.*;


//protected class DataBlock {
public class DataBlock {
    public byte[] data;
    public int pos;      // position in the data block

    public DataBlock(int len)
    {
	data = new byte[len];
	pos=0;
    }

    public DataBlock()
    {
	this(1500); // default MTU size
    }

    public synchronized void realloc(int size) 
    {
	byte tmp[] = new byte[size];
	int max = ( size> data.length) ? size: data.length;
	System.arraycopy(data, 0, tmp, 0, max);
	if (pos>max) pos=max;
	data = tmp;
    }

    protected void growIfNeeded(int toSize) 
    {
	if (pos >= data.length)
	    realloc(toSize);
    }
    
    private void growIfNeeded() 
    {
	growIfNeeded(data.length * 2);
    }

    public synchronized void add(byte b) 
    {
	growIfNeeded();
	data[pos++]=b;
    }

    public synchronized void add(byte[] b) {

	growIfNeeded( (data.length * 2) + b.length);	
    	    	
    	System.arraycopy(b, 0, data, pos, b.length);
    	pos = pos + b.length;    	
    }
    
    public synchronized void addShortInt(int i) 
    {
	//if (i>=128) {err}
	growIfNeeded();
	data[pos++]= (byte)(i | 0x80);
    }
    
    public void addString(String s) 
    {
	char cs[] = s.toCharArray();
	for (int i=0;i<cs.length;i++) {
	    add((byte)(cs[i]));
	}
	add((byte)(0x00));
    }

    public void addUIntVar(long var)
    {
	int i;
	if (var<0){ // it should work anyway, but haven't tried
	    throw new NumberFormatException("No negative values supported");
	} 
	if(var==0){
	    add((byte)0);
	    return;
	}
	// split into 7-bit values
	byte bits[] = new byte[8];
	for (i=0;i<8;i++) {
	    bits[i] = (byte)(var & 0x7f);
	    var >>=7;
	}
	i=7;
	while ( bits[i] == 0 ) { i--; } // skip over empty values
	// now start coding:
	while (i>0) {
	    add( (byte)(0x80 | bits[i]) );
	    i--;
	} 
	add(bits[0]);
    }


    public synchronized byte[] getAsByteArray() {
    	byte ret[] = new byte[pos];
    	System.arraycopy(data, 0, ret, 0, pos);
    	
    	return ret;
    }
}

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
//import net.sourceforge.jwap.util.*;
import java.io.*;
import java.util.*;
import org.apache.log4j.*;

import at.jku.soft.mms.lib.*;
import at.jku.soft.mms.lib.head.*;

/** MMSPDUFactory contains helper functions that create MMSPDU objects
 * based on individual arguments. 
 *
 * @author Jonee Ryan Ty 
 */
public class MMSPDUFactory {
    static Logger logger = Logger.getLogger(MMSPDUFactory.class);



    public static MMSPDU makeMmsSendReq(String recipient, byte body[])
    {
	MMSPDU pdu = new MMSPDU();	
	return pdu;
    }

    public static MMSPDU makeMmsMboxViewReq()
    {
	MBoxViewReq head = new MBoxViewReq();
	// add config...
	System.out.println(head.toString());
	
	MMSPDU pdu = new MMSPDU(head);

	return pdu;
    }

}

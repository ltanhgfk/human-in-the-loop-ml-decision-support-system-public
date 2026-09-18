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
package at.jku.soft.mms.lib.head;

import net.sourceforge.jwap.wsp.*;
import net.sourceforge.jwap.wsp.pdu.*;
import net.sourceforge.jwap.util.*;
import java.util.*;
import org.apache.log4j.*;

import at.jku.soft.mms.util.DataBlock;
import at.jku.soft.mms.lib.MMSHeaders;

public class SendReq extends MMSHeaders {

    public SendReq()
    {
	super();
	setMessageType(Mms_M_Send_Req);
    }    
    
    public void setMessageType(int type) 
    { //fail...
    }


    public void replyTo(MMSHeaders sender) 
    {
	this.setTo(sender.getFrom());
	// do NOT touch from field
	this.setTransactionId("tid_"+System.currentTimeMillis());
       
    }

    public void emitSpecificHeaders(DataBlock db) 
    {
	db.addShortInt(MMSHeaders.HEADER_X_Mms_MMS_Version);
	db.addShortInt(0x10);

	db.addShortInt(MMSHeaders.HEADER_To);
	db.addString("4369914219500");

	db.addShortInt(MMSHeaders.HEADER_From);
	db.addString("4369914219503");

	db.addShortInt(MMSHeaders.HEADER_X_Mms_Message_Class);
	db.addShortInt(0x00); //personal
	
	db.addShortInt(MMSHeaders.HEADER_X_Mms_Priority);
	db.addShortInt(0x01); //normal
	
	db.addShortInt(MMSHeaders.HEADER_X_Mms_Delivery_Report);
	db.addShortInt(0x01); // no

	db.addShortInt(MMSHeaders.HEADER_X_Mms_Read_Report);
	db.addShortInt(0x01); // no
    }
}

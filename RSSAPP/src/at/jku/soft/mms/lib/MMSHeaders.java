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
package at.jku.soft.mms.lib;

import net.sourceforge.jwap.wsp.*;
import net.sourceforge.jwap.wsp.pdu.*;
import net.sourceforge.jwap.util.*;
import java.util.*;
import org.apache.log4j.*;

import at.jku.soft.mms.lib.head.*; // the concrete header classes
import at.jku.soft.mms.util.DataBlock;

public class MMSHeaders extends CWSPHeaders {

    public static final String mms_headers[] = {
	"undef.", 
	"Bcc",	"Cc",
	"X-Mms-Content-Location",	"Content-Type",
	"Date",	"X-Mms-Delivery-Report",
	"X-Mms-Delivery-Time",	"X-Mms-Expiry",
	"From",	"X-Mms-Message-Class",
	"Message-ID",	"X-Mms-Message-Type",
	"X-Mms-MMS-Version",	"X-Mms-Message-Size",
	"X-Mms-Priority",	"X-Mms-Read-Report",
	"X-Mms-Report-Allowed",	"X-Mms-Response-Status",
	"X-Mms-Response-Text",	"X-Mms-Sender-Visibility",
	"X-Mms-Status",	"Subject",
	"To",	"X-Mms-Transaction-Id",
	"X-Mms-Retrieve-Status",	"X-Mms-Retrieve-Text",
	"X-Mms-Read-Status",	"X-Mms-Reply-Charging",
	"X-Mms-Reply-Charging-Deadline",	"X-Mms-Reply-Charging-ID",
	"X-Mms-Reply-Charging-Size",	"X-Mms-Previously-Sent-By",
	"X-Mms-Previously-Sent-Date",
    };

    public static final short HEADER_Bcc = 0x01 ;
    public static final short HEADER_Cc = 0x02 ;
    public static final short HEADER_X_Mms_Content_Location = 0x03 ;
    public static final short HEADER_Content_Type = 0x04 ;
    public static final short HEADER_Date = 0x05 ;
    public static final short HEADER_X_Mms_Delivery_Report = 0x06 ;
    public static final short HEADER_X_Mms_Delivery_Time = 0x07 ;
    public static final short HEADER_X_Mms_Expiry = 0x08 ;
    public static final short HEADER_From = 0x09 ;
    public static final short HEADER_X_Mms_Message_Class = 0x0a ;
    public static final short HEADER_Message_ID = 0x0b ;
    public static final short HEADER_X_Mms_Message_Type = 0x0c ;
    public static final short HEADER_X_Mms_MMS_Version = 0x0d ;
    public static final short HEADER_X_Mms_Message_Size = 0x0e ;
    public static final short HEADER_X_Mms_Priority = 0x0f ;
    public static final short HEADER_X_Mms_Read_Report = 0x10 ;
    public static final short HEADER_X_Mms_Report_Allowed = 0x11 ;
    public static final short HEADER_X_Mms_Response_Status = 0x12 ;
    public static final short HEADER_X_Mms_Response_Text = 0x13 ;
    public static final short HEADER_X_Mms_Sender_Visibility = 0x14 ;
    public static final short HEADER_X_Mms_Status = 0x15 ;
    public static final short HEADER_Subject = 0x16 ;
    public static final short HEADER_To = 0x17 ;
    public static final short HEADER_X_Mms_Transaction_Id = 0x18 ;
    public static final short HEADER_X_Mms_Retrieve_Status = 0x19 ;
    public static final short HEADER_X_Mms_Retrieve_Text = 0x1a ;
    public static final short HEADER_X_Mms_Read_Status = 0x1b ;
    public static final short HEADER_X_Mms_Reply_Charging = 0x1c ;
    public static final short HEADER_X_Mms_Reply_Charging_Deadline = 0x1d ;
    public static final short HEADER_X_Mms_Reply_Charging_ID = 0x1e ;
    public static final short HEADER_X_Mms_Reply_Charging_Size = 0x1f ;
    public static final short HEADER_X_Mms_Previously_Sent_By = 0x20 ;
    public static final short HEADER_X_Mms_Previously_Sent_Date = 0x21 ;

    public static final short HEADER_Content_ID = 0x40 ; // for multipart

    public static final short Mms_M_Send_Req = 0x00 ;
    public static final short Mms_M_Send_Resp = 0x01 ;
    public static final short Mms_M_Notification_Ind = 0x02 ;
    public static final short Mms_M_NotifyResp_Ind = 0x03 ;
    public static final short Mms_M_Retrieve_Conf = 0x04 ;
    public static final short Mms_M_Acknowledge_Ind = 0x05 ;
    public static final short Mms_M_Delivery_Ind = 0x06 ;
    public static final short Mms_M_Read_Rec_Ind = 0x07 ;
    public static final short Mms_M_Read_Orig_Ind = 0x08 ;
    public static final short Mms_M_Forward_Req = 0x09 ;
    public static final short Mms_M_Forward_Conf = 0x0a ;
    public static final short Mms_M_Mbox_Store_Req = 0x0b ;
    public static final short Mms_M_Mbox_Store_Conf = 0x0c ;
    public static final short Mms_M_Mbox_View_Req = 0x0d ;
    public static final short Mms_M_Mbox_View_Conf = 0x0e ;
    public static final short Mms_M_Mbox_Upload_Req = 0x0f ;
    public static final short Mms_M_Mbox_Upload_Conf = 0x10 ;
    public static final short Mms_M_Mbox_Delete_Req = 0x11 ;
    public static final short Mms_M_Mbox_Delete_Conf = 0x12 ;
    public static final short Mms_M_Mbox_Descr = 0x13 ;

    public static final short Mms_MsgClass_Personal = 0x00 ;

    public static final String mms_message_types[] = {
	"m-send-req",
	"m-send-conf",
	"m-notification-ind",
	"m-notifyresp-ind",
	"m-retrieve-conf",
	"m-acknowledge-ind",
	"m-delivery-ind",
	"m-read-rec-ind",
	"m-read-orig-ind",
	"m-forward-req",
	"m-forward-conf",
	"m-mbox-store-req",
	"m-mbox-store-conf",
	"m-mbox-view-req",
	"m-mbox-view-conf",
	"m-mbox-upload-req",
	"m-mbox-upload-conf",
	"m-mbox-delete-req",
	"m-mbox-delete-conf",
	"m-mbox-descr"
    };

    private int messageType;
    private String transactionId;
    private String from;
    private String to;
    private String version;
    private int vers_major;
    private int vers_minor;
    private long ldate;
    private int rstatus=-1;
    private String messageId;
    private int messageClass=0;      // personal
    private int priority=1;          // normal
    private boolean delivRpt=false;
    private boolean readRpt=false;
    private String contentId;

    public MMSHeaders()
    {
	ct = new ContentType();
	vers_major = 1; 
	vers_minor = 0;
    }
    
    public MMSHeaders(MMSHeaders clone) 
    {
	messageType = clone.messageType ;
	transactionId = clone.transactionId ;
	from = clone.from ;
	to = clone.to ;
	version = clone.version ;
	ldate = clone.ldate ;
	rstatus = clone.rstatus ;
	messageId = clone.messageId ;
	messageClass = clone.messageClass ;
	priority = clone.priority ;          
	delivRpt = clone.delivRpt ;
	readRpt = clone.readRpt ;	

	complete = clone.complete;
    }

    public void setMessageType(int type) { this.messageType = type; }
    public void setTransactionId(String id) { transactionId = id; }
    public void setMessageId(String id) { messageId = id; }
    public void setDate(long d) {ldate = d; }
    public void setContentId(String id) { contentId = id; }

    public int getMessageType() { return messageType; }
    public String getMessageTypeName() { return mms_message_types[messageType]; }
    public String getTransactionId() { return transactionId; }
    public String getMessageId() { return messageId; }
    public String getContentId() { return contentId; }

    public void setFrom(String id) { from = id; }
    public void setResponseStatus(int s) { rstatus = s; }
    public void setTo(String id) { to = id; }
    public void setDelivReport(int rpt) { delivRpt =  (rpt==0);  }
    public void setReadReport(int rpt) { readRpt =  (rpt==0);  }
    public void setPriority(int p) { priority= p;  }

    public void setVersion(String v) { version= v; }
    public void setMmsVersion(int maj, int min) { vers_major = maj; vers_minor= min; }

    public String getFrom() { return from; }
    public String getTo() { return to; }

    private ContentType ct;

    public ContentType getContentType() { return ct; }
    public int getResponseStatus() { return rstatus; }

    /** set if the header parsing is complete. 
     */
    private boolean complete = false;
    public boolean isComplete() { return complete; }
    public void setComplete(boolean c) { complete =c;}
    public void setComplete() { complete =true;}

    public boolean isMultiPart() 
    {
	return ct.isMultiPart();
    }

    public int tokenForName(String name)
    {
	if (name == null) return -1;
	for (int i=0;i<mms_headers.length;i++) {
	    if (mms_headers[i]!=null && mms_headers[i].equals(name)) {
		return i;
	    }
	}
	return -1;
    }

    /** nameForToken returns the string representation of a given token
     */
    public String nameForToken(int token) {
	if (token<0||token>=mms_headers.length) { 
	    return null;
	}
	return mms_headers[token];
    }

    public String toString() 
    {
	String s;
	s  = "X-Mms-Message-Type: " + mms_message_types[messageType];
	s += "\r\nX-Mms-Transaction-ID: " + transactionId;
	s += "\r\nX-Mms-Version: " + version;
	if (messageId != null)
	    s += "\r\nMessage-ID: " + messageId;
	s += "\r\nDate: " + ldate;
	s += "\r\n" + ct.toString();

	switch (messageType) {
	case 0:
	    break;
	case 4: // retrieve-conf
	    s += "\r\nFrom: " + from;
	    s += "\r\nTo: " + to;
	    break;
	case 1: // send-resp
	    s += "\r\nResponse-Status: " + rstatus;
	    break;
	default:
	    break;
	}
	if (contentId!=null) {
	    s += "\r\nContent-ID: " + contentId;
	}
	return s;
    }


    protected void emitCommonHeaders(DataBlock db) 
    {
	db.addShortInt(HEADER_X_Mms_Message_Type);
	db.addShortInt(getMessageType());

	db.addShortInt(MMSHeaders.HEADER_X_Mms_Transaction_Id);
	if (getTransactionId() == null)
	    db.addString("tid_"+System.currentTimeMillis());
	else
	    db.addString(getTransactionId());

	db.addShortInt(MMSHeaders.HEADER_X_Mms_MMS_Version);
	db.addShortInt( (vers_major << 4) | vers_minor );
    }


    protected void emitSpecificHeaders(DataBlock db) 
    {
	//
    }


    public void emitContentType(DataBlock db)
    {
	db.addShortInt(MMSHeaders.HEADER_Content_Type);
	ct.emit(db);
    }
     
    public void emit(DataBlock db)
    {
	emitCommonHeaders(db);

	emitSpecificHeaders(db);

	//todo:
	//emitOptionalHeaders(db);
	
	// last step: add content-type
	if (messageType == Mms_M_Send_Req
	    //|| messageType == Mms_M_Forward_Req
	    //|| messageType ==
	    ) {
	    emitContentType(db);
	}
    }
}

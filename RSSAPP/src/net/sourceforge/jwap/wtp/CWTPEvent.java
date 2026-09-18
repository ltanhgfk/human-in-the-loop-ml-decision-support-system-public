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
package net.sourceforge.jwap.wtp;

import net.sourceforge.jwap.util.Logger;


/**
 *
 * This class represents a service primitive used to communicate between WTP
 * layer and the layer above (e.g. WSP). Generally are four types of
 * service primitives:
 * <ul>
 *   <li>req (upper -> WTP) - Anounce a request</li>
 *   <li>ind (WTP -> upper) - Indicate a request by a remote stack</li>
 *   <li>res (upper -> WTP) - Answer a indication</li>
 *   <li>cnf (wtp -> upper) - Answer a request</li>
 * </ul>
 * The wireless transaction protocol provides the following services
 * according tp section 5.3 of the specification.
 *
 * <ul>
 *   <li>TR-INVOKE - Used to initiate a new transaction (req, ind, res, cnf) </li>
 *   <li>TR-RESULT - Used to send back a result of a previously initiated transaction</li>
 *   <li>TR-ABORT - Used to abort an existing transaction.</li>
 * </ul>
 * TR-INVOKE and TR-RESULT are modeled by this class. TR-ABORT is a call of
 * <code>abort(abordCode)</code> on the corresponding transaction.
 * Service primitive can be processed by CWTPSocket, that implements the state
 * machine of the WTP layer or they are used to inform IWTPListeners by the WTP layer.
 */
public class CWTPEvent {
    // use these constants to specify the service primitive type
    public static final byte TR_INVOKE_REQ = 0x00;
    public static final byte TR_INVOKE_IND = 0x01;
    public static final byte TR_INVOKE_RES = 0x02;
    public static final byte TR_INVOKE_CNF = 0x03;
    public static final byte TR_RESULT_REQ = 0x04;
    public static final byte TR_RESULT_IND = 0x05;
    public static final byte TR_RESULT_RES = 0x06;
    public static final byte TR_RESULT_CNF = 0x07;
    public static final byte TR_INVOKEDATA_REQ = 0x08;
    public static final byte TR_INVOKEDATA_IND = 0x09;
    public static final byte TR_INVOKEDATA_RES = 0x0A;
    public static final byte TR_INVOKEDATA_CNF = 0x0B;
    public static final byte TR_RESULTDATA_REQ = 0x0C;
    public static final byte TR_RESULTDATA_IND = 0x0D;
    public static final byte TR_RESULTDATA_RES = 0x0E;
    public static final byte TR_RESULTDATA_CNF = 0x0F;
    public static final String[] types = {
        "TR_INVOKE_REQ", "TR_INVOKE_IND", "TR_INVOKE_RES", "TR_INVOKE_CNF",
        
        "TR_RESULT_REQ", "TR_RESULT_IND", "TR_RESULT_RES", "TR_RESULT_CNF",
        
        "TR_INVOKEDATA_REQ", "TR_INVOKEDATA_IND", "TR_INVOKEDATA_RES",
        "TR_INVOKEDATA_CNF",
        
        "TR_RESULTDATA_REQ", "TR_RESULTDATA_IND", "TR_RESULTDATA_RES",
        "TR_RESULTDATA_CNF"
    };
    static Logger logger = Logger.getLogger(CWTPEvent.class);

    /**
     * the service primitive type
     */
    private byte type;

    //XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    // Fields used in a TR-Invoke oder TR-Result according to the spec

    /**
     * Section 5.3.1.6 User Data<br>
     * The user data carried by the  WTP protocol (payload).
     * WTP layer will submit this data to the destination without changing content.
     */
    private byte[] userData;

    /**
     * Section 5.3.1.8 Exit Info<br>
     * additional user data to be sent to the originator on transaction completion.
     * moredata has to be false and classtype of transacition has to be 1.
     */
    private byte[] exitInfo = new byte[0];

    /**
     * Section 5.3.1.9 More Data<br>
     * Will there be more invocations of this primitive for the same transaction?
     * ext. segmentation and re-assambly has to be used!
     */
    private boolean moreData;

    /**
     * Section 5.3.1.10 Frame Boundary<br>
     * Is this user data the beginning of a new user defined frame?
     * ext. segmentation and re-assambly has to be used!
     */
    private boolean frameBoundary;
    private IWTPTransaction transaction;

    //XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

    /**
     * Constructor to construct tr-invoke and tr-result
     * <br>(construct Aborts by calling abort(code) on the transaction.
     * After this the transaction throws abortedExceptions on calling anything on it.)
     *
     *
     * @param userData Payload/bytes of the upper layer
     * @param exitInfo Will there be more invocations of this primitive for the
     * same transaction? (available if ext. segmentation and re-assambly used)
     * @param moreData  Additional invocations for the same transaction follow
     * (available if ext. segmentation and re-assambly used)
     * @param frameBoundary specifies a new user defined data frame
     * (available if ext. segmentation and re-assambly used)
     * @param transaction the transaction the service primitive belongs to.
     * @param type the type of this service primitive
     * (use constants defined in the class)
     */
    public CWTPEvent(byte[] userData, byte[] exitInfo, boolean moreData,
        boolean frameBoundary, byte type) {
        this.userData = userData;
        this.exitInfo = exitInfo;
        this.moreData = moreData;
        this.frameBoundary = frameBoundary;
        this.type = type;

        //logger.debug("" + types[type]);
    }

    /**
     * Constructor to construct tr-invoke and tr-result<br>
     * defaults: moreData = false, no exitInfo, frameBoundary = false
     *
     * @param userData Payload/bytes of the upper layer
     * @param transaction the transaction the service primitive belongs to.
     * @param type the type of this service primitive
     * (use constants defined in the class)
     */
    public CWTPEvent(byte[] userData, byte type) {
        this.userData = userData;
        this.moreData = false;
        this.frameBoundary = false;
        this.type = type;

        //logger.debug("" + types[type]);
    }

    /////////////////////////////////////////////////////////////////////////////
    //////////////////////////////// SET/GET ////////////////////////////////////
    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public byte[] getUserData() {
        return userData;
    }

    public void setUserData(byte[] userData) {
        this.userData = userData;
    }

    public byte[] getExitInfo() {
        return exitInfo;
    }

    public void setExitInfo(byte[] data) {
        this.exitInfo = data;
    }

    public boolean getMoreData() {
        return moreData;
    }

    public void setMoreData(boolean moreData) {
        this.moreData = moreData;
    }

    public boolean getFrameBoundary() {
        return frameBoundary;
    }

    public void setFrameBoundary(boolean boundary) {
        this.frameBoundary = boundary;
    }

    public IWTPTransaction getTransaction() {
        return transaction;
    }

    public void setTransaction(IWTPTransaction transaction) {
        this.transaction = transaction;
    }
}

/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is the Tambur MMS library.
 *
 * The Initial Developer of the Original Code is FlyerOne Ltd.
 * Portions created by the Initial Developer are Copyright (C) 2005
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 * 	Anders Lindh <alindh@flyerone.com>
 *
 * ***** END LICENSE BLOCK ***** */

/**
 * MMEncodingException.java
 * 
 * @author Anders Lindh
 * @copyright Copyright FlyerOne Ltd 2005
 * @version $Revision: 1.1.1.1 $ $Date: 2005/04/14 09:04:10 $
 */
package mmslib.mms;

/**
 * This exception is thrown when an error occurs when decoding a MMMessage
 * 
 * @author Anders Lindh
 */
public class MMDecodingException extends Exception {

	/**
	 * Constructor for MMEncodingException.
	 */
	public MMDecodingException() {
		super();
	}

	/**
	 * Constructor for MMEncodingException.
	 * @param arg0
	 */
	public MMDecodingException(String arg0) {
		super(arg0);
	}

}
 
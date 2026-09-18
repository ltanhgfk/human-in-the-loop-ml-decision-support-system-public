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
 * MMReceiver.java
 * 
 * @author Anders Lindh
 * @copyright Copyright FlyerOne Ltd 2005
 * @version $Revision: 1.1.1.1 $ $Date: 2005/04/14 09:04:10 $
 * @see MimeMessage 
 */
package mmslib.mms;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A class for receiving MMMessages. With this class you can receive MMMessages, but you should
 * really use a servlet container for doing this. This class is highly experimental! 
 * 
 * @author Anders Lindh
 * @copyright Copyright FlyerOne Ltd 2005
 * @version $Revision: 1.1.1.1 $ $Date: 2005/04/14 09:04:10 $
 */
public class MMReceiver {
	
	protected int port = 8189;
	protected ServerSocket serverSocket = null;
	
	/**
	 * Constructor for MMReceiver. 
	 */
	public MMReceiver() {
		super();
		port = 8189;
	}
	
	/**
	 * Get port number
	 */
	public int getPort() { return port; }
	
	/**
	 * Set port
	 */
	public void setPort(int port) { this.port = port; serverSocket = null; }

	/**
	 * Receive raw bytes (emulate http server)
	 */		
	public byte[] receiveBytes() throws Exception {
		if (serverSocket == null)
			serverSocket = new ServerSocket(port);
		
		Socket clientSocket = serverSocket.accept();
		DataInputStream is = new DataInputStream(clientSocket.getInputStream());
		DataOutputStream os = new DataOutputStream(clientSocket.getOutputStream());
				
		String line = null;
		int len = 0;
		
		while ((line = is.readLine()) != null) {
			System.out.println(line);
			if (line.equals("")) break;			
			
			if (line.substring(0, 14).equalsIgnoreCase("Content-Length")) {
				String s = line.substring(line.indexOf(" ")+1, line.length());
				System.out.println(s);
				len = Integer.valueOf(s).intValue();
			} 
		}
		
		if (len == 0) len = is.available();
		byte[] dta = new byte[len];
		int offs = 0;
		
		while (offs < len) {
			int i = is.read(dta, offs, (len-offs));
			if (i == -1) break;
			offs += i;
		}
		
		// create reply
		String reply = "HTTP/1.0 204 OK\n"+
					   "Connection: close\n"+
					   "Server: FlyerOne MMReceiver\n\n";
		
		os.write(reply.getBytes());
		os.flush();
		
		// clientSocket.close();	

		return dta;
	}

	/**
	 * Receive a messages. This is done by binding to the specified port, waiting for connections
	 * and returning the message when done. This function emulates a HTTP 1.0 server, and blocks
	 * until a connection is established.
	 */
	public MMMessage receive() throws Exception {
		byte[] dta = receiveBytes();
		return MMDecoder.decode(dta);
	}	
}

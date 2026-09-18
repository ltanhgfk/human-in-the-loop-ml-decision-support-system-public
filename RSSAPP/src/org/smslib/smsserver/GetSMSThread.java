// SMSLib for Java v3
// A Java API library for sending and receiving SMS via a GSM modem
// or other supported gateways.
// Web Site: http://www.smslib.org
//
// Copyright (C) 2002-2012, Thanasis Delenikas, Athens/GREECE.
// SMSLib is distributed under the terms of the Apache License version 2.0
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.smslib.smsserver;

import java.io.FileInputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import model.Config;

import org.smslib.ICallNotification;
import org.smslib.IInboundMessageNotification;
import org.smslib.IOrphanedMessageNotification;
import org.smslib.IOutboundMessageNotification;
import org.smslib.IQueueSendingNotification;
import org.smslib.InboundMessage;
import org.smslib.InboundMessage.MessageClasses;
import org.smslib.Library;
import org.smslib.Message.MessageTypes;
import org.smslib.OutboundMessage;
import org.smslib.Service;
import org.smslib.helper.Logger;
import org.smslib.smsserver.SMSServer;
import org.smslib.smsserver.gateways.AGateway;
import org.smslib.smsserver.interfaces.Interface;
import org.smslib.smsserver.interfaces.Interface.InterfaceTypes;

/**
 * SMSServer Application.
 */
public class GetSMSThread extends Thread
{
	//@Override
    public void run()
    {
		try{            
            System.out.println("Dog running.......");  
            SMSServer app = new SMSServer();
            try {
 				app.run();
 				System.out.println("SMSServer exiting normally.");
            }
            catch (Exception e)
 	   		{
         	   System.out.println("SMSServer Error: ");
 	   			try
 	   			{
 	   				Service.getInstance().stopService();
 	   			}
 	   			catch (Exception e1)
 	   			{
 	   				System.out.println("SMSServer error while shutting down: ");
 	   			}
 	   		}
        }
        catch(Exception e)
        {
            System.err.println(e.getMessage());
        }
		
    	/*int i=0;
        while(true)
        {
        	i++;
            try{
                sleep(1000);
                System.out.println(" " + i);  
                SMSServer app = new SMSServer();
                try {
     				app.run();
     				System.out.print("SMSServer exiting normally.");
                }
                catch (Exception e)
     	   		{
             	   System.out.print("SMSServer Error: ");
     	   			try
     	   			{
     	   				Service.getInstance().stopService();
     	   			}
     	   			catch (Exception e1)
     	   			{
     	   				System.out.print("SMSServer error while shutting down: ");
     	   			}
     	   		}
            }
            catch(Exception e)
            {
                System.err.println(e.getMessage());
            }
        }*/
    }
}

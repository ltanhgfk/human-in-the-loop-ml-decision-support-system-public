package org.smslib.smsserver;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

import org.smslib.*;
import org.smslib.smsserver.SMSServer;
 
/**
 *
 * @author
 */
public class RunApplicationsThread extends JFrame implements ActionListener{
    private JButton jbtStart = new JButton("Start");
    private JButton jbtStop = new JButton("Stop");
 
	public RunApplicationsThread()
	{
	    setTitle("TestActionEvent");
	 
	    getContentPane().setLayout(new FlowLayout());
	    getContentPane().add(jbtStart);
	    getContentPane().add(jbtStop);
	 
	    jbtStart.addActionListener(this);
	    jbtStop.addActionListener(this);
	}
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        RunApplicationsThread frame = new RunApplicationsThread();
        frame.setTitle("ASSMMS");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);
        frame.setVisible(true);
    }
 
    public void actionPerformed(ActionEvent e)
    {
    	/*Thread threadcat = new Thread(cat);
    	Thread threadrabbit = new Thread(rabbit);
    	Thread threaddog = new Thread(dog);
    	//Thread thread = new Thread(draadje);
        //thread.start();  
    	if (e.getSource() == jbtStart)
        {
            threadcat.start();
            threadrabbit.start();
            threaddog.start();
        }
        else if (e.getSource() == jbtStop)
        {
            //draadje.stop();
        	cat.stop();
	        rabbit.stop();
	        dog.stop();
        }*/
    	
    	//new ProcessMessagesThread().start();
        //new ProcessModelsThread().start();
        //new GetSMSThread().start();
    	ProcessMessagesThread cat = new ProcessMessagesThread();
        ProcessModelsThread rabbit = new ProcessModelsThread();
        //GetSMSThread  dog = new GetSMSThread();
        
        if (e.getSource() == jbtStart)
        {        	
	        cat.start();
	        rabbit.start();
	        //dog.start();
        }
        else if (e.getSource() == jbtStop)
        {
        	//cat.setloop(false);
        	rabbit.setloop(false);        	
        	
        	//-----Stop running SMSServer----------//
        	/*/Service sv = new Service();
        	SMSServer ssv = new SMSServer();
        	try {
				ssv.stopInterfaces();
			} catch (Exception e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
        	try {        		
    			Service.getInstance().stopService();
				//sv.stopService();
			} catch (TimeoutException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (GatewayException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (SMSLibException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        	//-----------------------------------------/*/
        }
    }
}
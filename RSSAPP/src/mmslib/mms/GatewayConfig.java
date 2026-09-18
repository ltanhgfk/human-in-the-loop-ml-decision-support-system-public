package mmslib.mms;

import java.net.*;
import java.io.*;
import java.util.*;

public class GatewayConfig 
{
    public boolean execCommand(String commandline)
    {
    	boolean ok = false;
        try
        {
            Process pro = Runtime.getRuntime().exec("cmd.exe /c "+commandline);//
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(pro.getInputStream())); 
            
            String line;
            while((line = bufferedReader.readLine())!=null)
            {
                line = line.trim();
                if(line.contains("OK"))
                {
                	ok = true;
                }                
            }
            //pro.waitFor();      
        }
        catch(IOException e)
        {
            System.err.println(e);
            e.printStackTrace();
        }
        return ok;
    }
    
    public boolean setMetric(String interfaceName, int metric, String adapterName) throws IOException
    {
    	boolean ok = false;
    	String cmdSetInfMetric="netsh interface ipv4 set interface \"" + interfaceName+"\" metric="+metric;	
    	
    	String ifx = getInterfaceIndex(adapterName);
		String cmdSetGwMetric="route change 0.0.0.0 mask 0.0.0.0 192.168.1.1 metric " + metric + " if " + ifx;	
		
		System.out.println("cmdSetInfMetric: " + cmdSetInfMetric);
		System.out.println("cmdSetGwMetric: " + cmdSetGwMetric);
    	
		if(!execCommand(cmdSetGwMetric) || !execCommand(cmdSetInfMetric))
		//if(execCommand(cmdSetGwMetric))
		{
			System.out.println("cmdSetGwMetric2: " + cmdSetGwMetric);
			ok = true;
		}    	
    	return ok;
    }
    
    public String getInterfaceIndex(String adapterName)
    {
    	String index = "";
        try
        {
            Process pro = Runtime.getRuntime().exec("cmd.exe /c route print if -4");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(pro.getInputStream())); 

            String line;
            int i=0;
            while((line = bufferedReader.readLine())!=null)
            {
                line = line.trim();
                if(line.toLowerCase().contains(adapterName.toLowerCase()))
                {
                	int pos = line.indexOf("...");
                	index = line.substring(0,pos);                	
                	//System.out.println("commandline: " +i+"-" + index);
                }
                i++;
            }
            //pro.waitFor();      
        }
        catch(IOException e)
        {
            System.err.println(e);
            e.printStackTrace();
        }
        return index;
    }
    
   
    
	/*public static void main(String[] args)
    {
		String[] tokenArrayString = new String[10];
        try
        {
        	GatewayConfig gc = new GatewayConfig();        	
        	tokenArrayString = gc.getNetConnectionId();
        	for(int i=0;i<tokenArrayString.length;i++)
        	{        		
        		System.out.println("sam: " +tokenArrayString[i]);
        		if(tokenArrayString[i]!=null)
        		{
	        		if(tokenArrayString[i].toLowerCase().contains("viettel"))
					{
						String commandline="netsh interface ipv4 set interface \"" + tokenArrayString[i]+"\" metric=10";
						System.out.println("commandline: " + commandline);
						gc.execCommand(commandline);
						if(gc.execCommand(commandline)==true);
						{
							//new MmsGetter(gateway, port, mmsc, row.getid());
							commandline="netsh interface ipv4 set interface \"" + tokenArrayString[i]+"\" metric=300";
							System.out.println("commandline: " + commandline);
							gc.execCommand(commandline);
						}
					}
        		}
        	}
        	
        	//getInterfaceIndex("Mobile Connect - 3G Network Card #2");
        	System.out.println("KQ: " +setMetric("Mobile Broadband Connection 2", 10, "Mobile Connect - 3G Network Card #2"));
        	
        }
        catch(Exception e)
        {
            System.out.println( e);
            e.printStackTrace();
        }
    }*/
}


////////////////////////////////////////////////////////////////////////////////////////////


/*
 *
 * public String getNetConnectionId(String )
    {	String[] str = new String[10];
        try
        {        	
            Process pro = Runtime.getRuntime().exec("cmd.exe /c wmic nic where \"netconnectionid like '%'\" get netconnectionid");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(pro.getInputStream())); 

            String line;            
            int i=0;
            while((line = bufferedReader.readLine())!=null)
            {                
            	if(line.compareTo("")!=0)
            	{  
            		str[i]=line.trim();   
            		//System.out.println("commandline: " +i+"-" + str[i]);            		
            		i++;
            	}                
            }
            //pro.waitFor();
        }
        catch(IOException e)
        {
            System.err.println(e);
            e.printStackTrace();
        }
        return str;
    }
 * 
 * private void parseWindows()
    {
        try
        {
            Process pro = Runtime.getRuntime().exec("cmd.exe /c route print");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(pro.getInputStream())); 

            String line;
            while((line = bufferedReader.readLine())!=null)
            {
                line = line.trim();
                String [] tokens = Tokenizer.parse(line, ' ', true , true);// line.split(" ");
                if(tokens.length == 5 && tokens[0].equals("0.0.0.0"))
                {
                    _gateway = tokens[2];
                    _ip = tokens[3];
                    return;
                }
            }
            //pro.waitFor();      
        }
        catch(IOException e)
        {
            System.err.println(e);
            e.printStackTrace();
        }
    }
 * 
public static void main(String[] args)
    {
        try
        {
            GatewayConfig pr = GatewayConfig.getInstance();
            System.out.println( "Gateway: " + pr.getGateway() );
            System.out.println( "IP: " + pr.getLocalIPAddress() );
        }
        catch(Exception e)
        {
            System.out.println( e);
            e.printStackTrace();
        }
    }
    
public GatewayConfig ()
{
    parse();
}

private static boolean isWindows ()
{
    String os = System.getProperty ( "os.name" ).toUpperCase ();
    return os.contains( "WINDOWS" ) ;
}

private static boolean isLinux ()
{
    String os = System.getProperty ( "os.name" ).toUpperCase ();
    return os.contains( "LINUX" )  ;
}

public String getLocalIPAddress()
{
    return _ip;
}

public String getGateway()
{
    return _gateway;
}

public static GatewayConfig getInstance()
{
    if(_instance == null)
    {
        _instance = new GatewayConfig();
    }
    return _instance;
}

private void parse() 
{
    if(isWindows())
    {
        parseWindows();
    }
    else if(isLinux())
    {
        parseLinux();
    }
}

public String[] getDefaultGateway(String commandline)
    {
    	String [] result = new String[2];
        try
        {
            //Process pro = Runtime.getRuntime().exec("cmd.exe /c "+commandline);
        	Process pro = Runtime.getRuntime().exec("cmd.exe /c route print");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(pro.getInputStream())); 
            
            String line;
            while((line = bufferedReader.readLine())!=null)
            {
                line = line.trim();
                String [] tokens = Tokenizer.parse(line, ' ', true , true);// line.split(" ");
                if(tokens.length == 5 && tokens[0].equals("0.0.0.0"))//=> chi dia chi nay la default
                {
                    result[0] = tokens[2];
                    result[1] = tokens[3];
                    return;
                }
            }
            //pro.waitFor();      
        }
        catch(IOException e)
        {
            System.err.println(e);
            e.printStackTrace();
        }
        return result;
    }

    private void parseLinux()
    {
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader("/proc/net/route"));
            String line;
            while((line = reader.readLine())!=null)
            {
                line = line.trim();
                String [] tokens = Tokenizer.parse(line, '\t', true , true);// line.split(" ");
                if(tokens.length > 1 && tokens[1].equals("00000000"))
                {
                    String gateway = tokens[2]; //0102A8C0
                    if(gateway.length() == 8)
                    {
                        String[] s4 = new String[4];
                        s4[3] = String.valueOf(Integer.parseInt(gateway.substring(0, 2), 16));
                        s4[2] = String.valueOf(Integer.parseInt(gateway.substring(2, 4), 16));
                        s4[1] = String.valueOf(Integer.parseInt(gateway.substring(4, 6), 16));
                        s4[0] = String.valueOf(Integer.parseInt(gateway.substring(6, 8), 16));
                        _gateway = s4[0] + "." + s4[1] + "." + s4[2] + "." + s4[3];
                    }
                    String iface = tokens[0];
                    NetworkInterface nif = NetworkInterface.getByName(iface);
                    Enumeration addrs = nif.getInetAddresses();
                    while(addrs.hasMoreElements())
                    {
                        Object obj = addrs.nextElement();
                        if(obj instanceof Inet4Address)
                        {
                            _ip =  obj.toString();
                            if(_ip.startsWith("/")) _ip = _ip.substring(1);
                            return;
                        }
                    }
                    return;
                }
            }
            reader.close();
        }
        catch(IOException e)
        {
            System.err.println(e);
            e.printStackTrace();
        }
    }
}


public class GatewayConfig
{
    public boolean isWindows ()
    {
        String os = System.getProperty ( "os.name" ).toUpperCase ();
        return os.contains( "WINDOWS" ) ;
    }

    public boolean isLinux ()
    {
        String os = System.getProperty ( "os.name" ).toUpperCase ();
        return os.contains( "LINUX" )  ;
    }    
  */  
    /*public static void main(String args[]) throws UnknownHostException
    {
    	System.out.print("ket qua" + execCommand("route delete 0.0.0.0 mask 0.0.0.0 YOUR_MMS_GATEWAY_HOST"));
    	
    }*/
 /*   
    public boolean execCommand(String commandline)
    {
    	boolean ok = false;
        try
        {
            Process pro = Runtime.getRuntime().exec("cmd.exe /c "+commandline);//
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(pro.getInputStream())); 
            
            String line;
            while((line = bufferedReader.readLine())!=null)
            {
                line = line.trim();
                if(line.compareTo("OK!")==0)
                {
                	ok = true;
                }                
            }
            //pro.waitFor();      
        }
        catch(IOException e)
        {
            System.err.println(e);
            e.printStackTrace();
        }
        return ok;
    }    
        
    public String[] getDefaultGateway(String commandline)
    {
    	String [] result = new String[2];
        try
        {
            Process pro = Runtime.getRuntime().exec("cmd.exe /c "+commandline);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(pro.getInputStream())); 
            
            String line;
            while((line = bufferedReader.readLine())!=null)
            {
                line = line.trim();
                String [] tokens = Tokenizer.parse(line, ' ', true , true);// line.split(" ");
                if(tokens.length == 5 && tokens[0].equals("0.0.0.0"))//=> chi dia chi nay la default
                {
                    result[0] = tokens[2];
                    result[1] = tokens[3];
                }
            }
            //pro.waitFor();      
        }
        catch(IOException e)
        {
            System.err.println(e);
            e.printStackTrace();
        }
        return result;
    }
    
    /*public boolean execCommandLinux(String commandline)
    {
    	try
        {
            BufferedReader reader = new BufferedReader(new FileReader("/proc/net/route"));
            String line;
            while((line = reader.readLine())!=null)
            {
                line = line.trim();
                String [] tokens = Tokenizer.parse(line, '\t', true , true);// line.split(" ");
                if(tokens.length > 1 && tokens[1].equals("00000000"))
                {
                    String gateway = tokens[2]; //0102A8C0
                    if(gateway.length() == 8)
                    {
                        String[] s4 = new String[4];
                        s4[3] = String.valueOf(Integer.parseInt(gateway.substring(0, 2), 16));
                        s4[2] = String.valueOf(Integer.parseInt(gateway.substring(2, 4), 16));
                        s4[1] = String.valueOf(Integer.parseInt(gateway.substring(4, 6), 16));
                        s4[0] = String.valueOf(Integer.parseInt(gateway.substring(6, 8), 16));
                        _gateway = s4[0] + "." + s4[1] + "." + s4[2] + "." + s4[3];
                    }
                    String iface = tokens[0];
                    NetworkInterface nif = NetworkInterface.getByName(iface);
                    Enumeration addrs = nif.getInetAddresses();
                    while(addrs.hasMoreElements())
                    {
                        Object obj = addrs.nextElement();
                        if(obj instanceof Inet4Address)
                        {
                            _ip =  obj.toString();
                            if(_ip.startsWith("/")) _ip = _ip.substring(1);
                            return;
                        }
                    }
                    return;
                }
            }
            reader.close();
        }
        catch(IOException e)
        {
            System.err.println(e);
            e.printStackTrace();
        }
    }*/

   /*private void parseLinux()
    {
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader("/proc/net/route"));
            String line;
            while((line = reader.readLine())!=null)
            {
                line = line.trim();
                String [] tokens = Tokenizer.parse(line, '\t', true , true);// line.split(" ");
                if(tokens.length > 1 && tokens[1].equals("00000000"))
                {
                    String gateway = tokens[2]; //0102A8C0
                    if(gateway.length() == 8)
                    {
                        String[] s4 = new String[4];
                        s4[3] = String.valueOf(Integer.parseInt(gateway.substring(0, 2), 16));
                        s4[2] = String.valueOf(Integer.parseInt(gateway.substring(2, 4), 16));
                        s4[1] = String.valueOf(Integer.parseInt(gateway.substring(4, 6), 16));
                        s4[0] = String.valueOf(Integer.parseInt(gateway.substring(6, 8), 16));
                        _gateway = s4[0] + "." + s4[1] + "." + s4[2] + "." + s4[3];
                    }
                    String iface = tokens[0];
                    NetworkInterface nif = NetworkInterface.getByName(iface);
                    Enumeration addrs = nif.getInetAddresses();
                    while(addrs.hasMoreElements())
                    {
                        Object obj = addrs.nextElement();
                        if(obj instanceof Inet4Address)
                        {
                            _ip =  obj.toString();
                            if(_ip.startsWith("/")) _ip = _ip.substring(1);
                            return;
                        }
                    }
                    return;
                }
            }
            reader.close();
        }
        catch(IOException e)
        {
            System.err.println(e);
            e.printStackTrace();
        }
    }
}*/

/* How to change default gateway on windows by command line.
By admin on November 10, 2007 in Windows Right, you can change you default gateway on windows by route command , it’s very useful. Other than , you can add route to other network by this command too. I have example for you too. ( I get from link that :) )

Syntax route [-f] [-p] [Command[Destination] [mask Network] [Gateway] [metric Metric]] [if Interface]]

Example:

To display the entire contents of the IP routing table, type:

route print

To display the routes in the IP routing table that begin with 10., type:

route print 10.*

To add a default route with the default gateway address of 192.168.12.1, type:

route add 0.0.0.0 mask 0.0.0.0 192.168.12.1

To add a route to the destination 10.41.0.0 with the subnet mask of 255.255.0.0 and the next hop address of 10.27.0.1, type:

route add 10.41.0.0 mask 255.255.0.0 10.27.0.1

To add a persistent route to the destination 10.41.0.0 with the subnet mask of 255.255.0.0 and the next hop address of 10.27.0.1, type:

route -p add 10.41.0.0 mask 255.255.0.0 10.27.0.1

To add a route to the destination 10.41.0.0 with the subnet mask of 255.255.0.0, the next hop address of 10.27.0.1, and the cost metric of 7, type:

route add 10.41.0.0 mask 255.255.0.0 10.27.0.1 metric 7

To add a route to the destination 10.41.0.0 with the subnet mask of 255.255.0.0, the next hop address of 10.27.0.1, and using the interface index 0×3, type: 

route add 10.41.0.0 mask 255.255.0.0 10.27.0.1 if 0×3

To delete the route to the destination 10.41.0.0 with the subnet mask of 255.255.0.0, type:

route delete 10.41.0.0 mask 255.255.0.0

To delete all routes in the IP routing table that begin with 10., type:

route delete 10.*

To change the next hop address of the route with the destination of 10.41.0.0 and the subnet mask of 255.255.0.0 from 10.27.0.1 to 10.27.0.25, type:

route change 10.41.0.0 mask 255.255.0.0 10.27.0.25


netsh interface ipv4 set interface  "Wireless Network Connection" metric=10

wmic nic where "netconnectionid like '%'" get netconnectionid

 */



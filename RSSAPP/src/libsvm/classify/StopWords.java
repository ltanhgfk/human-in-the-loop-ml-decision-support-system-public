package libsvm.classify;

import java.util.*;
import java.util.regex.Pattern;
import java.io.*;
import java.net.UnknownHostException;

import model.Config;

import vn.hus.nlp.tokenizer.VietTokenizer;


public class StopWords
{
	public String removeStopWord(String text)
    {
		String restWord="";
    	try
		{
    		text = text.toLowerCase();
    		VietTokenizer tokenizer = new VietTokenizer();
			String[] tokenArrayString;			
			BufferedReader stopwordFile = new BufferedReader(new InputStreamReader(new FileInputStream("./classifications/stopwordlist.txt"), "UTF8"));				
			
			Set<String> stopwordSet = new LinkedHashSet<String>();
			for(String line;(line = stopwordFile.readLine()) != null;)
			{
				stopwordSet.add(line.trim().toLowerCase());
			}
			stopwordFile.close();
			
			ArrayList<String> keyArray = new ArrayList<String>();
			tokenArrayString = tokenizer.tokenize(text);
			
				tokenArrayString = tokenArrayString[0].replaceAll("[,:;?“”–!%&+-/[/]<>$#@^={}|.1234567890()\"'\\/…]", "").split(" ");

				for (int j=0;j<tokenArrayString.length;j++)
				{
					keyArray.add(tokenArrayString[j].toLowerCase());
				}
			
			for(int i=0; i<keyArray.size(); i++)
			{
				if(!stopwordSet.contains(keyArray.get(i)))
				{
					restWord = restWord + keyArray.get(i) + " ";
				}
			}
		}
		catch (Exception e) 
	       {
	         e.printStackTrace();
	       }
    	return restWord.trim();
    }
	
	public String tokenString(String text)
    {
		String restWord="";
    	try
		{
    		text = text.toLowerCase();
    		VietTokenizer tokenizer = new VietTokenizer();
			String[] tokenArrayString;
			
			ArrayList<String> keyArray = new ArrayList<String>();
			tokenArrayString = tokenizer.tokenize(text);
			
				//tokenArrayString = tokenArrayString[0].replaceAll("[,:;?Ã¢â‚¬Å“Ã¢â‚¬ï¿½Ã¢â‚¬â€œ!%&+-<>$#@^={}|.1234567890()\"'\\/Ã¢â‚¬Â¦]", "").split(" ");//loi font 
				tokenArrayString = tokenArrayString[0].replaceAll("[,:;?“”–!%&+-/[/]<>$#@^={}|.1234567890()\"'\\/…]", "").split(" ");

				for (int j=0;j<tokenArrayString.length;j++)
				{
					keyArray.add(tokenArrayString[j].toLowerCase());
				}
			
			for(int i=0; i<keyArray.size(); i++)
			{
				restWord = restWord + keyArray.get(i) + " ";
			}
		}
		catch (Exception e) 
	       {
	         e.printStackTrace();
	       }
    	return restWord.trim();
    }
	
	public void removeDuplicatesLines(String filename) {
    	try
    	{
	        //BufferedReader reader = new BufferedReader(new FileReader(filename));
    		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF8"));
	        Set<String> lines = new LinkedHashSet<String>(); //HashSet<String>(1000000); //10000 maybe should be bigger
	        String line;
	        while ((line = reader.readLine()) != null)
	        {
	            lines.add(line);
	        }
	        reader.close();
	        //BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
	        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "UTF8"));
	        for (String unique : lines) {
	            writer.write(unique);
	            writer.newLine();
	        }
	        writer.close();
    	}
    	catch(Exception ex)
    	{
    		System.out.print(ex);
    	}
    }	
		
    public void removeDublicate()
    {    	
    	try
    	{
    		Config keyword = new Config();
    		keyword = keyword.getConfigBycode("autokeyword");
    		String fileString="";
    		String lastFile="";
    		if(keyword.getvaluebool()==true)
    		{
    			fileString = "./classifications/autokeywordlistTemp.txt";
    			lastFile = "./classifications/autokeywordlist.txt";
    		}
    		else
    		{
    			fileString = "./classifications/keywordlistTemp.txt";
    			lastFile = "./classifications/keywordlist.txt";
    		}
		 	BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileString), "UTF8"));
		 	BufferedWriter keyfoundFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(lastFile), "UTF8"));	 
	        
		 	StringBuffer buffer = new StringBuffer();
	        String str;
	        while((str=br.readLine())!=null)
	        {
		        buffer.append(str);
		        buffer.append(" ");
	        }
	        ArrayList list = new ArrayList();
	        StringTokenizer st = new StringTokenizer(buffer.toString().toLowerCase());
	                while(st.hasMoreTokens())
	                {
	                        String s = st.nextToken();
	                        list.add(s);
	                }
	        HashSet set = new HashSet(list);
	        List arrayList = new ArrayList(set);
	        Collections.sort(arrayList);
	        for (Object ob : arrayList)
	        {
	        	keyfoundFile.write((String) ob.toString()); 
			    keyfoundFile.newLine();
	        	//System.out.println(ob.toString());
	        }
	        keyfoundFile.close();
    	}
        catch (Exception e) 
	       {
	         e.printStackTrace();
	       }
    }	
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/*public static void main(String args[]) throws UnknownHostException
    {
    	try
		{
    		String text = "BÃ¡ÂºÂ¥t kÃƒÂ¬ 1 usd tÃ„Æ’ng trÃ†Â°Ã¡Â»Å¸ng nÃƒÂ o lÃƒÂ  cÃ¡Â»Â§a ASEAN cÃ…Â©ng cÃƒÂ³ tÃ¡Â»â€ºi 6 cent xuÃ¡ÂºÂ¥t khÃ¡ÂºÂ©u lÃƒÂ  cÃ¡Â»Â§a NhÃ¡ÂºÂ­t BÃ¡ÂºÂ£n tÃ¡Â»â€ºi ASEAN NhÃ†Â° bÃ¡ÂºÂ¡n Ã„â€˜ÃƒÂ£ biÃ¡ÂºÂ¿t, BiÃ¡Â»Æ’n Ã„ï¿½ÃƒÂ´ng lÃƒÂ  tuyÃ¡ÂºÂ¿n Ã„â€˜Ã†Â°Ã¡Â»ï¿½ng biÃ¡Â»Æ’n tÃ¡Â»â€ºi chiÃ¡ÂºÂ¿n lÃ†Â°Ã¡Â»Â£c. ThÃ†Â°Ã†Â¡ng mÃ¡ÂºÂ¡i vÃƒÂ  cÃƒÂ¡c sÃ¡ÂºÂ£n phÃ¡ÂºÂ©m cÃ…Â©ng vÃ¡ÂºÂ­y.";
			text = text.replaceAll("[,:;.\"'\\/]", "");//^truoc neu muon khong thay the cac ky tu sau do
    		VietTokenizer tokenizer = new VietTokenizer();
			String[] tokenArrayString;			
			BufferedReader stopwordFile = new BufferedReader(new InputStreamReader(new FileInputStream("./classifications/stopwordlist.txt"), "UTF-8"));				
			
			Set<String> stopwordSet = new LinkedHashSet<String>();
			for(String line;(line = stopwordFile.readLine()) != null;)
			{
				stopwordSet.add(line.trim().toLowerCase());				
			}
			stopwordFile.close();			
			
			ArrayList<String> keyArray = new ArrayList<String>();
			tokenArrayString = tokenizer.tokenize(text);
			for (int i=0;i<tokenArrayString.length;i++)
			{			
				tokenArrayString = tokenArrayString[i].split(" ");
				for (int j=0;j<tokenArrayString.length;j++)
				{
					keyArray.add(tokenArrayString[j].toLowerCase());
				}
			}			

			BufferedWriter keyfoundFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("./classifications/autokeywordlist.txt",true), "UTF8"));
			int i=0;
			while(i < keyArray.size())
			{
				if(!stopwordSet.contains(keyArray.get(i)))
				{
					//ghi vao file nhung tu con lai
				 	keyfoundFile.append(keyArray.get(i));
				 	keyfoundFile.newLine();
				   	//System.out.println(keyArray.get(i));
				}				
			    i++;        
			}
			keyfoundFile.close();
		}
		catch (Exception e) 
	       {
	         e.printStackTrace();
	       }
    }*/
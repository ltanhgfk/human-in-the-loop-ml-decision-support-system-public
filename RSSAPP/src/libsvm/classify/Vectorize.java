package libsvm.classify;

import vn.hus.nlp.tokenizer.*;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import java.io.InputStreamReader;

import model.Config;


public class Vectorize {

	public String CreateVector(String text)
	{
		String Vector = "";
		try
		{			
			String[] tokenArrayString;			
			ArrayList<String> keywordArray = new ArrayList<String>();
			
			Config keyword = new Config();
			keyword = keyword.getConfigBycode("autokeyword");			
			
			if(keyword.getvaluebool()==true)
			{
				BufferedReader keywordFile = new BufferedReader(new InputStreamReader(new FileInputStream("./classifications/autokeywordlist.txt"), "UTF8"));
				for(String line;(line = keywordFile.readLine()) != null;)
				{
					keywordArray.add(line.trim().toLowerCase());				
				}
				keywordFile.close();
				StopWords sw =  new StopWords();
				String tokenStr = sw.removeStopWord(text);
				Set<String> keySet = new LinkedHashSet<String>();
							
				tokenArrayString = tokenStr.split(" ");
				for (int j=0;j<tokenArrayString.length;j++)
					{
						keySet.add(tokenArrayString[j].toLowerCase());
					}
				
				int i=0;
				while(i < keywordArray.size())
				{
				    if(keySet.contains(keywordArray.get(i)))
				    {
				    	Vector = Vector + i + ":" + 1 + " ";
				    }
				    i++;        
				}
			}
			else
			{
				// Legacy punctuation normalization; kept intentionally simple for the public UTF-8 source.
				text = text.toLowerCase();
				VietTokenizer tokenizer = new VietTokenizer();
				BufferedReader keywordFile = new BufferedReader(new InputStreamReader(new FileInputStream("./classifications/keywordlist.txt"), "UTF8"));
				for(String line;(line = keywordFile.readLine()) != null;)
				{
					keywordArray.add(line.trim().toLowerCase());				
				}
				keywordFile.close();
				Set<String> keySet = new LinkedHashSet<String>();
				tokenArrayString = tokenizer.tokenize(text);
				tokenArrayString = tokenArrayString[0].replaceAll("[,;:!?%&+\\-<>$#@^={}|.1234567890()\"\'\\/?]", "").split(" ");
				for (int j=0;j<tokenArrayString.length;j++)
					{
						keySet.add(tokenArrayString[j].toLowerCase());
					}
				int i=0;
				while(i < keywordArray.size())
				{
				    if(keySet.contains(keywordArray.get(i)))
				    {
				    	Vector = Vector + i + ":" + 1 + " ";
				    }
				    i++;        
				}
			}			
		}
		catch (Exception e) 
	       {
	         e.printStackTrace();
	       }
		return Vector.trim();
	}
}

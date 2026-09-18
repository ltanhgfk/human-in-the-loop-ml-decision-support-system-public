package libsvm.classify;

/*
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
*/
import model.Config;
import utils.DataFileReader;
import ca.uwo.csd.ai.nlp.kernel.KernelManager;
import ca.uwo.csd.ai.nlp.kernel.LinearKernel;
import ca.uwo.csd.ai.nlp.libsvm.svm_model;
import ca.uwo.csd.ai.nlp.libsvm.ex.Instance;
import ca.uwo.csd.ai.nlp.libsvm.ex.SVMPredictor;

public class ClassifySms {
	
	public String classify(String inputFileName)
    {
		String result="";
    	try
    	{
    		//Register kernel function: Linear
            KernelManager.setCustomKernel(new LinearKernel()); 
            
			Config keyword = new Config();
			keyword = keyword.getConfigBycode("autokeyword");
			String fileString="";
			if(keyword.getvaluebool()==true)
			{
				fileString = "./classifications/autosms.model";
			}
			else
			{
				fileString = "./classifications/sms.model";
			}
			svm_model model = SVMPredictor.loadModel(fileString);
			
			//Read test file
	        Instance[] testingInstances = DataFileReader.readDataFile(inputFileName);
	        
	        //Predict results
	        double[] predictions = SVMPredictor.predict(testingInstances, model, true);
	        //writeOutputs("./samples/result.txt", predictions);
	        //System.out.print(String.format("%.0f\n",predictions[0]));
	        result = String.format("%.0f",predictions[0]);
		} 
    	catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return result.trim();
    }
    /*
    private static void writeOutputs(String outputFileName, double[] predictions) throws IOException 
    {
	    BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName));
	    for (double p : predictions) {
	    writer.write(String.format("%.0f\n", p));
	    }
    	writer.close();
    } 
    */   
}

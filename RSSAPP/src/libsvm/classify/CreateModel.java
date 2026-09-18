	package libsvm.classify;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import model.*;
import utils.DataFileReader;
import ca.uwo.csd.ai.nlp.kernel.CompositeKernel;
import ca.uwo.csd.ai.nlp.kernel.CustomKernel;
import ca.uwo.csd.ai.nlp.kernel.KernelManager;
import ca.uwo.csd.ai.nlp.kernel.LinearKernel;
import ca.uwo.csd.ai.nlp.kernel.RBFKernel;
import ca.uwo.csd.ai.nlp.kernel.TreeKernel;
import ca.uwo.csd.ai.nlp.libsvm.svm_model;
import ca.uwo.csd.ai.nlp.libsvm.svm_parameter;
import ca.uwo.csd.ai.nlp.libsvm.ex.Instance;
import ca.uwo.csd.ai.nlp.libsvm.ex.SVMPredictor;
import ca.uwo.csd.ai.nlp.libsvm.ex.SVMTrainer;

public class CreateModel {
	
	public void createTrainingFile() 
	{
		try
		{
			String vector="";
			Vectorize vt = new Vectorize();
			Config cg = new Config();
			cg = cg.getConfigBycode("systemconfig");
			MMS ms = new MMS();			
			ArrayList<MMS> mmslist =  new ArrayList<MMS>();
			
			ChuyenMon mj = new ChuyenMon();
			
			if(cg.getvaluechar().compareTo("semi-auto")==0)
			{
				mmslist = ms.getAllMMSForTrainByHuman(true, true);
			}
			else if(cg.getvaluechar().compareTo("auto")==0)
			{
				mmslist = ms.getAllMMSForTrainByMachine(true, true);
			}
			
			Config keyword = new Config();
			keyword = keyword.getConfigBycode("autokeyword");
			String fileString="";
			if(keyword.getvaluebool()==true)
			{
				fileString = "./classifications/autosms.train";
			}
			else
			{
				fileString = "./classifications/sms.train";
			}			
			
			//Khi tao file train khong append ma ghi het tu dau
    		BufferedWriter  bufferWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileString), "UTF8"));
    		
			for (int i = 0; i < mmslist.size(); i++ )
	            {	            	
					vector = vt.CreateVector(mmslist.get(i).getmsg());
					
					if(cg.getvaluechar().compareTo("semi-auto")==0)
					{				
						mj = mj.getMajorById(mmslist.get(i).getmajoridByHuman());						
					}
					else if(cg.getvaluechar().compareTo("auto")==0)
					{
						mj = mj.getMajorById(mmslist.get(i).getmajoridByMachine());
					}

					if(vector.compareTo("") != 0)
					{
						vector = mj.getCode() + " " + vector;
						vector = vector.trim();						
			    	    //bufferWriter.append(vector);
						bufferWriter.write(vector);//xoa cu thay moi
			    	    bufferWriter.newLine();
					}					
	            }
			bufferWriter.close();
			StopWords st = new StopWords();
			st.removeDuplicatesLines(fileString);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	
	public void trainLinearKernel() throws IOException, ClassNotFoundException 
    {   
		String trainFileName = "";
        String modelFileName = "";
		Config keyword = new Config();
		keyword = keyword.getConfigBycode("autokeyword");
		if(keyword.getvaluebool()==true)
		{
			trainFileName = "./classifications/autosms.train";
	        modelFileName = "./classifications/autosms.model";
		}
		else
		{
			trainFileName = "./classifications/sms.train";
	        modelFileName = "./classifications/sms.model";
		}
        
        //Read training file
        Instance[] trainingInstances = DataFileReader.readDataFile(trainFileName);
        
        //Setup parameters
        svm_parameter param = new svm_parameter();
        
        //Register kernel function
        KernelManager.setCustomKernel(new LinearKernel());        
        //KernelManager.setCustomKernel(new CompositeKernel());
        //KernelManager.setCustomKernel(new RBFKernel(param));        
        
        //Train the model
        svm_model model = SVMTrainer.train(trainingInstances, param);
                
        //Save the trained model
        SVMTrainer.saveModel(model, modelFileName);
        //Test accuracy
        SVMTrainer.doCrossValidation(trainingInstances, param, 10, true);
        //SVMTrainer.doInOrderCrossValidation(trainingInstances, param, 10, true);
    }	
}

/*////////////////////////////////////////////////////////////////////////////////////////////
public void trainLinearKernel() throws IOException, ClassNotFoundException 
{   
	String trainFileName = "";
    String modelFileName = "";
	Config keyword = new Config();
	keyword = keyword.getConfigBycode("autokeyword");
	if(keyword.getvaluebool()==true)
	{
		trainFileName = "./classifications/autosms.train";
        modelFileName = "./classifications/autosms.model";
	}
	else
	{
		trainFileName = "./classifications/sms.train";
        modelFileName = "./classifications/sms.model";
	}
    
    //Read training file
    Instance[] trainingInstances = DataFileReader.readDataFile(trainFileName);
    
    //Setup parameters
    svm_parameter param = new svm_parameter();
    
    //Register kernel function
    //KernelManager.setCustomKernel(new CompositeKernel());        
    KernelManager.setCustomKernel(new LinearKernel());        
    //KernelManager.setCustomKernel(new RBFKernel(param));        
    
    //Train the model
    System.out.println("Training started...");
    svm_model model = SVMTrainer.train(trainingInstances, param);
    System.out.println("Training completed.");
            
    //Save the trained model
    SVMTrainer.saveModel(model, modelFileName);
    //model = SVMPredictor.loadModel("./samples/a2a.model");
    
    //Read test file
    //Instance[] testingInstances = DataFileReader.readDataFile(testFileName);
    
    //Predict results
    //double[] predictions = SVMPredictor.predict(testingInstances, model, true);
    //writeOutputs(outputFileName, predictions);// thay vi ghi ra thi luu vao csdl
    //System.out.print(predictions[0]);
    
    SVMTrainer.doCrossValidation(trainingInstances, param, 5, true);
    //System.out.print("Xac xuat chinh xac:" + param);
    //SVMTrainer.doInOrderCrossValidation(trainingInstances, param, 10, true);
}

private static void writeOutputs(String outputFileName, double[] predictions) throws IOException 
	{
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName));
        for (double p : predictions) {
            writer.write(String.format("%.0f\n", p));
        }
        writer.close();
    }
*/

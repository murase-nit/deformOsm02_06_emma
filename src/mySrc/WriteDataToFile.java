package mySrc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


/**
 * ふぁいるへの書き込み
 * @author murase
 *
 */
public class WriteDataToFile {
	private static final String FILE_PATH = "../sample.csv";
	
	
	public WriteDataToFile(double[] aData1, double[] aData2){
		writeFile3(aData1, aData2);
	}
	
	private void writeFile3(double[] aData1, double[]aData2){
		
//		QuickSort qSort = new QuickSort(aData1, aData2);
//		aData1 = qSort.getArrDoubleValue();
//		aData2 = qSort.getArrDouble2Value();
		
		try{
		      File file = new File(FILE_PATH);

		      if (checkBeforeWritefile(file)){
		        //PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
		    	  PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file,true)));

//		        pw.println("今日の最高気温は");
//		        pw.println(10);
//		        pw.println("度です");
		        for (int i=0; i<aData1.length; i++) {
		        	pw.print(aData1[i]+",");
		        }
		        pw.print(",,");
		        for (int i=0; i<aData2.length; i++) {
		        	pw.print(aData2[i]+",");
		        }
		        pw.println("");

		        pw.close();
		      }else{
		        System.out.println("ファイルに書き込めません");
		      }
		    }catch(IOException e){
		      System.out.println(e);
		    }
	}
	
	private static boolean checkBeforeWritefile(File file){
		if (file.exists()){
			if (file.isFile() && file.canWrite()){
				return true;
			}
		}
		return false;
	}


}

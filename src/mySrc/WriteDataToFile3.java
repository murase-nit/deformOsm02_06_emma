package mySrc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;


/**
 * ファイルへの書き込み(動的な型付け)
 * @author murase
 *
 */
public class WriteDataToFile3<T extends Object,F extends Object> {
	private String FILE_PATH = "../sample.csv";
	
	public WriteDataToFile3(ArrayList<T> aData1, ArrayList<F> aData2, String filePath){
		FILE_PATH = filePath.equals("") ? FILE_PATH : filePath;
		writeFile3(aData1, aData2);
	}
	public WriteDataToFile3(ArrayList<T> aData1, String filePath){
		FILE_PATH = filePath.equals("") ? FILE_PATH : filePath;
		writeFile3(aData1);
	}
	
	public WriteDataToFile3(T aData1, F aData2, String filePath){
		FILE_PATH = filePath.equals("") ? FILE_PATH : filePath;
		writeFile3(aData1, aData2);
	}
	
	private void writeFile3(ArrayList<T> aData1, ArrayList<F> aData2){
		
		try{
			File file = new File(FILE_PATH);

			if (checkBeforeWritefile(file)){
				//PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
				PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file,true)));
				
				for(int i=0; i<aData1.size(); i++){
						pw.println(""+aData1.get(i)+","+aData2.get(i));
		    	}
		        pw.close();
		      }else{
		        System.out.println("ファイルに書き込めません");
		      }
		    }catch(IOException e){
		      System.out.println(e);
		    }
	}
	private void writeFile3(ArrayList<T> aData1){
		
		try{
			File file = new File(FILE_PATH);

			if (checkBeforeWritefile(file)){
				//PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
				PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file,true)));
				
				for(int i=0; i<aData1.size(); i++){
						pw.println(""+aData1.get(i));
		    	}
		        pw.close();
		      }else{
		        System.out.println("ファイルに書き込めません");
		      }
		    }catch(IOException e){
		      System.out.println(e);
		    }
	}
	private void writeFile3(T aData1, F aData2){
		
		try{
			File file = new File(FILE_PATH);

			if (checkBeforeWritefile(file)){
				PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file,true)));
				
				pw.println(""+aData1+","+aData2);
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

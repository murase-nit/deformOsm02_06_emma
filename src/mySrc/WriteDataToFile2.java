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
public class WriteDataToFile2<T extends Number> {
	private static final String FILE_PATH = "../sample.csv";
	
	public WriteDataToFile2(ArrayList<ArrayList<T>> aData){
		writeFile3(aData);
	}
	
	private void writeFile3(ArrayList<ArrayList<T>> aData){
		
		try{
			File file = new File(FILE_PATH);

			if (checkBeforeWritefile(file)){
				//PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
				PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file,true)));
				
				for(int i=0; i<aData.size(); i++){
					for (int j=0; j<aData.get(i).size(); j++) {
						pw.print(aData.get(i).get(j)+",");
					}
					pw.println("");
		    	}
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

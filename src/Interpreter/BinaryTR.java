package Interpreter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class BinaryTR {
  public BinaryTR(){
	  String input = "readme.txt";
		//1. read the file into steam	File t = new File("/Users/benzhangtang/Desktop/cs4321/project3/samples/input/db/data/Boats");
		try {
			FileInputStream fis = new FileInputStream(input);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
  }
}

package iconic.mytrade.gutenbergPrinter;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class CheckFw {
	private static String list = "/bs2coop/epsonFw.lst";
	
	protected static boolean inList(String fw) {
		boolean ret = false;
		log("list : "+list);
		log("fw : "+fw);
		
		File FILE = new File(list);
		if (!FILE.exists()) {
			log(list+" does not exist");
			return true;
		}
		FileInputStream fstream;
		try {
			fstream = new FileInputStream(FILE);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line;
			while ((line = br.readLine()) != null)
			{
				if (line.indexOf(fw) > -1) {
					ret = true;
					log("line : "+line);
					break;
				}
			}	
			in.close();
		} catch (Exception e) {
			log(e.getMessage());
			return true;
		}
		
		log("ret : "+ret);
		return ret;
	}
	
	private static void log(String s) {
		System.out.println("CheckFw - "+s);
	}
}

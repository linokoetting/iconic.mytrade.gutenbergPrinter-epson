package iconic.mytrade.gutenbergPrinter.barcodes;

import java.util.ArrayList;

public class Barcodes {
	private static ArrayList <String> barcodes = null;
	
	public static void rememberBarcode(String data)
	{
		if (barcodes == null) {
			barcodes = new ArrayList <String> ();
		}
		barcodes.add(data);
		log("rememberBarcode : "+data);
	}
	
	public static void resetBarcode()
	{
		barcodes = new ArrayList <String> ();
		log("resetBarcode");
	}
	
	public static ArrayList <String> getBarcodes()
	{
		return barcodes;
	}
	
	private static void log(String s)
	{
		System.out.println("Barcodes - "+s);
	}
}

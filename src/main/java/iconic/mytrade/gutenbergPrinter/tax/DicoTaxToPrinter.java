package iconic.mytrade.gutenbergPrinter.tax;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

/* 
 * il costruttore prepara la tabella con le varie IVA da mandare alla printer
 * 
 * quando è possibile REPORT - Z  a ZERO bisogna mandare le IVA alla printer
 * solo se siamo nel casi nuovi RT o SRT
 * 
 */
public class DicoTaxToPrinter {

	private static HashMap taxConverted= null;
	private static int INDEXTOCALL = 0;
	private static final String NAME = ".dicotaxconverted";	
	
	/*
	 * questo metodo deve essere chiamato subito dopo la DicoTaxLoad
	 * anzi facciamo che chiamarlo da li
	 */
	public static void DicoTaxToPrinterInit(){
		getTaxConverted();
		System.out.println("DicoTaxToPrinterInit - DicoTaxSize="+DicoTaxLoad.DicoTaxSize());
		if ( DicoTaxLoad.DicoTaxSize() > 0 ){
			for ( int idx = 0 ; idx < DicoTaxLoad.DicoTaxSize(); idx++){
				DicoTaxObject oo = (DicoTaxObject)DicoTaxLoad.getTax().get(idx);
				if ( taxConverted != null ){
					if ( taxConverted.containsKey(""+oo.getTaxnumber())){
						oo.setPrinterTaxnumber((String)taxConverted.get(""+oo.getTaxnumber()));
						System.out.println ( "RT2 - tax ADD DTO:"+oo.getTaxnumber()+" - "+oo.getPrinterTaxnumber()+" - "+oo.getDescription()+" - "+oo.getShortdescription()+" - "+oo.getType()+" - "+oo.getAtecoId());
						continue;
					}
				}
				oo.setPrinterTaxnumber ( ""+oo.getTaxnumber() );
				System.out.println ( "RT2 - tax ADD DTO:"+oo.getTaxnumber()+" - "+oo.getPrinterTaxnumber()+" - "+oo.getDescription()+" - "+oo.getShortdescription()+" - "+oo.getType()+" - "+oo.getAtecoId());
			}
		}
		INDEXTOCALL = 0;
	}
	/*
	 * questo metodo torna il DicoTaxObjectCorrente da mandare alla printer
	 * se torna NULL non ci sono più oggetti
	 */
	public static DicoTaxObject next(){
		if ( INDEXTOCALL >= DicoTaxLoad.DicoTaxSize()){
			return ( null );
		}
		return ( DicoTaxLoad.get(INDEXTOCALL) );
	}
	/*
	 * questo metodo va chiamato dopo aver mandato il DicoTaxObject alla printer
	 * e setta il codice taxPrinter mandato alla printer nell'oggetto.
	 */
	public static void setTaxPrinterCode ( int x ){
		if ( taxConverted == null ){
			taxConverted = new HashMap();
		}
		DicoTaxObject oo = DicoTaxLoad.get(INDEXTOCALL);
		oo.setPrinterTaxnumber(""+x);
		taxConverted.put(""+oo.getTaxnumber(), ""+oo.getPrinterTaxnumber());
		INDEXTOCALL++;
	}
	/*
	 * questo metodo deve essere chiamato alla fine del processo. 
	 * dopo avere mandato tutte le IVA alla printer.
	 * Questo metodo genera il file .dicotaxprinter.iva che contiene
	 * l'ultima conversione (ivaPOLIPOS <> ivaPRINTER ) utilizzata
	 */
	public static void setTaxConverted()
	{
		try
		{
			delTaxConverted();
			File FILE = new File(getName());
			FileOutputStream fstream = new FileOutputStream(FILE);
			DataOutputStream out = new DataOutputStream(fstream);	
		
			for ( int i = 0 ; i < DicoTaxLoad.DicoTaxSize() ; i++ )
			{
				DicoTaxObject tax = DicoTaxLoad.get(i);
				out.writeBytes(tax.getTaxnumber()+":"+tax.getPrinterTaxnumber()+"\n");
			}
			out.close();
			fstream.close();
		}
		catch ( IOException e )
		{
			System.out.println ("WRITE .dicotaxconverted.iva error is:"+e.toString()+">");
		}
	}
	public static int getFromPoliposToPrinter ( int tx ){
		if (!DicoTaxLoad.isIvaAllaPrinter() || taxConverted == null)
			return (0);
		
		if ( taxConverted.containsKey(""+tx ) ){
			//return ( Integer.parseInt((String)taxConverted.get( ""+tx )));
			return (DicoTaxLoad.getDTO(tx).getPrinterDeptnumber());
		}
		return ( tx );
	}
	public static int getReallyFromPoliposToPrinter ( int tx ){
		//if (!DicoTaxLoad.isIvaAllaPrinter() || taxConverted == null)
		// return (0);

		try {
		if ( taxConverted.containsKey(""+tx ) ){
		return ( Integer.parseInt((String)taxConverted.get( ""+tx )));
		}
		}
		        catch (java.lang.NumberFormatException e) {
		tx = 1000 + (Integer.parseInt(((String)taxConverted.get( ""+tx )).substring(1)));
		}
		return ( tx );
	}
	public static void delTaxConverted()
	{
		File FILE = new File(getName());
		if (FILE.exists()) FILE.delete();
	}
	private static void getTaxConverted()	{
		String strLine = null;
		try{
			File FILE = new File(getName());
			FileInputStream fstream = new FileInputStream(FILE);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
	
			taxConverted = new HashMap();
			while ((strLine = br.readLine()) != null){
				String ss [] = strLine.split(":");
				taxConverted.put(ss[0], ss[1]);			
			}
			in.close();
			fstream.close();
		}catch ( IOException e ){
			taxConverted = null;
			getTaxFromDB();
			System.out.println ("READ .dicotaxconverted.iva error is:"+e.toString()+">");
		}
	}	
	private static void getTaxFromDB()
	{
		taxConverted = new HashMap();
		for ( int i = 0 ; i < DicoTaxLoad.DicoTaxSize() ; i++ ){
			DicoTaxObject tax = DicoTaxLoad.get(i);
			taxConverted.put(""+tax.getTaxnumber(), ""+(i+1));	
			System.out.println ("taxConverted ADD:"+tax.getTaxnumber() + " - "+(i+1));
		}
	}
	public static String getName ( )
	{
		return ( NAME+".iva" );
	}
}

package iconic.mytrade.gutenbergPrinter.tax;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import iconic.mytrade.gutenberg.jpos.printer.service.tax.TaxInfo;
import iconic.mytrade.gutenbergPrinter.SharedPrinterFields;

public class DicoTaxLoad 
{
	private static ArrayList tax = null;
	private static boolean ivaAllaPrinter = false;
    
    private static int BENI = TaxData.getBeni();
    
    private static boolean RT2enabled = false;
	
	public static boolean isRT2enabled() {
		return RT2enabled;
	}
	
	private static boolean isRT2disabled() {
		return (!isRT2enabled());
	}
	
	public static void setRT2enabled(boolean rT2enabled) {
		System.out.println("RT2 - setRT2enabled : "+rT2enabled);
		RT2enabled = rT2enabled;
	}
	
	private static void init() {
	}
	
	static int DicoTaxSize()
	{
		return ( (tax != null ) ? tax.size() : 0 );
	}
	
	static DicoTaxObject get ( int i )
	{
		if ( tax != null )
		{
			if ( i < tax.size() )
				return ( (DicoTaxObject)tax.get(i));
		}
		return ( null );
	}
	
	static ArrayList getTax() {
		return tax;
	}
	
	public static boolean isIvaAllaPrinter() {
		return ivaAllaPrinter;
	}
	
	public static void setIvaAllaPrinter() {
		boolean RT = iconic.mytrade.gutenberg.jpos.printer.service.properties.SRTPrinterExtension.isPRT();
		DicoTaxLoad.ivaAllaPrinter = RT;
	}
	
	private static TaxInfo[] TaxInfoLookup (HashMap<String, TaxInfo> taxInfoMap)
	{
	    TaxInfo[] ret = new TaxInfo[taxInfoMap.size()];
		
		int i = 0;
	    for (String key : taxInfoMap.keySet()) {
	    	ret[i++] = taxInfoMap.get(key);
	    }
	    
	    return ret;
	}
	
	public static void DicoTaxLoadInit( ){
		DicoTaxLoadInitInternal();
		setIvaAllaPrinter();
//		if ( isIvaAllaPrinter() ){
			DicoTaxToPrinter.DicoTaxToPrinterInit();
//		}
	}
	
	private static void DicoTaxLoadInitInternal( )
	{
		init();
		try {
			if ( tax == null )		// load tax table
			{	
				tax = new ArrayList ();
				TaxInfo[] taxInfo = TaxInfoLookup(SharedPrinterFields.taxInfoMap);
				if ( taxInfo != null )
				{		
					File FILE = new File(DicoTaxToPrinter.getName());
					FileOutputStream fstream = new FileOutputStream(FILE);
					DataOutputStream out = new DataOutputStream(fstream);	
					for ( int i = 0 ; i < taxInfo.length ; i ++ )
					{
						String txt =  taxInfo[i].getTaxCode();
						if ( txt == null || txt.length() == 0 || txt.equals(" ")){
							continue;
						}
						DicoTaxObject dto = new DicoTaxObject();
						dto.setTaxnumber ( taxInfo[i].getTaxNumber() );
						dto.setTaxrate(taxInfo[i].getRate());
						String AA="";
						if ((int)(dto.getTaxrate()) == 0){
							AA=" 000";
						}
						else{
							AA = "" +(int)(dto.getTaxrate() * 100);
							while (AA.length() < 4){
								AA=" "+AA;
							}
						}
						//dto.setDescription( "IVA "+ AA.substring(0, 2)+","+AA.substring(2, 4)+"%");//Full desc
						//dto.setShortdescription( AA.substring(0, 2)+","+AA.substring(2, 4));//Short					
						dto.setDescription(taxInfo[i].getRt_FullDesc());
						dto.setShortdescription(taxInfo[i].getRt_ShortDesc());
						dto.setType((taxInfo[i].getRt2_Type() > 0) ? taxInfo[i].getRt2_Type() : BENI);		// default = Beni
//						if (isRT2disabled())	// cutover non fatto
//							dto.setType(BENI);	// solo Beni
						dto.setAtecoId((taxInfo[i].getRt2_AtecoNr() > 0) ? taxInfo[i].getRt2_AtecoNr() : 1);	// default = 1
						dto.setPrinterDeptnumber(taxInfo[i].getRt_TaxCode());
						out.writeBytes(taxInfo[i].getTaxNumber()+":"+taxInfo[i].getRt_TaxCode()+"\n");
						tax.add(dto);
						System.out.println ( "RT2 - tax ADD DTO:"+dto.getTaxnumber()+" - "+dto.getPrinterTaxnumber()+" - "+dto.getDescription()+" - "+dto.getShortdescription()+" - "+dto.getType()+" - "+dto.getAtecoId());
					}
				}
			}
			else
			{
				for ( int i = 0 ; i < tax.size(); i++ )
					((DicoTaxObject)tax.get(i)).clean();
			}
		}catch (Exception e) {
			System.out.println("DicoTaxLoad - DicoTaxLoadInitInternal - errore:"+e);
		}
	}
	
	public static DicoTaxObject getDTO ( int taxNr ){
		int i = DicoTaxFind ( taxNr );
		if ( ( i >= 0 )){
			return ( (DicoTaxObject)tax.get(i));
		}else{
			return ( null );
		}
	}
	
	private static int DicoTaxFind ( int taxnumber )
	{
		if ( tax != null )
		{
			for ( int i = 0 ; i < tax.size(); i++ ) {
				if ( ((DicoTaxObject)tax.get(i)).getTaxnumber() == taxnumber ) {
					return ( i );
				}
			}
		}
		System.out.println ( "************ WARNING TAX COD <"+taxnumber+"> MISSING *******************");
		return ( -1 );
	}
	
}

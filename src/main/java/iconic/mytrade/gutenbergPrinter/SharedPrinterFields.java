package iconic.mytrade.gutenbergPrinter;

import java.util.ArrayList;
import java.util.HashMap;

import iconic.mytrade.gutenberg.jpos.printer.service.Extra;
import iconic.mytrade.gutenberg.jpos.printer.service.RTLottery;
import iconic.mytrade.gutenberg.jpos.printer.service.mop.MediaInfo;
import iconic.mytrade.gutenberg.jpos.printer.service.properties.SRTPrinterExtension;
import iconic.mytrade.gutenberg.jpos.printer.service.tax.AtecoInfo;
import iconic.mytrade.gutenberg.jpos.printer.service.tax.TaxInfo;
import iconic.mytrade.gutenbergPrinter.ej.FiscalEJFile;
import iconic.mytrade.gutenbergPrinter.mop.LoadMops;
import iconic.mytrade.gutenbergPrinter.tax.DicoTaxLoad;

public class SharedPrinterFields {
	
	public static ArrayList a = null;
	
	public static boolean PosponedInError = false;
	
	public static FiscalEJFile fiscalEJ = null;
	private static boolean fiscalEJenabled = true;
	
	public static boolean isfiscalEJenabled() {
		return fiscalEJenabled;
	}
	
	public void setfiscalEJenabled(boolean fiscalEJenabled) {
		this.fiscalEJenabled = fiscalEJenabled;
	}
	
	public static int INDEX_A = 0;
	
	private static boolean flagsVoidTicket = false;
	
	protected static boolean isFlagsVoidTicket() 
	{
		return flagsVoidTicket;
	}
	
	protected static void setFlagsVoidTicket(boolean flagsvoidticket) 
	{
		flagsVoidTicket = flagsvoidticket;
	}
	
    private static boolean fwRT2enabled = false;	// abilita/disabilita i comandi in modalità RT2
    
	public static boolean isfwRT2enabled() {
		return fwRT2enabled;
	}
	
	public static boolean isfwRT2disabled() {
		return (!isfwRT2enabled());
	}
	
	static void setfwRT2enabled(boolean fwrT2enabled) {
		System.out.println("RT2 - setfwRT2enabled : "+fwrT2enabled);
		fwRT2enabled = fwrT2enabled;
		DicoTaxLoad.setRT2enabled(fwRT2enabled);
	}
	
	public static boolean isRT2On() {
		if (SRTPrinterExtension.isPRT()) {
			return (isfwRT2enabled() && DicoTaxLoad.isRT2enabled());
		}
		else if (SRTPrinterExtension.isSRT()){
			return (Extra.isServerRt20() && DicoTaxLoad.isRT2enabled());
		}
		return (false);
	}
	
    public static RTLottery Lotteria = null;
    
	public static HashMap<String, TaxInfo> taxInfoMap = new HashMap<String, TaxInfo>();
	public static ArrayList<MediaInfo> mediaInfoMap = null;
	public static ArrayList<String> gvTypesMap = null;
	public static HashMap<String, AtecoInfo> atecoInfoMap = new HashMap<String, AtecoInfo>();
	
	public static boolean inException;
	
	public static final String	LOGO_FOLDER	=	"/bs2coop/pos/image/header/";
	static final String	LOGO_FILE			=	"MyLogo.bmp";
	static final String	TRAILER_LOGO_FILE	=	"MyTrailerLogo.bmp";
	static final int	LOGO_NUMBER			=	1;
	static final int	TRAILER_LOGO_NUMBER	=	2;
	
	public static final String ALINER = "                                         ";
	
	public static String KEY_LOCK	= "=C0";
    public static String KEY_REG	= "=C1";
    public static String KEY_X		= "=C2";
    public static String KEY_Z		= "=C3";
    public static String KEY_PRG	= "=C4";
    public static String KEY_SRV	= "=C5";
    
	public static int INDICE_CONTANTI = 0;
	public static int INDICE_SCONTOAPAGARE = 0;
	public static String DESCRIZIONE_CONTANTI = "";
	public static String DESCRIZIONE_SCONTOAPAGARE = "";
	
	public static int VAT_N4_Index = 0;
	public static String VAT_N4_Dept = "20";
  
    static String ChangeCurrency	= "";
	
    public static String RTPrinterId = null;
	public static String Printer_IPAddress = "";
	
	public static String rtlog_folder = "/Retail3/tmp/";
	public static String rtlog_name = "checkRTStatus";
	public static String ltlog_name = "checkLTStatus";
	public static String iltlog_name = "checkILTStatus";
	public static String rtlog_ext = ".log";
    
	public static void getChangeDescription(){
		LoadMops.loadMops();
		
		if ((ChangeCurrency == null) || (ChangeCurrency == ""))
			ChangeCurrency = LoadMops.getChangeDescription(LoadMops.Mops);
	}

    protected static Double RoundingRT = 0.0;
	
    public static HashMap<String, Double> lineePagamento = null;
    
    private static boolean CFcliente = false;
    
    static boolean isCFcliente() {
 		return CFcliente;
 	}

 	public static void setCFcliente(boolean cFcliente) {
 		CFcliente = cFcliente;
 	}
 	
	public static String lastticket = "/bs2coop/pos/LastTicket.out";
	
	public static boolean	inRetryFiscal;
	
	private static boolean inTicket;
	
	public static boolean isInTicket()
	{	
		return ( inTicket );
	}
	
	public static boolean setInTicket()
	{
		inTicket = true;
		return ( isInTicket() );
	}
	
	public static boolean resetInTicket()
	{
		inTicket = false;
		return ( isInTicket() );
	}
	
}

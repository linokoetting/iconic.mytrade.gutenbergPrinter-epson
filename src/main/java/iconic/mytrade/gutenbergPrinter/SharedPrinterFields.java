package iconic.mytrade.gutenbergPrinter;

import java.util.ArrayList;
import java.util.HashMap;

import iconic.mytrade.gutenberg.jpos.printer.service.R3define;
import iconic.mytrade.gutenberg.jpos.printer.service.RTLottery;
import iconic.mytrade.gutenberg.jpos.printer.service.mop.MediaInfo;
import iconic.mytrade.gutenberg.jpos.printer.service.tax.AtecoInfo;
import iconic.mytrade.gutenberg.jpos.printer.service.tax.TaxInfo;
import iconic.mytrade.gutenbergPrinter.ej.EjCommands;
import iconic.mytrade.gutenbergPrinter.ej.FiscalEJFile;
import iconic.mytrade.gutenbergPrinter.mop.LoadMops;

public class SharedPrinterFields {
	
	public static ArrayList a = null;
	
	public static boolean PosponedInError = false;
	
	public static FiscalEJFile fiscalEJ = null;
	private static boolean fiscalEJenabled = true;
	
	public static boolean isfiscalEJenabled() {
		return fiscalEJenabled;
	}
	
	public static void setfiscalEJenabled(boolean fiscalejenabled) {
		fiscalEJenabled = fiscalejenabled;
	}
	
	public static int INDEX_A = 0;
	
    public static RTLottery Lotteria = null;
    
	public static HashMap<String, TaxInfo> taxInfoMap = new HashMap<String, TaxInfo>();
	public static ArrayList<MediaInfo> mediaInfoMap = null;
	public static ArrayList<String> gvTypesMap = null;
	public static HashMap<String, AtecoInfo> atecoInfoMap = new HashMap<String, AtecoInfo>();
	
	public static boolean inException;
	
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
	
	public static String WorkingFolder = "";
	public static String BaseFolder = "";
    
	public static String FwList = "";
    
	public static String lastticket = "LastTicket.out";
	
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
	
	private static boolean	prtDone;	// printrectotalDone
	
	static boolean isprtDone()
	{	
		return ( prtDone );
	}
	
	static boolean setprtDone()
	{
		prtDone = true;
		return ( isprtDone() );
	}
	
	public static boolean resetprtDone()
	{
		prtDone = false;
		return ( isprtDone() );
	}
	
	private static long theBill;
	
	public static long cleanTheBill( )
	{
		return ( setTheBill( 0 ) );
	}
	
	private static long setTheBill( long V )
	{
		theBill =  V;
		return ( getTheBill() );
	}
	
	private static long getTheBill( )
	{
		return ( theBill );
	}
	
	public static boolean checkTheBill ( long T, long P )
	{
		return ( (T <= setTheBill( getTheBill() + P ) ) );
	}
   
	private static int simulateState;
	
	static int setMonitorState()
	{	
		return ( setSimulateState ( jpos.FiscalPrinterConst.FPTR_PS_MONITOR ) );
	}
	
	public static int setFiscalState()
	{	
		return ( setSimulateState ( jpos.FiscalPrinterConst.FPTR_PS_FISCAL_RECEIPT ) );
	}
	
	public static int setNonFiscalState()
	{	
		return ( setSimulateState ( jpos.FiscalPrinterConst.FPTR_PS_NONFISCAL ) );
	}
	
	public static int setEndingFiscalState()
	{	
		return ( setSimulateState ( jpos.FiscalPrinterConst.FPTR_PS_FISCAL_RECEIPT_ENDING ) );
	}
	
	static int getSimulateState()
	{	
		return ( simulateState );
	}
	
	private static int setSimulateState( int State )
	{
		simulateState = State;
		return ( getSimulateState() );
	}
	
	private static String CFPIvaTag = "CoDICefiSCAlepaRTItaiVA:";
	private static boolean CFPIvaFlag = false;
	
	static String getCFPIvaTag() {
		return CFPIvaTag;
	}
	
    static boolean isCFPIvaFlag() {
		return CFPIvaFlag;
	}
    
	public static void setCFPIvaFlag(boolean cFPIvaFlag) {
		CFPIvaFlag = cFPIvaFlag;
	}
    
    private static boolean CFcliente = false;
    
    static boolean isCFcliente() {
 		return CFcliente;
 	}

 	public static void setCFcliente(boolean cFcliente) {
 		CFcliente = cFcliente;
 	}
 	
	private static PrinterCommands cmd = null;
	
	public static PrinterCommands getPrinterCommands()
	{
		if ( cmd == null)
			cmd = new PrinterCommands();
		return cmd;
	}
	
	private static EjCommands ejcmd = null;
	
	public static EjCommands getEjCommands()
	{
		if ( ejcmd == null)
			ejcmd = new EjCommands();
		return ejcmd;
	}
	
	private static int myReply = R3define.PRINTER_OK;

	public static int getMyReply() {
		return myReply;
	}

	public static void setMyReply(int myReply) {
		SharedPrinterFields.myReply = myReply;
	}

}

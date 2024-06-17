package iconic.mytrade.gutenbergPrinter;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import iconic.mytrade.gutenberg.jpos.printer.service.CarteFidelity;
import iconic.mytrade.gutenberg.jpos.printer.service.Company;
import iconic.mytrade.gutenberg.jpos.printer.service.Extra;
import iconic.mytrade.gutenberg.jpos.printer.service.LastTicket;
import iconic.mytrade.gutenberg.jpos.printer.service.MessageBox;
import iconic.mytrade.gutenberg.jpos.printer.service.MethodE;
import iconic.mytrade.gutenberg.jpos.printer.service.PleaseDisplay;
import iconic.mytrade.gutenberg.jpos.printer.service.PosApp;
import iconic.mytrade.gutenberg.jpos.printer.service.R3define;
import iconic.mytrade.gutenberg.jpos.printer.service.RTLottery;
import iconic.mytrade.gutenberg.jpos.printer.service.RTRounding;
import iconic.mytrade.gutenberg.jpos.printer.service.RTTxnType;
import iconic.mytrade.gutenberg.jpos.printer.service.SetVoidTrx;
import iconic.mytrade.gutenberg.jpos.printer.service.SmartTicket;
import iconic.mytrade.gutenberg.jpos.printer.service.TakeYourTime;
import iconic.mytrade.gutenberg.jpos.printer.service.TicketErrorSupport;
import iconic.mytrade.gutenberg.jpos.printer.service.TransactionSale;
import iconic.mytrade.gutenberg.jpos.printer.service.TxnHeader;
import iconic.mytrade.gutenberg.jpos.printer.service.hardTotals.HardTotals;
import iconic.mytrade.gutenberg.jpos.printer.service.properties.Lotteria;
import iconic.mytrade.gutenberg.jpos.printer.service.properties.LotteriaInstant;
import iconic.mytrade.gutenberg.jpos.printer.service.properties.PaperSavingProperties;
import iconic.mytrade.gutenberg.jpos.printer.service.properties.PrinterType;
import iconic.mytrade.gutenberg.jpos.printer.service.properties.SRTPrinterExtension;
import iconic.mytrade.gutenberg.jpos.printer.service.tax.AtecoInfo;
import iconic.mytrade.gutenberg.jpos.printer.service.tax.RoungickTax;
import iconic.mytrade.gutenberg.jpos.printer.service.tax.TaxInfo;
import iconic.mytrade.gutenberg.jpos.printer.service.tax.VatInOutHandling;
import iconic.mytrade.gutenberg.jpos.printer.service.utils.BasicDicoData;
import iconic.mytrade.gutenberg.jpos.printer.service.utils.Files;
import iconic.mytrade.gutenberg.jpos.printer.service.utils.LocalTimestamp;
import iconic.mytrade.gutenberg.jpos.printer.service.utils.Rounding;
import iconic.mytrade.gutenberg.jpos.printer.service.utils.RunShellScriptPoli20;
import iconic.mytrade.gutenberg.jpos.printer.service.utils.SRTCheckInput;
import iconic.mytrade.gutenberg.jpos.printer.service.utils.Sprint;
import iconic.mytrade.gutenberg.jpos.printer.service.utils.String13Fix;
import iconic.mytrade.gutenberg.srt.DummyServerRT;
import iconic.mytrade.gutenberg.srt.RTConsts;
import iconic.mytrade.gutenberg.srt.Xml4SRT;
import iconic.mytrade.gutenbergPrinter.ej.EjCommands;
import iconic.mytrade.gutenbergPrinter.ej.ForFiscalEJFile;
import iconic.mytrade.gutenbergPrinter.lottery.LotteryCommands;
import iconic.mytrade.gutenbergPrinter.mop.LoadMops;
import iconic.mytrade.gutenbergPrinter.mop.Mop;
import iconic.mytrade.gutenbergPrinter.mop.RchMop;
import iconic.mytrade.gutenbergPrinter.refund.RefundCommands;
import iconic.mytrade.gutenbergPrinter.report.Report;
import iconic.mytrade.gutenbergPrinter.rtvoid.VoidCommands;
import iconic.mytrade.gutenbergPrinter.smtk.SMTKCommands;
import iconic.mytrade.gutenbergPrinter.tax.DicoTaxLoad;
import iconic.mytrade.gutenbergPrinter.tax.DicoTaxToPrinter;
import iconic.mytrade.gutenbergPrinter.tax.TaxData;
import jpos.FiscalPrinterConst;
import jpos.JposException;
import rtsTrxBuilder.support.ivaSEMPLICE;
import rtsTrxBuilder.support.scontiSEMPLICI;

public class PrinterCommands extends iconic.mytrade.gutenbergInterface.PrinterCommands {
	
	private static int MyPrinterType = PrinterType.EFP90_F;
	
	private static double fw = 0;
	
	static int staticMsgLen = 0;
	
	protected static final String	R3_DELETELASTTICKET_R3 = "R3_NEWCMD01_R3";
	protected static final String	R3_PRINTNORMALLONGER_R3 = "R3_NEWCMD02_R3";
	private static	 final String	R3_DOUBLE_R3 = "R3double3R";
	
	static String OPERAZIONEANNULLATA = "OPERAZIONE ANNULLATA";
	static String RESONONCORRETTO = "PREZZO NON CORRETTO ";

    public static String barcodePrefix = "VAR";
	private String CF = "C.F. Cliente ";
	private String PI = "P.IVA. Cliente ";
	private int CFLEN = 16;
	private int PILEN = 11;
	private String CFPIvaTag = "CoDICefiSCAlepaRTItaiVA:";
	private boolean CFPIvaFlag = false;
	
	private String getCFPIvaTag() {
		return CFPIvaTag;
	}
	
    protected boolean isCFPIvaFlag() {
		return CFPIvaFlag;
	}
    
	protected void setCFPIvaFlag(boolean cFPIvaFlag) {
		CFPIvaFlag = cFPIvaFlag;
	}
    
	private boolean flagVoidRefund = false;
	
	protected boolean isFlagVoidRefund(long amt, long adj) 
	{
		return ((flagVoidRefund) && (amt == 0) && (adj > 0));
	}
	
	protected void setFlagVoidRefund(boolean flagVoidRefund) 
	{
		this.flagVoidRefund = flagVoidRefund;
	}
	
	private boolean	prtDone;	// printrectotalDone
	
	public boolean isprtDone()
	{	
		return ( prtDone );
	}
	
	public boolean setprtDone()
	{
		prtDone = true;
		return ( isprtDone() );
	}
	
	public boolean resetprtDone()
	{
		prtDone = false;
		return ( isprtDone() );
	}
	
	public static String lastticketsaved = null;
    private static String lastwrittenstring = "";
	
	private boolean	inReset;
	
	private long theBill;
	
	public long cleanTheBill( )
	{
		return ( this.setTheBill( 0 ) );
	}
	
	public long setTheBill( long V )
	{
		theBill =  V;
		return ( getTheBill() );
	}
	
	public long getTheBill( )
	{
		return ( theBill );
	}
	
	public boolean checkTheBill ( long T, long P )
	{
		return ( (T <= setTheBill( getTheBill() + P ) ) );
	}
   
//    static ICurrency discountedtaxamount = CurrencyInfo.newCurrency(null,0.0);	// prima era così
	static BigDecimal discountedtaxamount = new BigDecimal(0.);
	
    ArrayList SSCO = null;
	private static double 	dailyTotal = 0.0;
	private	static double 	currentTicket = 0.0;
    private static boolean 	provaErrori = false;		// attenzione versione di test per prova errori
	private static int	   	countErrori = 1;
	protected static long	totaleNonRiscosso = 0;
	
	protected static boolean progress_cash = true;
	protected static boolean progress_eft = true;
	protected static long progress_amount = 0;
	private static int enabledLowerRoundedPay = -1;
	
	protected boolean AtLeastOnePrintedItem = false;
	
	protected static double smtkamount = 0.;

	private ArrayList xml = null;
	
	private static int simulateState;
	
	public static int setMonitorState()
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
	
	public int setEndingFiscalState()
	{	
		return ( setSimulateState ( jpos.FiscalPrinterConst.FPTR_PS_FISCAL_RECEIPT_ENDING ) );
	}
	
	public static int getSimulateState()
	{	
		return ( simulateState );
	}
	
	public static int setSimulateState( int State )
	{
		simulateState = State;
		return ( getSimulateState() );
	}
	
//	private static GuiPosPrinterDriver nonFiscal = null;
	protected static GuiFiscalPrinterDriver fiscalPrinterDriver = null;
	
	private static File inout = null;		
	private static FileOutputStream fos = null;
	private static PrintStream ps = null;
	
	boolean isFiscalAndSRTModel()
	{
		return (SRTPrinterExtension.isSRT() && SRTPrinterExtension.isNotLikeNonFiscalMode());
	}

	private boolean isLNFMAndSRTModel()
	{
		return (SRTPrinterExtension.isSRT() && SRTPrinterExtension.isLikeNonFiscalMode());
	}

	public int getOpenTimeout() {
		int timeout = fiscalPrinterDriver.getOpenTimeout();
		System.out.println("getOpenTimeout: "+timeout+" msec.");
		return timeout;
	}
	
    /* printer commands - Start
     * 
	public void open(int model,String device);
	public void beginFiscalReceipt(boolean printHeader) throws JposException;
	public void beginNonFiscal() throws JposException;
	public void endFiscalReceipt(boolean printHeader) throws JposException;
	public void endNonFiscal() throws JposException;
	public void printNormal(int station, String data) throws JposException;
	public void printRecItem(String description, long price, int quantity, int vatInfo, long unitPrice, String unitName) throws JposException;
	public void printRecItemAdjustment(int adjustmentType, String description, long amount, int vatInfo) throws JposException;
	public void printRecMessage(String message) throws JposException;
	public void printRecRefund(String description, long amount, int vatInfo) throws JposException;
	public void printRecSubtotal(long amount) throws JposException;
	public void printRecSubtotalAdjustment(int adjustmentType, String description, long amount) throws JposException;
	public void printRecTotal(long total, long payment, String description) throws JposException;
	public void printRecVoid(String description) throws JposException;
	public void printRecVoidItem(String description, long amount, int quantity, int adjustmentType, long adjustment, int vatInfo) throws JposException;
	public void printReport(int reportType, String startNum, String endNum) throws JposException;
	public void printXReport() throws JposException;
	public void printZReport() throws JposException;
	public void resetPrinter() throws JposException;
	public void setDate(String date) throws JposException;
	public void setHeaderLine(int lineNumber, String text, boolean doubleWidth) throws JposException;
	public void setTrailerLine(int lineNumber, String text, boolean doubleWidth) throws JposException;
	public void close();
	*
	*/
    
	public void open(int arg0, String arg1) {
		if (arg0 != MyPrinterType) {
			System.out.println("Wrong PrinterType Request : "+arg0+" instead of "+MyPrinterType);
			System.exit(-1);
		}
		
		SharedPrinterFields.resetInTicket();
		resetprtDone();
		RTConsts.setMAXITEMDESCRLENGTH(false, SRTPrinterExtension.isSRT(), SRTPrinterExtension.isNotLikeNonFiscalMode(), false);
		
		try
		{
			SharedPrinterFields.Lotteria = new RTLottery();
		}
		catch ( Exception e )
		{
			System.out.println ( "Errore costruttore : "+e.getMessage() );
		}
			
		DummyServerRT.XMLfilePath = rtsTrxBuilder.storerecallticket.Default.getExchangeRtsName();
		SharedPrinterFields.lastticket = rtsTrxBuilder.storerecallticket.Default.getExchangeName();
		lastticketsaved = rtsTrxBuilder.storerecallticket.Default.getsourcePath()+"LastTicket.sav";
		
		fiscalPrinterDriver = new GuiFiscalPrinterDriver();
		fw = fiscalPrinterDriver.doLoad(arg0, arg1);
	}
	
	public void close() {
		try
		{
			System.out.println ( "Prima di Close" );
			fiscalPrinterDriver.close();
		}
		catch ( jpos.JposException e )
		{
			System.out.println ( "Printer Exception <"+e.toString()+">");
		}
	}
	
	public void beginFiscalReceipt(boolean arg0) throws JposException
	{
		System.out.println("MAPOTO-EXEC BEGINFISCAL");
		
		SharedPrinterFields.setInTicket();
		resetprtDone();
		cleanTheBill();
		setFiscalState();
		setCFPIvaFlag(false);
		SharedPrinterFields.Lotteria.setILotteryCode("");
		
		if (SRTPrinterExtension.isPRT()){
			String[] doc = {""};
    		String s = intestazione1(RTTxnType.getTypeTrx());
    		if (s.length() > 0){
    			ForFiscalEJFile.writeToFile("\n\t\t"+s.trim());
    			doc = s.trim().split(" ");
    		}
    		s = intestazione2(RTTxnType.getTypeTrx());
    		if (s.length() > 0)
    			ForFiscalEJFile.writeToFile("\t\t"+s.trim());
    		s = intestazione3(RTTxnType.getTypeTrx());
    		if (s.length() > 0)
    			ForFiscalEJFile.writeToFile("\t\t"+s.trim());
    		s = intestazione4(RTTxnType.getTypeTrx());
    		if (s.length() > 0)
    			ForFiscalEJFile.writeToFile("\t\t"+s.trim());
    		
	        int[] ai = new int[1];
	        String[] as = new String[1];
	        fiscalPrinterDriver.getData(FiscalPrinterConst.FPTR_GD_FISCAL_REC, ai, as);
            String n = as[0];
            fiscalPrinterDriver.getData(FiscalPrinterConst.FPTR_GD_Z_REPORT, ai, as);
            String z = "";
            try {
            	z = Sprint.f("%04d",Integer.parseInt(as[0])+1);
	        } catch (NumberFormatException e) {
			   System.out.println("beginFiscalReceipt - NumberFormatException : " + e.getMessage());
			   z = Sprint.f("%04d",0);
	        }
            
            s = doc[0]+" N. "+z+"-"+n;
            ForFiscalEJFile.writeToFile("\t\t"+s);
            LastTicket.setDocnum(SharedPrinterFields.ALINER.substring(0, (int)((RTConsts.setMAXLNGHOFLENGTH()-s.length())/2))+s);
            
            intestazione5(false);
            
            DummyServerRT.CurrentFiscalClosure = Integer.parseInt(z);
            DummyServerRT.CurrentReceiptNumber = n;
		}
		
		inReset = false;
		
//		discountedtaxamount = CurrencyInfo.newCurrency(null,0.0);	// prima era così
		discountedtaxamount = new BigDecimal(0.);
		discountedtaxamount = discountedtaxamount.setScale(2, RoundingMode.HALF_DOWN);
		
		if (SRTPrinterExtension.isSRT())
		{
			SSCO = null;
			totaleNonRiscosso = 0;
		}
		
		if (isLNFMAndSRTModel())
		{
			setFlagVoidRefund(false);
			SharedPrinterFields.inRetryFiscal = true;				// per le volte successive alla prima è una retry
			cleanDailyTotal();
			String Total = currentFiscalTotal(jpos.FiscalPrinterConst.FPTR_GD_DAILY_TOTAL);
		    storeDailyTotal(Total);
		    
			cancellaFile();
			
			HardTotals.doBeginFiscal();
			
			beginNonFiscal();
			
			testata();

			return;
		}
		
	    if (SmartTicket.isSmart_Ticket())
	    {
    		if ((SmartTicket.getCustomerType() == SmartTicket._Smart_Ticket_CustomerType) && (SmartTicket.getCustomerId().equalsIgnoreCase(SmartTicket._Smart_Ticket_CustomerId))) {
    			if (CarteFidelity.getCartaFidelity() != null && CarteFidelity.getCartaFidelity().length() > 0) {
    				// se non è stato specificato nessun customerid ma è stata passata la tessera fidelity allora usiamo questa
    				SmartTicket.setCustomerType(SmartTicket.ERECEIPT_DEFAULT_CUSTOMER);
    				SmartTicket.setCustomerId(CarteFidelity.getCartaFidelity());
    			}
    		}
    		
	    	if (SRTPrinterExtension.isPRT()) {
	    		// qui setto i parametri per lo scontrino che sta per andare in stampa, secondo le impostazioni decise dall'interfaccia grafica
	    		// oppure con le impostazioni di default se non sono state specificate tramite l'interfaccia grafica
	    		fiscalPrinterDriver.SMTKsetReceiptType(SmartTicket.Smart_Ticket_ReceiptMode, SmartTicket.Smart_Ticket_Validity);
	    		fiscalPrinterDriver.SMTKsetCustomerID(SmartTicket.Smart_Ticket_CustomerType, SmartTicket.Smart_Ticket_CustomerId);
	    	}
    		
    		SmartTicket.SMTKbarcodes_reset();
	    }
	    
		setFlagVoidRefund(false);
		AtLeastOnePrintedItem = false;
		SharedPrinterFields.inRetryFiscal = true;				// per le volte successive alla prima è una retry
		cleanDailyTotal();
		String Total = currentFiscalTotal(jpos.FiscalPrinterConst.FPTR_GD_DAILY_TOTAL);
	    storeDailyTotal(Total);
		System.out.println ( "MAPOTO-EXEC BEGINFISCAL AFTER STORE DAILY TOTAL" );
		
    	if (PrinterType.isDieboldRTOneModel()) {
			StringBuffer Klean = new StringBuffer("=K");
			fiscalPrinterDriver.executeRTDirectIo(0, 0, Klean);
    	}
		
		System.out.println ( "MAPOTO-EXEC BEGINFISCAL BEFORE NATIVE" );
		fiscalPrinterDriver.beginFiscalReceipt(arg0);
		System.out.println ( "MAPOTO-EXEC BEGINFISCAL AFTER  NATIVE" );
		
		if (isFiscalAndSRTModel())
		{
		    cancellaFile();

		    HardTotals.doBeginFiscal();
			
			testata();
		}
		
		if (SRTPrinterExtension.isPRT())
		{
			totaleNonRiscosso = 0;
			
		    cancellaFile();
			initTicketOnFile();	// rimette l'header nel file LastTicket.out appena cancellato
		    
		    HardTotals.doBeginFiscal();
		    
			String s = LastTicket.getIntestazione1();
			if (s.length() > 0) scriviLastTicket(s);
			s = LastTicket.getIntestazione2();
			if (s.length() > 0) scriviLastTicket(s);
			s = LastTicket.getIntestazione3();
			if (s.length() > 0) scriviLastTicket(s);
			s = LastTicket.getIntestazione4();
			if (s.length() > 0) scriviLastTicket(s);
			s = LastTicket.getIntestazione5();
			if (s.length() > 0) scriviLastTicket(s);
		}
	}
	
	public void printNormal(int i, String s) throws JposException
	{
		EjCommands ej = new EjCommands();
		ej.printNormal(i, s+R3define.CrLf);
		
		printNormal_ejoff(i, s);
	}
	
	public void printNormal_ejoff(int i, String s) throws JposException
	{
		System.out.println("MAPOTO-EXEC PRINT NORMAL "+s);
		
		if (isLNFMAndSRTModel())
		{
			if (s.startsWith(R3_DELETELASTTICKET_R3))
			{
				// il file LastTicket.out viene cancellato e ricreato ad ogni BeginFiscal()
				// oppure se si riceve la stringa "R3_NEWCMD01_R3" in una printNormal()
				cancellaFile();
				//initTicketOnFile();	// rimette l'header nel file LastTicket.out appena cancellato
				return;
			}
			scriviLastTicket(s);
			
			if (PrinterType.isTHFEJModel())
			{
				// aggiungo crlf perchè non messi dalla printer durante la printNormal
				
				if (s.length() >= 2){
					if (!((s.substring(s.length()-2)).equalsIgnoreCase(R3define.CrLf))){
						s = s+R3define.CrLf;
					}
				}
				else
					s = s+R3define.CrLf;
			}
		}
		
		if (isFiscalAndSRTModel() || SRTPrinterExtension.isPRT())
			lastwrittenstring = s;
		
		setFlagVoidRefund(false);
		
		if (s.startsWith(R3_DELETELASTTICKET_R3))
		{
			return;
		}
		
		printNormalEFP90_F(i,s);
		
		lastwrittenstring = "";
		
		if (isFiscalAndSRTModel() || SRTPrinterExtension.isPRT())
		{
			scriviLastTicket(s);
		}
	}
	
	public void printRecItem(String s, long l, int i, int jIvaPolipos, long l1, String s1) throws JposException {
		System.out.println ( "MAPOTO-EXEC PRINT ITEM s="+s );
		System.out.println ( "MAPOTO-EXEC PRINT ITEM l="+l );
		System.out.println ( "MAPOTO-EXEC PRINT ITEM i="+i );
		System.out.println ( "MAPOTO-EXEC PRINT ITEM jIvaPolipos="+jIvaPolipos );
		System.out.println ( "MAPOTO-EXEC PRINT ITEM l1="+l1 );
		System.out.println ( "MAPOTO-EXEC PRINT ITEM s1="+s1 );
		
		if (SRTPrinterExtension.isSRT())
		{
			String ivadesc = DicoTaxLoad.getDTO(jIvaPolipos).getShortdescription();
			
			s = addIva(s, RTConsts.getMAXITEMDESCRLENGTH()-1, ivadesc);
		}
		
		if (SRTPrinterExtension.isPRT() && PrinterType.isRCHPrintFModel() && TaxData.isOmaggio(jIvaPolipos) && SharedPrinterFields.isfwRT2enabled()) {
			// lo vende automaticamente la printer
			return;
		}
		
		if (SRTPrinterExtension.isPRT()){
			if (RTTxnType.isRefundTrx() && PrinterType.isEpsonModel()){
				printRecNormalRefund(s, l, jIvaPolipos);
				return;
			}
		}
		
		AtLeastOnePrintedItem = true;
		
		int j = DicoTaxToPrinter.getFromPoliposToPrinter(jIvaPolipos);
		if ((DicoTaxLoad.isIvaAllaPrinter()) && (j == SharedPrinterFields.VAT_N4_Index))
			j = Integer.parseInt(SharedPrinterFields.VAT_N4_Dept);
			
		System.out.println ( "MAPOTO-EXEC PRINT ITEM jIvaPolipos="+jIvaPolipos);
		System.out.println ( "MAPOTO-EXEC PRINT ITEM j="+j);
		
		final int MAXLNGHOFDESCR = 28;
		
		setFlagVoidRefund(false);
		
		System.out.println ( "MAPOTO-EXEC PRINT ITEM"+s );
		
		String newdes= null;
		if ( PrinterType.isEpsonModel() )
			newdes =s;
		else
		{
			newdes = s.replaceAll("(?i)TOTALE", "TOTA..");
			s = newdes;
			newdes = s.replaceAll("(?i)TOTAL", "TOTA.");
			
			String k2desc="";
			for(int k2idx=0; k2idx<newdes.length(); k2idx++)
			{
                if ((int)(newdes.charAt(k2idx)) >= 128)
                    k2desc = k2desc + ".";
                else
                    k2desc = k2desc + newdes.charAt(k2idx);
			}
			newdes=k2desc;
				
			if (PrinterType.isDieboldRTOneModel())
			{
				s = newdes;
				newdes = s.replaceAll("\\)", "-");
				s = newdes;
				newdes = s.replaceAll("\\(", "-");
			}
		}
        String s2 = (s.length() > MAXLNGHOFDESCR) ? newdes.substring(0, MAXLNGHOFDESCR) :newdes;
        System.out.println("printRecItem in - s=<" + s2 + "> l=" + l + " i=" + i + " j=" + j
                + " l1=" + l1 + " s1=" + s1);
        if (PrinterType.isNCRFiscalModel() || PrinterType.isRCHPrintFModel())
        	fiscalPrinterDriver.printRecItem(s2, l / 100, 0, j, l1, s1);
        else
        	fiscalPrinterDriver.printRecItem(s2, l, 0, j, l1, s1);
        System.out.println("printRecItem out");
		
		if ( iconic.mytrade.gutenberg.jpos.printer.service.RoungickInLinePromo.isRoungickInLinePromo() )
		{
			System.out.println ( "MAPOTO-EXEC PRINT ITEM PLUS"+s1 );
			ArrayList A = iconic.mytrade.gutenberg.jpos.printer.service.RoungickInLinePromo.getDiscountFromTable(s1);
				
			if ( A != null )
			{
				for ( int x = 0 ; x < A.size(); x++ )
				{
					String S = (String) A.get(x);
					printNormal(jpos.FiscalPrinterConst.FPTR_S_RECEIPT, S);
				}
			}	
		}
		
		if (isFiscalAndSRTModel())
		{
			HardTotals.doPrintRecItem(l);
			
			scriviLastTicket(buildItem ( s, l ));
		}
		
		if (SRTPrinterExtension.isPRT())
		{
			HardTotals.doPrintRecItem(l);
			
			String ivadesc = String.format("%.2f", DicoTaxLoad.getDTO(j).getTaxrate())+"%";
			s = addIva(s, RTConsts.getMAXITEMDESCRLENGTH()-1, ivadesc);
			
			scriviLastTicket(buildItem ( s, l ));
		}
	}

	public void printRecVoid(String arg0) throws JposException {
		System.out.println ( "MAPOTO-EXEC PRINT VOID" );

		if (SRTPrinterExtension.isPRT())
			SharedPrinterFields.setFlagsVoidTicket( true );
		
		setFlagVoidRefund(false);
		
		pleaseVoid ( arg0 );
	}
	
	private void pleaseVoid ( String arg0 ) throws JposException
	{
		if ( PrinterType.isTH230Model() )
		{
			fiscalPrinterDriver.printRecVoid(arg0+" ");
		}
		else
		{
			fiscalPrinterDriver.printRecVoid(arg0);
		}
	}
	
	public void endFiscalReceipt(boolean arg0) throws JposException {

        if (SRTPrinterExtension.isPRT())
        	SharedPrinterFields.Lotteria.resetLottery();
        
		System.out.println ( "MAPOTO-EXEC_ENDFISCAL CHECKING NONFISCAL TRAILER" );
		
		if (!SRTPrinterExtension.isSRT()){
			switch ( PrinterType.getPrinterModel() )
			{
				case PrinterType.TH230_N:
			        if ( ! SharedPrinterFields.isFlagsVoidTicket() )
			        {
				        printRecMessage(" ");
				        printRecMessage("--------------------------------------------");
				        printRecMessage("   scontrino non fiscale ai sensi dello");
				        printRecMessage("    articolo 1, comma 429, della legge");
				        printRecMessage("            n. 311 del 2004");
				        printRecMessage("--------------------------------------------");
				        printRecMessage(" ");
			        }
			        SharedPrinterFields.setFlagsVoidTicket( false );
					break;
				case PrinterType.NCR7198_N:
			        if ( ! SharedPrinterFields.isFlagsVoidTicket() )
			        {
				        printRecMessage(" ");
				        printRecMessage("--------------------------------------------");
				        printRecMessage("   scontrino non fiscale ai sensi dello");
				        printRecMessage("    articolo 1, comma 429, della legge");
				        printRecMessage("            n. 311 del 2004");
				        printRecMessage("--------------------------------------------");
				        printRecMessage(" ");
			        }
			        SharedPrinterFields.setFlagsVoidTicket( false );
					break;
				case PrinterType.EP_TMT88:
			        if ( ! SharedPrinterFields.isFlagsVoidTicket() )
			        {
				        printRecMessage(" ");
				        printRecMessage("------------------------------------------");
				        printRecMessage("  scontrino non fiscale ai sensi dello");
				        printRecMessage("   articolo 1, comma 429, della legge");
				        printRecMessage("           n. 311 del 2004");
				        printRecMessage("------------------------------------------");
				        printRecMessage(" ");
			        }
			        SharedPrinterFields.setFlagsVoidTicket( false );
					break;
			}
		}
		
		System.out.println ( "MAPOTO-EXEC ENDFISCAL - arg0="+arg0 );
		String dateTime = "";
		
		if (SRTPrinterExtension.isRTActivated() == true) {
			DummyServerRT.setSRTTillID();
		}
		
  	    if (SRTPrinterExtension.isSRT())
		{
  	    	if (RTTxnType.isVoidTrx())
  	    		DummyServerRT.saveXmlReceipt();
  	    	
  	    	DummyServerRT.pleaseDoServerInfo();
  	    	
  	    	//setSRTTillID();
  	    	
  	    	if (RTTxnType.isVoidTrx())
  	    		DummyServerRT.restoreXmlReceipt();
  	    	
  	    	String current_dateTime = "";
  	    	LocalTimestamp clts = TransactionSale.getEndData();
  	    	current_dateTime = Sprint.f("%d%02d%02dT%02d%02d%02d",new Integer(clts.getYear()+1900),new Integer(clts.getMonth()+1),new Integer(clts.getDay()),new Integer(clts.getHour()),new Integer(clts.getMinute()),new Integer(clts.getSecond()));
  	    	System.out.println("current_dateTime="+current_dateTime+" previous_dateTime="+DummyServerRT.previous_dateTime);
  	    	if (!SharedPrinterFields.isFlagsVoidTicket() && (!current_dateTime.equalsIgnoreCase(DummyServerRT.previous_dateTime)))
  	    	{
				xml = new ArrayList();
				
				if (RTTxnType.isSaleTrx() || RTTxnType.isRefundTrx())
				{
                   if (DummyServerRT.isSRTNcrType()) {
                        if (DummyServerRT.CurrentReceiptNumber != null && DummyServerRT.CurrentReceiptNumber.trim().equals("1")) {
                        	DummyServerRT.pleaseDoDailyOpen();
                        }
                    }
	                   
					xml = SharedPrinterFields.a;
					LocalTimestamp lts = TransactionSale.getEndData();
					dateTime = Sprint.f("%d%02d%02dT%02d%02d%02d",new Integer(lts.getYear()+1900),new Integer(lts.getMonth()+1),new Integer(lts.getDay()),new Integer(lts.getHour()),new Integer(lts.getMinute()),new Integer(lts.getSecond()));
		  	    	System.out.println("dateTime="+dateTime);

					LoadMops.loadMops();
					
					ArrayList ivasemplice = new ArrayList();
					ArrayList sco = null;
					if (SSCO != null){
						sco = new ArrayList();
			       		for ( int index = 0 ; index < SSCO.size(); index++  ){
			       			VatInOutHandling vatInOutH = (VatInOutHandling) SSCO.get(index);
			    			
			    			double value = ( vatInOutH.getLordo() - vatInOutH.getTotalAmount() );
			    			sco.add((scontiSEMPLICI ) new scontiSEMPLICI(Integer.parseInt(vatInOutH.getSwVatCode()), Integer.parseInt(vatInOutH.getRtVatCode()), value, vatInOutH.getRate(), vatInOutH.getFullDescription()));
			    			
			    			ivaSEMPLICE iva = new ivaSEMPLICE(""+vatInOutH.getSwVatCode(),
									  ""+vatInOutH.getRtVatCode(),
									  vatInOutH.getRate(),
									  vatInOutH.getLordo(),
									  vatInOutH.getTotalNet(),
									  vatInOutH.getTotalTax(),
									  vatInOutH.getFullDescription(),
									  vatInOutH.getShortDescription());
			    			ivasemplice.add(iva);
			    			
			    		}
			       	}
					
					if (RTTxnType.isRefundTrx()){
						DummyServerRT.pleaseDoSetReferenceDocument(RTConsts.INTESTAZIONE4, SRTPrinterExtension.isRoungick());
					}
					
					if (RTTxnType.isSaleTrx()){
						if (!DummyServerRT.isSRTToshibaType())
							DummyServerRT.CurrentDailyAmount = ""+(int)((Double.parseDouble(DummyServerRT.CurrentDailyAmount)+(HardTotals.Totale.getDouble()*100)));
					}
					
					if (SharedPrinterFields.isRT2On()) {
//						J14Data.setrsAllTicket(RoungickTax.getCompleteVatTable());	// ???
					}
					
					DummyServerRT.pleaseDoFiscalReceipt(sco,
													   	xml,
													   	LoadMops.Mops,
													   	RTTxnType.getTypeTrx(),
													   	RoungickTax.getTotalTaxDouble(),
													   	SRTPrinterExtension.isIndentMode(),
													   	SRTPrinterExtension.isRoungick(),
													   	DummyServerRT.SRTTillID,
													   	dateTime,
													   	SRTPrinterExtension.getRefundMode(),
													   	DummyServerRT.CurrentReceiptNumber,
													   	DummyServerRT.CurrentFiscalClosure,
													   	ivasemplice,
													   	null,
													   	DummyServerRT.CurrentDailyAmount);
					
					if (RTTxnType.isSaleTrx()){
						if (DummyServerRT.isSRTToshibaType())
							DummyServerRT.CurrentDailyAmount = ""+(int)((Double.parseDouble(DummyServerRT.CurrentDailyAmount)+(HardTotals.Totale.getDouble()*100)));
					}
				}
				
				if (RTTxnType.isVoidTrx())
				{
					toDayDate();
					dateTime = DummyServerRT.getCurrent_dateTime();
					DummyServerRT.WriteVoided(dateTime,
											  DummyServerRT.CurrentReceiptNumber,
											  DummyServerRT.CurrentFiscalClosure,
											  DummyServerRT.CurrentDailyAmount);	// crea l'xml relativo allo scontrino annullato
				}
				
				//boolean secured = xml4srt.getSecuredMode();
				
				String xmlticket = "";
				if (RTTxnType.isVoidTrx())
				 xmlticket = DummyServerRT.StoreXmlTicket(DummyServerRT.CurrentReceiptNumber, SetVoidTrx.getTxnnumbertovoid());
				else
				 xmlticket = DummyServerRT.StoreXmlTicket(DummyServerRT.CurrentReceiptNumber, PosApp.getTransactionNumber()-1);
					
//				if (secured){
					if (DummyServerRT.sendToSRT(xmlticket, SRTPrinterExtension.getSecureModePort(), SRTPrinterExtension.getSecureModeTimeout()) == false)
					{
						if (RTTxnType.isSaleTrx() || RTTxnType.isRefundTrx())
							AutoVoidTrx();	// ???
						else {
							MessageBox.showMessage("OPERAZIONE ANNULLATA", null, MessageBox.OK);
						}
						
						printRecVoid("");
						
				  	    endTicket(DummyServerRT.CurrentReceiptNumber);
				  	    endTicketSRT(DummyServerRT.SRTServerID, DummyServerRT.SRTTillID, DummyServerRT.currentFingerPrint);
				  	    SharedPrinterFields.resetInTicket();
						
				  	    SharedPrinterFields.setFlagsVoidTicket( false );
					    
						endNonFiscal();
						
						setMonitorState();
						SharedPrinterFields.a = new ArrayList();
				   		
				   		RTTxnType.setSaleTrx();
//						setCanPost(true);	// ???
						return;
					}
//				}
				
  	    		if (RTTxnType.isSaleTrx() || RTTxnType.isRefundTrx())
  	    			DummyServerRT.previous_dateTime = dateTime;
				if (RTTxnType.isVoidTrx())
				{
					Files.moveFile(SetVoidTrx.getSourcefiletovoid(), SetVoidTrx.getSourcefiletovoid()+".voided");
//					setPrelevaDenaro(VoidTrx(SetVoidTrx.getTxnheadertovoid()));	// aggiorna il flag Voided della transazione sul db		// ???
				}
				
				SSCO = null;
  	    	}
		}
  	    
		if (isLNFMAndSRTModel())
		{
			setFlagVoidRefund(false);
			
	  	    endTicket(DummyServerRT.CurrentReceiptNumber);
	  	    
	  	    endTicketSRT(DummyServerRT.SRTServerID, DummyServerRT.SRTTillID, DummyServerRT.currentFingerPrint);
			HardTotals.doEndFiscalSRT(RTTxnType.getTypeTrx());
			SharedPrinterFields.resetInTicket();	    

			SharedPrinterFields.setFlagsVoidTicket( false );
	        
			endNonFiscal();
			
			DummyServerRT.StoreTicket4Reprint(DummyServerRT.CurrentReceiptNumber);
			
			setMonitorState();
			SharedPrinterFields.a = new ArrayList();

			RTTxnType.setSaleTrx();
//			setCanPost(true);	// ???
			return;
		}
		
		if (isFiscalAndSRTModel())
		{
	  	    endTicket(DummyServerRT.CurrentReceiptNumber);
	  	    
	  	    endTicketSRT(DummyServerRT.SRTServerID, DummyServerRT.SRTTillID, DummyServerRT.currentFingerPrint);

	  	    SharedPrinterFields.setFlagsVoidTicket( false );
		}
		
		setFlagVoidRefund(false);
		AtLeastOnePrintedItem = false;
		int state=0;
		resetprtDone();
		
		System.out.println ( "MAPOTO-EXEC_ENDFISCAL" );
		
		SharedPrinterFields.resetInTicket();	    
		if (PrinterType.isCUSTOMK2Model())
		{
			// se per caso si chiama la fiscalPrinterDriver.getPrinterState() senza aver
			// venduto nulla il driver della stampante esce logicamente dallo stato fiscale
			// e si mette in stato FPTR_PS_MONITOR (e ad esempio la sequenza passaggio tessera
			// più subtotale provocherebbe la non chiusura dello scontrino).		  
			state = this.getPrinterState();
			System.out.println("CUSTOMK2 state="+state);
		}
		else if (PrinterType.isNCRFiscalModel())
        	state = getFiscalPrinterState();
        if (!((PrinterType.isNCRFiscalModel()) && state == jpos.FiscalPrinterConst.FPTR_PS_MONITOR))
        {
        	if (PrinterType.isEpsonModel()) {
        		abilitaTaglioCarta(false);
        	}
        	
        	if (PrinterType.isRCHPrintFModel()) {
        		stampaBarcodePerResi();
        	}
        	
        	fiscalPrinterDriver.endFiscalReceipt(arg0);
        	
        	if (PrinterType.isEpsonModel()) {
        		stampaBarcodePerResi();
        		abilitaTaglioCarta(true);
        	}
        }
        
        SharedPrinterFields.resetInTicket();	    
        SharedPrinterFields.setFlagsVoidTicket( false );
        
		if (SRTPrinterExtension.isPRT() && PrinterType.isEpsonModel() && RTTxnType.isRefundTrx())
			currentTicket = 0;

		String Total = currentFiscalTotal(jpos.FiscalPrinterConst.FPTR_GD_DAILY_TOTAL);
	    if ( fiscalPrinterDriver.checkCurrentDailyTotal(Total) == false )
	    {
			if (SRTPrinterExtension.isPRT() && PrinterType.isRCHPrintFModel() && (RTConsts.getCURRENTROUNDING() == RTConsts.FULLROUNDING) && (SharedPrinterFields.RoundingRT != 0.0)) {
				if ( fiscalPrinterDriver.checkCurrentDailyTotalRounded(Total, SharedPrinterFields.RoundingRT.doubleValue()) == false ) {
					System.out.println ("FISCAL PRINTER RECEIPT NOT STORED");
			    	throw new JposException(9999);
				}
			}
			else {
				System.out.println ("FISCAL PRINTER RECEIPT NOT STORED");
		    	throw new JposException(9999);
			}
	    }
		
		if (SRTPrinterExtension.isPRT())
			RTTxnType.setSaleTrx();

		setMonitorState();
		SharedPrinterFields.a = new ArrayList();			// prova reset scontrino precedente
		
//		SharedPrinterFields.setCanPost(true);	// ???

		if (isFiscalAndSRTModel())
		{
			HardTotals.doEndFiscalSRT(RTTxnType.getTypeTrx());
			
			DummyServerRT.StoreTicket4Reprint(DummyServerRT.CurrentReceiptNumber);
			
			RTTxnType.setSaleTrx();
		}
		
		if (SRTPrinterExtension.isPRT())
		{
			HardTotals.doEndFiscalSRT(RTTxnType.getTypeTrx());
		}
		
		SMTKCommands.Base64_Ticket(PosApp.getTransactionNumber()-1, false);
		SMTKCommands.Smart_Ticket(PosApp.getTransactionNumber()-1, false);
	}
	
	public void resetPrinter() throws JposException {
		System.out.println ( "MAPOTO-EXEC RESET" );
		
		if ( inReset == true )
		{
			return;
		}
		inReset = true;
		setFlagVoidRefund(false);
		int a = getFiscalPrinterState();
		System.out.println("MAPOTO-EXEC_RESET_IN <"+a+">");
		if ( a == jpos.FiscalPrinterConst.FPTR_PS_MONITOR )
		{
			System.out.println("MAPOTO-EXEC_RESET_OUT IN STATE MONITOR NO ACTION ");
			return;
		}
		if (a == jpos.FiscalPrinterConst.FPTR_PS_FISCAL_RECEIPT)
		{
			pleaseVoid("");
			fiscalPrinterDriver.endFiscalReceipt(false);
		}
		else if (a == jpos.FiscalPrinterConst.FPTR_PS_NONFISCAL)
		{
			fiscalPrinterDriver.endNonFiscal();
		}
		else if (a == jpos.FiscalPrinterConst.FPTR_PS_FISCAL_RECEIPT_ENDING)
		{
			fiscalPrinterDriver.endFiscalReceipt(false);
		}
		resetAndClear();
		if (!PrinterType.isCustomModel())	// per non rallentare troppo
			System.out.println("MAPOTO-EXEC_RESET_OUT <"+getFiscalPrinterState()+">");
		else
			System.out.println("MAPOTO-EXEC_RESET_OUT");
	}
	
	public void beginNonFiscal() throws JposException {
    	if (!PrinterType.isCustomModel())	// per non rallentare troppo
    		System.out.println(  "MAPOTO-EXEC BEGINNONFISCALT <"+getFiscalPrinterState()+">");
    	else
    		System.out.println(  "MAPOTO-EXEC BEGINNONFISCALT");
		
		SharedPrinterFields.setInTicket();
		resetprtDone();
		setNonFiscalState();
		
		if (SRTPrinterExtension.isSRT())
		{
			HardTotals.doBeginNonFiscal();
		}
		
		if (SRTPrinterExtension.isPRT())
		{
		    cancellaFile();
			initTicketOnFile();	// rimette l'header nel file LastTicket.out appena cancellato
			HardTotals.doBeginNonFiscal();
		}
		
		inReset = false;
		setFlagVoidRefund(false);
		
    	fiscalPrinterDriver.beginNonFiscal( );
    	PrintLogo(SharedPrinterFields.LOGO_NUMBER);
		initTicket();
	}
	
	public void endNonFiscal() throws JposException {
		System.out.println  ( "MAPOTO-EXEC ENDNONFISCAL" );

        if (SRTPrinterExtension.isPRT())
        	SharedPrinterFields.Lotteria.resetLottery();
        
		if (SRTPrinterExtension.isSRT())
		{
			HardTotals.doEndNonFiscal();
		}
		
		if (SRTPrinterExtension.isPRT())
		{
			HardTotals.doEndNonFiscal();
		}
		
		setFlagVoidRefund(false);
		SharedPrinterFields.resetInTicket();
		resetprtDone();
    	PrintLogo(SharedPrinterFields.TRAILER_LOGO_NUMBER);
    	fiscalPrinterDriver.endNonFiscal( );
		setMonitorState();
	}
	
	public void printRecItemAdjustment(int arg0, String arg1, long arg2, int arg3) throws JposException {
		System.out.println ( "MAPOTO-EXEC PRINT ITEM ADJUSTMENT" );
		
		setFlagVoidRefund(false);
		if ( PrinterType.isEpsonModel() )
		{
			System.out.println("printRecItemAdj. in - arg0=" + arg0 + " arg1=" + arg1 + " arg2="
	                + arg2 + " arg3=" + arg3);
	        fiscalPrinterDriver.printRecSubtotalAdjustment(arg0, "Sconto " + arg1, arg2);
	        System.out.println("printRecItemAdj. out");
		}
		else
		{
			fiscalPrinterDriver.printRecItemAdjustment(arg0, arg1, arg2, arg3);
		}
		
		if (isFiscalAndSRTModel() || SRTPrinterExtension.isPRT())
		{
	    	HardTotals.doSubtotalAdjustment(arg2);

	    	scriviLastTicket(buildItemAdjustment ( "", arg2 ));
		}
	}

	public void printRecMessage(String arg0) throws JposException {
		System.out.println ( "MAPOTO-EXEC PRINT MESSAGE "+arg0+" staticMsgLen=<"+staticMsgLen+">" );
		
		if (isLNFMAndSRTModel())
		{
			setFlagVoidRefund(false);
			
		    int i = RTConsts.setMAXLNGHOFLENGTH();
		    
		    String s1 = String13Fix.replaceAll(arg0,R3define.Lf, "�");
		    s1 = String13Fix.replaceAll(s1,R3define.Cr, "�");
		    s1 = String13Fix.replaceAll(s1,"��", "�");
		    String[] msg = String13Fix.split(s1,"�");
		    for (int z = 0; z < msg.length; z++)
		    {
		    	printNormal_ejoff(jpos.FiscalPrinterConst.FPTR_S_RECEIPT,(msg[z].length() > i) ? msg[z].substring(0, i) : msg[z]);
		    }
			return;
		}
		
		if (PrinterType.isDieboldRTOneModel())
		{
			String s = arg0;
			arg0 = s.replaceAll("\\)", " ");
			s = arg0;
			arg0 = s.replaceAll("\\(", " ");
		}
		
		String back30 = "";
		setFlagVoidRefund(false);
	    int i = 43;
	    
    	if (( staticMsgLen == 0 ) || ( PrinterType.isTHFEJModel() ))
    		staticMsgLen = fiscalPrinterDriver.getMessageLength();
    	i = staticMsgLen;
    	
	    String s1 = String13Fix.replaceAll(arg0,R3define.Lf, "�");
	    s1 = String13Fix.replaceAll(s1,R3define.Cr, "�");
	    s1 = String13Fix.replaceAll(s1,"��", "�");

	    if (PrinterType.isEpsonModel())
	    	s1 = String13Fix.replaceAll(s1, "€", ""+(char)96);
	    if (PrinterType.isRCHPrintFModel())
	    	s1 = String13Fix.replaceAll(s1, "€", ""+(char)127);
		
		if ( !PrinterType.isEpsonModel() )
		{
			String newdes= null;
			newdes = s1.replaceAll("(?i)TOTALE", "TOTA..");
			s1 = newdes;
			newdes = s1.replaceAll("(?i)TOTAL", "TOTA.");
			s1 = newdes;
			
			if (PrinterType.isRCHPrintFModel() && SRTPrinterExtension.isPRT())
			{
			      String oldS = s1;
			      s1 = "";
			      for ( int j = 0 ; j < oldS.length(); j++ )
			      {
			    	  if ((oldS.charAt(j) < (char)32) || (oldS.charAt(j) > (char)127))
			    	  {
		        		  if ((oldS.charAt(j) == (char)10) || (oldS.charAt(j) == (char)13) || (oldS.charAt(j) == '�'))
		    	    		  s1 = s1 + oldS.charAt(j);
		        		  else
		        			  s1 = s1 + ' ';
			    	  }
			    	  else
			    		  s1 = s1 + oldS.charAt(j);
			      }
			}
			
		}
		
	    String[] msg = String13Fix.split(s1,"�");
	    for (int z = 0; z < msg.length; z++)
	    {
        	if (msg[z].startsWith(getCFPIvaTag())) {
        		String cfpi = msg[z].substring(getCFPIvaTag().length());
                cfpi = String13Fix.replaceAll(cfpi, "\r", "");
                cfpi = String13Fix.replaceAll(cfpi, "\n", "").trim();
        		if (SRTPrinterExtension.isPRT()) {
        			printRecCFPiva(cfpi);
        			continue;
        		}
        		else {
        			msg[z] = (cfpi.length() == PILEN ? PI+cfpi : CF+cfpi);
        		}
        	}

        	if (PaperSavingProperties.isMode()) {
	        	if (msg[z].length() == 0 || msg[z].replaceAll(" ", "").length() == 0)
	        		continue;
        	}
        	
	    	if (PrinterType.isNCRFiscalModel())
	    	{
   				back30 = please30 ( msg[z] );
	    		fiscalPrinterDriver.printRecMessage(back30);
	    	}
	    	else
	    	{
	    		if (PrinterType.isRCHPrintFModel())
	    		{
	    			if (fw >= 6.0) {
	    				String Testo = (msg[z].length() > i) ? msg[z].substring(0, i) : msg[z];
	    				StringBuffer printrecmessage = new StringBuffer("=\"/("+Testo+")/&1");	/* 		="/(Testo)/&1 		*/
	    				if (AtLeastOnePrintedItem)
		    				printrecmessage = new StringBuffer("=\"/("+Testo+")");
	    				fiscalPrinterDriver.executeRTDirectIo(0, 0, printrecmessage);
	    			}
		    		else
		    			fiscalPrinterDriver.printRecMessage((msg[z].length() > i) ? msg[z].substring(0, i) : msg[z]);
	    		}
	    		else
	    			fiscalPrinterDriver.printRecMessage((msg[z].length() > i) ? msg[z].substring(0, i) : msg[z]);
	    	}
	    }
	    
		if (isFiscalAndSRTModel() || SRTPrinterExtension.isPRT())
		{
	    	if (!arg0.equalsIgnoreCase(lastwrittenstring)){
		    	scriviLastTicket(arg0);
	    	}
		}
		
	    System.out.println ( "MAPOTO-EXEC PRINT MESSAGE FINITA "+arg0 );
	}

	public void printRecNormalRefund(String s, long l, int i) throws JposException
	{
		System.out.println ( "MAPOTO-EXEC PRINT REFUND s="+s );
		System.out.println ( "MAPOTO-EXEC PRINT REFUND l="+l);
		System.out.println ( "MAPOTO-EXEC PRINT REFUND i="+i );
		
		printRecRefund(s, l, i);
		
		if (isFiscalAndSRTModel())
		{
		  	HardTotals.doPrintRecRefund(l);
		  	
	    	scriviLastTicket(buildItemRefund ( s, l ));
		}
		
		if (SRTPrinterExtension.isPRT())
		{
		  	HardTotals.doPrintRecRefund(l);
		  	
			String ivadesc = String.format("%.2f", DicoTaxLoad.getDTO(i).getTaxrate())+"%";
			s = addIva(s, RTConsts.getMAXITEMDESCRLENGTH()-1, ivadesc);
			
			scriviLastTicket(buildItem ( s, l ));
		}
	}

	public void printRecRefund(String s, long l, int iIvaPolipos) throws JposException {
		System.out.println ( "MAPOTO-EXEC PRINT REFUND s="+s );
		System.out.println ( "MAPOTO-EXEC PRINT REFUND l="+l);
		System.out.println ( "MAPOTO-EXEC PRINT REFUND i="+iIvaPolipos );
		
		if (SRTPrinterExtension.isSRT())
		{
			String ivadesc = DicoTaxLoad.getDTO(iIvaPolipos).getShortdescription();
			
			s = addIva(s, RTConsts.getMAXITEMDESCRLENGTH()-1, ivadesc);
		}
		
		if (SRTPrinterExtension.isPRT() && TaxData.isAcconti(iIvaPolipos) && SharedPrinterFields.isfwRT2enabled())
		{
			printRecAcconto(s, l, iIvaPolipos);
			return;
		}
		
		if (SRTPrinterExtension.isPRT() && TaxData.isBuonoMonouso(iIvaPolipos) && SharedPrinterFields.isfwRT2enabled())
		{
			printRecBMonoUso(s, l, iIvaPolipos);
			return;
		}
		
		int i = DicoTaxToPrinter.getFromPoliposToPrinter(iIvaPolipos);
		if ((DicoTaxLoad.isIvaAllaPrinter()) && (i == SharedPrinterFields.VAT_N4_Index))
			i = Integer.parseInt(SharedPrinterFields.VAT_N4_Dept);
		
		setFlagVoidRefund(true);
		System.out.println ( "MAPOTO-EXEC PRINT REFUND "+s );
		
	    final int MAXLNGHOFDESCR = 23;
		String newdes = null;
		if ( PrinterType.isEpsonModel() )
			newdes =s;
		else
		{
			newdes = s.replaceAll("(?i)TOTALE", "TOTA..");     
			s = newdes;
			newdes = s.replaceAll("(?i)TOTAL", "TOTA.");

			String k2desc="";
			for(int k2idx=0; k2idx<newdes.length(); k2idx++)
			{
		        if ((int)(newdes.charAt(k2idx)) >= 128)
		            k2desc = k2desc + ".";
		        else
		            k2desc = k2desc + newdes.charAt(k2idx);
			}
			newdes=k2desc;
		}
	    String newMsg = (newdes.length()<= MAXLNGHOFDESCR)? newdes: newdes.substring (0, MAXLNGHOFDESCR);
	    if (PrinterType.isNCRFiscalModel())
	    	fiscalPrinterDriver.printRecRefund (newMsg, l / 100, i);
	    else if (PrinterType.isRCHPrintFModel())
	    {
	    	if (l > 0)
	    		fiscalPrinterDriver.printRecRefund (newMsg, l / 100, i);
	    	else
	    		printNormal(jpos.FiscalPrinterConst.FPTR_S_RECEIPT, "Reso "+s);
	    }
	    else
	    	fiscalPrinterDriver.printRecRefund (newMsg, l, i);
	    
		if (SRTPrinterExtension.isPRT()){
			if (RTTxnType.isRefundTrx() && PrinterType.isEpsonModel()){
				return;	// perchè in questo caso la parte sottostante la fa già il metodo printRecNormalRefund()
			}
		}
		
		if (isFiscalAndSRTModel())
		{
		  	HardTotals.doPrintRecRefund(l);
		  	
	    	scriviLastTicket(buildItemRefund ( s, l ));
		}
		
		if (SRTPrinterExtension.isPRT())
		{
		  	HardTotals.doPrintRecRefund(l);
		  	
			String ivadesc = String.format("%.2f", DicoTaxLoad.getDTO(i).getTaxrate())+"%";
			s = addIva(s, RTConsts.getMAXITEMDESCRLENGTH()-1, ivadesc);
			
			scriviLastTicket(buildItemRefund ( s, l ));
		}
	}

	public void printRecSubtotal(long arg0) throws JposException {
		System.out.println ( "MAPOTO-EXEC PRINT SUBTOTAL "+arg0 );
		
		setFlagVoidRefund(false);
		
		if (PrinterType.isRCHPrintFModel()) {
/*			ITransactionSale tsale = null;										// ???
			try{
				tsale = (ITransactionSale)posEngine.getTransaction();
			}
			catch(ClassCastException e){
				return;
			}
			if ((tsale != null) && (tsale.getItemLines(false).size() == 0))
				return;*/
		}
		if (!isprtDone() && (fiscalPrinterDriver.getPrinterState() == jpos.FiscalPrinterConst.FPTR_PS_FISCAL_RECEIPT))
			fiscalPrinterDriver.printRecSubtotal(arg0);
	}

	public void printRecSubtotalAdjustment(int arg0, String arg1, long arg2) throws JposException {
		System.out.println ( "MAPOTO-EXEC PRINT SUBTOTAL ADJUSTMENT" );
		System.out.println ( "MAPOTO-EXEC PRINT SUBTOTAL ADJUSTMENT - arg0="+arg0 );
		System.out.println ( "MAPOTO-EXEC PRINT SUBTOTAL ADJUSTMENT - arg1="+arg1 );
		System.out.println ( "MAPOTO-EXEC PRINT SUBTOTAL ADJUSTMENT - arg2="+arg2 );
		
		if (SRTPrinterExtension.isPRT()){
			HardTotals.doSubtotalAdjustment(arg2);
			printScontiByTax();
			return;
		}
		
		printRecSubtotalAdjustment_I(arg0, arg1, arg2);
		
		if (isFiscalAndSRTModel())
		{
			if (RTTxnType.isVoidTrx())
				return;
			printScontiByTax(arg0, arg1, arg2);
			HardTotals.doSubtotalAdjustment(arg2);
		}
	}
	
	private void printRecSubtotalAdjustment_I(int arg0, String arg1, long arg2) throws JposException {
		setFlagVoidRefund(false);
		
		System.out.println ( "MAPOTO-EXEC PRINT SUBTOTAL ADJ "+arg1+" "+arg2 );
		
		if ( PrinterType.isEpsonModel() )
		{
	        System.out.println("printRecSubtotalAdjustment in - arg0=" + arg0 + " arg1=" + arg1
	                + " arg2=" + arg2);
	        fiscalPrinterDriver.printRecSubtotalAdjustment(arg0, "Sconto " + arg1, arg2);
	        System.out.println("printRecSubtotalAdjustment out");
		}
		else
		{
	    	if ( PrinterType.isTH230Model() )
	    		fiscalPrinterDriver.printRecSubtotalAdjustment(arg0, arg1+" ", arg2);
	    	else if (PrinterType.isNCRFiscalModel() || PrinterType.isRCHPrintFModel())
	    		fiscalPrinterDriver.printRecSubtotalAdjustment(arg0, "Sconto " + arg1, arg2 / 100);
	    	else
	    		fiscalPrinterDriver.printRecSubtotalAdjustment(arg0, arg1, arg2);
		}
	}

	public void directIO(int i, int data[],  Object o) throws JposException
	{
		System.out.println ( "MAPOTO-EXEC DIRECTIO" );
		
		String s =  o.toString();
		StringBuffer bf = new StringBuffer(s);
		
		if ( ! SharedPrinterFields.isInTicket() ) 				// directIO estemporanei
		{
			if (! PrinterType.isEpsonModel())
				fiscalPrinterDriver.directIO(i, data, o);
			else
				fiscalPrinterDriver.directIO(i, data, bf);
			return;
		}
		
		if (SRTPrinterExtension.isSRT() || SRTPrinterExtension.isPRT())
		{
			scriviLastTicket(bf.toString());
		}
		
		setFlagVoidRefund(false);
		
		System.out.println ( "XDIRECTIO :"+ i +":"+data[0]+":"+s+":");
		
		if (PrinterType.isRCHPrintFModel()) {
            String[] pString = {new String(s)};
        	if (i >= 1000)
        		fiscalPrinterDriver.directIO(i, data, pString);
        	else
        		fiscalPrinterDriver.directIO(i, data, s);
		}
		else {
			if (PrinterType.isNCRFiscalModel())
				fiscalPrinterDriver.directIO(i, data, s.toString());
			else
				fiscalPrinterDriver.directIO(i, data, s);
		}
	}
	
    private void printScontiByTax(int arg0, String arg1, long arg2) throws JposException
	{
		SSCO = RoungickTax.getVatTable(true);
		if (SSCO != null)
		{
//			discountedtaxamount = CurrencyInfo.newCurrency(null,0.0);	// prima era così
			discountedtaxamount = new BigDecimal(0.);
			discountedtaxamount = discountedtaxamount.setScale(2, RoundingMode.HALF_DOWN);
			for (int index=0; index < SSCO.size(); index++)
			{
				VatInOutHandling vatInOutH = (VatInOutHandling) SSCO.get(index);
				double value = Math.rint(( vatInOutH.getLordo() - vatInOutH.getTotalAmount() ) * 100) / 100;
				if (value > 0.00) {
					String out = buildSRTSubtotalAdjustment ( vatInOutH.getFullDescription(), (long)(value*10000) );
					
					EjCommands.Write(out, true);
					
					printRecSubtotalAdjustment_I(arg0, vatInOutH.getFullDescription(), (long)(value*10000));

//					ICurrency discamount = CurrencyInfo.newCurrency(null,value);	// prima era così
					BigDecimal discamount = new BigDecimal(value);
					discamount = discamount.setScale(2, RoundingMode.HALF_DOWN);

//					ICurrency d = discamount.subtract((discamount.multiplyBy(100.0)).divideBy(100+vatInOutH.getRate()));	// prima era così
					BigDecimal d = discamount.subtract(discamount.multiply(new BigDecimal(100.0)).divide(new BigDecimal(100.0+vatInOutH.getRate()),2,RoundingMode.HALF_DOWN));
					d = d.setScale(2, RoundingMode.HALF_DOWN);
					
//					ICurrency dd = CurrencyInfo.newCurrency(null,Math.rint(d.doubleValue()*100)/100);						// prima era così
					BigDecimal dd = d;
					dd = dd.setScale(2, RoundingMode.HALF_DOWN);
					
//					discountedtaxamount = discountedtaxamount.add(dd);														// prima era così
//					discountedtaxamount = discountedtaxamount.truncate(discountedtaxamount.minimumDisplayableValue());		// prima era così
					discountedtaxamount = discountedtaxamount.add(dd);
				}
			}
		}
		else
			System.out.println("printScontiByTax - SSCO is null");
	}
    
    private void printScontiByTax() throws JposException
	{
		SSCO = RoungickTax.getVatTable(true);				
		if (SSCO != null)
		{
			for (int index=0; index < SSCO.size(); index++)
			{
				VatInOutHandling vatInOutH = (VatInOutHandling) SSCO.get(index);
				double value = ( vatInOutH.getLordo() - vatInOutH.getTotalAmount() );
				if (value > 0.00) {
					if (PrinterType.isEpsonModel()){
						String myDepartment = "";
						if (Integer.parseInt(vatInOutH.getRtVatCode()) == SharedPrinterFields.VAT_N4_Index)
							myDepartment = Sprint.f("%02d", SharedPrinterFields.VAT_N4_Dept);
						else
							myDepartment = Sprint.f("%02d", vatInOutH.getRtVatCode());
						
						StringBuffer sbcmd = new StringBuffer("");
						sbcmd = new StringBuffer(myDepartment);
				      	
						String myOperator = "01";
						String myDiscountDescription = "Sconto IVA";
						String myDiscountAmount = Sprint.f("%09d", Math.rint(value*100));
						String myDiscountType = "3";
						String myAlignment = "1";
						
						sbcmd = new StringBuffer(myOperator + myDiscountDescription + myDiscountAmount + myDiscountType + myDepartment + myAlignment);
						System.out.println("printScontiByTax - sbcmd="+sbcmd);
						fiscalPrinterDriver.executeRTDirectIo(1083, 0, sbcmd);
						
				        String out = buildSubtotalAdjustment ( myDiscountDescription.substring(7)+" "+vatInOutH.getRate()+"%", (long)(Math.rint(value*100) * 100) );
				        scriviLastTicket(out);
					}
					else if (PrinterType.isRCHPrintFModel()){
						String myDepartment = "";
						if (Integer.parseInt(vatInOutH.getRtVatCode()) == SharedPrinterFields.VAT_N4_Index)
							myDepartment = SharedPrinterFields.VAT_N4_Dept;
						else
							myDepartment = ""+DicoTaxToPrinter.getFromPoliposToPrinter(Integer.parseInt(vatInOutH.getSwVatCode()));
						String myDiscountAmount = ""+(int)(Math.rint(value*100));
						String myDiscountDescription = "Sconto IVA";
						
						StringBuffer sbcmd = new StringBuffer("=U/*"+myDepartment+"/$"+myDiscountAmount+"/("+myDiscountDescription+")");
						System.out.println("printScontiByTax - sbcmd="+sbcmd);
						fiscalPrinterDriver.executeRTDirectIo(0, 0, sbcmd);
						
				        String out = buildSubtotalAdjustment ( myDiscountDescription.substring(7)+" "+vatInOutH.getRate()+"%", (long)(Math.rint(value*100) * 100) );
				        scriviLastTicket(out);
					}
				}
			}
		}
		else
			System.out.println("printScontiByTax - SSCO is null");
	}
    
	public void printRecTotal(long arg0, long arg1, String arg2) throws JposException {
		System.out.println ( "MAPOTO-EXEC PRINT TOTAL "+arg0+" "+arg1+" "+arg2 );
		
		if (SRTPrinterExtension.isPRT() && (RTConsts.getCURRENTROUNDING() == RTConsts.FULLROUNDING)) {
	    	System.out.println("RoundingData.getRoundingRT = "+RTRounding.getRoundingRT());
			if (!(RTRounding.getRoundingRT() == 0.)) {
				SharedPrinterFields.RoundingRT = new Double(RTRounding.getRoundingRT());
				long val =  (long) (SharedPrinterFields.RoundingRT*10000);
				if (SharedPrinterFields.fiscalEJ != null)
					SharedPrinterFields.fiscalEJ.setRounding(val);
				System.out.println("RoundingRT = "+SharedPrinterFields.RoundingRT);
				RTRounding.setRoundingRT(0.);
			}
			else {
				SharedPrinterFields.RoundingRT = 0.0;
				if (SharedPrinterFields.fiscalEJ != null)
					SharedPrinterFields.fiscalEJ.setRounding(0);
			}
		}
		
	    if (SmartTicket.isSmart_Ticket())
			smtkamount = (double)((new Long(arg0)).doubleValue() / 10000);
		
		// ora dovrebbe arrivare sempre tipo 001Contanti
		String pd = arg2;
		String p = arg2.substring(0,3);
		String d = arg2.substring(3);
		if (SRTPrinterExtension.isPRT())
			arg2 = d;
		System.out.println ( "MAPOTO-EXEC PRINT TOTAL - arg2="+arg2 );
		
		if (SRTPrinterExtension.isSRT())
			DummyServerRT.pleaseDoServerInfo();
		
		if (SRTPrinterExtension.isPRT() && PrinterType.isRCHPrintFModel()){
			if ((arg0 == 0) && (arg1 == 0)) {
/*				ITransactionSale tsale = null;												// ???
				try{
					tsale = (ITransactionSale)posEngine.getTransaction();
				}
				catch(ClassCastException e){
				}
				if ((tsale != null) && ((tsale.getItemLines(false).size() == 0) || (AtLeastOnePrintedItem == false))){
					int aliquota = getVATTableEntry_Rch();
					// brutto ma per ora la stampante si incazza se si passa la tessera 
					// e poi si chiude lo scontrino a zero senza vendere nulla
					// oppure se nello scontrino non c'è nessun item fiscale
					//fiscalPrinterDriver.printRecItem("....................", 1, 0, 8, 0, "");
					//fiscalPrinterDriver.printRecVoidItem("....................", 1, 1, 1, 0, 8);
					fiscalPrinterDriver.printRecItem("....................", 0, 0, aliquota, 0, "");
				}*/
			}
		}
		
		if (SRTPrinterExtension.isPRT()){
			if (PrinterType.isEpsonModel())
				arg2 = LoadMops.getPrefixPayment(p+arg2) + arg2;
			else if (PrinterType.isRCHPrintFModel()) {
				pd = arg2;
				arg2 = LoadMops.getRCHPrefixPayment(arg2);
				System.out.println ( "MAPOTO-EXEC PRINT TOTAL - arg2="+arg2 );
				if (SharedPrinterFields.isfwRT2enabled()) {
					int t = Integer.parseInt(p.substring(0,1));
					if (t == LoadMops.TICKETWN_TYPE)
						arg2 = arg2+","+p.substring(1,3);
					System.out.println ( "MAPOTO-EXEC PRINT TOTAL - arg2="+arg2 );
				}
			}
			System.out.println ( "MAPOTO-EXEC PRINT TOTAL - arg2="+arg2 );
		}
		
		if (SRTPrinterExtension.isPRT()){
			if (RTTxnType.isRefundTrx() && PrinterType.isRCHPrintFModel()){
				if ((SharedPrinterFields.Lotteria.getLotteryCode() != null) && (SharedPrinterFields.Lotteria.getLotteryCode().length() > 0))
					LotteryCommands.SendLotteryCode(SharedPrinterFields.Lotteria.getLotteryCode());
				
				long t = arg0;
				
				SharedPrinterFields.getChangeDescription();
				arg0 = 0;
				arg1 = 0;
				arg2 = LoadMops.getRCHPrefixPayment(SharedPrinterFields.ChangeCurrency);
				System.out.println ( "MAPOTO before driver.printRecTotal arg2="+arg2);
	        	fiscalPrinterDriver.printRecTotal(arg0, arg1, arg2);
	        	
	        	try {
					// scrittura file lastticket
					
					String out = buildTotal ( "", t );
					scriviLastTicket(out);
					
					out = buildTotalIva ( "", RoungickTax.getTotalTaxLong() );
					scriviLastTicket(out);
					
					LocalTimestamp lts = TransactionSale.getEndData();
					String date = Sprint.f("%02d-%02d-%d %02d:%02d",new Integer(lts.getDay()),new Integer(lts.getMonth()+1),new Integer(lts.getYear()+1900),new Integer(lts.getHour()),new Integer(lts.getMinute())); 
					out = SharedPrinterFields.ALINER.substring(0, (int)((RTConsts.setMAXLNGHOFLENGTH()-date.length())/2))+date;
					scriviLastTicket(out);
					
					out = LastTicket.getDocnum();
					scriviLastTicket(out);
					
					if ((SharedPrinterFields.Lotteria.getLotteryCode() != null) && (SharedPrinterFields.Lotteria.getLotteryCode().length() > 0)) {
						out = LastTicket.getLottery()+SharedPrinterFields.Lotteria.getLotteryCode();
						scriviLastTicket(out);
					}
					
					out = SharedPrinterFields.ALINER.substring(0, (int)((RTConsts.setMAXLNGHOFLENGTH()-SharedPrinterFields.RTPrinterId.length())/2))+SharedPrinterFields.RTPrinterId;
					scriviLastTicket(out);
				} catch (Exception e) {
					System.out.println("LastTicket error: "+e.getMessage());
				}
	        	return;
			}
		}

		if (SRTPrinterExtension.isSRT() || (SRTPrinterExtension.isPRT() && RTTxnType.isRefundTrx()==false)){
			String s = arg2;
			arg2 = LoadMops.getSrtDescription(pd);
			SharedPrinterFields.getChangeDescription();
			if (LoadMops.isNonRiscosso(arg2)) {
				totaleNonRiscosso+=arg1;
			}
			if (SRTPrinterExtension.isPRT())
				arg2=s;
		}
		
		if (isFiscalAndSRTModel() || SRTPrinterExtension.isPRT())
		{
			if (HardTotals.TotalePagato.getLongX100() == 0)
			{
				String out = buildTotal ( "", arg0 );
				scriviLastTicket(out);
				
				DummyServerRT.groups = new ArrayList();
			}
		}

		System.out.println ( "MAPOTO xprintRecTotal"+arg0+" "+arg1+" "+arg2);
		if ((arg0 < 0) || (arg1 < 0)){
			System.out.println ( "MAPOTO MINORE DI ZERO");
			return;
		}
		
		setFlagVoidRefund(false);
		setprtDone();
		
		if ( isCurrentTicketZero() )
		{
			System.out.println ( "MAPOTO before currentFiscalTotal");
			String printerTotal = currentFiscalTotal(jpos.FiscalPrinterConst.FPTR_GD_CURRENT_TOTAL);
			System.out.println ( "MAPOTO after currentFiscalTotal <"+printerTotal+">");
			if ( fiscalPrinterDriver.checkCurrentTicketTotal(printerTotal, arg0) == false )
			{
				System.out.println ("FISCAL PRINTER TOTAL TICKET UNMATCHED");
				throw new JposException(9998);
			}
			
			if (PrinterType.isCUSTOMK2Model())
			{
				// questa printer non vuole il printRecTotal se non è stato venduto niente
				// (ad esempio la sequenza passaggio tessera più subtotale), l'accetterebbe
				// invece su uno scontrino a zero con vendite/annulli/storni
				double tot = Double.valueOf(printerTotal).doubleValue()/100;
				System.out.println("CUSTOMK2 tot="+tot);
				if (tot == 0.)
					return;
			}
			
	        if (SRTPrinterExtension.isPRT())
	        	LotteryCommands.SendLotteryCode(SharedPrinterFields.Lotteria.getLotteryCode());
	        
	        progress_cash = true;
	        progress_eft = true;
	        progress_amount = 0;
		}
		
		if (SRTPrinterExtension.isSRT() && SRTPrinterExtension.isNotLikeNonFiscalMode())
		{
			if (SRTPrinterExtension.isTenderMerge()){
		        DummyServerRT.srtRecTotal tendermerge = new DummyServerRT.srtRecTotal(arg2, arg1);
			}
			else{
				System.out.println ( "MAPOTO before driver.printRecTotal");
		        if (PrinterType.isNCRFiscalModel() || PrinterType.isRCHPrintFModel()){
		        	fiscalPrinterDriver.printRecTotal(arg0 / 100, arg1 / 100, arg2);
		        }
		        else
		        	fiscalPrinterDriver.printRecTotal(arg0, arg1, arg2);
				System.out.println ( "MAPOTO after driver.printRecTotal");
			}
		}
		else
		{
			if (SRTPrinterExtension.isPRT()) {
		        DummyServerRT.srtRecTotal tendermerge = null;
		        if (PrinterType.isRCHPrintFModel()) {
		        	String prefix = arg2;
					if (arg2.indexOf(",") >= 0)
						prefix = arg2.substring(0, arg2.indexOf(","));
					if (prefix == "" || prefix.length() == 0) {
						// il mop che si sta usando non è caricato nei 30 mop sulla printer, in questo caso lo consideriamo Contanti come fa la printer 
						prefix = ""+SharedPrinterFields.INDICE_CONTANTI;
					}
			        tendermerge = new DummyServerRT.srtRecTotal(LoadMops.getSrtDescription(LoadMops.getRCHDescriptionPayment(Integer.parseInt(prefix))), arg1);
		        }
		        else {
			        tendermerge = new DummyServerRT.srtRecTotal(LoadMops.getSrtDescription(arg2), arg1);
		        }
			}
			
			System.out.println ( "MAPOTO before driver.printRecTotal - arg0="+arg0+" arg1="+arg1+" arg2="+arg2);
	        if (PrinterType.isNCRFiscalModel() || PrinterType.isRCHPrintFModel()){
	        	fiscalPrinterDriver.printRecTotal(arg0 / 100, arg1 / 100, arg2);
	        }
	        else{
	        	// PROVVISORIO
	        	if (PrinterType.isEpsonModel() && SharedPrinterFields.isfwRT2enabled() && (arg0 == arg1)) {
	        		SharedPrinterFields.getChangeDescription();
	        		if (arg2.equalsIgnoreCase(SharedPrinterFields.ChangeCurrency)) {
	        			arg0 = 0;
						progress_amount+=arg1;
	        			arg1 = 0;
	        		}
	        	}
	        	// PROVVISORIO
	        	
	        	if (PrinterType.isEpsonModel() && SharedPrinterFields.isfwRT2enabled()) {
					if (arg2.length() > 0) {
						int prefix = Integer.parseInt(LoadMops.getPrefixPayment(arg2));
						if ((prefix / 100) == LoadMops.TICKETWN_TYPE) {
							int howmany = prefix % 100;
							if (howmany > 1) {
								long singleamount = arg1 / howmany;
								long totalamount = singleamount * howmany;
								System.out.println ( "MAPOTO before driver.printRecTotal - singleamount="+singleamount+" howmany="+howmany+" totalamount="+totalamount);
								if (totalamount == arg1) {
									// singleamount ok
									arg1 = singleamount;	// questo falserà il valore di progress_amount ma me ne frego tanto progress_eft sarà false
								}
								else {
									// singleamount ko, problemi di arrotondamento ?
									System.out.println ( "MAPOTO before driver.printRecTotal - WARNING B.PASTO - "+"MAYBE ROUNDING PROBLEMS");
									String s = arg2.replaceFirst(""+prefix, LoadMops.TICKETWN_TYPE+"01");
									arg2 = s;
								}
								System.out.println ( "MAPOTO before driver.printRecTotal - arg0="+arg0+" arg1="+arg1+" arg2="+arg2);
							}
						}
					}
	        	}
	        	
	        	fiscalPrinterDriver.printRecTotal(arg0, arg1, arg2);
	        }
			System.out.println ( "MAPOTO after driver.printRecTotal");
			
			if (enabledLowerRoundedPay == -1) {
				if (PrinterType.isRCHPrintFModel() && SharedPrinterFields.isfwRT2enabled()) {
					progress_amount+=arg1;
					if (arg2.indexOf(",") >= 0)
						arg2 = arg2.substring(0, arg2.indexOf(","));
					System.out.println ( "MAPOTO after driver.printRecTotal - arg2="+arg2);
					if (arg2.length() > 0) {
						if (Integer.parseInt(arg2) != SharedPrinterFields.INDICE_CONTANTI)
							progress_cash = false;
					}
					else
						progress_cash = false;
					System.out.println ( "MAPOTO after driver.printRecTotal - progress_amount="+progress_amount+" progress_cash="+progress_cash);
					long stilltopay = arg0-progress_amount;
					System.out.println ( "MAPOTO after driver.printRecTotal - stilltopay="+stilltopay+" getCURRENTROUNDING="+RTConsts.getCURRENTROUNDING()+" onlycash="+progress_cash);
					if ((stilltopay > 0) && (stilltopay < 300)) {
						if ((RTConsts.getCURRENTROUNDING() == RTConsts.FULLROUNDING) || (RTConsts.getCURRENTROUNDING() == RTConsts.NEGATIVEROUNDING)) {
							if (progress_cash == true) {
								System.out.println ( "MAPOTO before driver.printRecTotal("+arg0/100+","+stilltopay/100+","+SharedPrinterFields.INDICE_SCONTOAPAGARE+")");
					        	fiscalPrinterDriver.printRecTotal(arg0/100, stilltopay/100, ""+SharedPrinterFields.INDICE_SCONTOAPAGARE);
								System.out.println ( "MAPOTO after driver.printRecTotal("+arg0/100+","+stilltopay/100+","+SharedPrinterFields.INDICE_SCONTOAPAGARE+")");
							}
						}
					}
				}
			}
			if (PrinterType.isEpsonModel() && SharedPrinterFields.isfwRT2enabled()) {
				progress_amount+=arg1;
				if (arg2.length() > 0) {
					if (!arg2.equalsIgnoreCase(SharedPrinterFields.ChangeCurrency))
						progress_cash = false;
					if (!((Integer.parseInt(LoadMops.getPrefixPayment(arg2)) / 100) == RTConsts.PAGELETT))
						progress_eft = false;
				}
				else {
					progress_cash = false;
					progress_eft = false;
				}
				System.out.println ( "MAPOTO after driver.printRecTotal - progress_amount="+progress_amount+" progress_cash="+progress_cash+" progress_eft="+progress_eft);
				
				if (progress_amount >= arg0) {
					if ((!RTTxnType.isRefundTrx()) && (!RTTxnType.isVoidTrx())) {
						if (fiscalPrinterDriver.isfwILotteryenabled()) {
							if (progress_eft == true && progress_amount >= (1 * 10000)) {
								if (SharedPrinterFields.Lotteria.getLotteryCode() == null || SharedPrinterFields.Lotteria.getLotteryCode() == "" || SharedPrinterFields.Lotteria.getLotteryCode().length() == 0) {
									if (!isCFPIvaFlag()) {
										SharedPrinterFields.Lotteria.setILotteryCode("AAAAAAAA");
									}
								}
							}
						}
					}
				}
			}
		}
		
		if (isFiscalAndSRTModel())
		{
			HardTotals.doPrintRecTotal(arg1);
			
			if (!SRTPrinterExtension.isTenderMerge()){
				String out = buildItem ( arg2, arg1 );
				scriviLastTicket(out);
			}
			
			//if (HardTotals.TotalePagato.getLongX100() == 0)
			if (HardTotals.TotalePagato.getLongX100() >= HardTotals.Totale.getLongX100())
			{
				if (SRTPrinterExtension.isTenderMerge()){
					for (int i=0; i<DummyServerRT.groups.size(); i++){
						System.out.println ( "MAPOTO before driver.printRecTotal");
				        if (PrinterType.isNCRFiscalModel() || PrinterType.isRCHPrintFModel()){
				        	fiscalPrinterDriver.printRecTotal(arg0 / 100, ((DummyServerRT.srtRecTotal)DummyServerRT.groups.get(i)).getAmount() / 100, ((DummyServerRT.srtRecTotal)DummyServerRT.groups.get(i)).getDescr());
				        }
				        else
				        	fiscalPrinterDriver.printRecTotal(arg0, ((DummyServerRT.srtRecTotal)DummyServerRT.groups.get(i)).getAmount(), ((DummyServerRT.srtRecTotal)DummyServerRT.groups.get(i)).getDescr());
						System.out.println ( "MAPOTO after driver.printRecTotal");
						
						String out = buildItem ( ((DummyServerRT.srtRecTotal)DummyServerRT.groups.get(i)).getDescr(), ((DummyServerRT.srtRecTotal)DummyServerRT.groups.get(i)).getAmount() );
						scriviLastTicket(out);
					}
				}
				
				String out = buildChange ( "", (HardTotals.TotalePagato.getLongX100()-HardTotals.Totale.getLongX100()) );
				scriviLastTicket(out);
				
				out = buildTotalIva ( "", RoungickTax.getTotalTaxLong() );
				printNormal_ejoff(jpos.FiscalPrinterConst.FPTR_S_RECEIPT, out);
				printNormal_ejoff(jpos.FiscalPrinterConst.FPTR_S_RECEIPT, " ");
				
				out = buildTotalAmount ( "", HardTotals.Totale.getLongX100() );
				printNormal_ejoff(jpos.FiscalPrinterConst.FPTR_S_RECEIPT, out);
				out = buildAmountPaid ( "", HardTotals.Totale.getLongX100()-totaleNonRiscosso );
				printNormal_ejoff(jpos.FiscalPrinterConst.FPTR_S_RECEIPT, out);
				printNormal_ejoff(jpos.FiscalPrinterConst.FPTR_S_RECEIPT, " ");
			}
		}
		
		if (SRTPrinterExtension.isPRT())
		{
			HardTotals.doPrintRecTotal(arg1);
			
			if (HardTotals.TotalePagato.getLongX100() >= HardTotals.Totale.getLongX100()+(long)(SharedPrinterFields.RoundingRT*10000))
			{
				try {
					// scrittura file lastticket
					
					String out = "";
					
					long resto = HardTotals.TotalePagato.getLongX100()-HardTotals.Totale.getLongX100();
					
					long rounding = 0;
					if (RTTxnType.isSaleTrx() && progress_cash)
						rounding = (long)(Rounding.roundingSimulation(((double)arg0/10000))*10000);
					
					if (rounding > 0) {		// a favore del pdv
						if (RTConsts.getCURRENTROUNDING() == RTConsts.NEGATIVEROUNDING)
							rounding = 0;
						resto-=rounding;
					}
					else if (rounding < 0) {	// a favore del cliente
						if (RTConsts.getCURRENTROUNDING() == RTConsts.POSITIVEROUNDING)
							rounding = 0;
						if (HardTotals.TotalePagato.getLongX100() > HardTotals.Totale.getLongX100())
							resto+=Math.abs(rounding);
					}
					
					if (PrinterType.isRCHPrintFModel()) {
						if ((resto == 0) && (rounding < 0))
							resto+=Math.abs(rounding);
					}
					
					if (RTTxnType.isSaleTrx())
					{
						if (resto > 0) {
							out = buildChange ( "", resto );
							scriviLastTicket(out);
						}
					}
					
					out = buildTotalIva ( "", RoungickTax.getTotalTaxLong() );
					scriviLastTicket(out);
					
					if (RTTxnType.isSaleTrx())
					{
						for (int i=0; i<DummyServerRT.groups.size(); i++) {
							if (((DummyServerRT.srtRecTotal)DummyServerRT.groups.get(i)).getAmount() == HardTotals.Totale.getLongX100()) {
								if (PrinterType.isRCHPrintFModel() && rounding < 0) {
									out = buildItem ( ((DummyServerRT.srtRecTotal)DummyServerRT.groups.get(i)).getDescr(), ((DummyServerRT.srtRecTotal)DummyServerRT.groups.get(i)).getAmount() );
								}
								else {
									out = buildItem ( ((DummyServerRT.srtRecTotal)DummyServerRT.groups.get(i)).getDescr(), ((DummyServerRT.srtRecTotal)DummyServerRT.groups.get(i)).getAmount()+rounding );
								}
							}
							else {
								out = buildItem ( ((DummyServerRT.srtRecTotal)DummyServerRT.groups.get(i)).getDescr(), ((DummyServerRT.srtRecTotal)DummyServerRT.groups.get(i)).getAmount() );
							}
							scriviLastTicket(out);
						}
						
						if (rounding < 0) {
							out = buildItem(LastTicket.getDisconpay(), Math.abs(rounding));
							scriviLastTicket(out);
						}
						
						//out = buildTotalAmount ( "", HardTotals.Totale.getLongX100() );
						//scriviLastTicket(out);
						
						long importopagato = HardTotals.Totale.getLongX100()-totaleNonRiscosso;
						if (rounding > 0)
							importopagato+=rounding;
						else if (rounding < 0)
							importopagato+=rounding;
						out = buildAmountPaid ( "", importopagato );
						scriviLastTicket(out);
					}
					
					LocalTimestamp lts = TransactionSale.getEndData();
					String date = Sprint.f("%02d-%02d-%d %02d:%02d",new Integer(lts.getDay()),new Integer(lts.getMonth()+1),new Integer(lts.getYear()+1900),new Integer(lts.getHour()),new Integer(lts.getMinute())); 
					out = SharedPrinterFields.ALINER.substring(0, (int)((RTConsts.setMAXLNGHOFLENGTH()-date.length())/2))+date;
					scriviLastTicket(out);
					
					out = LastTicket.getDocnum();
					scriviLastTicket(out);
					
					if ((SharedPrinterFields.Lotteria.getLotteryCode() != null) && (SharedPrinterFields.Lotteria.getLotteryCode().length() > 0)) {
						out = LastTicket.getLottery()+SharedPrinterFields.Lotteria.getLotteryCode();
						scriviLastTicket(out);
					}

					if (SharedPrinterFields.Lotteria.getILotteryCode().length() > 0) {
						out = LastTicket.getLottery()+SharedPrinterFields.Lotteria.getILotteryCode();
						printNormal(jpos.FiscalPrinterConst.FPTR_S_RECEIPT, out);
						EjCommands ej = new EjCommands();
						ej.printNormal(jpos.FiscalPrinterConst.FPTR_S_RECEIPT, out+R3define.CrLf);
					}
					
					out = SharedPrinterFields.ALINER.substring(0, (int)((RTConsts.setMAXLNGHOFLENGTH()-SharedPrinterFields.RTPrinterId.length())/2))+SharedPrinterFields.RTPrinterId;
					scriviLastTicket(out);
					
					if (RTTxnType.isSaleTrx())
					{
						out = SharedPrinterFields.ALINER.substring(0, (int)((RTConsts.setMAXLNGHOFLENGTH()-LastTicket.getPaymdetails().length())/2))+LastTicket.getPaymdetails();
						scriviLastTicket(out);
						
						for  ( int i = 0; i < SharedPrinterFields.a.size() ; i++ )
						{
							MethodE M = (MethodE)SharedPrinterFields.a.get(i);
							if (M.getM() == R3define.fprintRecTotal){
			                   if (SharedPrinterFields.lineePagamento != null) {
			                        for (Iterator iterator = SharedPrinterFields.lineePagamento.keySet().iterator(); iterator.hasNext();) {
			                           String k = (String) iterator.next();
			                           //Double valDouble = new Double(((ICurrency) SharedPrinterFields.lineePagamento.get(k)).doubleValue() * 10000);	// prima era così
			                           BigDecimal bd = new BigDecimal((Double)SharedPrinterFields.lineePagamento.get(k));
			                           bd = bd.setScale(2, RoundingMode.HALF_DOWN);
			                           Double valDouble = new Double(bd.doubleValue() * 10000);
			                           long val =  valDouble.longValue();                                        
			                           if (val == HardTotals.Totale.getLongX100())
			                        	   out = buildItem ( k, val+rounding);
			                           else
			                        	   out = buildItem ( k, val);
			                           scriviLastTicket(out);
			                        }
			                        SharedPrinterFields.lineePagamento = null;
			                    }else {                        
			                    	if (Long.parseLong(M.getV().get(1).toString()) == HardTotals.Totale.getLongX100()) {
										if (PrinterType.isRCHPrintFModel() && rounding < 0)
				                    		out = buildItem ( M.getV().get(2).toString().substring(3), Long.parseLong(M.getV().get(1).toString()) /*- resto*/ );
										else
											out = buildItem ( M.getV().get(2).toString().substring(3), Long.parseLong(M.getV().get(1).toString())+rounding /*- resto*/ );
			                    	}
			                    	else {
			                    		out = buildItem ( M.getV().get(2).toString().substring(3), Long.parseLong(M.getV().get(1).toString()) /*- resto*/ );
			                    	}
									scriviLastTicket(out);
			                    }
							}
						}
						
						if (rounding != 0.0) {
							long val =  rounding;
							if (val > 0) {
								out = buildItem (LastTicket.getRoundingup(), val);
								if (PrinterType.isRCHPrintFModel())
									out = buildItem (LastTicket.getRoundingup1(), val);
							}
							else {
								out = buildItem (LastTicket.getRoundingdown(), Math.abs(val));
								if (PrinterType.isRCHPrintFModel())
									out = buildItem (LastTicket.getRoundingdown1(), Math.abs(val));
							}
							scriviLastTicket(out);
						}
					}
					
				} catch (Exception e) {
					System.out.println("LastTicket error: "+e.getMessage());
				}
			}
		}
	}
	
	public void printRecVoidItem(String s, long l, int i, int j, long l1, int kIvaPolipos) throws JposException {
		System.out.println ( "MAPOTO-EXEC PRINT VOID ITEM s="+s );
		System.out.println ( "MAPOTO-EXEC PRINT VOID ITEM l="+l);
		System.out.println ( "MAPOTO-EXEC PRINT VOID ITEM i="+i );
		System.out.println ( "MAPOTO-EXEC PRINT VOID ITEM j="+j );
		System.out.println ( "MAPOTO-EXEC PRINT VOID ITEM l1="+l1);
		System.out.println ( "MAPOTO-EXEC PRINT VOID ITEM k="+kIvaPolipos );
		
		if (SRTPrinterExtension.isSRT())
		{
			String ivadesc = DicoTaxLoad.getDTO(kIvaPolipos).getShortdescription();
			
			s = addIva(s, RTConsts.getMAXITEMDESCRLENGTH()-1, ivadesc);
		}
		
		if (SRTPrinterExtension.isPRT() && TaxData.isOmaggio(kIvaPolipos) && SharedPrinterFields.isfwRT2enabled())
		{
			printRecOmaggio(s, l, i, j, l1, kIvaPolipos);
			return;
		}
		
		int k = DicoTaxToPrinter.getFromPoliposToPrinter(kIvaPolipos);
		if ((DicoTaxLoad.isIvaAllaPrinter()) && (k == SharedPrinterFields.VAT_N4_Index))
			k = Integer.parseInt(SharedPrinterFields.VAT_N4_Dept);
		
		System.out.println ( "MAPOTO-EXEC PRINT VOID ITEM k="+k );
		
		String m = "CORREZIONE STORNO";
		
		System.out.println ( "MAPOTO-EXEC PRINT VOID ITEM "+s );
		
		if (isFlagVoidRefund(l, l1))
		{
			m = "CORREZIONE RESO";
			if (i > 0)
				i=i*(-1);
		}
		
		if ( PrinterType.isEpsonModel() )
		{
	        System.out.println("printRecVoidItem in - s=" + s + " l=" + l + " i=" + i + " j="
	                + j + " l1=" + l1 + " k=" + k);
	        String s1 = s.length() > 17 ? s.substring(0, 17) : s;
	        
	        if (SRTPrinterExtension.isSRT()) s1 = s;
	        
	        if ((l == 0) && (i > 0))
	        	fiscalPrinterDriver.printRecVoidItem(s1, l1 * i / 1000, 1000, j, l, k);
	        else
	        	fiscalPrinterDriver.printRecVoidItem(s1, l * i / 1000, 1000, j, l1, k);
	        System.out.println("printRecVoidItem out");
		}
		else
		{
	      final int MAXLNGHOFDESCR = 17;
	      String new_description = s.replaceAll("(?i)TOTALE", "TOTA..");
	      s = new_description;
	      new_description = s.replaceAll("(?i)TOTAL", "TOTA.");
	      
			String k2desc="";
			for(int k2idx=0; k2idx<new_description.length(); k2idx++)
			{
		        if ((int)(new_description.charAt(k2idx)) >= 128)
		            k2desc = k2desc + ".";
		        else
		            k2desc = k2desc + new_description.charAt(k2idx);
			}
			new_description=k2desc;
				
			if (PrinterType.isDieboldRTOneModel())
			{
				s = new_description;
				new_description = s.replaceAll("\\)", "-");
				s = new_description;
				new_description = s.replaceAll("\\(", "-");
			}

			String newMsg = (new_description.length()<= MAXLNGHOFDESCR)? new_description: 
	                                                               new_description.substring (0, MAXLNGHOFDESCR);			      
		    if (SRTPrinterExtension.isSRT()) newMsg = new_description;
		      
	      if ( i < 0)
	      {
	    	  long a;
	    	  if ( PrinterType.isTH230Model() )
	    	  {
	    		  if (!isFlagVoidRefund(l, l1))
	    			  a = l1 * i / -1000;
	    		  else
	    			  a = l1;
		    	  fiscalPrinterDriver.printRecItem(m, a, 1, k, 0, "");
	    	  }
	    	  else
	    	  {
	    		  if (!isFlagVoidRefund(l, l1))
	    			  a = l1 * i / -1000;
	    		  else
	    			  a = l1;
				  if (PrinterType.isNCRFiscalModel() || PrinterType.isRCHPrintFModel())
					  fiscalPrinterDriver.printRecItem(m, a / 100, 0, k, 0, "");
				  else
					  fiscalPrinterDriver.printRecItem(m, a, 0, k, 0, "");
	    	  }
	    	  setFlagVoidRefund(false);
	    	  return;
	      }
	      if ( l ==  0 )
	    	  pleaseVoidItem (newMsg, l1, i, j, l, k);
	      else
	    	  pleaseVoidItem (newMsg, l, i, j, l1, k); 
	    }
		
		setFlagVoidRefund(false);
		
		if (isFiscalAndSRTModel() || SRTPrinterExtension.isPRT())
		{
			long chi = ( l ==  0 ) ? l1 : l;
		    int quanti = i / 1000;
  		  	if (!isFlagVoidRefund(l, l1))
  		  		chi = chi * quanti;
  		  	else
  		  		chi=chi*(-1);
			if (i < 0)
			{
				chi=chi*(-1);
				return;
			}
			HardTotals.doPrintRecVoid(chi);
			
	    	scriviLastTicket(buildItemVoid ( s, chi ));
		}
	}

	private void pleaseVoidItem (String s, long l, int i, int j, long l1, int k) throws JposException
	{
		System.out.println ( "MAPOTO-EXEC PLEASEVOIDITEM"+s );
		  
		if ( PrinterType.getPrinterJavaPosModel() == PrinterType.JAVAPOS113 )
		{
			if ( PrinterType.isTH230Model()){
	      		((jpos.FiscalPrinterControl113)fiscalPrinterDriver).printRecItemVoid(s, l * i / 1000, i, k, l , "Pz");
			}else{
				System.out.println ( "JavaPos 1.13 - Printer != TH230: unimplemented version" );
			}
		}
		else
		{
			if ( PrinterType.isTH230Model()){
		     	fiscalPrinterDriver.printRecVoidItem (s, l * i / 1000, i, j, l1, k);
			}
			else{
			    if (PrinterType.isNCRFiscalModel())
			    	fiscalPrinterDriver.printRecVoidItem (s, (l / 100) * i / 1000, i, j, l1, k);
			    else if (PrinterType.isRCHPrintFModel())
			    {
			    	if (l > 0){
			    		//fiscalPrinterDriver.printRecVoidItem (s, l / 100, i / 1000, j, l1, k);
			    		long prezzo = (l / 100) * (i / 1000);
			    		int qta = 1;
			    		fiscalPrinterDriver.printRecVoidItem (s, prezzo, qta, j, l1, k);
			    	}
			    	else
			    		printNormal(jpos.FiscalPrinterConst.FPTR_S_RECEIPT, "Storno "+s);
			    }
			    else
			    	fiscalPrinterDriver.printRecVoidItem (s, l, i, j, l1, k);
			}
		}
	}
	
	public void printReport(int id, String ini,String end) throws JposException {
		printReportInHouse(id,ini,end);
	}

	private void printReportInHouse(int reportType, String startNum, String endNum)
	{
		System.out.println ( "MAPOTO-printReportInHouse in" );
		
		if (!(SRTPrinterExtension.isSRT() && reportType == Report.PRINTSOMERECEIPTBYDATE)) {
			Report.printReportInHouse(reportType, startNum, endNum);
			return;
		}
			
  		if (reportType == Report.PRINTFISCALMEMBYDATE)
  			reportType = jpos.FiscalPrinterConst.FPTR_RT_DATE;
  
		switch (reportType)
		{
			case Report.PRINTSOMERECEIPTBYDATE:
				r3PrintSomeReceiptByDate (startNum, endNum);
				break;

			case Report.PRINTALLRECEIPTBYDATE:
			case Report.DOWNLOADONFILE:
			case jpos.FiscalPrinterConst.FPTR_RT_ORDINAL:
			case jpos.FiscalPrinterConst.FPTR_RT_DATE:
			case Report.PRINTTOTFISCALMEMBYDATE:
			case Report.INITJOURNAL:
				MessageBox.showMessage("Funzione non valida", null, MessageBox.OK);
				break;
			
			default:
				break;
		}
		
		System.out.println ( "MAPOTO-printReportInHouse out" );
		return;
	}
	
	private void r3PrintSomeReceiptByDate (String startNum, String endNum)
	{
		//startNum = "ggmmaaaaNNNN";
		//endNum   = "ggmmaaaaNNNN";
		
		System.out.println ( "r3PrintSomeReceiptByDate in: "+startNum+"-"+endNum );
		
		Files.copyFile(SharedPrinterFields.lastticket,lastticketsaved);
		
		rtsTrxBuilder.storerecallticket.RecallTicket RT = new rtsTrxBuilder.storerecallticket.RecallTicket(startNum, endNum);
		
		leggiFile();
		
		Files.moveFile(lastticketsaved,SharedPrinterFields.lastticket);
		
		System.out.println ( "r3PrintSomeReceiptByDate out" );
	}
	
	public void printXReport() throws JposException {
		fiscalPrinterDriver.printXReport();
	}

	public void printZReport() throws JposException {
		System.out.println ( "MAPOTO-EXEC PRINTREPORTZ" );
		
		if (SRTPrinterExtension.isRTActivated() == true) {
			DummyServerRT.setSRTTillID();
		}
		
  	    if (SRTPrinterExtension.isSRT())
  	    {
  	    	DummyServerRT.pleaseDoDailyClosure();
			
			System.out.println("printZReport - CurrentFiscalClosure = "+DummyServerRT.CurrentFiscalClosure);
			System.out.println("printZReport - CurrentReceiptNumber = "+DummyServerRT.CurrentReceiptNumber);
			System.out.println("printZReport - CurrentDailyAmount = "+DummyServerRT.CurrentDailyAmount);
			int OldCurrentFiscalClosure = DummyServerRT.CurrentFiscalClosure;
			int retry = 0;
			while (true)
			{
				retry++;
				
				DummyServerRT.pleaseDoServerInfo();
				
				if (DummyServerRT.CurrentFiscalClosure > OldCurrentFiscalClosure)
					break;
				
				System.out.println("printZReport - OldCurrentFiscalClosure = " + OldCurrentFiscalClosure + " - CurrentFiscalClosure = " + DummyServerRT.CurrentFiscalClosure + " - retry = " + retry);
				
				if (retry == 5)
					break;
				
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
				}
			}
			if (DummyServerRT.CurrentFiscalClosure <= OldCurrentFiscalClosure){
				System.out.println("printZReport - OldCurrentFiscalClosure = " + OldCurrentFiscalClosure + " - CurrentFiscalClosure = " + DummyServerRT.CurrentFiscalClosure + " - retry = " + retry);
				DummyServerRT.CurrentFiscalClosure++;
				DummyServerRT.CurrentReceiptNumber = "1";
				DummyServerRT.CurrentDailyAmount = "000";
			}
			System.out.println("printZReport - CurrentFiscalClosure = "+DummyServerRT.CurrentFiscalClosure);
			System.out.println("printZReport - CurrentReceiptNumber = "+DummyServerRT.CurrentReceiptNumber);
			System.out.println("printZReport - CurrentDailyAmount = "+DummyServerRT.CurrentDailyAmount);
  	    }
  	    
  	    setFlagVoidRefund(false);
  	    
  	    if ( (!PrinterType.isEpsonModel()) && (!PrinterType.isRCHPrintFModel()) )
  	    {
  	    	if (!fiscalPrinterDriver.getDayOpened())
  	    	{
  	    		System.out.println("printZReport - Fiscal day not opened: don't do it.");
  	    		return;
  	    	}
  	    }
  	    fiscalPrinterDriver.printZReport();
  	    
		if (isFiscalAndSRTModel() || SRTPrinterExtension.isPRT())
		{
			HardTotals.ProAzz.add(1);
			HardTotals.delReportZ();
		}
	}

	public void setDate(String arg0) throws JposException {
       System.out.println("setDate in - s=" + arg0);
	   if ( ! PrinterType.isEpsonModel() )
	   {
			if (fiscalPrinterDriver.getDayOpened())
			{
				System.out.println("setDate - Fiscal day opened: don't do it.");
				return;
			}
	   }
	   String s1 = arg0.substring(8, 10) + arg0.substring(5, 7) + arg0.substring(0, 4)
	                + arg0.substring(11, 13) + arg0.substring(14, 16);
	   fiscalPrinterDriver.setDate(s1);
	   System.out.println("setDate out");
	}

	public void setHeaderLine(int arg0, String arg1, boolean arg2) throws JposException {
		if ( ! PrinterType.isEpsonModel() )
		{
		  	  if (fiscalPrinterDriver.getDayOpened())
		  	  {
		  		  System.out.println("setHeaderLine - Fiscal day opened: don't do it.");
		  		  return;
		  	  }
			  if ((PrinterType.isTH230Model() || PrinterType.isRCHPrintFModel()) && (arg0 == 1))
			  {
				  SetLogo(SharedPrinterFields.LOGO_FILE, SharedPrinterFields.LOGO_NUMBER);
			  }
		}
		int maxHeaderLine = fiscalPrinterDriver.getNumHeaderLines();
		if(arg0 > maxHeaderLine) 
			return;

		String s = arg1;
		arg1 = cleanLine(s);
		   
		if (PrinterType.isRCHPrintFModel())
			fiscalPrinterDriver.setHeaderLine(arg0-1, (arg1.length()>36 ? arg1.substring(0, 36) : arg1), arg2);
		else
		{
			   if (PrinterType.isTH230Model())
			   {
				   arg1 = align_center(arg1, 44);	// 44 = da specifiche
			   }
			   if (PrinterType.isTH230Model() && (arg0 == 1))
			   {
				   String UseBitmapInSlot = ""+(char)0x1B + (char)0x7C + (byte)SharedPrinterFields.LOGO_NUMBER + (char)0x42;
				   arg1 = UseBitmapInSlot+arg1;
				   TakeYourTime.takeYourTime(600);
			   }
			   fiscalPrinterDriver.setHeaderLine(arg0, arg1, arg2);
		}
	}

	public void setTrailerLine(int arg0, String arg1, boolean arg2) throws JposException {
		String s = arg1;
		arg1 = cleanLine(s);
		   
		fiscalPrinterDriver.setTrailerLine(arg0,arg1,arg2);
	}
	
	public void setAdditionalHeader(String value)
	{
		try
		{
			fiscalPrinterDriver.setAdditionalHeader(value);
		}
		catch (Exception e)
		{
		}
	}
	   
	   public void setAdditionalTrailer(String text) throws JposException
	   {
		   /* TEMPORANEO */
		   if ((!PrinterType.isTH230Model()) && (!PrinterType.isRCHPrintFModel()))
			   return;
		   /* TEMPORANEO */
			   
		   if (PrinterType.isTH230Model() || PrinterType.isRCHPrintFModel())
		   {
			   if (fiscalPrinterDriver.getDayOpened())
				   System.out.println("setAdditionalTrailer - Fiscal day opened: don't do it.");
			   else
				   SetLogo(SharedPrinterFields.TRAILER_LOGO_FILE, SharedPrinterFields.TRAILER_LOGO_NUMBER);
		   }
			   
		   try
		   {
			   if (PrinterType.isTH230Model())
			   {
				   String UseBitmapInSlot = ""+(char)0x1B + (char)0x7C + (byte)SharedPrinterFields.TRAILER_LOGO_NUMBER + (char)0x42;
				   text = UseBitmapInSlot+text;
			   }
			   fiscalPrinterDriver.setAdditionalTrailer(text);
		   }
		   catch ( JposException nsme )
		   {
			   if ( nsme.getErrorCode() == 104 && nsme.getErrorCodeExtended() == 0 )
			   {
				   System.out.println ( "Versione di JavaPOS non supporta il metodo");
			   }
			   else
			   {
				   throw nsme;
			   }
		   }
	   }
	   
	public static int getState() throws JposException
	{
         if (!PrinterType.isCustomModel())	// per non rallentare troppo
        	 System.out.println("MAPOTO-FISICAL_PRINTER-STATE " + getFiscalPrinterState());
		 else
			 System.out.println("MAPOTO-FISICAL_PRINTER-STATE");
		 return getFiscalPrinterState();
	}
	
	public boolean getCoverOpen()
	{
		boolean retv = false;
		try
		{
			retv = fiscalPrinterDriver.getCoverOpen();
		}
		catch (jpos.JposException e)
		{
			System.out.println("getCoverOpen - exception:"+e.getMessage());
		}
		return retv;
	}
	
	public boolean getJrnEmpty()
	{
		boolean retv = false;
		try
		{
			retv = fiscalPrinterDriver.getJrnEmpty();
		}
		catch (jpos.JposException e)
		{
			System.out.println("getJrnEmpty - exception:"+e.getMessage());
		}
		return retv;
	}
	
	public boolean getJrnNearEnd()
	{
		boolean retv = false;
		try
		{
			retv = fiscalPrinterDriver.getJrnNearEnd();
		}
		catch (jpos.JposException e)
		{
			System.out.println("getJrnNearEnd - exception:"+e.getMessage());
		}
		return retv;
	}
	
	public boolean getRecEmpty()
	{
		boolean retv = false;
		try
		{
			retv = fiscalPrinterDriver.getRecEmpty();
		}
		catch (jpos.JposException e)
		{
			System.out.println("getRecEmpty - exception:"+e.getMessage());
		}
		return retv;
	}
	
	public boolean getRecNearEnd()
	{
		boolean retv = false;
		try
		{
			retv = fiscalPrinterDriver.getRecNearEnd();
		}
		catch (jpos.JposException e)
		{
			System.out.println("getRecNearEnd - exception:"+e.getMessage());
		}
		return retv;
	}
	
	public static void getDate(String[] arg0) throws JposException {
		try
		{
			fiscalPrinterDriver.getDate(arg0);
		}
		catch (jpos.JposException e)
		{
			System.out.println("getDate - exception:"+e.getMessage());
		}
	}
	
	public void getData(int i, int ai[], String as[])
	{
		fiscalPrinterDriver.getData(i, ai, as);
	}
	
    /* printer commands - End
     * 
     */
	
    protected String buildItem ( String desc, long price )
    {
        int MAXLNGHOFLENGTH = RTConsts.setMAXLNGHOFLENGTH();
    	return ( buildbuild(MAXLNGHOFLENGTH,desc,price ));
    }
    
    protected String buildItemVoid ( String de, long price )
    {
        int MAXLNGHOFLENGTH = RTConsts.setMAXLNGHOFLENGTH();
        String desc = de;
    	String lo =  ( buildbuild(MAXLNGHOFLENGTH,desc,price ));
    	return ( "Storno\n"+lo+"-" );
    }
    
    protected String buildItemRefund ( String de, long price )
    {
        int MAXLNGHOFLENGTH = RTConsts.setMAXLNGHOFLENGTH();
        String desc = de;
    	String lo =  ( buildbuild(MAXLNGHOFLENGTH,desc,price ));
    	return ( "Reso\n"+lo+"-" );
    }
    
   	protected String buildbuild ( int mx, String desc, long price )
    {
   		System.out.println("LONGPRICE:"+price);
    	if ( PrinterType.isTMT88model() )	//epson non fiscale accorcia lunghezza stampa riga normale
    	{
    		mx = mx - 2;
    	}
        int MAXLNGHOFDESCR = 28;
        String newMsg = (desc.length()<= MAXLNGHOFDESCR)? desc: desc.substring (0, MAXLNGHOFDESCR);

        String p = (price == 0 ) ? "000":""+price;
        while ( p.length() < 3 )
        	p = p + "0";
        p = p.substring(0,p.length()-2);
        if ( p.length() < 2 )
        	p = "0"+p;
        if ( p.length() < 3 )
        	p = "0."+p;
        else
        	p = p.substring ( 0,p.length()-2 ) + "." + p.substring ( p.length()-2 );
    	return ( newMsg + SharedPrinterFields.ALINER.substring(0,mx-newMsg.length()-p.length())+ p);
    }
   	
    protected String buildTotal ( String de, long price )
    {
        int MAXLNGHOFLENGTH = RTConsts.setMAXLNGHOFLENGTH();
        String desc = "TOTALE COMPLESSIVO "+de;
    	return ( buildbuild(MAXLNGHOFLENGTH,desc,price ));
    }
    
    protected String buildChange ( String de, long price )
    {
        int MAXLNGHOFLENGTH = RTConsts.setMAXLNGHOFLENGTH();
        String desc = "Resto "+de;
    	return ( buildbuild(MAXLNGHOFLENGTH,desc,price ));
    }
    
    protected String buildItemAdjustment ( String de, long price )
    {
        int MAXLNGHOFLENGTH = RTConsts.setMAXLNGHOFLENGTH();
        String desc = "Sconto "+de;
    	String lo =  ( buildbuild(MAXLNGHOFLENGTH,desc,price ));
    	return ( lo+"-" );
    }
    
    public String buildSubtotalAdjustment ( String de, long price )
    {
        int MAXLNGHOFLENGTH = RTConsts.setMAXLNGHOFLENGTH();
        String desc = "Sconto "+de;
    	String lo =  ( buildbuild(MAXLNGHOFLENGTH,desc,price ));
    	return ( lo+"-" );
    }
    
    protected String buildTotalIva ( String de, long price )
    {
    	if (RTTxnType.isVoidTrx(RTTxnType.getTypeTrx())){
    		price = (long)Math.rint((Math.rint((DummyServerRT.getOld_receiptVAT())*100)/100)*10000);
    	}
        int MAXLNGHOFLENGTH = RTConsts.setMAXLNGHOFLENGTH();
        String desc = RTConsts.DICUIIVA+de;
        String out = buildbuild(MAXLNGHOFLENGTH,desc,price );
        EjCommands.Write(out, true);
    	return ( out );
    }
    
    protected String buildTotalAmount ( String de, long price )
    {
        int MAXLNGHOFLENGTH = RTConsts.setMAXLNGHOFLENGTH();
        String desc = RTConsts.IMPORTOTOTALE+de;
        String out = buildbuild(MAXLNGHOFLENGTH,desc,price );
        EjCommands.Write(out, true);
    	return ( out );
    }
    
    protected String buildAmountPaid ( String de, long price )
    {
        int MAXLNGHOFLENGTH = RTConsts.setMAXLNGHOFLENGTH();
        String desc = RTConsts.IMPORTOPAGATO+de;
        String out = buildbuild(MAXLNGHOFLENGTH,desc,price );
        EjCommands.Write(out, true);
    	return ( out );
    }
    
	public static String buildItemPlus ( String discount, String ivadesc )
	{
		int a = discount.lastIndexOf('-');
		String amt = discount.substring(a);
		
		String s = discount.substring(0, a);
		for (a=0; a<s.length(); a++){
			if (s.charAt(a) != ' ')
				break;
		}
		s = s.substring(a);
		for (a=s.length()-1; a>=0; a--){
			if (s.charAt(a) != ' ')
				break;
		}
		String des = s.substring(0, a+1);
		
		return(des + SharedPrinterFields.ALINER.substring(0,RTConsts.getMAXITEMDESCRLENGTH()-des.length()-ivadesc.length()-amt.length()-1)+ amt);
	}
    
    protected String buildSRTSubtotalAdjustment ( String de, long price )
    {
        int MAXLNGHOFLENGTH = RTConsts.setMAXLNGHOFDISCOUNT();
        String desc = "Sconto "+de;
    	String lo =  ( buildbuild(MAXLNGHOFLENGTH,desc,price ));
    	return ( lo+"-" );
    }
    
	public void resetAndClear() throws JposException {
		if (PrinterType.isDieboldRTOneModel()) {
			StringBuffer closestateprinter = new StringBuffer("=x");
			fiscalPrinterDriver.executeRTDirectIo(0, 0, closestateprinter);
		}
		fiscalPrinterDriver.resetPrinter();
		fiscalPrinterDriver.clearError();
	}
	
	public void Reset_I() throws JposException {
	}
	
	public void Reset_II() throws JposException {
		fiscalPrinterDriver.resetPrinter();
	}
	
	public void Reset_III(boolean doit) {
		if (!doit && PrinterType.isEpsonModel())
			return;
		
		while ( true )
		{
			try
			{					 
				System.out.println ( "MAPOTO-RESET AFTERCLEAR INIT <"+fiscalPrinterDriver.getState()+">");
				resetAndClear();
				if ((!PrinterType.isCustomModel()) && (!PrinterType.isRCHPrintFModel()))
					fiscalPrinterDriver.clearOutput();
				System.out.println ( "MAPOTO-RESET AFTERCLEAR END <"+fiscalPrinterDriver.getState()+">");
				break;
			}
			catch ( JposException e )
			{
				System.out.println ( "MAPOTO-RESETAFTERCLEAR <"+e.toString()+">");
				PleaseDisplay.pleaseDisplayWait( " VERIFICA STAMPANTE ", 400);
			}
		}
	}
	
	public void AnnullaResoRT_Posponed()
	{
		if (!SharedPrinterFields.isFlagsVoidTicket())
			MessageBox.showMessage(RESONONCORRETTO, null, MessageBox.OK);
		
		try {
			printRecVoid(OPERAZIONEANNULLATA);
			endFiscalReceipt(true);
		} catch (JposException e) {
			System.out.println("AnnullaResoRT_Posponed - Exception : " + e.getMessage());
		}
		
//		setTxnnumbertorefund(LineRefundSRT.getTxnnumbertorefund());	// ??? non dovrebbe più servire visto che setVoided() sulla transazione non si farà più da qui immagino
		
		if (!SharedPrinterFields.isFlagsVoidTicket())
			MessageBox.showMessage(OPERAZIONEANNULLATA, null, MessageBox.OK);
	}
	
	public static String currentFiscalTotal(int what) throws JposException
	{
			int [] arr_optArgs = new int[1];
			String [] arr_data = new String[1];
			arr_optArgs [0] = 1;
			arr_data    [0] = new String ();

			fiscalPrinterDriver.getData (what, arr_optArgs, arr_data);
	  	    if (what == jpos.FiscalPrinterConst.FPTR_GD_CURRENT_TOTAL)
	  	    	iconic.mytrade.gutenberg.jpos.printer.service.FiscalPrinterDataInformation.setNewDataAvailable(false);
	  	    System.out.println ( "RISPOSTA <"+what+"><"+arr_data[0]+">");
		    return ( arr_data[0] );
	}
	
	public static void cleanDailyTotal ( )
	{
		dailyTotal = (double)-1.0;	// distrugge il totale precedente
	}
	
	public static boolean niceDailyTotal ( )
	{
		return ((dailyTotal == (double)-1.0) ? false : true );
	}
	
	public static void storeDailyTotal ( String In )
	{
		dailyTotal = (Double.valueOf(In).doubleValue()/100);
		currentTicket = (double)0.0;
		if ( provaErrori )
			countErrori++;
	}
	
	public static boolean checkStoredDailyTotal(String Pr)
	{
		double	fromPrinter = Double.valueOf(Pr).doubleValue()/100;
		return ( fromPrinter == dailyTotal );
	}
	
	public static boolean timeToRecoveryTotal()
	{
		boolean res = false;
		try
		{
			String Total = currentFiscalTotal(jpos.FiscalPrinterConst.FPTR_GD_DAILY_TOTAL);
		    if ( fiscalPrinterDriver.checkCurrentDailyTotal(Total) == true )
		    	res = true;
		}
		catch ( Exception e )
		{
			System.out.println("MAPOTO-PrinterRecovery: getDailyTotal <"+e.toString()+">");
			res = false;
		}
		return ( res );
	}
	
	  public static int resumeErrorI()
	  {
		  try
		  {
			  if ((SharedPrinterFields.isInTicket() == false) || 
				((SharedPrinterFields.isInTicket() == true) && (getState() == jpos.FiscalPrinterConst.FPTR_PS_FISCAL_RECEIPT_ENDING)))
			  {
				  String Total = currentFiscalTotal(jpos.FiscalPrinterConst.FPTR_GD_DAILY_TOTAL);
				  if ( fiscalPrinterDriver.checkCurrentDailyTotal(Total) == true )
				  {
					  SharedPrinterFields.a = new ArrayList();
					  return (0);
				  }
			  }
		  }
		  catch (JposException e)
		  {
			  e.printStackTrace();
			  return (-1);
		  }
			  
		  return (1);
	  }
	   
		static String addIva(String desc, int max, String iva)
		{
			if (desc.length() > max)
				desc = desc.substring(0,max);
			
			for (int i=desc.length()+1; i<=max; i++)
				desc=desc+" ";
			
			desc=desc.substring(0,desc.length()-iva.length()-1)+" "+iva;
			
			return desc;
		}
		
		private void toDayDate() throws JposException 
		{
			String anno;
			String mese;
			String giorno;
			String ore;
			String min;
			String sec;
			
			GregorianCalendar gc = new GregorianCalendar();
			
			anno = gc.get(gc.YEAR) + "";
			mese = alligne100( gc.get(gc.MONTH) + 1 );
			giorno = alligne100( gc.get(gc.DATE) );
			
			ore = alligne100( (gc.get(gc.AM_PM) == gc.PM) ? gc.get(gc.HOUR)+12 : gc.get(gc.HOUR) );
			min = alligne100( gc.get(gc.MINUTE) );
			sec = alligne100( gc.get(gc.SECOND) );
			
			DummyServerRT.setCurrent_dateTime(giorno, mese, anno, ore, min, sec);
		}
		
		private void toDayDate(String num) throws JposException 
		{
			String anno;
			String mese;
			String giorno;
			String ore;
			String min;
			String sec;
			
			GregorianCalendar gc = new GregorianCalendar();
			
			anno = gc.get(gc.YEAR) + "";
			mese = alligne100( gc.get(gc.MONTH) + 1 );
			giorno = alligne100( gc.get(gc.DATE) );
			
			ore = alligne100( (gc.get(gc.AM_PM) == gc.PM) ? gc.get(gc.HOUR)+12 : gc.get(gc.HOUR) );
			min = alligne100( gc.get(gc.MINUTE) );
			sec = alligne100( gc.get(gc.SECOND) );
			
			String today = giorno + "/" + mese + "/" + anno + "      " + ore + ":" + min + ":" + sec;
			String fiscalnumber = "N."+Sprint.f("%04d", String.valueOf(Integer.parseInt(num)));
			String fiscalclosure = "RZ."+Sprint.f("%04d", String.valueOf(DummyServerRT.CurrentFiscalClosure));
			printNormal(jpos.FiscalPrinterConst.FPTR_S_RECEIPT, today + "  " + fiscalclosure + " " + fiscalnumber);
			DummyServerRT.setCurrent_dateTime(giorno, mese, anno, ore, min, sec);
		}
		
		private String alligne100( int who )
		{
			String res = Integer.toString(who + 100 );
			return ( res.substring(1) );
		}
		
		private void endTicket(String FiscalNum) throws JposException
		{
			if (SharedPrinterFields.isFlagsVoidTicket())
				return;
			
			if (!RTTxnType.isVoidTrx())
			{
				printNormal(jpos.FiscalPrinterConst.FPTR_S_RECEIPT, " ");
				printNormal(jpos.FiscalPrinterConst.FPTR_S_RECEIPT, RTConsts.IVADETAILS);
				SSCO = RoungickTax.getVatTable(true);
				if (SSCO != null) {
					boolean isEsente = false;
					for ( int index = 0 ; index < SSCO.size(); index++  ){
						VatInOutHandling vatInOutH = (VatInOutHandling) SSCO.get(index);
						System.out.println("vatInOutH:"+vatInOutH);
						if (vatInOutH.getTotalAmount() > 0) {
						 String out = buildItem ( vatInOutH.getRtVatCode()+" - "+vatInOutH.getShortDescription(), (long)(vatInOutH.getTotalAmount()*10000));
		                 printNormal(jpos.FiscalPrinterConst.FPTR_S_RECEIPT, out);
						}
						if (vatInOutH.getRate() == 0) {
							isEsente = true;
						}
					}							
					printNormal(jpos.FiscalPrinterConst.FPTR_S_RECEIPT, " ");
					if (isEsente) {
						for ( int index = 0 ; index < SSCO.size(); index++  ){
							VatInOutHandling vatInOutH = (VatInOutHandling) SSCO.get(index);
							if (vatInOutH.getTotalAmount() > 0 && vatInOutH.getRate() == 0) {
							 String out = vatInOutH.getShortDescription() + " = " + vatInOutH.getFullDescription();
			                 printNormal(jpos.FiscalPrinterConst.FPTR_S_RECEIPT, out);
							}
						}
					}
				}
			}
			
			printNormal(jpos.FiscalPrinterConst.FPTR_S_RECEIPT, " ");
			toDayDate(FiscalNum);
			printNormal(jpos.FiscalPrinterConst.FPTR_S_RECEIPT, " ");
			
			//CurrentReceiptNumber = ""+(Integer.parseInt(FiscalNum)+1);
		}
		
		private void endTicketSRT(String SRTServerID, String SRTTillID, String currentFingerPrint) throws JposException
		{
			if (SharedPrinterFields.isFlagsVoidTicket())
				return;
			
			if (!RTTxnType.isVoidTrx())
			{
				printNormal(jpos.FiscalPrinterConst.FPTR_S_RECEIPT, RTConsts.PAYMDETAILS);
				for  ( int i = 0; i < SharedPrinterFields.a.size() ; i++ )
				{
					MethodE M = (MethodE)SharedPrinterFields.a.get(i);
					if (M.getM() == R3define.fprintRecTotal){
	                   if (SharedPrinterFields.lineePagamento != null) {
	                        for (Iterator iterator = SharedPrinterFields.lineePagamento.keySet().iterator(); iterator.hasNext();) {
	                           String k = (String) iterator.next();
	                           //Double valDouble = new Double(((ICurrency) SharedPrinterFields.lineePagamento.get(k)).doubleValue() * 10000);	// prima era così
	                           BigDecimal bd = new BigDecimal((Double)SharedPrinterFields.lineePagamento.get(k));
	                           bd = bd.setScale(2, RoundingMode.HALF_DOWN);
	                           Double valDouble = new Double(bd.doubleValue() * 10000);
	                           long val =  valDouble.longValue();                                        
	                           String out = buildItem ( k, val);
	                           printNormal(jpos.FiscalPrinterConst.FPTR_S_RECEIPT, out);
	                        }
	                        SharedPrinterFields.lineePagamento = null;
	                    }else {                        
							long resto = 0;
							if (M.getV().get(2).toString().equalsIgnoreCase(SharedPrinterFields.ChangeCurrency))
								resto = HardTotals.TotalePagato.getLongX100()-HardTotals.Totale.getLongX100();
							String out = buildItem ( M.getV().get(2).toString().substring(3), Long.parseLong(M.getV().get(1).toString()) - resto );
							printNormal(jpos.FiscalPrinterConst.FPTR_S_RECEIPT, out);
	                    }
					}
				}
				if (SharedPrinterFields.RoundingRT > 0.0) {
					long val =  SharedPrinterFields.RoundingRT.longValue();
	                String out = buildItem (RTConsts.ROUNDINGDETAILS, val);
					printNormal(jpos.FiscalPrinterConst.FPTR_S_RECEIPT, out);
				}
			}
			
			if ((SharedPrinterFields.Lotteria.getLotteryCode() != null) && (SharedPrinterFields.Lotteria.getLotteryCode().length() > 0))
			{
				if (!RTTxnType.isVoidTrx())
				{
					printNormal(jpos.FiscalPrinterConst.FPTR_S_RECEIPT, R3define.LotteryMessage + SharedPrinterFields.Lotteria.getLotteryCode());
					//for (int i = 0; i < R3define.LotteryMessages.length; i++) {
					//	printNormal(jpos.FiscalPrinterConst.FPTR_S_RECEIPT, R3define.LotteryMessages[i]);
					//}
				}
				SharedPrinterFields.Lotteria.resetLottery();
			}

			printNormal(jpos.FiscalPrinterConst.FPTR_S_RECEIPT, " ");
			printNormal(jpos.FiscalPrinterConst.FPTR_S_RECEIPT, RTConsts.SERVERID+SRTServerID);
			printNormal(jpos.FiscalPrinterConst.FPTR_S_RECEIPT, RTConsts.TILLID+SRTTillID);
			printNormal(jpos.FiscalPrinterConst.FPTR_S_RECEIPT, " ");
			
			if ((currentFingerPrint != null) && (currentFingerPrint.length() > 0))
			{
				printNormal(jpos.FiscalPrinterConst.FPTR_S_RECEIPT, "--------"+RTConsts.FINGERPRINT+"-------");
				
				ArrayList cfp = new ArrayList();
				cfp = splitFingerPrint(currentFingerPrint);
				if (cfp.size() > 0){
					for (int i=0; i<cfp.size(); i++)
						printNormal(jpos.FiscalPrinterConst.FPTR_S_RECEIPT, (String)cfp.get(i));
				}
				printNormal(jpos.FiscalPrinterConst.FPTR_S_RECEIPT, "--------------------------------");
			}
			
			printNormal(jpos.FiscalPrinterConst.FPTR_S_RECEIPT, " ");
			
			if (!SharedPrinterFields.isCFcliente()){
				printNormal(jpos.FiscalPrinterConst.FPTR_S_RECEIPT, RTConsts.INTERPELLO141);
				printNormal(jpos.FiscalPrinterConst.FPTR_S_RECEIPT, RTConsts.INTERPELLO142);
				printNormal(jpos.FiscalPrinterConst.FPTR_S_RECEIPT, RTConsts.INTERPELLO143);
				printNormal(jpos.FiscalPrinterConst.FPTR_S_RECEIPT, " ");
			}
			SharedPrinterFields.setCFcliente(false);
		}
		
		ArrayList splitFingerPrint(String fingerprint)
		{
			int max = RTConsts.setMAXLNGHOFFINGERPRINT();
			
			ArrayList reply = new ArrayList();
			
			int lines = (fingerprint.length() / max) + 1;
			
			for (int i = 0; i < lines; i++){
				int begin = i * max;
				int end = begin + max;
	            if (end > fingerprint.length())
	                end = fingerprint.length();
				
				if (end > begin){
					String s = fingerprint.substring(begin, end);
					reply.add(s);
				}
			}
			
			return reply;
		}
		
		private void AutoVoidTrx()
		{
/*			ITransactionSale tSale = (ITransactionSale) getTranstovoid();				// ???
			if (tSale == null)
				System.out.println ("AutoVoidTrx - tSale = null");
			else{
				VoidTransaction voiding = new VoidTransaction();
				boolean av = false;
				boolean at = false;
				try {
					System.out.println ("AutoVoidTrx - Voiding");
					voiding.initialize(posEngine);
					av = voiding.getAutoVoid();
					at = voiding.getAutoTender();
					IInput data = new Input(fingerprinterror,fingerprinterror,true,false);
					voiding.setAutoVoid(true);
					voiding.setAutoTender(true);
					voiding.allowInteraction(null, tSale);
					voiding.setAutoVoid(true);
					voiding.setAutoTender(true);
					voiding.doInteraction(data, tSale);
					voiding.setAutoVoid(av);
					voiding.setAutoTender(at);
					voiding.destroy();
				} catch (Exception e) {
					System.out.println ("AutoVoidTrx - exception = "+e.getMessage());
					voiding.setAutoVoid(av);
					voiding.setAutoTender(at);
				}
			}*/
		}
		
		public static int  getPrinterState() throws JposException
		{
			while ( true )
			{
				if ( SharedPrinterFields.isInTicket() )
				{
					return ( getSimulateState() );
				}
				else
				{
					return ( getState() );
				}
			}
		}
		
		private static int getFiscalPrinterState() throws JposException
		{
			if (PrinterType.isCustomModel())
				TakeYourTime.takeYourTime(80);
			return (fiscalPrinterDriver.getPrinterState());
		}
		
		public void SetLogo(String name, int number)
		{
			if (PrinterType.isDieboldRTOneModel())
				return;
				   
			if (PrinterType.isTH230Model())
			{
				System.out.println("SetLogo - setting logo: "+number+" - "+name);
				String[] params = {SharedPrinterFields.LOGO_FOLDER+name, null};
				int[] params1 = {number};
		            
				try {
					if (number == SharedPrinterFields.LOGO_NUMBER)
					{
						// erase all images
						fiscalPrinterDriver.directIO(1201, null, null);
					}
				} catch (JposException e) {
					System.out.println ( "MAPOTO-SETLOGO (1201) <"+e.toString()+">");
					e.printStackTrace();
				}
				TakeYourTime.takeYourTime(600);
		            
				int retry = (number == SharedPrinterFields.LOGO_NUMBER ? 3 : 1);
				for (int i=0; i<retry; i++)
				{
					try {
						// image loading
						fiscalPrinterDriver.directIO(1202, params1, params[0]);
						System.out.println ( "MAPOTO-SETLOGO (1202) retry="+i+" OK");
						TakeYourTime.takeYourTime(600);
						break;
					} catch (JposException e) {
						System.out.println ( "MAPOTO-SETLOGO (1202) <"+e.toString()+">");
						e.printStackTrace();
					}
					TakeYourTime.takeYourTime(600);
				}
			}
				   
			if (PrinterType.isRCHPrintFModel())
			{
				System.out.println("SetLogo - setting logo: "+number+" - "+name);
						
						File filep = new File(SharedPrinterFields.LOGO_FOLDER+name);
						if (!filep.exists()){
							System.out.println("SetLogo: "+filep.getAbsolutePath()+" doesn't exist");
							return;
						}

						int cmdLoad = 1202;
						int cmdSelect = 1204;
			    		int[] configuration = new int[] {3};
			    		String[] path = new String[] {SharedPrinterFields.LOGO_FOLDER+name};
			    		int[] logo = new int[] {3};
			    		
			    		if (number == SharedPrinterFields.TRAILER_LOGO_NUMBER) {
			    			cmdSelect = 1205;
			    			configuration[0] = 4;
			    			logo[0] = 4;
			    		}
			    		
			    		try {
							fiscalPrinterDriver.directIO(cmdLoad, configuration, path);
						} catch (Exception e) {
							System.out.println("SetLogo ("+cmdLoad+") - exception: "+e.getMessage());
						}
			    		System.out.println("Waiting a little while...");
			    		TakeYourTime.takeYourTime(5000);
			    		
			    		try {
			    			fiscalPrinterDriver.directIO(cmdSelect, logo, null);
						} catch (Exception e) {
							System.out.println("SetLogo ("+cmdSelect+") - exception: "+e.getMessage());
						}
			    		System.out.println("Waiting a little while...");
			    		TakeYourTime.takeYourTime(5000);
			}
		}
		   
		protected void PrintLogo(int number)
		{
			if (PrinterType.isCUSTOMK2Model() && (number == SharedPrinterFields.TRAILER_LOGO_NUMBER))
				return;
			   
			if (PrinterType.isTH230Model())
			{
				// dovremmo essere in uno scontrino non fiscale, perchè
				// negli scontrini fiscali è impostato/stampato tramite l'AdditionalTrailer
				String UseBitmapInSlot = ""+(char)0x1B + (char)0x7C + (byte)number + (char)0x42;
				try {
					fiscalPrinterDriver.printNormal(jpos.FiscalPrinterConst.FPTR_S_RECEIPT, UseBitmapInSlot);
				} catch (JposException e) {
					System.out.println ( "MAPOTO-PRINTLOGO <"+e.toString()+">");
					e.printStackTrace();
				}
			}
			if (PrinterType.isCUSTOMK2Model())
			{
				byte JPOS_FPTR_DI_RAWDATA = 0;
				int[] data = new int[25];
				String strBytData;

				try
				{
					strBytData = new String("3017031");
					if (number == SharedPrinterFields.TRAILER_LOGO_NUMBER)
						strBytData = new String("3017032");
					fiscalPrinterDriver.directIO(JPOS_FPTR_DI_RAWDATA, data, strBytData);
				}
				catch ( JposException e )
				{
					System.out.println ( "MAPOTO-PRINTLOGO <"+e.toString()+">");
					e.printStackTrace();
				}
			}
		}
		
		protected static void scriviLastTicket(String linea){
	    	apriFile();
			scriviFile(linea);
			chiudiFile();
		}
		
		private static void apriFile(){
		  	 try{
		  		 inout = new File(SharedPrinterFields.lastticket);
		  		 fos = new FileOutputStream(inout,true);
		  		 ps = new PrintStream(fos);
		  	 }catch (Exception e) {
		  		 System.out.println("apriFile - errore:"+e);
		  	 }
		   }
		
		   private static void chiudiFile(){
		   	try{
		   		ps.close();
		   		ps = null;
		   		fos.close();
		   		fos = null;
		//	    inout = null;
		   	}catch (Exception e) {
		   		System.out.println("chiudiFile - errore:"+e);
		   	}
		   }
		   
		   private static void scriviFile(String linea)
		   {
			    if ((linea == null) || (linea.length() == 0))
			    	return;
			    
			    String s = linea;
			    char lastch;
			    
				lastch = s.charAt(s.length()-1);
				if (lastch >= ' ')
					s = s + R3define.LF;
			   
		    	 try{
					ps.print(s);
		    	 }catch (Exception e) {
		    		 System.out.println("scriviFile - errore:"+e);
		    	 }
		     }
		   
		   private void cancellaFile()
		   {
				if (inout == null)
			  		 inout = new File(SharedPrinterFields.lastticket);
				
		     	 try{
		     		 inout.delete();
		     	 }catch (Exception e) {
		     		 System.out.println("cancellaFile - errore:"+e);
		     	 }
		      }
		   
			public boolean leggiFile()
			{
				char lastch;
				
				if (inout == null)
			  		 inout = new File(SharedPrinterFields.lastticket);
				
				FileInputStream fis = null;
				try {
					fis = new FileInputStream(inout);
				} catch (FileNotFoundException e) {
					 System.out.println("leggiFile - errore:"+e);
					 return(false);
				}
				
				try {
					beginNonFiscal();
				} catch (JposException e) {
			   		 System.out.println("leggiFile - errore:"+e);
					 return(false);
				}
				
				InputStreamReader isr = new InputStreamReader(fis);
				BufferedReader br = new BufferedReader(isr);
				String linea = null;
				try {
					linea = br.readLine();
				} catch (IOException e) {
				 System.out.println("leggiFile - errore:"+e);
				 return(false);
				}
				while (linea != null)
				{
					// può capitare xchè readLine() elimina i CrLf a fine linea
					if (linea.length() == 0)
						linea=" ";
					
					if (PrinterType.isEpsonModel()){
						int a = linea.indexOf("TOTALE");
						if (a >= 0)
							linea = linea.substring(a);
					}
					
					try {
						lastch = linea.charAt(linea.length()-1);
						if (lastch >= ' ')
							linea = linea + R3define.LF;
						printNormal(jpos.POSPrinterConst.PTR_S_RECEIPT, linea);
					} catch (JposException e1) {
				   		 System.out.println("leggiFile - errore:"+e1);
			    		 return(false);
					}
					
					linea = null;
					
					try {
						linea = br.readLine();
					} catch (IOException e) {
			    		 System.out.println("leggiFile - errore:"+e);
			    		 return(false);
					}
				}
				
				try {
					endNonFiscal();
				} catch (JposException e) {
			   		 System.out.println("leggiFile - errore:"+e);
					 return(false);
				}
				
				return(true);
			}
			
		protected static boolean initTicketOnFile() throws JposException
		{
			char	lastch;
			int		i;
			String	sd;
			
			ArrayList HH = TicketErrorSupport.getHD();
			for ( i = 0; i < 2 ; i++ )
			{
				sd = " ";
				lastch = sd.charAt(sd.length()-1);
				if (lastch >= ' ')
					sd = sd + R3define.LF;
				
				scriviLastTicket(sd);
			}
			for ( i = 0; i < HH.size() ; i++ )
			{
				sd = (String)HH.get( i );
				lastch = sd.charAt(sd.length()-1);
				if (lastch >= ' ')
					sd = sd + R3define.LF;
				
				scriviLastTicket(sd);
			}
			for ( i = 0; i < 2 ; i++ )
			{
				sd = " ";
				lastch = sd.charAt(sd.length()-1);
				if (lastch >= ' ')
					sd = sd + R3define.LF;
				
				scriviLastTicket(sd);
			}
			return ( true );
		}
		
		private void initTicket() throws JposException
		{
			if (PrinterType.isTH230Model())
			{
				int maxHeaderLine = fiscalPrinterDriver.getNumHeaderLines();

				ArrayList HH = TicketErrorSupport.getHD();
				if (HH == null)
					return;
				space (2);
				for ( int i = 0; (i < HH.size()) && (i < maxHeaderLine); i++ )
				{
					String sd = (String)HH.get(i);
					
					sd = align_center(sd, fiscalPrinterDriver.getMessageLength());
					   
					fiscalPrinterDriver.printNormal(jpos.FiscalPrinterConst.FPTR_S_RECEIPT, sd);
				}
				space ( 2 );
			}
		}
		
	    private void space (int how) throws JposException
	    {
			for ( int i = 0 ; i < how; i++ )
			{
				fiscalPrinterDriver.printNormal(jpos.FiscalPrinterConst.FPTR_S_RECEIPT," ");
			}
	    }
	    
	    private String align_center(String s, int max)
	    {
	    	String t=s;
	    	String spaces = new String(new char[(max-(t.trim().length()))/2]).replace('\0', ' ');
	    	return(spaces+t);
	    }
		   
	    public boolean  getCapAdditionalLines() throws jpos.JposException
	    {
	    	return ( fiscalPrinterDriver.getCapAdditionalLines() );
	    }
	    
	    private void printNormalEFP90_F(int i, String s) throws JposException
	    {
	        final int DESC_LENGTH = 32;
	        System.out.println("printNormal in i=" + i + " s=" + s);
	        String s1 = String13Fix.replaceAll(s,"\r\n", "\n");
	    	s1 = String13Fix.replaceAll(s1, "€", ""+(char)96);
	        int j = getFiscalPrinterState();
	        switch (j)
	        {
	        case jpos.FiscalPrinterConst.FPTR_PS_NONFISCAL:
	    		if (s1.trim().indexOf(R3_DOUBLE_R3) > -1)
	    			printDouble(R3define.DOUBLEPRINT_LEFT, String13Fix.replaceAll(s1, R3_DOUBLE_R3, ""), 40);
	    		else
	    			fiscalPrinterDriver.printNormal(i, s1);
	            break;
	        case jpos.FiscalPrinterConst.FPTR_PS_FISCAL_RECEIPT_ENDING:
	            if (getCapAdditionalLines())
	            {
	            	printRecMessage(s1);
	            } 
	            else
	            {
	                fiscalPrinterDriver.printNormal(i, s1);
	            }
	            break;
	        case jpos.FiscalPrinterConst.FPTR_PS_FISCAL_RECEIPT:
	        	if (SRTPrinterExtension.isPRT() || SRTPrinterExtension.isSRT()) {
	        		if (s.startsWith(SharedPrinterFields.Lotteria.getLotteryTag())) {
	        			SharedPrinterFields.Lotteria.setLotteryCode(s.substring(SharedPrinterFields.Lotteria.getLotteryTag().length()));
	        			break;
	        		}
	        	}
	        	if (s.startsWith(getCFPIvaTag())) {
	        		String cfpi = s.substring(getCFPIvaTag().length());
	                cfpi = String13Fix.replaceAll(cfpi, "\r", "");
	                cfpi = String13Fix.replaceAll(cfpi, "\n", "").trim();
	        		if (SRTPrinterExtension.isPRT()) {
	        			printRecCFPiva(cfpi);
	        			break;
	        		}
	        		else {
	        			s1 = (cfpi.length() == PILEN ? PI+cfpi : CF+cfpi);
	        			break;	// non lo devo stampare
	        		}
	        	}
	        	String s3 = "";
	        	if (SRTPrinterExtension.isPRT()) {
	        		s3 = please25 ( s1 );
	        	}
	        	else {
		            s3 = (s1.length() > DESC_LENGTH) ? s1.substring(0, DESC_LENGTH) : s1;
	        	}
	            fiscalPrinterDriver.printNormal(i, s3);
	            break;
	        case jpos.FiscalPrinterConst.FPTR_PS_FISCAL_RECEIPT_TOTAL:
	        case jpos.FiscalPrinterConst.FPTR_PS_FISCAL_DOCUMENT:
	        case jpos.FiscalPrinterConst.FPTR_PS_FIXED_OUTPUT:
	        case jpos.FiscalPrinterConst.FPTR_PS_ITEM_LIST:
	        case jpos.FiscalPrinterConst.FPTR_PS_LOCKED:
	        default:
	        	String s2 = "";
	        	if (SRTPrinterExtension.isPRT()) {
	        		s2 = please25 ( s1 );
	        	}
	        	else {
		            s2 = (s1.length() > DESC_LENGTH) ? s1.substring(0, DESC_LENGTH) : s1;
	        	}
	            fiscalPrinterDriver.printNormal(i, s2);
	            break;
	        }
	        System.out.println("printNormal out");
	    }
	    
	    private String please25( String in)
	    {
	    	return ( pleaseAny (in,25) );
	    }
	    private String please30( String in)
	    {
	    	return ( pleaseAny (in,30) );
	    }
	    private String please36( String in)
	    {
	    	return ( pleaseAny (in,36) );
	    }
	    private String please40( String in)
	    {
	    	return ( pleaseAny (in,40) );
	    }
	    private String pleaseAny( String in,int qta)
	    {
	    	if (SRTPrinterExtension.isSRT())
	    	{
	    		char lastch = in.charAt(in.length()-1);
	    		if (!(lastch == '-'))
	    			qta--;
	    	}
			   
	    	String out = in;
	    	if ( in.length() <= qta )
	    		return ( out );
	    	int diff = in.length() - qta;
	    	while ( diff > 0 )
	    	{
	    		int i = out.indexOf("  ");
	    		if ( i == -1)
	    			break;
	    		diff--;
	    		String n = out.substring(0, i);
	    		n = n + out.substring(i+1);
	    		out = n;
	    	}
	    	return ( out );
	    }
		   
	    protected static int[] dobcc(String s)
	    {
	    	int[] bcc = {0,0};
	    	int a=0;
	    	int bccl=0;
	    	int bcch=0;
	    	for (int i=1; i<(s.length()-3); i++){
	    		a = a ^ ((char) (s.charAt(i)));
	    	}
	    	bccl = a & 0x0F;
	        bccl = bccl + 0x20;
	        bcch = a >> 4;
	        bcch = bcch + 0x20;
	        bcc[0] = bcch;
	        bcc[1] = bccl;
	        return (bcc);
	    }
		   
	    private String cleanLine(String in)
	    {
	    	String out = "";
	    	for ( int i = 0 ; i < in.length(); i++ )
	    	{
	    		if ( in.charAt(i) >= ' ' )
	    			out = out + in.charAt(i);
	    		else
	    			out = out + ' ';
	    	}
			   
	    	if (PrinterType.isDieboldRTOneModel())
	    	{
	    		String s = out;
	    		out = s.replaceAll("\\)", " ");
	    		s = out;
	    		out = s.replaceAll("\\(", " ");
	    	}
				
	    	return (out);
	    }
		   
		public void printDouble(int Mode, String data, int coloumn) throws JposException
		{
			String	buff = "";
			String	hdFsc = "014";
			String	hdPrt = R3define.ESC +"|3C";
			String	hd;
			int		i, j;
			int		coloumnLimit;
	        int[]	dt={0};
	        
	        dt[0] = R3define.DOUBLEPRINT;
			
			coloumnLimit = coloumn - 1;
			System.out.println ( "DOUBLE <"+coloumn+"><"+coloumnLimit+">");
			
			hd = hdFsc;

			String[] data1 = String13Fix.split(data,"\n");
			for (j = 0; j < data1.length; j++)
			{
				if ( Mode == R3define.DOUBLEPRINT_LEFT )
				{
					buff = data1[j] + R3define.DOUBLEPRINTBLANK;
					StringBuffer bjct=new StringBuffer(hd+buff.substring(0,coloumn));
					fiscalPrinterDriver.directIO(0, dt, bjct);
				}
				else if ( Mode == R3define.DOUBLEPRINT_RIGHT )
				{
					buff = R3define.DOUBLEPRINTBLANK + data1[j];
					StringBuffer bjct=new StringBuffer(hd+buff.substring(buff.length()-coloumn,buff.length()));
					fiscalPrinterDriver.directIO(0, dt, bjct);
				}
				else
				{
					i = data1[j].length();
					if ( i < coloumnLimit )
					{
						buff = R3define.DOUBLEPRINTBLANK.substring(0,(coloumn-i)/2);
						buff = buff + data1[j] + R3define.DOUBLEPRINTBLANK;
						StringBuffer bjct=new StringBuffer(hd+buff.substring(0,coloumn));
	    				fiscalPrinterDriver.directIO(0, dt, bjct);
					}
				}
			}
			return;
		}
		
		private void printRecCFPiva(String s) throws JposException
		{
			if (SRTPrinterExtension.isPRT())
			{
				int state = this.getFiscalPrinterState(); 
				System.out.println("printRecCFPiva - s = "+s+" - state = "+state);
				
				if ((s.length() != PILEN) && (s.length() != CFLEN))
					return;
				
				// lascio passare il comando solo in stato FPTR_PS_FISCAL_RECEIPT
				// anche se la Epson lo accetterebbe pure nello stato FPTR_PS_FISCAL_RECEIPT_ENDING,
				// ma la la RTOne segnalerebbe errore
				if (state != jpos.FiscalPrinterConst.FPTR_PS_FISCAL_RECEIPT)
					return;
				
				if (PrinterType.isEpsonModel())
				{
					String op = "01";
					String fiscalcode = s;
					StringBuffer sbcmd = new StringBuffer(op + fiscalcode);
					int cmd = 0;
					if (s.length() == PILEN)
						cmd = 1060;															// stampa P.Iva
					else
						cmd = 1061;															// stampa C.F.
					
					System.out.println("printRecCFPiva - sbcmd = "+sbcmd.toString());
					fiscalPrinterDriver.executeRTDirectIo(cmd, 0, sbcmd);
					System.out.println("printRecCFPiva - sbcmd = "+sbcmd.toString());
					if (Integer.parseInt(sbcmd.toString()) == 30) {
						System.out.println("printRecCFPiva - invalid fiscal code (bad checksum)");
						return;
					}
				}
				if (PrinterType.isRCHPrintFModel())
				{
		            String fiscalcode = s;
					StringBuffer sbcmd = new StringBuffer("=\"/?C/("+fiscalcode+")");		// stampa P.Iva / C.F.
		            
					System.out.println("printRecCFPiva - sbcmd = "+sbcmd.toString());
					fiscalPrinterDriver.executeRTDirectIo(0, 0, sbcmd);
					System.out.println("printRecCFPiva - sbcmd = "+sbcmd.toString());
				}
				setCFPIvaFlag(true);
			}
		}
		
	    public void executeDirectIo(int Command, String Data){
	    	int[] dt={0};
	        StringBuffer bjct=new StringBuffer(Data);
	        try{
	        	dt[0]=Command;
	                  
	        	System.out.println("Appena Data: "+String.valueOf(dt[0])+" --- "+"Object: "+bjct.toString()); 

	        	this.directIO(0, dt, bjct); 
	                  
	        	System.out.println("Data: "+String.valueOf(dt[0])+" --- "+"Object: "+bjct.toString()); 
	        }catch(Exception e){
	        	System.out.println("Data error Exception: "+ e.getMessage());   
	        }
	    }
	    
//	    protected int executeRTDirectIo (int Command, int pData, StringBuffer bjct)
//	    {
//	    	int reply = 0;
//	    	
//	    	if (PrinterType.isEpsonModel()){
//		    	int[] dt={0};
//		        try
//		        {
//		        	dt[0]=Command;
////		        	if ((Command == 4002) || (Command == 4005) || (Command == 4037))
////		        		System.out.println("EPSON - directIO(0,"+dt[0]+","+bjct.toString()+") - lunghezza="+bjct.toString().length());
//		        	fiscalPrinterDriver.directIO(0, dt, bjct); 
//		        }
//		        catch(Exception e)
//		        {
//		        	reply = -1;
//		        	System.out.println("Data error\nException: "+ e.getMessage());
//		        }
//	    	}
//	    	
//	    	if (PrinterType.isRCHPrintFModel()){
//	            int[] dt={pData};
//	            String[] pString = {new String(bjct)};
//	            String str = bjct.toString();
//	            try
//	            {
//	            	System.out.println("executeRTDirectIo - Command : "+Command+" - dt : "+dt[0]);
//	            	if (Command >= 1000) {
//	            		fiscalPrinterDriver.directIO(Command, dt, pString);
//		            	System.out.println("executeRTDirectIo - pString : "+pString[0]);
//		            	if (Command == 8003)
//		            		bjct.append(pString[0]);
//	            	}
//	            	else {
//		            	System.out.println("executeRTDirectIo - str input: "+str);
//	            		fiscalPrinterDriver.directIO(Command, dt, str);
//		            	System.out.println("executeRTDirectIo - str output: "+str);
//	            	}
//	            }
//	            catch(Exception e)
//	            {
//	            	System.out.println("Data error\nException: "+ e.getMessage());
//	            	
//	            	if (((Command == 6000) || (Command == 6001)) && (pData == 1))
//	            		dt[0] = 0;	// document not voidable/refundable
//	            	
//	            	if ((Command == 0) && (pData == 0))
//	            		return -1;	// per non sporcare dt[0] assegnandogli -1 
//	            }
//	           reply = dt[0];
//	    	}
//	    	
//	    	return reply;
//	    }

		public String intestazione5(boolean fiscalreceipt)
		{
			if (RTTxnType.isVoidTrx())
				return "";
			
			String intestazione = RTConsts.DESCRIZIONE;
			if (fiscalreceipt){
				intestazione = intestazione + SharedPrinterFields.ALINER.substring(0,RTConsts.getMAXITEMDESCRLENGTH()-2-intestazione.length()-RTConsts.IVA.length())+ RTConsts.IVA;
			}
			else{
				intestazione = intestazione + SharedPrinterFields.ALINER.substring(0,RTConsts.getMAXITEMDESCRLENGTH()-1-intestazione.length()-RTConsts.IVA.length())+ RTConsts.IVA;
				intestazione = intestazione + SharedPrinterFields.ALINER.substring(0,RTConsts.setMAXLNGHOFLENGTH()-intestazione.length()-RTConsts.PREZZO.length())+ RTConsts.PREZZO;
			}
			LastTicket.setIntestazione5(intestazione);
			return intestazione;
		}
		
		public String intestazione1(int type)
		{
			String intestazione = "";
			
			intestazione = RTConsts.INTESTAZIONE1;
			intestazione = SharedPrinterFields.ALINER.substring(0, (int)((RTConsts.setMAXLNGHOFLENGTH()-intestazione.length())/2))+intestazione;
			
			LastTicket.setIntestazione1(intestazione);
			return intestazione;
		}
		
		public String intestazione2(int type)
		{
			String intestazione = "";
			
			if (RTTxnType.isVoidTrx(type)){
				intestazione = RTConsts.INTESTAZIONE23;
				intestazione = SharedPrinterFields.ALINER.substring(0, (int)((RTConsts.setMAXLNGHOFLENGTH()-intestazione.length())/2))+intestazione;
			}
			if (RTTxnType.isRefundTrx(type)){
				intestazione = RTConsts.INTESTAZIONE21;
				intestazione = SharedPrinterFields.ALINER.substring(0, (int)((RTConsts.setMAXLNGHOFLENGTH()-intestazione.length())/2))+intestazione;
			}
			if (RTTxnType.isSaleTrx(type)){
				intestazione = RTConsts.INTESTAZIONE20;
				intestazione = SharedPrinterFields.ALINER.substring(0, (int)((RTConsts.setMAXLNGHOFLENGTH()-intestazione.length())/2))+intestazione;
			}
			LastTicket.setIntestazione2(intestazione);
			return intestazione;
		}
		
		public String intestazione3(int type)
		{
			String intestazione = "";
			
			if (RTTxnType.isVoidTrx(type) || RTTxnType.isRefundTrx(type)){
				intestazione = RTConsts.INTESTAZIONE3;
				intestazione = SharedPrinterFields.ALINER.substring(0, (int)((RTConsts.setMAXLNGHOFLENGTH()-intestazione.length())/2))+intestazione;
			}
			LastTicket.setIntestazione3(intestazione);
			return intestazione;
		}
		
		public String intestazione4(int type)
		{
			String intestazione = "";
			
			if (RTTxnType.isVoidTrx(type) || RTTxnType.isRefundTrx(type)){
				intestazione = RTConsts.INTESTAZIONE4;
				intestazione = SharedPrinterFields.ALINER.substring(0, (int)((RTConsts.setMAXLNGHOFLENGTH()-intestazione.length())/2))+intestazione;
			}
			LastTicket.setIntestazione4(intestazione);
			return intestazione;
		}
		
		void testata() throws JposException
		{
			printNormal_ejoff(jpos.FiscalPrinterConst.FPTR_S_RECEIPT, " ");
			String s = intestazione1(RTTxnType.getTypeTrx());
			if (s.length() > 0) printNormal(jpos.FiscalPrinterConst.FPTR_S_RECEIPT, s);
			s = intestazione2(RTTxnType.getTypeTrx());
			if (s.length() > 0) printNormal(jpos.FiscalPrinterConst.FPTR_S_RECEIPT, s);
			s = intestazione3(RTTxnType.getTypeTrx());
			if (s.length() > 0) printNormal(jpos.FiscalPrinterConst.FPTR_S_RECEIPT, s);
			s = intestazione4(RTTxnType.getTypeTrx());
			if (s.length() > 0) printNormal(jpos.FiscalPrinterConst.FPTR_S_RECEIPT, s);
			printNormal_ejoff(jpos.FiscalPrinterConst.FPTR_S_RECEIPT, " ");
			s = intestazione5(this.isFiscalAndSRTModel());
			if (this.isFiscalAndSRTModel()){
				if (s.length() > 0) printNormal_ejoff(jpos.FiscalPrinterConst.FPTR_S_RECEIPT, s);
			}
			else{
				if (s.length() > 0) printNormal_ejoff(jpos.FiscalPrinterConst.FPTR_S_RECEIPT, rightAlign(s));
			}
		}
		
	    private String rightAlign ( String desc )
	    {
	        int MAXLNGHOFLENGTH = RTConsts.setMAXLNGHOFLENGTH();
	        
	        int mx = MAXLNGHOFLENGTH;
	        String newMsg = (desc.length() <= mx)? desc: desc.substring (0, mx);
	        
	    	return ( SharedPrinterFields.ALINER.substring(0,mx-newMsg.length())+ newMsg);
	    }
		
		protected void stampaBarcodePerResi()
		{
			// usata solo negli scontrini fiscali
			
			if (PrinterType.isEpsonModel() && SmartTicket.isSmart_Ticket() && fiscalPrinterDriver.isfwSMTKenabled())	// TEMPORANEO IN ATTESA DEL FIX FW EPSON
				if (SmartTicket.Smart_Ticket_ReceiptMode != SmartTicket.ERECEIPT_PAPER)
					return;
			
			if (!Lotteria.isPrintBarcode())
				return;
			
			if (RTTxnType.isRefundTrx() || RTTxnType.isVoidTrx())
				return;
			
			if (SharedPrinterFields.isFlagsVoidTicket())
				return;
			
			String anno;
			String mese;
			String giorno;
			GregorianCalendar gc = new GregorianCalendar();
			anno = Sprint.f("%02d",gc.get(gc.YEAR)-2000);	// fino al 2099 dovremmo essere a posto
			mese = Sprint.f("%02d",gc.get(gc.MONTH)+1);
			giorno = Sprint.f("%02d",gc.get(gc.DATE));
			
			String num = "";
			String repz = "";
			if (SRTPrinterExtension.isSRT()) {
				num = Sprint.f("%04d",DummyServerRT.CurrentReceiptNumber);
				repz = Sprint.f("%04d",DummyServerRT.CurrentFiscalClosure);
			}
			else {
				if (PrinterType.isRchPrintFModel()) {
					num = fiscalPrinterDriver.myRchFiscalNumber;
				}
				
		        int[] ai = new int[1];
		        String[] as = new String[1];
				if (!PrinterType.isRchPrintFModel()) {
					fiscalPrinterDriver.getData(FiscalPrinterConst.FPTR_GD_FISCAL_REC, ai, as);
		            num = as[0];
				}
				
	            if (PrinterType.isEpsonModel()) {
	            	try {
						if (fiscalPrinterDriver.getPrinterState() == jpos.FiscalPrinterConst.FPTR_PS_MONITOR) {
							// siamo in extra lines dopo l'endfiscal
	    	            	num = Sprint.f("%04d",Integer.parseInt(as[0])-1);
						}
					} catch (Exception e) {
						   System.out.println("stampaBarcodePerResi - Exception : " + e.getMessage());
						   return;
					}
	            }
	            
	            fiscalPrinterDriver.getData(FiscalPrinterConst.FPTR_GD_Z_REPORT, ai, as);
	            try {
	            	repz = Sprint.f("%04d",Integer.parseInt(as[0])+1);
                } catch (NumberFormatException e) {
				   System.out.println("stampaBarcodePerResi - Exception : " + e.getMessage());
				   return;
                }
			}
            
            String bc = barcodePrefix;
			bc = bc + PosApp.getTillNumber() + giorno + mese + anno + repz + num;
			bc = bc + SharedPrinterFields.Lotteria.getLotteryCode();
			System.out.println ( "stampaBarcodePerResi printing barcode : <"+bc+">");
			
			StringBuffer objb = new StringBuffer("");
    		if (PrinterType.isEpsonModel()) {
    			bc = convertiBarcodePerResi(bc);
    			String width = setwidth(SharedPrinterFields.Lotteria.getLotteryCode());
    			String subset = setsubset();
				objb = new StringBuffer("01"+"901"+width+"150"+"2"+"0"+"00"+"73"+subset+bc);
				fiscalPrinterDriver.executeRTDirectIo(1075, 0, objb);
    		}
    		
    		if (PrinterType.isRCHPrintFModel()) {
				objb = new StringBuffer(bc);
				fiscalPrinterDriver.executeRTDirectIo(5000, 8, objb);
    		}
    		
    		SmartTicket.SMTKbarcodes_add(bc);
		}
		
		public void stampaBarcodePerResi(String bc, String lott)
		{
			// usata solo negli scontrini regalo non fiscali
			
//			if (isEpsonModel() && isSmart_Ticket() && isfwSMTKenabled())	// TEMPORANEO IN ATTESA DEL FIX FW EPSON
//				if (Smart_Ticket_ReceiptMode != ERECEIPT_PAPER)
//					return;
			
			if (!Lotteria.isPrintBarcode())
				return;
			
			if (RTTxnType.isRefundTrx() || RTTxnType.isVoidTrx())
				return;
			
			System.out.println ( "stampaBarcodePerResi printing barcode : <"+bc+">");
			
			StringBuffer objb = new StringBuffer("");
    		if (PrinterType.isEpsonModel()) {
    			bc = convertiBarcodePerResi(bc);
    			String width = setwidth(lott);
    			String subset = setsubset();
				objb = new StringBuffer("01"+"901"+width+"150"+"2"+"0"+"00"+"73"+subset+bc);
				executeDirectIo(1075, objb.toString());
    		}
    		
    		if (PrinterType.isRCHPrintFModel()) {
    			int[] dt={0};
    			dt[0]=8;
				objb = new StringBuffer(bc);
				try {
					this.directIO(5000, dt, objb);
				} catch (JposException e) {
					System.out.println("stampaBarcodePerResi - Exception : " + e.getMessage());
				}
    		}
    		
//    		SMTKbarcodes_add(bc);
		}

		public static String getBarcodePrefix() {
			if (!PrinterType.isEpsonPrinterModel())
				return barcodePrefix;
			
	    	String bcp = "";
	    	bcp = convertiBarcodePerResi(barcodePrefix);
	    	return bcp;
		}
		
		protected static String convertiBarcodePerResi(String barcode)
		{
			if (!Extra.isNumericVAR())
				return barcode;
			
			String bc = "";
			
	    	for (int i=0; i<barcode.length(); i++) {
	    		if ((i < 3) || (i > 18)) {
	    			int c = (int)barcode.charAt(i);
	    			bc = bc + String.valueOf(c);
	    		}
	    		else
	    		{
	    			bc = bc + barcode.charAt(i);
	    		}
	    	}
	    	
			System.out.println("convertiBarcodePerResi returning barcode : <"+bc+">");
	    	return bc;
		}
		
		private String setwidth(String lott)
		{
			int width = 2;
			if (getBarcodePrefix().length() == 6)
				width = 3;
			else if (getBarcodePrefix().length() == 3)
				width = 2;
			if (lott.length() > 0)
				width--;
			return ""+width;
		}
		
		private String setsubset()
		{
			String subset = "{B";
			if (getBarcodePrefix().length() == 6)
				subset = "{C";
			else if (getBarcodePrefix().length() == 3)
				subset = "{B";
			return subset;
		}
		
		protected void abilitaTaglioCarta(boolean flag)
		{
			if (PrinterType.isEpsonModel() && SmartTicket.isSmart_Ticket() && fiscalPrinterDriver.isfwSMTKenabled())	// TEMPORANEO IN ATTESA DEL FIX FW EPSON
				if (SmartTicket.Smart_Ticket_ReceiptMode != SmartTicket.ERECEIPT_PAPER)
					return;
			
			if (!Lotteria.isPrintBarcode())
				return;
			
			if (!PrinterType.isEpsonModel())
				return;
			
			String op;
			String cutMode;
			StringBuffer objb = new StringBuffer("");
			
			op = "01";
			if (!flag)
				cutMode = "0";
			else
				cutMode = "1";
			objb = new StringBuffer(op + cutMode);
			fiscalPrinterDriver.executeRTDirectIo(1137, 0, objb);
		}
		
		public static boolean checkCurrentTicketTotal ( String Pr, long In )
		{
		    if (Company.getNegTill() == 1)
		    {
				System.out.println("MAPOTO-TicketErrorSupport:checkCurrentTicketTotal isNegativeTill="+Company.getNegTill());
				return ( true );
		    }

			System.out.println("TicketErrorSupport:Verifica Totale Gestionale con Totale Fiscale");
			if ( ( countErrori % 3 ) == 0 )		// debugs phase only
			{
				System.out.println("TicketErrorSupport:Simula Errore Totale Gestionale diverso da Totale Fiscale");
				return ( false );
			}
			double	fromPrinter = Double.valueOf(Pr).doubleValue()/100;
			currentTicket = (double)(In /100);
			if ( Math.abs(currentTicket) == Math.abs(fromPrinter) )
			{
				System.out.println("TicketErrorSupport:checkCurrentTicketTotal <true>");
				return ( true );
			}
			else
			{
				System.out.println("MAPOTO-TicketErrorSupport:checkCurrentTicketTotal <false> at PRINTER="+fromPrinter+" at TILL="+currentTicket);
			    if (PrinterType.isNCR2215Model())
					return ( true );	// pare che questa interrogazione non funzioni su NCR
				return ( false );
			}
		}
		
		public static boolean isCurrentTicketZero (  )
		{
			System.out.println("MAPOTO-TicketErrorSupport:isCurrentTicketZero <"+( currentTicket == (double)0.0 )+">");
			return ( currentTicket == (double)0.0 );
		}
		
		public static boolean checkCurrentDailyTotal ( String In )
		{
		    if (Company.getNegTill() == 1)
		    {
				System.out.println("MAPOTO-TicketErrorSupport:checkCurrentDailyTotal isNegativeTill="+Company.getNegTill());
				return ( true );
		    }

			System.out.println("MAPOTO-TicketErrorSupport:Verifica Scontrino Fiscalizzato");
			if ( ( countErrori % 5 ) == 0 )	// debugs phase only
			{
				System.out.println("MAPOTO-TicketErrorSupport:Simula Errore Scontrino Non Fiscalizzato");
				return ( false );
			}
			boolean res = false;
			double	inD = Double.valueOf(In).doubleValue()/100;
			double	inT = dailyTotal + currentTicket;
			if ( Math.abs(inT) == Math.abs(inD) )
				res = true;
			System.out.println("MAPOTO-TicketErrorSupport:Check dailyTotal consistency="+res+"IN+SCO="+inT+ " PRT="+inD);
			return ( res );
		}

		public static boolean checkCurrentDailyTotalRounded ( String In, double rounding )
		{
		    if (Company.getNegTill() == 1)
		    {
				System.out.println("MAPOTO-checkCurrentDailyTotalRounded isNegativeTill="+Company.getNegTill());
				return ( true );
		    }

			System.out.println("MAPOTO-checkCurrentDailyTotalRounded:Verifica Scontrino Fiscalizzato");
			if ( ( countErrori % 5 ) == 0 )	// debugs phase only
			{
				System.out.println("MAPOTO-checkCurrentDailyTotalRounded:Simula Errore Scontrino Non Fiscalizzato");
				return ( false );
			}
			boolean res = false;
			double	inD = Double.valueOf(In).doubleValue()/100;
			double	inT = dailyTotal + currentTicket + (rounding*100);
			if ( Math.abs(inT) == Math.abs(inD) )
				res = true;
			System.out.println("MAPOTO-checkCurrentDailyTotalRounded:Check dailyTotal consistency="+res+"IN+SCO="+inT+ " PRT="+inD);
			return ( res );
		}

		protected void printRecOmaggio(String s, long l, int i, int j, long l1, int kIvaPolipos) throws JposException
		{
			System.out.println ( "MAPOTO-EXEC PRINT OMAGGIO s="+s );
			System.out.println ( "MAPOTO-EXEC PRINT OMAGGIO l="+l);
			System.out.println ( "MAPOTO-EXEC PRINT OMAGGIO kIvaPolipos="+kIvaPolipos );
			
			int Command = 1;
			
			int k = DicoTaxToPrinter.getFromPoliposToPrinter(kIvaPolipos);
			if ((DicoTaxLoad.isIvaAllaPrinter()) && (k == SharedPrinterFields.VAT_N4_Index))
				k = Integer.parseInt(SharedPrinterFields.VAT_N4_Dept);
			
			newModifierCommand(s, l, Command, k, kIvaPolipos);
		}
		
		protected void printRecAcconto(String s, long l, int iIvaPolipos) throws JposException
		{
			System.out.println ( "MAPOTO-EXEC PRINT ACCONTO s="+s );
			System.out.println ( "MAPOTO-EXEC PRINT ACCONTO l="+l);
			System.out.println ( "MAPOTO-EXEC PRINT ACCONTO iIvaPolipos="+iIvaPolipos );
			
			int Command = 0;
			
			int i = DicoTaxToPrinter.getFromPoliposToPrinter(iIvaPolipos);
			if ((DicoTaxLoad.isIvaAllaPrinter()) && (i == SharedPrinterFields.VAT_N4_Index))
				i = Integer.parseInt(SharedPrinterFields.VAT_N4_Dept);
			
			newModifierCommand(s, l, Command, i, iIvaPolipos);
		}
		
		protected void printRecBMonoUso(String s, long l, int iIvaPolipos) throws JposException
		{
			System.out.println ( "MAPOTO-EXEC PRINT BMONOUSO s="+s );
			System.out.println ( "MAPOTO-EXEC PRINT BMONOUSO l="+l);
			System.out.println ( "MAPOTO-EXEC PRINT BMONOUSO iIvaPolipos="+iIvaPolipos );
			
			int Command = 2;
			
			int i = DicoTaxToPrinter.getFromPoliposToPrinter(iIvaPolipos);
			if ((DicoTaxLoad.isIvaAllaPrinter()) && (i == SharedPrinterFields.VAT_N4_Index))
				i = Integer.parseInt(SharedPrinterFields.VAT_N4_Dept);
			
			newModifierCommand(s, l, Command, i, iIvaPolipos);
		}

		private void newModifierCommand(String descr, long amount, int cmd, int vat, int ivapolipos)
		{
			if (SharedPrinterFields.isfwRT2disabled())
				return;
			
			System.out.println("newModifierCommand - descr=<"+descr+">");
			System.out.println("newModifierCommand - amount="+amount);
			System.out.println("newModifierCommand - cmd="+cmd);
			System.out.println("newModifierCommand - vat="+vat);
			
			if (PrinterType.isEpsonModel()) {
				String op = "01";
				String amn = Sprint.f("%09d",amount/100);;
				String type = Sprint.f("%02d",cmd);
				String department = Sprint.f("%02d",vat);
				String alignment = "1";
				descr="";
				while (descr.length() < 38)
					descr = descr+" ";
				
				StringBuffer sbcmd = new StringBuffer(op+descr+amn+type+department+alignment);
				System.out.println("RT2 - newModifierCommand - sbcmd = "+sbcmd.toString());
				fiscalPrinterDriver.executeRTDirectIo(1090, 0, sbcmd);
				System.out.println("RT2 - newModifierCommand - sbcmd = "+sbcmd.toString());
			}
			if (PrinterType.isRCHPrintFModel()) {
				if (cmd == 2) {
					if (TaxData.isServizi(ivapolipos))
						System.out.println("RT2 - newModifierCommand - WARNING : articolo tipo Servizio");
					
					// =R2/$2000/&3/(BUONO MONOUSO)
					cmd = 3;
				}
				if (cmd == 1) {
					// =R2/$2000/&1/(OMAGGIO)
					cmd = 1;
				}
				if (cmd == 0) {
					if (TaxData.isServizi(ivapolipos))
						System.out.println("RT2 - newModifierCommand - WARNING : articolo tipo Servizio");
					
					// =R2/$2000/&2/(ACCONTO)
					cmd = 2;
				}
				StringBuffer sbcmd = new StringBuffer("=R"+vat+"/$"+(amount/100)+"/&"+cmd+"/("+descr+")");
				System.out.println("RT2 - newModifierCommand - sbcmd = "+sbcmd.toString());
				fiscalPrinterDriver.executeRTDirectIo(0, 0, sbcmd);
				System.out.println("RT2 - newModifierCommand - sbcmd = "+sbcmd.toString());
			}
		}

		public void RTRefund(String data, String serialSRT, boolean freerefund) {
			
			if (!SRTCheckInput.checkInput(data, freerefund)) {
				MessageBox.showMessage("WrongSequence", null, MessageBox.OK);
				return;
			}
		
			if (SRTPrinterExtension.isPRT()) {
				boolean isRefundable = false;
				
				String cassa = Sprint.f("%02d", data.substring(SRTCheckInput.INPUTRT.indexOf(SRTCheckInput.CC), SRTCheckInput.INPUTRT.indexOf(SRTCheckInput.CC)+SRTCheckInput.CC.length()));
				String repz = Sprint.f("%04d", data.substring(SRTCheckInput.INPUTRT.indexOf(SRTCheckInput.ZZZZ), SRTCheckInput.INPUTRT.indexOf(SRTCheckInput.ZZZZ)+SRTCheckInput.ZZZZ.length()));
				String num = Sprint.f("%04d", data.substring(SRTCheckInput.INPUTRT.indexOf(SRTCheckInput.NNNN), SRTCheckInput.INPUTRT.indexOf(SRTCheckInput.NNNN)+SRTCheckInput.NNNN.length()));
				BasicDicoData date = new BasicDicoData (data,
						SRTCheckInput.INPUTRT.indexOf(SRTCheckInput.DD),
						SRTCheckInput.INPUTRT.indexOf(SRTCheckInput.DD)+SRTCheckInput.DD.length()+SRTCheckInput.MM.length()+SRTCheckInput.YY.length(),
														false);
				String printerid = "";
				if (data.length() < SRTCheckInput.INPUTRT.length()) {
					printerid = SharedPrinterFields.RTPrinterId;
					String parte1 = data.substring(SRTCheckInput.INPUTRT.indexOf(SRTCheckInput.CC), SRTCheckInput.INPUTRT.indexOf(SRTCheckInput.NNNN)+SRTCheckInput.NNNN.length());
					String parte2 = data.substring(SRTCheckInput.INPUTRT.indexOf(SRTCheckInput.NNNN)+SRTCheckInput.NNNN.length());
					data = parte1+printerid+parte2;
				}
				else {
					printerid = data.substring(SRTCheckInput.INPUTRT.indexOf(SRTCheckInput.MMMMMMMMMMM), SRTCheckInput.INPUTRT.indexOf(SRTCheckInput.MMMMMMMMMMM)+SRTCheckInput.MMMMMMMMMMM.length());
				}
				System.out.println("RTRefund - cassa = <"+cassa+"> repz = <"+repz+"> num = <"+num+"> data = <"+date.toString()+"> printerid = <"+printerid+">");

				RTConsts.INTESTAZIONE4 = repz+"-"+num+" del "+date.toString().substring(0, 2)+"-"+date.toString().substring(2, 4)+"-"+date.toString().substring(4);
				
				try {
					RefundCommands refcmd = new RefundCommands();
					isRefundable = refcmd.isRefundableDocument(repz, num, date.toString(), printerid, freerefund);
				} catch (JposException e) {
					System.out.println("RTRefund - errore:"+e.getMessage());
					isRefundable = false;
				}
				
				if (!isRefundable){
					System.out.println("RTRefund - isRefundable="+isRefundable);
					MessageBox.showMessage("WrongSequence", null, MessageBox.OK);
					return;
				}
				
				try {
					RefundCommands refcmd = new RefundCommands();
					if (!refcmd.RefundDocument(repz, num, date.toString(), printerid, freerefund)){
						MessageBox.showMessage("WrongSequence", null, MessageBox.OK);
						return;
					}
				} catch (JposException e) {
					System.out.println("RTRefund - errore:"+e.getMessage());
					return;
				}
				
				SharedPrinterFields.Lotteria.setLotteryTill(Integer.parseInt(cassa));
				DummyServerRT.setOriginalFiscalClosure(repz);
				DummyServerRT.setOriginalReceiptNumber(num);
				if (data.length() > SRTCheckInput.INPUTRT.length()){
					SharedPrinterFields.Lotteria.setLotteryCode(data.substring(SRTCheckInput.INPUTRT.length()));
				}
				SharedPrinterFields.Lotteria.setLotteryMF(printerid);
				
				RTTxnType.setRefundTrx();
				
//				setTxnnumbertorefund(PosApp.getTransactionNumber());	// ??? non dovrebbe più servire visto che setVoided() sulla transazione non si farà più da qui immagino
				
				//posEngine.printSelectedDevices("EchoLineRefund", null, false, "OD");	// ???
				
				//Transazione.nuovaTransazione();						// ???
				//GdONEData.statusTogdONE("main.state", "RtRefund");	// ???
				
				return;
			}
			
			if (SRTPrinterExtension.isSRT()) {
				String repz = null;
				String num = null;
				String del = null;
				
				boolean isRefundable = false;
				
				String negozio = Sprint.f("%s", data.substring(SRTCheckInput.INPUTSRT.indexOf(SRTCheckInput.SSSS), SRTCheckInput.INPUTSRT.indexOf(SRTCheckInput.SSSS)+SRTCheckInput.SSSS.length()));
				String cassa = Sprint.f("%02d", data.substring(SRTCheckInput.INPUTSRT.indexOf(SRTCheckInput.CC), SRTCheckInput.INPUTSRT.indexOf(SRTCheckInput.CC)+SRTCheckInput.CC.length()));		
				del = Sprint.f("%s", data.substring(SRTCheckInput.INPUTSRT.indexOf(SRTCheckInput.YY), SRTCheckInput.INPUTSRT.indexOf(SRTCheckInput.YY)+SRTCheckInput.YY.length()*2));
				del = del + Sprint.f("%s", data.substring(SRTCheckInput.INPUTSRT.indexOf(SRTCheckInput.MM), SRTCheckInput.INPUTSRT.indexOf(SRTCheckInput.MM)+SRTCheckInput.MM.length()));
				del = del + Sprint.f("%s", data.substring(SRTCheckInput.INPUTSRT.indexOf(SRTCheckInput.DD), SRTCheckInput.INPUTSRT.indexOf(SRTCheckInput.DD)+SRTCheckInput.DD.length()));
				num = Sprint.f("%04d", data.substring(SRTCheckInput.INPUTSRT.indexOf(SRTCheckInput.NNNN), SRTCheckInput.INPUTSRT.indexOf(SRTCheckInput.NNNN)+SRTCheckInput.NNNN.length()));		
				repz = Sprint.f("%04d", data.substring(SRTCheckInput.INPUTSRT.indexOf(SRTCheckInput.ZZZZ), SRTCheckInput.INPUTSRT.indexOf(SRTCheckInput.ZZZZ)+SRTCheckInput.ZZZZ.length()));
				String serverid = "";
				if (data.length() < SRTCheckInput.INPUTSRT.length()) {
					serverid = DummyServerRT.SRTServerID;
					String parte1 = data.substring(SRTCheckInput.INPUTSRT.indexOf(SRTCheckInput.SSSS), SRTCheckInput.INPUTSRT.indexOf(SRTCheckInput.NNNN)+SRTCheckInput.NNNN.length());
					String parte2 = data.substring(SRTCheckInput.INPUTSRT.indexOf(SRTCheckInput.NNNN)+SRTCheckInput.NNNN.length());
					data = parte1+serverid+parte2;
				}
				else {
					serverid = data.substring(SRTCheckInput.INPUTSRT.indexOf(SRTCheckInput.MMMMMMMMMMM), SRTCheckInput.INPUTSRT.indexOf(SRTCheckInput.MMMMMMMMMMM)+SRTCheckInput.MMMMMMMMMMM.length());
				}
				
				System.out.println("RTRefund - negozio = <"+negozio+"> cassa = <"+cassa+"> repz = <"+repz+"> num = <"+num+"> data = <"+del+"> serverid = <"+serverid+">");
				
				//posEngine.printSelectedDevices("PleaseWait",null,false,"OD");	// ???
				
				boolean noSearch = false;
				if (serialSRT == null || serialSRT.equalsIgnoreCase(DummyServerRT.SRTServerID) == false) {
					System.out.println("RTRefund - serialSRT diverso da quello del negozio:"+serialSRT+" "+DummyServerRT.SRTServerID);
				 	noSearch = true;
				}
				
				RTConsts.INTESTAZIONE4 = repz+"-"+num+" del "+del;
				
				isRefundable = DummyServerRT.pleaseDoReportReceiptRefund(repz, num, del, noSearch);
				
				System.out.println("RTRefund - isRefundable:"+isRefundable);				
				
				DummyServerRT.pleaseSetRefoundFound(isRefundable,serialSRT);		
				
				//posEngine.printSelectedDevices("EchoLineRefund", null, false, "OD");	// ???	
				
				SharedPrinterFields.Lotteria.setLotteryTill(Integer.parseInt(cassa));
				DummyServerRT.setOriginalFiscalClosure(repz);
				DummyServerRT.setOriginalReceiptNumber(num);
				if (data.length() > SRTCheckInput.INPUTSRT.length()){
					SharedPrinterFields.Lotteria.setLotteryCode(data.substring(SRTCheckInput.INPUTSRT.length()));
				}
				else {
					//Transazione.nuovaTransazione();						// ???
					//GdONEData.statusTogdONE("main.state", "RtRefund");	// ???
					RTTxnType.setRefundTrx();
				}
				
				return;
			}
		}
		
		public void RTVoid(String data) {
			
			if (!SRTCheckInput.checkInput(data, false)) {
				MessageBox.showMessage("WrongSequence", null, MessageBox.OK);
				return;
			}
			
			if (SRTPrinterExtension.isPRT()) {
				if (Extra.isDeniedPostVoid()) {
					System.out.println("RTVoid - funzionalita' disabilitata");
					MessageBox.showMessage("WrongSequence", null, MessageBox.OK);
					return;
				}
				
				boolean isVoidable = false;
				
				String cassa = Sprint.f("%02d", data.substring(SRTCheckInput.INPUTRT.indexOf(SRTCheckInput.CC), SRTCheckInput.INPUTRT.indexOf(SRTCheckInput.CC)+SRTCheckInput.CC.length()));
				String repz = Sprint.f("%04d", data.substring(SRTCheckInput.INPUTRT.indexOf(SRTCheckInput.ZZZZ), SRTCheckInput.INPUTRT.indexOf(SRTCheckInput.ZZZZ)+SRTCheckInput.ZZZZ.length()));
				String num = Sprint.f("%04d", data.substring(SRTCheckInput.INPUTRT.indexOf(SRTCheckInput.NNNN), SRTCheckInput.INPUTRT.indexOf(SRTCheckInput.NNNN)+SRTCheckInput.NNNN.length()));
				BasicDicoData date = new BasicDicoData (data,
						SRTCheckInput.INPUTRT.indexOf(SRTCheckInput.DD),
						SRTCheckInput.INPUTRT.indexOf(SRTCheckInput.DD)+SRTCheckInput.DD.length()+SRTCheckInput.MM.length()+SRTCheckInput.YY.length(),
														false);
				String printerid = "";
				if (data.length() < SRTCheckInput.INPUTRT.length()) {
					printerid = SharedPrinterFields.RTPrinterId;
					String parte1 = data.substring(SRTCheckInput.INPUTRT.indexOf(SRTCheckInput.CC), SRTCheckInput.INPUTRT.indexOf(SRTCheckInput.NNNN)+SRTCheckInput.NNNN.length());
					String parte2 = data.substring(SRTCheckInput.INPUTRT.indexOf(SRTCheckInput.NNNN)+SRTCheckInput.NNNN.length());
					data = parte1+printerid+parte2;
				}
				else {
					printerid = data.substring(SRTCheckInput.INPUTRT.indexOf(SRTCheckInput.MMMMMMMMMMM), SRTCheckInput.INPUTRT.indexOf(SRTCheckInput.MMMMMMMMMMM)+SRTCheckInput.MMMMMMMMMMM.length());
				}
				System.out.println("RTVoid - cassa = <"+cassa+"> repz = <"+repz+"> num = <"+num+"> data = <"+date.toString()+"> printerid = <"+printerid+">");
				
				if (!SRTCheckInput.chkValidDate(date.toString())) {
					System.out.println("RTVoid - funzionalita' fuori tempo massimo");
					MessageBox.showMessage("WrongSequence", null, MessageBox.OK);
					return;
				}

				// TEMPORANEO fino a quando non si implementerà il postVoid su printer diversa 
				if (!printerid.equalsIgnoreCase(SharedPrinterFields.RTPrinterId)) {
					System.out.println("RTVoid - funzionalita' disabilitata");
					MessageBox.showMessage("WrongSequence", null, MessageBox.OK);
					return;
				}
				// TEMPORANEO fino a quando non si implementerà il postVoid su printer diversa 
				
				TxnHeader txnHeader = SetVoidTrx.getTxnheadertovoid();
				if(txnHeader == null){
					System.out.println("RTVoid - txnHeader = null");
					MessageBox.showMessage("WrongSequence", null, MessageBox.OK);
					return;
				}

				try {
					VoidCommands voidcmd = new VoidCommands();
					isVoidable = voidcmd.isVoidableDocument(repz, num, date.toString(), printerid);
				} catch (JposException e) {
					System.out.println("RTVoid - e:"+e.getMessage());
					isVoidable = false;
				}
				
				if (!isVoidable){
					System.out.println("RTVoid - isVoidable="+isVoidable);
					MessageBox.showMessage("WrongSequence", null, MessageBox.OK);
					return;
				}
				
				SharedPrinterFields.Lotteria.setLotteryTill(Integer.parseInt(cassa));
				if (data.length() > SRTCheckInput.INPUTRT.length()){
					SharedPrinterFields.Lotteria.setLotteryCode(data.substring(SRTCheckInput.INPUTRT.length()));
				}
				SharedPrinterFields.Lotteria.setLotteryMF(printerid);
				
				try {
					SMTKCommands.SMTKsetVoidReceiptType();
					VoidCommands voidcmd = new VoidCommands();
					if (!voidcmd.VoidDocument(repz, num, date.toString(), printerid))
						MessageBox.showMessage("WrongSequence", null, MessageBox.OK);
					else {
						SMTKCommands.Base64_Ticket(txnHeader.getTransactionNumber(), true);
						SMTKCommands.Smart_Ticket(txnHeader.getTransactionNumber(), true);
//						setPrelevaDenaro(VoidTrx(SetVoidTrx.getTxnheadertovoid()));	// aggiorna il flag Voided della transazione sul db		// ???
					}
				} catch (JposException e) {
					System.out.println("RTVoid - e:"+e.getMessage());
				}
				
				return;
			}
			
			if (SRTPrinterExtension.isSRT()) {
				boolean isVoidable = false;
				int trxnum = 0;
				
				String cassa = Sprint.f("%02d", data.substring(SRTCheckInput.INPUT.indexOf(SRTCheckInput.CC), SRTCheckInput.INPUT.indexOf(SRTCheckInput.CC)+SRTCheckInput.CC.length()));
				String repz = Sprint.f("%05d", data.substring(SRTCheckInput.INPUT.indexOf(SRTCheckInput.ZZZZ), SRTCheckInput.INPUT.indexOf(SRTCheckInput.ZZZZ)+SRTCheckInput.ZZZZ.length()));
				String num = Sprint.f("%04d", data.substring(SRTCheckInput.INPUT.indexOf(SRTCheckInput.NNNN), SRTCheckInput.INPUT.indexOf(SRTCheckInput.NNNN)+SRTCheckInput.NNNN.length()));
				String serverid = "";
				if (data.length() < SRTCheckInput.INPUT.length()) {
					serverid = DummyServerRT.SRTServerID;
					String parte1 = data.substring(SRTCheckInput.INPUT.indexOf(SRTCheckInput.CC), SRTCheckInput.INPUT.indexOf(SRTCheckInput.NNNN)+SRTCheckInput.NNNN.length());
					String parte2 = data.substring(SRTCheckInput.INPUT.indexOf(SRTCheckInput.NNNN)+SRTCheckInput.NNNN.length());
					data = parte1+serverid+parte2;
				}
				else {
					serverid = data.substring(SRTCheckInput.INPUT.indexOf(SRTCheckInput.MMMMMMMMMMM), SRTCheckInput.INPUT.indexOf(SRTCheckInput.MMMMMMMMMMM)+SRTCheckInput.MMMMMMMMMMM.length());
				}
				System.out.println("RTVoid - cassa = <"+cassa+"> repz = <"+repz+"> num = <"+num+"> serverid = <"+serverid+">");
	
				String filename = rtsTrxBuilder.storerecallticket.Default.getRtsOutputPrefix() + "." + repz + "." + num + "." + "*";
				String[] rsh = RunShellScriptPoli20.runScript(true, "cd "+rtsTrxBuilder.storerecallticket.Default.getRtsStorePath()+"; find . -name "+filename);
				if ((rsh == null) || (rsh.length == 0)){
					System.out.println("RTVoid - <"+filename+"> non esistente");
					MessageBox.showMessage("WrongSequence", null, MessageBox.OK);
					return;
				}
				String source = rtsTrxBuilder.storerecallticket.Default.getRtsStorePath()+rsh[0];
				String destin = rtsTrxBuilder.storerecallticket.Default.getExchangeRtsName();
				System.out.println("RTVoid - source = <"+source+"> destin = <"+destin+">");
				
				try {
					trxnum = Integer.parseInt(source.substring(source.lastIndexOf(".")+1));
				} catch (NumberFormatException e) {
					System.out.println("RTVoid - scontrino gia' annullato - trxnum="+source.substring(source.lastIndexOf(".")+1));
					MessageBox.showMessage("WrongSequence", null, MessageBox.OK);
					return;
				}
				
				TxnHeader txnHeader = SetVoidTrx.getTxnheadertovoid();
				if(txnHeader == null){
					System.out.println("RTVoid - txnHeader = null");
					MessageBox.showMessage("WrongSequence", null, MessageBox.OK);
					return;
				}
				
				String txndate = Sprint.f("%02d%02d%d", txnHeader.getStartDateTime().getDay(), txnHeader.getStartDateTime().getMonth()+1, txnHeader.getStartDateTime().getYear()+1900);
				if (!SRTCheckInput.chkValidDate(txndate)) {
					System.out.println("RTVoid - funzionalita' fuori tempo massimo");
					MessageBox.showMessage("WrongSequence", null, MessageBox.OK);
					return;
				}
				
				File f = new File(source);
				if (f.exists() == false){
					System.out.println("RTVoid - <"+source+"> non esistente");
					MessageBox.showMessage("WrongSequence", null, MessageBox.OK);
					return;
				}
				
				repz = Sprint.f("%04d", data.substring(SRTCheckInput.INPUT.indexOf(SRTCheckInput.ZZZZ), SRTCheckInput.INPUT.indexOf(SRTCheckInput.ZZZZ)+SRTCheckInput.ZZZZ.length()));
				try {
					isVoidable = DummyServerRT.pleaseDoReportReceipt(repz, num, f.getCanonicalPath(), trxnum);
				} catch (IOException e) {
					System.out.println("RTVoid - e:"+e.getMessage());
				}
				
				if (!isVoidable){
					System.out.println("RTVoid - isVoidable="+isVoidable);
					MessageBox.showMessage("WrongSequence", null, MessageBox.OK);
					return;
				}
				
				if (!Files.copyFile(source, destin)){
					MessageBox.showMessage("WrongSequence", null, MessageBox.OK);
					return;
				}
				
				SharedPrinterFields.Lotteria.setLotteryTill(Integer.parseInt(cassa));
				if (data.length() > SRTCheckInput.INPUT.length()){
					SharedPrinterFields.Lotteria.setLotteryCode(data.substring(SRTCheckInput.INPUT.length()));
				}
				
				Date date = new Date(f.lastModified());
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				RTConsts.INTESTAZIONE4 = repz+"-"+num+" del "+sdf.format(date);
				
				RTTxnType.setVoidTrx();
				SetVoidTrx.setVoidTrx(source, txnHeader, trxnum);
				
				DummyServerRT.ReadVoided();		// legge i dati dello scontrino da annullare
				
	//			R3printersSRT.PrintVoided();	// stampa lo scontrino annullato
				repz = Sprint.f("%05d", data.substring(SRTCheckInput.INPUT.indexOf(SRTCheckInput.ZZZZ), SRTCheckInput.INPUT.indexOf(SRTCheckInput.ZZZZ)+SRTCheckInput.ZZZZ.length()));
				String toprint = DummyServerRT.PrintVoided(cassa, repz, num);	// stampa lo scontrino annullato
				try {
					pleasePrintFiscalReceipt(toprint);
				} catch (Exception e1) {
					System.out.println("RTVoid - e:"+e1.getMessage());
				}
				
				try {
					SMTKCommands.SMTKsetVoidReceiptType();
					SMTKCommands.Base64_Ticket(txnHeader.getTransactionNumber(), true);
					SMTKCommands.Smart_Ticket(txnHeader.getTransactionNumber(), true);
				} catch (JposException e) {
					System.out.println("RTVoid - e:"+e.getMessage());
				}
				
				RTTxnType.setSaleTrx();
				
				SetVoidTrx.resetVoidTrx();
				
				return;
			}
		}
		
		static void pleasePrintFiscalReceipt(String filename) throws Exception
		{
			boolean lnfm = false;

			lnfm = SRTPrinterExtension.isLikeNonFiscalMode();
			SRTPrinterExtension.setLikeNonFiscalMode(true);
			
			PrinterCommands prtcmd = new PrinterCommands();
			prtcmd.beginFiscalReceipt(false);
			
			prtcmd.printNormal(jpos.FiscalPrinterConst.FPTR_S_RECEIPT, " ");
			prtcmd.printNormal(jpos.FiscalPrinterConst.FPTR_S_RECEIPT, " ");
			
			File FILE = new File(filename);
			FileInputStream fstream = new FileInputStream(FILE);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line;
			boolean doprint = false;
			while ((line = br.readLine()) != null)
			{
				if (line.startsWith(RTConsts.DESCRIZIONE))
					doprint = true;
				
				if ((!doprint) ||
					(line.startsWith(SharedPrinterFields.Lotteria.getLotteryTag())))
			    	continue;
			    
				prtcmd.printNormal(jpos.FiscalPrinterConst.FPTR_S_RECEIPT, line.length() > RTConsts.setMAXLNGHOFLENGTH() ? line.substring(0, RTConsts.setMAXLNGHOFLENGTH()) : line);
			}	
			in.close();
			
			prtcmd.printNormal(jpos.FiscalPrinterConst.FPTR_S_RECEIPT, " ");
			prtcmd.printNormal(jpos.FiscalPrinterConst.FPTR_S_RECEIPT, " ");
			prtcmd.printNormal(jpos.FiscalPrinterConst.FPTR_S_RECEIPT, " ");
			
			prtcmd.endFiscalReceipt(false);
			
			SRTPrinterExtension.setLikeNonFiscalMode(lnfm);
		}
		
		static void pleasePrintFiscalReceipt() throws Exception
		{
			//String flag;
			//int station;
			String message = null;
			String description = null;
			long price = 0;
			int quantity = 0;
			int vatInfo = 0;
			long unitPrice;
			String fullDescription = null;
			int adjustmentType = 0;
			long amount = 0;
			long total = 0;
			long payment = 0;
			long adjustment = 0;
			int id = 0;
			int data[] = new int[1];
			Object object;
			
			PrinterCommands prtcmd = new PrinterCommands();
			
			SAXBuilder builder = new SAXBuilder();
			FileInputStream fis = new FileInputStream(DummyServerRT.XMLfilePath);
			Document document = (Document) builder.build(fis);
//			Element rootNode = document.getRootElement().getChild(RECEIPT).getChild(PRINTERFISCALRECEIPT);
			Element rootNode = document.getRootElement();
			
			List list = rootNode.getChildren();
			for (int i = 0; i < list.size(); i++)
			{
				Element node = (Element) list.get(i);
				
				String cmd = node.getName();
				System.out.println("Xml4SRT - pleasePrint - cmd = "+cmd);
				if (cmd.equalsIgnoreCase(Xml4SRT.BEGINFISCALRECEIPT))
				{
					//flag = node.getAttributeValue("flag");
					//System.out.println(flag);
					try {
						prtcmd.beginFiscalReceipt(false);
					} catch (JposException e) {
						System.out.println(Xml4SRT.BEGINFISCALRECEIPT+": "+e.toString());
					}
				}
				//if (cmd.equalsIgnoreCase("beginNonFiscal"))
				//{
				//	try {
				//		beginNonFiscal();
				//	} catch (JposException e) {
				//		System.out.println("beginNonFiscal: "+e.toString());
				//	}
				//}
				if (cmd.equalsIgnoreCase(Xml4SRT.ENDFISCALRECEIPT))
				{
					//flag = node.getAttributeValue("flag");
					//System.out.println(flag);
					try {
						prtcmd.endFiscalReceipt(false);
					} catch (JposException e) {
						System.out.println(Xml4SRT.ENDFISCALRECEIPT+": "+e.toString());
					}
				}
				//if (cmd.equalsIgnoreCase("endNonFiscal"))
				//{
				//	try {
				//		endNonFiscal();
				//	} catch (JposException e) {
				//		System.out.println("endNonFiscal: "+e.toString());
				//	}
				//}
				//if (cmd.equalsIgnoreCase("printNormal"))
				//{
				//	station = Integer.parseInt(node.getAttributeValue("station"));
				//	message = node.getAttributeValue("message");
				//	System.out.println(""+station);
				//	System.out.println(message);
				//	try {
				//		printNormal(station, message);
				//	} catch (JposException e) {
				//		System.out.println("printNormal: "+e.toString());
				//	}
				//}
				if (cmd.equalsIgnoreCase(Xml4SRT.PRINTRECITEM))
				{
					description = node.getAttributeValue(Xml4SRT.DESCRIPTION);
					//price = Long.parseLong(node.getAttributeValue("price"));
					//quantity = Integer.parseInt(node.getAttributeValue(QUANTITY));
					quantity = (int)(Double.parseDouble(node.getAttributeValue(Xml4SRT.QUANTITY)));
					vatInfo = Integer.parseInt(node.getAttributeValue(Xml4SRT.VATID));
					unitPrice = Long.parseLong(""+(int)(Double.parseDouble(node.getAttributeValue(Xml4SRT.UNITPRICE).replace(',', '.'))*10000));
					//fullDescription = node.getAttributeValue("fullDescription");
					price = unitPrice;
					fullDescription = description;
					System.out.println(description);
					System.out.println("Xml4SRT - pleasePrint - "+price);
					System.out.println("Xml4SRT - pleasePrint - "+quantity);
					System.out.println("Xml4SRT - pleasePrint - "+vatInfo);
					System.out.println("Xml4SRT - pleasePrint - "+unitPrice);
					System.out.println("Xml4SRT - pleasePrint - "+fullDescription);
					try {
						prtcmd.printRecItem(description, price, quantity, vatInfo, unitPrice, fullDescription);
					} catch (JposException e) {
						System.out.println(Xml4SRT.PRINTRECITEM+": "+e.toString());
					}
				}
				if (cmd.equalsIgnoreCase(Xml4SRT.PRINTRECITEMADJUSTMENT))
				{
					adjustmentType = Integer.parseInt(node.getAttributeValue(Xml4SRT.ADJUSTMENTTYPE));
					description = node.getAttributeValue(Xml4SRT.DESCRIPTION);
					amount = Long.parseLong(node.getAttributeValue(Xml4SRT.AMOUNT));
					vatInfo = Integer.parseInt(node.getAttributeValue(Xml4SRT.VATID));
					System.out.println("Xml4SRT - pleasePrint - "+adjustmentType);
					System.out.println("Xml4SRT - pleasePrint - "+description);
					System.out.println("Xml4SRT - pleasePrint - "+amount);
					System.out.println("Xml4SRT - pleasePrint - "+vatInfo);
					try {
						prtcmd.printRecItemAdjustment(adjustmentType, description, amount, vatInfo);
					} catch (JposException e) {
						System.out.println(Xml4SRT.PRINTRECITEMADJUSTMENT+": "+e.toString());
					}
				}
				if (cmd.equalsIgnoreCase(Xml4SRT.PRINTRECSUBTOTAL))
				{
					amount = Long.parseLong(node.getAttributeValue(Xml4SRT.AMOUNT));
					System.out.println("Xml4SRT - pleasePrint - "+amount);
					try {
						prtcmd.printRecSubtotal(amount);
					} catch (JposException e) {
						System.out.println(Xml4SRT.PRINTRECSUBTOTAL+": "+e.toString());
					}
				}
				if (cmd.equalsIgnoreCase(Xml4SRT.PRINTRECMESSAGE))
				{
					message = node.getAttributeValue(Xml4SRT.MESSAGE);
					System.out.println("Xml4SRT - pleasePrint - "+message);
					try {
						prtcmd.printRecMessage(message);
					} catch (JposException e) {
						System.out.println(Xml4SRT.PRINTRECMESSAGE+": "+e.toString());
					}
				}
				if (cmd.equalsIgnoreCase(Xml4SRT.PRINTRECSUBTOTALADJUSTMENT))
				{
					adjustmentType = Integer.parseInt(node.getAttributeValue(Xml4SRT.ADJUSTMENTTYPE));
					description = node.getAttributeValue(Xml4SRT.DESCRIPTION);
					amount = Long.parseLong(""+(int)(Double.parseDouble(node.getAttributeValue(Xml4SRT.AMOUNT))*10000));
					vatInfo = Integer.parseInt(node.getAttributeValue(Xml4SRT.VATID));
					//System.out.println("Xml4SRT - pleasePrint - "+adjustmentType);
					//System.out.println("Xml4SRT - pleasePrint - "+description);
					//System.out.println("Xml4SRT - pleasePrint - "+amount);
					//System.out.println("Xml4SRT - pleasePrint - "+vatInfo);
					try {
						if (SRTPrinterExtension.isSRT())
							prtcmd.printRecSubtotalAdjustment(adjustmentType, "", amount);
						
				        String out = prtcmd.buildSubtotalAdjustment ( description, amount );
				        prtcmd.printNormal(jpos.FiscalPrinterConst.FPTR_S_RECEIPT,out);
					} catch (JposException e) {
						System.out.println(Xml4SRT.PRINTRECSUBTOTALADJUSTMENT+": "+e.toString());
					}
			    	HardTotals.doSubtotalAdjustment(amount);
				}
				if (cmd.equalsIgnoreCase(Xml4SRT.PRINTRECTOTAL))
				{
					total = Long.parseLong(""+(int)(Double.parseDouble(node.getAttributeValue(Xml4SRT.RECEIPTAMOUNT).replace(',', '.'))*10000));
					payment = Long.parseLong(""+(int)(Double.parseDouble(node.getAttributeValue(Xml4SRT.PAYMENT).replace(',', '.'))*10000));
					description = node.getAttributeValue(Xml4SRT.DESCRIPTION);
					System.out.println("Xml4SRT - pleasePrint - "+total);
					System.out.println("Xml4SRT - pleasePrint - "+payment);
					System.out.println("Xml4SRT - pleasePrint - "+description);
					try {
						prtcmd.printRecTotal(total, payment, description);
					} catch (JposException e) {
						System.out.println(Xml4SRT.PRINTRECTOTAL+": "+e.toString());
					}
				}
				if (cmd.equalsIgnoreCase(Xml4SRT.PRINTRECVOID))
				{
					description = node.getAttributeValue(Xml4SRT.DESCRIPTION);
					System.out.println("Xml4SRT - pleasePrint - "+description);
					try {
						prtcmd.printRecVoid(description);
					} catch (JposException e) {
						System.out.println(Xml4SRT.PRINTRECVOID+": "+e.toString());
					}
				}
				if (cmd.equalsIgnoreCase(Xml4SRT.PRINTRECITEMVOID))
				{
					description = node.getAttributeValue(Xml4SRT.DESCRIPTION);
					//amount = Long.parseLong(node.getAttributeValue("amount"));
					amount = Long.parseLong(""+(int)(Double.parseDouble(node.getAttributeValue(Xml4SRT.UNITPRICE))*10000));
					//quantity = Integer.parseInt(node.getAttributeValue(QUANTITY));
					quantity = Integer.parseInt(""+(int)(Double.parseDouble(node.getAttributeValue(Xml4SRT.QUANTITY))*1000));
					//adjustmentType = Integer.parseInt(node.getAttributeValue("adjustmentType"));
					//adjustment = Long.parseLong(node.getAttributeValue("adjustment"));
					adjustmentType = 1;
					adjustment = amount;
					vatInfo = Integer.parseInt(node.getAttributeValue(Xml4SRT.VATID));
					System.out.println("Xml4SRT - pleasePrint - "+description);
					System.out.println("Xml4SRT - pleasePrint - "+amount);
					System.out.println("Xml4SRT - pleasePrint - "+quantity);
					System.out.println("Xml4SRT - pleasePrint - "+adjustmentType);
					System.out.println("Xml4SRT - pleasePrint - "+adjustment);
					System.out.println("Xml4SRT - pleasePrint - "+vatInfo);
					try {
						prtcmd.printRecVoidItem(description, amount, quantity, adjustmentType, adjustment, vatInfo);
					} catch (JposException e) {
						System.out.println(Xml4SRT.PRINTRECITEMVOID+": "+e.toString());
					}
				}
				if (cmd.equalsIgnoreCase(Xml4SRT.PRINTRECREFUNDVOID))
				{
					description = Xml4SRT.CorrezioneReso;
					quantity = 1;
					unitPrice = amount;
					price = unitPrice;
					fullDescription = description;
					System.out.println("Xml4SRT - pleasePrint - "+description);
					System.out.println("Xml4SRT - pleasePrint - "+price);
					System.out.println("Xml4SRT - pleasePrint - "+quantity);
					System.out.println("Xml4SRT - pleasePrint - "+vatInfo);
					System.out.println("Xml4SRT - pleasePrint - "+unitPrice);
					System.out.println("Xml4SRT - pleasePrint - "+fullDescription);
					try {
						prtcmd.printRecItem(description, price, quantity, vatInfo, unitPrice, fullDescription);
					} catch (JposException e) {
						System.out.println(Xml4SRT.PRINTRECREFUNDVOID+": "+e.toString());
					}
				}
				//if (cmd.equalsIgnoreCase("resetPrinter"))
				//{
				//	try {
				//		resetPrinter();
				//	} catch (JposException e) {
				//		System.out.println("resetPrinter: "+e.toString());
				//	}
				//}
				if (cmd.equalsIgnoreCase("directIO"))
				{
					id = Integer.parseInt(node.getAttributeValue(Xml4SRT.ID));
					data[0] = Integer.parseInt(node.getAttributeValue(Xml4SRT.DATA));
					object = node.getAttributeValue(Xml4SRT.OBJECT);
					try {
						prtcmd.directIO(id, data, object);
					} catch (JposException e) {
						System.out.println("directIO: "+e.toString());
					}
				}
				if (cmd.equalsIgnoreCase(Xml4SRT.PRINTRECREFUND))
				{
					description = node.getAttributeValue(Xml4SRT.DESCRIPTION);
					amount = Long.parseLong(""+(int)(Double.parseDouble(node.getAttributeValue(Xml4SRT.UNITPRICE))*10000));
					vatInfo = Integer.parseInt(node.getAttributeValue(Xml4SRT.VATID));
					System.out.println("Xml4SRT - pleasePrint - "+description);
					System.out.println("Xml4SRT - pleasePrint - "+amount);
					System.out.println("Xml4SRT - pleasePrint - "+vatInfo);
					try {
						prtcmd.printRecRefund(description, amount, vatInfo);
					} catch (JposException e) {
						System.out.println(Xml4SRT.PRINTRECREFUND+": "+e.toString());
					}
				}
				//if (cmd.equalsIgnoreCase("printZReport"))
				//{
				//	try {
				//		printZReport();
				//	} catch (JposException e) {
				//		System.out.println("printZReport: "+e.toString());
				//	}
				//}
			}
		}
		
		public void setPaperSavingMode()
		{
			if (SharedPrinterFields.isfwRT2disabled())
				return;
			
			if (SRTPrinterExtension.isPRT())
			{
				if (PrinterType.isEpsonModel()) {
					if (PaperSavingProperties.getPrinterMode() != null && PaperSavingProperties.getPrinterMode().isEmpty() == false) {
						String mode = PaperSavingProperties.getPrinterMode();
						
						String risparmiocarta = Sprint.f("%03d", mode);
						StringBuffer sbcmd = new StringBuffer("18"+risparmiocarta);
						System.out.println("setPaperSavingMode - sbcmd = "+sbcmd.toString());
				      	fiscalPrinterDriver.executeRTDirectIo(4015, 0, sbcmd);
						System.out.println("setPaperSavingMode - sbcmd = "+sbcmd.toString());
					}
				}
			}
		}		

		public void setRoundingMode(int mode)
		{
			if (SharedPrinterFields.isfwRT2disabled())
				return;
			
			System.out.println("RT2 - setRoundingMode - mode = "+mode);
			
			if (SRTPrinterExtension.isPRT())
			{
				if (PrinterType.isEpsonModel()) {
					String Kind = Sprint.f("%03d", mode);
					StringBuffer sbcmd = new StringBuffer("27"+Kind);
			      	fiscalPrinterDriver.executeRTDirectIo(4015, 0, sbcmd);
					System.out.println("RT2 - setRoundingMode - sbcmd = "+sbcmd.toString());
			      	
					getRoundingMode();
				}
				if (PrinterType.isRCHPrintFModel()) {
					StringBuffer key = new StringBuffer(SharedPrinterFields.KEY_SRV);
					fiscalPrinterDriver.executeRTDirectIo(0, 0, key);
		
					int Kind = mode;
					StringBuffer sbcmd = new StringBuffer(">C928/&8/$"+Kind);
					fiscalPrinterDriver.executeRTDirectIo(0, 0, sbcmd);
		
					key = new StringBuffer(SharedPrinterFields.KEY_REG);
					fiscalPrinterDriver.executeRTDirectIo(0, 0, key);
					
					if (PrinterType.isRchPrintFModel()) {
						if ((mode == RTConsts.FULLROUNDING) || (mode == RTConsts.NEGATIVEROUNDING)) {
							if (fw < 8.3) {
								enabledLowerRoundedPay = enableLowerRoundedPay(0);
								enabledLowerRoundedPay = -1;
							}
							else {
								enabledLowerRoundedPay = enableLowerRoundedPay(1);
							}
						}
						else
							enabledLowerRoundedPay = enableLowerRoundedPay(0);
						System.out.println("RT2 - setRoundingMode - enabledLowerRoundedPay = "+enabledLowerRoundedPay);
					}
				}
			}
			
			RTConsts.setCURRENTROUNDING(mode);
		}
		
		private String getRoundingMode()
		{
			String Kind = Sprint.f("%03d", RTConsts.ROUNDINGDISABLE);
			
			if (SharedPrinterFields.isfwRT2enabled())
			{
				if (PrinterType.isEpsonModel()) {
					StringBuffer sbcmd = new StringBuffer("27");
			      	fiscalPrinterDriver.executeRTDirectIo(4215, 0, sbcmd);
					System.out.println("RT2 - getRoundingMode - sbcmd = "+sbcmd.toString());
			      	
			      	Kind = sbcmd.toString().substring(2, 5);
				}
			}
	      	
			System.out.println("RT2 - getRoundingMode - Kind = "+Kind);
			return (Kind);
		}
		
		private int enableLowerRoundedPay(int mode)
		{
			int ret = -1;
			
			if (SharedPrinterFields.isfwRT2disabled())
				return ret;
			
			System.out.println("RT2 - enableLowerRoundedPay - mode = "+mode);
			
			if (PrinterType.isRchPrintFModel()) {
				StringBuffer sbcmd = new StringBuffer(">C928/&11/$"+mode);
				ret = fiscalPrinterDriver.executeRTDirectIo(0, 0, sbcmd);
			}
			
			System.out.println("RT2 - enableLowerRoundedPay - ret = "+ret);
			return ret;
		}

}

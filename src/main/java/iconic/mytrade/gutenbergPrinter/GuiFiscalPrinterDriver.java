package iconic.mytrade.gutenbergPrinter;

import java.util.GregorianCalendar;

import iconic.mytrade.gutenberg.jpos.linedisplay.service.OperatorDisplay;
import iconic.mytrade.gutenberg.jpos.printer.service.PrinterInfo;
import iconic.mytrade.gutenberg.jpos.printer.service.R3define;
import iconic.mytrade.gutenberg.jpos.printer.service.SmartTicket;
import rtsTrxBuilder.hardTotals.HardTotals;
import iconic.mytrade.gutenberg.jpos.printer.service.properties.SRTPrinterExtension;
import iconic.mytrade.gutenberg.jpos.printer.service.properties.SmartTicketProperties;
import iconic.mytrade.gutenberg.jpos.printer.utils.Sprint;
import iconic.mytrade.gutenberg.jpos.printer.utils.String13Fix;
import iconic.mytrade.gutenbergGuiPrinter.PrinterGUI;
import iconic.mytrade.gutenbergPrinter.eftpos.EftPos;
import iconic.mytrade.gutenbergPrinter.ej.FiscalEJFile;
import jpos.JposException;
import jpos.events.ErrorListener;
import jpos.events.OutputCompleteListener;
import jpos.events.StatusUpdateListener;

public class GuiFiscalPrinterDriver extends FiscalPrinterDriver implements jpos.FiscalPrinterControl17 {

//	int getOpenTimeout() {
//		return 0;
//	}
//	
//	public double doLoad(int PrinterModel, String PrinterName) {
//	    double fw = 0;
//	    
//		setRTModel(SRTPrinterExtension.isPRT());
//		setSRTModel(SRTPrinterExtension.isSRT());
//		
//		if (isSRTModel() || isRTModel())
//		{
//			HardTotals.init();
//		}
//		
//		PrinterInfo.SavePrinterInfo("Model", ""+PrinterModel);
//		
//		System.out.println ( "OPEN CON PRINTER <"+PrinterModel+">");
//		
////		try
////		{
////			System.out.println ( "Prima di Open ["+PrinterType.getPrinterJavaPosModel()+"]" );
////			if ( PrinterType.getPrinterJavaPosModel() == PrinterType.JAVAPOS113 )
////				fiscalPrinter = (jpos.FiscalPrinterControl113)new FiscalPrinter();
////			else
////				fiscalPrinter = (jpos.FiscalPrinterControl17)new FiscalPrinter();
////			fiscalPrinter.addStatusUpdateListener(this);
////			
////			fiscalPrinter.open(PrinterName);
////			
////			System.out.println ( "Prima di Claim" );
////			fiscalPrinter.claim(1000);
////		}
////		catch ( jpos.JposException e )
////		{
////			System.out.println ( "Printer Exception <"+e.toString()+">");
////			return;
////		}
//		OperatorDisplay.pleaseDisplay (R3define.PRINTERVERIFY);
////		System.out.println ("FP-1");
////	    while ( true ) 
////	    {
////	    	System.out.println ("FP-2");
////    		setNoReactionStatus ( pleaseCheck() );
////	    	System.out.println ("FP-3");
////    		if ( getNoReactionStatus () == false ) 
////	    	{
////	    		System.out.println ("FP-4");
////	    		break;
////	    	}
////	    	System.out.println ("FP-5");
////	    	pleaseBeep(100,10);
////	    	System.out.println ("FP-6");
////	    }
////		System.out.println ("FP-7");
//		OperatorDisplay.pleaseDisplay (R3define.PRINTERREADY);
//		
//	    SharedPrinterFields.fiscalEJ = new FiscalEJFile ();
//	    SharedPrinterFields.fiscalEJ.open("uk.co.datafit.wincor.system.device.FiscalEJFile", null);
//	    SharedPrinterFields.fiscalEJ.setDeviceEnabled(true);
//	    
////		if (PrinterType.isDieboldRTOneModel()) {
////			try {
////				fiscalPrinter.resetPrinter();
////			} catch (JposException e) {
////				System.out.println ( "Printer Exception <"+e.toString()+">");
////			}
////		}
//		
////	    if (isRTModel())
////	    {
////	    	int rtstatus = RT_KO;
////	    	
////	    	while (rtstatus < RT_OK)
////	    	{
////	    		try {
////					rtstatus = checkRTStatus();
////				} catch (JposException e1) {
////					System.out.println ( "Printer Exception <"+e1.toString()+">");
////				}
////	    		if (rtstatus  < RT_OK){
//////	    			if (isEpsonModel() && (rtstatus == RT_OLDFILESTOSEND))	// avanti lo stesso
//////	    				break;
////	    			OperatorDisplay.pleaseDisplay ( PRINTERRTOFF );
////	    		}
////	    		else
////	    			break;
////	    		
////	    		try {
////					Thread.sleep(10000);
////				} catch (InterruptedException e) {
////				}
////	    	}
////	    	
////	    	if (rtstatus == RT_PRESERVICE)
////	    		setRTModel(false);
////	    	
////	    	if (rtstatus == RT_NWP && isExpiredCertificate() == false) {
////	    		System.out.println("checkRTStatus - ATTENZIONE --------------------------------------");
////	    		System.out.println("checkRTStatus - FORZATO REPORT Z");
////	    		System.out.println("checkRTStatus - ATTENZIONE --------------------------------------");
////	    		try {
////					fiscalPrinter.printZReport();
////				} catch (JposException e) {
////					System.out.println ( "Printer Exception <"+e.toString()+">");
////				}
////	    	}
////	    	
////			PrinterInfo.SavePrinterInfo("RT FW Build Number", fwBuildNumber);
////	    }
//	    
////	    if (isRTModel())
////	    {
////	    	while (RTPrinterId == null)
////	    	{
////	    		try {
////					RTPrinterId = getPrinterId();
////				} catch (JposException e1) {
////					RTPrinterId = null;
////					System.out.println ( "Printer Exception <"+e1.toString()+">");
////				}
////	    		if (RTPrinterId == null){
////	    			OperatorDisplay.pleaseDisplay ( PRINTERRTOFF );
////	    		}
////	    		else {
////	    			PrinterInfo.SavePrinterInfo("PrinterId", RTPrinterId);
////	    			PrinterInfo.SavePrinterInfo("Printer Description", getPrinterDesc(RTPrinterId));
////	    			break;
////	    		}
////	    		
////	    		try {
////					Thread.sleep(5000);
////				} catch (InterruptedException e) {
////				}
////	    	}
////	    }
//	    SharedPrinterFields.RTPrinterId = "99MEX024625";
//	    
////		int[] ai = new int[1];
////		String[] as = new String[1];
////		getData(jpos.FiscalPrinterConst.FPTR_GD_FIRMWARE, ai, as);
////        System.out.println ("RetailCube-R3printers printer FW : "+as[0]);
////
////        PrinterInfo.SavePrinterInfo("FW", as[0]);
//        
////		if (isRTModel())
////		{
////			if (PrinterType.isEpsonModel()) {
////				String f = "fiscal_"+
////						   (as[0].substring(0, as[0].indexOf("."))).trim()+(as[0].substring(as[0].indexOf(".")+1)).trim()+
////						   "_"+RTPrinterId.substring(2, 5)
////						   +"_"+Sprint.f("%04d", fwBuildNumber)
////						   +"_";
////				
////				boolean tested = CheckFw.inList(f);
////				if (!tested) {
////					System.out.println("\n---------------------------------------------------------------------------------------------");
////					System.out.println("ATTENZIONE - FIRMWARE EPSON NON CERTIFICATO DA RETEX : "+f);
////					System.out.println("---------------------------------------------------------------------------------------------\n");
////				}
////				PrinterInfo.SavePrinterInfo("Tested FW", String.valueOf(tested));
////			}
////		}
//        
////        String newS = String13Fix.cleandoublestr(as[0]);
////		as[0] = newS;
//		
////	    if (PrinterType.isEpsonModel()) {
////	    	if (!epsonClass4){
////				int fw = Integer.parseInt((as[0].substring(0, as[0].indexOf("."))).trim());
////				epsonClass4 = (fw >= 4 ? true : false);
////		        System.out.println ("RetailCube-R3printers printer fw = "+fw+" - epsonClass4 = "+epsonClass4);
////	    	}
////	    	fw = Double.parseDouble(as[0].trim());
////			System.out.println("RT2 - fw : "+fw);
////	    	setfwLotteryenabled(fw >= getLotteryfw());
////	    	SharedPrinterFields.setfwRT2enabled(fw >= getRT2fw());
////	    	setfwSMTKenabled(fw >= getSMTKfw());
////	    	setfwILotteryenabled(fw >= getILotteryfw());
////	    }
////	    if (PrinterType.isRCHPrintFModel()) {
////	    	fw = Double.parseDouble((as[0].substring(0, as[0].lastIndexOf((int)'.'))).trim());
////			System.out.println("RT2 - fw : "+fw);
////	    	setfwLotteryenabled(fw >= getLotteryfw());
////	    	SharedPrinterFields.setfwRT2enabled(fw >= getRT2fw());
////	    	setfwSMTKenabled(fw >= getSMTKfw());
////	    	setfwILotteryenabled(fw >= getILotteryfw());
////	    }
//    	setfwLotteryenabled(isRTModel());
//    	setfwRT2enabled(isRTModel());
//    	//setfwSMTKenabled(isRTModel());
//    	setfwILotteryenabled(isRTModel());
//	    
////	    if (isRTModel())
////	    {
////    		setLocalAccessControl();
////    		setFidelityOption(1);
////    		setAppendixOption(1,0);
////	    	if (PrinterType.isEpsonModel()) {
////	    		SharedPrinterFields.VAT_N4_Dept = "20";
////	    	}
////	    	if (PrinterType.isRCHPrintFModel()) {
////	    		SharedPrinterFields.VAT_N4_Dept = "40";
////	    	}
////    		DicoTaxObject.setBASE_SERVICES_DEPT(Integer.parseInt(SharedPrinterFields.VAT_N4_Dept)+1);
////    		Printer_IPAddress = getPrinterIpAdd();
////			PrinterInfo.SavePrinterInfo("IPAddress", Printer_IPAddress);
////	    }
//		SharedPrinterFields.Printer_IPAddress = "123.456.789.012";
//	    
////		LogPrinterLevel(RTPrinterId, fw, isfwLotteryenabled(), SharedPrinterFields.isfwRT2enabled(), isfwSMTKenabled(), isfwILotteryenabled());
//		PrinterInfo.LogPrinterInfo();
//	    
//		if(PrinterGUI.isWindowOpen(PrinterGUI.getFrame()) == false) {
//			PrinterGUI.createWindow();
//			PrinterGUI.setTitle(PrinterName);
//		}
//		
//		PrinterGUI.addText("-- open --"+PrinterName);
//		
//		return fw;
//	}
//	
//	public int getActualCurrency() throws JposException {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//	public String getAdditionalHeader() throws JposException {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	public String getAdditionalTrailer() throws JposException {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	public boolean getCapAdditionalHeader() throws JposException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public boolean getCapAdditionalTrailer() throws JposException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public boolean getCapChangeDue() throws JposException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public boolean getCapEmptyReceiptIsVoidable() throws JposException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public boolean getCapFiscalReceiptStation() throws JposException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public boolean getCapFiscalReceiptType() throws JposException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public boolean getCapMultiContractor() throws JposException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public boolean getCapOnlyVoidLastItem() throws JposException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public boolean getCapPackageAdjustment() throws JposException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public boolean getCapPostPreLine() throws JposException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public boolean getCapSetCurrency() throws JposException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public boolean getCapTotalizerType() throws JposException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public String getChangeDue() throws JposException {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	public int getContractorId() throws JposException {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//	public int getDateType() throws JposException {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//	public int getFiscalReceiptStation() throws JposException {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//	public int getFiscalReceiptType() throws JposException {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//	public int getMessageType() throws JposException {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//	public String getPostLine() throws JposException {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	public String getPreLine() throws JposException {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	public int getTotalizerType() throws JposException {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//	public void printRecCash(long arg0) throws JposException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void printRecItemFuel(String arg0, long arg1, int arg2, int arg3, long arg4, String arg5, long arg6,
//			String arg7) throws JposException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void printRecItemFuelVoid(String arg0, long arg1, int arg2, long arg3) throws JposException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void printRecPackageAdjustVoid(int arg0, String arg1) throws JposException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void printRecPackageAdjustment(int arg0, String arg1, String arg2) throws JposException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void printRecRefundVoid(String arg0, long arg1, int arg2) throws JposException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void printRecSubtotalAdjustVoid(int arg0, long arg1) throws JposException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void printRecTaxID(String arg0) throws JposException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void setAdditionalHeader(String arg0) throws JposException {
//		if(PrinterGUI.isWindowOpen(PrinterGUI.getFrame()) == false) {
//			PrinterGUI.createWindow();			
//		}
//		
//		PrinterGUI.addText("-- setAdditionalHeader --"+arg0);	
//	}
//
//	public void setAdditionalTrailer(String arg0) throws JposException {
//		if(PrinterGUI.isWindowOpen(PrinterGUI.getFrame()) == false) {
//			PrinterGUI.createWindow();			
//		}
//		
//		PrinterGUI.addText("-- setAdditionalTrailer --"+arg0);	
//	}
//
//	public void setChangeDue(String arg0) throws JposException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void setContractorId(int arg0) throws JposException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void setCurrency(int arg0) throws JposException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void setDateType(int arg0) throws JposException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void setFiscalReceiptStation(int arg0) throws JposException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void setFiscalReceiptType(int arg0) throws JposException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void setMessageType(int arg0) throws JposException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void setPostLine(String arg0) throws JposException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void setPreLine(String arg0) throws JposException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void setTotalizerType(int arg0) throws JposException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void addDirectIOListener(DirectIOListener arg0) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void addErrorListener(ErrorListener arg0) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void addOutputCompleteListener(OutputCompleteListener arg0) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void addStatusUpdateListener(StatusUpdateListener arg0) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void beginFiscalDocument(int arg0) throws JposException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void beginFiscalReceipt(boolean arg0) throws JposException {
//		if(PrinterGUI.isWindowOpen(PrinterGUI.getFrame()) == false) {
//			PrinterGUI.createWindow();			
//		}
//		
//		PrinterGUI.addText(" ");	
//		PrinterGUI.addText("-- beginFiscalReceipt --");	
//	}
//
//	public void beginFixedOutput(int arg0, int arg1) throws JposException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void beginInsertion(int arg0) throws JposException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void beginItemList(int arg0) throws JposException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void beginNonFiscal() throws JposException {
//		if(PrinterGUI.isWindowOpen(PrinterGUI.getFrame()) == false) {
//			PrinterGUI.createWindow();			
//		}
//		
//		PrinterGUI.addText(" ");	
//		PrinterGUI.addText("-- beginNonFiscal --");	
//	}
//
//	public void beginRemoval(int arg0) throws JposException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void beginTraining() throws JposException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void clearError() throws JposException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void clearOutput() throws JposException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void endFiscalDocument() throws JposException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void endFiscalReceipt(boolean arg0) throws JposException {
//		if(PrinterGUI.isWindowOpen(PrinterGUI.getFrame()) == false) {
//			PrinterGUI.createWindow();			
//		}
//		
//		PrinterGUI.addText("-- endFiscalReceipt --");	
//		PrinterGUI.addText(" ");	
//	}
//
//	public void endFixedOutput() throws JposException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void endInsertion() throws JposException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void endItemList() throws JposException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void endNonFiscal() throws JposException {
//		if(PrinterGUI.isWindowOpen(PrinterGUI.getFrame()) == false) {
//			PrinterGUI.createWindow();			
//		}
//		
//		PrinterGUI.addText("-- endNonFiscal --");	
//		PrinterGUI.addText(" ");	
//	}
//
//	public void endRemoval() throws JposException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void endTraining() throws JposException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public int getAmountDecimalPlace() throws JposException {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//	public boolean getAsyncMode() throws JposException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public boolean getCapAdditionalLines() throws JposException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public boolean getCapAmountAdjustment() throws JposException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public boolean getCapAmountNotPaid() throws JposException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public boolean getCapCheckTotal() throws JposException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public boolean getCapCoverSensor() throws JposException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public boolean getCapDoubleWidth() throws JposException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public boolean getCapDuplicateReceipt() throws JposException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public boolean getCapFixedOutput() throws JposException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public boolean getCapHasVatTable() throws JposException {
//		// TODO Auto-generated method stub
//		return true;
//	}
//
//	public boolean getCapIndependentHeader() throws JposException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public boolean getCapItemList() throws JposException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public boolean getCapJrnEmptySensor() throws JposException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public boolean getCapJrnNearEndSensor() throws JposException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public boolean getCapJrnPresent() throws JposException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public boolean getCapNonFiscalMode() throws JposException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public boolean getCapOrderAdjustmentFirst() throws JposException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public boolean getCapPercentAdjustment() throws JposException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public boolean getCapPositiveAdjustment() throws JposException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public boolean getCapPowerLossReport() throws JposException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public int getCapPowerReporting() throws JposException {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//	public boolean getCapPredefinedPaymentLines() throws JposException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public boolean getCapRecEmptySensor() throws JposException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public boolean getCapRecNearEndSensor() throws JposException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public boolean getCapRecPresent() throws JposException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public boolean getCapReceiptNotPaid() throws JposException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public boolean getCapRemainingFiscalMemory() throws JposException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public boolean getCapReservedWord() throws JposException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public boolean getCapSetHeader() throws JposException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public boolean getCapSetPOSID() throws JposException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public boolean getCapSetStoreFiscalID() throws JposException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public boolean getCapSetTrailer() throws JposException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public boolean getCapSetVatTable() throws JposException {
//		// TODO Auto-generated method stub
//		return true;
//	}
//
//	public boolean getCapSlpEmptySensor() throws JposException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public boolean getCapSlpFiscalDocument() throws JposException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public boolean getCapSlpFullSlip() throws JposException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public boolean getCapSlpNearEndSensor() throws JposException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public boolean getCapSlpPresent() throws JposException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public boolean getCapSlpValidation() throws JposException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public boolean getCapSubAmountAdjustment() throws JposException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public boolean getCapSubPercentAdjustment() throws JposException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public boolean getCapSubtotal() throws JposException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public boolean getCapTrainingMode() throws JposException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public boolean getCapValidateJournal() throws JposException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public boolean getCapXReport() throws JposException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public boolean getCheckTotal() throws JposException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public int getCountryCode() throws JposException {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//	public boolean getCoverOpen() throws JposException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public void getData(int arg0, int[] arg1, String[] arg2) {
//		if(PrinterGUI.isWindowOpen(PrinterGUI.getFrame()) == false) {
//			PrinterGUI.createWindow();			
//		}
//		
//		PrinterGUI.addText("-- getData --"+arg0);
//		
//		//arg2[0] = "10.0.2";
//		arg2[0] = "001";
//	}
//
//	public void getDate(String[] arg0) throws JposException {
//		String anno;
//		String mese;
//		String giorno;
//		GregorianCalendar gc = new GregorianCalendar();
//		anno = Sprint.f("%04d",gc.get(gc.YEAR));
//		mese = Sprint.f("%02d",gc.get(gc.MONTH)+1);
//		giorno = Sprint.f("%02d",gc.get(gc.DATE));
//		arg0[0] = giorno+mese+anno;
//	}
//
//	public boolean getDayOpened() throws JposException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public int getDescriptionLength() throws JposException {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//	public boolean getDuplicateReceipt() throws JposException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public int getErrorLevel() throws JposException {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//	public int getErrorOutID() throws JposException {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//	public int getErrorState() throws JposException {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//	public int getErrorStation() throws JposException {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//	public String getErrorString() throws JposException {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	public boolean getFlagWhenIdle() throws JposException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public boolean getJrnEmpty() throws JposException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public boolean getJrnNearEnd() throws JposException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public int getMessageLength() throws JposException {
//		return 100;
//	}
//
//	public int getNumHeaderLines() throws JposException {
//		return 6;
//	}
//
//	public int getNumTrailerLines() throws JposException {
//		return 6;
//	}
//
//	public int getNumVatRates() throws JposException {
//		// TODO Auto-generated method stub
//		return 9;
//	}
//
//	public int getOutputID() throws JposException {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//	public int getPowerNotify() throws JposException {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//	public int getPowerState() throws JposException {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//	public String getPredefinedPaymentLines() throws JposException {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	public int getPrinterState() throws JposException {
//		if(PrinterGUI.isWindowOpen(PrinterGUI.getFrame()) == false) {
//			PrinterGUI.createWindow();			
//		}
//		
//		PrinterGUI.addText("-- getPrinterState --");	
//		return 0;
//	}
//
//	public int getQuantityDecimalPlaces() throws JposException {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//	public int getQuantityLength() throws JposException {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//	public boolean getRecEmpty() throws JposException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public boolean getRecNearEnd() throws JposException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public boolean getSlipNearEnd() throws JposException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public int getRemainingFiscalMemory() throws JposException {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//	public String getReservedWord() throws JposException {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	public int getSlipSelection() throws JposException {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//	public boolean getSlpEmpty() throws JposException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public boolean getSlpNearEnd() throws JposException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public void getTotalizer(int arg0, int arg1, String[] arg2) throws JposException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public boolean getTrainingModeActive() throws JposException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public void getVatEntry(int arg0, int arg1, int[] arg2) throws JposException {
//		if (arg0 == 1)
//			arg2[0] = 400;
//		if (arg0 == 2)
//			arg2[0] = 500;
//		if (arg0 == 3)
//			arg2[0] = 1000;
//		if (arg0 == 4)
//			arg2[0] = 2000;
//		if (arg0 == 5)
//			arg2[0] = 2100;
//		if (arg0 == 6)
//			arg2[0] = 2200;
//	}
//
//	public void printDuplicateReceipt() throws JposException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void printFiscalDocumentLine(String arg0) throws JposException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void printFixedOutput(int arg0, int arg1, String arg2) throws JposException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void printNormal(int arg0, String arg1) throws JposException {
//		if(PrinterGUI.isWindowOpen(PrinterGUI.getFrame()) == false) {
//			PrinterGUI.createWindow();			
//		}
//		
//		PrinterGUI.addText("-- printNormal --"+arg0+"--"+arg1);	
//	}
//
//	public void printPeriodicTotalsReport(String arg0, String arg1) throws JposException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void printPowerLossReport() throws JposException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void printRecItem(String arg0, long arg1, int arg2, int arg3, long arg4, String arg5) throws JposException {
//		if(PrinterGUI.isWindowOpen(PrinterGUI.getFrame()) == false) {
//			PrinterGUI.createWindow();			
//		}
//		
//		PrinterGUI.addText("-- printRecItem --"+arg0+"--"+arg1+"--"+arg2+"--"+arg3+"--"+arg4+"--"+arg5);	
//	}
//
//	public void printRecItemAdjustment(int arg0, String arg1, long arg2, int arg3) throws JposException {
//		if(PrinterGUI.isWindowOpen(PrinterGUI.getFrame()) == false) {
//			PrinterGUI.createWindow();			
//		}
//		
//		PrinterGUI.addText("-- printRecItemAdjustment --"+arg0+"--"+arg1+"--"+arg2+"--"+arg3);	
//	}
//
//	public void printRecMessage(String arg0) throws JposException {
//		if(PrinterGUI.isWindowOpen(PrinterGUI.getFrame()) == false) {
//			PrinterGUI.createWindow();			
//		}
//		
//		PrinterGUI.addText("-- printRecMessage --"+arg0);	
//	}
//
//	public void printRecNotPaid(String arg0, long arg1) throws JposException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void printRecRefund(String arg0, long arg1, int arg2) throws JposException {
//		if(PrinterGUI.isWindowOpen(PrinterGUI.getFrame()) == false) {
//			PrinterGUI.createWindow();			
//		}
//		
//		PrinterGUI.addText("-- printRecRefund --"+arg0+"--"+arg1+"--"+arg2);	
//	}
//
//	public void printRecSubtotal(long arg0) throws JposException {
//		if(PrinterGUI.isWindowOpen(PrinterGUI.getFrame()) == false) {
//			PrinterGUI.createWindow();			
//		}
//		
//		PrinterGUI.addText("-- printRecSubtotal --"+arg0);	
//	}
//
//	public void printRecSubtotalAdjustment(int arg0, String arg1, long arg2) throws JposException {
//		if(PrinterGUI.isWindowOpen(PrinterGUI.getFrame()) == false) {
//			PrinterGUI.createWindow();			
//		}
//		
//		PrinterGUI.addText("-- printRecSubtotalAdjustment --"+arg0+"--"+arg1+"--"+arg2);	
//	}
//
//	public void printRecTotal(long arg0, long arg1, String arg2) throws JposException {
//		if(PrinterGUI.isWindowOpen(PrinterGUI.getFrame()) == false) {
//			PrinterGUI.createWindow();			
//		}
//		
//		PrinterGUI.addText("-- printRecTotal --"+arg0+"--"+arg1+"--"+arg2);	
//	}
//
//	public void printRecVoid(String arg0) throws JposException {
//		if(PrinterGUI.isWindowOpen(PrinterGUI.getFrame()) == false) {
//			PrinterGUI.createWindow();			
//		}
//		
//		PrinterGUI.addText("-- printRecVoid --"+arg0);	
//	}
//
//	public void printRecVoidItem(String arg0, long arg1, int arg2, int arg3, long arg4, int arg5) throws JposException {
//		if(PrinterGUI.isWindowOpen(PrinterGUI.getFrame()) == false) {
//			PrinterGUI.createWindow();			
//		}
//		
//		PrinterGUI.addText("-- printRecVoidItem --"+arg0+"--"+arg1+"--"+arg2+"--"+arg3+"--"+arg4+"--"+arg5);	
//	}
//
//	public void printReport(int arg0, String arg1, String arg2) throws JposException {
//		if(PrinterGUI.isWindowOpen(PrinterGUI.getFrame()) == false) {
//			PrinterGUI.createWindow();			
//		}
//		
//		PrinterGUI.addText("-- printReport --"+arg0+"--"+arg1+"--"+arg2);	
//	}
//
//	public void printXReport() throws JposException {
//		if(PrinterGUI.isWindowOpen(PrinterGUI.getFrame()) == false) {
//			PrinterGUI.createWindow();			
//		}
//		
//		PrinterGUI.addText("-- printXReport --");	
//	}
//
//	public void printZReport() throws JposException {
//		if(PrinterGUI.isWindowOpen(PrinterGUI.getFrame()) == false) {
//			PrinterGUI.createWindow();			
//		}
//		
//		PrinterGUI.addText("-- printZReport --");	
//	}
//
//	public void removeDirectIOListener(DirectIOListener arg0) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void removeErrorListener(ErrorListener arg0) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void removeOutputCompleteListener(OutputCompleteListener arg0) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void removeStatusUpdateListener(StatusUpdateListener arg0) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void resetPrinter() throws JposException {
//		if(PrinterGUI.isWindowOpen(PrinterGUI.getFrame()) == false) {
//			PrinterGUI.createWindow();			
//		}
//		
//		PrinterGUI.addText("-- resetPrinter --");	
//	}
//
//	public void setAsyncMode(boolean arg0) throws JposException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void setCheckTotal(boolean arg0) throws JposException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void setDate(String arg0) throws JposException {
//		if(PrinterGUI.isWindowOpen(PrinterGUI.getFrame()) == false) {
//			PrinterGUI.createWindow();			
//		}
//		
//		PrinterGUI.addText("-- setDate --"+arg0);	
//	}
//
//	public void setDuplicateReceipt(boolean arg0) throws JposException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void setFlagWhenIdle(boolean arg0) throws JposException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void setHeaderLine(int arg0, String arg1, boolean arg2) throws JposException {
//		if(PrinterGUI.isWindowOpen(PrinterGUI.getFrame()) == false) {
//			PrinterGUI.createWindow();			
//		}
//		
//		PrinterGUI.addText("-- setHeaderLine --"+arg0+"--"+arg1+"--"+arg2);	
//	}
//
//	public void setPOSID(String arg0, String arg1) throws JposException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void setPowerNotify(int arg0) throws JposException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void setSlipSelection(int arg0) throws JposException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void setStoreFiscalID(String arg0) throws JposException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void setTrailerLine(int arg0, String arg1, boolean arg2) throws JposException {
//		if(PrinterGUI.isWindowOpen(PrinterGUI.getFrame()) == false) {
//			PrinterGUI.createWindow();			
//		}
//		
//		PrinterGUI.addText("-- setTrailerLine --"+arg0+"--"+arg1+"--"+arg2);	
//	}
//
//	public void setVatTable() throws JposException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void setVatValue(int arg0, String arg1) throws JposException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void verifyItem(String arg0, int arg1) throws JposException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void checkHealth(int arg0) throws JposException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void claim(int arg0) throws JposException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void close() throws JposException {
//		if(PrinterGUI.isWindowOpen(PrinterGUI.getFrame()) == false) {
//			PrinterGUI.createWindow();			
//		}
//		
//		PrinterGUI.addText("-- close --");	
//	}
//
//	public void directIO(int arg0, int[] arg1, StringBuffer arg2) throws JposException {
//		if(PrinterGUI.isWindowOpen(PrinterGUI.getFrame()) == false) {
//			PrinterGUI.createWindow();			
//		}
//		
//		PrinterGUI.addText("-- directIO --"+arg0+"--"+arg1[0]+"--"+arg2);
//		arg2.append("1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890");
//	}
//
//	public void directIO(int arg0, int[] arg1, String arg2) throws JposException {
//		if(PrinterGUI.isWindowOpen(PrinterGUI.getFrame()) == false) {
//			PrinterGUI.createWindow();			
//		}
//		
//		PrinterGUI.addText("-- directIO --"+arg0+"--"+arg1[0]+"--"+arg2);
//		arg2=arg2+"1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890";
//	}
//
//	public void directIO(int arg0, int[] arg1, String[] arg2) throws JposException {
//		if(PrinterGUI.isWindowOpen(PrinterGUI.getFrame()) == false) {
//			PrinterGUI.createWindow();			
//		}
//		
//		PrinterGUI.addText("-- directIO --"+arg0+"--"+arg1[0]+"--"+arg2[0]);
//		arg2[0]=arg2[0]+"1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890";
//	}
//
//	public void directIO(int arg0, int[] arg1, Object arg2) throws JposException {
//		if(PrinterGUI.isWindowOpen(PrinterGUI.getFrame()) == false) {
//			PrinterGUI.createWindow();			
//		}
//		
//		PrinterGUI.addText("-- directIO --"+arg0+"--"+arg1[0]+"--"+arg2);
//	}
//
//	public String getCheckHealthText() throws JposException {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	public boolean getClaimed() throws JposException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public String getDeviceControlDescription() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	public int getDeviceControlVersion() {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//	public boolean getDeviceEnabled() throws JposException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public String getDeviceServiceDescription() throws JposException {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	public int getDeviceServiceVersion() throws JposException {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//	public boolean getFreezeEvents() throws JposException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public String getPhysicalDeviceDescription() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	public String getPhysicalDeviceName() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	public int getState() {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//	public void open(String arg0) throws JposException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void release() throws JposException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void setDeviceEnabled(boolean arg0) throws JposException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void setFreezeEvents(boolean arg0) throws JposException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public int getAmountDecimalPlaces() throws JposException {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//	boolean checkCurrentTicketTotal ( String Pr, long In ) {
//		return true;
//	}
//	
//	static boolean checkCurrentDailyTotal ( String In ) {
//		return true;
//	}
//	
//	static boolean checkCurrentDailyTotalRounded ( String In, double rounding ) {
//		return true;
//	}
//	
//    public static int executeRTDirectIo (int Command, int pData, StringBuffer bjct)
//    {
//		if(PrinterGUI.isWindowOpen(PrinterGUI.getFrame()) == false) {
//			PrinterGUI.createWindow();			
//		}
//		
//		PrinterGUI.addText("-- RTdirectIO --"+Command+"--"+pData+"--"+bjct);
//		if (Command == 1134)
//			((StringBuffer)bjct).append("1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890");
//		else if (Command == 9218)
//			((StringBuffer)bjct).setLength(1);
//		else if (Command == 4237)
//			((StringBuffer)bjct).append("123456789012345678901");
//		else if (Command == 9205)
//			((StringBuffer)bjct).replace(0, 0, "0");
////		else if (Command == 1078)
////			((StringBuffer)bjct).replace(0, 1, "5");	// reso ko
//		else if (Command == 4205)
//			((StringBuffer)bjct).append("1111");		// regime di ventilazione iva
//		else
//			((StringBuffer)bjct).append("12345678");
//		
//		return 0;
//    }
//    
//	String GetILotteryDate()
//	{
//		String result = "160624";
//		
//		return result;
//	}
//	
//	void SetILotteryDate(String date)
//	{
//	}
//	
//	int GetILotteryQRCodeSize()
//	{
//		int result = 0;
//		
//		return result;
//	}
//
//	void SetILotteryQRCodeSize(int size)
//	{
//	}
//	
//	public RTStatus getRTStatus()
//	{
//		RTStatus status = null;
//		StringBuffer op = new StringBuffer("01");
//		executeRTDirectIo(1138, 0, op);
//		status = new RTStatus(op.toString());
//		return status;
//	}
//	
//	public String checkEJStatus() {
//		return "33";
//	}
//
//	public void fwUpdate()
//	{
//	}
//    
//	public void bitmap(String filename,int width,int height,int align) //throws PrinterException
//	{
//	}
	   
}

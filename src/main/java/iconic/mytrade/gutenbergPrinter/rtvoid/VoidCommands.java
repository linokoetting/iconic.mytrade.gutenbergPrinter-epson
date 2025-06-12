package iconic.mytrade.gutenbergPrinter.rtvoid;

import iconic.mytrade.gutenberg.jpos.linedisplay.service.OperatorDisplay;
import iconic.mytrade.gutenberg.jpos.printer.service.LastTicket;
import iconic.mytrade.gutenberg.jpos.printer.service.RTTxnType;
import iconic.mytrade.gutenberg.jpos.printer.srt.DummyServerRT;
import iconic.mytrade.gutenberg.jpos.printer.srt.RTConsts;
import iconic.mytrade.gutenberg.jpos.printer.srt.Xml4SRT;
import iconic.mytrade.gutenberg.jpos.printer.utils.Sprint;
import iconic.mytrade.gutenbergPrinter.PrinterCommands;
import iconic.mytrade.gutenbergPrinter.SharedPrinterFields;
import iconic.mytrade.gutenbergPrinter.ej.ForFiscalEJFile;
import jpos.FiscalPrinterConst;
import jpos.JposException;

public class VoidCommands extends PrinterCommands {

	public boolean isVoidableDocument(String zRepId, String recId, String date, String printerId) throws JposException
	{
		try{
			StringBuffer command = new StringBuffer("2" + printerId + date + recId + zRepId);
	      	System.out.println("isVoidableDocument - command : " + command.toString());
			OperatorDisplay.printSelectedDevices("R3getPhotoDisplay3R", null, false, "OD");
			OperatorDisplay.printSelectedDevices("PleaseWait",null,false,"OD");
	      	int ret = fiscalPrinterDriver.executeRTDirectIo(9205, 0, command);
	      	System.out.println("isVoidableDocument - result : " + command.toString() + " - ret : " + ret);
			if ((ret == 0) && (command.substring(0, 1).equals("0") || command.substring(0, 1).equals("1"))){
	            System.out.println("isVoidableDocument - Document is voidable");
	            return true;
			}
	        else{
	            System.out.println("isVoidableDocument - Document is NOT voidable");
				OperatorDisplay.printSelectedDevices("R3setPhotoDisplay3R", null, false, "OD");
	            return false;
	        }
		}catch(Exception e){
			System.out.println("isVoidableDocument - Exception : " + e.getMessage());
			}
		
		return false;
    }
    
	public boolean VoidDocument(String zRepId, String recId, String date, String printerId) throws JposException
	{
		boolean reply = true;
		
		try{
			StringBuffer command = new StringBuffer("0140001VOID " + zRepId + " " + recId + " " + date + " "+ printerId);
	      	System.out.println("VoidDocument - command : " + command.toString());
	      	int ret = fiscalPrinterDriver.executeRTDirectIo(1078, 0, command);
	      	System.out.println("VoidDocument - result : " + command.toString() + " - ret : " + ret);
	      	if ((ret == -1) || (Integer.parseInt(command.toString().substring(0, 2)) < 50)){
	      		if (printerId.equalsIgnoreCase(SharedPrinterFields.RTPrinterId)) {
		      		reply = false;
		      		System.out.println("VoidDocument - Document NOT found");
	      		}
	      		else {
	      			// TEMPORANEO fino a quando non si implementerà il postVoid su printer diversa
	      			System.out.println("VoidDocument - funzionalita' disabilitata");
	      			// TEMPORANEO fino a quando non si implementerà il postVoid su printer diversa
	      		}
	      	}
	      	else{
	      		VoidDocumentToEJ(zRepId, recId, date, printerId);
	      	}
		}catch(Exception e){
      		reply = false;
			System.out.println("VoidDocument - Exception : " + e.getMessage());
			}
		
		return reply;
    }
	
	private void VoidDocumentToEJ(String zRepId, String recId, String date, String printerId) throws JposException
	{
		if (!SharedPrinterFields.isfiscalEJenabled())
			return;
		
  		String barre = "========================================";
		String[] doc = {""};
		ForFiscalEJFile.writeToFile("\n\t\t"+barre);
		
		String s = intestazione1(RTTxnType.getTypeTrx());
		if (s.length() > 0){
			ForFiscalEJFile.writeToFile("\t\t"+s.trim());
			doc = s.trim().split(" ");
		}
		s = intestazione2(Xml4SRT.VOID_TYPE);
		if (s.length() > 0)
			ForFiscalEJFile.writeToFile("\t\t"+s.trim());
		s = intestazione3(Xml4SRT.VOID_TYPE);
		if (s.length() > 0)
			ForFiscalEJFile.writeToFile("\t\t"+s.trim());
		s = zRepId+"-"+recId+" del "+
			date.toString().substring(0, 2)+"-"+date.toString().substring(2, 4)+"-"+date.toString().substring(4);
		if (s.length() > 0)
			ForFiscalEJFile.writeToFile("\t\t"+s.trim());
		
        int[] ai = new int[1];
        String[] as = new String[1];
        getData(FiscalPrinterConst.FPTR_GD_FISCAL_REC, ai, as);
        String n = as[0];
    	n = Sprint.f("%04d",Integer.parseInt(as[0])-1);
        getData(FiscalPrinterConst.FPTR_GD_Z_REPORT, ai, as);
        String z = "";
        try {
        	z = Sprint.f("%04d",Integer.parseInt(as[0])+1);
        } catch (NumberFormatException e) {
		   System.out.println("VoidDocumentToEJ - Exception : " + e.getMessage());
		   z = Sprint.f("%04d",0);
        }
        s = doc[0]+" N. "+z+"-"+n;
        ForFiscalEJFile.writeToFile("\t\t"+s);
        LastTicket.setDocnum(ALINER.substring(0, (int)((RTConsts.setMAXLNGHOFLENGTH()-s.length())/2))+s);
        
		ForFiscalEJFile.writeToFile("\t\t"+barre);
		
		DummyServerRT.CurrentReceiptNumber = n;
	}
	
}

package iconic.mytrade.gutenbergPrinter.refund;

import iconic.mytrade.gutenberg.jpos.printer.service.Extra;
import iconic.mytrade.gutenberg.jpos.printer.service.properties.PrinterType;
import iconic.mytrade.gutenbergPrinter.FiscalPrinterDriver;
import iconic.mytrade.gutenbergPrinter.PrinterCommands;
import iconic.mytrade.gutenbergPrinter.SharedPrinterFields;
import jpos.JposException;

public class RefundCommands extends PrinterCommands {

	public boolean isRefundableDocument(String zRepId, String recId, String date, String printerId, boolean freeRefund) throws JposException
    {
		if (freeRefund) {
			if (Extra.isDeniedRefund()) {
	            System.out.println("isRefundableDocument - Free refund is denied");
	            return false;
			}
            System.out.println("isRefundableDocument - Document is Free refundable");
            return true;
		}
		
		if (PrinterType.isEpsonModel())
		{
			try{
				StringBuffer command = new StringBuffer("1" + printerId + date + recId + zRepId);
		      	System.out.println("isRefundableDocument - command : " + command.toString());
				//posEngine.printSelectedDevices("R3getPhotoDisplay3R", null, false, "OD");	// ???
				//posEngine.printSelectedDevices("PleaseWait",null,false,"OD");				// ???
		      	int ret = fiscalPrinterDriver.executeRTDirectIo(9205, 0, command);
		      	System.out.println("isRefundableDocument - result : " + command.toString() + " - ret : " + ret);
				if ((ret == 0) && (command.substring(0, 1).equals("0") || command.substring(0, 1).equals("1"))){
		            System.out.println("isRefundableDocument - Document is refundable");
		            return true;
				}
		        else{
		            System.out.println("isRefundableDocument - Document is NOT refundable");
					//posEngine.printSelectedDevices("R3setPhotoDisplay3R", null, false, "OD");	// ???
		            return false;
		        }
			}catch(Exception e){
				System.out.println("isRefundableDocument - Exception : " + e.getMessage());
				}
		}
		else if (PrinterType.isRCHPrintFModel())
		{
			try{
				String mydate = date.substring(0, 4) + date.substring(6, 8);
				date = mydate;
				StringBuffer command = new StringBuffer(date + "," + Integer.parseInt(zRepId) + "," + Integer.parseInt(recId));
				if (fiscalPrinterDriver.isfwRT2enabled()) {
					command = new StringBuffer(date + "," + Integer.parseInt(zRepId) + "," + Integer.parseInt(recId) + "," + printerId + "," + "0");
			      	System.out.println("isRefundableDocument - manual refund");
				}
		      	System.out.println("isRefundableDocument - command : " + command.toString());
		      	int result = fiscalPrinterDriver.executeRTDirectIo(6000, 1, command);
		      	System.out.println("isRefundableDocument - result : " + result);
				if (result == 1){
		            System.out.println("isRefundableDocument - Document is refundable");
		            return true;
				}
		        else{
		            System.out.println("isRefundableDocument - Document is NOT refundable");
		            return false;
		        }
			}catch(Exception e){
				System.out.println("isRefundableDocument - Exception : " + e.getMessage());
				}
		}
		
		return false;
    }
	
	public boolean RefundDocument(String zRepId, String recId, String date, String printerId, boolean freeRefund) throws JposException
	{
		boolean reply = true;
		
		if (PrinterType.isEpsonModel())
		{
			if (freeRefund) {
				printerId = "00000000000";
			}
			
			try{
				StringBuffer command = new StringBuffer("0140001REFUND " + zRepId + " " + recId + " " + date + " " + printerId);
		      	System.out.println("RefundDocument - command : " + command.toString());
		      	int ret = fiscalPrinterDriver.executeRTDirectIo(1078, 0, command);
		      	System.out.println("RefundDocument - result : " + command.toString() + " - ret : " + ret);
		      	if ((ret == -1) || (Integer.parseInt(command.toString().substring(0, 2)) > 50)){
		      		reply = false;
		      		System.out.println("RefundDocument - Document already refunded");
		      	}
		      	else{
/*					fiscalPrinterDriver.beginFiscalReceipt(true);
					fiscalPrinterDriver.printRecRefund("Item Refund", 200000, 1);
					fiscalPrinterDriver.printRecTotal(200000, 200000, "001Contante");
					fiscalPrinterDriver.endFiscalReceipt(false);*/
		      	}
			}catch(Exception e){
	      		reply = false;
				System.out.println("RefundDocument - Exception : " + e.getMessage());
				fiscalPrinterDriver.resetPrinter();
			}
		}
		else if (PrinterType.isRCHPrintFModel())
		{
			if (freeRefund) {
				printerId = printerId.substring(0, 5)+"000000";
			}
			
			try{
				String mydate = date.substring(0, 4) + date.substring(6, 8);
				date = mydate;
				StringBuffer command = new StringBuffer(date + "," + Integer.parseInt(zRepId) + "," + Integer.parseInt(recId));
				if (fiscalPrinterDriver.isfwRT2enabled()) {
					command = new StringBuffer(date + "," + Integer.parseInt(zRepId) + "," + Integer.parseInt(recId) + "," + printerId + "," + "0");
					if (!freeRefund) {
						// Se fai un reso manuale si, non è necessario verificarne la rendibilità, in quanto non può ovviamente farlo
						// per cui il comando di verifica della rendibilità già apre il documento di reso, non è più necessario farlo qui
				      	System.out.println("RefundDocument - manual refund");
						return true;
					}
				}
		      	System.out.println("RefundDocument - command : " + command.toString());
		      	fiscalPrinterDriver.executeRTDirectIo(6000, 0, command);
/*			      	fiscalPrinterDriver.beginFiscalReceipt(true);
		      	fiscalPrinterDriver.printRecItem("Item Refund", 2000, 0, 1, 0, "");	
		      	fiscalPrinterDriver.printRecItem("Item Refund", 800, 0, 0, 0, "");	
		      	fiscalPrinterDriver.printRecTotal(0, 0, "Contanti");
		      	fiscalPrinterDriver.endFiscalReceipt(false);*/
			}catch(Exception e){
	      		reply = false;
				System.out.println("RefundDocument - Exception : " + e.getMessage());
				}
		}
		
		return reply;
	}
	
}

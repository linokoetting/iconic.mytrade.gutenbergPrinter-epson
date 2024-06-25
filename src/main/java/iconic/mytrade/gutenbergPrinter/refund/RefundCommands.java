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
		
		return false;
    }
	
	public boolean RefundDocument(String zRepId, String recId, String date, String printerId, boolean freeRefund) throws JposException
	{
		boolean reply = true;
		
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
		
		return reply;
	}
	
}

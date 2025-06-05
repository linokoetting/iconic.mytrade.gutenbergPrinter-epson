package iconic.mytrade.gutenbergPrinter.eftpos;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import iconic.mytrade.gutenberg.jpos.printer.service.properties.MyTradeProperties;
import iconic.mytrade.gutenberg.jpos.printer.service.properties.SRTPrinterExtension;
import iconic.mytrade.gutenberg.jpos.printer.utils.Sprint;
import iconic.mytrade.gutenbergPrinter.PrinterCommands;
import jpos.JposException;

public class EftPos extends PrinterCommands {
	
	private static String EFTAuthorizationCode = "Eft Offline";
	
	private static HashMap<String, Long> EFTStanCodes = new HashMap<String, Long>();
	
	public static String getEFTAuthorizationCode(long amount) {
//        for (Map.Entry<String, Long> entry : EFTStanCodes.entrySet()) {
//            System.out.println("getEFTAuthorizationCode - StanCode: " + entry.getKey() + ", Amount: " + entry.getValue());
//        }
	    String reply = EFTAuthorizationCode;
	    if (EFTStanCodes != null) {
	        Iterator<Map.Entry<String, Long>> iterator = EFTStanCodes.entrySet().iterator();
	        while (iterator.hasNext()) {
	            Map.Entry<String, Long> entry = iterator.next();
	            if (entry.getValue().equals(amount)) {
	                reply = entry.getKey();
	                iterator.remove();
	                break;
	            }
	        }
	    }
//        for (Map.Entry<String, Long> entry : EFTStanCodes.entrySet()) {
//            System.out.println("getEFTAuthorizationCode - StanCode: " + entry.getKey() + ", Amount: " + entry.getValue());
//        }
		System.out.println("getEFTAuthorizationCode - reply:"+reply);
	    return reply;
	}
	
	public static void setEFTAuthorizationCode(String stanCode, long amount) {
		if (EFTStanCodes != null) {
			EFTStanCodes.put(stanCode, amount);
		}
//        for (Map.Entry<String, Long> entry : EFTStanCodes.entrySet()) {
//            System.out.println("setEFTAuthorizationCode - StanCode: " + entry.getKey() + ", Amount: " + entry.getValue());
//        }
	}
	
	public static void OfflineEftSetting(String... authcodes)
	{
		String authcode = "";
		if (authcodes != null && authcodes.length > 0)
			authcode = authcodes[0];
		else
			authcode = EFTAuthorizationCode;
		
		if (!SRTPrinterExtension.isPRT())
			return;
		
		if (MyTradeProperties.isOfflineEftHandling() == false)
			return;
		
		System.out.println("OfflineEftSetting - authcode:"+authcode);
		
		// nothing to do
		
	}
	
	public static void OfflineEftHandling(long amount, String authcode)
	{
		if (MyTradeProperties.isOfflineEftHandling() == false)
			return;
		
		System.out.println("OfflineEftHandling - amount:"+amount);
		System.out.println("OfflineEftHandling - authcode:"+authcode);
		
    	String[] printerdate = new String[1];
    	try {
			fiscalPrinterDriver.getDate(printerdate);
		} catch (JposException e) {
			System.out.println("OfflineEftHandling - Exception:"+e.getMessage());
			return;
		}
    	
	  	String operator = "01";
	  	String datetime = printerdate[0].substring(0, 4) + printerdate[0].substring(6, 8) + printerdate[0].substring(8) + "00";
	  	String amn = Sprint.f("%09d", amount/100);
	  	String transcodetype = "00";	// 00 = OTHER - 01 = STAN
	  	if (!authcode.equalsIgnoreCase(EFTAuthorizationCode))
	  		transcodetype = "01";
	  	String transcode = authcode;
	  	String spare = "0000000000000000";
	  	try {
		  	int[] dt={1108};
		  	StringBuffer offlineEftPayment = new StringBuffer(operator + datetime + amn + transcodetype + transcode + spare);		
			System.out.println("OfflineEftHandling - offlineEftPayment:"+offlineEftPayment.toString());
			fiscalPrinterDriver.directIO(0, dt, offlineEftPayment);
		} catch (JposException e) {
			System.out.println("OfflineEftHandling - Exception:"+e.getMessage());
		}
	}

}

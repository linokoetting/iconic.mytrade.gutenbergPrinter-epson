package iconic.mytrade.gutenbergPrinter.rtchecks;

import iconic.mytrade.gutenberg.jpos.printer.service.properties.Lotteria;
import iconic.mytrade.gutenberg.jpos.printer.service.properties.SRTPrinterExtension;
import jpos.FiscalPrinterControl17;
import jpos.JposException;

public class RTchecks {

    static int SI = 89;
    static int NO = 78;
    static int ERR = 99;
    
	public static boolean checkSRT(FiscalPrinterControl17 fp)
	{
		int reply = NO;
		
		reply = getStatus(fp);
		
		if (reply != ERR) {
			return(updateProperties(reply == SI));
		}
		return false;
	}
	
	private static int getStatus(FiscalPrinterControl17 fp) {
		int[] icmd = {0};
		StringBuffer sbcmd = new StringBuffer("");
		
		int reply = NO;
		
		try{	
			icmd[0] = 1138;
			sbcmd = new StringBuffer("01");
			fp.directIO(0, icmd, sbcmd);
			log("getStatus - sbcmd = "+sbcmd.toString());
			if((sbcmd.length()) >= 30){
				reply = SI;
				String rtMainStatus = (sbcmd.toString()).substring(3,5);
				String rtSubStatus = (sbcmd.toString()).substring(5,7);
				if (rtMainStatus.equals("01"))
					reply = NO;
			}
			else
				reply = NO;
		}catch(JposException je){
			reply = ERR;
			log("getStatus exception = "+je.getMessage());
		}
		return reply;
	}
	
	public static boolean checkLottery(boolean lotteryFW)
	{
		return(updateLotteryProperties(lotteryFW));
	}
	
    private static boolean updateProperties(boolean propValue) {
        boolean updated = false;
        
        if (propValue != SRTPrinterExtension.isPRT()) {
        	SRTPrinterExtension.setPRT(propValue);
        	updated = true;
            log("updateProperties - updated " + "isPRT" + "=" + propValue);
        }
        
        return updated;
    }
    
    private static boolean updateLotteryProperties(boolean lotteryFW) {
		try {
			boolean updated = false;
			boolean isEnable = Lotteria.isEnable();
			if ( lotteryFW && !isEnable )
			{
				Lotteria.setEnable(lotteryFW);
				updated = true;
                log("updateLotteryProperties - updated " + "Enable" + "=" + String.valueOf(lotteryFW));
			}
			else if ( !lotteryFW && isEnable )
				log("updateLotteryProperties - (Enable) La risposta false e' inaffidabile, perfavore verificare manualmente se e' veramente false.");
			
			boolean isPrintBarcode = Lotteria.isPrintBarcode();
			if ( lotteryFW && !isPrintBarcode )
			{
				Lotteria.setPrintBarcode(lotteryFW);
				updated = true;
                log("updateLotteryProperties - updated " + "PrintBarcode" + "=" + String.valueOf(lotteryFW));
			}
			else if ( !lotteryFW && isPrintBarcode )
				log("updateLotteryProperties - (PrintBarcode) La risposta false e' inaffidabile, perfavore verificare manualmente se e' veramente false.");
			
			return updated;
		}catch (Exception e) {
            log("updateLotteryProperties exception = " + e.getMessage());
		}
		
		return false;
	}
    
	private static void log(String s)
	{
		System.out.println("RTchecks - "+s);
	}
	
}

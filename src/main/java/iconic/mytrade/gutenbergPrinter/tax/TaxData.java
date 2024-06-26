package iconic.mytrade.gutenbergPrinter.tax;

import iconic.mytrade.gutenberg.jpos.printer.service.tax.TaxInfo;
import iconic.mytrade.gutenbergPrinter.SharedPrinterFields;

public class TaxData {
	
	static final int BENI = 0;
	private static final int BENIOMAGGIO = 40;
	private static final int SERVIZI = 1;
	private static final int SERVIZIOMAGGIO = 41;
	private static final int ACCONTIBENI = 10;
	private static final int ACCONTISERVIZI = 11;
	private static final int BUONIMONOUSOBENI = 20;
	private static final int BUONIMONOUSOSERVIZI = 21;
	
	private static boolean isBeni = false;
	private static boolean isBeniOmaggio = false;
	private static boolean isServizi = false;
	private static boolean isServiziOmaggio = false;
	private static boolean isAccontiBeni = false;
	private static boolean isAccontiServizi = false;
	private static boolean isBuonoMonousoBeni = false;
	private static boolean isBuonoMonousoServizi = false;
	private static boolean isBuonoMonouso = false;
	private static boolean isAcconti = false;
	private static boolean isOmaggio = false;
	
	
	private static Object lastObj = null;
	private static int lastTaxNumber = -1;
	private static int lastType = -1;
	private static int globalRtCode = 1;
	private static int globalRtCodeRTONE = 15;
		
	private static void setTypeByTaxNumber(int taxNumber) {
		try {
			if (taxNumber == lastTaxNumber){
			  return;
			}
			lastTaxNumber = taxNumber;
			TaxInfo taxInfoObj = SharedPrinterFields.taxInfoMap.get(String.valueOf(taxNumber));
			if (taxInfoObj != null) {
				System.out.println("TaxData - setTypeByTaxNumber - taxNumber:"+taxInfoObj);
				
				isBeni = (taxInfoObj.getRt2_Type() == BENI);
				isBeniOmaggio = (taxInfoObj.getRt2_Type() == BENIOMAGGIO);
				isServizi = (taxInfoObj.getRt2_Type() == SERVIZI);
				isServiziOmaggio = (taxInfoObj.getRt2_Type() == SERVIZIOMAGGIO);
				isAccontiBeni = (taxInfoObj.getRt2_Type() == ACCONTIBENI);
				isAccontiServizi = (taxInfoObj.getRt2_Type() == ACCONTISERVIZI);
				isBuonoMonousoBeni = (taxInfoObj.getRt2_Type() == BUONIMONOUSOBENI);
				isBuonoMonousoServizi = (taxInfoObj.getRt2_Type() == BUONIMONOUSOSERVIZI);
								
				isOmaggio = (isBeniOmaggio || isServiziOmaggio);
				isBeni = (isBeni || isBeniOmaggio || isAccontiBeni || isBuonoMonousoBeni);
				isServizi = (isServizi || isServiziOmaggio || isAccontiServizi || isBuonoMonousoServizi);
				isAcconti = (isAccontiBeni || isAccontiServizi);		
				isBuonoMonouso = (isBuonoMonousoBeni || isBuonoMonousoServizi);
				
				lastType = taxInfoObj.getRt2_Type();
				
				System.out.println("TaxData - setTypeByTaxNumber - isBeni:"+isBeni+" isBeniOmaggio:"+isBeniOmaggio+" isServizi:"+isServizi+" isServiziOmaggio:"+isServiziOmaggio+" isAcconti:"+isAcconti+" isAccontiBeni:"+isAccontiBeni+" isAccontiServizi:"+isAccontiServizi+" isBuonoMonouso:"+isBuonoMonouso+" isBuonoMonousoBeni:"+isBuonoMonousoBeni+" isBuonoMonousoServizi:"+isBuonoMonousoServizi+" isOmaggio:"+isOmaggio);
				
			}else {
				resetType();
			}
		}catch (Exception e) {
			System.out.println("TaxData - setTypeByTaxNumber - errore:"+e);
		}
	}
	
	public static int getBeni() {
		return BENI;
	}

	public static boolean isServizi(int taxNumber) {
		setTypeByTaxNumber(taxNumber);
		return isServizi;
	}
	
	public static boolean isAcconti(int taxNumber) {
		setTypeByTaxNumber(taxNumber);
		return isAcconti;
	}
	
	public static boolean isOmaggio(int taxNumber) {
		setTypeByTaxNumber(taxNumber);
		return isOmaggio;
	}
	
	public static boolean isBuonoMonouso(int taxNumber) {
		setTypeByTaxNumber(taxNumber);
		return isBuonoMonouso;
	}
	
	private static void resetType() {
		isBeni = false;
		isBeniOmaggio = false;
		isServizi = false;
		isServiziOmaggio = false;
		isAcconti = false;
		isOmaggio = false;
		isAcconti = false;
		isAccontiBeni = false;
		isAccontiServizi = false;
		isBuonoMonouso = false;
		isBuonoMonousoBeni = false;
		isBuonoMonousoServizi = false;
		lastObj = null;
		lastTaxNumber = -1;		
		lastType = -1;
	}
	
}

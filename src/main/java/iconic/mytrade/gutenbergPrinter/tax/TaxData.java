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
	
/*	private static ITaxInfo[] taxInfoTable = null;
	private static HashMap<String, ITaxInfo> taxInfoMap = new HashMap<String, ITaxInfo>();
	private static HashMap<Double, Integer> mapRateRtCode = new HashMap<Double, Integer>();
	private static HashMap<Double, Integer> mapRateRtCodeS = new HashMap<Double, Integer>();*/
	
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
		
	
/*	public static int getRTTaxCode(double rate, int rt_TaxCode, int rt_Type) {
		try {
			IDebug.debug.debug(IDebug.DEBUG_NORMAL,"TaxData"," getRTTaxCode - in rate:"+rate+" rt_TaxCode:"+rt_TaxCode+" rt_Type:"+rt_Type);
			if (rt_TaxCode == 15) {
				IDebug.debug.debug(IDebug.DEBUG_NORMAL,"TaxData"," IvaVentilata: true");
				IDebug.debug.debug(IDebug.DEBUG_NORMAL,"TaxData"," getRTTaxCode - mapRateRtCode:"+mapRateRtCode);
				IDebug.debug.debug(IDebug.DEBUG_NORMAL,"TaxData"," getRTTaxCode - mapRateRtCodeS:"+mapRateRtCodeS);
				if (isServizio(rt_Type) && R3printers.isDieboldRTOneModel() && R3printers.isfwRT2enabled()) {
					if (mapRateRtCodeS.containsKey(rate)) {
						rt_TaxCode = mapRateRtCodeS.get(rate);
					}else {
						if (globalRtCode == 10) {
							IDebug.debug.debug(IDebug.DEBUG_NORMAL,"TaxData"," getRTTaxCode - siamo fuori mappa slot esauriti RT ONE.");
							rt_TaxCode = -1;
						}else {
							rt_TaxCode = globalRtCode;
							mapRateRtCodeS.put(rate, rt_TaxCode);
							globalRtCode = globalRtCode + 1;
						}
					}
				}
				else {
					if (mapRateRtCode.containsKey(rate)) {
						rt_TaxCode = mapRateRtCode.get(rate);
					}else {
						if (R3printers.isEpsonPrinterModel() || ((R3printers.isDieboldRTOneModel() || R3printers.isRchPrintFModel()) && R3printers.isfwRT2enabled() == false)) {
							if (globalRtCode == 10) {
								IDebug.debug.debug(IDebug.DEBUG_NORMAL,"TaxData"," getRTTaxCode - siamo fuori mappa slot esauriti EPSON o RT ONE non fw2 enabled.");
								rt_TaxCode = -1;
							}else {
								rt_TaxCode = globalRtCode;
								mapRateRtCode.put(rate, rt_TaxCode);
								globalRtCode = globalRtCode + 1;
							}
						}else if ((R3printers.isDieboldRTOneModel() || R3printers.isRchPrintFModel()) && R3printers.isfwRT2enabled()){
							if (globalRtCodeRTONE == 24) {
								IDebug.debug.debug(IDebug.DEBUG_NORMAL,"TaxData"," getRTTaxCode - siamo fuori mappa slot esauriti RT ONE.");
								rt_TaxCode = -1;
							}else {
								rt_TaxCode = globalRtCodeRTONE;
								mapRateRtCode.put(rate, rt_TaxCode);
								globalRtCodeRTONE = globalRtCodeRTONE + 1;
							}
						}
					}
				}
				
				if (R3printers.isEpsonPrinterModel()) {
					R3printers.setEpsonVentilata(true);
				}
			}
		}catch (Exception e) {
			IDebug.debug.debug(IDebug.DEBUG_EXCEPTION,"TaxData"," getRTTaxCode - errore:"+e);
		}
		IDebug.debug.debug(IDebug.DEBUG_NORMAL,"TaxData"," getRTTaxCode - out rt_TaxCode"+rt_TaxCode);
		return rt_TaxCode;
	}
	
	public static void loadTax(ITaxInfo[] taxInfoFetch) {
		reset();
		try {			
			 taxInfoTable = taxInfoFetch;
			 for (ITaxInfo iTaxInfo : taxInfoFetch) {
			   taxInfoMap.put(String.valueOf(iTaxInfo.getTaxNumber()), iTaxInfo);	 
			 }
		}catch (Exception e) {
			IDebug.debug.debug(IDebug.DEBUG_EXCEPTION,"TaxData"," loadTax - errore:"+e);
		}
	}
	
	public static int switchTaxToOmaggio(int taxNumber) {
		try {
			IDebug.debug.debug(IDebug.DEBUG_NORMAL,"TaxData"," switchTaxToOmaggio - taxNumber:"+taxNumber);
			setTypeByTaxNumber(taxNumber);
			IDebug.debug.debug(IDebug.DEBUG_NORMAL,"TaxData"," switchTaxToOmaggio - type:"+lastType);
			if (isOmaggio == false) {
				int typeToSearch = BENIOMAGGIO;
				if (isServizi) {
					typeToSearch = SERVIZIOMAGGIO;
				}
				IDebug.debug.debug(IDebug.DEBUG_NORMAL,"TaxData"," switchTaxToOmaggio - typeToSearch:"+typeToSearch);
				ITaxInfo taxInfoObj = taxInfoMap.get(String.valueOf(taxNumber));
				IDebug.debug.debug(IDebug.DEBUG_NORMAL,"TaxData"," switchTaxToOmaggio - taxInfoObj:"+taxInfoObj);
				if (taxInfoObj != null) {
					boolean isBeccato = false;
					for (ITaxInfo iTaxInfo : taxInfoTable) {
						//IDebug.debug.debug(IDebug.DEBUG_NORMAL,"TaxData"," switchTaxToOmaggio - iTaxInfo.getRt2Type():"+iTaxInfo.getRt2Type()+" iTaxInfo.getRate():"+iTaxInfo.getRate());
						if (iTaxInfo.getRt2Type() == typeToSearch && iTaxInfo.getRate() == taxInfoObj.getRate()) {
							IDebug.debug.debug(IDebug.DEBUG_NORMAL,"TaxData"," switchTaxToOmaggio - beccato, cambio tax da "+taxNumber+" a "+iTaxInfo.getTaxNumber());
							isBeccato = true;
							taxNumber = iTaxInfo.getTaxNumber();							
							break;
						}
					}					
					if (isBeccato == false) {
						IDebug.debug.debug(IDebug.DEBUG_NORMAL,"TaxData"," switchTaxToOmaggio - non beccato, switch impossibile, conf tabella errata non si fa niente.");
					}
				}		
			}
		}catch (Exception e) {
			IDebug.debug.debug(IDebug.DEBUG_EXCEPTION,"TaxData"," switchTaxToOmaggio - errore:"+e);
		}
		return taxNumber;
	}*/
	
	private static void setTypeByTaxNumber(int taxNumber) {
		try {
			//IDebug.debug.debug(IDebug.DEBUG_NORMAL,"TaxData"," setTypeByTaxNumber - taxNumber:"+taxNumber+" lastTaxNumber:"+lastTaxNumber);
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
	
/*	private static void setTypeByLine(ITransLine transLine) {
		try {			
				if (transLine == null) {
					resetType();
					return;
				}else if (transLine.equals(lastObj)){				  
				  return;
				}
			    lastObj = transLine;
				if (transLine instanceof IItemKnown) {
					IItemKnown itemk = (IItemKnown) transLine;
					setTypeByTaxNumber(itemk.getPlu().getSku().getTaxNumber());
				}else if (transLine instanceof IItemDepartment) {
					IItemDepartment itemDept = (IItemDepartment) transLine;
					setTypeByTaxNumber(itemDept.getLevel5().getTaxNr().getTaxMethodId());
				}else if (transLine instanceof IItemMeasured) {
					IItemMeasured itemm = (IItemMeasured) transLine;
					setTypeByTaxNumber(itemm.getPlu().getSku().getTaxNumber());
				}else {
					resetType();
				}
		}catch (Exception e) {
			IDebug.debug.debug(IDebug.DEBUG_EXCEPTION,"TaxData"," setTypeByLine - errore:"+e);
		}
	}*/
	
	public static int getBeni() {
		return BENI;
	}

/*	public static boolean isBeni(ITransLine transLine) {
		setTypeByLine(transLine);
		return isBeni;
	}
	
	public static boolean isBeni(int taxNumber) {
		setTypeByTaxNumber(taxNumber);
		return isBeni;
	}
	
	public static boolean isBeniOmaggio(ITransLine transLine) {
		setTypeByLine(transLine);
		return isBeniOmaggio;
	}
	
	public static boolean isBeniOmaggio(int taxNumber) {
		setTypeByTaxNumber(taxNumber);
		return isBeniOmaggio;
	}
	
	public static boolean isServizi(ITransLine transLine) {
		setTypeByLine(transLine);
		return isServizi;
	}*/
	
	public static boolean isServizi(int taxNumber) {
		setTypeByTaxNumber(taxNumber);
		return isServizi;
	}
	
/*	public static boolean isServiziOmaggio(ITransLine transLine) {
		setTypeByLine(transLine);
		return isServiziOmaggio;
	}
	
	public static boolean isServiziOmaggio(int taxNumber) {
		setTypeByTaxNumber(taxNumber);
		return isServiziOmaggio;
	}
	
	public static boolean isAccontiBeni(ITransLine transLine) {
		setTypeByLine(transLine);
		return isAccontiBeni;
	}
	
	public static boolean isAccontibeni(int taxNumber) {
		setTypeByTaxNumber(taxNumber);
		return isAccontiBeni;
	}
	
	public static boolean isAccontiServizi(ITransLine transLine) {
		setTypeByLine(transLine);
		return isAccontiServizi;
	}
	
	public static boolean isAccontiServizi(int taxNumber) {
		setTypeByTaxNumber(taxNumber);
		return isAccontiServizi;
	}
	
	public static boolean isBuonoMonousoBeni(ITransLine transLine) {
		setTypeByLine(transLine);
		return isBuonoMonousoBeni;
	}
	
	public static boolean isBuonoMonousoBeni(int taxNumber) {
		setTypeByTaxNumber(taxNumber);
		return isBuonoMonousoBeni;
	}
	
	public static boolean isBuonoMonousoServizi(ITransLine transLine) {
		setTypeByLine(transLine);
		return isBuonoMonousoServizi;
	}
	
	public static boolean isBuonoMonousoServizi(int taxNumber) {
		setTypeByTaxNumber(taxNumber);
		return isBuonoMonousoServizi;
	}
	
	public static boolean isAcconti(ITransLine transLine) {
		setTypeByLine(transLine);
		return isAcconti;
	}*/
	
	public static boolean isAcconti(int taxNumber) {
		setTypeByTaxNumber(taxNumber);
		return isAcconti;
	}
	
/*	public static boolean isOmaggio(ITransLine transLine) {
		setTypeByLine(transLine);
		return isOmaggio;
	}*/
	
	public static boolean isOmaggio(int taxNumber) {
		setTypeByTaxNumber(taxNumber);
		return isOmaggio;
	}
	
/*	public static boolean isBuonoMonouso(ITransLine transLine) {
		setTypeByLine(transLine);
		return isBuonoMonouso;
	}*/
	
	public static boolean isBuonoMonouso(int taxNumber) {
		setTypeByTaxNumber(taxNumber);
		return isBuonoMonouso;
	}
	
/*	private static boolean isServizio(int rtType) {
		return (rtType == SERVIZI || rtType == SERVIZIOMAGGIO || rtType == ACCONTISERVIZI || rtType == BUONIMONOUSOSERVIZI);
	}*/
	
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
	
/*	public static void reset() {
		taxInfoMap = new HashMap<String, ITaxInfo>();		
		resetType();
	}*/

}

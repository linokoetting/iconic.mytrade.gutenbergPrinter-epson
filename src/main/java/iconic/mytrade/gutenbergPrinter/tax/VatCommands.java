package iconic.mytrade.gutenbergPrinter.tax;

import java.util.HashMap;

import iconic.mytrade.gutenberg.jpos.printer.service.Extra;
import iconic.mytrade.gutenberg.jpos.printer.service.PosApp;
import iconic.mytrade.gutenberg.jpos.printer.service.VentilazioneIva;
import iconic.mytrade.gutenberg.jpos.printer.service.properties.PrinterType;
import iconic.mytrade.gutenberg.jpos.printer.service.tax.AtecoInfo;
import iconic.mytrade.gutenberg.jpos.printer.utils.Sprint;
import iconic.mytrade.gutenbergPrinter.PrinterCommands;
import iconic.mytrade.gutenbergPrinter.ateco.AtecoCommands;
import iconic.mytrade.gutenbergPrinter.tax.DicoTaxLoad;
import jpos.JposException;
import iconic.mytrade.gutenbergPrinter.RTStatus;
import iconic.mytrade.gutenbergPrinter.SharedPrinterFields;

public class VatCommands extends PrinterCommands {
	
	private boolean IvaVentilata = false;
	
	private boolean isEpsonVentilata() {
		System.out.println("isEpsonVentilata - VentilazioneIva.getIvaVentilata: "+VentilazioneIva.getIvaVentilata());
		return (VentilazioneIva.getIvaVentilata());
	}

	public void resetVATtable(){
		if (!fiscalPrinterDriver.isfwRT2enabled())
			return;
		
		int MaxVatRates = 9;
		
		if ( DicoTaxLoad.isIvaAllaPrinter() ){
			
			RTStatus status = fiscalPrinterDriver.getRTStatus();
			if(status.isDailyOpen()){
    	      	System.out.println("resetVATtable - isDailyOpen() = "+status.isDailyOpen());
				return;
			}
			
			try {
				if (!(fiscalPrinterDriver.getCapHasVatTable() && fiscalPrinterDriver.getCapSetVatTable())){
					System.out.println("resetVATtable - getCapHasVatTable: "+fiscalPrinterDriver.getCapHasVatTable());
					System.out.println("resetVATtable - getCapSetVatTable: "+fiscalPrinterDriver.getCapSetVatTable());
					return;
				}
			} catch (JposException e) {
				System.out.println("resetVATtable - error: "+e.getMessage());
				return;
			}
			
			int NumVatRates = MaxVatRates;
			try {
				NumVatRates = fiscalPrinterDriver.getNumVatRates();
				System.out.println("resetVATtable - getNumVatRates: "+NumVatRates);
			} catch (JposException e) {
				System.out.println("resetVATtable - getNumVatRates error: "+e.getMessage());
			}
			if (NumVatRates > MaxVatRates)
				NumVatRates = MaxVatRates;

			int myVATRate[] = {0};
			for(int i = 1; i <= NumVatRates; i++) {
				try {
					fiscalPrinterDriver.getVatEntry(i, 0, myVATRate) ;
					System.out.println("resetVATtable - getVatEntry VAT ID "+ i + ": " + myVATRate[0]);
				} catch (JposException e) {
					System.out.println("resetVATtable - getVatEntry error: "+e.getMessage());
				}
			}

			for(int i = 1; i <= NumVatRates; i++) {
				try {
					SetVatValue(""+i, "0000", 1);
				} catch (JposException e) {
					System.out.println("resetVATtable - setVatValue error: "+e.getMessage());
				}
			}
			
			try {
				SetVatTable();
			} catch (JposException e) {
				System.out.println("resetVATtable - setVatTable error: "+e.getMessage());
			}
			
			for(int i = 1; i <= NumVatRates; i++) {
				try {
					fiscalPrinterDriver.getVatEntry(i, 0, myVATRate) ;
					System.out.println("resetVATtable - getVatEntry VAT ID "+ i + ": " + myVATRate[0]);
				} catch (JposException e) {
					System.out.println("resetVATtable - getVatEntry error: "+e.getMessage());
				}
			}
		}
		return;
	}
	
	private void SetVatValue(String taxnumber, String vatrate, int atecoid) throws JposException {
		if (fiscalPrinterDriver.isfwRT2enabled()) {
			// usiamo directIO perchè il fw preferisce così
			
			String vatid = Sprint.f("%02d", taxnumber);
			StringBuffer sbcmd = new StringBuffer(vatid + vatrate);
			fiscalPrinterDriver.executeRTDirectIo(4005, 0, sbcmd);
			
			if ((AtecoCommands.getAtecoVI(atecoid) == 1) || (isEpsonVentilata())) {
				// regime Ventilazione Iva
				if ((AtecoCommands.getAtecoVI(atecoid) == 0) && (IvaVentilata == false))
					AtecoCommands.setATECOtable(1,1);
				IvaVentilata = true;
				sbcmd = new StringBuffer("98" + vatrate);
				fiscalPrinterDriver.executeRTDirectIo(4005, 0, sbcmd);
			}
			else {
				if (IvaVentilata == false) {
					sbcmd = new StringBuffer("97" + vatrate);
					fiscalPrinterDriver.executeRTDirectIo(4005, 0, sbcmd);
				}
			}
			return;
		}
		
		System.out.println("EPSON - setVatValue("+Integer.parseInt(taxnumber)+","+vatrate+")");
		fiscalPrinterDriver.setVatValue(Integer.parseInt(taxnumber), vatrate);
	}
	
	private void SetVatTable() throws JposException {
		if (fiscalPrinterDriver.isfwRT2enabled()) {
			// usiamo directIO perchè il fw preferisce così
			return;
		}
		
		fiscalPrinterDriver.setVatTable();
	}
	
	private void SetVatAtecoValue(String taxnumber, String vatrate, int atecoid) throws JposException {
		if (fiscalPrinterDriver.isfwRT2disabled())
			return;
	}

	public boolean setVATtable(){
		int MaxVatRates = 9;
		
		int taxinput[] = {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};
		int taxoutput[] = {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};
		boolean esito = true;
		
		DicoTaxLoad.DicoTaxLoadInit ( );
		
		if ( DicoTaxLoad.isIvaAllaPrinter() ){
			
			RTStatus status = fiscalPrinterDriver.getRTStatus();
			if(status.isDailyOpen()){
    	      	System.out.println("setVATtable - isDailyOpen() = "+status.isDailyOpen());
				return true;
			}
			
			try {
				if (!(fiscalPrinterDriver.getCapHasVatTable() && fiscalPrinterDriver.getCapSetVatTable())){
					System.out.println("setVATtable - getCapHasVatTable: "+fiscalPrinterDriver.getCapHasVatTable());
					System.out.println("setVATtable - getCapSetVatTable: "+fiscalPrinterDriver.getCapSetVatTable());
					return true;
				}
			} catch (JposException e) {
				System.out.println("setVATtable - error: "+e.getMessage());
				return true;
			}
			
			int NumVatRates = MaxVatRates;
			try {
				NumVatRates = fiscalPrinterDriver.getNumVatRates();
				System.out.println("setVATtable - getNumVatRates: "+NumVatRates);
			} catch (JposException e) {
				System.out.println("setVATtable - getNumVatRates error: "+e.getMessage());
			}
			if (NumVatRates > MaxVatRates)
				NumVatRates = MaxVatRates;

			int myVATRate[] = {0};
			for(int i = 1; i <= NumVatRates; i++) {
				try {
					fiscalPrinterDriver.getVatEntry(i, 0, myVATRate) ;
					System.out.println("setVATtable - getVatEntry VAT ID "+ i + ": " + myVATRate[0]);
				} catch (JposException e) {
					System.out.println("setVATtable - getVatEntry error: "+e.getMessage());
				}
			}

			int quellodellaprinter = 1;
			while (true) {
				
				DicoTaxObject oo = DicoTaxToPrinter.next();
				if ( oo == null ){
					break;
				}
				
				try {
					if ((!TaxData.isOmaggio(oo.getTaxnumber())) && (!TaxData.isAcconti(oo.getTaxnumber())) && (!TaxData.isBuonoMonouso(oo.getTaxnumber())))
					{
				        String VATRate = Sprint.f("%04d", oo.getTaxrate()*100);
				        
						//String salesType = Sprint.f("%02d", TaxData.isServizi(oo.getTaxnumber()) ? 1 : 0);
						String salesType = (TaxData.isServizi(oo.getTaxnumber()) ? "1" : "0");
						String salesAttribute = "00";
						String atecoIndex = (AtecoCommands.isMultiAttivita() ? Sprint.f("%02d", oo.getAtecoId()) : "00");
						String VATGroup = "";
						String description = "";
						String department = "";
						String price1 = "000000000";
						String price2 = "000000000";
						String price3 = "000000000";
						String singleItem = "0";
						String priceLimit = "999999999";
						String printGroup = "00";
						String superGruppoMerceologico = "00";
						//String fatturaUnitMeasure = " ";
						String fatturaUnitMeasure = "KG";
			            String DEPARTMENTVATGRP = "DEPARTMENT VAT GRP";
						
				        if ((Integer.parseInt(VATRate) > 0) && ((quellodellaprinter <= NumVatRates) || (TaxData.isServizi(oo.getTaxnumber())))){
							System.out.println("setVATtable - setVatValue idx="+quellodellaprinter+" VATRate="+VATRate+" getPrinterTaxnumber="+oo.getPrinterTaxnumber());
				            taxinput[Integer.parseInt(oo.getPrinterTaxnumber())] = Integer.parseInt(VATRate);
							System.out.println("setVATtable - taxinput["+Integer.parseInt(oo.getPrinterTaxnumber())+"] = "+taxinput[Integer.parseInt(oo.getPrinterTaxnumber())]);
							SetVatValue(oo.getPrinterTaxnumber(), VATRate, oo.getAtecoId());
							
							VATGroup = Sprint.f("%02d", Integer.parseInt(oo.getPrinterTaxnumber()));
				            description = DEPARTMENTVATGRP+VATGroup; // Must be exactly 20 characters
							department = Sprint.f("%02d", oo.getPrinterDeptnumber());
					        
							System.out.println("setVATtable - VATGroup: " + VATGroup+" - department: "+department+" - description: <"+description+"> - salesType: " + salesType+" - salesAttribute: "+salesAttribute+" - atecoIndex: <"+atecoIndex+">");
							SetDepartment(description,
										  VATGroup,
										  department,
										  price1,
										  price2,
										  price3,
										  singleItem,
										  priceLimit,
										  printGroup,
										  superGruppoMerceologico,
										  fatturaUnitMeasure,
										  salesType,
										  salesAttribute,
										  atecoIndex);
							quellodellaprinter++;
				        }
				        else if (Integer.parseInt(VATRate) == 0) {
							System.out.println("setVATtable - setVatValue idx="+quellodellaprinter+" VATRate="+VATRate+" getPrinterTaxnumber="+oo.getPrinterTaxnumber());
							
							VATGroup = Sprint.f("%02d", Integer.parseInt(oo.getPrinterTaxnumber()));
							if (Integer.parseInt(oo.getPrinterTaxnumber()) == SharedPrinterFields.VAT_N4_Index)
								VATGroup = Sprint.f("%02d", Integer.parseInt(SharedPrinterFields.VAT_N4_Dept));
				            description = DEPARTMENTVATGRP+VATGroup; // Must be exactly 20 characters
							department = Sprint.f("%02d", oo.getPrinterDeptnumber());
							if (oo.getPrinterDeptnumber() == SharedPrinterFields.VAT_N4_Index)
								department = Sprint.f("%02d", Integer.parseInt(SharedPrinterFields.VAT_N4_Dept));
					        
							System.out.println("setVATtable - VATGroup: " + VATGroup+" - department: "+department+" - description: <"+description+"> - salesType: " + salesType+" - salesAttribute: "+salesAttribute+" - atecoIndex: <"+atecoIndex+">");
							SetDepartment(description,
										  VATGroup,
										  department,
										  price1,
										  price2,
										  price3,
										  singleItem,
										  priceLimit,
										  printGroup,
										  superGruppoMerceologico,
										  fatturaUnitMeasure,
										  salesType,
										  salesAttribute,
										  atecoIndex);
				        }
				        else {
				        	if (isRT2On())
				        		System.out.println("setVATtable - WARNING scartato per ERRORE - getTaxnumber:"+oo.getTaxnumber()+" - type="+oo.getType()+" - getPrinterTaxnumber:"+oo.getPrinterTaxnumber()+" - getPrinterDeptnumber:"+oo.getPrinterDeptnumber());
				        }
					}
					else
					{
						System.out.println("setVATtable - scartato - getTaxnumber:"+oo.getTaxnumber()+" - type="+oo.getType()+" - getPrinterTaxnumber:"+oo.getPrinterTaxnumber()+" - getPrinterDeptnumber:"+oo.getPrinterDeptnumber());
					}
				} catch (JposException e) {
					System.out.println("setVATtable - setVatValue error: "+e.getMessage());
				}
				
				DicoTaxToPrinter.setTaxPrinterCode (Integer.parseInt(oo.getPrinterTaxnumber()));
				
			}
			
			DicoTaxToPrinter.setTaxConverted();
			
			try {
				SetVatTable();
			} catch (JposException e) {
				System.out.println("setVATtable - setVatTable error: "+e.getMessage());
			}
			
			for(int i = 1; i <= NumVatRates; i++) {
				try {
					fiscalPrinterDriver.getVatEntry(i, 0, myVATRate) ;
					System.out.println("setVATtable - getVatEntry VAT ID "+ i + ": " + myVATRate[0]);
		            taxoutput[i] = myVATRate[0];
				} catch (JposException e) {
					System.out.println("setVATtable - getVatEntry error: "+e.getMessage());
				}
			}
			if (fiscalPrinterDriver.isfwRT2enabled())
				System.out.println("setVATtable - IvaVentilata: "+IvaVentilata);
			else {
				System.out.println("setVATtable - IvaVentilata: "+"NON SIGNIFICATIVO");
				System.out.println("setVATtable - IvaVentilata: "+"CONTROLLARE LA VENTILAZIONE DELLE ALIQUOTE DIRETTAMENTE SULLA STAMPANTE");
			}
			
			for (int i=0; i<taxinput.length; i++) {
				System.out.println("setVATtable - taxinput["+i+"] = "+taxinput[i]);
				System.out.println("setVATtable - taxoutput["+i+"] = "+taxoutput[i]);
				if (taxinput[i] != -1) {
					if (taxoutput[i] != taxinput[i]) {
						esito = false;
						break;
					}
				}
			}
		}
		System.out.println("setVATtable - esito = "+esito);
		return esito;
	}
	
	private void SetDepartment(String desc, String VATGroup, String dept, String price1, String price2,
			   String price3, String singleItem, String priceLimit, String printGroup, 
			   String superGruppoMerceologico, String fatturaUnitMeasure,
			   String salesType, String salesAttribute, String atecoIndex) throws JposException {
		
		StringBuffer sbcmd = new StringBuffer("");
		
		if (fiscalPrinterDriver.isfwRT2disabled()) {
			salesType = "";
			salesAttribute = "";
			atecoIndex = "";
		}
		
		System.out.println("EPSON - " + "|" + dept + "|" + desc + "|" + price1 + "|" + price2 + "|" + price3 + "|" + singleItem + "|" + VATGroup + "|" + 
				 priceLimit + "|" + printGroup + "|" + superGruppoMerceologico + "|" + fatturaUnitMeasure + "|" + salesType + "|" + salesAttribute + "|" + atecoIndex);
		
		sbcmd = new StringBuffer(dept + desc + price1 + price2 + price3 + singleItem + VATGroup + 
						 priceLimit + printGroup + superGruppoMerceologico + fatturaUnitMeasure +
						 salesType + salesAttribute + atecoIndex);
		
		System.out.println("EPSON - executeRTDirectIo(4002,0,"+sbcmd.toString()+") - lunghezza="+sbcmd.toString().length());
		System.out.println("SetDepartment - command = executeRTDirectIo(4002,0,"+sbcmd.toString()+")");
		fiscalPrinterDriver.executeRTDirectIo(4002, 0, sbcmd);
	}
	
}

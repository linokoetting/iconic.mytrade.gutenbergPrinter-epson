package iconic.mytrade.gutenbergPrinter.mop;

import java.util.ArrayList;
import java.util.HashMap;

import iconic.mytrade.gutenberg.jpos.printer.service.Extra;
import iconic.mytrade.gutenberg.jpos.printer.service.PosApp;
import iconic.mytrade.gutenberg.jpos.printer.service.properties.PrinterType;
import iconic.mytrade.gutenberg.jpos.printer.service.properties.SRTPrinterExtension;
import iconic.mytrade.gutenberg.jpos.printer.service.tax.AtecoInfo;
import iconic.mytrade.gutenberg.jpos.printer.service.utils.Sprint;
import iconic.mytrade.gutenberg.srt.RTConsts;
import iconic.mytrade.gutenbergPrinter.FiscalPrinterDriver;
import iconic.mytrade.gutenbergPrinter.PrinterCommands;
import iconic.mytrade.gutenbergPrinter.SharedPrinterFields;
import iconic.mytrade.gutenbergPrinter.tax.DicoTaxLoad;

public class RchCommands extends PrinterCommands {
	
	public void setMOPtable(boolean reallyDoIt){
		int NonRiscossoBeni =		1;
		int NonRiscossoServizi =	2;
		int NonRiscossoFatture =	3;
		int NonRiscossoDCRaSSN =	4;
		int ScontoPagare =			5;
		
		if (SRTPrinterExtension.isPRT()){
			if (PrinterType.isRCHPrintFModel()){
				int globalIndex = 0;
				int typeIndex = 0;
				
				int restoAbilitato;
				int sommaACassa;
				int NonRiscosso;
				int AperturaCassetto;
				int ImportoObbligatorio;
				int Ticket;
				String Descrizione = "";
				boolean isContanti;
				boolean isScontoAPagare;
				
				LoadMops.loadMops();
				
				for (int giro = 1; giro <= 2; giro++) {
					for (typeIndex = 0; typeIndex <= 6; typeIndex++){
						if (giro == 1) {
							if ((typeIndex == LoadMops.NOPAY2_TYPE) || (typeIndex == LoadMops.TICKETWN_TYPE))	// RTOne al massimo MAXMOPS forme di pagamento
								continue;
						}
						if (giro == 2) {
							if ((typeIndex != LoadMops.NOPAY2_TYPE) && (typeIndex != LoadMops.TICKETWN_TYPE))	// RTOne al massimo MAXMOPS forme di pagamento
								continue;
						}
						for (int i = 0; i < LoadMops.Mops.size(); i++){
							Mop mop = (Mop) LoadMops.Mops.get(i);
							if ((mop.getSrtdescription() == null) || (mop.getSrtdescription().length() == 0))
								continue;
							if (mop.getType() == typeIndex){
								restoAbilitato = 0;
								sommaACassa = 0;
								NonRiscosso = 0;
								AperturaCassetto = 1;
								if (PrinterType.isDieboldRTOneModel())
									AperturaCassetto = 0;
								ImportoObbligatorio = 1;
								Ticket = 0;
								isContanti = false;
								isScontoAPagare = false;
								
								if (typeIndex == LoadMops.CASH_TYPE){
									restoAbilitato = 1;
									sommaACassa = 1;
									isContanti = true;
								}
								if (typeIndex == LoadMops.NOPAY1_TYPE){
									NonRiscosso = 1;
								}
								if (typeIndex == LoadMops.EFT_TYPE){
								}
								if (typeIndex == LoadMops.NOPAY2_TYPE){
									if (SharedPrinterFields.isfwRT2disabled())
										NonRiscosso = 1;
									Ticket = 1;
								}
								if (typeIndex == LoadMops.TICKETWN_TYPE){
									if (SharedPrinterFields.isfwRT2disabled())
										NonRiscosso = 1;
									Ticket = 1;
								}
								if (typeIndex == LoadMops.NOPAID_TYPE){
									if (mop.getInd() == 0)
										NonRiscosso = NonRiscossoBeni;
									else if (mop.getInd() == 4)
										NonRiscosso = (SharedPrinterFields.isfwRT2enabled() ? NonRiscossoFatture : NonRiscossoBeni);
									else if (mop.getInd() == 5)
										NonRiscosso = (SharedPrinterFields.isfwRT2enabled() ? NonRiscossoDCRaSSN : NonRiscossoBeni);
									else
										NonRiscosso = (SharedPrinterFields.isfwRT2enabled() ? mop.getInd() : NonRiscossoBeni);
								}
								if (typeIndex == LoadMops.DSCONPAY_TYPE){
									NonRiscosso = (SharedPrinterFields.isfwRT2enabled() ? ScontoPagare : NonRiscossoBeni);
									isScontoAPagare = true;
								}
								
								Descrizione = (mop.getDescription().length() <= 20 ? mop.getDescription() : mop.getDescription().substring(0, 20));
								
								globalIndex++;
								
								if (isContanti && Descrizione.equalsIgnoreCase(SharedPrinterFields.DESCRIZIONE_CONTANTI)) {
									SharedPrinterFields.INDICE_CONTANTI = globalIndex;
									System.out.println("setMOPtable - INDICE_CONTANTI = "+SharedPrinterFields.INDICE_CONTANTI);
								}
								if (isScontoAPagare && Descrizione.equalsIgnoreCase(SharedPrinterFields.DESCRIZIONE_SCONTOAPAGARE)) {
									SharedPrinterFields.INDICE_SCONTOAPAGARE = globalIndex;
									System.out.println("setMOPtable - INDICE_SCONTOAPAGARE = "+SharedPrinterFields.INDICE_SCONTOAPAGARE);
								}
								
								StringBuffer command = new StringBuffer(globalIndex+","+
																		restoAbilitato+","+
																		sommaACassa+","+
																		NonRiscosso+","+
																		AperturaCassetto+","+
																		ImportoObbligatorio+","+
																		Ticket+","+
																		Descrizione);
								if (reallyDoIt) {
									System.out.println("setMOPtable - command = "+command);
									fiscalPrinterDriver.executeRTDirectIo(5100, 0, command);
								}
						      	
								if (globalIndex == RTConsts.MAXMOPS-1) {
									globalIndex++;
									addMOPtable(globalIndex, reallyDoIt);
									return;
								}
							}
						}
					}
				}
				
				LoadMops.loadVops();
				
				for (int i = 0; i < LoadMops.Vops.size(); i++){
					String mop = ((String)LoadMops.Vops.get(i));
					restoAbilitato = 0;
					sommaACassa = 0;
					NonRiscosso = 0;
					AperturaCassetto = 1;
					if (PrinterType.isDieboldRTOneModel())
						AperturaCassetto = 0;
					ImportoObbligatorio = 1;
					Ticket = 0;
					NonRiscosso = 1;
					Ticket = 1;
					
					Descrizione = (mop.length() <= 20 ? mop : mop.substring(0, 20));
					
					globalIndex++;
					
					StringBuffer command = new StringBuffer(globalIndex+","+
															restoAbilitato+","+
															sommaACassa+","+
															NonRiscosso+","+
															AperturaCassetto+","+
															ImportoObbligatorio+","+
															Ticket+","+
															Descrizione);
					if (reallyDoIt) {
						System.out.println("setMOPtable - command = "+command);
						fiscalPrinterDriver.executeRTDirectIo(5100, 0, command);
					}
			      	
					if (globalIndex == RTConsts.MAXMOPS-1) {
						globalIndex++;
						addMOPtable(globalIndex, reallyDoIt);
						return;
					}
				}
				
				globalIndex++;
				addMOPtable(globalIndex, reallyDoIt);
			}
		}
	}

	private void addMOPtable(int globalIndex, boolean reallyDoIt){
		if (DicoTaxLoad.isIvaAllaPrinter()) {
			if (PrinterType.isRCHPrintFModel()) {
				int restoAbilitato = 0;
				int sommaACassa = 0;
				int NonRiscosso = 1;
				int AperturaCassetto = 0;
				int ImportoObbligatorio = 1;
				int Ticket = 0;
				String Descrizione = "Pagamento Multiplo";
				StringBuffer command = new StringBuffer(globalIndex+","+
						restoAbilitato+","+
						sommaACassa+","+
						NonRiscosso+","+
						AperturaCassetto+","+
						ImportoObbligatorio+","+
						Ticket+","+
						Descrizione);
				if (reallyDoIt) {
					System.out.println("setMOPtable - command = "+command);
					fiscalPrinterDriver.executeRTDirectIo(5100, 0, command);
				}
			}
		}
	}
	
	public void setRchMOPtable(){
		if (SRTPrinterExtension.isPRT()){
			if (PrinterType.isRCHPrintFModel()){
				int globalIndex = 0;
				int typeIndex = 0;
				String Descrizione = "";
				LoadMops.RchMops = new ArrayList();
				
				LoadMops.loadMops();
				
				for (int giro = 1; giro <= 2; giro++) {
					for (typeIndex = 0; typeIndex <= 6; typeIndex++){
						if (giro == 1) {
							if ((typeIndex == LoadMops.NOPAY2_TYPE) || (typeIndex == LoadMops.TICKETWN_TYPE))	// RTOne al massimo MAXMOPS forme di pagamento
								continue;
						}
						if (giro == 2) {
							if ((typeIndex != LoadMops.NOPAY2_TYPE) && (typeIndex != LoadMops.TICKETWN_TYPE))	// RTOne al massimo MAXMOPS forme di pagamento
								continue;
						}
						for (int i = 0; i < LoadMops.Mops.size(); i++){
							Mop mop = (Mop) LoadMops.Mops.get(i);
							if ((mop.getSrtdescription() == null) || (mop.getSrtdescription().length() == 0))
								continue;
							if (mop.getType() == typeIndex){
								Descrizione = (mop.getDescription().length() <= 20 ? mop.getDescription() : mop.getDescription().substring(0, 20));
								globalIndex++;
								RchMop rchmop = new RchMop(Descrizione,globalIndex);
								System.out.println("setRchMOPtable - added "+rchmop.getCode()+" - "+((Mop)mop).getType()+" "+((Mop)mop).getInd()+" "+rchmop.getDescription());
								LoadMops.RchMops.add(rchmop);
								
								if (globalIndex == RTConsts.MAXMOPS-1) {
									globalIndex++;
									addRchMOPtable(globalIndex);
									return;
								}
							}
						}
					}
				}
				
				LoadMops.loadVops();
				
				for (int i = 0; i < LoadMops.Vops.size(); i++){
					String mop = ((String)LoadMops.Vops.get(i));
					Descrizione = (mop.length() <= 20 ? mop : mop.substring(0, 20));
					globalIndex++;
					RchMop rchmop = new RchMop(Descrizione,globalIndex);
					System.out.println("setRchMOPtable - added "+rchmop.getCode()+" - "+rchmop.getDescription());
					LoadMops.RchMops.add(rchmop);
					
					if (globalIndex == RTConsts.MAXMOPS-1) {
						globalIndex++;
						addRchMOPtable(globalIndex);
						return;
					}
				}
				
				globalIndex++;
				addRchMOPtable(globalIndex);
			}
		}
	}

	private void addRchMOPtable(int globalIndex){
		if (SRTPrinterExtension.isPRT()){
			if (PrinterType.isRCHPrintFModel()){
				String Descrizione = "Pagamento Multiplo";
				RchMop rchmop = new RchMop(Descrizione,globalIndex);
				System.out.println("setRchMOPtable - added "+rchmop.getCode()+" - "+rchmop.getDescription());
				LoadMops.RchMops.add(rchmop);
			}
		}
	}
	
}

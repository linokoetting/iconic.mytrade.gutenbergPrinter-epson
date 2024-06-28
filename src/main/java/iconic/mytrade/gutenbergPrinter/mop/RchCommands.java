package iconic.mytrade.gutenbergPrinter.mop;

import iconic.mytrade.gutenberg.jpos.printer.service.properties.SRTPrinterExtension;
import iconic.mytrade.gutenbergPrinter.PrinterCommands;
import iconic.mytrade.gutenbergPrinter.tax.DicoTaxLoad;

public class RchCommands extends PrinterCommands {
	
	public void setMOPtable(boolean reallyDoIt){
		int NonRiscossoBeni =		1;
		int NonRiscossoServizi =	2;
		int NonRiscossoFatture =	3;
		int NonRiscossoDCRaSSN =	4;
		int ScontoPagare =			5;
		
		if (SRTPrinterExtension.isPRT()){
		}
	}

	private void addMOPtable(int globalIndex, boolean reallyDoIt){
		if (DicoTaxLoad.isIvaAllaPrinter()) {
		}
	}
	
	public void setRchMOPtable(){
		if (SRTPrinterExtension.isPRT()){
		}
	}

	private void addRchMOPtable(int globalIndex){
		if (SRTPrinterExtension.isPRT()){
		}
	}
	
}

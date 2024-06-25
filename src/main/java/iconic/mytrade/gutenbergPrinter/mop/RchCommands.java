package iconic.mytrade.gutenbergPrinter.mop;

import java.util.ArrayList;
import java.util.HashMap;

import iconic.mytrade.gutenberg.jpos.printer.service.Extra;
import iconic.mytrade.gutenberg.jpos.printer.service.PosApp;
import iconic.mytrade.gutenberg.jpos.printer.service.properties.PrinterType;
import iconic.mytrade.gutenberg.jpos.printer.service.properties.SRTPrinterExtension;
import iconic.mytrade.gutenberg.jpos.printer.service.tax.AtecoInfo;
import iconic.mytrade.gutenberg.jpos.printer.srt.RTConsts;
import iconic.mytrade.gutenberg.jpos.printer.utils.Sprint;
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

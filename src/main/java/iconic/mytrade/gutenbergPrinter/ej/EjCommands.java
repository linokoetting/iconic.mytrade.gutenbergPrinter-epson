package iconic.mytrade.gutenbergPrinter.ej;

import java.util.ArrayList;

import iconic.mytrade.gutenberg.jpos.printer.service.Cancello;
import iconic.mytrade.gutenberg.jpos.printer.service.R3define;
import iconic.mytrade.gutenberg.jpos.printer.service.RoungickInLinePromo;
import iconic.mytrade.gutenberg.jpos.printer.srt.DummyServerRT;
import iconic.mytrade.gutenberg.jpos.printer.utils.Sprint;
import iconic.mytrade.gutenbergPrinter.SharedPrinterFields;
import jpos.JposException;

public class EjCommands {

	public void open(int model, String device) {
	}

	public void beginFiscalReceipt() throws JposException {
		if (SharedPrinterFields.isfiscalEJenabled() && !SharedPrinterFields.PosponedInError) {
			SharedPrinterFields.fiscalEJ.beginFiscalReceipt(false);
		}
	}

	public void beginNonFiscal() throws JposException {
		if (SharedPrinterFields.isfiscalEJenabled() && !SharedPrinterFields.PosponedInError) {
			SharedPrinterFields.fiscalEJ.beginNonFiscal();
		}
	}

	public void endFiscalReceipt() throws JposException {
		if (SharedPrinterFields.isfiscalEJenabled() && !SharedPrinterFields.PosponedInError) {
			String BASERECEIPT = "<ReceNR>        Numero Scontrino Fiscale  ";
			String BASEZREPORT = "<ReceNR>        Numero Chiusura  Fiscale  ";
			
			String z = Sprint.f("%04d",DummyServerRT.CurrentFiscalClosure);
            ForFiscalEJFile.writeToFile( BASEZREPORT + z);
            ForFiscalEJFile.writeToFile( BASERECEIPT + DummyServerRT.CurrentReceiptNumber);
			SharedPrinterFields.fiscalEJ.endFiscalReceipt(true);
		}
	}

	public void endNonFiscal() throws JposException {
		if (SharedPrinterFields.isfiscalEJenabled() && !SharedPrinterFields.PosponedInError) {
			SharedPrinterFields.fiscalEJ.endNonFiscal();
		}
	}

	public static void printNormal(int station, String data) throws JposException {
		if (SharedPrinterFields.isfiscalEJenabled() && !SharedPrinterFields.PosponedInError) {
			if (data.indexOf(Cancello.getTag()) >= 0) {
				String s = data.replaceAll(Cancello.getTag()+R3define.CrLf, "");
				data = s;
			}
			SharedPrinterFields.fiscalEJ.printNormal(station, data);
		}
	}

	public void printRecItem(String description, long price, int quantity, int vatInfo, long unitPrice, String unitName) throws JposException {
		if (SharedPrinterFields.isfiscalEJenabled() && !SharedPrinterFields.PosponedInError) {
			SharedPrinterFields.fiscalEJ.printRecItem(description,price,quantity,vatInfo,unitPrice,unitName);
			if ( RoungickInLinePromo.isRoungickInLinePromo() )
			{
				ArrayList A = RoungickInLinePromo.getDiscountFromTable(unitName);
					
				if ( A != null )
				{
					for ( int x = 0 ; x < A.size(); x++ )
					{
						String S = (String) A.get(x);
						SharedPrinterFields.fiscalEJ.printRecMessage(S);
					}
				}	
			}
		}
	}

	public void printRecItemAdjustment(int adjustmentType, String description, long amount, int vatInfo) throws JposException {
		if (SharedPrinterFields.isfiscalEJenabled() && !SharedPrinterFields.PosponedInError) {
			SharedPrinterFields.fiscalEJ.printRecItemAdjustment(adjustmentType,description,amount,vatInfo);
		}
	}

	public void printRecMessage(String message) throws JposException {
		if (SharedPrinterFields.isfiscalEJenabled() && !SharedPrinterFields.PosponedInError) {
			if (!message.equalsIgnoreCase(Cancello.getTag()))
				SharedPrinterFields.fiscalEJ.printRecMessage(message);
		}
	}

	public void printRecRefund(String description, long amount, int vatInfo) throws JposException {
		if (SharedPrinterFields.isfiscalEJenabled() && !SharedPrinterFields.PosponedInError) {
			SharedPrinterFields.fiscalEJ.printRecRefund(description,amount,vatInfo);
		}
	}

	public void printRecSubtotal(long amount) throws JposException {
		if (SharedPrinterFields.isfiscalEJenabled() && !SharedPrinterFields.PosponedInError) {
			SharedPrinterFields.fiscalEJ.printRecSubtotal(amount);
		}
	}

	public void printRecSubtotalAdjustment(int adjustmentType, String description, long amount) throws JposException {
		if (SharedPrinterFields.isfiscalEJenabled() && !SharedPrinterFields.PosponedInError) {
			SharedPrinterFields.fiscalEJ.printRecSubtotalAdjustment(adjustmentType,description,amount);
		}
	}

	public void printRecTotal(long total, long payment, String description) throws JposException {
		if (SharedPrinterFields.isfiscalEJenabled() && !SharedPrinterFields.PosponedInError) {
			SharedPrinterFields.fiscalEJ.printRecTotal(total,payment,description);
		}
	}

	public void printRecVoid(String description) throws JposException {
		if (SharedPrinterFields.isfiscalEJenabled() && !SharedPrinterFields.PosponedInError) {
			SharedPrinterFields.fiscalEJ.printRecVoid(description);
		}
	}

	public void printRecVoidItem(String description, long amount, int quantity, int adjustmentType, long adjustment, int vatInfo) throws JposException {
		if (SharedPrinterFields.isfiscalEJenabled() && !SharedPrinterFields.PosponedInError) {
			SharedPrinterFields.fiscalEJ.printRecVoidItem(description,amount,quantity,adjustmentType,adjustment,vatInfo);
		}
	}

	public void printReport(int reportType, String startNum, String endNum) throws JposException {
		if (SharedPrinterFields.isfiscalEJenabled() && !SharedPrinterFields.PosponedInError) {
			SharedPrinterFields.fiscalEJ.printReport(reportType, startNum, endNum);
		}
	}

	public void printXReport() throws JposException {
		if (SharedPrinterFields.isfiscalEJenabled() && !SharedPrinterFields.PosponedInError) {
			SharedPrinterFields.fiscalEJ.printXReport();
		}
	}

	public void printZReport() throws JposException {
		if (SharedPrinterFields.isfiscalEJenabled() && !SharedPrinterFields.PosponedInError) {
			SharedPrinterFields.fiscalEJ.printZReport();
		}
	}

	public void resetPrinter() throws JposException {
		if (SharedPrinterFields.isfiscalEJenabled() && !SharedPrinterFields.PosponedInError) {
			SharedPrinterFields.fiscalEJ.resetPrinter();
		}
	}

	public void setDate(String date) throws JposException {
		if (SharedPrinterFields.isfiscalEJenabled() && !SharedPrinterFields.PosponedInError) {
			SharedPrinterFields.fiscalEJ.setDate(date);
		}
	}

	public void setHeaderLine(int lineNumber, String text, boolean doubleWidth) throws JposException {
		if (SharedPrinterFields.isfiscalEJenabled() && !SharedPrinterFields.PosponedInError) {
			SharedPrinterFields.fiscalEJ.setHeaderLine(lineNumber, text, doubleWidth);
		}
	}

	public void setTrailerLine(int lineNumber, String text, boolean doubleWidth) throws JposException {
		if (SharedPrinterFields.isfiscalEJenabled() && !SharedPrinterFields.PosponedInError) {
			SharedPrinterFields.fiscalEJ.setTrailerLine(lineNumber, text, doubleWidth);
		}
	}

	public void close() {
	}

	public static void Write(String s, boolean crlf) {
		try {
			if (crlf)
				printNormal(jpos.POSPrinterConst.PTR_S_RECEIPT, s+R3define.CrLf);
			else
				printNormal(jpos.POSPrinterConst.PTR_S_RECEIPT, s);
		} catch (JposException e) {
			System.out.println("EJCommands - Write: exception="+e.getMessage());
		}
	}
	
}

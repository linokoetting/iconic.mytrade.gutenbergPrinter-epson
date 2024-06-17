package iconic.mytrade.gutenbergPrinter.ej;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import jpos.JposException;

public class FiscalEJFile extends FiscalEJ {

	private long totale = 0;
	private long pagato = 0;
	
	private static final String BASEROUNDING  = "<Round>         Arrotondamento            ";
	private static final String BASERESTO  = "<Resto>         Resto                     ";
	private static final String BASETOTALE = "<Totale>        Totale                    ";	
	private static final String PREBARRE = "====";
	private static final String POSTBARRE = "=================================";
	
	public static final String BNF = "<BNF>           "+PREBARRE+"BNF"+POSTBARRE;
	public static final String ENF = "<ENF>           "+PREBARRE+"ENF"+POSTBARRE;

//	public void beginFiscalDocument(int documentAmount) throws JposException {
//		totale = 0;
//		pagato = 0;
//		super.beginFiscalDocument(documentAmount);
//	}

	public void printRecTotal(long total, long payment, String description)
			throws JposException {

		totale = total;
		if (pagato == 0) {
			ForFiscalEJFile.writeToFile( BASETOTALE
					+ formatAmount(totale));
		}
		pagato += payment;
		System.out.println("FiscalEJFile totale="+totale+" pagato="+pagato+" rounding="+getRounding());
		super.printRecTotal(total, payment, description);
		if (pagato >= totale + Math.abs(getRounding())) {
			ForFiscalEJFile.writeToFile( BASERESTO
					+ formatAmount(totale - pagato + getRounding()));
		}
		if (getRounding() != 0) {
			ForFiscalEJFile.writeToFile( BASEROUNDING
					+ formatAmount(getRounding()));
			setRounding(0);
		}
	}

	public void endFiscalReceipt(boolean printHeader) throws JposException {
		totale = 0;
		pagato = 0;
		if (printHeader) 
			ForFiscalEJFile.writeToFile("<DataOra>       "+getDataOra());
		
		super.endFiscalReceipt(printHeader);
		
		if (printHeader)
			ForFiscalEJFile.writeToFile("<EF>            "+PREBARRE+"EF"+POSTBARRE);
	}
	
	public void beginFiscalReceipt(boolean printHeader) throws JposException {
		ForFiscalEJFile.writeToFile("<BF>            "+PREBARRE+"BF"+POSTBARRE);
		super.beginFiscalReceipt(printHeader);
	}
	
	public void beginNonFiscal() throws JposException {
		ForFiscalEJFile.writeToFile(BNF);
		super.beginNonFiscal();
	}

	public void endNonFiscal() throws JposException {
		super.endNonFiscal();
		ForFiscalEJFile.writeToFile(ENF);
	}

	public void beginFiscalDocument(int documentAmount) throws JposException {

		ForFiscalEJFile.writeToFile("<BF>            "+PREBARRE+"BF "+POSTBARRE);
		
//		super.beginFiscalDocument(documentAmount);
	}

	public void endFiscalDocument() throws JposException {
		
		ForFiscalEJFile.writeToFile("<EF>            "+PREBARRE+"EF "+POSTBARRE);
		
//		super.endFiscalDocument();
	}

	
	
	//////////////////////////////////////////////
	private String getDataOra(GregorianCalendar greg){
		return new SimpleDateFormat("dd-MM-yy  HH:mm").format(greg.getTime());
	}
	private String getDataOra(){
		return getDataOra(new GregorianCalendar());
	}
	
	/**
	 * Format an amount for printing.
	 * @param amount the amount
	 * @return the string to print
	 **/
	protected String formatAmount(long amount) {
		if (amount < 0) amount = Math.abs(amount);
		String decimals = Long.toString((amount / 100) % 100);
		while (decimals.length() < 2)
			decimals = "0" + decimals;

		String s = Long.toString(amount / 10000) + "." + decimals;
		while (s.length() < 8)
			s = " " + s;
		return s;
	}

}

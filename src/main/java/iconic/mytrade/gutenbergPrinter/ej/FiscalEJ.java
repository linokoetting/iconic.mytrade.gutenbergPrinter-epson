package iconic.mytrade.gutenbergPrinter.ej;

import java.awt.Checkbox;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;

import iconic.mytrade.gutenberg.jpos.printer.service.Company;
import iconic.mytrade.gutenberg.jpos.printer.service.EjTokens;
import iconic.mytrade.gutenbergPrinter.tax.TaxData;
import jpos.FiscalPrinterConst;
import jpos.JposConst;
import jpos.JposException;
import jpos.services.EventCallbacks;

public class FiscalEJ extends NullFiscalPrinter implements FiscalPrinterConst,JposConst
{
	private static final int MAX_LINES = 100;
	public static final String BEANSTORE_PROPERTIES = "/bs2coop/beanstore.properties";
	public static final String ERRLOG_OUT = "/bs2coop/pos/errlog.out";
	public static final String[] strippedStrs = {"total=","qty=","price=","paid=","iva=","adjust=","amount="};
	public static final String EJ_FILE_NAME = "ejfile.txt";
	public static final long MAX_DIMENSION = 5000;
	public  static final String BASEJOURNAL = "<Journal>       ";

	private Frame frame = new Frame();
	private POSPrinter.PrinterList[] dummyPrinters = new POSPrinter.PrinterList[2];

	// holds the current line
	private StringBuffer[] buffers = new StringBuffer[2];
	
	private Checkbox recLow = new Checkbox("Receipt low");
	private Checkbox recEmpty = new Checkbox("Receipt empty");

	private Checkbox slipLow = new Checkbox("Slip low");
	private Checkbox slipIn = new Checkbox("Slip in");
	private Checkbox slipAuto = new Checkbox("Slip auto");

	private Checkbox coverOpen = new Checkbox("Cover open");
	private Checkbox paperJam = new Checkbox("Paper jam");

	private Checkbox trainingMode = new Checkbox("Training");

	private boolean enabled = false;

	private boolean slipInsertionMode;
	private boolean slipRemovalMode;

	private int printerState = FPTR_PS_MONITOR;
	
	private long rounding = 0;
	
	public void open(String logicalName,EventCallbacks cb)
	{
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints();
		Panel controlPanel = new Panel();

		// use fixed size font
		frame.setTitle("Printer "+logicalName);
		frame.setFont(new Font("Monospaced",Font.PLAIN,12));

		for (int i=0; i<2; i++)
		{
			dummyPrinters[i] = new POSPrinter.PrinterList();
			buffers[i] = new StringBuffer();
		}

		frame.setLayout(layout);

		constraints.gridx = 0;
		constraints.gridy = GridBagConstraints.RELATIVE;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = constraints.weighty = 1;
		constraints.fill = GridBagConstraints.BOTH;
		layout.setConstraints(dummyPrinters[0],constraints);
		frame.add(dummyPrinters[0]);

		controlPanel.setLayout(new GridLayout(3,3));
		controlPanel.add(recLow);
		controlPanel.add(trainingMode);
		controlPanel.add(slipLow);
		controlPanel.add(recEmpty);
		controlPanel.add(coverOpen);
		controlPanel.add(slipIn);
		controlPanel.add(paperJam);
		controlPanel.add(new Label());
		controlPanel.add(slipAuto);

		constraints.fill = GridBagConstraints.HORIZONTAL;
		layout.setConstraints(controlPanel,constraints);
		frame.add(controlPanel);

		constraints.fill = GridBagConstraints.BOTH;
		layout.setConstraints(dummyPrinters[1],constraints);
		frame.add(dummyPrinters[1]);

		slipIn.setEnabled(false);
		trainingMode.setEnabled(false);

		frame.pack();
		frame.setSize(500,400);
		setPosition(frame);
		frame.show();
		try{
			frame.setExtendedState(java.awt.Frame.ICONIFIED);
			frame.setVisible(false);
		}catch (NoClassDefFoundError ne) {
		}
		catch (NoSuchMethodError me) {
		}
		catch(Exception e){    	
		}
	}
	   
	public void beginFiscalReceipt(boolean printHeader) throws JposException
	{
		if (printHeader)
			addItem(dummyPrinters[0],"<Fiscal Receipt Header>");
		printerState = FPTR_PS_FISCAL_RECEIPT;
	}

	public void beginNonFiscal() throws JposException
	{
		printerState = FPTR_PS_NONFISCAL;
	}

	public void endNonFiscal() throws JposException
	{
		printerState = FPTR_PS_MONITOR;
	}

	public void endFiscalReceipt(boolean printHeader) throws JposException
	{
		if(Company.isTillCash()){
			ForFiscalEJFile.writeToFile(BASEJOURNAL+Company.getCashIN());
				ForFiscalEJFile.writeToFile(BASEJOURNAL+Company.getCashOUT());
		}
		if (printHeader)
		{
			addItem(dummyPrinters[0],"<Fiscal Receipt Trailer>");
			addItem(dummyPrinters[0],"");
			addItem(dummyPrinters[0],"<CUT>");
			addItem(dummyPrinters[0],"");
		}
		printerState = FPTR_PS_MONITOR;
	}

	public void printNormal(int station,String data) throws JposException
	{
		StringBuffer buffer;
		POSPrinter.PrinterList dummyPrinter;
		int index = 0;
		switch (station)
		{
		case FPTR_S_RECEIPT:
			if (slipIn.getState())
				throw new JposException(JPOS_E_ILLEGAL);
			break;
		case FPTR_S_JOURNAL:
			return;
		case FPTR_S_SLIP:
			index = 1;
			break;
		default:
			throw new JposException(JPOS_E_ILLEGAL);
		}

		checkSimulatedErrors(station);

		buffer = buffers[index];
		dummyPrinter = dummyPrinters[index];

		data = convertEscapes(data,false);
		
		data = hideSomething(data);

		for (int i=0; i<data.length(); i++)
		{
			char c = data.charAt(i);

			if (c == '\n')
			{
				// here is where we actually print the data
				addItem(dummyPrinter,"<Non Fiscal>    "+buffer.toString());
				buffer.setLength(0);
			}
			else
				if (c != '\r')
					buffer.append((char)c);
		}
	}

	public void printRecItem(String description,long price,int quantity,int vatInfo,long unitPrice,String unitName) throws JposException
	{
		checkSimulatedErrors(0);
		addItem(dummyPrinters[0],"<Item>          "+convertEscapes(description,true)+" price="+formatAmount(price)+"    qty="+quantity /* +" vatInfo="+vatInfo+" unitPrice="+unitPrice+" unitName="+unitName */);
	}

	public void printRecItem(String description,long price,int quantity,int vatInfo,long unitPrice,String unitName,String iva) throws JposException
	{
		checkSimulatedErrors(0);
		addItem(dummyPrinters[0],"<Item>          "+convertEscapes(description,true)+"iva="+iva+"price="+formatAmount(price)+" qty="+quantity);
	}

	public void printRecItemAdjustment(int adjustmentType,String description,long amount,int vatInfo) throws JposException
	{
		checkSimulatedErrors(0);
		addItem(dummyPrinters[0],"<Item Adj>      "+convertEscapes(description,true)+" amount="+formatAmount(amount)+" "+adjType(adjustmentType) /* +" vatInfo="+vatInfo */);
	}

	public void printRecMessage(String message) throws JposException
	{
		checkSimulatedErrors(0);
		addItem(dummyPrinters[0],"<Message>       "+convertEscapes(message,false));
	}

	public void printRecRefund(String description,long amount,int vatInfo) throws JposException
	{
		checkSimulatedErrors(0);
		if (TaxData.isAcconti(vatInfo))
			addItem(dummyPrinters[0],"<Acconto>        "+convertEscapes(description,true)+" amount="+formatNegAmount(amount) /* +" vatInfo="+vatInfo */);
		else if (TaxData.isBuonoMonouso(vatInfo))
			addItem(dummyPrinters[0],"<BMonoUso>        "+convertEscapes(description,true)+" amount="+formatNegAmount(amount) /* +" vatInfo="+vatInfo */);
		else
			addItem(dummyPrinters[0],"<Refund>        "+convertEscapes(description,true)+" amount="+formatAmount(amount) /* +" vatInfo="+vatInfo */);
	}

	public void printRecRefund(String description,long amount,int vatInfo,String iva) throws JposException
	{
		checkSimulatedErrors(0);
		if (TaxData.isAcconti(vatInfo))
			addItem(dummyPrinters[0],"<Acconto>        "+convertEscapes(description,true)+"iva="+iva+"amount="+formatNegAmount(amount));
		else if (TaxData.isBuonoMonouso(vatInfo))
			addItem(dummyPrinters[0],"<BMonoUso>        "+convertEscapes(description,true)+"iva="+iva+"amount="+formatNegAmount(amount));
		else
			addItem(dummyPrinters[0],"<Refund>        "+convertEscapes(description,true)+"iva="+iva+"amount="+formatAmount(amount));
	}

	public void printRecSubtotal(long amount) throws JposException
	{
		checkSimulatedErrors(0);
		addItem(dummyPrinters[0],"<Subtotal>      "+convertEscapes("",true)+" amount="+formatAmount(amount));
	}

	public void printRecTotal(long total,long payment,String description) throws JposException
	{
		checkSimulatedErrors(0);
		// addItem(dummyPrinters[0],"<Total>         "+convertEscapes(description,true)+"  total="+formatAmount(total)+"   paid="+formatAmount(payment));
		addItem(dummyPrinters[0],"<Tender>        "+convertEscapes(description,true)+"  paid="+formatAmount(payment));

		printerState = (payment >= total) ? FPTR_PS_FISCAL_RECEIPT_ENDING : FPTR_PS_FISCAL_RECEIPT_TOTAL;
	}

	public void printRecVoid(String description) throws JposException
	{
		checkSimulatedErrors(0);
		addItem(dummyPrinters[0],"<Void>          --------- SCONTRINO ANNULLATO ---------"+convertEscapes(description,false));
		printerState = FPTR_PS_FISCAL_RECEIPT_ENDING;
	}

	public void printRecVoidItem(String description,long amount,int quantity,int adjustmentType,long adjustment,int vatInfo) throws JposException
	{
		checkSimulatedErrors(0);
		if (TaxData.isOmaggio(vatInfo))
			addItem(dummyPrinters[0],"<Omaggio>     "+convertEscapes(description,true)+" amount="+formatNegAmount(amount) /* +" vatInfo="+vatInfo */);
		else
			addItem(dummyPrinters[0],"<Void Item>     "+convertEscapes(description,true)+" amount="+formatAmount(amount)+"    qty="+quantity+" "+adjType(adjustmentType)+" adjust="+formatAmount(adjustment) /* +" vatInfo="+vatInfo */);
	}

	public void printRecVoidItem(String description,long amount,int quantity,int adjustmentType,long adjustment,int vatInfo,String iva) throws JposException
	{
		checkSimulatedErrors(0);
		if (TaxData.isOmaggio(vatInfo))
			addItem(dummyPrinters[0],"<Omaggio>     "+convertEscapes(description,true)+"iva="+iva+" amount="+formatNegAmount(amount));
		else
			addItem(dummyPrinters[0],"<Void Item>     "+convertEscapes(description,true)+"iva="+iva+"adjust="+formatAmount(adjustment)+" qty="+quantity);
	}

	public void printRecSubtotalAdjustment(int adjustmentType,String description,long amount) throws JposException
	{
		checkSimulatedErrors(0);
		addItem(dummyPrinters[0],"<Subtotal Adj>  "+adjType(adjustmentType)+" "+convertEscapes(description,true)+" amount="+formatAmount(amount));
	}

	public void printZReport() throws JposException
	{
		checkSimulatedErrors(0);
		addItem(dummyPrinters[0],"<Z REPORT>");
	}

	private void addItem(POSPrinter.PrinterList list,String string)
	{
		ForFiscalEJFile.writeToFile(string);
		int count = list.getItemCount();

		while (count > MAX_LINES)
		{
			list.remove(0);
			count--;
		}

		list.addItem(string);
		list.select(count);
	}

	private void checkSimulatedErrors(int station) throws JposException
	{
		switch (station)
		{
		case FPTR_S_RECEIPT:
			if (recEmpty.getState())
				throw new JposException(JPOS_E_EXTENDED,JPOS_EFPTR_REC_EMPTY);
			break;
		case FPTR_S_JOURNAL:
			break;
		case FPTR_S_SLIP:
			if (!slipIn.getState())
				throw new JposException(JPOS_E_EXTENDED,JPOS_EFPTR_SLP_EMPTY);
			break;
		}

		if (coverOpen.getState())
			throw new JposException(JPOS_E_EXTENDED,JPOS_EFPTR_COVER_OPEN);

		if (paperJam.getState())
			throw new JposException(JPOS_E_FAILURE);
	}

	private String convertEscapes(String data,boolean trim)
	{
		StringBuffer buffer = new StringBuffer();
		int state = 0;

		for (int i=0; i<data.length(); i++)
		{
			char c = data.charAt(i);

			switch (state)
			{
			case 0:
				if (c == '\u001b')
					state = 1;
				else
					buffer.append(c);
				break;
			case 1:
				if (c == '|')
					state = 2;
				else
				{
					buffer.append('\u001b').append(c);
					state = 0;
				}
				break;
			case 2:
				if (Character.isUpperCase(c))
					state = 0;
				break;
			}
		}

		if (trim)
			while (buffer.length() < 24)
				buffer.append(' ');

		return buffer.toString();
	}

	private String formatAmount(long amount)
	{
		String decimals = Long.toString((amount/100)%100);
		while (decimals.length() < 2)
			decimals = "0"+decimals;

		String s = Long.toString(amount/10000)+"."+decimals;
		while (s.length() < 8)
			s = " "+s;
		return s;
	}

	private String formatNegAmount(long amount)
	{
		String decimals = Long.toString((amount/100)%100);
		while (decimals.length() < 2)
			decimals = "0"+decimals;

		String s = Long.toString(amount/10000)+"."+decimals;
		s = "-"+s;
		while (s.length() < 8)
			s = " "+s;
		return s;
	}

	private String adjType(int adjustmentType)
	{
		switch (adjustmentType)
		{
		case FPTR_AT_AMOUNT_DISCOUNT:
			return "(discount)  ";
		case FPTR_AT_AMOUNT_SURCHARGE:
			return "(surcharge) ";
		case FPTR_AT_PERCENTAGE_DISCOUNT:
			return "(discount %)";
		case FPTR_AT_PERCENTAGE_SURCHARGE:
			return "(surcharge%)";
		default:
			return "(unknown)   ";
		}
	}

	public long getRounding() {
		return rounding;
	}
	
	public void setRounding(long rounding) {
		this.rounding = rounding;
	}

    private static String fixToken(String in, String token)
    {
    	// non essendo sicuro della lunghezza della linea (35 ?)
    	// vado a prendermi la linea del token dentro alle righe da stampare
    	// e la battezzo come token
    	
    	String reply = "";
    	
    	if (in.indexOf(token) < 0)
    		return reply;
    	
        String[] lines = in.split("[\\n|]+");	// uso come delimitatori "\n" e "|"
        
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].indexOf(token) >= 0) {
                reply = lines[i];
                break;
            }
        }
		
    	return reply;
    }
    
    public static String hideSomething(String in)
    {
    	String tokenstart = EjTokens.getTokenStart();
    	String tokenstop  = EjTokens.getTokenStop();
    	
    	String out = "";
    	if (in == null || in.length() == 0) {
    		return out;
    	}
    	
    	if (in.indexOf(tokenstart.trim()) < 0)
    		return in;	// lascio tutto com'è
    	
    	//System.out.println("hideSomething - in = "+in);
    	
    	String token = fixToken(in, tokenstart.trim());
    	if (token != null && token.length() > 0)
    		tokenstart = token;
    	token = fixToken(in, tokenstop.trim());
    	if (token != null && token.length() > 0)
    		tokenstop = token;
    	
    	out = in;
    	
    	int indexstart = in.indexOf(tokenstart);
    	int indexstop = in.indexOf(tokenstop);
    	
    	if (indexstart < 0 && indexstop < 0) {
    		return out;
    	}
    	
        if (indexstart > indexstop && indexstop >= 0 && indexstart >= 0) {
            // nel caso i token siano invertiti per errore
        	// in teoria non dovremmo mai passare di qui
            int temp = indexstart;
            indexstart = indexstop;
            indexstop = temp;
        }
    	
    	if (indexstart >= 0)
    		out = in.substring(0, indexstart);
    	else
    		out = in.substring(0, indexstop);
    	if (indexstop > indexstart)
    		out = out + in.substring(indexstop+tokenstop.length());
    	
    	//System.out.println("hideSomething - out = "+out);
    	
    	return out;
    }
	
}

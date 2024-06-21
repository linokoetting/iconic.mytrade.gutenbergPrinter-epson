package iconic.mytrade.gutenbergPrinter.smtk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;

import iconic.mytrade.gutenberg.jpos.printer.service.LastTicket;
import iconic.mytrade.gutenberg.jpos.printer.service.PosApp;
import iconic.mytrade.gutenberg.jpos.printer.service.R3define;
import iconic.mytrade.gutenberg.jpos.printer.service.SmartTicket;
import iconic.mytrade.gutenberg.jpos.printer.service.properties.PrinterType;
import iconic.mytrade.gutenberg.jpos.printer.service.properties.SRTPrinterExtension;
import iconic.mytrade.gutenberg.jpos.printer.smtk.Base64;
import iconic.mytrade.gutenberg.jpos.printer.srt.DummyServerRT;
import iconic.mytrade.gutenberg.jpos.printer.srt.Xml4SRT;
import iconic.mytrade.gutenberg.jpos.printer.utils.Files;
import iconic.mytrade.gutenberg.jpos.printer.utils.RunShellScriptPoli20;
import iconic.mytrade.gutenberg.jpos.printer.utils.Sprint;
import iconic.mytrade.gutenbergPrinter.PrinterCommands;
import iconic.mytrade.gutenbergPrinter.SharedPrinterFields;
import jpos.FiscalPrinterConst;
import jpos.JposException;

public class SMTKCommands extends PrinterCommands {

	public static void Base64_Ticket(int transactionnumber, boolean voiding) throws JposException
	{
		if (!SmartTicket.isBase64_Ticket())
			return;
		
    	String[] date = new String[1];
    	getDate(date);
    	
		if (SRTPrinterExtension.isPRT()) {
			if (voiding)
			{
	        	// per i postVoid non abbiamo i dati per costruire il lastticket
	        	
				ArrayList<String> ticket = null;
				if (PrinterType.isEpsonModel()) {
			    	//ticket = getReceiptFromEj_Epson(date[0].substring(0, 8));
			    	ticket = fiscalPrinterDriver.getReceiptFromEj_Epson(DummyServerRT.CurrentReceiptNumber, date[0].substring(0, 8));	// uso questa anche se è più lenta perchè tanto è solo per i postvoid e mi restituisce le righe già divise, che dovrei comunque dividerle io con l'altra procedura che è appena più veloce
				}
				if (PrinterType.isRCHPrintFModel()) {
					ticket = fiscalPrinterDriver.getReceiptFromEj_Rch();
				}
				
				initTicketOnFile();
		    	for (int i=0; i<ticket.size(); i++)
		    		scriviLastTicket(ticket.get(i));
			}
		}
    	
		if (SRTPrinterExtension.isSRT()) {
			// il file LastTicket.out viene già fatto normalmente
		}
		
		String toEncode = SharedPrinterFields.lastticket;
		String Encoded = rtsTrxBuilder.storerecallticket.Default.getsourcePath()+"LastTicket.b64";
		String Decoded = "";
		if ((SmartTicket.getBase64_Decode() != null) && (SmartTicket.getBase64_Decode().length() > 0))
			Decoded = SmartTicket.getBase64_Decode()+"/"+SharedPrinterFields.lastticket.substring(SharedPrinterFields.lastticket.lastIndexOf("/")+1);
		try {
			Base64.Encode(toEncode, Encoded, Decoded);
		} catch (IOException e) {
			System.out.println("Base64 encoding error : "+e.getMessage());
		}
		
    	moveBase64Ticket(Encoded, date[0].substring(0, 2), transactionnumber, "b64", Integer.parseInt(DummyServerRT.CurrentReceiptNumber), voiding);
	}

	public static void Smart_Ticket(int transactionnumber, boolean voiding) throws JposException
	{
		if (!(SmartTicket.isSmart_Ticket() && SmartTicket.Smart_Ticket_Mode.equalsIgnoreCase(SmartTicket.ERECEIPT_URL_SERVER_PULL) && (SmartTicket.Smart_Ticket_ReceiptMode != SmartTicket.ERECEIPT_PAPER)))
			return;
		
		String key = "ipserver"+PosApp.getStoreCode()+PosApp.getTillNumber()+transactionnumber;	// TEMPORANEO ipserver dovrà contenere l'ip address del server smart ticket preso da qualche tabella
		
		if (SRTPrinterExtension.isPRT() && fiscalPrinterDriver.isfwSMTKenabled())
		{
	    	String[] date = new String[1];
	    	getDate(date);
	    	
	    	if (DummyServerRT.CurrentFiscalClosure == 0) {
    	        int[] ai = new int[1];
    	        String[] as = new String[1];
                fiscalPrinterDriver.getData(FiscalPrinterConst.FPTR_GD_Z_REPORT, ai, as);
                DummyServerRT.CurrentFiscalClosure = Integer.parseInt(as[0])+1;
	    	}
	    	
	        String textfile = "";
	        
	        if (voiding)
	        {
	        	// per i postVoid non abbiamo i dati per costruire il lastticket
	        	
				ArrayList<String> ticket = null;
				if (PrinterType.isEpsonModel()) {
			    	//ticket = getReceiptFromEj_Epson(date[0].substring(0, 8));
			    	ticket = fiscalPrinterDriver.getReceiptFromEj_Epson(DummyServerRT.CurrentReceiptNumber, date[0].substring(0, 8));	// uso questa anche se è più lenta perchè tanto è solo per i postvoid e mi restituisce le righe già divise, che dovrei comunque dividerle io con l'altra procedura che è appena più veloce 
				}
				if (PrinterType.isRCHPrintFModel()) {
					ticket = fiscalPrinterDriver.getReceiptFromEj_Rch();
				}
				
		        for(int i = 0; i < ticket.size(); i++)
		        	textfile = (new StringBuilder(String.valueOf(textfile))).append((String)ticket.get(i)).toString();
	        }
	        else
	        {
				try {
			        BufferedReader in = new BufferedReader(new FileReader(SharedPrinterFields.lastticket));
			        String line;
			        StringBuilder sb = new StringBuilder();
			        while ((line = in.readLine()) != null) {
			        	sb.append(line+R3define.Lf);
			        }
			        in.close();
			        textfile = sb.toString();
				} catch (Exception e) {
					System.out.println("Smart_Ticket error : "+e.getMessage());
				}
	        }
	        
	    	System.out.println("SMTK - data="+date[0]);
	    	System.out.println("SMTK - CurrentFiscalClosure="+DummyServerRT.CurrentFiscalClosure);
	    	System.out.println("SMTK - CurrentReceiptNumber="+DummyServerRT.CurrentReceiptNumber);
	    	System.out.println("SMTK - RTPrinterId="+SharedPrinterFields.RTPrinterId);
	    	System.out.println("SMTK - PrinterIpAdd="+SharedPrinterFields.Printer_IPAddress);
	    	System.out.println("SMTK - TransactionNumber="+transactionnumber);
	    	System.out.println("SMTK - voiding="+voiding);
	    	
	    	if (fiscalPrinterDriver.SMTKgetStatusReceipt(DummyServerRT.CurrentFiscalClosure, DummyServerRT.CurrentReceiptNumber, date[0].substring(0, 4)+date[0].substring(6, 8)))
	    	{
	    		if (PrinterType.isEpsonModel())
	    		{
			    	String PdfPath = buildPdfPath(SharedPrinterFields.Printer_IPAddress, date[0]);
			    	System.out.println("SMTK - PdfPath="+PdfPath);
			    	String PdfFilename = buildPdfFilename(PdfPath, DummyServerRT.CurrentFiscalClosure, DummyServerRT.CurrentReceiptNumber);
			    	if (PdfFilename != null && PdfFilename.length() > 0)
			    	{
				    	System.out.println("SMTK - PdfFilename="+PdfFilename);
				    	String XmlFilename = buildXmlFilename(PdfFilename);
				    	System.out.println("SMTK - XmlFilename="+XmlFilename);
				    	SMTKdownload(PdfPath+PdfFilename);
				    	SMTKdownload(PdfPath+XmlFilename);
				    	Xml4SRT.runXmlUpdater(XmlFilename, textfile, true, false, true, key, false);
				    	moveSmartTicket(PdfFilename, date[0].substring(0, 2), transactionnumber, "pdf", Integer.parseInt(DummyServerRT.CurrentReceiptNumber), voiding);
				    	moveSmartTicket(XmlFilename, date[0].substring(0, 2), transactionnumber, "xml", Integer.parseInt(DummyServerRT.CurrentReceiptNumber), voiding);
			    	}
			    	else
			    	{
			    		System.out.println("SMTK - Http connection timeout : download failed.");
			    		if (fiscalPrinterDriver.SMTKgetReceiptType() == SmartTicket.ERECEIPT_DIGITAL)
			    			fiscalPrinterDriver.reprintLastTicket();
			    	}
	    		}
	    		
	    		if (PrinterType.isDieboldRTOneModel())
	    		{
	    			String PdfFilename = SMTKdownload(rtsTrxBuilder.storerecallticket.Default.getsourcePath());
			    	if (PdfFilename != null && PdfFilename.length() > 0)
			    	{
				    	System.out.println("SMTK - PdfFilename="+PdfFilename);
				    	String XmlFilename = buildXmlFilename(PdfFilename);
				    	System.out.println("SMTK - XmlFilename="+XmlFilename);
				    	
						StringTokenizer st = new StringTokenizer(PdfFilename, ".");
						String[] tmp1 = new String[st.countTokens()];
						for (int i = 0; i < tmp1.length; i++) {
							tmp1[i] = st.nextToken();
						}
						st = new StringTokenizer(tmp1[0], "_");
						String[] tmp2 = new String[st.countTokens()];
						for (int i = 0; i < tmp2.length; i++) {
							tmp2[i] = st.nextToken();
						}
						String printerid = tmp2[0];
						int repz = Integer.parseInt(tmp2[3]);
						int nrec = Integer.parseInt(tmp2[4]);
						String datetime = "20"+tmp2[1]+"T"+tmp2[2]+"00";
						
						Xml4SRT.runXmlCoder(XmlFilename, "", SRTPrinterExtension.isPRT(), printerid, repz, nrec, datetime, smtkamount, SmartTicket.smtkbarcodes, SmartTicket.Smart_Ticket_CustomerType, SmartTicket.Smart_Ticket_CustomerId, true, false, true);
						Xml4SRT.runXmlUpdater(XmlFilename, textfile, true, false, true, key, false);
				    	moveSmartTicket(PdfFilename, date[0].substring(0, 2), transactionnumber, "pdf", nrec, voiding);
				    	moveSmartTicket(XmlFilename, date[0].substring(0, 2), transactionnumber, "xml", nrec, voiding);
			    	}
			    	else
			    	{
			    		System.out.println("SMTK - download failed.");
			    		if (SmartTicket.Smart_Ticket_ReceiptMode == SmartTicket.ERECEIPT_DIGITAL)
			    			fiscalPrinterDriver.reprintLastTicket();
			    	}
	    		}
	    	}
		}
		
		if (SRTPrinterExtension.isSRT())
		{
			// il file LastTicket.out viene già fatto normalmente
			
			String textfile = "";
			try {
		        BufferedReader in = new BufferedReader(new FileReader(SharedPrinterFields.lastticket));
		        String line;
		        StringBuilder sb = new StringBuilder();
		        while ((line = in.readLine()) != null) {
		        	sb.append(line+R3define.Lf);
		        }
		        in.close();
		        textfile = sb.toString();
			} catch (Exception e) {
				System.out.println("Smart_Ticket error : "+e.getMessage());
			}
	        
			String toEncode = SharedPrinterFields.lastticket;
			String Encoded = rtsTrxBuilder.storerecallticket.Default.getsourcePath()+"LastTicket.b64";
			String Decoded = "";
			if ((SmartTicket.getBase64_Decode() != null) && (SmartTicket.getBase64_Decode().length() > 0))
				Decoded = SmartTicket.getBase64_Decode()+"/"+SharedPrinterFields.lastticket.substring(SharedPrinterFields.lastticket.lastIndexOf("/")+1);
			try {
				Base64.Encode(toEncode, Encoded, Decoded);
			} catch (IOException e) {
				System.out.println("Base64 encoding error : "+e.getMessage());
			}
	    	moveSmartTicket(Encoded, DummyServerRT.getCurrent_dateTime().substring(6, 8), transactionnumber, "b64", Integer.parseInt(DummyServerRT.CurrentReceiptNumber), voiding);
	    	
	    	String XmlFilename = rtsTrxBuilder.storerecallticket.Default.getsourcePath()+"LastTicket.xml";
	    	Xml4SRT.runXmlCoder(XmlFilename, "", SRTPrinterExtension.isPRT(), DummyServerRT.SRTServerID, DummyServerRT.CurrentFiscalClosure, Integer.parseInt(DummyServerRT.CurrentReceiptNumber), DummyServerRT.getCurrent_dateTime(), smtkamount, SmartTicket.smtkbarcodes, SmartTicket.Smart_Ticket_CustomerType, SmartTicket.Smart_Ticket_CustomerId, true, false, true);
	    	Xml4SRT.runXmlUpdater(XmlFilename, textfile, true, false, true, key, false);
	    	moveSmartTicket(XmlFilename, DummyServerRT.getCurrent_dateTime().substring(6, 8), transactionnumber, "xml", Integer.parseInt(DummyServerRT.CurrentReceiptNumber), voiding);

/*			questo pezzo non serve più perchè il file pdf se lo costruisce il server partendo dal b64 che gli mandiamo, creato partendo dal file LastTicket.out

	    	String PdfFilename = rtsTrxBuilder.storerecallticket.Default.getsourcePath()+"LastTicket.pdf";
	    	String XmlFilename = rtsTrxBuilder.storerecallticket.Default.getsourcePath()+"LastTicket.xml";
	    	String ret = runPdfCoder(lastticket, PdfFilename);
	    	if ((ret == null) || (ret.length() == 0)) {
		    	System.out.println("SMTK - runPdfCoder failed.");
	    	}
	    	else {
		    	xml4srt.runXmlCoder(XmlFilename, "", RTPrinterId, CurrentFiscalClosure, Integer.parseInt(CurrentReceiptNumber), getCurrent_dateTime(), smtkamount, smtkbarcodes, SMTKgetCustomerType(), SMTKgetCustomerId(), true);
		    	moveSmartTicket(PdfFilename, getCurrent_dateTime().substring(6, 8), transactionnumber, "pdf", Integer.parseInt(CurrentReceiptNumber), voiding);
		    	moveSmartTicket(XmlFilename, getCurrent_dateTime().substring(6, 8), transactionnumber, "xml", Integer.parseInt(CurrentReceiptNumber), voiding);
	    	}*/
		}
		
    	SmartTicket.SMTKrestoreDefault();
	}
	
	public static String SMTKdownload(String url)
	{
		String ret = "";
		
		if (fiscalPrinterDriver.isfwSMTKdisabled())
			return ret;
		
		if (PrinterType.isEpsonModel())
		{
			String cmd = "wget --tries=1 --timeout=" + SmartTicket.Smart_Ticket_HttpTimeout + " "+url;
			Runtime rt = Runtime.getRuntime();
			Process proc;
			try {
				proc = rt.exec(cmd);
			} catch (IOException e) {
				System.out.println("SMTKdownload - e:"+e.getMessage());
				return ret;
			}					       
			int exitVal = -1;
			try {
				exitVal = proc.waitFor();
			} catch (InterruptedException e) {
				System.out.println("SMTKdownload - e:"+e.getMessage());
			}
			if (exitVal != 0)
				System.out.println("SMTKdownload - errore:"+exitVal);	   
		}
		
		if (PrinterType.isDieboldRTOneModel())
		{
			//StringBuffer sbcmd = new StringBuffer(url);
	      	//executeRTDirectIo(8600, 1, sbcmd);
	        int Command = 8600;
	        int[] dt={1};				// download last ticket
	        String[] pString = {url};
	        try {
	        	fiscalPrinterDriver.directIO(Command, dt, pString);
	        	ret = pString[0];
				System.out.println("SMTKdownload - ret = "+ret);
			} catch (JposException e) {
				System.out.println("SMTKdownload - e:"+e.getMessage());
			}
		}
		
		return ret;
	}
	
	private static void moveBase64Ticket(String sourcefile, String subfolder, int transactionnumber, String extension, int receiptnumber, boolean voiding)
	{
		moveSmartTicket(sourcefile, subfolder, transactionnumber, extension, receiptnumber, voiding);
	}
	
	private static void moveSmartTicket(String sourcefile, String subfolder, int transactionnumber, String extension, int receiptnumber, boolean voiding)
	{
		boolean ret = true;
		
		boolean clean = receiptnumber == 1;
		
		File f = new File(SmartTicket.ERECEIPT_DESTIN_FOLDER);
		if (!f.exists()) {
			System.out.println("SMTK - moveSmartTicket - creating: "+SmartTicket.ERECEIPT_DESTIN_FOLDER);	   
			ret = f.mkdirs();
			if (!ret)
				System.out.println("SMTK - moveSmartTicket - ret: "+ret);	   
		}
		if (!ret)
			return;
		
		String destinationfolder = SmartTicket.ERECEIPT_DESTIN_FOLDER + subfolder;
		f = new File(destinationfolder);
		if (!f.exists()) {
			System.out.println("SMTK - moveSmartTicket - creating: "+destinationfolder);	   
			ret = f.mkdirs();
			if (!ret)
				System.out.println("SMTK - moveSmartTicket - ret: "+ret);	   
		}
		if (ret) {
			if (clean) {
				String oldfiles = destinationfolder + "/" + "*." + extension;
				Files.removeFiles(oldfiles);
				if (extension.equalsIgnoreCase("pdf")) {
					oldfiles = oldfiles + "64";
					Files.removeFiles(oldfiles);
				}
			}
			
			String destfile = destinationfolder + "/" + transactionnumber + "." + extension;
			if (voiding)
				destfile = destinationfolder + "/" + transactionnumber + ".voided." + extension;
			Files.moveFile(sourcefile, destfile);
			
			if (extension.equalsIgnoreCase("pdf")) {
				// i pdf li inviamo convertiti in base64
				
				String toEncode = destfile;
				String Encoded = toEncode + "64";
				String Decoded = "";
				if ((SmartTicket.getBase64_Decode() != null) && (SmartTicket.getBase64_Decode().length() > 0))
					Decoded = SmartTicket.getBase64_Decode()+"/"+"LastTicket.pdf";
				try {
					Base64.Encode(toEncode, Encoded, Decoded);
				} catch (IOException e) {
					System.out.println("Base64 encoding error : "+e.getMessage());
				}
			}
		}
	}
	
	private static String buildPdfPath(String ip, String date)
	{
		String path = "";
		
		if (PrinterType.isEpsonModel()) {
			path = "http://"+ip+"/"+SmartTicket.ERECEIPT_SOURCE_FOLDER+date.substring(4,8)+date.substring(2, 4)+date.substring(0, 2)+"/";
		}
		return path;
	}
	
	private static String buildPdfFilename(String path, int zrep, String nrec)
	{
		String filename = "";
		
		if (PrinterType.isEpsonModel()) {
			String z = "Z"+Sprint.f("%04d", zrep);
			String n = "N"+nrec;
			String cmd = "wget --tries=1 --timeout=" + SmartTicket.Smart_Ticket_HttpTimeout +" --quiet -O - " + path + " | grep '.pdf' | grep " + z + "-" + n + " | cut -d\"\\\"\" -f2";
			String[] rsh = RunShellScriptPoli20.runScript(true, cmd);
			if ((rsh != null) && (rsh.length > 0)){
				filename = rsh[0];
			}
		}
		return filename;
	}
	
	private static String buildXmlFilename(String pdffilename)
	{
		String filename = "";
		
		filename = pdffilename.substring(0, pdffilename.lastIndexOf(".")) + "-ESITO-OK.xml";
		
		return filename;
	}
	
	public static void SMTKsetVoidReceiptType()
	{
	    if (SmartTicket.isSmart_Ticket())
	    {
	    	if (SRTPrinterExtension.isPRT()) {
	    		// qui setto i parametri per lo scontrino che sta per andare in stampa, secondo le impostazioni decise dall'interfaccia grafica
	    		// oppure con le impostazioni di default se non sono state specificate tramite l'interfaccia grafica
	    		fiscalPrinterDriver.SMTKsetReceiptType(SmartTicket.Smart_Ticket_ReceiptMode, SmartTicket.Smart_Ticket_Validity);
	    	}
	    	
	    	SmartTicket.SMTKbarcodes_reset();
	    }
	}
}

package iconic.mytrade.gutenbergPrinter.monitorrt;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import iconic.mytrade.gutenberg.jpos.printer.monitorrt.Aliquota;
import iconic.mytrade.gutenberg.jpos.printer.monitorrt.AliquotaTicket;
import iconic.mytrade.gutenberg.jpos.printer.monitorrt.Pagamento;
import iconic.mytrade.gutenberg.jpos.printer.service.properties.SRTPrinterExtension;
import iconic.mytrade.gutenberg.jpos.printer.srt.DummyServerRT;
import iconic.mytrade.gutenberg.jpos.printer.srt.Xml4SRT;
import iconic.mytrade.gutenberg.jpos.printer.utils.Files;
import iconic.mytrade.gutenberg.jpos.printer.utils.RunShellScriptPoli20;
import iconic.mytrade.gutenberg.jpos.printer.utils.Sprint;
import iconic.mytrade.gutenbergPrinter.PrinterCommands;
import iconic.mytrade.gutenbergPrinter.SharedPrinterFields;
import iconic.mytrade.gutenbergPrinter.lottery.LotteryCommands;
import iconic.mytrade.gutenbergPrinter.mop.LoadMops;
import iconic.mytrade.gutenbergPrinter.mop.Mop;
import iconic.mytrade.gutenbergPrinter.tax.DicoTaxLoad;
import iconic.mytrade.gutenbergPrinter.tax.DicoTaxObject;
import jpos.FiscalPrinterConst;
import jpos.events.DirectIOEvent;

public class MonitorRT extends PrinterCommands {
	private String TILLSTATUS_XML			= SharedPrinterFields.WorkingFolder+"/"+"TillStatus.xml";
	private String TRANSMISSIONSTATUS_XML	= SharedPrinterFields.WorkingFolder+"/"+"TransmissionStatus.xml";
	private String CHECKDETAILS_XML			= SharedPrinterFields.WorkingFolder+"/"+"CheckDetails.xml";
	
	private boolean INDENTMODE		= true;
	private boolean OMITDECLARATION	= false;
	private boolean OMITENCODE		= true;
	
	private int							UltimaChiusura	= 0;
	private int							DocumentiEmessi	= 0;
	private Double						Corrispettivo	= 0.;
	private Double						TotaleAnnullo	= 0.;
	private Double						TotaleReso		= 0.;
	private boolean						Ventilazione	= false;
	private boolean						Simulazione		= false;
	private int							DocLotteria		= 0;
	private ArrayList<Aliquota>			AliquoteRT		= new ArrayList();
	private ArrayList<Pagamento>		PagamentiRT		= new ArrayList();
	public ArrayList<AliquotaTicket>	AliquoteTicket	= new ArrayList();
	
	public void logMRT(String s) {
		System.out.println("RTMonitorRT - "+s);
	}
	
	public void preMonitorRT() throws Exception {
		// qui si raccolgono tutti i dati del giorno che devono essere richiesti alla stampante prima di eseguire il reportZ
		
		if (!SRTPrinterExtension.isPRT())
			return;
		
		logMRT("start");
		
    	Ventilazione = MixedVat();
    	this.logMRT("Ventilazione = "+Ventilazione);
    	
    	String[] dailySalesByVat = VatReport(0, 40);
    	String[] dailyRefundByVat = VatReport(0, 41);
    	String[] dailyVoidByVat = VatReport(0, 42);
    	AliquoteRT = doAliquote(dailySalesByVat, dailyRefundByVat, dailyVoidByVat);
        for (Aliquota aliquota : AliquoteRT) {
        	logMRT(aliquota.toString());
        }
        
        String[] dailyPayments = PaymentReport(0, 13);
        PagamentiRT = doPagamenti(dailyPayments);
        for (Pagamento pagamento : PagamentiRT) {
        	logMRT(pagamento.toString());
        }
	}

	public void doMonitorRT(String ipaddress, int zreport, String printerid, String date, String till) throws Exception
	{
		// qui si raccolgono tutti i dati del giorno che devono essere richiesti alla stampante dopo l'esecuzione del reportZ
		
		if (!SRTPrinterExtension.isPRT())
			return;
		
		logMRT("ipaddress = "+ipaddress+" - zreport = "+zreport+" - printerid = "+printerid+" - date = "+date+" - till = "+till);
		
		GetTillStatus(ipaddress, zreport, printerid, date, till);

		String zrepid = getZRepIdAnswer(zreport);
		if (zrepid != null && zrepid.length() > 0) {
			
			GetTransmissionStatus(ipaddress, zreport, printerid, date);
			
//			operation = 2;
//			GetTransmission(ipaddress, operation, zreport, printerid, date);
			
		}
		else {
			logMRT("TransmissionStatus report not available at "+ipaddress+" for "+date);
		}
		
		GetDetailsStatus();
		
		logMRT("end");
	}
	
	public void GetTicketDetails(String printerid, String date, int zreport, String docnum,
								  double total, ArrayList<AliquotaTicket> aliquoteRT, String textfile)
	{
		Xml4SRT.pleaseDoCheckDetail(CHECKDETAILS_XML, printerid, date, zreport, docnum, total, aliquoteRT, textfile, "???????????", "????????????", INDENTMODE, OMITDECLARATION, OMITENCODE);
	}
	
	private void GetDetailsStatus()
	{
		//xml4srt.pleaseMergeCheckDetail(".", CHECKDETAILS_XML, INDENTMODE, OMITDECLARATION, OMITENCODE);
	}
	
	private void GetTillStatus(String ip, int zrep, String id, String date, String till)
	{
		String[] reportZ = null;
		
		logMRT("GetTillStatus - ip = "+ip);
		logMRT("GetTillStatus - zrep = "+zrep);
		logMRT("GetTillStatus - id = "+id);
    	logMRT("GetTillStatus - date = "+date);
		logMRT("GetTillStatus - till = "+till);

		Simulazione = (getSimulation() == 1);
		String mydate = date.substring(0,2)+date.substring(2,4)+date.substring(6,8);	// from mmddyyyyhhmm to ddMMyy
		DocLotteria = LotteryCommands.getLotteryRec(till, mydate, zrep);
		
		int operation = 3;
		reportZ = GetTransmission(ip, operation, zrep, id, date);
		
		for (int i=0; i<reportZ.length; i++) {
			if (!reportZ[i].startsWith(id))
				continue;
			
			UltimaChiusura = Integer.parseInt(readFromFile(reportZ[i], "NUMERO AZZERAMENTI"));
			logMRT("UltimaChiusura="+UltimaChiusura);
			
			DocumentiEmessi = Integer.parseInt(readFromFile(reportZ[i], "NUMERO DOCUMENTI COMMERCIALI"));
			logMRT("DocumentiEmessi="+DocumentiEmessi);
			
			String tmp = makeDoubleParseable(readFromFile(reportZ[i], "TOTALE GIORNO VENDITE"));
			Corrispettivo = Double.parseDouble(tmp);
			logMRT("Corrispettivo="+Corrispettivo);
			
			tmp = makeDoubleParseable(readFromFile(reportZ[i], "TOTALE GIORNO ANNULLAMENTI"));
			TotaleAnnullo = Double.parseDouble(tmp);
			logMRT("TotaleAnnullo="+TotaleAnnullo);
			
			tmp = makeDoubleParseable(readFromFile(reportZ[i], "TOTALE GIORNO RESI"));
			TotaleReso = Double.parseDouble(tmp);
			logMRT("TotaleReso="+TotaleReso);
		}
		
		Xml4SRT.pleaseDoTillStatus(TILLSTATUS_XML, id, true, Simulazione, DocLotteria, Ventilazione,
								   UltimaChiusura, DocumentiEmessi, Corrispettivo, TotaleAnnullo, TotaleReso,
								   AliquoteRT, PagamentiRT, "???????????????????????????????", "???????????????????????????????",
								   INDENTMODE, OMITDECLARATION, OMITENCODE);
		
		for (int i=0; i<reportZ.length; i++) {
			delete(reportZ[i]);
		}
	}
	
	private void GetTransmissionStatus(String ip, int zrep, String id, String date)
	{
		logMRT("GetTransmissionStatus - ip = "+ip);
		logMRT("GetTransmissionStatus - zrep = "+zrep);
		logMRT("GetTransmissionStatus - id = "+id);
    	logMRT("GetTransmissionStatus - date = "+date);
		
		int operation = 1;
		String[] corrisp = GetTransmission(ip, operation, zrep, id, date);
		
		for (int i=0; i<corrisp.length; i++) {
			rename(corrisp[i], TRANSMISSIONSTATUS_XML);	// x ora fisso cosÃ¬
		}
	}
	
	private String[] GetTransmission(String ip, int operation, int zrep, String id, String date) {
		logMRT("GetTransmission - ip = "+ip);
		logMRT("GetTransmission - operation = "+operation);	// 1 = transmission, 2 = transmission result, 3 = zreport
		logMRT("GetTransmission - zrep = "+zrep);
		logMRT("GetTransmission - id = "+id);
    	logMRT("GetTransmission - date = "+date);
		
    	String[] Filenames = new String[1];

    	String Path = buildPath(ip,date);
    	
    	Filenames = buildFilename(Path, operation, zrep);
    	if (Filenames != null && Filenames.length > 0)
    	{
    		for (int i=0; i<Filenames.length; i++) {
    			if (!Filenames[i].startsWith(id))
    				continue;
    			TransmissionDownload(Path+Filenames[i]);
    		}
    	}
    	
    	return Filenames;
	}
	
	private String buildPath(String ip, String date)
	{
		logMRT("buildPath - ip = "+ip);
		logMRT("buildPath - date = "+date);
		
		String SOURCE_FOLDER = "www/dati-rt/";
		
		String path = "";
		
		path = "http://"+ip+"/"+SOURCE_FOLDER+date.substring(4,8)+date.substring(2, 4)+date.substring(0, 2)+"/";
		logMRT("path = "+path);
		
		return path;
	}
	
	private String[] buildFilename(String path, int operation)
	{
		logMRT("buildFilename - path = "+path);
		logMRT("buildFilename - operation = "+operation);	// 1 = transmission, 2 = transmission result, 3 = zreport

		int HttpTimeout = 3;	// sec.
		
    	String[] rsh = new String[1];
		
    	String type = "";
    	String cmd = "";
    	if (operation == 1)
    		type = "CORRISP";
    	if (operation == 2)
    		type = "ESITO";
    	if (operation == 3)
    		type = "ZREPORT";
		cmd = "wget --tries=1 --timeout=" + HttpTimeout +" --quiet -O - " + path + " | grep '" + type + "' | sed \\\"s/href/href\\\\n/g\\\" | cut -d\"\\\"\" -f2";
		logMRT("cmd = "+cmd);
		
		rsh = RunShellScriptPoli20.runScript(true, cmd);
		
		return rsh;
	}
	
	private String[] buildFilename(String path, int operation, int zrep)
	{
		logMRT("buildFilename - path = "+path);
		logMRT("buildFilename - operation = "+operation);	// 1 = transmission, 2 = transmission result, 3 = zreport
		logMRT("buildFilename - zrep = "+zrep);
		
		int HttpTimeout = 3;	// sec.
		
    	String[] rsh = new String[1];
    	
    	String z = Sprint.f("%04d", zrep);
		
    	String type = "";
    	String cmd = "";
    	if (operation == 1)
    		type = "-CORRISP";
    	if (operation == 2)
    		type = "-ESITO";
    	if (operation == 3)
    		type = "-ZREPORT";
		cmd = "wget --tries=1 --timeout=" + HttpTimeout +" --quiet -O - " + path + " | grep '" + z + type + "' | sed \"s/href/href\\n/g\" | cut -d\"\\\"\" -f2";
		logMRT("cmd = "+cmd);
		
		rsh = RunShellScriptPoli20.runScript(true, cmd);
		
		return rsh;
	}
	
	private String TransmissionDownload(String url)
	{
		logMRT("TransmissionDownload - url = "+url);

		String ret = "";
		
		int HttpTimeout = 3;	// sec.
		
		String cmd = "wget --tries=1 --timeout=" + HttpTimeout + " "+url;
		Runtime rt = Runtime.getRuntime();
		Process proc;
		try {
			proc = rt.exec(cmd);
		} catch (IOException e) {
			logMRT("TransmissionDownload - e:"+e.getMessage());
			return ret;
		}					       
		int exitVal = -1;
		try {
			exitVal = proc.waitFor();
		} catch (InterruptedException e) {
			logMRT("TransmissionDownload - e:"+e.getMessage());
		}
		if (exitVal != 0)
			logMRT("TransmissionDownload - errore:"+exitVal);	   
		
		return ret;
	}
	
	private String[] GetZReport(String id, int zrep, String date) {
    	String[] Filenames = new String[1];
    	
		return Filenames;
	}

    private String readFromFile(String filename, String token)
    {
    	String ret = "";
    	
		File FILE = new File(filename);
		if (!FILE.exists()) {
			logMRT("readFromFile - "+filename+" does not exist");
			return ret;
		}
		FileInputStream fstream;
		try {
			fstream = new FileInputStream(FILE);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line;
			while ((line = br.readLine()) != null)
			{
				if (line.startsWith(token)) {
					ret = line.substring(line.lastIndexOf(" ")+1);
					break;
				}
			}	
			in.close();
		} catch (Exception e) {
			logMRT("readFromFile - exception:"+e.getMessage());
		}
    	return ret;
    }
    
    private void readFromFile(String filename, int zrep)
    {
		File FILE = new File(filename);
		if (!FILE.exists()) {
			logMRT("readFromFile - "+filename+" does not exist");
			return;
		}
		FileInputStream fstream;
		try {
			fstream = new FileInputStream(FILE);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line;
			String subline;
			while ((line = br.readLine()) != null)
			{
				//logMRT(line);
				if (line.contains("TOTALE VENDITE")) {
					subline = line.substring(line.lastIndexOf(" ")+1);
					line = makeDoubleParseable(subline);
					Corrispettivo+=Double.parseDouble(line);
				}
				
				if (line.contains("TOTALE RESI")) {
					subline = line.substring(line.lastIndexOf(" ")+1);
					line = makeDoubleParseable(subline);
					TotaleReso+=Double.parseDouble(line);
				}
				
				if (line.contains("TOTALE ANNULLI")) {
					subline = line.substring(line.lastIndexOf(" ")+1);
					line = makeDoubleParseable(subline);
					TotaleAnnullo+=Double.parseDouble(line);
				}
				
				if (line.startsWith("CHIUSURA GIORNALIERA N.")) {
					UltimaChiusura = Integer.parseInt(line.substring(line.lastIndexOf(" ")+1));
					if (UltimaChiusura != zrep) {	// bisogna considerare solo l'ultimo
						Corrispettivo = 0.;
						TotaleAnnullo = 0.;
						TotaleReso = 0.;
					}
				}
				
				if (line.startsWith("DOCUMENTI COMMERCIALI")) {
					if (UltimaChiusura == zrep) {
						DocumentiEmessi = Integer.parseInt(line.substring(line.lastIndexOf(" ")+1));
						break;
					}
				}
			}	
			in.close();
			
			logMRT("Corrispettivo = "+Corrispettivo);
			logMRT("TotaleReso = "+TotaleReso);
			logMRT("TotaleAnnullo = "+TotaleAnnullo);
			logMRT("DocumentiEmessi = "+DocumentiEmessi);
			logMRT("UltimaChiusura = "+UltimaChiusura);
			
		} catch (Exception e) {
			logMRT("readFromFile - exception:"+e.getMessage());
		}
    }
    
    boolean started = true;
    String buffer = "";
    
    private class ZRepDirectIOListener implements jpos.events.DirectIOListener
	{
		public void directIOOccurred(DirectIOEvent arg0) {
			 if ((arg0.getSource().toString() != null) &&
				 (arg0.getSource().toString().startsWith("jpos.service.FiscalPrinter"))) {
				 buffer+=(String)arg0.getObject().toString();
				 buffer+="\n";
				 started = false;
			 }
		}
	}
    
    private class XmlDirectIOListener implements jpos.events.DirectIOListener
	{
		public void directIOOccurred(DirectIOEvent arg0) {
			 if ((arg0.getSource().toString() != null) &&
				 (arg0.getSource().toString().startsWith("jpos.service.FiscalPrinter"))) {
				 if (((String)arg0.getObject().toString().trim()).startsWith("<"))	// altrimenti l'ultima riga è sporca e invalida l'xml
					 buffer+=(String)arg0.getObject().toString();
				 started = false;
			 }
		}
	}
    
    private String makeDoubleParseable(String s)
    {
    	return(s.replaceAll("\\.", "").replaceAll(",", "\\."));
    }
    
    private void delete(String filename) {
    	logMRT("trying to delete "+filename);
    	Files.removeFile(filename);
    }
    
    private void rename(String oldfilename, String newfilename) {
    	logMRT("trying to rename "+oldfilename+" in "+newfilename);
    	Files.moveFile(oldfilename, newfilename);
    }

    // Gestione totali Aliquote e Pagamenti della giornata fiscale - Start
    
    private ArrayList<Aliquota> doAliquote(String[] dailySalesByVat, String[] dailyRefundByVat, String[] dailyVoidByVat) {
        Map<String, double[]> aliquoteMap = new HashMap<String, double[]>();

        processVat(dailySalesByVat, aliquoteMap, 0);
        processVat(dailyRefundByVat, aliquoteMap, 1);
        processVat(dailyVoidByVat, aliquoteMap, 2);

        ArrayList<Aliquota> result = new ArrayList<Aliquota>();
        for (Map.Entry<String, double[]> entry : aliquoteMap.entrySet()) {
            String numeroAliquota = entry.getKey();
            double[] amounts = entry.getValue();
            ArrayList taxes = this.getTaxNumber(Integer.parseInt(numeroAliquota));
            result.add(new Aliquota(numeroAliquota, amounts[0], amounts[1], amounts[2], taxes));
        }

        return result;
    }
    
    private void processVat(String[] transactions, Map<String, double[]> aliquoteMap, int index) {
        if (transactions == null || transactions.length == 0) return;

        for (String transaction : transactions) {
            String numeroAliquota = transaction.substring(2, 4);
            double ammontare = calcAmount(transaction.substring(5, 14), transaction.charAt(4));
            double tassa = calcAmount(transaction.substring(15, 24), transaction.charAt(14));

            double[] amounts = getOrDefault(aliquoteMap, numeroAliquota, new double[]{0.0, 0.0, 0.0});
            amounts[index] += Math.rint((ammontare+tassa)*100)/100;

            aliquoteMap.put(numeroAliquota, amounts);
        }
    }
    
    private String numeroAliquotaFromChar(char c) {
        if (Character.isDigit(c)) {
            return String.valueOf(c);
        } else if (c >= 'A' && c <= 'Z') {
            return String.valueOf(c - 'A' + 10);
        } else if (c >= 'a' && c <= 'd') {
            return String.valueOf(c - 'a' + 36);
        }
        return String.valueOf(-1);
    }

    private double calcAmount(String value, char sign) {
        double amount = Double.parseDouble(value) / 100.0;
        return sign == '-' ? -amount : amount;
    }

    private <K, V> V getOrDefault(Map<K, V> map, K key, V defaultValue) {
        if (map.containsKey(key)) {
            return map.get(key);
        } else {
            return defaultValue;
        }
    }
    
    private ArrayList getTaxNumber(int aliquotaRt) {
  	  	logMRT("getTaxNumber : "+aliquotaRt);
  	  	
    	ArrayList ret = new ArrayList();
    	
  	  	for ( int i = 0; i < DicoTaxLoad.DicoTaxSize() ; i++ )
  	  	{
  	  		DicoTaxObject tax = DicoTaxLoad.get(i);
  	  		//logMRT("<"+tax.getTaxnumber()+"> - <"+tax.getPrinterTaxnumber()+"> - <"+tax.getDescription()+"> - <"+tax.getShortdescription()+"> - <"+tax.getTaxrate()+"> - <"+tax.getPrinterDeptnumber()+">");
  	  		if (aliquotaRt == tax.getPrinterDeptnumber())
  	  			ret.add(tax.getTaxnumber());
  	  	}
  	  	
  	  	logMRT("getTaxNumber : "+ret.toString());
    	return ret;
    }
	    
    private ArrayList<Pagamento> doPagamenti(String[] dailyPayments) {
        Map<String, Pagamento> pagamentiMap = new HashMap<String, Pagamento>();
        
        if (dailyPayments == null || dailyPayments.length == 0) return new ArrayList<Pagamento>();

        for (String payment : dailyPayments) {
            String tipoPagamento = payment.substring(0, 2);
            String identificativoPagamento = payment.substring(2, 4);
            String numeroPagamento = tipoPagamento + identificativoPagamento;
            numeroPagamento = doRtCode(Integer.parseInt(tipoPagamento));
            
            char quantitaSegno = payment.charAt(4);
            double quantita = Double.parseDouble(payment.substring(5, 14)) * (quantitaSegno == '-' ? -1 : 1);
            
            char ammontareSegno = payment.charAt(14);
            double ammontare = Double.parseDouble(payment.substring(15, 24)) / 100.0 * (ammontareSegno == '-' ? -1 : 1);
            
            ArrayList mops = this.getMediaInfo(Integer.parseInt(numeroPagamento));
            
            Pagamento pagamento = getOrDefault(pagamentiMap, numeroPagamento, new Pagamento(numeroPagamento, 0.0, 0.0, mops));
            pagamento.ammontare += ammontare;
            pagamento.quantita += quantita;

            pagamentiMap.put(numeroPagamento, pagamento);
        }

        return new ArrayList<Pagamento>(pagamentiMap.values());
    }
    
    private String doRtCode(int paymentCode) {
    	// converts from printer payment code to mediainfo format payment code
    	
    	int rtcode = 0;
    	
    	switch (paymentCode)
    	{
    		case 0:				// Rch returned payment codes (0-7)
    		case 13:			// Epson returned payment codes (13-81)
    			rtcode = 0;
    			break;
    		case 1:
    		case 18:
    			rtcode = 201;
    			break;
    		case 2:
    		case 74:
    			rtcode = 501;
    			break;
    		case 3:
    		case 75:
    			rtcode = 502;
    			break;
    		case 4:
    		case 76:
    			rtcode = 503;
    			break;
    		case 5:
    		case 78:
    			rtcode = 505;
    			break;
    		case 6:
    		case 81:
    			rtcode = 600;
    			break;
    		case 7:
    		case 19:
    			rtcode = 401;
    			break;
    		case 17:
    			rtcode = 101;
    			break;
    		case 73:
    			rtcode = 500;
    			break;
    		case 80:
    			rtcode = 601;
    			break;
    		default:
    			break;
    	}
    	
    	String reply = Sprint.f("%03d", rtcode);
    	return reply;
    }
    
    private ArrayList getMediaInfo(int rtcode) {
  	  	logMRT("getMediaInfo : "+rtcode);
  	  	
    	ArrayList ret = new ArrayList();
    	
    	LoadMops.loadMops();
    	for (int i=0; i<LoadMops.Mops.size(); i++) {
  	  		Mop mop = (Mop) LoadMops.Mops.get(i);
  	  		//logMRT("<"+mop.getType()+"> - <"+mop.getInd()+"> - <"+mop.getDescription()+"> - <"+mop.getSrtdescription()+">");
  	  		if (rtcode == (mop.getType()*100)+mop.getInd())
  	  			ret.add(mop.getDescription());
    	}
    	
  	  	logMRT("getMediaInfo : "+ret.toString());
    	return ret;
    }

    // Gestione totali Aliquote e Pagamenti della giornata fiscale - End

    // Gestione totali Aliquote e Pagamenti dello scontrino corrente - Start
    
	public void MonitorRT_Ticket(int trxnum, int zrepnum, String docnum, String printerid/*, String date*/) throws Exception {
		// qui si raccolgono tutti i dati dello scontrino corrente
		
		if (!SRTPrinterExtension.isPRT())
			return;
		
    	if (zrepnum == 0) {
	        int[] ai = new int[1];
	        String[] as = new String[1];
            getData(FiscalPrinterConst.FPTR_GD_Z_REPORT, ai, as);
            DummyServerRT.CurrentFiscalClosure = Integer.parseInt(as[0])+1;
           	zrepnum = DummyServerRT.CurrentFiscalClosure;
    	}
    	
		logMRT("MonitorRT_Ticket - trxnum = "+trxnum);
		logMRT("MonitorRT_Ticket - zrepnum = "+zrepnum);
		logMRT("MonitorRT_Ticket - docnum = "+docnum);
		logMRT("MonitorRT_Ticket - printerid = "+printerid);
//		logMRT("MonitorRT_Ticket - date = "+date);
		
    	String[] ticketSalesByVat = VatReport(0, 44);
    	AliquoteTicket = doAliquoteTicket(ticketSalesByVat);
        for (AliquotaTicket aliquota : AliquoteTicket) {
        	logMRT(aliquota.toString());
        }
	}

    private ArrayList<AliquotaTicket> doAliquoteTicket(String[] ticketSalesByVat) {
        Map<String, AliquotaTicket> aliquoteMap = new HashMap<String, AliquotaTicket>();
        
        if (ticketSalesByVat == null || ticketSalesByVat.length == 0) return new ArrayList<AliquotaTicket>();

        for (String sale : ticketSalesByVat) {
            String numeroAliquota = sale.substring(2, 4);
            double imponibile = calcAmount(sale.substring(5, 14), sale.charAt(4));
            double imposta = calcAmount(sale.substring(15, 24), sale.charAt(14));
            double importo = Math.rint((imponibile+imposta)*100)/100;
            
            AliquotaTicket aliquotaTicket = getOrDefault(aliquoteMap, numeroAliquota, new AliquotaTicket(numeroAliquota, 0.0, 0.0, 0.0));
            aliquotaTicket.importo += importo;
            aliquotaTicket.imponibile += imponibile;
            aliquotaTicket.imposta += imposta;

            if (aliquotaTicket.importo != 0.)
            	aliquoteMap.put(numeroAliquota, aliquotaTicket);
        }

        return new ArrayList<AliquotaTicket>(aliquoteMap.values());
    }
    
    private ArrayList<AliquotaTicket> doAliquoteTicket(String[] ticketTaxableByVat, String[] ticketTaxByVat) {
        Map<String, Double> imponibileMap = new HashMap<String, Double>();
        Map<String, Double> impostaMap = new HashMap<String, Double>();

        for (String taxable : ticketTaxableByVat) {
            String numeroAliquota = numeroAliquotaFromChar(taxable.charAt(13));
            double imponibile = calcAmount(taxable.substring(14, 24), '+');
            imponibileMap.put(numeroAliquota, imponibile);
        }

        for (String tax : ticketTaxByVat) {
            String numeroAliquota = numeroAliquotaFromChar(tax.charAt(13));
            double imposta = calcAmount(tax.substring(14, 24), '+');
            impostaMap.put(numeroAliquota, imposta);
        }

        ArrayList<AliquotaTicket> result = new ArrayList<AliquotaTicket>();
        for (String numeroAliquota : imponibileMap.keySet()) {
            double imponibile = imponibileMap.get(numeroAliquota);
            double imposta = getOrDefault(impostaMap, numeroAliquota, 0.0);
            double importo = Math.rint((imponibile+imposta)*100)/100;

            AliquotaTicket aliquotaTicket = new AliquotaTicket(numeroAliquota, importo, imponibile, imposta);
            result.add(aliquotaTicket);
        }

        return result;
    }
    
    // Gestione totali Aliquote e Pagamenti dello scontrino corrente - End
}

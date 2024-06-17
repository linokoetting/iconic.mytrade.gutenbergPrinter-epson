package iconic.mytrade.gutenbergPrinter.lottery;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import iconic.mytrade.gutenberg.jpos.printer.service.PosApp;
import iconic.mytrade.gutenberg.jpos.printer.service.R3define;
import iconic.mytrade.gutenberg.jpos.printer.service.properties.PrinterType;
import iconic.mytrade.gutenberg.jpos.printer.service.utils.Sprint;
import iconic.mytrade.gutenberg.srt.RTConsts;
import iconic.mytrade.gutenbergPrinter.PrinterCommands;
import iconic.mytrade.gutenbergPrinter.SharedPrinterFields;
import jpos.FiscalPrinterConst;

public class LotteryReportCommands extends PrinterCommands {
	
	public void printLotteryReport()
	{
		if (!PrinterType.isEpsonModel())
			return;
		
		String till = PosApp.getTillNumber();
		
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -1);	// yesterday
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyy");
        String date = sdf.format(c.getTime());
        
        int[] ai = new int[1];
        String[] as = new String[1];
        getData(FiscalPrinterConst.FPTR_GD_Z_REPORT, ai, as);
        int repz = 0;
        try {
        	repz = Integer.parseInt(as[0]);
        } catch (NumberFormatException e) {
		   System.out.println("printLotteryReport - Exception : " + e.getMessage());
		   return;
        }
        
        System.out.println("printLotteryReport - till = " + till + " date = " + date + " repz = " + repz);
        
		checkLTStatus_Epson(till, date, repz);
		checkILTStatus_Epson(till, date, repz);
	}
	
	private void checkLTStatus_Epson(String till, String date, int repz)
	{
		Calendar c = Calendar.getInstance();
		String dayofmonth = Sprint.f("%02d", c.getTime().getDate());
		String filename = SharedPrinterFields.rtlog_folder+SharedPrinterFields.ltlog_name+"_"+dayofmonth+SharedPrinterFields.rtlog_ext;
		
		int lotterytype = 0;	// Deferred lottery
		LotteryStatus status = ReadLotteryStatus(lotterytype, till, date, repz);
		
		if (status != null)
		{
			System.out.println("checkLTStatus - LT tillId : " + status.getTillId());
			System.out.println("checkLTStatus - LT zRepNum : " + status.getzRepNum());
			System.out.println("checkLTStatus - LT date : " + status.getDate());
			System.out.println("checkLTStatus - LT kindOfRequest : " + status.getKindOfRequest());
			System.out.println("checkLTStatus - LT filesToSend : " + status.getFilesToSend());
			System.out.println("checkLTStatus - LT oldFilesToSend : " + status.getOldFilesToSend());
			System.out.println("checkLTStatus - LT rejectedFiles : " + status.getRejectedFiles());
			System.out.println("checkLTStatus - LT waitingReceipts : " + status.getWaitingReceipts());
			System.out.println("checkLTStatus - LT receiptsToSend : " + status.getReceiptsToSend());
			System.out.println("checkLTStatus - LT acceptedReceipts : " + status.getAcceptedReceipts());
			System.out.println("checkLTStatus - LT rejectedReceipts : " + status.getRejectedReceipts());
			
			status.setTillId(till);	// x mettere la cassa giusta nel log seguente
			
			logLTStatus(status, filename);
		}
		else
		{
			   System.out.println("checkLTStatus - LotteryStatus is null");
			   return;
		}
		
		int recnum = 0;	// the next lottery receipt
		int type = 1;	// with errors
		int NOTFOUND = 5;
		while (true)
		{
			LotteryReceiptStatus recstatus = ReadLotteryReceiptStatus(till, repz, recnum, date, type);
			if (recstatus != null)
			{
				System.out.println("checkLTStatus - LT tillId : " + recstatus.getTillId());
				System.out.println("checkLTStatus - LT zRepNum : " + recstatus.getzRepNum());
				System.out.println("checkLTStatus - LT recNum : " + recstatus.getRecNum());
				System.out.println("checkLTStatus - LT recDate : " + recstatus.getRecDate());
				System.out.println("checkLTStatus - LT result : " + recstatus.getResult());
				System.out.println("checkLTStatus - LT errCode : " + recstatus.getErrCode());
				System.out.println("checkLTStatus - LT idAnswer : " + recstatus.getIdAnswer());
				System.out.println("checkLTStatus - LT kindOfReceipt : " + recstatus.getKindOfReceipt());
				
				if (recstatus.getResult() == NOTFOUND)
					break;
				
				recstatus.setTillId(till);	// x mettere la cassa giusta nel log seguente
				
				logLTStatus(recstatus, filename);
				
				recnum = recstatus.getRecNum();
			}
			else
			{
				   System.out.println("checkLTStatus - LotteryReceiptStatus is null");
				   return;
			}
		}
		
		printLTStatus(filename);
	}
	
	public LotteryStatus ReadLotteryStatus(int requesttype, String till, String date, int repz) {
		LotteryStatus status = null;
		
		if (PrinterType.isEpsonModel() && SharedPrinterFields.Lotteria.isLotteryOn())
		{
			String operator = "01";
			String zrep_num = Sprint.f("%04d", repz);
			String kind = Sprint.f("%02d", requesttype);
			
			StringBuffer command = new StringBuffer(operator + "00000000" + zrep_num + date + kind);
			SharedPrinterFields.Lotteria.LotteryTrace("ReadLotteryStatus - command : <"+command.toString()+">");
			fiscalPrinterDriver.executeRTDirectIo(1134, 0, command);
			SharedPrinterFields.Lotteria.LotteryTrace("ReadLotteryStatus - result : "+command.toString());
			if (command.toString().length() <= 2)	// sarà corretto per capire la situazione di errore ?
				return  null;
			
			status = new LotteryStatus(command.toString());
		}
		return status;
	}
	
	public LotteryReceiptStatus ReadLotteryReceiptStatus(String till, int repz, int recnum, String date, int type) {
		LotteryReceiptStatus status = null;
		
		if (PrinterType.isEpsonModel() && SharedPrinterFields.Lotteria.isLotteryOn())
		{
			String zrep_num = Sprint.f("%04d", repz);
			String rec_num = Sprint.f("%04d", recnum);
			String kind = Sprint.f("%02d", type);
			
			StringBuffer command = new StringBuffer("00000000" + zrep_num + rec_num + date + kind);
			SharedPrinterFields.Lotteria.LotteryTrace("ReadLotteryReceiptStatus - command : <"+command.toString()+">");
			fiscalPrinterDriver.executeRTDirectIo(9218, 0, command);
			SharedPrinterFields.Lotteria.LotteryTrace("ReadLotteryReceiptStatus - result : "+command.toString());
			if (command.toString().length() <= 2)	// sarà corretto per capire la situazione di errore ?
				return  null;
			
			status = new LotteryReceiptStatus(command.toString());
		}
		return status;
	}
	
	private void checkILTStatus_Epson(String till, String date, int repz)
	{
		Calendar c = Calendar.getInstance();
		String dayofmonth = Sprint.f("%02d", c.getTime().getDate());
		String filename = SharedPrinterFields.rtlog_folder+SharedPrinterFields.iltlog_name+"_"+dayofmonth+SharedPrinterFields.rtlog_ext;
		
		int lotterytype = 1;	// Instant lottery
		LotteryStatus status = ReadLotteryStatus(lotterytype, till, date, repz);
		
		if (status != null)
		{
			System.out.println("checkILTStatus - ILT tillId : " + status.getTillId());
			System.out.println("checkILTStatus - ILT zRepNum : " + status.getzRepNum());
			System.out.println("checkILTStatus - ILT date : " + status.getDate());
			System.out.println("checkILTStatus - ILT kindOfRequest : " + status.getKindOfRequest());
			System.out.println("checkILTStatus - ILT filesToSend : " + status.getFilesToSend());
			System.out.println("checkILTStatus - ILT oldFilesToSend : " + status.getOldFilesToSend());
			System.out.println("checkILTStatus - ILT rejectedFiles : " + status.getRejectedFiles());
			System.out.println("checkILTStatus - ILT waitingReceipts : " + status.getWaitingReceipts());
			System.out.println("checkILTStatus - ILT receiptsToSend : " + status.getReceiptsToSend());
			System.out.println("checkILTStatus - ILT acceptedReceipts : " + status.getAcceptedReceipts());
			System.out.println("checkILTStatus - ILT rejectedReceipts : " + status.getRejectedReceipts());
			System.out.println("checkILTStatus - ILT NumRemainingCodes : " + status.getNumRemainingCodes());
			System.out.println("checkILTStatus - ILT ILVersion : " + status.getILVersion());
			System.out.println("checkILTStatus - ILT LastReqResult : " + status.getLastReqResult());
			System.out.println("checkILTStatus - ILT SubError : " + status.getSubError());
			
			status.setTillId(till);	// x mettere la cassa giusta nel log seguente
			
			logILTStatus(status, filename);
		}
		else
		{
			   System.out.println("checkILTStatus - LotteryStatus is null");
			   return;
		}
		
		printLTStatus(filename);
	}
	
	private void logILTStatus(LotteryStatus status, String filename)
	{
		   String HEADER = "LOTTERIA ISTANT. ";
		   String TILLID = "CASSA : ";
		   String ZREPNUM = "CHIUSURA FISCALE : ";
		   String DATE = "DATA DEL PRIMO CODICE DISP. : ";
		   String FILESTOSEND = "FILES DA INVIARE : ";
		   String OLDFILESTOSEND = "FILES VECCHI DA INVIARE : ";
		   String REJECTEDFILES = "RICHIESTE CODICI RIFIUTATE : ";
		   String WAITINGRECEIPTS = "SCONTRINI IN ATTESA : ";
		   String RECEIPTSTOSEND = "SCONTRINI DA INVIARE : ";
		   String ACCEPTEDRECEIPTS = "SCONTRINI ACCETTATI : ";
		   String REJECTEDRECEIPTS = "SCONTRINI RIFIUTATI : ";
		   String NUMREMAININGCODES = "CHIAVI DISPONIBILI RIMANENTI : ";
		   String ILVERSION = "VERSIONE LOTTERIA ISTANTANEA : ";
		   String LASTREQRESULT = "ULTIMO CODICE RISPOSTA ADE : ";
		   String SUBERROR = "ULTIMO ERRORE RISPOSTA ADE : ";
		   
		   File inout = null;		
		   FileOutputStream fos = null;
		   PrintStream ps = null;
		   
		   Calendar c = Calendar.getInstance();
           SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
           
           String s = sdf.format(c.getTime()); 
		   
		   s = s + " - " + HEADER;
		   
		   try {
			   //String dayofmonth = Sprint.f("%02d", c.getTime().getDate());
			   //String filename = rtlog_folder+ltlog_name+"_"+dayofmonth+rtlog_ext;
			   inout = new File(filename);
			   fos = new FileOutputStream(inout,false);
			   ps = new PrintStream(fos);
			   
			   ps.println(s);
			   ps.println(TILLID + status.getTillId());
			   ps.println(ZREPNUM + status.getzRepNum());
			   ps.println(DATE + status.getDate());
			   //ps.println("Lottery kindOfRequest : " + status.kindOfRequest);
			   ps.println(FILESTOSEND + status.getFilesToSend());
			   ps.println(OLDFILESTOSEND + status.getOldFilesToSend());
			   ps.println(REJECTEDFILES + status.getRejectedFiles());
			   ps.println(WAITINGRECEIPTS + status.getWaitingReceipts());
			   ps.println(RECEIPTSTOSEND + status.getReceiptsToSend());
			   ps.println(ACCEPTEDRECEIPTS + status.getAcceptedReceipts());
			   ps.println(REJECTEDRECEIPTS + status.getRejectedReceipts());
			   ps.println(NUMREMAININGCODES + status.getNumRemainingCodes());
			   ps.println(ILVERSION + status.getILVersion());
			   ps.println(LASTREQRESULT + status.getLastReqResult());
			   ps.println(SUBERROR + status.getSubError());
			   
			   ps.close();
			   ps = null;
			   fos.close();
			   fos = null;
			   inout = null;
		   } catch(Exception e) {
			   System.out.println("logILTStatus - Exception : " + e.getMessage());
		   }
	}
	
	private void logLTStatus(LotteryStatus status, String filename)
	{
		   String HEADER = "LOTTERIA DIFFER. ";
		   String TILLID = "CASSA : ";
		   String ZREPNUM = "CHIUSURA FISCALE : ";
		   String DATE = "DATA : ";
		   String FILESTOSEND = "FILES DA INVIARE : ";
		   String OLDFILESTOSEND = "FILES VECCHI DA INVIARE : ";
		   String REJECTEDFILES = "FILES RIFIUTATI : ";
		   String WAITINGRECEIPTS = "SCONTRINI IN ATTESA : ";
		   String RECEIPTSTOSEND = "SCONTRINI DA INVIARE : ";
		   String ACCEPTEDRECEIPTS = "SCONTRINI ACCETTATI : ";
		   String REJECTEDRECEIPTS = "SCONTRINI RIFIUTATI : ";
		   
		   File inout = null;		
		   FileOutputStream fos = null;
		   PrintStream ps = null;
		   
		   Calendar c = Calendar.getInstance();
           SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
           
           String s = sdf.format(c.getTime()); 
		   
		   s = s + " - " + HEADER;
		   
		   try {
			   //String dayofmonth = Sprint.f("%02d", c.getTime().getDate());
			   //String filename = rtlog_folder+ltlog_name+"_"+dayofmonth+rtlog_ext;
			   inout = new File(filename);
			   fos = new FileOutputStream(inout,false);
			   ps = new PrintStream(fos);
			   
			   ps.println(s);
			   ps.println(TILLID + status.getTillId());
			   ps.println(ZREPNUM + status.getzRepNum());
			   ps.println(DATE + status.getDate());
			   //ps.println("Lottery kindOfRequest : " + status.kindOfRequest);
			   ps.println(FILESTOSEND + status.getFilesToSend());
			   ps.println(OLDFILESTOSEND + status.getOldFilesToSend());
			   ps.println(REJECTEDFILES + status.getRejectedFiles());
			   ps.println(WAITINGRECEIPTS + status.getWaitingReceipts());
			   ps.println(RECEIPTSTOSEND + status.getReceiptsToSend());
			   ps.println(ACCEPTEDRECEIPTS + status.getAcceptedReceipts());
			   ps.println(REJECTEDRECEIPTS + status.getRejectedReceipts());
			   
			   ps.close();
			   ps = null;
			   fos.close();
			   fos = null;
			   inout = null;
		   } catch(Exception e) {
			   System.out.println("logLTStatus - Exception : " + e.getMessage());
		   }
	}
	
	private void logLTStatus(LotteryReceiptStatus status, String filename)
	{
		   //String HEADER = "\nREPORT SCONTRINI LOTTERIA";
		   String TILLID = "\nCASSA : ";
		   String ZREPNUM = "CHIUSURA FISCALE : ";
		   String RECNUM = "NUMERO SCONTRINO : ";
		   String DATE = "DATA : ";
		   String RESULT = "ESITO RICHIESTA : ";
		   String ERRCODE = "CODICE DI ERRORE : ";
		   String ANSWERID = "ID. RISPOSTA : ";
		   
		   File inout = null;		
		   FileOutputStream fos = null;
		   PrintStream ps = null;
		   
		   //Calendar c = Calendar.getInstance();
           //SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
           
           //String s = sdf.format(c.getTime()); 
		   
		   //s = s + " - " + HEADER;
		   
		   try {
			   //String dayofmonth = Sprint.f("%02d", c.getTime().getDate());
			   //String filename = rtlog_folder+ltlog_name+"_"+dayofmonth+rtlog_ext;
			   inout = new File(filename);
			   fos = new FileOutputStream(inout,true);
			   ps = new PrintStream(fos);
			   
			   //ps.println(s);
			   ps.println(TILLID + status.getTillId());
			   ps.println(ZREPNUM + status.getzRepNum());
			   ps.println(RECNUM + status.getRecNum());
			   ps.println(DATE + status.getRecDate());
			   ps.println(RESULT + status.getResult());
			   ps.println(ERRCODE + status.getErrCode());
			   ps.println(ANSWERID + status.getIdAnswer());
			   //ps.println("Lottery Receipt kindOfReceipt : " + status.kindOfReceipt);
			   
			   ps.close();
			   ps = null;
			   fos.close();
			   fos = null;
			   inout = null;
		   } catch(Exception e) {
			   System.out.println("logLTStatus - Exception : " + e.getMessage());
		   }
	}
	
	private void printLTStatus(String filename)
	{
		if (PrinterType.isEpsonModel() && SharedPrinterFields.Lotteria.isLotteryOn())
		{
			try {
				
				beginNonFiscal();
			
				File FILE = new File(filename);
				if (!FILE.exists()) {
					System.out.println("printLTStatus - " + filename + "not found.");
					return;
				}
				FileInputStream fstream = new FileInputStream(FILE);
				DataInputStream in = new DataInputStream(fstream);
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				String line;
				while ((line = br.readLine()) != null)
				{
					printNormal(jpos.FiscalPrinterConst.FPTR_S_RECEIPT, line.length() > RTConsts.setMAXLNGHOFLENGTH() ? line.substring(0, RTConsts.setMAXLNGHOFLENGTH())+R3define.CrLf : line+R3define.CrLf);
				}	
				in.close();
				
				endNonFiscal();
			
			} catch (Exception e) {
				   System.out.println("printLTStatus - Exception : " + e.getMessage());
			}
		}
	}
	
}

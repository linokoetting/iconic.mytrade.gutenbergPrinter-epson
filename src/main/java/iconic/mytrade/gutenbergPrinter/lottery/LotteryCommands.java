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
import iconic.mytrade.gutenberg.jpos.printer.service.properties.LotteriaInstant;
import iconic.mytrade.gutenberg.jpos.printer.service.properties.PrinterType;
import iconic.mytrade.gutenberg.jpos.printer.service.properties.SRTPrinterExtension;
import iconic.mytrade.gutenberg.jpos.printer.service.utils.Sprint;
import iconic.mytrade.gutenberg.srt.RTConsts;
import iconic.mytrade.gutenbergPrinter.PrinterCommands;
import iconic.mytrade.gutenbergPrinter.SharedPrinterFields;
import jpos.FiscalPrinterConst;

public class LotteryCommands extends PrinterCommands {
	
	public void setILotteryProperties() {
		if (fiscalPrinterDriver.isfwILotteryenabled()) {
			if (LotteriaInstant.getDate() != null && LotteriaInstant.getDate().isEmpty() == false) {
				if (!GetILotteryDate().equalsIgnoreCase(LotteriaInstant.getDate())) {
					SetILotteryDate(LotteriaInstant.getDate());
					System.out.println("setILotteryProperties - "+GetILotteryDate());
				}
			}
			if 
			(LotteriaInstant.getSize() != null && LotteriaInstant.getSize().isEmpty() == false) {
				if (GetILotteryQRCodeSize() != Integer.parseInt(LotteriaInstant.getSize())) {
					SetILotteryQRCodeSize(Integer.parseInt(LotteriaInstant.getSize()));
					System.out.println("setILotteryProperties - "+GetILotteryQRCodeSize());
				}
			}
		}
	}

	public static void SendLotteryCode(String IdLotteryCode) {
		SharedPrinterFields.Lotteria.LotteryTrace("SendLotteryCode = "+IdLotteryCode+" - length = "+IdLotteryCode.length());
		
		if (!SRTPrinterExtension.isPRT())
			return;
		
		if (!SharedPrinterFields.Lotteria.checkLotteryCode(IdLotteryCode))
			return;
		
		if (PrinterType.isRCHPrintFModel() && SharedPrinterFields.Lotteria.isLotteryOn())
		{
			int enable = (SharedPrinterFields.Lotteria.checkLotteryCode(IdLotteryCode) ? 1 : 0);
			StringBuffer command = new StringBuffer(IdLotteryCode);
			SharedPrinterFields.Lotteria.LotteryTrace("SendLotteryCode - command : <"+command.toString()+">");
	      	fiscalPrinterDriver.executeRTDirectIo(3000, enable, command);
			SharedPrinterFields.Lotteria.LotteryTrace("SendLotteryCode - result : "+command.toString());
			return;
		}
		
		if (PrinterType.isEpsonModel() && SharedPrinterFields.Lotteria.isLotteryOn())
		{
			String operator = "01";
			String idcode = IdLotteryCode;
			String nu = "    ";
			
			while (idcode.length() < 16)
				idcode = idcode + " ";
			
			StringBuffer command = new StringBuffer(operator + idcode + nu);
			SharedPrinterFields.Lotteria.LotteryTrace("SendLotteryCode - command : <"+command.toString()+">");
			fiscalPrinterDriver.executeRTDirectIo(1135, 0, command);
			SharedPrinterFields.Lotteria.LotteryTrace("SendLotteryCode - result : "+command.toString());
		}
	}
	
	String GetILotteryDate()
	{
		String result = "";
		
		if (!SRTPrinterExtension.isPRT())
			return result;
		
		if (PrinterType.isEpsonModel() && SharedPrinterFields.Lotteria.isLotteryOn())
		{
			StringBuffer command = new StringBuffer("");
			SharedPrinterFields.Lotteria.LotteryTrace("GetILotteryDate - command : "+command.toString());
			fiscalPrinterDriver.executeRTDirectIo(4240, 0, command);
			SharedPrinterFields.Lotteria.LotteryTrace("GetILotteryDate - result : "+command.toString());
			result = command.toString();
		}
		
		if (PrinterType.isRCHPrintFModel() && SharedPrinterFields.Lotteria.isLotteryOn())
		{
			//result = getAllRTSettings("C495");
			String command = "C495";
			result = fiscalPrinterDriver.getRTSettings(command).substring(command.length(), command.length()+6);
		}
		
		SharedPrinterFields.Lotteria.LotteryTrace("GetILotteryDate - result : "+result);
		return result;
	}

	private void SetILotteryDate(int dd, int mm, int yy)
	{
		if (!SRTPrinterExtension.isPRT())
			return;
		
		String DD = Sprint.f("%02d", dd);
		String MM = Sprint.f("%02d", mm);
		String YY = Sprint.f("%02d", yy);
		
		if (PrinterType.isEpsonModel() && SharedPrinterFields.Lotteria.isLotteryOn())
		{
			String Spare = "000000000000";
			
			StringBuffer command = new StringBuffer(DD+MM+YY+Spare);
			SharedPrinterFields.Lotteria.LotteryTrace("SetILotteryDate - command : "+command.toString());
			fiscalPrinterDriver.executeRTDirectIo(4040, 0, command);
			SharedPrinterFields.Lotteria.LotteryTrace("SetILotteryDate - result : "+command.toString());
		}
		
		if (PrinterType.isRCHPrintFModel() && SharedPrinterFields.Lotteria.isLotteryOn())
		{
			StringBuffer key = new StringBuffer(SharedPrinterFields.KEY_SRV);
			fiscalPrinterDriver.executeRTDirectIo(0, 0, key);
	
			StringBuffer command = new StringBuffer("=C495/$"+DD+MM+YY);
			fiscalPrinterDriver.executeRTDirectIo(0, 0, command);
	
			key = new StringBuffer(SharedPrinterFields.KEY_REG);
			fiscalPrinterDriver.executeRTDirectIo(0, 0, key);
		}
		
	}

	void SetILotteryDate(String date)
	{
		if (!SRTPrinterExtension.isPRT())
			return;
		
		if (PrinterType.isEpsonModel() && SharedPrinterFields.Lotteria.isLotteryOn())
		{
			String Spare = "000000000000";
			
			StringBuffer command = new StringBuffer(date+Spare);
			SharedPrinterFields.Lotteria.LotteryTrace("SetILotteryDate - command : "+command.toString());
			fiscalPrinterDriver.executeRTDirectIo(4040, 0, command);
			SharedPrinterFields.Lotteria.LotteryTrace("SetILotteryDate - result : "+command.toString());
		}
		
		if (PrinterType.isRCHPrintFModel() && SharedPrinterFields.Lotteria.isLotteryOn())
		{
			StringBuffer key = new StringBuffer(SharedPrinterFields.KEY_SRV);
			fiscalPrinterDriver.executeRTDirectIo(0, 0, key);
	
			StringBuffer command = new StringBuffer("=C495/$"+date);
			fiscalPrinterDriver.executeRTDirectIo(0, 0, command);
	
			key = new StringBuffer(SharedPrinterFields.KEY_REG);
			fiscalPrinterDriver.executeRTDirectIo(0, 0, key);
		}
		
	}

	int GetILotteryQRCodeSize()
	{
		int result = 0;
		
		if (!SRTPrinterExtension.isPRT())
			return result;
		
		if (PrinterType.isEpsonModel() && SharedPrinterFields.Lotteria.isLotteryOn())
		{
			StringBuffer command = new StringBuffer("31");
			SharedPrinterFields.Lotteria.LotteryTrace("GetILotteryQRCodeSize - command : "+command.toString());
			fiscalPrinterDriver.executeRTDirectIo(4215, 0, command);
			SharedPrinterFields.Lotteria.LotteryTrace("GetILotteryQRCodeSize - result : "+command.toString());
			result = Integer.parseInt(command.toString().substring(2));
		}
		
		if (PrinterType.isRCHPrintFModel() && SharedPrinterFields.Lotteria.isLotteryOn())
		{
			
		}
		
		return result;
	}

	void SetILotteryQRCodeSize(int size)
	{
		if (!SRTPrinterExtension.isPRT())
			return;
		
		String QRCodeSize = Sprint.f("%03d", size);
		
		if (PrinterType.isEpsonModel() && SharedPrinterFields.Lotteria.isLotteryOn())
		{
			StringBuffer command = new StringBuffer("31"+QRCodeSize);
			SharedPrinterFields.Lotteria.LotteryTrace("SetILotteryQRCodeSize - command : "+command.toString());
			fiscalPrinterDriver.executeRTDirectIo(4015, 0, command);
			SharedPrinterFields.Lotteria.LotteryTrace("SetILotteryQRCodeSize - result : "+command.toString());
		}
		
		if (PrinterType.isRCHPrintFModel() && SharedPrinterFields.Lotteria.isLotteryOn())
		{
			
		}
	}

	private void ForcedILotteryCodesUpdate()
	{
		if (!SRTPrinterExtension.isPRT())
			return;
		
		if (PrinterType.isEpsonModel() && SharedPrinterFields.Lotteria.isLotteryOn())
		{
			
		}
		
		if (PrinterType.isRCHPrintFModel() && SharedPrinterFields.Lotteria.isLotteryOn())
		{
			StringBuffer key = new StringBuffer(SharedPrinterFields.KEY_Z);
			fiscalPrinterDriver.executeRTDirectIo(0, 0, key);
	
			StringBuffer command = new StringBuffer("=C490");
			fiscalPrinterDriver.executeRTDirectIo(0, 0, command);
	
			key = new StringBuffer(SharedPrinterFields.KEY_REG);
			fiscalPrinterDriver.executeRTDirectIo(0, 0, key);
		}
		
	}
	
}

package iconic.mytrade.gutenbergPrinter.report;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import iconic.mytrade.gutenberg.jpos.printer.service.MessageBox;
import iconic.mytrade.gutenberg.jpos.printer.service.TakeYourTime;
import iconic.mytrade.gutenberg.jpos.printer.service.properties.PrinterType;
import iconic.mytrade.gutenbergPrinter.PrinterCommands;
import iconic.mytrade.gutenbergPrinter.SharedPrinterFields;
import jpos.JposException;
import iconic.mytrade.gutenberg.jpos.printer.service.R3define;

public class Report extends PrinterCommands {
	
	public static final int PRINTALLRECEIPTBYDATE 	= 8001;
	public static final int PRINTSOMERECEIPTBYDATE	= 8002;
	public static final int DOWNLOADONFILE 		= 8004;
	public static final int PRINTFISCALMEMBYDATE	= 8005;
	public static final int PRINTTOTFISCALMEMBYDATE= 8006;
	public static final int INITJOURNAL			= 8007;
	
    public static void printReportInHouse(int reportType, String startNum, String endNum)
    {
			int ret;
			
            byte JPOS_FPTR_DI_RAWDATA = 0;
            int[] data = new int[25];
            
    		// debug( "*** Sono in PrintReport ***");
    		// debug("Tipo Report     :" + reportType);
    		// debug("Lungh. startNum :" + startNum.length());
    		// debug("StartNum        :" + startNum);
    		// debug( "Lungh. endNum   :" + endNum.length());
    		// debug( "EndNum          :" + endNum);
      
      		if (reportType == PRINTFISCALMEMBYDATE)
      			reportType = jpos.FiscalPrinterConst.FPTR_RT_DATE;
      
			switch (reportType)
			{
				case PRINTALLRECEIPTBYDATE:
					try
					{
						printAllReceiptByDate (startNum, endNum);
					}
					catch (JposException jpe)
					{
						System.out.println ( "MAPOTO-printAllReceiptByDate <"+jpe.getMessage()+">");
						MessageBox.showMessage(jpe.getMessage(), null, MessageBox.OK);
					}
				break;

				case PRINTSOMERECEIPTBYDATE:
					r3PrintSomeReceiptByDate (startNum, endNum);
				break;

				case DOWNLOADONFILE:
					try
					{
						downloadOnFile ();
					}
					catch (JposException jpe)
					{
						System.out.println ( "MAPOTO-downloadOnFile <"+jpe.getMessage()+">");
						MessageBox.showMessage(jpe.getMessage(), null, MessageBox.OK);
					}
				break;
				
				case jpos.FiscalPrinterConst.FPTR_RT_ORDINAL:
				case jpos.FiscalPrinterConst.FPTR_RT_DATE:
					if (PrinterType.isNCR2215Model())
					{
			            String givenDate = startNum.substring(6,8)+startNum.substring(2,4)+startNum.substring(0,2);
			            String endDate = endNum.substring(6,8)+endNum.substring(2,4)+endNum.substring(0,2);
		                try
		                {
	                        String strBytData = new String("\u0002"+"\u0020"+"\u0031"+"\u0043"+"\u0000"+"\u0020"+"\u001F"+givenDate+"\u001F"+endDate+"\u0003"+"\u0027"+"\u0020");
	                        int[] BCC = new int[2];
	                        BCC = dobcc(strBytData);
	                        char strCharData[] = strBytData.toCharArray();
	                        strCharData[strBytData.length()-2] = (char)BCC[0];
	                        strCharData[strBytData.length()-1] = (char)BCC[1];
	                        strBytData = new String(strCharData);
		                    fiscalPrinterDriver.directIO(JPOS_FPTR_DI_RAWDATA, data, strBytData);
		                }
		                catch ( JposException jpe )
		                {
		                    System.out.println ( "Print FM = <"+jpe.getMessage()+">" );
							MessageBox.showMessage(jpe.getMessage(), null, MessageBox.OK);
		                }
					}
					else
					{
						try
						{
							fiscalPrinterDriver.printReport (reportType, startNum, endNum);
						}
						catch (JposException jpe)
						{
							System.out.println ( "MAPOTO-fiscalPrinterDriver.printReport <"+jpe.getMessage()+">");
							MessageBox.showMessage(jpe.getMessage(), null, MessageBox.OK);
						}
					}
				break;
				
				case PRINTTOTFISCALMEMBYDATE:
					if (PrinterType.isNCR2215Model())
					{
			            String givenDate = startNum.substring(6,8)+startNum.substring(2,4)+startNum.substring(0,2);
			            String endDate = endNum.substring(6,8)+endNum.substring(2,4)+endNum.substring(0,2);
		                try
		                {
	                        String strBytData = new String("\u0002"+"\u0020"+"\u0031"+"\u0043"+"\u0000"+"\u0023"+"\u001F"+givenDate+"\u001F"+endDate+"\u0003"+"\u0027"+"\u0023");
	                        int[] BCC = new int[2];
	                        BCC = dobcc(strBytData);
	                        char strCharData[] = strBytData.toCharArray();
	                        strCharData[strBytData.length()-2] = (char)BCC[0];
	                        strCharData[strBytData.length()-1] = (char)BCC[1];
	                        strBytData = new String(strCharData);
		                	fiscalPrinterDriver.directIO(JPOS_FPTR_DI_RAWDATA, data, strBytData);
		                }
		                catch ( JposException jpe )
		                {
		                    System.out.println ( "Print FM = <"+jpe.getMessage()+">" );
							MessageBox.showMessage(jpe.getMessage(), null, MessageBox.OK);
		                }
					}
					else
					{
						try
						{
							printPeriodicTotalsReport(startNum, endNum);
						}
						catch (JposException jpe)
						{
							System.out.println ( "MAPOTO-printPeriodicTotalsReport <"+jpe.getMessage()+">");
							MessageBox.showMessage(jpe.getMessage(), null, MessageBox.OK);
						}
					}
					break;
					
				case INITJOURNAL:
					do {
						TakeYourTime.takeYourTime(200);
						ret = r3InitJournal();
					} while (ret == 111);
					break;
					
				default:
					// debug(  "Report type unknown");
				break;
			}
      return;
    }
    
	private static void r3PrintSomeReceiptByDate (String startNum, String endNum)
	{
		//startNum = "ggmmaaaaNNNN";
		//endNum   = "ggmmaaaaNNNN";

		String obj [] = new String [1];
		String givenDate = startNum.substring(0,8);
		String endDate = endNum.substring(0,8);
		int receiptStart = Integer.parseInt(startNum.substring(8,12));
		int receiptEnd = Integer.parseInt(endNum.substring(8,12));
        String range [] = new String [2];
//      int filter[] = new int [1];
        int session;
        int data[] = new int [5];
        int data1[] = new int [10];

		/* ONLY FOR DEBUG*/
		// debug( "DataInizio    : " + givenDate);
		// debug( "RicevutaInizo : " + receiptStart);
		// debug( "DataFine      : " + endDate);
		// debug( "RicevutaFine  : " + receiptEnd);
		
		try
		{
			if (PrinterType.isTHFEJModel())
			{
				obj[0] = givenDate +","+Integer.toString(receiptStart) + ","  + endDate + "," + Integer.toString(receiptEnd) +";";
				fiscalPrinterDriver.directIO( 11, null, obj);
			}
			else if (PrinterType.isTH230Model())
			{
				// la TH230 non considera receiptStart e receiptEnd
				// ma stampa gli scontrini compresi nell'intervallo orario 
				// quindi non possiamo usare questo comando
/*		        range[0] = givenDate + "0001" + Integer.toString(receiptStart);
		        range[1] = endDate + "2359" + Integer.toString(receiptEnd);
		        filter[0] = 15;
				directIO( 1103, filter, range);*/
				
                SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
                
                Calendar c1 = Calendar.getInstance();
                try {
					c1.setTime(sdf.parse(givenDate));
				} catch (ParseException e) {
					e.printStackTrace();
				}

                Calendar c2 = Calendar.getInstance();
                try {
					c2.setTime(sdf.parse(endDate));
				} catch (ParseException e) {
					e.printStackTrace();
				}
                
                do {
                	range[0] = givenDate;
                	fiscalPrinterDriver.directIO( 1106, null, range);
                    for (session=Integer.parseInt(range[0]); session<=Integer.parseInt(range[1]); session++)
                    {
            			System.out.println ( "r3PrintSomeReceiptByDate Date<"+givenDate+"> Session<"+session+">");
                    	data[0] = session;
                        data[1] = receiptStart;
                        data[2] = session;
                        data[3] = receiptEnd;
                        data[4] = 15;
                        fiscalPrinterDriver.directIO( 1105, data, obj);
                    }
                    c1.add(Calendar.DATE, 1);
                    givenDate = sdf.format(c1.getTime());
                } while (!c1.after(c2));

			}
			else if (PrinterType.isNCRFiscalModel())
			{
				// la NCR2215 non permette di stampare
				// un'intervallo di scontrini a cavallo di più date per cui stampo tutti
				// gli scontrini un giorno per volta per l'intervallo di giorni richiesto
				
                givenDate = startNum.substring(6,8)+startNum.substring(2,4)+startNum.substring(0,2);
                endDate = endNum.substring(6,8)+endNum.substring(2,4)+endNum.substring(0,2);
                String ticketStart = startNum.substring(8,12);
                String ticketEnd = endNum.substring(8,12);

                byte JPOS_FPTR_FPU_COMMAND_A = 9;
                byte EJ_PRINT_COMMAND = (byte)0x91;
                data = new int[1];
                String StrPrintEJ;
                data[0] = EJ_PRINT_COMMAND;

                SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");

                Calendar c1 = Calendar.getInstance();
                try {
                	c1.setTime(sdf.parse(givenDate));
                } catch (ParseException e) {
                	e.printStackTrace();
                }

                Calendar c2 = Calendar.getInstance();
                try {
                	c2.setTime(sdf.parse(endDate));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                do {
        			System.out.println ( "r3PrintSomeReceiptByDate Date<"+givenDate+"> ticketStart<"+ticketStart+">"+"> ticketEnd<"+ticketEnd+">");
        			if (PrinterType.isNCR2215Model())
        			{
	                    StrPrintEJ = new String("\u0004\u001F"+ticketStart+"\u001F"+ticketEnd+"\u001F"+givenDate);
	                    fiscalPrinterDriver.directIO(JPOS_FPTR_FPU_COMMAND_A, data, StrPrintEJ);
        			}
        			else if (PrinterType.isCustomModel())
        			{
                        StrPrintEJ = new String("8002"+givenDate.substring(4,6)+givenDate.substring(2,4)+givenDate.substring(0,2)+ticketStart+ticketEnd+"0");
                        data1[0]=0;
                        fiscalPrinterDriver.directIO(8002, data1, StrPrintEJ);
        			}
                    c1.add(Calendar.DATE, 1);
                    givenDate = sdf.format(c1.getTime());
                } while (!c1.after(c2));
			}
			else if (PrinterType.isEpsonModel())
			{
				// la Epson non permette di stampare
				// un'intervallo di scontrini a cavallo di più date per cui stampo tutti
				// gli scontrini un giorno per volta per l'intervallo di giorni richiesto

				givenDate = startNum.substring(0,2)+startNum.substring(2,4)+startNum.substring(6,8);
                endDate = endNum.substring(0,2)+endNum.substring(2,4)+endNum.substring(6,8);
                String ticketStart = startNum.substring(8,12);
                String ticketEnd = endNum.substring(8,12);

                SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");

                Calendar c1 = Calendar.getInstance();
                try {
                	c1.setTime(sdf.parse(givenDate));
                } catch (ParseException e) {
                	e.printStackTrace();
                }

                Calendar c2 = Calendar.getInstance();
                try {
                	c2.setTime(sdf.parse(endDate));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                do {
        			System.out.println ( "r3PrintSomeReceiptByDate Date<"+givenDate+"> ticketStart<"+ticketStart+">"+"> ticketEnd<"+ticketEnd+">");
        			PrinterCommands cmd = new PrinterCommands();
        			cmd.executeDirectIo(3098, "01"+givenDate+ticketStart+ticketEnd);
                    c1.add(Calendar.DATE, 1);
                    givenDate = sdf.format(c1.getTime());
                } while (!c1.after(c2));
			}
			else if (PrinterType.isRCHPrintFModel())
			{
				// la RCH non permette di stampare
				// un'intervallo di scontrini a cavallo di più date per cui stampo tutti
				// gli scontrini un giorno per volta per l'intervallo di giorni richiesto

				givenDate = startNum.substring(0,2)+startNum.substring(2,4)+startNum.substring(6,8);
                endDate = endNum.substring(0,2)+endNum.substring(2,4)+endNum.substring(6,8);
                String ticketStart = startNum.substring(8,12);
                String ticketEnd = endNum.substring(8,12);

                SimpleDateFormat sdf = new SimpleDateFormat("ddMMyy");

                Calendar c1 = Calendar.getInstance();
                try {
                	c1.setTime(sdf.parse(givenDate));
                } catch (ParseException e) {
                	e.printStackTrace();
                }

                Calendar c2 = Calendar.getInstance();
                try {
                	c2.setTime(sdf.parse(endDate));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                do {
        			System.out.println ( "r3PrintSomeReceiptByDate Date<"+givenDate+"> ticketStart<"+ticketStart+">"+"> ticketEnd<"+ticketEnd+">");
                    int cmdInt = 0;
                    int[] mydata = {0};
                    String cmd = SharedPrinterFields.KEY_Z;
                    fiscalPrinterDriver.directIO(cmdInt, mydata, cmd);
                    cmd = "=C452/$1/&"+givenDate+"/["+ticketStart+"/]"+ticketEnd;
                    fiscalPrinterDriver.directIO(cmdInt, mydata, cmd);
                    cmd = SharedPrinterFields.KEY_REG;
                    fiscalPrinterDriver.directIO(cmdInt, mydata, cmd);
                    c1.add(Calendar.DATE, 1);
                    givenDate = sdf.format(c1.getTime());
                } while (!c1.after(c2));
			}
		}
		
		catch (JposException jpe)
		{
			System.out.println ( "MAPOTO-r3PrintSomeReceiptByDate <"+jpe.getMessage()+">");
			if (jpe.getErrorCode() == 111)
				MessageBox.showMessage("HardwareFailure EJEmptyDate", null, MessageBox.OK);
			else
				MessageBox.showMessage(jpe.getMessage(), null, MessageBox.OK);
		}
	}

	private static void downloadOnFile () throws JposException
	{
		int idx;
		int index;
		int data 			[] 	= new int [1];
		String object [] 	= new String [1];

		// Output stream declaration
		FileOutputStream fout = null;
		ObjectOutputStream out = null;
		
		/***********************************************************/
		
		// get the number of sessions
		data[0] = 8;
		fiscalPrinterDriver.directIO(7, data, object);
		int totalNumberOfSessions = Integer.parseInt(object[0]);
		// debug( "Nr. totale sessioni : " + totalNumberOfSessions);
		if (totalNumberOfSessions < 1) return;
		
		// get the number of first session, to have a session for querying
		data[0] = 9;
		fiscalPrinterDriver.directIO(7, data, object);
		int sessionNumber = Integer.parseInt(object[0]);
		// debug( "Nr. prima sessione : " + sessionNumber);
//		String sessionNumberStr = object[0] + ";"; 

		try
		{
			fout 	= new FileOutputStream ("Pippero.dat", true);
			out		= new ObjectOutputStream (fout);
			out.writeObject( "Nr. totale sessioni : " + totalNumberOfSessions);
			
			for (idx = sessionNumber; idx<= totalNumberOfSessions; idx++)
			{
				// get the number of documents in a session
				data[0] = 1;
				object[0] = Integer.toString(idx)+ ";";
				fiscalPrinterDriver.directIO(8, data, object);
				int numberOfDocuments = Integer.parseInt(object[0]);
				if (numberOfDocuments < 1)
				{
				    out.writeObject("La sessione " + idx + " non contiene documenti ");
				    // debug( "ERROR: first session does not contain a document");
						continue;
				}
		
				// debug( "Nr. documenti nella sessione nr. " + idx + " : " + numberOfDocuments);
				out.writeObject("\nNr. documenti nella sessione nr. " + idx + " : " + numberOfDocuments + "\n");
				for (index = 1; index <= numberOfDocuments; index++)
				{
					out.writeObject("\nSessione Nr. " + idx + "   Documento Nr. " + index + R3define.Lf);
					// get document content
					//String documentSession = Integer.toString(sessionNumber) + ",1,";
					String documentSession = Integer.toString(idx) + "," + 
																	 Integer.toString(index) + ",";
					//IDebug.debug.debug(DEBUG_VERBOSE, CLASSNAME, "DOCUMENT SESSION :" + documentSession + "\n");
					int offset = 0;
					StringBuffer document = new StringBuffer();
					
					do
					{
				    object[0] = documentSession + Integer.toString(offset) + ";";
				    fiscalPrinterDriver.directIO(9, null, object);
				    document.append(object[0]);
				    offset += 512;
				    out.writeObject(object[0]);
					} while (object[0].length() == 512);
					// debug(document.toString());
				}
			}
			out.close();
			// debug("document content of the first document of the first session:");

		}catch (IOException err){ err.printStackTrace(); }
	}
	
	private static void printAllReceiptByDate (String startNum, String endNum) throws JposException
	{
		int data 	 [] 	= new int 		[1];
		String obj [] 	= new String 	[1];
		int sessionNumber;
		
		// get the number of sessions
		data[0] = 8;
		fiscalPrinterDriver.directIO(7, data, obj);
		int totalNumberOfSessions = Integer.parseInt(obj[0]);
		// debug("Nr. sessioni : " + totalNumberOfSessions);
		if (totalNumberOfSessions < 1) return;

		String givenDate = startNum.substring(8,10) + startNum.substring(5,7) + startNum.substring(0,4);
		GregorianCalendar date = new GregorianCalendar();
		int year 	= date.get(Calendar.YEAR);
		int month = date.get(Calendar.MONTH);
		int day 	= date.get(Calendar.DAY_OF_MONTH);
		String actualDate = Integer.toString(day) + Integer.toString(month) + Integer.toString(year);

		if(givenDate.equals(actualDate))
		{
			//the given date is today -> check in the actual session
			if (isActualSessionOpen (totalNumberOfSessions, actualDate)) return;
		}

		sessionNumber = findSession(givenDate, totalNumberOfSessions-1);
		if (sessionNumber > 0)
		{
			obj[0] = Integer.toString(sessionNumber) + ";";
			fiscalPrinterDriver.directIO( 10, null, obj);
		}
		// else // debug( "No session found for given date");
	}
	
	private static boolean isActualSessionOpen (int actualSession, String actualDate) throws JposException
	{
		boolean print = false;
		int data 			[] 	= new int [1];
		String object [] 	= new String [1];

		data[0] = 3;
		object[0] = actualSession + ";";
		try
		{
			fiscalPrinterDriver.directIO( 8, data, object);
		}
		catch (JposException jpe)
		{
	    if (jpe.getErrorCode() != jpos.JposConst.JPOS_E_NOEXIST) 
				throw(jpe);
			else print = true;
		}

		if((print)||((object[0].substring(0,8)).equals(actualDate)))
		{
			fiscalPrinterDriver.directIO( 10, null, object);
			return true;
		}
		else
		{
			return false;
		}
	}
	
	private static int findSession(String date, int totalNumberOfSessions) throws JposException
	{
		int data 			[] 	= new int [1];
		String object [] 	= new String [1];

		//find the given date through all sessions (end date of session = given date)
		data[0] = 3;
		for (int idx = totalNumberOfSessions; idx > 0; idx--)
		{
			object [0] = Integer.toString(idx) + ";";
			try
			{
				fiscalPrinterDriver.directIO( 8, data, object);
				if((object[0].substring(0,8)).equals(date))
				{
					return idx;
				}
			}
				catch (JposException jpe)
			{
		    if (jpe.getErrorCode() != jpos.JposConst.JPOS_E_NOEXIST) 
					throw(jpe);
			}
		}
		return -1;
	}
	
    private static int r3InitJournal ()
    {
    	String obj[] = new String[1];
    	int data[] = new int[1];
    	int ret = 0;
    	
		if (MessageBox.showMessage("INIZIALIZZ. GIORNALE?", MessageBox.YESNO) == MessageBox.NO)
			return(-1);

		try
		{
			if (PrinterType.isTHFEJModel())
				fiscalPrinterDriver.directIO(6, data, obj);
			else if (PrinterType.isTH230Model())
				fiscalPrinterDriver.directIO(1104, data, obj);
			else if (PrinterType.isNCR2215Model())
			{
				MessageBox.showMessage("Funzione non valida");
				return(-1);
			}
			else if (PrinterType.isEpsonModel())
			{
				data[0]=3097;
				obj[0]="01";
				fiscalPrinterDriver.directIO(0, data, obj);
			}
			else if (PrinterType.isCustomModel())
			{
		    	int data1[] = new int[25];
		    	fiscalPrinterDriver.directIO(8007, data1, "8007");
			}
		}
		catch (JposException jpe)
		{
			System.out.println ( "MAPOTO-r3InitJournal <"+jpe.getErrorCode()+"><"+jpe.getMessage()+">");
			ret = jpe.getErrorCode();
			if (ret == 111)
			{
				//BeepAlert();
				ret = MessageBox.showMessage("ChangeEJ", MessageBox.YESNO);
				if (ret == MessageBox.YES)
					ret = 111;
			}
			else if (ret == 114)
				MessageBox.showMessage("HardwareFailure EJFiscalDayOpen");
			else
				MessageBox.showMessage(jpe.getMessage());
			 return ret;
		}
		
		MessageBox.showMessage("NotifyInitJournal", null, MessageBox.OK);
		return ret;
    }
    
	public static void printPeriodicTotalsReport(String ini,String end ) throws JposException
	{
		fiscalPrinterDriver.printPeriodicTotalsReport(ini,end);
	}
	
}

package iconic.mytrade.gutenbergPrinter.report;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import iconic.mytrade.gutenberg.jpos.printer.service.OperatorDisplay.MessageBox;
import iconic.mytrade.gutenberg.jpos.printer.service.R3define;
import iconic.mytrade.gutenberg.jpos.printer.service.TakeYourTime;
import iconic.mytrade.gutenbergPrinter.PrinterCommands;
import iconic.mytrade.gutenbergPrinter.SharedPrinterFields;
import jpos.JposException;

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
					try
					{
						fiscalPrinterDriver.printReport (reportType, startNum, endNum);
					}
					catch (JposException jpe)
					{
						System.out.println ( "MAPOTO-fiscalPrinterDriver.printReport <"+jpe.getMessage()+">");
						MessageBox.showMessage(jpe.getMessage(), null, MessageBox.OK);
					}
				break;
				
				case PRINTTOTFISCALMEMBYDATE:
					try
					{
						printPeriodicTotalsReport(startNum, endNum);
					}
					catch (JposException jpe)
					{
						System.out.println ( "MAPOTO-printPeriodicTotalsReport <"+jpe.getMessage()+">");
						MessageBox.showMessage(jpe.getMessage(), null, MessageBox.OK);
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

		String givenDate = startNum.substring(0,8);
		String endDate = endNum.substring(0,8);

		/* ONLY FOR DEBUG*/
		// debug( "DataInizio    : " + givenDate);
		// debug( "RicevutaInizo : " + receiptStart);
		// debug( "DataFine      : " + endDate);
		// debug( "RicevutaFine  : " + receiptEnd);
		
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
			PrinterCommands cmd = SharedPrinterFields.getPrinterCommands();
			cmd.executeDirectIo(3098, "01"+givenDate+ticketStart+ticketEnd);
            c1.add(Calendar.DATE, 1);
            givenDate = sdf.format(c1.getTime());
        } while (!c1.after(c2));
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
			data[0]=3097;
			obj[0]="01";
			fiscalPrinterDriver.directIO(0, data, obj);
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
    
	private static void printPeriodicTotalsReport(String ini,String end ) throws JposException
	{
		fiscalPrinterDriver.printPeriodicTotalsReport(ini,end);
	}
	
}

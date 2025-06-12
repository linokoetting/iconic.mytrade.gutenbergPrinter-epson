package iconic.mytrade.gutenbergPrinter.ej;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForFiscalEJFile {
	
	private static String dir = "";
	private static final String PINstr = "PIN:";
	
	public static String getEJFileName() {
		String txt = ".txt";
		String fn = dir+"EJfile";
		int i = fn.indexOf(txt);
		if (i > 0) { //se i == 0 il nome del file rimmar� ".txt" 
			fn = fn.substring(0, i);
		}
		GregorianCalendar greg = new GregorianCalendar();
		int estensione = greg.get(Calendar.DAY_OF_MONTH);
		if (estensione<10)
			fn += ".0"+estensione;
		else
			fn += "."+estensione;

		return fn;
	}

	/**
	 * Esegue una serie di controlli preventivi sul file ejfile.txt
	 * se solo uno diquesti fallisce non sar� possibile operare sul file
	 * @return true se il file esiste, se � un regular file (ossia non si tratta
	 * di una directory) e se esiste permesso in scrittura.
	 */
	public static File workWithFile() {
		File ejFile = new File(getEJFileName());
		long ultimaModifica = ejFile.lastModified();
		GregorianCalendar greg = new GregorianCalendar();
		int meseCorrente = greg.get(Calendar.MONTH);
		greg.setTime(new Date(ultimaModifica));
		int meseDelFile = greg.get(Calendar.MONTH);
		if (ejFile.exists() && (meseCorrente != meseDelFile)){
			ejFile.delete();
			ejFile = new File(getEJFileName());
		}
		return ejFile;
		
//		if (ejFile.exists() && ejFile.isFile() && ejFile.canWrite()) {
//			return ejFile;
//		} else {
//			return null;
//		}
	}

	/**
	 * Scrive su file le informazioni che arrivano alla stampante fiscale.
	 * Si preoccupa di verificare l'effettiva utilizzabilt� del file (controllo
	 * di esistenza e della presenza dei necessari permessi).
	 * @param str la stringa da scrivere su file.
	 */
	public static void writeToFile(String str) {
		// controllo che il file esista, sia un regular file (ossia non una dir)
		// verifico di avere permesso in scrittura
		File workingFile = workWithFile();

		PrintStream printStream = fileOpenWrite(workingFile, "a");
		str = stripUnwantedStr(str);

		str = maskPin(str);
		printStream.println(str);
/*		if(CompanyData.getString("PATO.isPATO").equalsIgnoreCase("true")){
			if ( CompanyData.getRemoteWatcher())
			{
				SendCommand.spedisciMessaggio(TabellaComandi.PRINTEJ+"|"+str);
			}
		}
		try{
			if(uk.co.datafit.wincor.gdONE.GdONEData.isGdONEON()){
				uk.co.datafit.wincor.gdONE.AscoltogdONE.spedisciMessaggio(uk.co.datafit.wincor.gdONE.TabellaComandigdONE.PRINTEJ+ uk.co.datafit.wincor.gdONE.TabellaComandigdONE.SEPARATORE +str);
			}
		}catch (NoClassDefFoundError e) {	
		}
		catch (Exception e) {
			System.out.println("ForFiscalEJFile - writeToFile - errore gdONE:"+e);
		}*/
		printStream.close();
	}

    /**
     * If s has the pin, digits are hidden with <code>*</code>
     * @author: c.achilli.ext
     * @date: Jul 5, 2007
     * @param s
     * @return
     */
    private static String maskPin(String s) 
    {
        Pattern p = Pattern.compile(PINstr);
        Matcher m = p.matcher(s);
        if(m.find()) 
        {
        	try{
            	String newS = "";
            	int posPIN = s.indexOf(PINstr);
            	newS = newS + s.substring(0,posPIN);
            	newS = newS + PINstr+" *****";
            	while( posPIN<s.length() )
            	{
            		if(s.charAt(posPIN)=='\n')
            			break;
            		posPIN++;
            	}
            	s = newS + s.substring(posPIN,s.length());
        	}catch(Exception e)	{
        		System.out.println("ForFiscalEJFile-maskPin: Exception \""+e.getMessage()+"\"");
        		e.printStackTrace();
        		s = m.group()+" *****";
        	}
        }
        return s;
    }
    
	private static String stripUnwantedStr(String aStr) {
		if (aStr == null) return "";
		String oldStr = aStr;
		String newStr = null;
		boolean stripped = false;
		for (int i = 0; i < FiscalEJFile.strippedStrs.length; i++) {
			int index = aStr.indexOf(FiscalEJFile.strippedStrs[i]);
			if (index != -1) {
				stripped = true;
				//System.out.println("** searching substring: " + strippedStrs[i]);
				//System.out.println("** in string: " + aStr);
				newStr = aStr.substring(0, index);
				//System.out.println("** before substring: " + newStr);
				newStr += aStr.substring(index
						+ FiscalEJFile.strippedStrs[i].length());
				//System.out.println("** without substring: " + newStr);
				aStr = newStr;
			}
		}
		if (stripped)
			return newStr;
		else
			return oldStr;
	}

	/**
	 * Restituisce un PrintStream in append o in semplice scrittura su file.
	 * @param finp il file sul quale viene restituito il printStream.
	 * @param param definisce se il PrintStream viene restituito o meno in append.
	 * Accetta due valori. <code>a</code>
	 * @return il PrintStream sul file passato come parametro.
	 */
	public static PrintStream fileOpenWrite(File finp, String param) {
		boolean append = true;
		// Apertura file in scrittura

		// Verifica il parametro Passato
		if (param.startsWith("a") || param.startsWith("w")) {
			if (param.startsWith("w"))
				append = false;
			try {
				return new PrintStream(new FileOutputStream(finp.toString(), append));
			} catch (FileNotFoundException e) {
				System.out.println("FileName " + finp);
				e.printStackTrace(System.out);
			}
		}
		return null;
	}
	
}

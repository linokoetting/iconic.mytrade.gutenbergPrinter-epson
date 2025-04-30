package iconic.mytrade.gutenbergPrinter.mop;

import java.util.ArrayList;

import iconic.mytrade.gutenberg.jpos.printer.service.mop.MediaInfo;
import iconic.mytrade.gutenberg.jpos.printer.service.properties.SRTPrinterExtension;
import iconic.mytrade.gutenbergPrinter.SharedPrinterFields;

public class LoadMops {
	
    private static int    CASH_TYPE		= -1;
    private static int    NOPAY1_TYPE	= -1;
    private static int    EFT_TYPE		= -1;
    private static int    NOPAY2_TYPE	= -1;
    public static int    TICKETWN_TYPE	= -1;
    private static int    NOPAID_TYPE	= -1;
    private static int    DSCONPAY_TYPE = -1;
    
	private LoadMops()
	{
		
	}
	
	private static void init() {
	}
	
    public static ArrayList<Mop> Mops = null;		// method of payment
    private static ArrayList Vops = null;		// voucher of payment
    
	public static void loadMops(){
		
		if (Mops == null){
			Mops = getMops();
		}
	}
	
	private static void loadVops(){
		
		if (Vops == null){
			Vops = getVops();
		}
	}
	    
	private static ArrayList getMops()
	{
		init ();	 
		CASH_TYPE = SRTPrinterExtension.getCashType();
		EFT_TYPE = SRTPrinterExtension.getEftType();
		NOPAY1_TYPE = SRTPrinterExtension.getNoPay1Type();
		NOPAY2_TYPE = SRTPrinterExtension.getNoPay2Type();
		TICKETWN_TYPE = SRTPrinterExtension.getTicketWithNumber();
		NOPAID_TYPE = SRTPrinterExtension.getNoPaid();
		DSCONPAY_TYPE = SRTPrinterExtension.getDiscountOnPayment();
		ArrayList Mops = null;
		try {			  		
			Mops = new ArrayList();
			MediaInfo[] mediainfos = MediaInfoLookup(SharedPrinterFields.mediaInfoMap);				
			if (mediainfos != null){
				for (int i = 0; i < mediainfos.length; i++)
				{
					//System.out.println("LoadMops - getMops - mediainfo = "+mediainfos[i].getMediaType().getMediaType()+" "+mediainfos[i].getMediaNumber()+" "+mediainfos[i].getDescription()+" "+mediainfos[i].getRtMediaCode()+" "+mediainfos[i].getRtPosDesc());
					if (toIgnore(mediainfos[i].getMediaType(), mediainfos[i].getMediaNumber(), mediainfos[i].getRt_MediaCode()))
						continue;
					
                    int rtMediaType = ((mediainfos[i].getRt_MediaCode() >= 100) ? Integer.parseInt((""+mediainfos[i].getRt_MediaCode()).substring(0, 1)) : Integer.parseInt((""+mediainfos[i].getRt_MediaCode())));
                    int rtMediaIndex = ((mediainfos[i].getRt_MediaCode() >= 100) ? Integer.parseInt((""+mediainfos[i].getRt_MediaCode()).substring(1, 3)) : 0);
                    String rtDesc = mediainfos[i].getDescription();
                    
                    if ((mediainfos[i].getMediaType() == 1) && (mediainfos[i].getMediaNumber() == 1))
                    	SharedPrinterFields.DESCRIZIONE_CONTANTI = rtDesc;
                    if ((mediainfos[i].getMediaType() == 79) && (mediainfos[i].getMediaNumber() == 79))
                    	SharedPrinterFields.DESCRIZIONE_SCONTOAPAGARE = rtDesc;

					Mop mop = new Mop(rtDesc,
									  rtMediaType,
									  mediainfos[i].getRt_PosDesc(),
									  rtMediaIndex);
					Mops.add(mop);
					System.out.println("LoadMops - getMops - mop = "+((Mop)mop).getType()+" "+((Mop)mop).getInd()+" "+((Mop)mop).getDescription()+" - "+((Mop)mop).getSrtdescription());
				}
			}
		}catch (Exception e) {
			System.out.println("LoadMops - getMops - errore:"+e);
		}
		return Mops;
	}
	
	private static MediaInfo[] MediaInfoLookup(ArrayList<MediaInfo> mediaInfoMap) {
	    MediaInfo[] ret = new MediaInfo[mediaInfoMap.size()];
		
	    int i = 0;
		for (MediaInfo vdata : mediaInfoMap) {
	    	ret[i] = mediaInfoMap.get(i);
	    	i++;
		}
	    return ret;
	}

	private static ArrayList<String> getVops()
	{
	    ArrayList<String> Vops = new ArrayList();
	    
	    if (SharedPrinterFields.gvTypesMap != null) {
		    for (String vdata : SharedPrinterFields.gvTypesMap) {
		    	Vops.add(vdata);
		    }
	    }
	    
	    return Vops;
	}
	
	private static boolean toIgnore(short mt, short mn, int mc) {
		return false;
	}

	private static String getSrtDescription(String description, ArrayList Mops)
	{
		String srtdescription = "Non Riscosso";
		
		for (int i = 0; i < Mops.size(); i++){
			Mop mop = (Mop) Mops.get(i);
			if (mop.getDescription().equalsIgnoreCase(description)){
				srtdescription = mop.getSrtdescription();
				System.out.println("RT2 - LoadMops - getSrtDescription = "+srtdescription);
				break;
			}
		}
		
		return srtdescription;
	}
	
	public static String getChangeDescription(ArrayList Mops)
	{
		for (int i = 0; i < Mops.size(); i++){
			Mop mop = (Mop) Mops.get(i);
			if ((mop.getType() == CASH_TYPE) && (mop.getDescription().equalsIgnoreCase(SharedPrinterFields.DESCRIZIONE_CONTANTI))){
				System.out.println("RT2 - LoadMops - getChangeDescription = "+mop.getDescription());
				return mop.getDescription();
			}
		}
		return "";
	}
	
	private static boolean isNonRiscosso(ArrayList Mops, String srtdescription, boolean isRT2On)
	{
		for (int i = 0; i < Mops.size(); i++){
			Mop mop = (Mop) Mops.get(i);
			if (mop.getSrtdescription() != null && mop.getSrtdescription().equalsIgnoreCase(srtdescription)){
				if ((SRTPrinterExtension.isSRT() || SRTPrinterExtension.isPRT()) && isRT2On) {
					return ((mop.getType() == NOPAY1_TYPE) || 
							(mop.getType() == NOPAY2_TYPE) || 
							(mop.getType() == TICKETWN_TYPE) || 
							(mop.getType() == NOPAID_TYPE));
				}
				else {
	                return ((mop.getType() == NOPAY1_TYPE) || 
	                        (mop.getType() == NOPAY2_TYPE));
				}
			}
		}
		return false;
	}
	
	private static boolean isPagElettronico(ArrayList Mops, String srtdescription)
	{
		for (int i = 0; i < Mops.size(); i++){
			Mop mop = (Mop) Mops.get(i);
			if (mop.getSrtdescription() != null && mop.getSrtdescription().equalsIgnoreCase(srtdescription)) {
				return (mop.getType() == EFT_TYPE);
			}
		}
		return false;
	}
	
	private static String getPrefixPayment(String description, ArrayList Mops, boolean oldfw)
	{
		System.out.println("RT2 - LoadMops - getPrefixPayment - description = "+description+" - oldfw = "+oldfw);
		
		String prefixpayment = description.substring(0,3);		// ora dovrebbe arrivare sempre tipo 001Contanti
		int type = Integer.parseInt(description.substring(0,1));
		String index = description.substring(1,3);
		
		System.out.println("RT2 - LoadMops - getPrefixPayment - type = "+type+" - index = "+index);
		
		if (!oldfw) {
			// fw nuovo
			
			System.out.println("RT2 - LoadMops - getPrefixPayment - prefixpayment = "+prefixpayment);
			return prefixpayment; 
		}
		else {
			// fw vecchio
			
/*			if ((type == TICKETWN_TYPE) || (type == NOPAID_TYPE) || (type == DSCONPAY_TYPE)) {
				type = NOPAY2_TYPE;
		    	index = "01";
			    prefixpayment = "" + type + index;
			}*/
		}
		
		System.out.println("RT2 - LoadMops - getPrefixPayment - prefixpayment = "+prefixpayment);
		return prefixpayment;
	}
	
	public static String getSrtDescription(String description){
		loadMops();
		
		return(getSrtDescription(description, Mops));
	}

	public static boolean isNonRiscosso(String srtdescr, boolean isRT2On){
		loadMops();
		
		return isNonRiscosso(Mops, srtdescr, isRT2On);
	}

	public static boolean isPagElettronico(String srtdescr){
		loadMops();
		
		return isPagElettronico(Mops, srtdescr);
	}

	public static String getPrefixPayment(String description, boolean fwRT2disabled){
		loadMops();
		
		return(getPrefixPayment(description, Mops, fwRT2disabled));
	}
	
	private static boolean isNumeric (String s)
	{
		try 
		{
			Integer.parseInt(s);
			return ( true );
		}
		catch ( Exception e )
		{
			return ( false );
		}
	}
}

package iconic.mytrade.gutenbergPrinter.tax;

public class DicoTaxObject 
{
	private int		taxnumber;
	private String	description;
	private double	taxrate;
	private double	lordo;
	private double	netto;
	private String	shortdescription;
	private String	printerTaxnumber;
	private boolean sold;
	private int		type;
	private int		atecoId;
	private int		printerDeptnumber;
	
	private static int		BASE_SERVICES_DEPT = 21;
	
	public int getTaxnumber() {
		return taxnumber;
	}
	public void setTaxnumber(int taxnumber) {
		this.taxnumber = taxnumber;
	}
	public double getTaxrate() {
		return taxrate;
	}
	public void setTaxrate(double taxrate) {
		this.taxrate = taxrate;
	}
	public double getLordo() {
		return lordo;
	}
	public void setLordo(double amount) {
		this.lordo = amount;
	}
	public double getNetto() {
		return netto;
	}
	public void setNetto(double tax) {
		this.netto = tax;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	//
	// public non getter non setter
	//
	public void clean()
	{
		setLordo ( (double)0.0 );
		setNetto ( ( double )0.0 );
		setSold  ( false );
	}
	public String getShortdescription() {
		return shortdescription;
	}
	public void setShortdescription(String shortdescription) {
		this.shortdescription = shortdescription;
	}
	public String getPrinterTaxnumber() {
		return printerTaxnumber;
	}
	public void setPrinterTaxnumber(String printerTaxnumber) {
		this.printerTaxnumber = printerTaxnumber;
	}
/*	public void setPrinterTaxnumber(String printerTaxnumber, double rate, int atecoid) {
		this.printerTaxnumber = printerTaxnumber;
		if ((R3printers.getAtecoVI(atecoid) == 0) && (Integer.parseInt(printerTaxnumber) == 15) && (TaxData.isBeni(this.getTaxnumber()))) {
			if (rate == 22)
				printerTaxnumber = "15";
			else if (rate == 10)
				printerTaxnumber = "16";
			else if (rate == 4)
				printerTaxnumber = "17";
			else if (rate == 5)
				printerTaxnumber = "18";
			this.printerTaxnumber = printerTaxnumber;
			setPrinterDeptnumber(Integer.parseInt(printerTaxnumber));
		}
	}*/
	public boolean isSold() {
		return sold;
	}
	public void setSold(boolean sold) {
		this.sold = sold;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getAtecoId() {
		return atecoId;
	}
	public void setAtecoId(int atecoId) {
		this.atecoId = atecoId;
	}
	public int getPrinterDeptnumber() {
		return printerDeptnumber;
	}
	public void setPrinterDeptnumber(int printerDeptnumber) {
		if (TaxData.isServizi(this.getTaxnumber()))
			printerDeptnumber+=getBASE_SERVICES_DEPT();
		this.printerDeptnumber = printerDeptnumber;
	}
	private int getBASE_SERVICES_DEPT() {
		return BASE_SERVICES_DEPT;
	}
	public static void setBASE_SERVICES_DEPT(int bASE_SERVICES_DEPT) {
		BASE_SERVICES_DEPT = bASE_SERVICES_DEPT;
	}
}

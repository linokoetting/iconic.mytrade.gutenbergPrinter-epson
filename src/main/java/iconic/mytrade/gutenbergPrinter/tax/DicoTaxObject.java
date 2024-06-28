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
	
	int getTaxnumber() {
		return taxnumber;
	}
	void setTaxnumber(int taxnumber) {
		this.taxnumber = taxnumber;
	}
	public double getTaxrate() {
		return taxrate;
	}
	void setTaxrate(double taxrate) {
		this.taxrate = taxrate;
	}
	private double getLordo() {
		return lordo;
	}
	private void setLordo(double amount) {
		this.lordo = amount;
	}
	private double getNetto() {
		return netto;
	}
	private void setNetto(double tax) {
		this.netto = tax;
	}
	String getDescription() {
		return description;
	}
	void setDescription(String description) {
		this.description = description;
	}
	//
	// private non getter non setter
	//
	void clean()
	{
		setLordo ( (double)0.0 );
		setNetto ( ( double )0.0 );
		setSold  ( false );
	}
	public String getShortdescription() {
		return shortdescription;
	}
	void setShortdescription(String shortdescription) {
		this.shortdescription = shortdescription;
	}
	String getPrinterTaxnumber() {
		return printerTaxnumber;
	}
	void setPrinterTaxnumber(String printerTaxnumber) {
		this.printerTaxnumber = printerTaxnumber;
	}
/*	private void setPrinterTaxnumber(String printerTaxnumber, double rate, int atecoid) {
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
	private boolean isSold() {
		return sold;
	}
	private void setSold(boolean sold) {
		this.sold = sold;
	}
	int getType() {
		return type;
	}
	void setType(int type) {
		this.type = type;
	}
	int getAtecoId() {
		return atecoId;
	}
	void setAtecoId(int atecoId) {
		this.atecoId = atecoId;
	}
	int getPrinterDeptnumber() {
		return printerDeptnumber;
	}
	void setPrinterDeptnumber(int printerDeptnumber) {
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

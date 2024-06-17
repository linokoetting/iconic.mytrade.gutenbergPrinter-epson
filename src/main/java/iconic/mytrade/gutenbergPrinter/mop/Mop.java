package iconic.mytrade.gutenbergPrinter.mop;

public class Mop {
	private String description = "";
	private int type = 0;
	private String srtdescription = "";
	private int ind = 0;
	
	public Mop (String description, int type, String srtdescription, int ind)
	{
		this.setDescription(description);
		this.setType(type);
		this.setSrtdescription(srtdescription);
		this.setInd(ind);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getSrtdescription() {
		return srtdescription;
	}

	public void setSrtdescription(String srtdescription) {
		this.srtdescription = srtdescription;
	}

	public int getInd() {
		return ind;
	}

	public void setInd(int ind) {
		this.ind = ind;
	}
}

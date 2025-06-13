package iconic.mytrade.gutenbergPrinter.mop;

public class Mop {
	private String description = "";
	private int type = 0;
	private String srtdescription = "";
	private int ind = 0;
	
	Mop (String description, int type, String srtdescription, int ind)
	{
		this.setDescription(description);
		this.setType(type);
		this.setSrtdescription(srtdescription);
		this.setInd(ind);
	}

	public String getDescription() {
		return description;
	}

	private void setDescription(String description) {
		this.description = description;
	}

	public int getType() {
		return type;
	}

	private void setType(int type) {
		this.type = type;
	}

	String getSrtdescription() {
		return srtdescription;
	}

	private void setSrtdescription(String srtdescription) {
		this.srtdescription = srtdescription;
	}

	public int getInd() {
		return ind;
	}

	private void setInd(int ind) {
		this.ind = ind;
	}
}

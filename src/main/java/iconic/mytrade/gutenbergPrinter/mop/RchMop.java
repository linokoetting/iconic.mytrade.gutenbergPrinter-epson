package iconic.mytrade.gutenbergPrinter.mop;

public class RchMop {
	private String description = "";
	private int code = 0;
	
	public RchMop (String description, int code)
	{
		this.setDescription(description);
		this.setCode(code);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
}

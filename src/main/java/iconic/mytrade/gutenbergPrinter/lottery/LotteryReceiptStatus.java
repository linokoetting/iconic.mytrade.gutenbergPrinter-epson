package iconic.mytrade.gutenbergPrinter.lottery;

public class LotteryReceiptStatus {
	private String tillId;
	private int zRepNum;
	private int recNum;
	private String recDate;
	private int result;
	private String errCode;
	private String idAnswer;
	private int kindOfReceipt;
		
	private LotteryReceiptStatus(String tillId, int zRepNum, int recNum, String recDate,
								 int result, String errCode, String idAnswer, int kindOfReceipt) {
			this.tillId = tillId;
			this.zRepNum = zRepNum;
			this.recNum = recNum;
			this.recDate = recDate;
			this.result = result;
			this.errCode = errCode;
			this.idAnswer = idAnswer;
			this.kindOfReceipt = kindOfReceipt;
	}
	
	public LotteryReceiptStatus(String result)
	{
		//System.out.println("LotteryReceiptStatus - result : "+result);
		if((result.length()) >= 24)
		{
			this.tillId = result.substring(0, 8);
			this.zRepNum = Integer.parseInt(result.substring(8, 12));
			this.recNum = Integer.parseInt(result.substring(12, 16));
			this.recDate = result.substring(16, 22);
			this.result = Integer.parseInt(result.substring(22, 24));
			this.errCode = result.substring(24, 29);
			this.idAnswer = result.substring(29, 79);
			this.kindOfReceipt = Integer.parseInt(result.substring(85, 87));
		}
	}

	public String getTillId() {
		return tillId;
	}

	public void setTillId(String tillId) {
		this.tillId = tillId;
	}

	public int getzRepNum() {
		return zRepNum;
	}

	public void setzRepNum(int zRepNum) {
		this.zRepNum = zRepNum;
	}

	public int getRecNum() {
		return recNum;
	}

	public void setRecNum(int recNum) {
		this.recNum = recNum;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public String getErrCode() {
		return errCode;
	}

	public void setErrCode(String errCode) {
		this.errCode = errCode;
	}

	public String getRecDate() {
		return recDate;
	}

	public void setRecDate(String recDate) {
		this.recDate = recDate;
	}

	public String getIdAnswer() {
		return idAnswer;
	}

	public void setIdAnswer(String idAnswer) {
		this.idAnswer = idAnswer;
	}

	public int getKindOfReceipt() {
		return kindOfReceipt;
	}

	public void setKindOfReceipt(int kindOfReceipt) {
		this.kindOfReceipt = kindOfReceipt;
	}
	
}

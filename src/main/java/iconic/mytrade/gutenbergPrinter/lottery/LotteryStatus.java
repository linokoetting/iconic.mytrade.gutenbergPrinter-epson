package iconic.mytrade.gutenbergPrinter.lottery;

public class LotteryStatus {
	private String tillId;
	private int zRepNum;
	private String date;
	private int kindOfRequest;
	private int filesToSend;
	private int oldFilesToSend;
	private int rejectedFiles;
	private int waitingReceipts;
	private int receiptsToSend;
	private int acceptedReceipts;
	private int rejectedReceipts;
	private int NumRemainingCodes;
	private int ILVersion;
	private int LastReqResult;
	private String SubError;
		
	private LotteryStatus(String tillId, int zRepNum, String date, int kindOfRequest,
						  int filesToSend, int oldFilesToSend, int rejectedFiles, int waitingReceipts,
						  int receiptsToSend, int acceptedReceipts, int rejectedReceipts,
						  int NumRemainingCodes, int ILVersion, int LastReqResult, String SubError) {
			this.tillId = tillId;
			this.zRepNum = zRepNum;
			this.date = date;
			this.kindOfRequest = kindOfRequest;
			this.filesToSend = filesToSend;
			this.oldFilesToSend = oldFilesToSend;
			this.rejectedFiles = rejectedFiles;
			this.waitingReceipts = waitingReceipts;
			this.receiptsToSend = receiptsToSend;
			this.acceptedReceipts = acceptedReceipts;
			this.rejectedReceipts = rejectedReceipts;
	      	this.NumRemainingCodes = NumRemainingCodes;
	      	this.ILVersion = ILVersion;
	      	this.LastReqResult = LastReqResult;
	      	this.SubError = SubError;
	}
	
	public LotteryStatus(String result)
	{
		//System.out.println("LotteryStatus - result : "+result);
		if((result.length()) >= 26)
		{
			this.tillId = result.substring(2,10);
			this.zRepNum = Integer.parseInt(result.substring(10,14));
			this.date = result.substring(14,20);
			this.kindOfRequest = Integer.parseInt(result.substring(20,22));
			this.filesToSend = Integer.parseInt(result.substring(22,26));
			this.oldFilesToSend = Integer.parseInt(result.substring(26,30));
			this.rejectedFiles = Integer.parseInt(result.substring(30,34));
			this.waitingReceipts = Integer.parseInt(result.substring(34,38));
			this.receiptsToSend = Integer.parseInt(result.substring(38,42));
			this.acceptedReceipts = Integer.parseInt(result.substring(42,46));
			this.rejectedReceipts = Integer.parseInt(result.substring(46,50));
	      	this.NumRemainingCodes = Integer.parseInt(result.substring(50, 52));
	      	this.ILVersion = Integer.parseInt(result.substring(52, 54));
	      	this.LastReqResult = Integer.parseInt(result.substring(54, 57));
	      	this.SubError = result.substring(57, 62);
		}
	}

	public String getTillId() {
		return tillId;
	}

	public void setTillId(String tillId) {
		this.tillId = tillId;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getzRepNum() {
		return zRepNum;
	}

	public void setzRepNum(int zRepNum) {
		this.zRepNum = zRepNum;
	}

	public int getKindOfRequest() {
		return kindOfRequest;
	}

	public void setKindOfRequest(int kindOfRequest) {
		this.kindOfRequest = kindOfRequest;
	}

	public int getFilesToSend() {
		return filesToSend;
	}

	public void setFilesToSend(int filesToSend) {
		this.filesToSend = filesToSend;
	}

	public int getOldFilesToSend() {
		return oldFilesToSend;
	}

	public void setOldFilesToSend(int oldFilesToSend) {
		this.oldFilesToSend = oldFilesToSend;
	}

	public int getRejectedFiles() {
		return rejectedFiles;
	}

	public void setRejectedFiles(int rejectedFiles) {
		this.rejectedFiles = rejectedFiles;
	}

	public int getWaitingReceipts() {
		return waitingReceipts;
	}

	public void setWaitingReceipts(int waitingReceipts) {
		this.waitingReceipts = waitingReceipts;
	}

	public int getReceiptsToSend() {
		return receiptsToSend;
	}

	public void setReceiptsToSend(int receiptsToSend) {
		this.receiptsToSend = receiptsToSend;
	}

	public int getAcceptedReceipts() {
		return acceptedReceipts;
	}

	public void setAcceptedReceipts(int acceptedReceipts) {
		this.acceptedReceipts = acceptedReceipts;
	}

	public int getRejectedReceipts() {
		return rejectedReceipts;
	}

	public void setRejectedReceipts(int rejectedReceipts) {
		this.rejectedReceipts = rejectedReceipts;
	}
	
	public int getNumRemainingCodes() {
		return NumRemainingCodes;
	}

	public void setNumRemainingCodes(int numRemainingCodes) {
		NumRemainingCodes = numRemainingCodes;
	}

	public int getILVersion() {
		return ILVersion;
	}

	public void setILVersion(int iLVersion) {
		ILVersion = iLVersion;
	}

	public int getLastReqResult() {
		return LastReqResult;
	}

	public void setLastReqResult(int lastReqResult) {
		LastReqResult = lastReqResult;
	}

	public String getSubError() {
		return SubError;
	}

	public void setSubError(String subError) {
		SubError = subError;
	}

}

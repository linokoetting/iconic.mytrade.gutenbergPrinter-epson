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
	
	LotteryStatus(String result)
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

	String getTillId() {
		return tillId;
	}

	void setTillId(String tillId) {
		this.tillId = tillId;
	}

	String getDate() {
		return date;
	}

	private void setDate(String date) {
		this.date = date;
	}

	int getzRepNum() {
		return zRepNum;
	}

	private void setzRepNum(int zRepNum) {
		this.zRepNum = zRepNum;
	}

	int getKindOfRequest() {
		return kindOfRequest;
	}

	private void setKindOfRequest(int kindOfRequest) {
		this.kindOfRequest = kindOfRequest;
	}

	int getFilesToSend() {
		return filesToSend;
	}

	private void setFilesToSend(int filesToSend) {
		this.filesToSend = filesToSend;
	}

	int getOldFilesToSend() {
		return oldFilesToSend;
	}

	private void setOldFilesToSend(int oldFilesToSend) {
		this.oldFilesToSend = oldFilesToSend;
	}

	int getRejectedFiles() {
		return rejectedFiles;
	}

	private void setRejectedFiles(int rejectedFiles) {
		this.rejectedFiles = rejectedFiles;
	}

	int getWaitingReceipts() {
		return waitingReceipts;
	}

	private void setWaitingReceipts(int waitingReceipts) {
		this.waitingReceipts = waitingReceipts;
	}

	int getReceiptsToSend() {
		return receiptsToSend;
	}

	private void setReceiptsToSend(int receiptsToSend) {
		this.receiptsToSend = receiptsToSend;
	}

	int getAcceptedReceipts() {
		return acceptedReceipts;
	}

	private void setAcceptedReceipts(int acceptedReceipts) {
		this.acceptedReceipts = acceptedReceipts;
	}

	int getRejectedReceipts() {
		return rejectedReceipts;
	}

	private void setRejectedReceipts(int rejectedReceipts) {
		this.rejectedReceipts = rejectedReceipts;
	}
	
	int getNumRemainingCodes() {
		return NumRemainingCodes;
	}

	private void setNumRemainingCodes(int numRemainingCodes) {
		NumRemainingCodes = numRemainingCodes;
	}

	int getILVersion() {
		return ILVersion;
	}

	private void setILVersion(int iLVersion) {
		ILVersion = iLVersion;
	}

	int getLastReqResult() {
		return LastReqResult;
	}

	private void setLastReqResult(int lastReqResult) {
		LastReqResult = lastReqResult;
	}

	String getSubError() {
		return SubError;
	}

	private void setSubError(String subError) {
		SubError = subError;
	}

}

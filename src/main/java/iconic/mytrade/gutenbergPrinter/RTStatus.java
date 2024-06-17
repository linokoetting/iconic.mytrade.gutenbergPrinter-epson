package iconic.mytrade.gutenbergPrinter;

public class RTStatus {
	   String rtType;
	   int mainStatus;
	   int subStatus;
	   boolean dailyOpen;
	   boolean rtNoWorkingPeriod;
	   int rtFileToSend;
	   int rtOldFileToSend;
	   int rtFileRejected;
	   int fwBuildNumber;
	   int lastFwUpdateResult;
	   int outOfService;
	   String expiryDateCD;
	   String expiryDateCA;
	   int dgfeFileSystem;
	   int trainingMode;
	   int archivedFileRejected;
	   int ripristinoCertif;
		
		RTStatus(String rtType, int mainStatus, int subStatus, boolean dailyOpen,
						 boolean rtNoWorkingPeriod, int rtFileToSend, int rtOldFileToSend, int rtFileRejected,
						 int fwBuildNumber, int lastFwUpdateResult, int outOfService,
						 String expiryDateCD, String expiryDateCA, int dgfeFileSystem,
						 int trainingMode, int archivedFileRejected, int ripristinoCertif) {
			this.rtType = rtType;
			this.mainStatus = mainStatus;
			this.subStatus = subStatus;
			this.dailyOpen = dailyOpen;
			this.rtNoWorkingPeriod = rtNoWorkingPeriod;
			this.rtFileToSend = rtFileToSend;
			this.rtOldFileToSend = rtOldFileToSend;
			this.rtFileRejected = rtFileRejected;
			this.fwBuildNumber = fwBuildNumber;
			this.lastFwUpdateResult = lastFwUpdateResult;
			this.outOfService = outOfService;
			this.expiryDateCD = expiryDateCD;
			this.expiryDateCA = expiryDateCA;
			this.dgfeFileSystem = dgfeFileSystem;
			this.trainingMode = trainingMode;
			this.archivedFileRejected = archivedFileRejected;
			this.ripristinoCertif = ripristinoCertif;
		}
	
		RTStatus(String result)
		{
			//System.out.println("RTStatus - result : "+result);
			if((result.length()) >= 30)
			{
				//System.out.println("RTStatus - Printer is RT model");
				this.rtType = result.substring(2,3);
				this.mainStatus = Integer.parseInt(result.substring(3,5));
				this.subStatus = Integer.parseInt(result.substring(5,7));
				this.dailyOpen = false;
				this.rtNoWorkingPeriod = false;
				if(result.substring(7,8).equals("1")) {
					this.dailyOpen = true;
				}
				if(result.substring(8,9).equals("1")) {
					this.rtNoWorkingPeriod = true;
				}
				this.rtFileToSend = Integer.parseInt(result.substring(9,13));
				this.rtOldFileToSend = Integer.parseInt(result.substring(13,17));
				this.rtFileRejected = Integer.parseInt(result.substring(17,21));
				this.expiryDateCD = (result.toString()).substring(21,27);
				this.expiryDateCA = (result.toString()).substring(27,33);
				this.fwBuildNumber = Integer.parseInt(result.substring(33,37));
				this.dgfeFileSystem = Integer.parseInt(result.substring(37,38));
				this.trainingMode = Integer.parseInt(result.substring(38,39));
				this.lastFwUpdateResult = Integer.parseInt(result.substring(39,40));
				this.archivedFileRejected = Integer.parseInt(result.substring(40,44));
				this.outOfService = Integer.parseInt(result.substring(44,45));
				this.ripristinoCertif = Integer.parseInt(result.substring(45,46));
			}
		}
	
		String getRtType() {
			return rtType;
		}
	
		private void setRtType(String rtType) {
			this.rtType = rtType;
		}
	
		private int getMainStatus() {
			return mainStatus;
		}
	
		private void setMainStatus(int mainStatus) {
			this.mainStatus = mainStatus;
		}
	
		private int getSubStatus() {
			return subStatus;
		}
	
		private void setSubStatus(int subStatus) {
			this.subStatus = subStatus;
		}
	
		public boolean isDailyOpen() {
			return dailyOpen;
		}
	
		private void setDailyOpen(boolean dailyOpen) {
			this.dailyOpen = dailyOpen;
		}
	
		private boolean isRtNoWorkingPeriod() {
			return rtNoWorkingPeriod;
		}
	
		private void setRtNoWorkingPeriod(boolean rtNoWorkingPeriod) {
			this.rtNoWorkingPeriod = rtNoWorkingPeriod;
		}
	
		private int getRtFileToSend() {
			return rtFileToSend;
		}
	
		private void setRtFileToSend(int rtFileToSend) {
			this.rtFileToSend = rtFileToSend;
		}
	
		private int getRtOldFileToSend() {
			return rtOldFileToSend;
		}
	
		private void setRtOldFileToSend(int rtOldFileToSend) {
			this.rtOldFileToSend = rtOldFileToSend;
		}
	
		private int getRtFileRejected() {
			return rtFileRejected;
		}
	
		private void setRtFileRejected(int rtFileRejected) {
			this.rtFileRejected = rtFileRejected;
		}
	
		private int getFwBuildNumber() {
			return fwBuildNumber;
		}

		private void setFwBuildNumber(int fwBuildNumber) {
			this.fwBuildNumber = fwBuildNumber;
		}

		private int getLastFwUpdateResult() {
			return lastFwUpdateResult;
		}

		private void setLastFwUpdateResult(int lastFwUpdateResult) {
			this.lastFwUpdateResult = lastFwUpdateResult;
		}

		int getOutOfService() {
			return outOfService;
		}

		private void setOutOfService(int outOfService) {
			this.outOfService = outOfService;
		}
		
/*				public String toString() {
			return "RTStatus [rtType=" + rtType + ", mainStatus=" + mainStatus + ", subStatus=" + subStatus + ", dailyOpen="
					+ dailyOpen + ", rtNoWorkingPeriod=" + rtNoWorkingPeriod + ", rtFileToSend=" + rtFileToSend
					+ ", rtOldFileToSend=" + rtOldFileToSend + ", rtFileRejected=" + rtFileRejected
					+ ", fwBuildNumber=" + fwBuildNumber + ", lastFwUpdateResult=" + lastFwUpdateResult + ", outOfService=" + outOfService + "]";
		}*/
}

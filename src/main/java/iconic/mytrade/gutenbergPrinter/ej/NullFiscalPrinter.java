// Source File Name:   NullFiscalPrinter.java

package iconic.mytrade.gutenbergPrinter.ej;

import jpos.FiscalPrinterConst;
import jpos.JposException;
import jpos.services.FiscalPrinterService17;

public class NullFiscalPrinter extends BasicSimulator implements FiscalPrinterService17, FiscalPrinterConst
{

    public NullFiscalPrinter()
    {
        mode = 1;
    }

    public boolean getAsyncMode()
    {
        return true;
    }

    public void setAsyncMode(boolean flag)
    {
    }

    public boolean getCapAmountAdjustment()
    {
        return true;
    }

    public boolean getCapAmountNotPaid()
    {
        return true;
    }

    public boolean getCapCoverSensor()
    {
        return true;
    }

    public boolean getCapDuplicateReceipt()
    {
        return true;
    }

    public boolean getCapFixedOutput()
    {
        return true;
    }

    public boolean getCapHasVatTable()
    {
        return true;
    }

    public boolean getCapIndependentHeader()
    {
        return true;
    }

    public boolean getCapItemList()
    {
        return true;
    }

    public boolean getCapJrnEmptySensor()
    {
        return true;
    }

    public boolean getCapJrnNearEndSensor()
    {
        return true;
    }

    public boolean getCapJrnPresent()
    {
        return true;
    }

    public boolean getCapNonFiscalMode()
    {
        return true;
    }

    public boolean getCapOrderAdjustmentFirst()
    {
        return true;
    }

    public boolean getCapPercentAdjustment()
    {
        return true;
    }

    public boolean getCapPositiveAdjustment()
    {
        return true;
    }

    public boolean getCapRecEmptySensor()
    {
        return true;
    }

    public boolean getCapRemainingFiscalMemory()
    {
        return true;
    }

    public boolean getCapReservedWord()
    {
        return true;
    }

    public boolean getCapSetHeader()
    {
        return true;
    }

    public boolean getCapSetPOSID()
    {
        return true;
    }

    public boolean getCapSetStoreFiscalID()
    {
        return true;
    }

    public boolean getCapSetTrailer()
    {
        return true;
    }

    public boolean getCapSetVatTable()
    {
        return true;
    }

    public boolean getCapSlpEmptySensor()
    {
        return true;
    }

    public boolean getCapSlpFiscalDocument()
    {
        return true;
    }

    public boolean getCapSlpFullSlip()
    {
        return true;
    }

    public boolean getCapSlpPresent()
    {
        return true;
    }

    public boolean getCapSlpValidation()
    {
        return true;
    }

    public boolean getCapSubAmountAdjustment()
    {
        return true;
    }

    public boolean getCapValidateJournal()
    {
        return true;
    }

    public boolean getCapXReport()
    {
        return true;
    }

    public boolean getCapSubPercentAdjustment()
    {
        return true;
    }

    public boolean getCapReceiptNotPaid()
    {
        return true;
    }

    public boolean getCapAdditionalLines()
    {
        return true;
    }

    public boolean getCapCheckTotal()
    {
        return true;
    }

    public boolean getCapRecNearEndSensor()
    {
        return true;
    }

    public boolean getCapSlpNearEndSensor()
    {
        return true;
    }

    public boolean getCapSubtotal()
    {
        return true;
    }

    public boolean getCapDoubleWidth()
    {
        return true;
    }

    public boolean getCapPowerLossReport()
    {
        return true;
    }

    public boolean getCheckTotal()
    {
        return true;
    }

    public void setCheckTotal(boolean flag)
    {
    }

    public boolean getCapPredefinedPaymentLines()
    {
        return true;
    }

    public boolean getCapTrainingMode()
    {
        return true;
    }

    public boolean getCapRecPresent()
    {
        return true;
    }
    
    public boolean getCoverOpen() throws JposException
    {
        return false;
    }

    public boolean getDayOpened()
    {
        return false;
    }

    public boolean getDuplicateReceipt()
    {
        return false;
    }

    public void setDuplicateReceipt(boolean flag)
    {
    }

    public boolean getFlagWhenIdle()
    {
        return false;
    }

    public void setFlagWhenIdle(boolean flag)
    {
    }

    public boolean getJrnEmpty() throws JposException
    {
        return false;
    }

    public boolean getJrnNearEnd()
    {
        return false;
    }

    public boolean getRecEmpty() throws JposException
    {
        return false;
    }

    public boolean getRecNearEnd()
    {
        return false;
    }

    public boolean getSlpEmpty()
    {
        return false;
    }

    public boolean getSlpNearEnd()
    {
        return false;
    }

    public boolean getTrainingModeActive()
    {
        return false;
    }

    public int getCountryCode()
    {
        return 0;
    }

    public int getErrorLevel()
    {
        return 0;
    }

    public int getErrorState()
    {
        return 0;
    }

    public int getErrorStation()
    {
        return 0;
    }

    public int getMessageLength()
    {
        return 0;
    }

    public int getNumTrailerLines()
    {
        return 0;
    }

    public int getNumVatRates()
    {
        return 0;
    }

    public int getPrinterState()
    {
        return 0;
    }

    public int getQuantityDecimalPlaces()
    {
        return 0;
    }

    public int getQuantityLength()
    {
        return 0;
    }

    public int getRemainingFiscalMemory()
    {
        return 0;
    }

    public int getSlipSelection()
    {
        return 0;
    }

    public void setSlipSelection(int i)
    {
    }

    public int getNumHeaderLines()
    {
        return 0;
    }

    public int getErrorOutID()
    {
        return 0;
    }

    public int getAmountDecimalPlace()
    {
        return 0;
    }

    public int getDescriptionLength()
    {
        return 0;
    }

    public String getErrorString()
    {
        return null;
    }

    public String getReservedWord()
    {
        return null;
    }

    public String getPredefinedPaymentLines()
    {
        return null;
    }

    public void beginFiscalDocument(int i) throws JposException
    {
    }

    public void beginFiscalReceipt(boolean flag) throws JposException
    {
    }

    public void beginNonFiscal() throws JposException
    {
    }

    public void beginRemoval(int i) throws JposException
    {
    }

    public void beginTraining() throws JposException
    {
    }

    public void endFiscalDocument() throws JposException
    {
    }

    public void endFiscalReceipt(boolean flag) throws JposException
    {
    }

    public void endFixedOutput() throws JposException
    {
    }

    public void endInsertion() throws JposException
    {
    }

    public void endItemList() throws JposException
    {
    }

    public void endNonFiscal() throws JposException
    {
    }

    public void endRemoval() throws JposException
    {
    }

    public void endTraining() throws JposException
    {
    }

    public void getDate(String as[])
        throws JposException
    {
    }

    public void getTotalizer(int i, int j, String as[]) throws JposException
    {
    }

    public void printDuplicateReceipt() throws JposException
    {
    }

    public void printFiscalDocumentLine(String s) throws JposException
    {
    }

    public void printFixedOutput(int i, int j, String s) throws JposException
    {
    }

    public void printPeriodicTotalsReport(String s, String s1) throws JposException
    {
    }

    public void printPowerLossReport() throws JposException
    {
    }

    public void printRecItem(String s, long l, int i, int j, long l1, String s1) throws JposException
    {
    }

    public void printRecItemAdjustment(int i, String s, long l, int j) throws JposException
    {
    }

    public void printRecMessage(String s) throws JposException
    {
    }

    public void printRecRefund(String s, long l, int i) throws JposException
    {
    }

    public void printRecSubtotal(long l) throws JposException
    {
    }

    public void printRecTotal(long l, long l1, String s) throws JposException
    {
    }

    public void printRecVoid(String s) throws JposException
    {
    }

    public void printRecVoidItem(String s, long l, int i, int j, long l1, int k) throws JposException
    {
    }

    public void printXReport() throws JposException
    {
    }

    public void resetPrinter() throws JposException
    {
    }

    public void setDate(String s) throws JposException
    {
    }

    public void setHeaderLine(int i, String s, boolean flag) throws JposException
    {
    }

    public void setPOSID(String s, String s1) throws JposException
    {
    }

    public void setStoreFiscalID(String s) throws JposException
    {
    }

    public void setTrailerLine(int i, String s, boolean flag) throws JposException
    {
    }

    public void setVatTable() throws JposException
    {
    }

    public void setVatValue(int i, String s) throws JposException
    {
    }

    public void verifyItem(String s, int i) throws JposException
    {
    }

    public void printZReport() throws JposException
    {
    }

    public void getData(int i, int ai[], String as[]) throws JposException
    {
    }

    public void printNormal(int i, String s) throws JposException
    {
    }

    public void printRecNotPaid(String s, long l) throws JposException
    {
    }

    public void printRecSubtotalAdjustment(int i, String s, long l) throws JposException
    {
    }

    public void beginItemList(int i) throws JposException
    {
    }

    public void beginInsertion(int i) throws JposException
    {
    }

    public void printReport(int i, String s, String s1) throws JposException
    {
    }

    public void getVatEntry(int i, int j, int ai[]) throws JposException
    {
    }

    public void beginFixedOutput(int i, int j) throws JposException
    {
    }

    public void clearError() throws JposException
    {
    }

    private int mode;

    //ENH-DDP#A BEGIN
		/*******************************************************************/
		/***    Methods of FiscalPrinterService17                        ***/
		/*******************************************************************/
    public boolean getCapAdditionalHeader() throws JposException
    {
    	return true;
    }

    public boolean getCapAdditionalTrailer() throws JposException
    {
    	return true;
    }

    public boolean getCapChangeDue() throws JposException
    {
    	return true;
    }

    public boolean getCapEmptyReceiptIsVoidable() throws JposException
    {
    	return true;
    }

    public boolean getCapFiscalReceiptStation() throws JposException
    {
    	return true;
    }

    public boolean getCapFiscalReceiptType() throws JposException
    {
    	return true;
    }

    public boolean getCapMultiContractor() throws JposException
    {
    	return true;
    }

    public boolean getCapOnlyVoidLastItem() throws JposException
    {
    	return true;
    }

    public boolean getCapPackageAdjustment() throws JposException
    {
    	return true;
    }

    public boolean getCapPostPreLine() throws JposException
    {
    	return true;
    }

    public boolean getCapSetCurrency() throws JposException
    {
    	return true;
    }

    public boolean getCapTotalizerType() throws JposException
    {
    	return true;
    }

    public int getActualCurrency() throws JposException
    {
    	return 0;
    }

    public String getAdditionalHeader() throws JposException
    {
    	return " ";
    }

    public void setAdditionalHeader(String s) throws JposException
    {
    }

    public String getAdditionalTrailer() throws JposException
    {
    	return " ";
    }

    public void setAdditionalTrailer(String s) throws JposException
    {
    }

    public String getChangeDue() throws JposException
    {
    	return " ";
    }

    public void setChangeDue(String s) throws JposException
    {
    }

    public int getContractorId() throws JposException
    {
    	return 0;
    }

    public void setContractorId(int i) throws JposException
    {
    }

    public int getDateType() throws JposException
    {
    	return 0;
    }

    public void setDateType(int i) throws JposException
    {
    }

    public int getFiscalReceiptStation() throws JposException
    {
    	return 0;
    }

    public void setFiscalReceiptStation(int i) throws JposException
    {
    }

    public int getFiscalReceiptType() throws JposException
    {
    	return 0;
    }

    public void setFiscalReceiptType(int i) throws JposException
    {
    }

    public int getMessageType() throws JposException
    {
    	return 0;
    }

    public void setMessageType(int i) throws JposException
    {
    }

    public String getPostLine() throws JposException
    {
    	return " ";
    }

    public void setPostLine(String s) throws JposException
    {
    }

    public String getPreLine() throws JposException
    {
    	return " ";
    }

    public void setPreLine(String s) throws JposException
    {
    }

    public int getTotalizerType() throws JposException
    {
    	return 0;
    }

    public void setTotalizerType(int i) throws JposException
    {
    }

    public void setCurrency(int i) throws JposException
    {
    }

    public void printRecCash(long l) throws JposException
    {
    }

    public void printRecItemFuel(String s, long l, int i, int j, long l1, 
            								String s1, long l2, String s2) throws JposException
    {
    }

    public void printRecItemFuelVoid(String s, long l, int i, long l1) throws JposException
    {
    }

    public void printRecPackageAdjustment(int i, String s, String s1) throws JposException
    {
    }

    public void printRecPackageAdjustVoid(int i, String s) throws JposException
    {
    }

    public void printRecRefundVoid(String s, long l, int i) throws JposException
    {
    }

    public void printRecSubtotalAdjustVoid(int i, long l) throws JposException
    {
    }

    public void printRecTaxID(String s) throws JposException
    {
    }

	public int getAmountDecimalPlaces() throws JposException {
		return 0;
	}
    
}

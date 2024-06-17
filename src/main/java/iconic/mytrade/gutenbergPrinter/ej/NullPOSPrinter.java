package iconic.mytrade.gutenbergPrinter.ej;

import jpos.JposException;
import jpos.POSPrinterConst;
import jpos.services.POSPrinterService14;

public class NullPOSPrinter extends BasicSimulator implements POSPrinterService14,POSPrinterConst
{
  protected static final int REC_LINES_TO_PAPER_CUT = 4;

  protected int characterSet = 932;

  public int getCapCharacterSet()
  {
    return PTR_CCS_KANJI;
  }

  public boolean getCapConcurrentJrnRec()
  {
    return true;
  }

  public boolean getCapConcurrentJrnSlp()
  {
    return false;
  }

  public boolean getCapConcurrentRecSlp()
  {
    return false;
  }

  public boolean getCapCoverSensor()
  {
    return true;
  }

  public boolean getCapJrn2Color()
  {
    return true;
  }

  public boolean getCapJrnBold()
  {
    return true;
  }

  public boolean getCapJrnDhigh()
  {
    return true;
  }

  public boolean getCapJrnDwide()
  {
    return true;
  }

  public boolean getCapJrnDwideDhigh()
  {
    return true;
  }

  public boolean getCapJrnEmptySensor()
  {
    return true;
  }

  public boolean getCapJrnItalic()
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

  public boolean getCapJrnUnderline()
  {
    return true;
  }

  public boolean getCapRec2Color()
  {
    return true;
  }

  public boolean getCapRecBarCode()
  {
    return true;
  }

  public boolean getCapRecBitmap()
  {
    return true;
  }

  public boolean getCapRecBold()
  {
    return true;
  }

  public boolean getCapRecDhigh()
  {
    return true;
  }

  public boolean getCapRecDwide()
  {
    return true;
  }

  public boolean getCapRecDwideDhigh()
  {
    return true;
  }

  public boolean getCapRecEmptySensor()
  {
    return true;
  }

  public boolean getCapRecItalic()
  {
    return true;
  }

  public boolean getCapRecLeft90()
  {
    return true;
  }

  public boolean getCapRecNearEndSensor()
  {
    return true;
  }

  public boolean getCapRecPapercut()
  {
    return true;
  }

  public boolean getCapRecPresent()
  {
    return true;
  }

  public boolean getCapRecRight90()
  {
    return true;
  }

  public boolean getCapRecRotate180()
  {
    return true;
  }

  public boolean getCapRecStamp()
  {
    return false;
  }

  public boolean getCapRecUnderline()
  {
    return true;
  }

  public boolean getCapSlp2Color()
  {
    return true;
  }

  public boolean getCapSlpBarCode()
  {
    return true;
  }

  public boolean getCapSlpBitmap()
  {
    return true;
  }

  public boolean getCapSlpBold()
  {
    return true;
  }

  public boolean getCapSlpDhigh()
  {
    return true;
  }

  public boolean getCapSlpDwide()
  {
    return true;
  }

  public boolean getCapSlpDwideDhigh()
  {
    return true;
  }

  public boolean getCapSlpEmptySensor()
  {
    return true;
  }

  public boolean getCapSlpFullslip()
  {
    return false;
  }

  public boolean getCapSlpItalic()
  {
    return true;
  }

  public boolean getCapSlpLeft90()
  {
    return true;
  }

  public boolean getCapSlpNearEndSensor()
  {
    return true;
  }

  public boolean getCapSlpPresent()
  {
    return true;
  }

  public boolean getCapSlpRight90()
  {
    return true;
  }

  public boolean getCapSlpRotate180()
  {
    return true;
  }

  public boolean getCapSlpUnderline()
  {
    return true;
  }

  public boolean getCapTransaction()
  {
    return false;
  }

  public boolean getAsyncMode()
  {
    return false;
  }

  public void setAsyncMode(boolean p0)
  {
  }

  public int getCharacterSet()
  {
    return characterSet;
  }

  public void setCharacterSet(int p0)
  {
    characterSet = p0;
  }

  public String getCharacterSetList()
  {
    return "932,998,999,850";
  }

  public boolean getCoverOpen()
  {
    return false;
  }

  public int getErrorLevel()
  {
    return 0;
  }

  public int getErrorStation()
  {
    return 0;
  }

  public String getErrorString()
  {
    return "";
  }

  public boolean getFlagWhenIdle()
  {
    return false;
  }

  public void setFlagWhenIdle(boolean p0)
  {
  }

  public String getFontTypefaceList()
  {
    return "";
  }

  public boolean getJrnEmpty()
  {
    return false;
  }

  public boolean getJrnLetterQuality()
  {
    return false;
  }

  public void setJrnLetterQuality(boolean p0)
  {
  }

  public int getJrnLineChars()
  {
    return 40;
  }

  public void setJrnLineChars(int p0)
  {
  }

  public String getJrnLineCharsList()
  {
    return "";
  }

  public int getJrnLineHeight()
  {
    return 16;
  }

  public void setJrnLineHeight(int p0)
  {
  }

  public int getJrnLineSpacing()
  {
    return 16;
  }

  public void setJrnLineSpacing(int p0)
  {
  }

  public int getJrnLineWidth()
  {
    return 320;
  }

  public boolean getJrnNearEnd()
  {
    return false;
  }

  public int getMapMode()
  {
    return 0;
  }

  public void setMapMode(int p0)
  {
  }

  public String getRecBarCodeRotationList()
  {
    return "";
  }

  public boolean getRecEmpty()
  {
    return false;
  }

  public boolean getRecLetterQuality()
  {
    return false;
  }

  public void setRecLetterQuality(boolean p0)
  {
  }

  public int getRecLineChars()
  {
    return 40;
  }

  public void setRecLineChars(int p0)
  {
  }

  public String getRecLineCharsList()
  {
    return "";
  }

  public int getRecLineHeight()
  {
    return 16;
  }

  public void setRecLineHeight(int p0)
  {
  }

  public int getRecLineSpacing()
  {
    return 16;
  }

  public void setRecLineSpacing(int p0)
  {
  }

  public int getRecLinesToPaperCut()
  {
    return REC_LINES_TO_PAPER_CUT;
  }

  public int getRecLineWidth()
  {
    return 320;
  }

  public boolean getRecNearEnd()
  {
    return false;
  }

  public int getRecSidewaysMaxChars()
  {
    return 0;
  }

  public int getRecSidewaysMaxLines()
  {
    return 0;
  }

  public int getRotateSpecial()
  {
    return 0;
  }

  public void setRotateSpecial(int p0)
  {
  }

  public String getSlpBarCodeRotationList()
  {
    return "";
  }

  public boolean getSlpEmpty()
  {
    return false;
  }

  public boolean getSlpLetterQuality()
  {
    return false;
  }

  public void setSlpLetterQuality(boolean p0)
  {
  }

  public int getSlpLineChars()
  {
    return 80;
  }

  public void setSlpLineChars(int p0)
  {
  }

  public String getSlpLineCharsList()
  {
    return "";
  }

  public int getSlpLineHeight()
  {
    return 16;
  }

  public void setSlpLineHeight(int p0)
  {
  }

  public int getSlpLinesNearEndToEnd()
  {
    return 0;
  }

  public int getSlpLineSpacing()
  {
    return 16;
  }

  public void setSlpLineSpacing(int p0)
  {
  }

  public int getSlpLineWidth()
  {
    return 640;
  }

  public int getSlpMaxLines()
  {
    return 0;
  }

  public boolean getSlpNearEnd()
  {
    return false;
  }

  public int getSlpSidewaysMaxChars()
  {
    return 0;
  }

  public int getSlpSidewaysMaxLines()
  {
    return 0;
  }

  public void beginInsertion(int p0) throws JposException
  {
  }

  public void beginRemoval(int p0) throws JposException
  {
  }

  public void cutPaper(int p0) throws JposException
  {
  }

  public void endInsertion() throws JposException
  {
  }

  public void endRemoval() throws JposException
  {
  }

  public void printBarCode(int p0,String p1,int p2,int p3,int p4,int p5,int p6) throws JposException
  {
  }

  public void printBitmap(int p0,String p1,int p2,int p3) throws JposException
  {
  }

  public void printImmediate(int p0,String p1) throws JposException
  {
  }

  public void printNormal(int p0,String p1) throws JposException
  {
  }

  public void printTwoNormal(int p0,String p1,String p2) throws JposException
  {
  }

  public void rotatePrint(int p0,int p1) throws JposException
  {
  }

  public void setBitmap(int p0,int p1,String p2,int p3,int p4) throws JposException
  {
  }

  public void setLogo(int p0,String p1) throws JposException
  {
  }

  public void transactionPrint(int p0,int p1) throws JposException
  {
  }

  public void validateData(int p0,String p1) throws JposException
  {
  }
}

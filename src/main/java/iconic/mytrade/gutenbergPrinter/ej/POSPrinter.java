package iconic.mytrade.gutenbergPrinter.ej;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Checkbox;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.ScrollPane;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.Vector;

import jpos.JposConst;
import jpos.JposException;
import jpos.POSPrinterConst;
import jpos.services.EventCallbacks;

public class POSPrinter extends NullPOSPrinter implements POSPrinterConst,JposConst
{
  private static final int MAX_LINES = 500;

  private Frame frame = new Frame();
  private PrinterList[] dummyPrinters = new PrinterList[3];

  // holds the current line
  private StringBuffer[] buffers = new StringBuffer[3];

  private Checkbox recLow = new Checkbox("Receipt low");
  private Checkbox recEmpty = new Checkbox("Receipt empty");

  private Checkbox jrnLow = new Checkbox("Journal low");
  private Checkbox jrnEmpty = new Checkbox("Journal empty");

  private Checkbox slipLow = new Checkbox("Slip low");
  private Checkbox slipIn = new Checkbox("Slip in");
  private Checkbox slipAuto = new Checkbox("Slip auto");

  private Checkbox coverOpen = new Checkbox("Cover open");
  private Checkbox paperJam = new Checkbox("Paper jam");

  private SaveBitmap[] saveBitmap = new SaveBitmap[4];
  private boolean enabled = false;

  // set to indicate slip insertion mode
  private boolean slipInsertionMode;

  // set to indicate slip removal mode
  private boolean slipRemovalMode;

  // print rotation
  private boolean recRotated = false;
  private boolean slipRotated = false;

  /**
   * See the JavaPOS documentation.
  **/
  public void open(String logicalName,EventCallbacks cb)
  {
    GridBagLayout layout = new GridBagLayout();
    GridBagConstraints constraints = new GridBagConstraints();
    Panel slipPanel = new Panel();
    Panel topPanel = new Panel();
    Component component = null;

    // use fixed size font
    frame.setTitle("Printer "+logicalName);
    frame.setFont(new Font("Monospaced",Font.PLAIN,12));
     
    frame.setLayout(layout);

    for (int i=0; i<3; i++)
    {
      dummyPrinters[i] = new PrinterList();
      buffers[i] = new StringBuffer();

      switch (i)
      {
        case 0:
          component = dummyPrinters[0];
          break;
        case 1:
          component = dummyPrinters[1];
          break;
        case 2:
          slipPanel.setLayout(new BorderLayout());
          slipPanel.add("Center",dummyPrinters[2]);
          slipPanel.add("North",topPanel);
          topPanel.setLayout(new GridLayout(3,3));
          topPanel.add(recLow);
          topPanel.add(jrnLow);
          topPanel.add(slipLow);
          topPanel.add(recEmpty);
          topPanel.add(jrnEmpty);
          topPanel.add(slipIn);
          topPanel.add(paperJam);
          topPanel.add(coverOpen);
          topPanel.add(slipAuto);
          component = slipPanel;
          break;
      }

      constraints.gridx = (i==1) ? 1 : 0;
      constraints.gridy = (i==2) ? 2 : 0;
      constraints.gridwidth = (i==2) ? 2 : 1;
      constraints.gridheight = 1;
      constraints.weightx = constraints.weighty = 1;
      constraints.fill = GridBagConstraints.BOTH;
      layout.setConstraints(component,constraints);
      frame.add(component);
    }

    slipIn.setEnabled(false);

    frame.pack();
    frame.setSize(500,400);
    setPosition(frame);
    frame.show();
  }
 
  /**
   * See the JavaPOS documentation.
  **/
  public void close()
  {
    frame.dispose();
  }

  /**
   * See the JavaPOS documentation.
  **/
  public void setDeviceEnabled(boolean deviceEnabled)
  {
    enabled = deviceEnabled;
  }

  /**
   * See the JavaPOS documentation.
   * @exception JposException see the JavaPOS documentation
  **/
  public void beginInsertion(int p0) throws JposException
  {
    slipInsertionMode = true;
    slipIn.setEnabled(true);
    if (slipAuto.getState())
      slipIn.setState(true);
  }

  /**
   * See the JavaPOS documentation.
   * @exception JposException see the JavaPOS documentation
  **/
  public void printTwoNormal(int p0,String p1,String p2) throws JposException
  {
    if (p0 == PTR_S_JOURNAL_RECEIPT)
    {
      if (recRotated)
        throw new JposException(JPOS_E_ILLEGAL);

      printNormal(PTR_S_JOURNAL,p1);
      printNormal(PTR_S_RECEIPT,p2.equals("") ? p1: p2);
    }
    else
      throw new JposException(JPOS_E_ILLEGAL);
  }

  /**
   * See the JavaPOS documentation.
   * @exception JposException see the JavaPOS documentation
  **/
  public void printNormal(int p0,String p1) throws JposException
  {
    StringBuffer buffer;
    PrinterList dummyPrinter;
    Reader inp;
    int prefix = 0;
    int number = 0;
    int index = 0;
    int mode = 0;
    int c;

    try
    {
      String encoding;
      switch (characterSet)
      {
        case PTR_CS_ASCII:
          encoding = "ASCII";
          break;
        case PTR_CS_ANSI:
          encoding = "Cp1252";
          break;
        case 932:
          encoding = "SJIS";
          break;
        default:
          encoding = "Cp"+characterSet;
          break;
      }

      byte[] bytes = new byte[p1.length()];
      p1.getBytes(0,bytes.length,bytes,0);
      inp = new InputStreamReader(new ByteArrayInputStream(bytes),encoding);
    }
    catch (UnsupportedEncodingException e)
    {
      inp = new StringReader(p1);
    }

    switch (p0)
    {
      case PTR_S_JOURNAL:
        index = 1;
        // fall through to next case
      case PTR_S_RECEIPT:
        if (slipIn.getState())
          throw new JposException(JPOS_E_ILLEGAL);
        break;
      case PTR_S_SLIP:
        index = 2;
        break;
      default:
        throw new JposException(JPOS_E_ILLEGAL);
    }

    checkSimulatedErrors(p0);

    buffer = buffers[index];
    dummyPrinter = dummyPrinters[index];

    // finite state machine to interpret JavaPOS escape sequences
    for (;;)
    {
      try
      {
        c = inp.read();
        if (c < 0)
          break;
      }
      catch (IOException e)
      {
        throw new JposException(JPOS_E_FAILURE);
      }

      switch (mode)
      {
        case 0:
          if (c == '\u001b')
          {
            mode = 1;
            number = 0;
            prefix = 0;
          }
          else
          if (c == '\n')
          {
            // here is where we actually print the data
            addItem(dummyPrinter,buffer.toString());
            buffer.setLength(0);
          }
          else
          if (c != '\r')
            buffer.append((char)c);
          break;
        case 1:
          if (c == '|')
            mode = 2;
          else
            mode = 0;
          break;
        case 2:
          switch (c)
          {
            case 'P':
              // cut paper
              cutPaper(0);
              mode = 0;
              break;
            case 'B':
              // print a stored bitmap
              SaveBitmap bitmap = saveBitmap[(number-1)*(p0==PTR_S_SLIP ? 2 : 0)];
              printBitmap(p0,bitmap.filename,bitmap.width,bitmap.alignment);
              mode = 0;
              break;
            case 'L':
              // print logo or fire the stamp solenoid
              // not implemented
              mode = 0;
              break;
            case 'F':
              // paper feed
              // not yet implemented
              mode = 0;
              break;
            case 'C':
              // print characteristics
              switch (prefix)
              {
                case 'b':
                  buffer.append("<B>");
                  break;
                case 'i':
                  buffer.append("<I>");
                  break;
                case 'r':
                  buffer.append("<R>");
                  break;
                case 'u':
                  buffer.append("<U>");
                  break;
              }
              switch (number)
              {
                case 1:
                  buffer.append("<N>");
                  break;
                case 2:
                  buffer.append("<W>");
                  break;
                case 3:
                  buffer.append("<H>");
                  break;
                case 4:
                  buffer.append("<D>");
                  break;
              }
              mode = 0;
              break;
            case 'A':
              // text alignment
              // not yet implemented
              mode = 0;
              break;
            case 'N':
              // normal printing
              buffer.append("<N>");
              mode = 0;
              break;
            default:
              if (c>='a' && c<='z')
                prefix = c;
              else
              if (c>='0' && c<='9')
                number = number*10 + c - '0';
              else
                mode = 0;
              break;
          }
          break;
      }
    }
  }
  
  /**
   * See the JavaPOS documentation.
  **/
  public void cutPaper(int p0)
  {
    int count;

    count = dummyPrinters[0].getItemCount();
    dummyPrinters[0].addItem("<------------ CUT ------------>",count-REC_LINES_TO_PAPER_CUT);
    dummyPrinters[0].select(count);
  }

  /**
   * See the JavaPOS documentation.
   * @exception JposException see the JavaPOS documentation
  **/
  public void endInsertion() throws JposException
  {
    // must be in insertion mode
    if (!slipInsertionMode)
      throw new JposException(JPOS_E_ILLEGAL);

    slipIn.setEnabled(false);
    slipInsertionMode = false;

    if (!slipIn.getState())
      throw new JposException(JPOS_E_EXTENDED,JPOS_EPTR_SLP_EMPTY);

    addItem(dummyPrinters[2],"<INSERT>");
  }

  /**
   * See the JavaPOS documentation.
   * @exception JposException see the JavaPOS documentation
  **/
  public void printImmediate(int p0,String p1) throws JposException
  {
    printNormal(p0,p1);
  }

  /**
   * See the JavaPOS documentation.
   * @exception JposException see the JavaPOS documentation
  **/
  public void endRemoval() throws JposException
  {
    // must be in removal mode
    if (!slipRemovalMode)
      throw new JposException(JPOS_E_ILLEGAL);

    slipRemovalMode = false;
    slipIn.setEnabled(false);

    if (slipIn.getState())
      throw new JposException(JPOS_E_EXTENDED,JPOS_EPTR_SLP_FORM);

    addItem(dummyPrinters[2],"<EJECT>");
  }

  /**
   * See the JavaPOS documentation.
   * @exception JposException see the JavaPOS documentation
  **/
  public void rotatePrint(int p0,int p1) throws JposException
  {
    switch (p0)
    {
      case PTR_S_RECEIPT:
        recRotated = p1!=PTR_RP_NORMAL;
        addItem(dummyPrinters[0],recRotated ? "<START ROTATE>" : "<END ROTATE>");
        break;
      case PTR_S_SLIP:
        slipRotated = p1!=PTR_RP_NORMAL;
        addItem(dummyPrinters[2],slipRotated ? "<START ROTATE>" : "<END ROTATE>");
        break;
      default:
        throw new JposException(JPOS_E_ILLEGAL);
    }
  }

  /**
   * See the JavaPOS documentation.
   * @exception JposException see the JavaPOS documentation
  **/
  public void beginRemoval(int p0) throws JposException
  {
    slipRemovalMode = true;
    slipIn.setEnabled(true);
    if (slipAuto.getState())
      slipIn.setState(false);
  }

  /**
   * See the JavaPOS documentation.
  **/
  public boolean getJrnNearEnd()
  {
    return jrnLow.getState();
  }

  /**
   * See the JavaPOS documentation.
  **/
  public boolean getRecNearEnd()
  {
    return recLow.getState();
  }

  /**
   * See the JavaPOS documentation.
  **/
  public boolean getSlpNearEnd()
  {
    return slipLow.getState();
  }

  /**
   * See the JavaPOS documentation.
  **/
  public boolean getJrnEmpty()
  {
    return jrnEmpty.getState();
  }

  /**
   * See the JavaPOS documentation.
  **/
  public boolean getRecEmpty()
  {
    return recEmpty.getState();
  }

  /**
   * See the JavaPOS documentation.
  **/
  public boolean getSlpEmpty()
  {
    // check slipIn checkbox
    return !slipIn.getState();
  }

  /**
   * See the JavaPOS documentation.
  **/
  public boolean getCoverOpen()
  {
    return coverOpen.getState();
  }

  /**
   * See the JavaPOS documentation.
  **/
  public void setBitmap(int p0,int p1,String p2,int p3,int p4)
  {
    saveBitmap[(p0-1)*(p1==PTR_S_SLIP ? 2 : 0)] = new SaveBitmap(p2,p3,p4);
  }

  /**
   * See the JavaPOS documentation.
   * @exception JposException see the JavaPOS documentation
  **/
  public void printBitmap(int p0,String p1,int p2,int p3) throws JposException
  {
    PrinterList dummyPrinter;
    int index = 0;

    switch (p0)
    {
      case PTR_S_JOURNAL:
        index = 1;
        // fall through to next case
      case PTR_S_RECEIPT:
        if (slipIn.getState())
          throw new JposException(JPOS_E_ILLEGAL);
        break;
      case PTR_S_SLIP:
        index = 2;
        break;
      default:
        throw new JposException(JPOS_E_ILLEGAL);
    }

    checkSimulatedErrors(p0);

    addItem(dummyPrinters[index],"<BITMAP> "+p1+" </BITMAP>");
  }

  /**
   * See the JavaPOS documentation.
   * @exception JposException see the JavaPOS documentation
  **/
  public void printBarCode(int p0,String p1,int p2,int p3,int p4,int p5,int p6) throws JposException
  {
    PrinterList dummyPrinter;
    int index = 0;

    switch (p0)
    {
      case PTR_S_JOURNAL:
        index = 1;
        // fall through to next case
      case PTR_S_RECEIPT:
        if (slipIn.getState())
          throw new JposException(JPOS_E_ILLEGAL);
        break;
      case PTR_S_SLIP:
        index = 2;
        break;
      default:
        throw new JposException(JPOS_E_ILLEGAL);
    }

    checkSimulatedErrors(p0);

    addItem(dummyPrinters[index],"<BARCODE> "+p1+" </BARCODE>");
  }

  /**
   * Check for simulated error conditions.
   * @exception JposException see the JavaPOS documentation
  **/
  private void checkSimulatedErrors(int station) throws JposException
  {
    switch (station)
    {
      case PTR_S_JOURNAL:
        if (jrnEmpty.getState())
          throw new JposException(JPOS_E_EXTENDED,JPOS_EPTR_JRN_EMPTY);
        break;
      case PTR_S_RECEIPT:
        if (recEmpty.getState())
          throw new JposException(JPOS_E_EXTENDED,JPOS_EPTR_REC_EMPTY);
        break;
      case PTR_S_SLIP:
        if (!slipIn.getState())
          throw new JposException(JPOS_E_EXTENDED,JPOS_EPTR_SLP_EMPTY);
        break;
    }

    if (coverOpen.getState())
      throw new JposException(JPOS_E_EXTENDED,JPOS_EPTR_COVER_OPEN);

    if (paperJam.getState())
      throw new JposException(JPOS_E_FAILURE);
  }

  /**
   * Add a string to a list; remove lines from the top of the list 
   * if it is getting too big, to stop it growing forever.
   * @param list the list
   * @param string the string
  **/
  private void addItem(PrinterList list,String string)
  {
    int count = list.getItemCount();

    while (count > MAX_LINES)
    {
      list.remove(0);
      count--;
    }

    list.addItem(string);
    list.select(count);
  }

  /**
   * Class to store bitmap details.
  **/
  public static class SaveBitmap
  {
    String filename;
    int width;
    int alignment;

    public SaveBitmap(String filename,int width,int alignment)
    {
      this.filename = filename;
      this.width = width;
      this.alignment = alignment;
    }
  }

  /**
   * This subclass of List supplies methods used for JavaStar testing.
   * The methods getLine1(), getLine2() etc return (respectively) the last
   * line, the line before last and so on.
   * <p>
   * The List class is horrendously inefficient (in JDK 1.1.7) so I've modified
   * this to do its own drawing. Now it subclasses ScrollPane instead.
   * Note that the code tries to ensure that the vertical scrollbar is always
   * moved to its bottom position after an update. This does not always work in
   * practice - there appears to be a subtle bug in the AWT - but this problem
   * is outweighted by the performance improvment got from this code.
  **/
  public static class PrinterList extends ScrollPane
  {
    Vector items = new Vector();

    private Canvas canvas;
    private boolean scrollNeeded;

    /**
     * Constructor set up a Canvas to do the drawing.
    **/
    public PrinterList()
    {
      /**
       * This inner class will do the drawing.
      **/
      canvas = new Canvas()
      {
        private Dimension preferredSize = new Dimension(0,0);

        /**
         * We re-size ourself in the paint() method when we discover how large
         * we actually need to be in order to paint the amount of data we've got.
         * @return the full size to display all the data
        **/
        public Dimension getPreferredSize()
        {
          return preferredSize;
        }

        /**
         * Paint all the data.
         * @param g the Graphics
        **/
        public void paint(Graphics g)
        {
          fixForEuro(g);

          Dimension size = getSize();
          int pageHeight = 0;

          FontMetrics fm = g.getFontMetrics();
          int lineHeight = fm.getHeight();

          synchronized (PrinterList.this)
          {
            int lineCount = items.size();

            if (lineCount >= 1)
            {
              pageHeight = lineCount*lineHeight;

              int y = size.height - pageHeight;
              for (int i=0; i<lineCount; i++)
              {
                g.drawString((String)items.elementAt(i),0,y);
                y += lineHeight;
              }
            }
          }

          if (pageHeight > preferredSize.height)
          {
            // resize to fit
            preferredSize = new Dimension(size.width,pageHeight);

            // rearrange the enclosing ScrollPane
            PrinterList.this.doLayout();
            scrollNeeded = true;
          }

          if (scrollNeeded)
          {
            // doing the scrolling here minimizes the number of times
            // it gets done, as the paint() method is only called
            // once if several lines are added in succession
            setScrollPosition(0,preferredSize.height);
            scrollNeeded = false;
          }
        }
      };

      add(canvas);
    }

    /**
     * Method ported from the original List class.
     * @return the number of items in the list
    **/
    public int getItemCount()
    {
      return items.size();
    }

    /**
     * Method ported from the original List class.
     * @param i an index in the list
     * @return the i'th item in the list
    **/
    public String getItem(int i)
    {
      return (String)items.elementAt(i);
    }

    /**
     * Method ported from the original List class.
     * @param s an item to add to the list
     * @param i an index in the list
    **/
    public synchronized void  addItem(String s,int i)
    {
      if (i>=0 && i<items.size())
        items.insertElementAt(s,i);
      else
        items.addElement(s);

      scrollNeeded = true;
      canvas.repaint(1000);
    }

    /**
     * Method ported from the original List class.
     * @param s an item to add to the end of the list
    **/
    public void addItem(String s)
    {
      addItem(s,-1);
    }

    /**
     * Method ported from the original List class.
     * @param i index of item to remove from the list
    **/
    public synchronized void remove(int i)
    {
      items.removeElementAt(i);

      scrollNeeded = true;
      canvas.repaint(1000);
    }

    /**
     * Method ported from the original List class.
     * @param i an index in the list
    **/
    public void select(int i)
    {
      // in the original implementation this was needed to force the
      // list to scroll to the bottom after it had been modified; the
      // re-written code does this automatically, so we don't need
      // to implement this method
    }

    public String getLine1()
    {
      return getItemN(1);
    }

    public String getLine2()
    {
      return getItemN(2);
    }

    public String getLine3()
    {
      return getItemN(3);
    }

    public String getLine4()
    {
      return getItemN(4);
    }

    public String getLine5()
    {
      return getItemN(5);
    }

    public String getLine6()
    {
      return getItemN(6);
    }

    public String getLine7()
    {
      return getItemN(7);
    }

    public String getLine8()
    {
      return getItemN(8);
    }

    public String getLine9()
    {
      return getItemN(9);
    }

    public String getLine10()
    {
      return getItemN(10);
    }

    public String getLine11()
    {
      return getItemN(11);
    }

    public String getLine12()
    {
      return getItemN(12);
    }

    public String getLine13()
    {
      return getItemN(13);
    }

    public String getLine14()
    {
      return getItemN(14);
    }

    public String getLine15()
    {
      return getItemN(15);
    }

    public String getLine16()
    {
      return getItemN(16);
    }

    public String getLine17()
    {
      return getItemN(17);
    }

    public String getLine18()
    {
      return getItemN(18);
    }

    public String getLine19()
    {
      return getItemN(19);
    }

    public String getLine20()
    {
      return getItemN(20);
    }

    private String getItemN(int index)
    {
      int i = getItemCount();
      return (i<index) ? null : getItem(i-index);
    }
  }
}

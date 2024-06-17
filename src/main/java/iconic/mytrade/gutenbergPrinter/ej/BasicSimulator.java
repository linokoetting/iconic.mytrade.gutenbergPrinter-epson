package iconic.mytrade.gutenbergPrinter.ej;

import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;

import jpos.JposException;
import jpos.config.JposEntry;
import jpos.loader.JposServiceInstance;
import jpos.loader.JposServiceInstanceFactory;
import jpos.services.EventCallbacks;

public abstract class BasicSimulator implements JposServiceInstanceFactory,JposServiceInstance
{
  private static int counter;

  /**
   * Factory for service object instances.
  **/
  public JposServiceInstance createInstance(String logicalName,JposEntry matchEntry)
  {
    return this;
  }

  /**
   * Needed for the JposServiceInstance interface.
  **/
  public void deleteInstance()
  {
  }

  /**
   * Not yet implemented.
   * @exception JposException see the JavaPOS documentation
  **/
  public void checkHealth(int level) throws JposException
  {
  }

  /**
   * Not yet implemented.
   * @exception JposException see the JavaPOS documentation
  **/
  public void claim(int timeout) throws JposException
  {
  }

  /**
   * Not yet implemented.
   * @exception JposException see the JavaPOS documentation
  **/
  public void clearInput() throws JposException
  {
  }

  /**
   * Not yet implemented.
   * @exception JposException see the JavaPOS documentation
  **/
  public void clearOutput() throws JposException
  {
  }

  /**
   * See the OPOS documentation.
   * This will be over-ridden by subclasses in order to do some real work.
  **/
  public void close()
  {
  }

  /**
   * Not yet implemented.
   * @exception JposException see the JavaPOS documentation
  **/
  public void directIO(int command,int[] pData,Object pString) throws JposException
  {
  }

  /**
   * See the OPOS documentation.
   * This will be over-ridden by subclasses in order to do some real work.
   * @exception JposException see the JavaPOS documentation
  **/
  public void open(String logicalName,EventCallbacks cb) throws JposException
  {
  }

  /**
   * Not yet implemented.
   * @exception JposException see the JavaPOS documentation
  **/
  public void release() throws JposException
  {
  }

  /**
   * Not yet implemented.
  **/
  public boolean getAutoDisable()
  {
    return false;
  }

  /**
   * Not yet implemented.
  **/
  public void setAutoDisable(boolean autoDisable)
  {
  }

  /**
   * Not yet implemented.
  **/
  public int getBinaryConversion()
  {
    return 0;
  }

  /**
   * Not yet implemented.
  **/
  public void setBinaryConversion(int binaryConversion)
  {
  }

  /**
   * Not yet implemented.
  **/
  public String getCheckHealthText()
  {
    return "";
  }

  /**
   * Not yet implemented.
  **/
  public boolean getClaimed()
  {
    return false;
  }

  /**
   * Not yet implemented.
  **/
  public int getDataCount()
  {
    return 0;
  }

  /**
   * Not yet implemented.
  **/
  public boolean getDataEventEnabled()
  {
    return false;
  }

  /**
   * Not yet implemented.
  **/
  public void setDataEventEnabled(boolean dataEventEnabled)
  {
  }

  /**
   * See the OPOS documentation.
  **/
  public String getPhysicalDeviceDescription()
  // public String getDeviceDescription()
  {
    return getClass().getName();
  }

  /**
   * Not yet implemented.
  **/
  public boolean getDeviceEnabled()
  {
    return false;
  }

  /**
   * Not yet implemented.
  **/
  public void setDeviceEnabled(boolean deviceEnabled)
  {
  }

  /**
   * See the OPOS documentation.
  **/
  public String getPhysicalDeviceName()
  // public String getDeviceName()
  {
    return "Dummy";
  }

  /**
   * Not yet implemented.
  **/
  public boolean getFreezeEvents()
  {
    return false;
  }

  /**
   * Not yet implemented.
  **/
  public void setFreezeEvents(boolean freezeEvents)
  {
  }

  /**
   * Not yet implemented.
  **/
  public int getOutputID()
  {
    return 0;
  }

  /**
   * Not yet implemented.
  **/
  public int getResultCode()
  {
    return 0;
  }

  /**
   * Not yet implemented.
  **/
  public int getResultCodeExtended()
  {
    return 0;
  }

  /**
   * See the OPOS Documentation.
  **/
  public String getDeviceServiceDescription()
  // public String getServiceObjectDescription()
  {
    return getClass().getName();
  }

  /**
   * Get the value of the deviceServiceVersion property.
   * @return the current property value
  **/
  public int getDeviceServiceVersion()
  {
    return 1006000 + (deviceVersion() % 1000);
  }

  /**
   * Minor version number.
   * @return a version number in the range 000 to 999
  **/
  public int deviceVersion()
  {
    return 000;
  }

  /**
   * Not yet implemented.
  **/
  public int getState()
  {
    return 0;
  }

  /**
   * Not yet implemented.
  **/
  public int     getCapPowerReporting() throws JposException
  {
    return 0;
  }

  /**
   * Not yet implemented.
  **/
  public int getPowerNotify()
  {
    return 0;
  }

  /**
   * Not yet implemented.
  **/
  public void setPowerNotify(int p0)
  {
  }

  /**
   * Not yet implemented.
  **/
  public int getPowerState()
  {
    return 0;
  }

  /**
   * Not yet implemented.
  **/
  public boolean getAsyncMode() throws JposException
  {
    return false;
  }

  /**
   * Not yet implemented.
  **/
  public void setAsyncMode(boolean asyncMode) throws JposException
  {
  }

  /**
   * Not yet implemented.
  **/
  public void    displayText(String data) throws JposException
  {
  }

  /**
   * Set the position of the device simulator on the screen.
   * @param frame the device simulator frame
  **/
  protected void setPosition(Frame frame)
  {
    if (counter == 0)
    {
      // this is a fix to force all messages boxes to be non-modal so that we can
      // switch away from a printer error message in order to simulate fixing the
      // error it describes; we have to communicate with the rest of the system
      // like this because it might have been loaded by a class loader, whereas
      // we must have been loaded by the default loader (or else the JPOS-OPOS
      // bridge would not have worked).
      try
      {
        System.getProperties().put("uk.co.datafit.system.MessageBoxDialog.forceNonModal","true");
      }
      catch (Throwable t)
      {
        // all sorts of things can go wrong when trying to set a system property
      }
    }

    // using a static counter means that the frames cascade down the screen
    counter = (counter % 20) + 1;

    Insets insets = frame.getInsets();
    frame.setLocation(counter*insets.top,counter*insets.top);
    frame.setState(frame.ICONIFIED);
  }

  /**
   * Workaround for bug in JDK 1.3. You should call this from paint() if the
   * Euro currency symbol does not paint correctly. If your paint() method
   * calls setFont(), you should do that first before calling this.
   * @param g the graphics to be fixed
  **/
  protected static void fixForEuro(Graphics g)
  {
    // we draw unicode char 0x80 outside the clip region, where it won't
    // appear; this character is supposed to be be a control character but
    // JDK 1.3 actually draws it as a Euro symbol; furthermore, unicode
    // character 0x20ac (the genuine Euro symbol) does not appear until
    // we have done this
    Rectangle clip = g.getClipBounds();
    g.drawString("\u0080",clip.x,clip.y);
  }

}

/* 
 * Copyright 2019 Jan de Jongh <jfcmdejongh@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.javajdj.jservice.midi.swing;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.time.Instant;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import org.javajdj.jservice.ServiceSupport;
import org.javajdj.jservice.midi.raw.RawMidiService;
import org.javajdj.jservice.midi.raw.RawMidiServiceListenerSupport;
import org.javajdj.jservice.midi.raw.RawMidiService_NetUdpMulticast;
import org.javajdj.jservice.Service;
import org.javajdj.jservice.activity.ActivityMonitor;
import org.javajdj.jservice.activity.DefaultActivityMonitor;
import org.javajdj.jservice.activity.swing.JActivityMonitor;
import org.javajdj.jservice.swing.JServiceControl;
import org.javajdj.swing.SwingUtilsJdJ;

/** A {@link JComponent} that implements a {@link RawMidiService}, either user-supplied
 *  or through selection and booting of such a service among supported types.
 *
 * @see RawMidiServiceType
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JRawMidiService
  extends JPanel
  implements RawMidiService
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JRawMidiService.class.getName ());

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTORS / FACTORIES / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Constructs a {@link RawMidiService} that is also a {@link JComponent}.
   * 
   * <p>
   * If a {@link RawMidiService} is supplied,
   * a {@link JComponent} is created for monitoring and controlling that service.
   * Otherwise, a {@link JComponent} is created allowing the user to select among supported
   * raw MIDI service types ({@link RawMidiServiceType}),
   * and start/stop it.
   * 
   * <p>
   * In both cases, unless overridden by the {@code jServiceControl} argument,
   * a {@link JServiceControl} component is created to control and monitor the service.
   * Note that it can be replaced even after construction through {@link #setJServiceControl}.
   * 
   * @param customRawMidiService The optional user-supplied {@link RawMidiService}.
   * @param jServiceControl      The optional service-control component.
   * 
   * @see #setJServiceControl
   * 
   */
  public JRawMidiService (final RawMidiService customRawMidiService, final JServiceControl jServiceControl)
  {
    super ();
    this.customRawMidiService = customRawMidiService;
    setLayout (new GridLayout (6, 2, 5, 5));
    add (new JLabel ("MIDI Enabled"));
    this.jMidiEnabledPanel = new JPanel ();
    this.jMidiEnabledPanel.setLayout (new GridLayout (1, 1));
    add (this.jMidiEnabledPanel);
    if (this.customRawMidiService == null)
      // XXX NEED TO BE ABLE TO ACCEPT A CUSTOM COLOR FUNCTION XXX TODO
      this.jMidiEnabled = (jServiceControl != null ? jServiceControl
                                                   : new JServiceControl (this, JRawMidiService.DEFAULT_STATUS_COLOR_FUNCTION));
    else
      this.jMidiEnabled = (jServiceControl != null ? jServiceControl
                                                   : new JServiceControl (this.customRawMidiService,
                                                                          JRawMidiService.DEFAULT_STATUS_COLOR_FUNCTION));
    this.jMidiEnabledPanel.add (this.jMidiEnabled);
    if (this.customRawMidiService == null)
    {
      add (new JLabel ("Service Type"));
      this.jRawMidiServiceType = new JComboBox (RawMidiServiceType.values ());
      this.jRawMidiServiceType.setSelectedItem (this.rawMidiServiceType);
      this.jRawMidiServiceType.addActionListener (new JRawMidiServiceTypeComboBoxListener ());
      add (this.jRawMidiServiceType);
      add (new JLabel ("Host/Group"));
      this.jHostOrGroup = new JTextField (this.hostOrGroup);
      // XXX Add listener!
      this.jHostOrGroup.setEditable (false);
      add (this.jHostOrGroup);
      add (new JLabel ("Port"));
      this.jPort = new JTextField (Integer.toString (this.port));
      // XXX Add listener!
      this.jPort.setEditable (false);
      add (this.jPort);
    }
    else
    {
      this.jRawMidiServiceType = null;
      this.jHostOrGroup = null;
      this.jPort = null;
      add (new JLabel ());
      add (new JLabel ());
      add (new JLabel ());
      add (new JLabel ());
      add (new JLabel ());
      add (new JLabel ());      
    }
    add (new JLabel ("Transmit"));
    this.jRawMidiTx = new JActivityMonitor (null, RawMidiService.ACTIVITY_TX_NAME);
    add (this.jRawMidiTx);
    add (new JLabel ("Receive"));
    this.jRawMidiRx = new JActivityMonitor (null, RawMidiService.ACTIVITY_RX_NAME);
    add (this.jRawMidiRx);    
  }
  
  /** Constructs a {@link RawMidiService} that is also a {@link JComponent}.
   * 
   * <p>
   * If a {@link RawMidiService} is supplied,
   * a {@link JComponent} is created for monitoring and controlling that service.
   * Otherwise, a {@link JComponent} is created allowing the user to select among supported
   * raw MIDI service types ({@link RawMidiServiceType}),
   * and start/stop it.
   * 
   * @param customRawMidiService The user-supplied {@link RawMidiService}, non-{@code null}.
   * 
   * @throws IllegalArgumentException If {@code customRawMidiService == null}.
   * 
   */
  public JRawMidiService (final RawMidiService customRawMidiService)
  {
    this (customRawMidiService, null);
    if (customRawMidiService == null)
      throw new IllegalArgumentException ();
  }

  /** Constructs a {@link JComponent} that implements a user-configurable {@link JRawMidiService}.
   * 
   * <p>
   * A {@link JComponent} is created allowing the user to select among supported
   * raw MIDI service types ({@link RawMidiServiceType}),
   * and start/stop it.
   * 
   * <p>
   * The user-supplied {@link JServiceControl} is used to control and monitor the service.
   * 
   * @param jServiceControl The service-control component, non-{@code null}.
   * 
   * @throws IllegalArgumentException If {@code jServiceControl == null}.
   * 
   * @see #setJServiceControl
   * 
   */
  public JRawMidiService (final JServiceControl jServiceControl)
  {
    this (null, jServiceControl);
    if (jServiceControl == null)
      throw new IllegalArgumentException ();
  }
  
  /** Constructs a {@link JComponent} that implements a user-configurable {@link JRawMidiService}.
   * 
   * <p>
   * A {@link JComponent} is created allowing the user to select among supported
   * raw MIDI service types ({@link RawMidiServiceType}),
   * and start/stop it.
   * 
   */
  public JRawMidiService ()
  {
    this (null, null);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // DEFAULT STATUS COLOR FUNCTION
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** The default color function for the service status of {@link JRawMidiService}.
   * 
   * <p>
   * The default coloring is none for {@link Service.Status#STOPPED},
   * {@link Color#red} for {@link Service.Status#ACTIVE},
   * and {@link Color#orange} for {@link Service.Status#ERROR}.
   * 
   */
  public final static Function<Service.Status, Color> DEFAULT_STATUS_COLOR_FUNCTION = (final Service.Status t) ->
  {
    if (t == null)
      return null;
    switch (t)
    {
      case STOPPED:
        return null;
      case ACTIVE:
        return java.awt.Color.red;
      case ERROR:
        return java.awt.Color.orange;
      default:
        throw new RuntimeException ();
    }
  };

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // toString
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public String toString ()
  {
    // XXX Why not set the name property?
    return "JRawMidiService";
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SERVICE SUPPORT
  // SETTINGS LISTENERS
  // STATUS LISTENERS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final ServiceSupport serviceSupport = new ServiceSupport ();
  
  @Override
  public final synchronized void addSettingsListener (final PropertyChangeListener l)
  {
    this.serviceSupport.addSettingsListener (l);
  }
  
  @Override
  public final synchronized void removeSettingsListener (final PropertyChangeListener l)
  {
    this.serviceSupport.removeSettingsListener (l);
  }
  
  @Override
  public final void addStatusListener (final StatusListener l)
  {
    this.serviceSupport.addStatusListener (l);
  }

  @Override
  public final void removeStatusListener (final StatusListener l)
  {
    this.serviceSupport.removeStatusListener (l);
  }
  
  @Override
  public final void removeStatusListeners ()
  {
    this.serviceSupport.removeStatusListeners ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // RAW MIDI SERVICE LISTENERS SUPPORT
  // RAW MIDI SERVICE LISTENERS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final RawMidiServiceListenerSupport rawMidiServiceListenerSupport = new RawMidiServiceListenerSupport ();

  @Override
  public void addRawMidiServiceListener (org.javajdj.jservice.midi.raw.RawMidiServiceListener l)
  {
    this.rawMidiServiceListenerSupport.addRawMidiServiceListener (l);
  }

  @Override
  public void removeRawMidiServiceListener (org.javajdj.jservice.midi.raw.RawMidiServiceListener l)
  {
    this.rawMidiServiceListenerSupport.removeRawMidiServiceListener (l);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // RAW MIDI SERVICE TYPE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private RawMidiServiceType rawMidiServiceType = RawMidiServiceType.MIDI_NET_UDP;

  private final JComboBox jRawMidiServiceType;
  
  private class JRawMidiServiceTypeComboBoxListener
    implements ActionListener
  {
    @Override
    public void actionPerformed (final ActionEvent ae)
    {
      if (ae != null)
      {
        final RawMidiServiceType selectedRawMidiServiceType =
          (RawMidiServiceType) JRawMidiService.this.jRawMidiServiceType.getSelectedItem ();
        JRawMidiService.this.setRawMidiServiceType (selectedRawMidiServiceType);
      }
    }
  }

  private synchronized void setRawMidiServiceType (final RawMidiServiceType rawMidiServiceType)
  {
    if (this.rawMidiServiceType != rawMidiServiceType)
    {
      if (getStatus () != Status.STOPPED)
      {
        stopService ();
        this.rawMidiServiceType = rawMidiServiceType;
        startService ();
      }
      else
        this.rawMidiServiceType = rawMidiServiceType;
    }
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CUSTOM RAW MIDI SERVICE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /** The user-supplied custom raw MIDI service.
   * 
   * <p>
   * This field is set upon construction and cannot be changed afterwards.
   * However, its being {@code null} or not has a huge impact on the appearance
   * of the {@link JComponent}, and the functionality of the object.
   * 
   */
  private final RawMidiService customRawMidiService;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SERVICE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /** The {@link RawMidiService} that is actually running if this service is active.
   * 
   */
  private volatile RawMidiService rawMidiService = null;
  
  /** The {@link ActivityMonitor} created to monitor the {@link RawMidiService}.
   * 
   */
  private volatile ActivityMonitor rawMidiActivityMonitor = null;
  
  @Override
  public final synchronized void startService ()
  {
    if (getStatus () == Status.ACTIVE)
      return;
    stopService ();
    LOG.log (Level.INFO, "Starting JRawMidiService {0}.", this);
    if (this.customRawMidiService == null)
    {
      final String hostOrGroup = getHostOrGroup ();
      final int port = getPort ();
      // rawMidiServiceType is non-null when customRawMidiService == null.
      this.rawMidiService = this.rawMidiServiceType.serviceFactory (hostOrGroup, port);
      this.rawMidiService.addRawMidiServiceListener (this.rawMidiServiceListener);
      this.rawMidiService.startService ();
    }
    else
    {
      this.rawMidiService = this.customRawMidiService;
      this.rawMidiService.addRawMidiServiceListener (this.rawMidiServiceListener);
      this.rawMidiService.startService ();
    }
    this.rawMidiActivityMonitor = new DefaultActivityMonitor (this.rawMidiService);
    this.rawMidiActivityMonitor.setActivityCheckInterval_ms (this.activityCheckInterval_ms);
    this.rawMidiActivityMonitor.setActivityTimeout_ms (this.txRxActivityTimeOut_ms);
    this.jRawMidiTx.setActivityMonitor (this.rawMidiActivityMonitor);
    this.jRawMidiRx.setActivityMonitor (this.rawMidiActivityMonitor);
    this.rawMidiActivityMonitor.startService ();
    // Starting the service may have already caused havoc.    
    if (getStatus () == Status.STOPPED)
      this.serviceSupport.setStatusServiceSupport (Status.ACTIVE);
  }

  @Override
  public final synchronized void stopService ()
  {
    if (getStatus () == Status.STOPPED)
      return;
    LOG.log (Level.INFO, "Stopping JRawMidiService {0}.", this);
    this.jRawMidiTx.setActivityMonitor (null);
    this.jRawMidiRx.setActivityMonitor (null);
    if (this.rawMidiActivityMonitor != null)
    {
      this.rawMidiActivityMonitor.stopService ();
      this.rawMidiActivityMonitor.destroyService ();
      this.rawMidiActivityMonitor = null;
    }
    if (this.rawMidiService != null)
    {
      this.rawMidiService.removeRawMidiServiceListener (this.rawMidiServiceListener);
      this.rawMidiService.stopService ();
      this.rawMidiService = null;
    }
    this.serviceSupport.setStatusServiceSupport (Status.STOPPED);
  }

  @Override
  public final synchronized Status getStatus ()
  {
    return this.serviceSupport.getStatus ();
  }

  @Override
  public final synchronized void toggleService ()
  {
    RawMidiService.super.toggleService ();
  }

  @Override
  public void destroyService () throws UnsupportedOperationException
  {
    RawMidiService.super.destroyService ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // RAW MIDI SERVICE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  @Override
  public final synchronized void sendRawMidiMessage (final byte[] midiMessage)
  {
    if (getStatus () == Status.ACTIVE && this.rawMidiService != null)
      this.rawMidiService.sendRawMidiMessage (midiMessage);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // RAW MIDI SERVICE LISTENER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private volatile RawMidiServiceListener rawMidiServiceListener = new RawMidiServiceListener ();
  
  private class RawMidiServiceListener
    implements org.javajdj.jservice.midi.raw.RawMidiServiceListener
  {

    @Override
    public void rawMidiMessageTx (final byte[] rawMessage)
    {
      if (JRawMidiService.this.getStatus () == Status.ACTIVE)
        JRawMidiService.this.rawMidiServiceListenerSupport.fireRawMidiMessageTx (rawMessage);
    }

    @Override
    public void rawMidiMessageRx (final byte[] rawMessage)
    {
      if (JRawMidiService.this.getStatus () == Status.ACTIVE)
        JRawMidiService.this.rawMidiServiceListenerSupport.fireRawMidiMessageRx (rawMessage);
    }
    
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // MIDI ENABLED
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final JPanel jMidiEnabledPanel;
  
  private volatile JServiceControl jMidiEnabled;
  
  /** Sets the service control component for this raw MIDI service.
   * 
   * <p>
   * Normally, the Raw MIDI Service created by the various constructors contains a {@link JServiceControl}
   * component for monitoring and controlling the service.
   * Some constructors even allow a user-supplied {@link JServiceControl} instead of a privately constructed one.
   * This allows for cases in which the Raw MIDI Service is actually part of another service,
   * and needs to be started/stopped through that service.
   * 
   * <p>
   * The present method allows for setting the {@link JServiceControl} component at any time after construction.
   * Its need arose from cases in which the {@link JServiceControl} component could not be passed (yet)
   * to any of the appropriate constructors.
   * 
   * @param jServiceControl The new service-control component, non-{@code null}.
   * 
   * @throws IllegalArgumentException If {@code jServiceControl == null}.
   * 
   */
  public final void setJServiceControl (final JServiceControl jServiceControl)
  {
    if (jServiceControl == null)
      throw new IllegalArgumentException ();
    SwingUtilsJdJ.invokeOnSwingEDT (() ->
    {
      if (jServiceControl != JRawMidiService.this.jMidiEnabled)
      {
        this.jMidiEnabled = jServiceControl;
        this.jMidiEnabledPanel.removeAll ();
        this.jMidiEnabledPanel.add (this.jMidiEnabled);
        this.jMidiEnabledPanel.validate ();
        this.jMidiEnabledPanel.repaint ();
      }
    });
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // HOST / GROUP
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private volatile String hostOrGroup = RawMidiService_NetUdpMulticast.DEFAULT_GROUP;

  // XXX javadoc...
  public String getHostOrGroup ()
  {
    return hostOrGroup;
  }

  private final JTextField jHostOrGroup;
  
  // XXX private final JTextFieldListener jHostOrGroupListener;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // PORT
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private volatile int port = RawMidiService_NetUdpMulticast.DEFAULT_PORT;
  
  private final JTextField jPort;
  
  // XXX javadoc...
  public int getPort ()
  {
    return port;
  }

  // XXX private final JTextFieldListener jPortListener;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // ACTIVITY MONITORABLE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Returns {@link RawMidiService#RAW_MIDI_SERVICE_MONITORABLE_ACTIVITIES}.
   * 
   * <p>
   * By virtue of the contract of {@link RawMidiService}.
   * 
   * @return {@link RawMidiService#RAW_MIDI_SERVICE_MONITORABLE_ACTIVITIES}.
   * 
   */
  @Override
  public final synchronized Set<String> getMonitorableActivities ()
  {
    return RawMidiService.RAW_MIDI_SERVICE_MONITORABLE_ACTIVITIES;
  }

  @Override
  public final synchronized Instant lastActivity ()
  {
    if (getStatus () == Status.ACTIVE && this.rawMidiService != null)
      return this.rawMidiService.lastActivity ();
    else
      return Instant.MIN;
  }

  @Override
  public final synchronized Instant lastActivity (final String monitorableActivity)
  {
    if (getStatus () == Status.ACTIVE && this.rawMidiService != null)
      return this.rawMidiService.lastActivity (monitorableActivity);
    else
      return Instant.MIN;
  }
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // RAW MIDI TX
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final JActivityMonitor jRawMidiTx;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // RAW MIDI RX
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final JActivityMonitor jRawMidiRx;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // MAIN - FOR TESTING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Creates a {@link JFrame} holding a {@link JRawMidiService} component.
   * 
   * @param args The command-line arguments (ignored).
   * 
   */
  public final static void main (final String[] args)
  {
    SwingUtilities.invokeLater (() ->
    {
      try
      {
        final JFrame frame = new JFrame ();
        frame.setTitle ("JRawMidiService Test Program - (C) Jan de Jongh 2019");
        frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        final JRawMidiService jRawMidiService = new JRawMidiService ();
        frame.setContentPane (jRawMidiService);
        frame.pack ();
        frame.setVisible (true);
        frame.setLocationRelativeTo (null);
      }
      catch (HeadlessException he)
      {
        LOG.log (Level.WARNING, "No GUI; bailing out!");
      }
    });
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // ACTIVITY ASSESSMENT
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private volatile long activityCheckInterval_ms = 50l;

  // XXX javadoc...
  public final synchronized long getActivityCheckInterval_ms ()
  {
    return this.activityCheckInterval_ms;
  }

  // XXX javadoc...
  public final synchronized void setActivityCheckInterval_ms (final long activityCheckInterval_ms)
  {
    this.activityCheckInterval_ms = activityCheckInterval_ms;
    if (this.rawMidiActivityMonitor != null)
    {
      this.rawMidiActivityMonitor.setActivityCheckInterval_ms (this.activityCheckInterval_ms);
    }
  }
  
  private volatile long txRxActivityTimeOut_ms = 200L;
  
  // XXX javadoc...
  public final synchronized long getTxRxActivityTimeOut_ms ()
  {
    return this.txRxActivityTimeOut_ms;
  }

  // XXX javadoc...
  public final synchronized void setTxRxActivityTimeOut_ms (final long txRxActivityTimeOut_ms)
  {
    if (txRxActivityTimeOut_ms <= 0)
      throw new IllegalArgumentException ();
    this.txRxActivityTimeOut_ms = txRxActivityTimeOut_ms;
    if (this.rawMidiActivityMonitor != null)
    {
      this.rawMidiActivityMonitor.setActivityTimeout_ms (this.txRxActivityTimeOut_ms);
    }
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}

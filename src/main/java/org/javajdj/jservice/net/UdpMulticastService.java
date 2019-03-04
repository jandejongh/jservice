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
package org.javajdj.jservice.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.javajdj.jservice.activity.ActivityMonitorable;
import org.javajdj.jservice.AbstractService;
import org.javajdj.jservice.Service;
import org.javajdj.jservice.Service.Status;

/** A {@link Service} for transmission to and reception from a UDP multi-cast address/port.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class UdpMulticastService
  extends AbstractService
  implements Service, ActivityMonitorable
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (UdpMulticastService.class.getName ());
	
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTORS / FACTORIES / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /** Creates a UDP multi-cast {@link Service} with given multi-cast group and port.
   * 
   * @param group The group, non-{@code null}.
   * @param port  The port.
   * 
   * @throws IllegalArgumentException If the group is {@code null} or the port number is negative.
   * 
   */
  public UdpMulticastService (final String group, final int port)
  {
    if (group == null || port < 0)
      throw new IllegalArgumentException ();
    this.group = group;
    this.port = port;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // MESSAGE LISTENERS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** A listener to messages sent from or received at this {@link UdpMulticastService}.
   * 
   * @see #addMessageListener
   * @see #removeMessageListener
   * 
   */
  public interface MessageListener
  {
    
    /** Notification of a transmitted message.
     * 
     * @param message The message (payload) sent.
     * 
     */
    void messageSent (final byte[] message);
    
    /** Notification (and delivery) of a received message.
     * 
     * @param message The message (payload) received.
     * 
     */
    void messageReceived (final byte[] message);
    
  }
  
  private final Set<MessageListener> messageListeners = new LinkedHashSet<> ();
  
  /** Adds a message listener.
   * 
   * <p>
   * The method silently ignores listeners that are already registered.
   * 
   * @param l The message listener, non-{@code null}.
   * 
   * @throws IllegalArgumentException If {@code l == null}.
   * 
   */
  public final synchronized void addMessageListener (final MessageListener l)
  {
    if (l == null)
      throw new IllegalArgumentException ();
    if (! this.messageListeners.contains (l))
      this.messageListeners.add (l);
  }

  /** Removes a message listener.
   * 
   * <p>
   * The method silently ignores listeners that are not registered.
   * 
   * @param l The message listener, non-{@code null}.
   * 
   * @throws IllegalArgumentException If {@code l == null}.
   * 
   */
  public final synchronized void removeMessageListener (final MessageListener l)
  {
    if (l == null)
      throw new IllegalArgumentException ();
    this.messageListeners.remove (l);
  }
  
  /** Notifies message listeners that a message has been sent.
   * 
   * @param message The message.
   * 
   */
  protected final void fireMessageSent (final byte[] message)
  {
    final Set<MessageListener> messageListenersCopy;
    synchronized (this)
    {
      messageListenersCopy = new LinkedHashSet<> (this.messageListeners);
    }
    for (final MessageListener l : messageListenersCopy)
      l.messageSent (message);
  }
  
  /** Notifies message listeners that a message has been received.
   * 
   * @param message The message.
   * 
   */
  protected final void fireMessageReceived (final byte[] message)
  {
    final Set<MessageListener> messageListenersCopy;
    synchronized (this)
    {
      messageListenersCopy = new LinkedHashSet<> (this.messageListeners);
    }
    for (final MessageListener l : messageListenersCopy)
      l.messageReceived (message);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // GROUP
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private volatile String group = null; /* No reasonable default, really! Anyway, set to non-null value in constructor. */
  
  /** Returns the UDP multi-cast group.
   * 
   * @return The UDP multi-cast group.
   * 
   */
  public final synchronized String getGroup ()
  {
    return this.group;
  }
  
  /** Sets the UDP multi-cast group.
   * 
   * <p>
   * If the group has changed, and the service is active,
   * it is restarted automatically.
   * 
   * @param group The UDP multi-cast group.
   * 
   * @throws IllegalArgumentException If {@code group == null}.
   * 
   * @see #restartService
   * 
   */
  public final synchronized void setGroup (final String group)
  {
    if (group == null)
      throw new IllegalArgumentException ("null host");
    if (! this.group.equals (group))
    {
      this.group = group;
      if (getStatus () == Status.ACTIVE)
        restartService ();
    }
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // PORT
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private volatile int port; // Set by constructor; no real reasonable default value.
  
  /** Returns the UDP port.
   * 
   * @return The UDP port.
   * 
   */
  public final synchronized int getPort ()
  {
    return this.port;
  }
  
  /** Sets the UDP port.
   * 
   * <p>
   * If the port has changed, and the service is active,
   * it is restarted automatically.
   * 
   * @param port The UDP port.
   * 
   * @throws IllegalArgumentException If {@code port < 0}.
   * 
   * @see #restartService
   * 
   */
  public final synchronized void setPort (final int port)
  {
    if (port < 0)
      throw new IllegalArgumentException ();
    if (this.port != port)
    {
      this.port = port;
      if (getStatus () == Status.ACTIVE)
        restartService ();
    }
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SERVICE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  // XXX SOMEWHERE WE HAVE TO JOIN THE MULTICAST GROUP???
  
  private volatile /* DatagramSocket */ MulticastSocket udpRxSocket = null;
  private volatile UdpRxThread udpRxThread = null;
  private volatile UdpDeliveryThread udpDeliveryThread = null;
  /** The size of the message buffer for reception.
   * 
   */
  public static final int UDP_RX_QUEUE_SIZE = 16;
  private final LinkedBlockingQueue<byte[]> udpRxQueue = new LinkedBlockingQueue<> (UDP_RX_QUEUE_SIZE);

  // private volatile DatagramSocket udpTxSocket = null;
  private volatile UdpTxThread udpTxThread = null;
  /** The size of the message buffer for transmission.
   * 
   */
  public static final int UDP_TX_QUEUE_SIZE = 16;
  private final LinkedBlockingQueue<byte[]> udpTxQueue = new LinkedBlockingQueue<> (UDP_TX_QUEUE_SIZE);
  
  @Override
  public final synchronized void startService ()
  {
    final Status oldStatus = getStatus ();
    if (oldStatus == Status.ACTIVE)
      return;
    stopService ();
    LOG.log (Level.INFO, "Starting Service Class {0} with Instance {1}.",
      new Object[]{this.getClass ().getSimpleName (), this});
    try
    {
      this.udpRxQueue.clear ();
      // this.udpRxSocket = new DatagramSocket (this.port);
      this.udpRxSocket = new MulticastSocket (this.port);
      this.udpRxSocket.joinGroup (InetAddress.getByName (this.group));
      this.udpRxSocket.setLoopbackMode (true);
      this.udpDeliveryThread = new UdpDeliveryThread ();
      this.udpDeliveryThread.mustRun = true;
      this.udpDeliveryThread.start ();
      this.udpRxThread = new UdpRxThread (this.udpRxSocket);
      this.udpRxThread.mustRun = true;
      this.udpRxThread.start ();
      this.udpTxQueue.clear ();
//      this.udpTxSocket = new DatagramSocket ();
//      this.udpTxSocket.connect (InetAddress.getByName (this.group), this.port);
//      this.udpRxSocket.connect (InetAddress.getByName (this.group), this.port);
      this.udpTxThread = new UdpTxThread (/* this.udpTxSocket */ this.udpRxSocket);
      this.udpTxThread.mustRun = true;
      this.udpTxThread.start ();
    }
    catch (IOException ioe)
    {
      LOG.log (Level.WARNING, "Service Class {0} caught IOException during start of Instance {1}!",
        new Object[]{this.getClass ().getSimpleName (), this});
      error ();
      return;
    }
//    catch (InterruptedException ie)
//    {
//      LOG.log (Level.WARNING, "Service Class {0} interrupted during startService of Instance {1}!",
//        new Object[]{this.getClass ().getSimpleName (), this});
//      stopService ();
//      return;
//    }
    // If we are still in STOPPED state, we can now safely switch to ACTIVE.
    // Otherwise, we are (already) in ERROR state as a result of trying to startService,
    // and we might as well stay there.
    if (getStatus () == Status.STOPPED)
      setStatus (Status.ACTIVE);
  }

  @Override
  public final synchronized void stopService ()
  {
    final Status oldStatus = getStatus ();
    if (oldStatus == Status.STOPPED)
      return;
    LOG.log (Level.INFO, "Stopping Service Class {0} with Instance {1}.",
      new Object[]{this.getClass ().getSimpleName (), this});
    // Note: In the code below, we will make serious attempts
    // to terminate all service threads (actually, destroy them for gc collection).
    // It is becoming a convention that the 'mustRun' field on the Thread object
    // is set to false BEFORE the thread is interrupted or deprived of essential
    // resources (e.g., through closing sockets, files, readers, writers, etc.).
    // That way the Thread can determine the error/excecption it encounters is
    // the result of a stopService () operation on the enclosing object,
    // instead of due to external reasons (host down, out of disk space, etc.).
    // In the latter case, the service Thread is likely to force the
    // enclosing service to enter to ERROR state.
    if (this.udpTxThread != null)
    {
      this.udpTxThread.mustRun = false;
      this.udpTxThread.interrupt ();
      this.udpTxThread = null;
    }
    this.udpTxQueue.clear ();
//    if (this.udpTxSocket != null)
//    {
//      this.udpTxSocket.close ();
//      this.udpTxSocket = null;
//    }
    if (this.udpRxThread != null)
    {
      this.udpRxThread.mustRun = false;
      this.udpRxThread.interrupt ();
      this.udpRxThread = null;
    }
    if (this.udpRxSocket != null)
    {
      // XXX
      // this.udpRxSocket.leaveGroup (/* XXX */);
      this.udpRxSocket.close ();
      this.udpRxSocket = null;
    }
    if (this.udpDeliveryThread != null)
    {
      this.udpDeliveryThread.mustRun = false;
      this.udpDeliveryThread.interrupt ();
      this.udpDeliveryThread = null;
    }
    this.udpRxQueue.clear ();
    setStatus (Status.STOPPED);
  }

  @Override
  protected final synchronized void error ()
  {
    super.error ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // UDP RECEPTION THREAD
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private class UdpRxThread
    extends Thread
  {
    
    private boolean mustRun = false;
    
    private final DatagramSocket udpRxSocket;
    
    private static final int BUFFER_SIZE = 2048;
    
    private UdpRxThread (final DatagramSocket udpRxSocket)
    {
      if (udpRxSocket == null)
        throw new IllegalArgumentException ();
      this.udpRxSocket = udpRxSocket;
    }

    @Override
    public void run ()
    {
      LOG.log (Level.INFO, "Started UDP Rx Thread for Service Class {0} on Instance {1}.",
        new Object[]{this.getClass ().getSimpleName (), this});
      try
      {
        while (this.mustRun)
        {
          final byte[] buffer = new byte[BUFFER_SIZE];
          final DatagramPacket p = new DatagramPacket (buffer, BUFFER_SIZE);
          this.udpRxSocket.receive (p);
//          LOG.log (Level.INFO, "Received UDP datagram for Service Class {0} on Instance {1}: {2}.",
//            new Object[]{this.getClass ().getSimpleName (),
//                         this,
//                         HexUtils.bytesToHex (p.getData (), p.getOffset (), p.getLength ())});
          synchronized (UdpMulticastService.this)
          {
            UdpMulticastService.this.monitorableActivities.put (UdpMulticastService.ACTIVITY_RX_NAME, Instant.now ());
          }
          final byte[] data = p.getData ();
          final int offset = p.getOffset ();
          final int length = p.getLength ();
          byte[] copiedData = data;
          if (offset != 0 || data.length != length)
          {
            copiedData = new byte[length];
            System.arraycopy (data, offset, copiedData, 0, length);
          }
          if (! UdpMulticastService.this.udpRxQueue.offer (copiedData))
            LOG.log (Level.WARNING, "Receive Buffer Overflow for Service Class {0} on Instance {1}!",
              new Object[]{this.getClass ().getSimpleName (), this});
        }
      }
      catch (IOException ioe)
      {
        LOG.log (Level.INFO, "UDP Rx Thread for Service Class {0} on Instance {1} caught IOException.",
          new Object[]{this.getClass ().getSimpleName (), this});
      }
      LOG.log (Level.INFO, "Terminating UDP Rx Thread for Service Class {0} on Instance {1}.",
        new Object[]{this.getClass ().getSimpleName (), this});
    }
    
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // UDP DELIVERY THREAD
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private class UdpDeliveryThread
    extends Thread
  {
    
    private boolean mustRun = false;
    
    @Override
    public void run ()
    {
      LOG.log (Level.INFO, "Started UDP Delivery Thread for Service Class {0} on Instance {1}.",
        new Object[]{this.getClass ().getSimpleName (), this});
      try
      {
        while (this.mustRun)
        {
          final byte[] message = UdpMulticastService.this.udpRxQueue.take ();
          UdpMulticastService.this.fireMessageReceived (message);
        }
      }
      catch (InterruptedException ie)
      {
        // EMPTY
      }
      LOG.log (Level.INFO, "Terminating UDP Delivery Thread for Service Class {0} on Instance {1}.",
        new Object[]{this.getClass ().getSimpleName (), this});
    }
    
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // TRANSMIT
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Schedules a message for transmission.
   * 
   * <p>
   * Message transmission is asynchronous.
   * This method is non-blocking; it merely attempt to put the message into the internal transmit buffer.
   * 
   * @param payload The message, non-{@code null}.
   * 
   * @return False if the service is not currently active, or in case of a transmit-buffer overflow, true otherwise.
   * 
   * @throws IllegalArgumentException If {@code payload == null}.
   * 
   * @see #UDP_TX_QUEUE_SIZE
   * 
   */
  public final synchronized boolean transmit (final byte[] payload)
  {
    if (payload == null)
      throw new IllegalArgumentException ();
    if (getStatus () != Status.ACTIVE)
      return false;
    else
    {
      final boolean insertionSuccess = this.udpTxQueue.offer (payload);
      if (! insertionSuccess)
        LOG.log (Level.WARNING, "Transmit Buffer Overflow for Service Class {0} on Instance {1}!",
          new Object[]{this.getClass ().getSimpleName (), this});
      return insertionSuccess;
    }
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // UDP TRANSMISSION THREAD
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private class UdpTxThread
    extends Thread
  {
    
    private boolean mustRun = false;
    
    private final /* DatagramSocket */ MulticastSocket udpTxSocket;
    
    private UdpTxThread (final /* DatagramSocket */ MulticastSocket udpTxSocket)
    {
      if (udpTxSocket == null)
        throw new IllegalArgumentException ();
      this.udpTxSocket = udpTxSocket;
    }

    @Override
    public void run ()
    {
      LOG.log (Level.INFO, "Started UDP Tx Thread for Service Class {0} on Instance {1}.",
        new Object[]{this.getClass ().getSimpleName (), this});
      try
      {
        while (this.mustRun)
        {
          final byte[] payload = UdpMulticastService.this.udpTxQueue.take ();
          final DatagramPacket p = new DatagramPacket (payload, payload.length, InetAddress.getByName (group), port);
//          LOG.log (Level.INFO, "Transmitting UDP datagram for Service Class {0} on Instance {1}: {2}.",
//            new Object[]{this.getClass ().getSimpleName (),
//                         this,
//                         HexUtils.bytesToHex (p.getData (), p.getOffset (), p.getLength ())});
          this.udpTxSocket.send (p);
          synchronized (UdpMulticastService.this)
          {
            UdpMulticastService.this.monitorableActivities.put (UdpMulticastService.ACTIVITY_TX_NAME, Instant.now ());
          }
        }
      }
      catch (IOException ioe)
      {
        LOG.log (Level.INFO, "UDP Tx Thread for Service Class {0} on Instance {1} caught IOException.",
          new Object[]{this.getClass ().getSimpleName (), this});
      }
      catch (InterruptedException ie)
      {
        // EMPTY
      }
      LOG.log (Level.INFO, "Terminating UDP Tx Thread for Service Class {0} on Instance {1}.",
        new Object[]{this.getClass ().getSimpleName (), this});
    }
    
  }
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // ACTIVITY MONITORABLE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final Map<String, Instant> monitorableActivities = new LinkedHashMap<> ();
  {
    // XXX Why is this set to Instant.now?? Breaks contract??
    this.monitorableActivities.put (null, Instant.now ());
    this.monitorableActivities.put (ACTIVITY_TX_NAME, Instant.MIN);
    this.monitorableActivities.put (ACTIVITY_RX_NAME, Instant.MIN);
  }
    
  /** The name of the transmission activity.
   * 
   * <p>
   * This class maintains the {@link Instant} of the last message transmission.
   * 
   * @see ActivityMonitorable
   * @see #getMonitorableActivities
   * 
   */
  public final static String ACTIVITY_TX_NAME = "Tx";
  
  /** The name of the reception activity.
   * 
   * <p>
   * This class maintains the {@link Instant} of the last message reception.
   * 
   * @see ActivityMonitorable
   * @see #getMonitorableActivities
   * 
   */
  public final static String ACTIVITY_RX_NAME = "Rx";
  
  /** Returns a {@code Set} (not to be modified) holding the {@link #ACTIVITY_TX_NAME} and {@link #ACTIVITY_RX_NAME} strings.
   * 
   * @return A {@code Set} (not to be modified) holding the {@link #ACTIVITY_TX_NAME} and {@link #ACTIVITY_RX_NAME} strings.
   * 
   */
  @Override
  public final synchronized Set<String> getMonitorableActivities ()
  {
    return this.monitorableActivities.keySet ();
  }

  @Override
  public synchronized Instant lastActivity ()
  {
    return this.monitorableActivities.get (null);
  }

  @Override
  public synchronized Instant lastActivity (final String monitorableActivity)
  {
    if (! this.monitorableActivities.containsKey (monitorableActivity))
      return Instant.MIN;
    else
      return this.monitorableActivities.get (monitorableActivity);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}

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
package org.javajdj.jservice.midi.raw;

import java.time.Instant;
import java.util.Set;
import java.util.logging.Logger;
import org.javajdj.jservice.net.UdpMulticastService;
import org.javajdj.jservice.Service;

/** A {@link RawMidiService} implementation using MIDI over UDP multi-cast.
 *
 * @see UdpMulticastService
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class RawMidiService_NetUdpMulticast
  extends AbstractRawMidiService
  implements RawMidiService
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (RawMidiService_NetUdpMulticast.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTORS / FACTORIES / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Creates a MIDI over UDP multi-cast {@link Service} with given name and multi-cast group and port.
   * 
   * @param name  The service name, non-{@code null}.
   * @param group The group, non-{@code null}.
   * @param port  The port.
   * 
   * @throws IllegalArgumentException If the name or group is {@code null} or the port number is negative.
   * 
   */
  public RawMidiService_NetUdpMulticast (final String name, final String group, final int port)
  {
    super (name);
    this.udpMulticastService = new UdpMulticastService (group, port);
    this.udpMulticastService.addMessageListener (new UdpMulticastService.MessageListener ()
    {
      @Override
      public void messageSent (final byte[] message)
      {
        RawMidiService_NetUdpMulticast.this.fireRawMidiMessageTx (message);
      }
      @Override
      public void messageReceived (final byte[] message)
      {
        RawMidiService_NetUdpMulticast.this.fireRawMidiMessageRx (message);
      }
    });
    addTargetService (this.udpMulticastService);
  }

  /** Creates a MIDI over UDP multi-cast {@link Service} with given multi-cast group and port.
   * 
   * <p>
   * The service name is set to {@code "RawMidiService_NetUdpMulticast"}.
   * 
   * @param group The group, non-{@code null}.
   * @param port  The port.
   * 
   * @throws IllegalArgumentException If the group is {@code null} or the port number is negative.
   * 
   */
  public RawMidiService_NetUdpMulticast (final String group, final int port)
  {
    this ("RawMidiService_NetUdpMulticast", group, port);
  }

  /** Creates a MIDI over UDP multi-cast {@link Service} with default multi-cast group and port.
   * 
   * <p>
   * The service name is set to {@code "RawMidiService_NetUdpMulticast"}.
   * 
   * @see #DEFAULT_GROUP
   * @see #DEFAULT_PORT
   * 
   */
  public RawMidiService_NetUdpMulticast ()
  {
    this (RawMidiService_NetUdpMulticast.DEFAULT_GROUP, RawMidiService_NetUdpMulticast.DEFAULT_PORT);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // GROUP
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** The name of the "group" property.
   * 
   */
  public static final String GROUP_PROPERTY_NAME = "group";
  
  /** The default UDP multi-cast group.
   * 
   */
  public static final String DEFAULT_GROUP = "225.0.0.37";
  
  /** Returns the UDP multi-cast group.
   * 
   * @return The UDP multi-cast group.
   * 
   */
  public final synchronized String getGroup ()
  {
    return this.udpMulticastService.getGroup ();
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
   */
  public final synchronized void setGroup (final String group)
  {
    this.udpMulticastService.setGroup (group);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // PORT
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** The name of the "port" property.
   * 
   */
  public static final String PORT_PROPERTY_NAME = "port";
  
  /** The default UDP port.
   * 
   */
  public static final int DEFAULT_PORT = 21928;
  
  /** Returns the UDP port.
   * 
   * @return The UDP port.
   * 
   */
  public final synchronized int getPort ()
  {
    return this.udpMulticastService.getPort ();
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
   */
  public final synchronized void setPort (final int port)
  {
    this.udpMulticastService.setPort (port);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // UDP MULTICAST SERVICE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final UdpMulticastService udpMulticastService;

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // RAW MIDI SERVICE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  @Override
  public void sendRawMidiMessage (final byte[] rawMidiMessage)
  {
    // XXX Sanity check on MIDI Message?
    if (rawMidiMessage != null)
    {
      // LOG.log (Level.INFO, "rawMidiMessage={0}.", HexUtils.bytesToHex (rawMidiMessage));
      this.udpMulticastService.transmit (rawMidiMessage);
    }
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // ACTIVITY MONITORABLE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public final Instant lastActivity (final String monitorableActivity)  
  {
    return this.udpMulticastService.lastActivity (monitorableActivity);
  }

  /** Returns {@link RawMidiService#RAW_MIDI_SERVICE_MONITORABLE_ACTIVITIES}.
   * 
   * <p>
   * By virtue of the contract of {@link RawMidiService}.
   * 
   * @return {@link RawMidiService#RAW_MIDI_SERVICE_MONITORABLE_ACTIVITIES}.
   * 
   */
  @Override
  public final Set<String> getMonitorableActivities ()
  {
    return RawMidiService.RAW_MIDI_SERVICE_MONITORABLE_ACTIVITIES;
  }

  @Override
  public final Instant lastActivity ()
  {
    return this.udpMulticastService.lastActivity ();
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}

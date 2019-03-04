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

import org.javajdj.jservice.midi.raw.RawMidiService;
import org.javajdj.jservice.midi.raw.RawMidiService_NetUdpMulticast;
import org.javajdj.jservice.midi.raw.RawMidiService_None;

/** A raw MIDI service type supported in this library.
 * 
 * <p>
 * Also serves as factory for {@link RawMidiService} objects.
 *
 * @see RawMidiService
 * @see JRawMidiService
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public enum RawMidiServiceType
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTORS / FACTORIES / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Raw MIDI providing no service (transmits nor receives anything).
   * 
   * @see RawMidiService_None
   * 
   */
  MIDI_NONE,
  /** Raw MIDI service using UDP multi-cast.
   * 
   * @see RawMidiService_NetUdpMulticast
   * 
   */
  MIDI_NET_UDP;

  /** Returns a new {@link RawMidiService} of given {@link RawMidiServiceType}.
   * 
   * @param rawMidiServiceType The raw MIDI service type (non-{@code null}.
   * @param host               The host name (or IP multi-cast group)
   *                             of the service (if applicable, may be {@code null} or empty).
   * @param port               The TCP or UDP port of the service (if applicable, set to zero if not).
   * 
   * @return The new {@link RawMidiService}, non-{@code null}.
   * 
   * @throws IllegalArgumentException If {@code rawMidiType} is {@code null}.
   * 
   * @see RawMidiService
   * @see RawMidiService_None
   * @see RawMidiService_NetUdpMulticast
   * 
   */
  public static RawMidiService serviceFactory (final RawMidiServiceType rawMidiServiceType, final String host, final int port)
  {
    if (rawMidiServiceType == null)
      throw new IllegalArgumentException ();
    return rawMidiServiceType.serviceFactory (host, port);
  }
    
  /** Returns a new {@link RawMidiService} of this {@link RawMidiServiceType}.
   * 
   * @param host               The host name (or IP multi-cast group)
   *                             of the service (if applicable, may be {@code null} or empty).
   * @param port               The TCP or UDP port of the service (if applicable, set to zero if not).
   * 
   * @return The new {@link RawMidiService}, non-{@code null}.
   * 
   * @throws IllegalArgumentException If {@code rawMidiType} is {@code null}.
   * 
   * @see RawMidiService
   * @see RawMidiService_None
   * @see RawMidiService_NetUdpMulticast
   * 
   */
  public final RawMidiService serviceFactory (final String host, final int port)
  {
    switch (this)
    {
      case MIDI_NONE:
        return new RawMidiService_None ();
      case MIDI_NET_UDP:
        return new RawMidiService_NetUdpMulticast (host, port);
      default:
        throw new RuntimeException ();
    }
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}

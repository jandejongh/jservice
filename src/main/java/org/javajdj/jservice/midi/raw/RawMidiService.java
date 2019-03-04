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
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.javajdj.jservice.activity.ActivityMonitorable;
import org.javajdj.jservice.net.UdpMulticastService;
import org.javajdj.jservice.Service;

/** A {@link Service} for transmission and reception of MIDI messages.
 *
 * <p>
 * Implementations must support activity monitoring for (at least) the transmission and reception of
 * messages.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public interface RawMidiService
  extends Service, ActivityMonitorable
{
  
  /** Adds a MIDI (raw) message listener.
   * 
   * @param l The listener, ignored if {@code null} or already registered.
   * 
   */
  void addRawMidiServiceListener (RawMidiServiceListener l);
  
  /** Removes a MIDI (raw) message listener.
   * 
   * @param l The listener, ignored if {@code null} or not registered.
   * 
   */
  void removeRawMidiServiceListener (RawMidiServiceListener l);
  
  /** Sends (schedules transmission of) a raw MIDI message.
   * 
   * @param midiMessage The message.
   * 
   * @throws IllegalArgumentException If the MIDI message specified is {@code null} or invalid.
   * 
   */
  void sendRawMidiMessage (byte[] midiMessage);
  
  /** The name of the transmission activity.
   * 
   * <p>
   * This class maintains the {@link Instant} of the last MIDI message transmission.
   * 
   * @see ActivityMonitorable
   * @see #getMonitorableActivities
   * 
   */
  final static String ACTIVITY_TX_NAME = UdpMulticastService.ACTIVITY_TX_NAME;
  
  /** The name of the reception activity.
   * 
   * <p>
   * This class maintains the {@link Instant} of the last message reception.
   * 
   * @see ActivityMonitorable
   * @see #getMonitorableActivities
   * 
   */
  final static String ACTIVITY_RX_NAME = UdpMulticastService.ACTIVITY_RX_NAME;
  
  /** The minimum activities that need to be monitored by a {@link RawMidiService}.
   * 
   * <p>
   * Implementations of {@link RawMidiService#getMonitorableActivities} should return
   * a {@code Set} holding at least the members of {@link #MONITORABLE_ACTIVITIES}.
   * 
   * @see #getMonitorableActivities
   * 
   */
  final static Set<String> MONITORABLE_ACTIVITIES = Collections.unmodifiableSet
    (new LinkedHashSet<> (Arrays.asList (new String[] {RawMidiService.ACTIVITY_TX_NAME, RawMidiService.ACTIVITY_RX_NAME})));
  
}

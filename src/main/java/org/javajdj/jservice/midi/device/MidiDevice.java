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
package org.javajdj.jservice.midi.device;

import java.util.Map;
import java.util.Set;
import org.javajdj.jservice.midi.MidiService;
import org.javajdj.jservice.Service;

/** A MIDI device connected through a MIDI bus (with a {@link MidiService}).
 *
 * <p>
 * The device exposes a {@link Map} interface, mapping device-parameters {@link String}s
 * onto {@link Object}s of key-dependent types.
 * The key-set of the map is fixed and set upon construction of the {@link Object}.
 * Implementation must throw {@link UnsupportedOperationException}s in all destructive operations on the key set.
 * 
 * <p>
 * The map does not allow {@code null} keys.
 * Neither does it support setting a parameter value to {@code null},
 * however,
 * {@code null} <i>is</i> used as a special value indicating the parameter is still unknown
 * (i.e., not yet received from the device) or its value timed out (was not refreshed in time from the device).
 * 
 * <p>
 * A {@link MidiDevice} must notify registered {@link MidiDeviceListener}s of various
 * events, including a change of a parameter value (or of multiple ones).
 * 
 * <p>
 * A {@link MidiService} also is a {@link Service},
 * which is <i>independent</i> from that of the {@link MidiService}.
 * When the service {@link Status} is {@link Status#STOPPED},
 * the device should not sent notifications,
 * time-out all parameters immediately (set to {@code null},
 * and ignore parameter-changing or other requests.
 * 
 * <p>
 * Implementations are encouraged to use {@link Status#ERROR} to indicate
 * lack of connectivity with the device.
 * 
 * @see MidiDeviceListener
 * @see AbstractMidiDevice
 * @see MidiService
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public interface MidiDevice
  extends Service, Map<String, Object>
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // MIDI DEVICE LISTENERS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Registers a {@link MidiDeviceListener}.
   * 
   * @param midiDeviceListener The listener, ignored if {@code null} or already registered.
   * 
   */  
  void addMidiDeviceListener (MidiDeviceListener midiDeviceListener);
  
  /** Unregisters a {@link MidiDeviceListener}.
   * 
   * @param midiDeviceListener The listener, ignored if {@code null} or not registered.
   * 
   */  
  void removeMidiDeviceListener (MidiDeviceListener midiDeviceListener);
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // MIDI SERVICE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Returns the {@link MidiService} (supposedly) connecting the MIDI device.
   * 
   * @return The {@link MidiService} (supposedly) connecting the MIDI device, may be {@code null}.
   * 
   */
  MidiService getMidiService ();
  
  /** Sets the {@link MidiService} (supposedly) connecting the MIDI device.
   * 
   * @param midiService The new {@link MidiService} (supposedly) connecting the MIDI device, may be {@code null}.
   * 
   */
  void setMidiService (MidiService midiService);

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // MIDI CHANNEL
  // MIDI RX OMNI
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Returns the MIDI channel used for transmission of "regular" MIDI messages (to the actual device).
   * 
   * <p>
   * Unless the {@code midiRxOmni} property is {@code true},
   * the MIDI channel is also used for filtering messages from the {@link MidiService}.
   * 
   * <p>
   * MIDI channels are between unity and 16 inclusive.
   * 
   * @return The MIDI channel used for transmission of "regular" MIDI messages to the device
   *           (and for reception filtering is {@code midiRxOmni == false}.
   * 
   * @see #isMidiRxOmni
   * 
   */
  int getMidiChannel ();
  
  /** Sets the MIDI channel used for transmission of "regular" MIDI messages (to the actual device).
   * 
   * <p>
   * Unless the {@code midiRxOmni} property is {@code true},
   * the MIDI channel is also used for filtering messages from the {@link MidiService}.
   * 
   * <p>
   * MIDI channels are between unity and 16 inclusive.
   * 
   * @param midiChannel The new MIDI channel used for transmission of "regular" MIDI messages to the device
   *                      (and for reception filtering is {@code midiRxOmni == false}.
   * 
   * @throws IllegalArgumentException If the channel is out of range; MIDI channels are between unity and 16 inclusive.
   * 
   * @see #isMidiRxOmni
   * 
   */
  void setMidiChannel (int midiChannel);
  
  /** Returns whether the device object listens on all MIDI channels for messages from the actual device.
   * 
   * @return Whether the device object listens on all MIDI channels for messages from the actual device.
   * 
   * @see #getMidiChannel
   * 
   */
  boolean isMidiRxOmni ();
  
  /** Sets whether the device object listens on all MIDI channels for messages from the actual device.
   * 
   * @param midiRxOmni Whether from now on the device object listens on all MIDI channels ({@code true})
   *                     for messages from the device.
   * 
   * @see #setMidiChannel
   * 
   */
  void setMidiRxOmni (boolean midiRxOmni);
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // MIDI DEVICE PARAMETER MAP
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Gets the device parameter key set.
   * 
   * <p>
   * The key set is fixed upon construction of the device,
   * and only depends on its (device's) type.
   * 
   * <p>
   * Implementation are strongly encouraged to return the keys
   * in fixed, deterministic ordering.
   * 
   * @return The device parameter key set.
   * 
   */
  @Override
  Set<String> keySet ();
  
  /** Gets the value of the device parameter with given key.
   * 
   * @param key The key, i.e., the parameter name.
   * 
   * @return The parameter value, null if the parameter is not (yet) known, or no longer known (time out from device).
   * 
   * @throws NullPointerException If the {@code key} is {@code null},
   *                              since {@code null} keys are not allowed.
   * 
   */
  @Override
  Object get (Object key);

  /** Sets the value of device parameter with given key.
   * 
   * @param key   The key, i.e., the parameter name.
   * @param value The new parameter value.
   * 
   * @return The old value of the device parameter, {@code null} if the parameter does not exist.
   * 
   * @throws UnsupportedOperationException If the key is not present, i.e., and attempt is made to modify
   *                                         the key set.
   * @throws NullPointerException          If either or both {@code key} or {@code value} is {@code null}.
   * 
   */
  @Override
  Object put(String key, Object value);
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}

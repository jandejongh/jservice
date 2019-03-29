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
package org.javajdj.jservice.midi;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import org.javajdj.jservice.activity.ActivityMonitorable;
import org.javajdj.jservice.midi.raw.RawMidiService;

/** A extension of a {@link RawMidiService} that features MIDI message interpretation,
 *  including MIDI channel support, SyEx, etc.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public interface MidiService
  extends RawMidiService
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // MIDI SERVICE LISTENERS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Registers a listener for this service.
   * 
   * @param l The listener, ignored if {@code null} or already registered.
   * 
   * @see MidiServiceListener
   * 
   */
  public void addMidiServiceListener (MidiServiceListener l);
  
  /** Removes a listener for this service.
   * 
   * @param l The listener, ignored if {@code null} or not registered.
   * 
   * @see MidiServiceListener
   * 
   */
  public void removeMidiServiceListener (MidiServiceListener l);
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // MIDI SERVICE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Transmits (schedules) a MIDI note-off message.
   * 
   * @param midiChannel The MIDI channel number, between unity and 16 inclusive.
   * @param note        The note, between zero and 127 inclusive.
   * @param velocity    The velocity, between zero and 127 inclusive.
   * 
   * @throws IllegalArgumentException If any of the arguments is out of range.
   * 
   * @see #sendRawMidiMessage
   * 
   */
  void sendMidiNoteOff (int midiChannel, int note, int velocity);
  
  /** Transmits (schedules) a MIDI note-on message.
   * 
   * @param midiChannel The MIDI channel number, between unity and 16 inclusive.
   * @param note        The note, between zero and 127 inclusive.
   * @param velocity    The velocity, between zero and 127 inclusive.
   * 
   * @throws IllegalArgumentException If any of the arguments is out of range.
   * 
   * @see #sendRawMidiMessage
   * 
   */
  void sendMidiNoteOn (int midiChannel, int note, int velocity);
  
  /** Transmits (schedules) a MIDI polyphonic key pressure (after-touch) message.
   * 
   * @param midiChannel The MIDI channel number, between unity and 16 inclusive.
   * @param note        The note, between zero and 127 inclusive.
   * @param pressure    The pressure, between zero and 127 inclusive.
   * 
   * @throws IllegalArgumentException If any of the arguments is out of range.
   * 
   * @see #sendRawMidiMessage
   * 
   */
  void sendMidiPolyphonicKeyPressure (int midiChannel, int note, int pressure);
  
  /** Transmits (schedules) a MIDI control change.
   * 
   * @param midiChannel The MIDI channel number, between unity and 16 inclusive.
   * @param controller  The MIDI controller number, between zero and 127 inclusive.
   * @param value       The new value for the controller, between zero and 127 inclusive.
   * 
   * @throws IllegalArgumentException If any of the arguments is out of range.
   * 
   * @see #sendRawMidiMessage
   * 
   */
  void sendMidiControlChange (int midiChannel, int controller, int value);
  
  /** Transmits (schedules) a MIDI program change.
   * 
   * @param midiChannel The MIDI channel number, between unity and 16 inclusive.
   * @param patch       The patch (program) number, between zero and 127 inclusive.
   * 
   * @throws IllegalArgumentException If any of the arguments is out of range.
   * 
   * @see #sendRawMidiMessage
   * 
   */
  void sendMidiProgramChange (int midiChannel, int patch);
  
  /** Transmits (schedules) a MIDI channel pressure.
   * 
   * @param midiChannel The MIDI channel number, between unity and 16 inclusive.
   * @param pressure    The pressure, between zero and 127 inclusive.
   * 
   * @throws IllegalArgumentException If any of the arguments is out of range.
   * 
   * @see #sendRawMidiMessage
   * 
   */
  void sendMidiChannelPressure (int midiChannel, int pressure);
  
  /** Transmits (schedules) a MIDI pitch bend change.
   * 
   * @param midiChannel The MIDI channel number, between unity and 16 inclusive.
   * @param pitchBend   The pitch bend, between -8192 and +8191 inclusive; zero meaning no pitch change.
   * 
   * @throws IllegalArgumentException If any of the arguments is out of range.
   * 
   * @see #sendRawMidiMessage
   * 
   */
  void sendMidiPitchBendChange (int midiChannel, int pitchBend);
  
  /** Transmits (schedules) a MIDI System Exclusive (SysEx) message.
   * 
   * @param vendorId       The vendor ID.
   * @param rawMidiMessage The complete raw MIDI message, non-{@code null}.
   * 
   * @throws IllegalArgumentException If {@code vendorId} is out of range,
   *                                  or if {@code rawMidiMessage} is {@code null}
   *                                     or an illegally formatted SysEx message
   *                                     (for the particular vendor ID).
   * 
   */
  void sendMidiSysEx (byte vendorId, byte[] rawMidiMessage);
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // ACTIVITY MONITORABLE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** The name of the activity related to MIDI reception errors (incorrectly formatted messages received).
   * 
   * @see ActivityMonitorable
   * @see #getMonitorableActivities
   * 
   */
  final static String ACTIVITY_RX_ERROR_NAME = "RxErr";
  
  /** The name of the SysEx activity.
   * 
   * <p>
   * This activity refers to transmission and reception of MIDI System Exclusive messages.
   * 
   * @see ActivityMonitorable
   * @see #getMonitorableActivities
   * 
   */
  final static String ACTIVITY_SYSEX_NAME = "SysEx";
  
  /** The minimum activities that need to be monitored by a {@link MidiService}.
   * 
   * <p>
   * Implementations of {@link MidiService#getMonitorableActivities} should return
   * a {@code Set} holding at least the members of {@link #MIDI_SERVICE_MONITORABLE_ACTIVITIES}.
   * 
   * <p>
   * Note that the {@code Set} also contains the members of {@link RawMidiService#RAW_MIDI_SERVICE_MONITORABLE_ACTIVITIES}.
   * 
   * @see #getMonitorableActivities
   * @see RawMidiService#RAW_MIDI_SERVICE_MONITORABLE_ACTIVITIES
   * @see #ACTIVITY_RX_NAME
   * @see #ACTIVITY_SYSEX_NAME
   * 
   */
  final static Set<String> MIDI_SERVICE_MONITORABLE_ACTIVITIES
    = MonitorableActivitiesInitializer.create (RAW_MIDI_SERVICE_MONITORABLE_ACTIVITIES,
                                               ACTIVITY_RX_ERROR_NAME,
                                               ACTIVITY_SYSEX_NAME);
  
  /** For internal use (initialization of {@link #MIDI_SERVICE_MONITORABLE_ACTIVITIES}.
   * 
   */
  class MonitorableActivitiesInitializer
  {
    
    static Set<String> create (final Set<String> startSet, final String ... stringsToAdd)
    {
      final Set<String> stringSet = new LinkedHashSet<> (startSet);
      stringSet.addAll (Arrays.asList (stringsToAdd));
      return stringSet;
    }
    
  };
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}

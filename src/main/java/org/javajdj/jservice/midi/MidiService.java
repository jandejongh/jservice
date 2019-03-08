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
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
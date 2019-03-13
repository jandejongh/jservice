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

/** A listener to a {@link MidiService}.
 *
 * @see MidiService#addMidiServiceListener
 * @see MidiService#removeMidiServiceListener
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public interface MidiServiceListener
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // NOTE OFF
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Notification of the transmission of a MIDI Note Off message.
   * 
   * @param midiChannel The MIDI channel number, between unity and 16 inclusive.
   * @param note        The note, between zero and 127 inclusive.
   * @param velocity    The velocity, between zero and 127 inclusive.
   * 
   */
  void midiTxNoteOff (int midiChannel, int note, int velocity);
  
  /** Notification of the reception of a MIDI Note Off message.
   * 
   * @param midiChannel The MIDI channel number, between unity and 16 inclusive.
   * @param note        The note, between zero and 127 inclusive.
   * @param velocity    The velocity, between zero and 127 inclusive.
   * 
   */
  void midiRxNoteOff (int midiChannel, int note, int velocity);
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // NOTE ON
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Notification of the transmission of a MIDI Note On message.
   * 
   * @param midiChannel The MIDI channel number, between unity and 16 inclusive.
   * @param note        The note, between zero and 127 inclusive.
   * @param velocity    The velocity, between zero and 127 inclusive.
   * 
   */
  void midiTxNoteOn (int midiChannel, int note, int velocity);
  
  /** Notification of the reception of a MIDI Note On message.
   * 
   * @param midiChannel The MIDI channel number, between unity and 16 inclusive.
   * @param note        The note, between zero and 127 inclusive.
   * @param velocity    The velocity, between zero and 127 inclusive.
   * 
   */
  void midiRxNoteOn (int midiChannel, int note, int velocity);
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // PROGRAM (PATCH) CHANGE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Notification of the transmission of a MIDI program (patch) change.
   * 
   * @param midiChannel The MIDI channel number, between unity and 16 inclusive.
   * @param patch       The patch (program) number, between zero and 127 inclusive.
   * 
   */
  void midiTxProgramChange (int midiChannel, int patch);
  
  /** Notification of the reception of a MIDI program (patch) change.
   * 
   * @param midiChannel The MIDI channel number, between unity and 16 inclusive.
   * @param patch       The patch (program) number, between zero and 127 inclusive.
   * 
   */
  void midiRxProgramChange (int midiChannel, int patch);
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONTROL CHANGE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Notification of the transmission of a MIDI control change.
   * 
   * @param midiChannel The MIDI channel number, between unity and 16 inclusive.
   * @param controller  The MIDI controller number, between zero and 127 inclusive.
   * @param value       The value for the controller, between zero and 127 inclusive.
   * 
   */
  void midiTxControlChange (int midiChannel, int controller, int value);
  
  /** Notification of the reception of a MIDI control change.
   * 
   * @param midiChannel The MIDI channel number, between unity and 16 inclusive.
   * @param controller  The MIDI controller number, between zero and 127 inclusive.
   * @param value       The value for the controller, between zero and 127 inclusive.
   * 
   */
  void midiRxControlChange (int midiChannel, int controller, int value);
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SYSTEM EXCLUSIVE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Notification of the transmission of a MIDI System Exclusive (SysEx) message.
   * 
   * @param vendorId       The vendor ID.
   * @param rawMidiMessage The complete raw MIDI message, non-{@code null}.
   * 
   */
  void midiTxSysEx (byte vendorId, byte[] rawMidiMessage);
  
  /** Notification of the reception of a MIDI System Exclusive (SysEx) message.
   * 
   * @param vendorId       The vendor ID.
   * @param rawMidiMessage The complete raw MIDI message, non-{@code null}.
   * 
   */
  void midiRxSysEx (byte vendorId, byte[] rawMidiMessage);
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}

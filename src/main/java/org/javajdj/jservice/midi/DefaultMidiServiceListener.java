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

/** A default {@link MidiServiceListener}.
 *
 * @see MidiService
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class DefaultMidiServiceListener
  implements MidiServiceListener
{
  
  /** Notification of the transmission of a MIDI program (patch) change.
   * 
   * <p>
   * This implementation does nothing.
   * 
   * @param midiChannel The MIDI channel number, between unity and 16 inclusive.
   * @param patch       The patch (program) number, between zero and 127 inclusive.
   * 
   */
  @Override
  public void midiTxProgramChange (final int midiChannel, final int patch)
  {
  }
  
  /** Notification of the reception of a MIDI program (patch) change.
   * 
   * <p>
   * This implementation does nothing.
   * 
   * @param midiChannel The MIDI channel number, between unity and 16 inclusive.
   * @param patch       The patch (program) number, between zero and 127 inclusive.
   * 
   */
  @Override
  public void midiRxProgramChange (final int midiChannel, final int patch)
  {
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONTROL CHANGE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Notification of the transmission of a MIDI control change.
   * 
   * <p>
   * This implementation does nothing.
   * 
   * @param midiChannel The MIDI channel number, between unity and 16 inclusive.
   * @param controller  The MIDI controller number, between zero and 127 inclusive.
   * @param value       The value for the controller, between zero and 127 inclusive.
   * 
   */
  @Override
  public void midiTxControlChange (final int midiChannel, final int controller, final int value)
  {
  }
  
  /** Notification of the reception of a MIDI control change.
   * 
   * <p>
   * This implementation does nothing.
   * 
   * @param midiChannel The MIDI channel number, between unity and 16 inclusive.
   * @param controller  The MIDI controller number, between zero and 127 inclusive.
   * @param value       The value for the controller, between zero and 127 inclusive.
   * 
   */
  @Override
  public void midiRxControlChange (final int midiChannel, final int controller, final int value)
  {
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SYSTEM EXCLUSIVE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Notification of the transmission of a MIDI System Exclusive (SysEx) message.
   * 
   * <p>
   * This implementation does nothing.
   * 
   * @param vendorId       The vendor ID.
   * @param rawMidiMessage The complete raw MIDI message, non-{@code null}.
   * 
   */
  @Override
  public void midiTxSysEx (final byte vendorId, final byte[] rawMidiMessage)
  {
  }
  
  /** Notification of the reception of a MIDI System Exclusive (SysEx) message.
   * 
   * <p>
   * This implementation does nothing.
   * 
   * @param vendorId       The vendor ID.
   * @param rawMidiMessage The complete raw MIDI message, non-{@code null}.
   * 
   */
  @Override
  public void midiRxSysEx (final byte vendorId, final byte[] rawMidiMessage)
  {
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}

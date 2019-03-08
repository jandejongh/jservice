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

/** MIDI utilities, mainly for message formatting and dissection.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class MidiUtils
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // MIDI MESSAGE FORMATTING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /** Creates a MIDI Program (Patch) Change message.
   * 
   * @param midiChannel The MIDI channel number, between unity and 16 inclusive.
   * @param patch       The patch (program) number, between zero and 127 inclusive.
   * 
   * @return The newly created raw MIDI message.
   * 
   * @throws IllegalArgumentException If any of the arguments is out of range.
   * 
   */
  public final static byte[] createMidiProgramChangeMessage (final int midiChannel, final int patch)
  {
    if (midiChannel < 1 || midiChannel > 16 || patch < 0 || patch > 127)
      throw new IllegalArgumentException ();
    final byte[] midiMessage = new byte[2];
    midiMessage[0] = (byte) (0xC0 + (midiChannel - 1));
    midiMessage[1] = (byte) patch;
    return midiMessage;
  }
  
  /** Creates a MIDI Control Change message.
   * 
   * @param midiChannel The MIDI channel number, between unity and 16 inclusive.
   * @param controller  The MIDI controller number, between zero and 127 inclusive.
   * @param value       The new value for the controller, between zero and 127 inclusive.
   * 
   * @return The newly created raw MIDI message.
   * 
   * @throws IllegalArgumentException If any of the arguments is out of range.
   * 
   */
  public final static byte[] createMidiControlChangeMessage (final int midiChannel, final int controller, final int value)
  {
    if (midiChannel < 1 || midiChannel > 16
      || controller < 0 || controller > 127
      || value < 0 || value > 127)
      throw new IllegalArgumentException ();
    final byte[] midiMessage = new byte[3];
    midiMessage[0] = (byte) (0xB0 + (midiChannel - 1));
    midiMessage[1] = (byte) controller;
    midiMessage[2] = (byte) value;
    return midiMessage;
  }
  
  /** Creates a MIDI System Exclusive (SysEx) message.
   * 
   * @param vendorId       The vendor ID, between 0 and 127 inclusive.
   * @param rawMidiMessage The complete raw MIDI message, non-{@code null}.
   * 
   * @return The newly created raw MIDI message.
   * 
   * @throws IllegalArgumentException If {@code vendorId} is out of range,
   *                                  or if {@code rawMidiMessage} is {@code null}
   *                                     or an illegally formatted SysEx message
   *                                     (for the particular vendor ID).
   * 
   */
  public final static byte[] createMidiSysEx (final byte vendorId, final byte[] rawMidiMessage)
  {
    if (vendorId < 0)
      throw new IllegalArgumentException ();
    if (rawMidiMessage == null || rawMidiMessage.length < 2)
      throw new IllegalArgumentException ();
    // XXX NEEDS FURTHER CHECKING!!
    final byte[] midiMessage = new byte[rawMidiMessage.length];
    System.arraycopy (rawMidiMessage, 0, midiMessage, 0, midiMessage.length);
    return midiMessage;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}

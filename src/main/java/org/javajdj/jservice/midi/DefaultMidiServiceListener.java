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
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // NOTE OFF
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /** Notification of the transmission of a MIDI Note Off message.
   * 
   * <p>
   * This implementation does nothing.
   * 
   * @param midiChannel The MIDI channel number, between unity and 16 inclusive.
   * @param note        The note, between zero and 127 inclusive.
   * @param velocity    The velocity, between zero and 127 inclusive.
   * 
   */
  @Override
  public void midiTxNoteOff (final int midiChannel, final int note, final int velocity)
  {
  }

  /** Notification of the reception of a MIDI Note Off message.
   * 
   * <p>
   * This implementation does nothing.
   * 
   * @param midiChannel The MIDI channel number, between unity and 16 inclusive.
   * @param note        The note, between zero and 127 inclusive.
   * @param velocity    The velocity, between zero and 127 inclusive.
   * 
   */
  @Override
  public void midiRxNoteOff (final int midiChannel, final int note, final int velocity)
  {
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // NOTE ON
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /** Notification of the transmission of a MIDI Note On message.
   * 
   * <p>
   * This implementation does nothing.
   * 
   * @param midiChannel The MIDI channel number, between unity and 16 inclusive.
   * @param note        The note, between zero and 127 inclusive.
   * @param velocity    The velocity, between zero and 127 inclusive.
   * 
   */
  @Override
  public void midiTxNoteOn (final int midiChannel, final int note, final int velocity)
  {
  }

  /** Notification of the reception of a MIDI Note On message.
   * 
   * <p>
   * This implementation does nothing.
   * 
   * @param midiChannel The MIDI channel number, between unity and 16 inclusive.
   * @param note        The note, between zero and 127 inclusive.
   * @param velocity    The velocity, between zero and 127 inclusive.
   * 
   */
  @Override  
  public void midiRxNoteOn (final int midiChannel, final int note, final int velocity)
  {
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // POLYPHONIC KEY PRESSURE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /** Notification of the transmission of a MIDI Polyphonic Key Pressure message.
   * 
   * <p>
   * This implementation does nothing.
   * 
   * @param midiChannel The MIDI channel number, between unity and 16 inclusive.
   * @param note        The note, between zero and 127 inclusive.
   * @param pressure    The pressure, between zero and 127 inclusive.
   * 
   */
  @Override
  public void midiTxPolyphonicKeyPressure (final int midiChannel, final int note, final int pressure)
  {
  }

  /** Notification of the reception of a MIDI Polyphonic Key Pressure message.
   * 
   * <p>
   * This implementation does nothing.
   * 
   * @param midiChannel The MIDI channel number, between unity and 16 inclusive.
   * @param note        The note, between zero and 127 inclusive.
   * @param pressure    The pressure, between zero and 127 inclusive.
   * 
   */
  @Override  
  public void midiRxPolyphonicKeyPressure (final int midiChannel, final int note, final int pressure)
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
  // PROGRAM CHANGE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
  // CHANNEL PRESSURE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /** Notification of the transmission of a MIDI channel pressure.
   * 
   * <p>
   * This implementation does nothing.
   * 
   * @param midiChannel The MIDI channel number, between unity and 16 inclusive.
   * @param pressure    The pressure, between zero and 127 inclusive.
   * 
   */
  @Override
  public void midiTxChannelPressure (final int midiChannel, final int pressure)
  {
  }
  
  /** Notification of the reception of a MIDI channel pressure.
   * 
   * <p>
   * This implementation does nothing.
   * 
   * @param midiChannel The MIDI channel number, between unity and 16 inclusive.
   * @param pressure    The pressure, between zero and 127 inclusive.
   * 
   */
  @Override
  public void midiRxChannelPressure (final int midiChannel, final int pressure)
  {
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // PITCH BEND CHANGE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /** Notification of the transmission of a MIDI pitch bend change.
   * 
   * <p>
   * This implementation does nothing.
   * 
   * @param midiChannel The MIDI channel number, between unity and 16 inclusive.
   * @param pitchBend   The pitch bend, between -8192 and +8191 inclusive; zero meaning no pitch change.
   * 
   */
  @Override
  public void midiTxPitchBendChange (final int midiChannel, final int pitchBend)
  {
  }
  
  /** Notification of the reception of a MIDI pitch bend change.
   * 
   * <p>
   * This implementation does nothing.
   * 
   * @param midiChannel The MIDI channel number, between unity and 16 inclusive.
   * @param pitchBend   The pitch bend, between -8192 and +8191 inclusive; zero meaning no pitch change.
   * 
   */
  @Override
  public void midiRxPitchBendChange (final int midiChannel, final int pitchBend)
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

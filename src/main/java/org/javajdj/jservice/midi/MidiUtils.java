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
  // MIDI MESSAGE FORMATTING [NOTE OFF]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /** Creates a MIDI Note Off message.
   * 
   * @param midiChannel The MIDI channel number, between unity and 16 inclusive.
   * @param note        The note, between zero and 127 inclusive.
   * @param velocity    The velocity, between zero and 127 inclusive.
   * 
   * @return The newly created raw MIDI message.
   * 
   * @throws IllegalArgumentException If any of the arguments is out of range.
   * 
   */
  public final static byte[] createMidiNoteOffMessage (final int midiChannel, final int note, final int velocity)
  {
    if (midiChannel < 1 || midiChannel > 16 || note < 0 || note > 127 || velocity < 0 || velocity > 127)
      throw new IllegalArgumentException ();
    final byte[] midiMessage = new byte[3];
    midiMessage[0] = (byte) (0x80 + (midiChannel - 1));
    midiMessage[1] = (byte) note;
    midiMessage[2] = (byte) velocity;
    return midiMessage;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // MIDI MESSAGE FORMATTING [NOTE ON]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /** Creates a MIDI Note On message.
   * 
   * @param midiChannel The MIDI channel number, between unity and 16 inclusive.
   * @param note        The note, between zero and 127 inclusive.
   * @param velocity    The velocity, between zero and 127 inclusive.
   * 
   * @return The newly created raw MIDI message.
   * 
   * @throws IllegalArgumentException If any of the arguments is out of range.
   * 
   */
  public final static byte[] createMidiNoteOnMessage (final int midiChannel, final int note, final int velocity)
  {
    if (midiChannel < 1 || midiChannel > 16 || note < 0 || note > 127 || velocity < 0 || velocity > 127)
      throw new IllegalArgumentException ();
    final byte[] midiMessage = new byte[3];
    midiMessage[0] = (byte) (0x90 + (midiChannel - 1));
    midiMessage[1] = (byte) note;
    midiMessage[2] = (byte) velocity;
    return midiMessage;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // MIDI MESSAGE FORMATTING [POLYPHONIC KEY PRESSURE]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /** Creates a MIDI Polyphonic Key Pressure message.
   * 
   * @param midiChannel The MIDI channel number, between unity and 16 inclusive.
   * @param note        The note, between zero and 127 inclusive.
   * @param pressure    The pressure, between zero and 127 inclusive.
   * 
   * @return The newly created raw MIDI message.
   * 
   * @throws IllegalArgumentException If any of the arguments is out of range.
   * 
   */
  public final static byte[] createMidiPolyphonicKeyPressureMessage (final int midiChannel, final int note, final int pressure)
  {
    if (midiChannel < 1 || midiChannel > 16 || note < 0 || note > 127 || pressure < 0 || pressure > 127)
      throw new IllegalArgumentException ();
    final byte[] midiMessage = new byte[3];
    midiMessage[0] = (byte) (0xA0 + (midiChannel - 1));
    midiMessage[1] = (byte) note;
    midiMessage[2] = (byte) pressure;
    return midiMessage;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // MIDI MESSAGE FORMATTING [CONTROL CHANGE]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // MIDI MESSAGE FORMATTING [PROGRAM CHANGE]
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
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // MIDI MESSAGE FORMATTING [CHANNEL PRESSURE]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /** Creates a MIDI Channel Pressure message.
   * 
   * @param midiChannel The MIDI channel number, between unity and 16 inclusive.
   * @param pressure    The pressure, between zero and 127 inclusive.
   * 
   * @return The newly created raw MIDI message.
   * 
   * @throws IllegalArgumentException If any of the arguments is out of range.
   * 
   */
  public final static byte[] createMidiChannelPressureMessage (final int midiChannel, final int pressure)
  {
    if (midiChannel < 1 || midiChannel > 16 || pressure < 0 || pressure > 127)
      throw new IllegalArgumentException ();
    final byte[] midiMessage = new byte[2];
    midiMessage[0] = (byte) (0xD0 + (midiChannel - 1));
    midiMessage[1] = (byte) pressure;
    return midiMessage;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // MIDI MESSAGE FORMATTING [PITCH BEND CHANGE]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /** Creates a MIDI Pitch Bend Change message.
   * 
   * @param midiChannel The MIDI channel number, between unity and 16 inclusive.
   * @param pitchBend   The pitch bend, between -8192 and +8191 inclusive; zero meaning no pitch change.
   * 
   * @return The newly created raw MIDI message.
   * 
   * @throws IllegalArgumentException If any of the arguments is out of range.
   * 
   */
  public final static byte[] createMidiPitchBendChangeMessage (final int midiChannel, final int pitchBend)
  {
    if (midiChannel < 1 || midiChannel > 16 || pitchBend < -8192 || pitchBend > 8191)
      throw new IllegalArgumentException ();
    final byte[] midiMessage = new byte[3];
    midiMessage[0] = (byte) (0xE0 + (midiChannel - 1));
    midiMessage[1] = (byte) ((pitchBend + 8192) & 0x7F);
    midiMessage[2] = (byte) (((pitchBend + 8192) & 0x3F80) >>> 7);
    return midiMessage;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // MIDI MESSAGE FORMATTING [SYSTEM_COMMON_SYSEX - GENERAL]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
  public final static byte[] createMidiSysExMessage (final byte vendorId, final byte[] rawMidiMessage)
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
  // MIDI MESSAGE FORMATTING [SYSTEM_COMMON_SYSEX - NON-REAL-TIME - GENERAL INFORMATION]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private static final byte[] MIDI_ID_REQ_BC = new byte[]
  {
    (byte) 0xF0, // System Exclusive
    (byte) 0x7E, // Non-Real-Time
    (byte) 0x7F, // SysEx Channel / Device ID -> All Channel Broadcast Code (0x7F)
    (byte) 0x06, // General Information
    (byte) 0x01, // Identity Request
    (byte) 0xF7  // End System Exclusive
  };
  
  /** Creates a MIDI SysEx Identity Request message, destined to all devices (All Channel Broadcast).
   * 
   * @return The MIDI message.
   * 
   */
  public static byte[] createMidiSysExMessage_IdentityRequest ()
  {
    return MIDI_ID_REQ_BC.clone ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // MIDI MESSAGE DISSECTION
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /** Dissects a given MIDI Message and returns its type ({@link MidiMessageType}).
   * 
   * <p>
   * Only a basic dissection into the "main" MIDI messages is performed.
   * In particular, dissection of system messages, including System Exclusive, is incomplete.
   * 
   * @param rawMidiMessage The message.
   * 
   * @return The MIDI message type; {@link MidiMessageType#INVALID} if the message was deemed invalid.
   * 
   * @throws IllegalArgumentException If the message is {@code null} or empty.
   * 
   */
  public static MidiMessageType dissectMidiMessage (final byte[] rawMidiMessage)
  {
    // If the message is null or empty, there most likely something wrong at our caller.
    // So, in these cases, we simply throw an IllegalArgumentException.
    if (rawMidiMessage == null || rawMidiMessage.length == 0)
      throw new IllegalArgumentException ();
    // ANY MIDI message must start with a status byte, i.e., having its msb set to 1.
    // If not, we return INVALID (in Java, bytes are signed, so this condition amounts to the byte being non-negative).
    final byte statusByte = rawMidiMessage[0];
    if (statusByte >= 0)
      return MidiMessageType.INVALID;
    // We now grab the first nibble in the status byte and consider the 8 different options.
    final int statusMsbNibble = statusByte & 0xF0;
    switch (statusMsbNibble)
    {
      case 0x80:
      {
        // Note Off: Two status bytes (note and velocity).
        if (rawMidiMessage.length == 3 && rawMidiMessage[1] >= 0 && rawMidiMessage[2] >= 0)
          return MidiMessageType.NOTE_OFF;
        else
          return MidiMessageType.INVALID;
      }
      case 0x90:
      {
        // Note On: Two status bytes (note and velocity).
        if (rawMidiMessage.length == 3 && rawMidiMessage[1] >= 0 && rawMidiMessage[2] >= 0)
          return MidiMessageType.NOTE_ON;
        else
          return MidiMessageType.INVALID;
      }
      case 0xA0:
      {
        // Polyphonic Key Pressure/Aftertouch: Two status bytes (note and pressure).
        if (rawMidiMessage.length == 3 && rawMidiMessage[1] >= 0 && rawMidiMessage[2] >= 0)
          return MidiMessageType.POLYPHONIC_KEY_PRESSURE_AFTERTOUCH;
        else
          return MidiMessageType.INVALID;
      }
      case 0xB0:
      {
        // Control Change: Two data bytes (controller and value/data).
        if (rawMidiMessage.length == 3 && rawMidiMessage[1] >= 0 && rawMidiMessage[2] >= 0)
          return MidiMessageType.CONTROL_CHANGE;
        else
          return MidiMessageType.INVALID;
      }
      case 0xC0:
      {
        // Program Change: Single data byte (program/patch).
        if (rawMidiMessage.length == 2 && rawMidiMessage[1] >= 0)
          return MidiMessageType.PROGRAM_CHANGE;
        else
          return MidiMessageType.INVALID;
      }
      case 0xD0:
      {
        // Channel Pressure/Aftertouch: Single data byte (pressure).
        if (rawMidiMessage.length == 2 && rawMidiMessage[1] >= 0)
          return MidiMessageType.CHANNEL_PRESSURE_AFTERTOUCH;
        else
          return MidiMessageType.INVALID;
      }
      case 0xE0:
      {
        // Pitch Bend Change: Two data bytes (LSB and MSB).
        if (rawMidiMessage.length == 3 && rawMidiMessage[1] >= 0 && rawMidiMessage[2] >= 0)
          return MidiMessageType.PITCH_BEND_CHANGE;
        else
          return MidiMessageType.INVALID;
      }
      case 0xF0:
      {
        // System Exclusive: 0xF0 vendorId data_1 ... data_n 0xF7.
        // XXX TODO This is incomplete!!
        if (rawMidiMessage.length >= 3 && (rawMidiMessage[rawMidiMessage.length - 1] & 0xFF) == 0xF7)
        {
          for (int i = 1; i < rawMidiMessage.length - 1; i++)
            if (rawMidiMessage[i] < 0)
              return MidiMessageType.INVALID;
          return MidiMessageType.SYSTEM_COMMON_SYSEX;
        }
        // XXX SUPPORT OTHER SYSTEM MESSAGES XXX TODO
        else
          return MidiMessageType.INVALID;
      }
      default:
      {
        throw new RuntimeException ();
      }
    }
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}

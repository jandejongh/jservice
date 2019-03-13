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

/** The (supported) MIDI Message types.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public enum MidiMessageType
{
  /** The message is invalid (or unsupported).
   * 
   */
  INVALID,
  /** Note Off Midi Message (0x8n).
   * 
   */
  NOTE_OFF,
  /** Note On Midi Message (0x9n).
   * 
   */
  NOTE_ON,
  /** Polyphonic Key Pressure / After-touch Midi Message (0xAn).
   * 
   */
  POLYPHONIC_KEY_PRESSURE_AFTERTOUCH,
  /** Program Change Midi Message (0xBn).
   * 
   */
  PROGRAM_CHANGE,
  /** Control Change Midi Message (0xCn).
   * 
   */
  CONTROL_CHANGE,
  /** Channel Pressure / After-touch Midi Message (0xDn).
   * 
   */
  CHANNEL_PRESSURE_AFTERTOUCH,
  /** Pitch Bend Change Midi Message (0xEn).
   * 
   */
  PITCH_BEND_CHANGE,
  /** System Common - System Exclusive Midi Message (0xF0 vendorId data_1 {@literal ...} data_n 0xF7).
   * 
   */
  SYSTEM_COMMON_SYSEX;
}

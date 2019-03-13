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

import java.util.LinkedHashSet;
import java.util.Set;

/** Support class for maintenance of {@link MidiServiceListener} in a {@link MidiService} implementation.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class MidiServiceListenerSupport
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // MIDI SERVICE LISTENERS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final Set<MidiServiceListener> midiServiceListeners = new LinkedHashSet<> ();
  
  private final Object midiServiceListenersLock = new Object ();
  
  /** See {@link MidiService#addMidiServiceListener}.
   * 
   * @param l The listener.
   * 
   */
  public final void addMidiServiceListener (final MidiServiceListener l)
  {
    if (l == null)
      return;
    synchronized (this.midiServiceListenersLock)
    {
      if (! this.midiServiceListeners.contains (l))
        this.midiServiceListeners.add (l);
    }
  }

  /** See {@link MidiService#removeMidiServiceListener}.
   * 
   * @param l The listener.
   * 
   */
  public final void removeMidiServiceListener (final MidiServiceListener l)
  {
    if (l == null)
      return;
    synchronized (this.midiServiceListenersLock)
    {
      this.midiServiceListeners.remove (l);
    }
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // MIDI SERVICE LISTENER NOTIFICATIONS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Notifies listeners of the transmission of a MIDI note off message.
   * 
   * @param midiChannel The MIDI channel number, between unity and 16 inclusive.
   * @param note        The note, between zero and 127 inclusive.
   * @param velocity    The velocity, between zero and 127 inclusive.
   * 
   */
  public final void fireMidiTxNoteOff (final int midiChannel, final int note, final int velocity)
  {
    final Set<MidiServiceListener> listeners;
    synchronized (this.midiServiceListenersLock)
    {
      listeners = new LinkedHashSet<> (this.midiServiceListeners);
    }
    for (final MidiServiceListener l : listeners)
      l.midiTxNoteOff (midiChannel, note, velocity);
  }
  
  /** Notifies listeners of the reception of a MIDI note off message.
   * 
   * @param midiChannel The MIDI channel number, between unity and 16 inclusive.
   * @param note        The note, between zero and 127 inclusive.
   * @param velocity    The velocity, between zero and 127 inclusive.
   * 
   */
  public final void fireMidiRxNoteOff (final int midiChannel, final int note, final int velocity)
  {
    final Set<MidiServiceListener> listeners;
    synchronized (this.midiServiceListenersLock)
    {
      listeners = new LinkedHashSet<> (this.midiServiceListeners);
    }
    for (final MidiServiceListener l : listeners)
      l.midiRxNoteOff (midiChannel, note, velocity);
  }
  
  /** Notifies listeners of the transmission of a MIDI note on message.
   * 
   * @param midiChannel The MIDI channel number, between unity and 16 inclusive.
   * @param note        The note, between zero and 127 inclusive.
   * @param velocity    The velocity, between zero and 127 inclusive.
   * 
   */
  public final void fireMidiTxNoteOn (final int midiChannel, final int note, final int velocity)
  {
    final Set<MidiServiceListener> listeners;
    synchronized (this.midiServiceListenersLock)
    {
      listeners = new LinkedHashSet<> (this.midiServiceListeners);
    }
    for (final MidiServiceListener l : listeners)
      l.midiTxNoteOn (midiChannel, note, velocity);
  }
  
  /** Notifies listeners of the reception of a MIDI note on message.
   * 
   * @param midiChannel The MIDI channel number, between unity and 16 inclusive.
   * @param note        The note, between zero and 127 inclusive.
   * @param velocity    The velocity, between zero and 127 inclusive.
   * 
   */
  public final void fireMidiRxNoteOn (final int midiChannel, final int note, final int velocity)
  {
    final Set<MidiServiceListener> listeners;
    synchronized (this.midiServiceListenersLock)
    {
      listeners = new LinkedHashSet<> (this.midiServiceListeners);
    }
    for (final MidiServiceListener l : listeners)
      l.midiRxNoteOn (midiChannel, note, velocity);
  }
  
  /** Notifies listeners of the transmission of a MIDI program (patch) change.
   * 
   * @param midiChannel The MIDI channel number, between unity and 16 inclusive.
   * @param patch       The patch (program) number, between zero and 127 inclusive.
   * 
   */
  public final void fireMidiTxProgramChange (final int midiChannel, final int patch)
  {
    final Set<MidiServiceListener> listeners;
    synchronized (this.midiServiceListenersLock)
    {
      listeners = new LinkedHashSet<> (this.midiServiceListeners);
    }
    for (final MidiServiceListener l : listeners)
      l.midiTxProgramChange (midiChannel, patch);
  }
  
  /** Notifies listeners of the reception of a MIDI program (patch) change.
   * 
   * @param midiChannel The MIDI channel number, between unity and 16 inclusive.
   * @param patch       The patch (program) number, between zero and 127 inclusive.
   * 
   */
  public final void fireMidiRxProgramChange (final int midiChannel, final int patch)
  {
    final Set<MidiServiceListener> listeners;
    synchronized (this.midiServiceListenersLock)
    {
      listeners = new LinkedHashSet<> (this.midiServiceListeners);
    }
    for (final MidiServiceListener l : listeners)
      l.midiRxProgramChange (midiChannel, patch);
  }
  
  /** Notifies listeners of the transmission of a MIDI control change.
   * 
   * @param midiChannel The MIDI channel number, between unity and 16 inclusive.
   * @param controller  The MIDI controller number, between zero and 127 inclusive.
   * @param value       The value for the controller, between zero and 127 inclusive.
   * 
   */
  public final void fireMidiTxControlChange (final int midiChannel, final int controller, final int value)
  {
    final Set<MidiServiceListener> listeners;
    synchronized (this.midiServiceListenersLock)
    {
      listeners = new LinkedHashSet<> (this.midiServiceListeners);
    }
    for (final MidiServiceListener l : listeners)
      l.midiTxControlChange (midiChannel, controller, value);    
  }
  
  /** Notifies listeners of the reception of a MIDI control change.
   * 
   * @param midiChannel The MIDI channel number, between unity and 16 inclusive.
   * @param controller  The MIDI controller number, between zero and 127 inclusive.
   * @param value       The value for the controller, between zero and 127 inclusive.
   * 
   */
  public final void fireMidiRxControlChange (final int midiChannel, final int controller, final int value)
  {
    final Set<MidiServiceListener> listeners;
    synchronized (this.midiServiceListenersLock)
    {
      listeners = new LinkedHashSet<> (this.midiServiceListeners);
    }
    for (final MidiServiceListener l : listeners)
      l.midiRxControlChange (midiChannel, controller, value);    
  }
  
  /** Notifies listeners of the transmission of a MIDI System Exclusive (SysEx) message.
   * 
   * @param vendorId       The vendor ID.
   * @param rawMidiMessage The complete raw MIDI message, non-{@code null}.
   * 
   */
  public final void fireMidiTxSysEx (final byte vendorId, final byte[] rawMidiMessage)
  {
    final Set<MidiServiceListener> listeners;
    synchronized (this.midiServiceListenersLock)
    {
      listeners = new LinkedHashSet<> (this.midiServiceListeners);
    }
    for (final MidiServiceListener l : listeners)
      l.midiTxSysEx (vendorId, rawMidiMessage);
  }
  
  /** Notifies listeners of the reception of a MIDI System Exclusive (SysEx) message.
   * 
   * @param vendorId       The vendor ID.
   * @param rawMidiMessage The complete raw MIDI message, non-{@code null}.
   * 
   */
  public final void fireMidiRxSysEx (final byte vendorId, final byte[] rawMidiMessage)
  {
    final Set<MidiServiceListener> listeners;
    synchronized (this.midiServiceListenersLock)
    {
      listeners = new LinkedHashSet<> (this.midiServiceListeners);
    }
    for (final MidiServiceListener l : listeners)
      l.midiRxSysEx (vendorId, rawMidiMessage);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}

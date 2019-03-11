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

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.javajdj.jservice.midi.MidiService;

/** Support for {@link MidiDeviceListener}s for the implementation of {@link MidiDevice}.
 *
 * @param <P> The parameter value (generic) type.
 * 
 * @see MidiDeviceListener
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class MidiDeviceListenerSupport<P>
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // MIDI DEVICE LISTENERS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final Set<MidiDeviceListener> midiDeviceListeners = new LinkedHashSet<> ();
  
  private final Object midiDeviceListenersLock = new Object ();
  
  /** Returns a (unmodifiable) {@code Set} holding the current listeners.
   * 
   * <p>
   * XXX It is, in fact, modifiable.
   * 
   * @return A (unmodifiable) {@code Set} holding the current listeners.
   * 
   * @see AbstractMidiDevice#getMidiDeviceListenersCopy
   * 
   */
  public final Set<MidiDeviceListener> getMidiDeviceListenersCopy ()
  {
    final Set<MidiDeviceListener> listeners;
    synchronized (this.midiDeviceListenersLock)
    {
      listeners = new LinkedHashSet<> (this.midiDeviceListeners);
    }
    return listeners;
  }
  
  /** Registers a {@link MidiDeviceListener}.
   * 
   * @param l The listener, ignored if {@code null} or already registered.
   * 
   * @see MidiService#addMidiServiceListener
   * 
   */  
  public final void addMidiDeviceListener (final MidiDeviceListener l)
  {
    if (l == null)
      return;
    synchronized (this.midiDeviceListenersLock)
    {
      if (! this.midiDeviceListeners.contains (l))
        this.midiDeviceListeners.add (l);
    }
  }

  /** Unregisters a {@link MidiDeviceListener}.
   * 
   * @param l The listener, ignored if {@code null} or not registered.
   * 
   * @see MidiService#removeMidiServiceListener
   * 
   */  
  public final void removeMidiDeviceListener (final MidiDeviceListener l)
  {
    if (l == null)
      return;
    synchronized (this.midiDeviceListenersLock)
    {
      this.midiDeviceListeners.remove (l);
    }
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // MIDI DEVICE LISTENER NOTIFICATIONS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Notifies listeners of a parameter change.
   * 
   * @param changes A {@link Map} holding the names of the parameters that have changed,
   *                mapped onto their new values (non-{@code null}).
   * 
   * @see MidiDeviceListener#notifyParameterChanged
   * 
   */
  public final void fireParameterChanged (final Map<String, P> changes)
  {
    final Set<MidiDeviceListener> listeners;
    synchronized (this.midiDeviceListenersLock)
    {
      listeners = new LinkedHashSet<> (this.midiDeviceListeners);
    }
    for (final MidiDeviceListener l : listeners)
      l.notifyParameterChanged (changes);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}

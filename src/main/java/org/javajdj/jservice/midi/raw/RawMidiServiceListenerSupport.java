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
package org.javajdj.jservice.midi.raw;

import java.util.LinkedHashSet;
import java.util.Set;

/** Support class (for delegation) for administering and notifying {@link RawMidiServiceListener}s.
 *
 * @see RawMidiService
 * @see RawMidiServiceListener
 * @see AbstractRawMidiService
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class RawMidiServiceListenerSupport
{
  
  private final Set<RawMidiServiceListener> rawMidiServiceListeners = new LinkedHashSet<> ();
  
  private final Object rawMidiServiceListenersLock = new Object ();
  
  /** Adds a MIDI (raw) message listener.
   * 
   * @param l The listener, ignored if {@code null} or already registered.
   * 
   * @see RawMidiService#addRawMidiServiceListener
   * 
   */
  public final void addRawMidiServiceListener (final RawMidiServiceListener l)
  {
    if (l == null)
      return;
    synchronized (this.rawMidiServiceListenersLock)
    {
      if (! this.rawMidiServiceListeners.contains (l))
        this.rawMidiServiceListeners.add (l);
    }
  }

  /** Removes a MIDI (raw) message listener.
   * 
   * @param l The listener, ignored if {@code null} or not registered.
   * 
   * @see RawMidiService#removeRawMidiServiceListener
   * 
   */
  public final void removeRawMidiServiceListener (final RawMidiServiceListener l)
  {
    if (l == null)
      return;
    synchronized (this.rawMidiServiceListenersLock)
    {
      this.rawMidiServiceListeners.remove (l);
    }
  }

  /** Notifies registered {@link RawMidiServiceListener}s of the transmission of a (raw) MIDI message.
   * 
   * @param message The message transmitted.
   * 
   * @see AbstractRawMidiService#fireRawMidiMessageTx
   * 
   */
  public final void fireRawMidiMessageTx (final byte[] message)
  {
    final Set<RawMidiServiceListener> listeners;
    synchronized (this.rawMidiServiceListenersLock)
    {
      listeners = new LinkedHashSet<> (this.rawMidiServiceListeners);
    }
    for (final RawMidiServiceListener l : listeners)
      l.rawMidiMessageTx (message);
  }
  
  /** Notifies registered {@link RawMidiServiceListener}s of the reception of a (raw) MIDI message.
   * 
   * @param message The message received.
   * 
   * @see AbstractRawMidiService#fireRawMidiMessageRx
   * 
   */
  public final void fireRawMidiMessageRx (final byte[] message)
  {
    final Set<RawMidiServiceListener> listeners;
    synchronized (this.rawMidiServiceListenersLock)
    {
      listeners = new LinkedHashSet<> (this.rawMidiServiceListeners);
    }
    for (final RawMidiServiceListener l : listeners)
      l.rawMidiMessageRx (message);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}

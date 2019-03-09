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

import java.time.Instant;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.javajdj.jservice.Service;

/** A {@link RawMidiService} that neither sends nor receives MIDI messages.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class RawMidiService_None
  extends AbstractRawMidiService
  implements RawMidiService
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (RawMidiService_None.class.getName ());

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SERVICE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public final synchronized void startService ()
  {
    if (getStatus () == Status.ACTIVE)
      return;
    LOG.log (Level.INFO, "Starting MIDI Service [None]: {0}.", this);
    setStatus (Status.ACTIVE);
  }

  @Override
  public final synchronized void stopService ()
  {
    if (getStatus () == Status.STOPPED)
      return;
    LOG.log (Level.INFO, "Stopping MIDI Service [None]: {0}.", this);
    setStatus (Service.Status.STOPPED);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // RAW MIDI SERVICE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Does nothing.
   * 
   * @param rawMidiMessage The (raw) MIDI message.
   * 
   */
  @Override
  public final void sendRawMidiMessage (final byte[] rawMidiMessage)
  {
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // ACTIVITY MONITORABLE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Returns {@link RawMidiService#RAW_MIDI_SERVICE_MONITORABLE_ACTIVITIES}.
   * 
   * <p>
   * By virtue of the contract of {@link RawMidiService}.
   * 
   * @return {@link RawMidiService#RAW_MIDI_SERVICE_MONITORABLE_ACTIVITIES}.
   * 
   */
  @Override
  public final Set<String> getMonitorableActivities ()
  {
    return RawMidiService.RAW_MIDI_SERVICE_MONITORABLE_ACTIVITIES;
  }

  /** Returns {@link Instant#MIN}.
   * 
   * @return {@link Instant#MIN}.
   * 
   */
  @Override
  public final Instant lastActivity ()
  {
    return Instant.MIN;
  }

  /** Returns {@link Instant#MIN}.
   * 
   * @return {@link Instant#MIN}.
   * 
   */
  @Override
  public final Instant lastActivity (final String monitorableActivity)
  {
    return Instant.MIN;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}

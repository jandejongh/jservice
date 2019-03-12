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

import java.util.logging.Logger;
import org.javajdj.jservice.midi.raw.AbstractRawMidiService;

/** Partial implementation of a {@link MidiService}.
 * 
 * <p>
 * In addition to the functionality of {@link AbstractRawMidiService},
 * this base class takes care of {@link MidiServiceListener} maintenance,
 * and features various MIDI formatting and transmission methods from {@link MidiService},
 * relying on {@link MidiUtils}.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public abstract class AbstractMidiService
  extends AbstractRawMidiService
  implements MidiService
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (AbstractMidiService.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTORS / FACTORIES / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Constructs a MIDI service with name {@code "AbstractMidiService"}.
   * 
   */
  public AbstractMidiService ()
  {
    this ("AbstractMidiService");
  }

  /** Constructs a MIDI service with given name.
   * 
   * @param name The name of the service.
   * 
   * @see #setName
   * 
   */
  public AbstractMidiService (final String name)
  {
    super (name);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // MIDI SERVICE LISTENERS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final MidiServiceListenerSupport midiServiceListenerSupport = new MidiServiceListenerSupport ();

  @Override
  public final void addMidiServiceListener (final MidiServiceListener l)
  {
    this.midiServiceListenerSupport.addMidiServiceListener (l);
  }

  @Override
  public final void removeMidiServiceListener (final MidiServiceListener l)
  {
    this.midiServiceListenerSupport.removeMidiServiceListener (l);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // MIDI SERVICE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public final void sendMidiProgramChange (final int midiChannel, final int patch)
  {
    if (getStatus () != Status.ACTIVE)
      return;
    final byte[] midiMessage = MidiUtils.createMidiProgramChangeMessage (midiChannel, patch);
    sendRawMidiMessage (midiMessage);
    this.midiServiceListenerSupport.fireMidiTxProgramChange (midiChannel, patch);
  }

  @Override
  public final void sendMidiControlChange (final int midiChannel, final int controller, final int value)
  {
    if (getStatus () != Status.ACTIVE)
      return;
    final byte[] midiMessage = MidiUtils.createMidiControlChangeMessage (midiChannel, controller, value);
    sendRawMidiMessage (midiMessage);
    this.midiServiceListenerSupport.fireMidiTxControlChange (midiChannel, controller, value);
  }
  
  @Override
  public final void sendMidiSysEx (final byte vendorId, final byte[] rawMidiMessage)
  {
    if (getStatus () != Status.ACTIVE)
      return;
    final byte[] midiMessage = MidiUtils.createMidiSysExMessage (vendorId, rawMidiMessage);
    sendRawMidiMessage (midiMessage);
    this.midiServiceListenerSupport.fireMidiTxSysEx (vendorId, rawMidiMessage);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}

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

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.javajdj.util.hex.HexUtils;
import org.javajdj.jservice.midi.raw.RawMidiService;
import org.javajdj.jservice.midi.raw.RawMidiServiceListener;
import org.javajdj.jservice.support.Service_FromMix;

/** A {@link MidiService} derived from a given {@link RawMidiService}.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class MidiService_FromRaw
  extends Service_FromMix
  implements MidiService
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (MidiService_FromRaw.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTORS / FACTORIES / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Constructs a {@link MidiService} derived from a given {@link RawMidiService}.
   * 
   * @param rawMidiService The raw MIDI service, may be {@code null}.
   * 
   */
  public MidiService_FromRaw (final RawMidiService rawMidiService)
  {
    super (null, null);
    this.rawMidiService = rawMidiService;
    addTargetService (this.rawMidiService);
    if (this.rawMidiService != null)
      this.rawMidiService.addRawMidiServiceListener (this.rawMidiServiceListener);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // RAW MIDI SERVICE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final RawMidiService rawMidiService;

  /** Returns the underlying raw MIDI service.
   * 
   * @return the underlying raw MIDI service, may be {@code null}, but is fixed.
   */
  public final RawMidiService getRawMidiService ()
  {
    return this.rawMidiService;
  }
  
  @Override
  public final void addRawMidiServiceListener (final RawMidiServiceListener l)
  {
    if (this.rawMidiService != null)
      this.rawMidiService.addRawMidiServiceListener (l);
  }

  @Override
  public final void removeRawMidiServiceListener (final RawMidiServiceListener l)
  {
    if (this.rawMidiService != null)
      this.rawMidiService.removeRawMidiServiceListener (l);
  }

  @Override
  public final void sendRawMidiMessage (final byte[] midiMessage)
  {
    if (this.rawMidiService != null)
      this.rawMidiService.sendRawMidiMessage (midiMessage);
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

  /** Notifies listeners of the transmission of a MIDI note off message.
   * 
   * @param midiChannel The MIDI channel number, between unity and 16 inclusive.
   * @param note        The note, between zero and 127 inclusive.
   * @param velocity    The velocity, between zero and 127 inclusive.
   * 
   */
  protected final void fireMidiTxNoteOff (final int midiChannel, final int note, final int velocity)
  {
    this.midiServiceListenerSupport.fireMidiTxNoteOff (midiChannel, note, velocity);
  }
  
  /** Notifies listeners of the reception of a MIDI note off message.
   * 
   * @param midiChannel The MIDI channel number, between unity and 16 inclusive.
   * @param note        The note, between zero and 127 inclusive.
   * @param velocity    The velocity, between zero and 127 inclusive.
   * 
   */
  protected final void fireMidiRxNoteOff (final int midiChannel, final int note, final int velocity)
  {
    this.midiServiceListenerSupport.fireMidiRxNoteOff (midiChannel, note, velocity);
  }
  
  /** Notifies listeners of the transmission of a MIDI note on message.
   * 
   * @param midiChannel The MIDI channel number, between unity and 16 inclusive.
   * @param note        The note, between zero and 127 inclusive.
   * @param velocity    The velocity, between zero and 127 inclusive.
   * 
   */
  protected final void fireMidiTxNoteOn (final int midiChannel, final int note, final int velocity)
  {
    this.midiServiceListenerSupport.fireMidiTxNoteOn (midiChannel, note, velocity);
  }
  
  /** Notifies listeners of the reception of a MIDI note on message.
   * 
   * @param midiChannel The MIDI channel number, between unity and 16 inclusive.
   * @param note        The note, between zero and 127 inclusive.
   * @param velocity    The velocity, between zero and 127 inclusive.
   * 
   */
  protected final void fireMidiRxNoteOn (final int midiChannel, final int note, final int velocity)
  {
    this.midiServiceListenerSupport.fireMidiRxNoteOn (midiChannel, note, velocity);
  }
  
  /** Notifies listeners of the transmission of a MIDI polyphonic key pressure message.
   * 
   * @param midiChannel The MIDI channel number, between unity and 16 inclusive.
   * @param note        The note, between zero and 127 inclusive.
   * @param pressure    The pressure, between zero and 127 inclusive.
   * 
   */
  protected final void fireMidiTxPolyphonicKeyPressure (final int midiChannel, final int note, final int pressure)
  {
    this.midiServiceListenerSupport.fireMidiTxPolyphonicKeyPressure (midiChannel, note, pressure);
  }
  
  /** Notifies listeners of the reception of a MIDI polyphonic key pressure message.
   * 
   * @param midiChannel The MIDI channel number, between unity and 16 inclusive.
   * @param note        The note, between zero and 127 inclusive.
   * @param pressure    The pressure, between zero and 127 inclusive.
   * 
   */
  protected final void fireMidiRxPolyphonicKeyPressure (final int midiChannel, final int note, final int pressure)
  {
    this.midiServiceListenerSupport.fireMidiRxPolyphonicKeyPressure (midiChannel, note, pressure);
  }
  
  /** Notifies registered {@link MidiServiceListener}s of the transmission of a MIDI program (patch) change.
   * 
   * @param midiChannel The MIDI channel number, between unity and 16 inclusive.
   * @param patch       The patch (program) number, between zero and 127 inclusive.
   * 
   */
  protected final void fireMidiTxProgramChange (final int midiChannel, final int patch)
  {
    this.midiServiceListenerSupport.fireMidiTxProgramChange (midiChannel, patch);
  }

  /** Notifies registered {@link MidiServiceListener}s of the reception of a MIDI program (patch) change.
   * 
   * @param midiChannel The MIDI channel number, between unity and 16 inclusive.
   * @param patch       The patch (program) number, between zero and 127 inclusive.
   * 
   */
  protected final void fireMidiRxProgramChange (final int midiChannel, final int patch)
  {
    this.midiServiceListenerSupport.fireMidiRxProgramChange (midiChannel, patch);
  }

  /** Notifies registered {@link MidiServiceListener}s of the transmission of a MIDI control change.
   * 
   * @param midiChannel The MIDI channel number, between unity and 16 inclusive.
   * @param controller  The MIDI controller number, between zero and 127 inclusive.
   * @param value       The value for the controller, between zero and 127 inclusive.
   * 
   */
  protected final void fireMidiTxControlChange (final int midiChannel, final int controller, final int value)
  {
    this.midiServiceListenerSupport.fireMidiTxControlChange (midiChannel, controller, value);
  }

  /** Notifies registered {@link MidiServiceListener}s of the reception of a MIDI control change.
   * 
   * @param midiChannel The MIDI channel number, between unity and 16 inclusive.
   * @param controller  The MIDI controller number, between zero and 127 inclusive.
   * @param value       The value for the controller, between zero and 127 inclusive.
   * 
   */
  protected final void fireMidiRxControlChange (final int midiChannel, final int controller, final int value)
  {
    this.midiServiceListenerSupport.fireMidiRxControlChange (midiChannel, controller, value);
  }

  /** Notifies registered {@link MidiServiceListener}s of the transmission of a MIDI System Exclusive (SysEx) message.
   * 
   * @param vendorId       The vendor ID.
   * @param rawMidiMessage The complete raw MIDI message, non-{@code null}.
   * 
   */
  protected final void fireMidiTxSysEx (final byte vendorId, final byte[] rawMidiMessage)
  {
    this.midiServiceListenerSupport.fireMidiTxSysEx (vendorId, rawMidiMessage);
  }

  /** Notifies registered {@link MidiServiceListener}s of the reception of a MIDI System Exclusive (SysEx) message.
   * 
   * @param vendorId       The vendor ID.
   * @param rawMidiMessage The complete raw MIDI message, non-{@code null}.
   * 
   */
  protected final void fireMidiRxSysEx (final byte vendorId, final byte[] rawMidiMessage)
  {
    this.midiServiceListenerSupport.fireMidiRxSysEx (vendorId, rawMidiMessage);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // MIDI SERVICE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public final void sendMidiNoteOff (final int midiChannel, final int note, final int velocity)
  {
    if (getStatus () != Status.ACTIVE)
      return;
    final byte[] midiMessage = MidiUtils.createMidiNoteOffMessage (midiChannel, note, velocity);
    sendRawMidiMessage (midiMessage);
    this.midiServiceListenerSupport.fireMidiTxNoteOff (midiChannel, note, velocity);    
  }
  
  @Override
  public final void sendMidiNoteOn (final int midiChannel, final int note, final int velocity)
  {
    if (getStatus () != Status.ACTIVE)
      return;
    final byte[] midiMessage = MidiUtils.createMidiNoteOnMessage (midiChannel, note, velocity);
    sendRawMidiMessage (midiMessage);
    this.midiServiceListenerSupport.fireMidiTxNoteOn (midiChannel, note, velocity);
  }
    
  @Override
  public final void sendMidiPolyphonicKeyPressure (final int midiChannel, final int note, final int pressure)
  {
    if (getStatus () != Status.ACTIVE)
      return;
    final byte[] midiMessage = MidiUtils.createMidiPolyphonicKeyPressureMessage (midiChannel, note, pressure);
    sendRawMidiMessage (midiMessage);
    this.midiServiceListenerSupport.fireMidiTxPolyphonicKeyPressure (midiChannel, note, pressure);
  }
    
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
    updateActivity (MidiService.ACTIVITY_SYSEX_NAME);
    this.midiServiceListenerSupport.fireMidiTxSysEx (vendorId, rawMidiMessage);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // ACTIVITY MONITORABLE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final Map<String, Instant> monitorableActivities = new HashMap<> ();
  
  protected final void updateActivity (final String activity)
  {
    synchronized (this.monitorableActivities)
    {
      this.monitorableActivities.put (activity, Instant.now ());
    }
  }
  
  /** Returns {@link MidiService#MIDI_SERVICE_MONITORABLE_ACTIVITIES}.
   * 
   * @return {@link MidiService#MIDI_SERVICE_MONITORABLE_ACTIVITIES}.
   * 
   */
  @Override
  public Set<String> getMonitorableActivities ()
  {
    return MidiService.MIDI_SERVICE_MONITORABLE_ACTIVITIES;
  }

  @Override
  public final Instant lastActivity ()
  {
    return MidiService.super.lastActivity ();
  }

  @Override
  public Instant lastActivity (final String monitorableActivity)
  {
    if (this.rawMidiService != null)
    {
      // XXX Still not feeling comfortable about null activities...
      final Set<String> rawMidiServiceActivities = this.rawMidiService.getMonitorableActivities ();
      if (rawMidiServiceActivities.contains (monitorableActivity))
        return this.rawMidiService.lastActivity (monitorableActivity);
    }
    synchronized (this.monitorableActivities)
    {
      if (this.monitorableActivities.containsKey (monitorableActivity))
        return this.monitorableActivities.get (monitorableActivity);
    }
    return Instant.MIN;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // RAW MIDI LISTENER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private volatile int rxErrors = 0;
  
  private final Object rxErrorsLock = new Object ();
  
  private final RawMidiServiceListener rawMidiServiceListener = new RawMidiServiceListener ()
  {
    
    /** Does nothing.
     * 
     * @param rawMidiMessage The (raw) MIDI message.
     * 
     */
    @Override
    public void rawMidiMessageTx (final byte[] rawMidiMessage)
    {
    }

    /** Main received raw MIDI message dissection.
     * 
     * @param rawMidiMessage The (raw) MIDI message.
     * 
     */
    @Override
    public void rawMidiMessageRx (final byte[] rawMidiMessage)
    {
      // LOG.log (Level.INFO, "Received MIDI: {0}.", HexUtils.bytesToHex (rawMidiMessage));
      // XXX We should do this; BUT APPARENTLY WE ARE NOT STARTED??
      //if (MidiService_FromRaw.this.getStatus () == Status.STOPPED)
      //  return;
      if (rawMidiMessage == null || rawMidiMessage.length == 0)
      {
        LOG.log (Level.SEVERE, "Received null or empty MIDI message from Raw Midi Service {0}; ignored!",
          MidiService_FromRaw.this.rawMidiService);
        return;
      }
      final MidiMessageType midiMessageType = MidiUtils.dissectMidiMessage (rawMidiMessage);
      final int statusByte = rawMidiMessage[0] & 0xFF;
      switch (midiMessageType)
      {
        case INVALID:
        {
          LOG.log (Level.WARNING, "Received invalid (or unsupported) MIDI message (ignored): {0}.",
            HexUtils.bytesToHex (rawMidiMessage));
          synchronized (MidiService_FromRaw.this.rxErrorsLock)
          {
            MidiService_FromRaw.this.rxErrors++;
          }
          MidiService_FromRaw.this.updateActivity (MidiService.ACTIVITY_RX_ERROR_NAME);
          break;
        }
        case NOTE_OFF:
        {
          final int midiChannel = (statusByte & 0x0F) + 1;
          final int note = (byte) rawMidiMessage[1];
          final int velocity = (byte) rawMidiMessage[2];
          fireMidiRxNoteOff (midiChannel, note, velocity);
          // LOG.log (Level.INFO, "Received note off, channel={0}, note={1}, velocity={2}.",
          //   new Object[]{midiChannel, note, velocity});
          break;
        }
        case NOTE_ON:
        {
          final int midiChannel = (statusByte & 0x0F) + 1;
          final int note = (byte) rawMidiMessage[1];
          final int velocity = (byte) rawMidiMessage[2];
          fireMidiRxNoteOn (midiChannel, note, velocity);
          // LOG.log (Level.INFO, "Received note on, channel={0}, note={1}, velocity={2}.",
          //   new Object[]{midiChannel, note, velocity});
          break;
        }
        case POLYPHONIC_KEY_PRESSURE_AFTERTOUCH:
        {
          final int midiChannel = (statusByte & 0x0F) + 1;
          final int note = (byte) rawMidiMessage[1];
          final int pressure = (byte) rawMidiMessage[2];
          fireMidiRxPolyphonicKeyPressure (midiChannel, note, pressure);
          // LOG.log (Level.INFO, "Received polyphonic key pressure, channel={0}, note={1}, pressure={2}.",
          //   new Object[]{midiChannel, note, pressure});
          break;
        }
        case PROGRAM_CHANGE:
        {
          final int midiChannel = (statusByte & 0x0F) + 1;
          final int patch = (byte) rawMidiMessage[1];
          fireMidiRxProgramChange (midiChannel, patch);
          // LOG.log (Level.INFO, "Received program change, channel={0}, patch={1}.", new Object[]{midiChannel, patch});
          break;
        }
        case CONTROL_CHANGE:
        {
          final int midiChannel = (statusByte & 0x0F) + 1;
          final int controller = rawMidiMessage[1];
          final int value = rawMidiMessage[2];
          fireMidiRxControlChange (midiChannel, controller, value);
          // LOG.log (Level.INFO, "Received control change, channel={0}, controller={1}, value={2}.",
          //   new Object[]{midiChannel, controller, value});
          break;
        }
        case CHANNEL_PRESSURE_AFTERTOUCH:
          throw new UnsupportedOperationException ();
        case PITCH_BEND_CHANGE:
          throw new UnsupportedOperationException ();
        case SYSTEM_COMMON_SYSEX:
        {
          // System Exclusive
          final byte vendorId = rawMidiMessage[1];
          MidiService_FromRaw.this.updateActivity (MidiService.ACTIVITY_SYSEX_NAME);
          fireMidiRxSysEx (vendorId, rawMidiMessage);
          // LOG.log (Level.INFO, "Received system exclusive, vendorId={0}, rawMidiMessage={1}.",
          //   new Object[]{HexUtils.bytesToHex (new byte[]{vendorId}), HexUtils.bytesToHex (rawMidiMessage)});
          break;
        }
        default:
          throw new RuntimeException ();
      }
    }
  };
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}

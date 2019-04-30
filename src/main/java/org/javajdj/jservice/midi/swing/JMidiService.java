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
package org.javajdj.jservice.midi.swing;

import java.awt.GridLayout;
import java.beans.PropertyChangeListener;
import java.time.Instant;
import java.util.Set;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.javajdj.jservice.midi.MidiService;
import org.javajdj.jservice.midi.MidiService_FromRaw;
import org.javajdj.jservice.midi.MidiServiceListener;
import org.javajdj.jservice.midi.raw.RawMidiService;
import org.javajdj.jservice.midi.raw.RawMidiServiceListener;
import org.javajdj.jservice.swing.JServiceControl;

/** A {@link JComponent} that implements a {@link MidiService}
 *  from a user-selectable {@link RawMidiService}.
 *
 * @see RawMidiService
 * @see RawMidiServiceType
 * @see JRawMidiService
 * @see MidiService
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JMidiService
  extends JPanel
  implements MidiService
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JMidiService.class.getName ());

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTORS / FACTORIES / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Constructs a {@link MidiService} that is also a {@link JComponent}.
   * 
   * <p>
   * An internally held {@link MidiService_FromRaw} is created from a new {@link JRawMidiService}.
   * All {@link MidiService} methods are delegated to the internally held object.
   * 
   * <p>
   * The {@link JServiceControl} on control the {@link JRawMidiService}
   * is replaced with a new one that control the {@link MidiService},
   * in order to have the raw service subordinate to this one.
   * 
   * <p>
   * XXX TODO The {@link MidiService} activities are not monitored yet.
   * 
   */
  public JMidiService ()
  {
    super ();
    setLayout (new GridLayout (1, 1, 0, 0));
    final JRawMidiService jRawMidiService = new JRawMidiService ();
    add (jRawMidiService);
    this.midiService = new MidiService_FromRaw (jRawMidiService);
    jRawMidiService.setJServiceControl (new JServiceControl (this, JRawMidiService.DEFAULT_STATUS_COLOR_FUNCTION));
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // MIDI SERVICE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Non-null and fixed.
   * 
   */
  private final MidiService midiService;

  @Override
  public void addSettingsListener (final PropertyChangeListener l)
  {
    this.midiService.addSettingsListener (l);
  }

  @Override
  public void removeSettingsListener (final PropertyChangeListener l)
  {
    this.midiService.removeSettingsListener (l);
  }

  @Override
  public void addStatusListener (final StatusListener l)
  {
    this.midiService.addStatusListener (l);
  }

  @Override
  public void removeStatusListener (final StatusListener l)
  {
    this.midiService.removeStatusListener (l);
  }

  @Override
  public void removeStatusListeners ()
  {
    this.midiService.removeStatusListeners ();
  }

  @Override
  public void addRawMidiServiceListener (final RawMidiServiceListener l)
  {
    this.midiService.addRawMidiServiceListener (l);
  }

  @Override
  public void removeRawMidiServiceListener (final RawMidiServiceListener l)
  {
    this.midiService.removeRawMidiServiceListener (l);
  }

  @Override
  public void addMidiServiceListener (final MidiServiceListener l)
  {
    this.midiService.addMidiServiceListener (l);
  }

  @Override
  public void removeMidiServiceListener (final MidiServiceListener l)
  {
    this.midiService.removeMidiServiceListener (l);
  }

  @Override
  public void sendRawMidiMessage (final byte[] midiMessage)
  {
    this.midiService.sendRawMidiMessage (midiMessage);
  }

  @Override
  public String getName ()
  {
    return this.midiService.getName ();
  }

  @Override
  public void setName (final String name)
  {
    this.midiService.setName (name);
  }

  @Override
  public void startService ()
  {
    this.midiService.startService ();
  }

  @Override
  public void stopService ()
  {
    this.midiService.stopService ();
  }

  @Override
  public Status getStatus ()
  {
    return this.midiService.getStatus ();
  }

  @Override
  public Set<String> getMonitorableActivities ()
  {
    return this.midiService.getMonitorableActivities ();
  }

  @Override
  public Instant lastActivity ()
  {
    return this.midiService.lastActivity ();
  }

  @Override
  public Instant lastActivity (String monitorableActivity)
  {
    return this.midiService.lastActivity (monitorableActivity);
  }

  /** Returns "JMidiService".
   * 
   * @return "JMidiService".
   * 
   */
  @Override
  public String toString ()
  {
    return "JMidiService";
  }

  @Override
  public void sendMidiNoteOff (final int midiChannel, final int note, final int velocity)
  {
    this.midiService.sendMidiNoteOff (midiChannel, note, velocity);
  }

  @Override
  public void sendMidiNoteOn (final int midiChannel, final int note, final int velocity)
  {
    this.midiService.sendMidiNoteOn (midiChannel, note, velocity);
  }

  @Override
  public void sendMidiPolyphonicKeyPressure (final int midiChannel, final int note, final int pressure)
  {
    this.midiService.sendMidiPolyphonicKeyPressure (midiChannel, note, pressure);
  }

  @Override
  public void sendMidiControlChange (final int midiChannel, final int controller, final int value)
  {
    this.midiService.sendMidiControlChange (midiChannel, controller, value);
  }

  @Override
  public void sendMidiProgramChange (final int midiChannel, final int patch)
  {
    this.midiService.sendMidiProgramChange (midiChannel, patch);
  }

  @Override
  public void sendMidiChannelPressure (final int midiChannel, final int pressure)
  {
    this.midiService.sendMidiChannelPressure (midiChannel, pressure);
  }

  @Override
  public void sendMidiPitchBendChange (final int midiChannel, final int pitchBend)
  {
    this.midiService.sendMidiPitchBendChange (midiChannel, pitchBend);
  }

  @Override
  public void sendMidiSysEx (final byte vendorId, final byte[] rawMidiMessage)
  {
    this.midiService.sendMidiSysEx (vendorId, rawMidiMessage);
  }
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}

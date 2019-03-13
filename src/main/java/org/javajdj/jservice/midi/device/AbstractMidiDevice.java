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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import org.javajdj.jservice.Service;
import org.javajdj.jservice.midi.DefaultMidiServiceListener;
import org.javajdj.jservice.midi.MidiService;
import org.javajdj.jservice.midi.MidiServiceListener;
import org.javajdj.jservice.support.Service_FromMix;

/** A partial implementation of a {@link MidiDevice}.
 *
 * @param <P> The parameter value (generic) type.
 * @param <D> The type used to describe parameters.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public abstract class AbstractMidiDevice<P, D extends ParameterDescriptor>
  extends Service_FromMix
  implements MidiDevice<P>
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (AbstractMidiDevice.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTORS / FACTORIES / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Constructs a {@link AbstractMidiDevice} that uses a given {@link MidiService}.
   * 
   * @param midiService The MIDI service, may be {@code null}.
   * 
   */
  public AbstractMidiDevice (final MidiService midiService)
  {
    super (null, null);
    // XXX For now, midiService is fixed and non-null...
    if (midiService == null)
      throw new IllegalArgumentException ();
    this.midiService = midiService;
    this.midiService.addMidiServiceListener (this.midiServiceListener);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // MIDI DEVICE LISTENERS
  // MIDI DEVICE LISTENER SUPPORT
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final MidiDeviceListenerSupport midiDeviceListenerSupport = new MidiDeviceListenerSupport ();
  
  /** Returns a (unmodifiable) {@code Set} holding the current listeners.
   * 
   * <p>
   * XXX It is, in fact, modifiable.
   * 
   * @return A (unmodifiable) {@code Set} holding the current listeners.
   * 
   */
  protected final Set<MidiDeviceListener> getMidiDeviceListenersCopy ()
  {
    return this.midiDeviceListenerSupport.getMidiDeviceListenersCopy ();
  }
  
  @Override
  public final void addMidiDeviceListener (final MidiDeviceListener midiDeviceListener)
  {
    this.midiDeviceListenerSupport.addMidiDeviceListener (midiDeviceListener);
  }

  @Override
  public final void removeMidiDeviceListener (final MidiDeviceListener midiDeviceListener)
  {
    this.midiDeviceListenerSupport.removeMidiDeviceListener (midiDeviceListener);
  }

  /** Notifies listeners of a parameter change.
   * 
   * @param changes A {@link Map} holding the names of the parameters that have changed,
   *                mapped onto their new values (non-{@code null}).
   * 
   * @see MidiDeviceListener#notifyParameterChanged
   * 
   */
  protected final void fireParameterChanged (final Map<String, P> changes)
  {
    this.midiDeviceListenerSupport.fireParameterChanged (changes);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // MIDI SERVICE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private /* volatile */ final MidiService midiService;

  @Override
  public final MidiService getMidiService ()
  {
    return this.midiService;
  }
  
  @Override
  public final void setMidiService (final MidiService midiService)
  {
    throw new UnsupportedOperationException ();
  }
  
  // XXX Note Off / On... XXX TODO
  
  /** Transmits (schedules) a MIDI program change at the MIDI service.
   * 
   * <p>
   * For sub-class use.
   * 
   * <p>
   * The request is silently ignored if this {@link Service} is {@link Status#STOPPED},
   * or if there is no {@link MidiService} available.
   * 
   * @param midiChannel The MIDI channel number, between unity and 16 inclusive.
   * @param patch       The patch (program) number, between zero and 127 inclusive.
   * 
   * @throws IllegalArgumentException If any of the arguments is out of range.
   * 
   * @see MidiService#sendMidiProgramChange
   * 
   */
  protected final void sendMidiProgramChange (final int midiChannel, final int patch)
  {
    synchronized (this)
    {
      if (getStatus () != Status.STOPPED && getMidiService () != null)
        getMidiService ().sendMidiProgramChange (midiChannel, patch);
    }
  }
  
  /** Transmits (schedules) a MIDI control change.
   * 
   * <p>
   * For sub-class use.
   * 
   * <p>
   * The request is silently ignored if this {@link Service} is {@link Status#STOPPED},
   * or if there is no {@link MidiService} available.
   * 
   * @param midiChannel The MIDI channel number, between unity and 16 inclusive.
   * @param controller  The MIDI controller number, between zero and 127 inclusive.
   * @param value       The new value for the controller, between zero and 127 inclusive.
   * 
   * @throws IllegalArgumentException If any of the arguments is out of range.
   * 
   * @see MidiService#sendMidiControlChange
   * 
   */
  protected final void sendMidiControlChange (final int midiChannel, final int controller, final int value)
  {
    synchronized (this)
    {
      if (getStatus () != Status.STOPPED && getMidiService () != null)
        getMidiService ().sendMidiControlChange (midiChannel, controller, value);
    }
  }

  /** Transmits (schedules) a MIDI System Exclusive (SysEx) message.
   * 
   * <p>
   * For sub-class use.
   * 
   * <p>
   * The request is silently ignored if this {@link Service} is {@link Status#STOPPED},
   * or if there is no {@link MidiService} available.
   * 
   * @param vendorId       The vendor ID.
   * @param rawMidiMessage The complete raw MIDI message, non-{@code null}.
   * 
   * @throws IllegalArgumentException If {@code vendorId} is out of range,
   *                                  or if {@code rawMidiMessage} is {@code null}
   *                                     or an illegally formatted SysEx message
   *                                     (for the particular vendor ID).
   * 
   */
  protected final void sendMidiSysEx (final byte vendorId, final byte[] rawMidiMessage)
  {
    synchronized (this)
    {
      if (getStatus () != Status.STOPPED && getMidiService () != null)
        getMidiService ().sendMidiSysEx (vendorId, rawMidiMessage);
    }
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // MIDI DEVICE PARAMETER MAP
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final Map<String, P> parameterMap = new LinkedHashMap<> ();
  
  private final Map<String, D> parameterDescriptorMap = new HashMap<> ();
  
  /** Registers a parameter (for sub-class use only).
   * 
   * <p>
   * This method is not final to allow sub-classes to set up and maintain more
   * refined parameter administrations.
   * Needless to say,
   * sub-class implementations <i>must</i> invoke the super
   * implementation.
   * 
   * <p>
   * The parameter descriptor provides the key name through {@link ParameterDescriptor#getParameterName}.
   * It must be non-{@code null} and non-empty (even when trimmed).
   * 
   * @param parameterDescriptor The parameter descriptor, non-{@code null}.
   * 
   * @throws IllegalArgumentException If either or both argument(s) is {@code null},
   *                                    the parameter name is illegal,
   *                                    or if the parameter (key) is already registered.
   * 
   * @see ParameterDescriptor
   * @see ParameterDescriptor#getParameterName
   * 
   */
  protected void registerParameter (final D parameterDescriptor)
  {
    if (parameterDescriptor == null)
      throw new IllegalArgumentException ();
    final String key = parameterDescriptor.getParameterName ();
    if (key == null || key.trim ().isEmpty () || this.parameterMap.keySet ().contains (key))
      throw new IllegalArgumentException ();
    if (this.parameterDescriptorMap.containsKey (key))
      throw new RuntimeException ();
    synchronized (this.parameterMap)
    {
      this.parameterMap.put (key, null);
      this.parameterDescriptorMap.put (key, parameterDescriptor);
    }
  }
  
  /** Updates a (single) device parameter (for sub-class use only) by virtue of a change on the device.
   * 
   * @param key   The parameter name.
   * @param value The new value, may be {@code null}.
   * 
   * @throws IllegalArgumentException If the key is {@code null} or not registered.
   * 
   * @see #fireParameterChanged
   * 
   */
  protected final void updateParameterFromDevice (final String key, final P value)
  {
    if (key == null || value == null || ! this.parameterMap.containsKey (key))
      throw new IllegalArgumentException ();
    synchronized (this.parameterMap)
    {
      this.parameterMap.put (key, value);
    }
    fireParameterChanged (Collections.singletonMap (key, value));
  }
  
  /** Updates a device parameter (for sub-class use only) by virtue of a change on the device.
   * 
   * @param changes A non-{@code null} {@link Map} holding the names of the parameters that have changed,
   *                mapped onto their new values (which may be {@code null}).
   * 
   * @throws IllegalArgumentException If {@code changes == null}
   *                                  or any key in {@code changes} is {@code null} or not registered.
   * 
   * @see #fireParameterChanged
   * 
   */
  protected final void updateParameterFromDevice (final Map<String, P> changes)
  {
    if (changes == null || changes.containsKey (null))
      throw new IllegalArgumentException ();
    if (! this.parameterMap.keySet ().containsAll (changes.keySet ()))
      throw new IllegalArgumentException ();
    synchronized (this.parameterMap)
    {
      this.parameterMap.putAll (changes);
    }
    fireParameterChanged (changes);
  }
  
  /** Gets the parameter descriptor for given key (for sub-class use).
   * 
   * @param key The key; must be non-{@code null} and in the {@link #keySet}.
   * 
   * @return The {@link ParameterDescriptor} for given key.
   * 
   * @throws IllegalArgumentException If the key is {@code null} or non-existent.
   * 
   */
  protected final D getParameterDescriptor (final String key)
  {
    if (key == null || ! this.parameterDescriptorMap.containsKey (key))
      throw new IllegalArgumentException ();
    return this.parameterDescriptorMap.get (key);
  }
  
  @Override
  public Set<String> keySet ()
  {
    return Collections.unmodifiableSet (this.parameterMap.keySet ());
  }

  @Override
  public P get (final Object key)
  {
    synchronized (this.parameterMap)
    {
      if (key == null)
        throw new NullPointerException ();
      return this.parameterMap.get (key);
    }
  }

  /** Performs basic argument checking and delegates to sub-class {@link #putImpl}.
   * 
   * {@inheritDoc}
   * 
   * @param key   The parameter to change, non-{@code null}.
   * @param value The new value.
   * 
   * @return The old value (mapping), {@code null} if no mapping existed yet.
   * 
   * @see Map#put
   * @see #putImpl
   * 
   */
  @Override
  public final P put (final String key, final P value)
  {
    if (key == null || ! this.parameterMap.containsKey (key))
      throw new IllegalArgumentException ();
    // XXX Shouldn't we compare with the current value.
    return putImpl (key, value);
  }

  /** Sub-class implementation of changing a value on the device.
   * 
   * @param key   The parameter to change, non-{@code null}.
   * @param value The new value.
   * 
   * @return The old value (mapping), {@code null} if no mapping existed yet.
   * 
   * @see Map#put
   * @see #put
   * 
   */
  protected abstract P putImpl (final String key, final P value);
  
  @Override
  public final int size ()
  {
    return this.parameterMap.size ();
  }

  @Override
  public final boolean isEmpty ()
  {
    return this.parameterMap.isEmpty ();
  }

  @Override
  public final boolean containsKey (final Object o)
  {
    if (o == null)
      throw new NullPointerException ();
    else
      return this.parameterMap.containsKey (o);
  }

  @Override
  public final boolean containsValue (final Object o)
  {
    return this.parameterMap.containsValue (o);
  }

  /** Throws {@link UnsupportedOperationException}.
   * 
   * @param o The object to remove.
   * @return  Never.
   * 
   * @throws UnsupportedOperationException Always.
   * 
   */
  @Override
  public final P remove (final Object o)
  {
    throw new UnsupportedOperationException ();
  }

  @Override
  public void putAll (Map map)
  {
    throw new UnsupportedOperationException ("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  /** Throws {@link UnsupportedOperationException}.
   * 
   * @throws UnsupportedOperationException Always.
   * 
   */
  @Override
  public final void clear ()
  {
    throw new UnsupportedOperationException ();
  }

  @Override
  public Collection values ()
  {
    throw new UnsupportedOperationException ("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public final Set entrySet ()
  {
    return Collections.unmodifiableSet (this.parameterMap.entrySet ());
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // MIDI SERVICE LISTENER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final MidiServiceListener midiServiceListener = new DefaultMidiServiceListener ()
  {
    
    @Override
    public void midiRxNoteOff (final int midiChannel, final int note, final int velocity)
    {
      AbstractMidiDevice.this.onMidiRxNoteOff (midiChannel, note, velocity);
    }
    
    @Override
    public void midiRxNoteOn (final int midiChannel, final int note, final int velocity)
    {
      AbstractMidiDevice.this.onMidiRxNoteOn (midiChannel, note, velocity);
    }

    @Override
    public void midiRxProgramChange (final int midiChannel, final int patch)
    {
      AbstractMidiDevice.this.onMidiRxProgramChange (midiChannel, patch);
    }

    @Override
    public void midiRxControlChange (final int midiChannel, final int controller, final int value)
    {
      AbstractMidiDevice.this.onMidiRxControlChange (midiChannel, controller, value);
    }

    @Override
    public void midiRxSysEx (final byte vendorId, final byte[] rawMidiMessage)
    {
      AbstractMidiDevice.this.onMidiRxSysEx (vendorId, rawMidiMessage);
    }
    
  };
  
  /** Invoked when a MIDI Note Off message has been received from the {@link MidiService}.
   * 
   * <p>
   * For sub-class use.
   * This implementation does nothing.
   * 
   * @param midiChannel The MIDI channel number, between unity and 16 inclusive.
   * @param note        The note, between zero and 127 inclusive.
   * @param velocity    The velocity, between zero and 127 inclusive.
   * 
   */
  protected void onMidiRxNoteOff (final int midiChannel, final int note, final int velocity)
  {
  }
  
  /** Invoked when a MIDI Note On message has been received from the {@link MidiService}.
   * 
   * <p>
   * For sub-class use.
   * This implementation does nothing.
   * 
   * @param midiChannel The MIDI channel number, between unity and 16 inclusive.
   * @param note        The note, between zero and 127 inclusive.
   * @param velocity    The velocity, between zero and 127 inclusive.
   * 
   */
  protected void onMidiRxNoteOn (final int midiChannel, final int note, final int velocity)
  {
  }
  
  /** Invoked when a MIDI Program Change message has been received from the {@link MidiService}.
   * 
   * <p>
   * For sub-class use.
   * This implementation does nothing.
   * 
   * @param midiChannel The MIDI channel number, between unity and 16 inclusive.
   * @param patch       The patch (program) number, between zero and 127 inclusive.
   * 
   */
  protected void onMidiRxProgramChange (final int midiChannel, final int patch)
  {
  }
  
  /** Invoked when a MIDI Control Change message has been received from the {@link MidiService}.
   * 
   * <p>
   * For sub-class use.
   * This implementation does nothing.
   * 
   * @param midiChannel The MIDI channel number, between unity and 16 inclusive.
   * @param controller  The MIDI controller number, between zero and 127 inclusive.
   * @param value       The value for the controller, between zero and 127 inclusive.
   * 
   */
  protected void onMidiRxControlChange (final int midiChannel, final int controller, final int value)
  {
  }
  
  /** Invoked when a MIDI System Exclusive message has been received from the {@link MidiService}.
   * 
   * <p>
   * For sub-class use.
   * This implementation does nothing.
   * 
   * @param vendorId       The vendor ID.
   * @param rawMidiMessage The complete raw MIDI message, non-{@code null}.
   *
   */
  protected void onMidiRxSysEx (final byte vendorId, final byte[] rawMidiMessage)
  {  
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}

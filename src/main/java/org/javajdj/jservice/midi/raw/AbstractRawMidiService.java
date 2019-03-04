package org.javajdj.jservice.midi.raw;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Logger;
import org.javajdj.jservice.AbstractService;

/** Partial implementation of a {@link RawMidiService}.
 * 
 * <p>
 * This base class takes care of administering and notifying {@link RawMidiServiceListener}s.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public abstract class AbstractRawMidiService
  extends AbstractService
  implements RawMidiService
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (AbstractRawMidiService.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTORS / FACTORIES / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Constructs a raw MIDI service with name {@code "No Name"}.
   * 
   */
  public AbstractRawMidiService ()
  {
    this ("No Name");
  }

  /** Constructs a raw MIDI service with given name.
   * 
   * @param name The name of the service.
   * 
   * @see #setName
   * 
   */
  public AbstractRawMidiService (final String name)
  {
    super (name);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // RAW MIDI SERVICE LISTENERS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final Set<RawMidiServiceListener> rawMidiServiceListeners = new LinkedHashSet<> ();
  
  private final Object rawMidiServiceListenersLock = new Object ();
  
  @Override
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

  @Override
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
   * @see #addRawMidiServiceListener
   * @see #removeRawMidiServiceListener
   * 
   */
  protected final void fireRawMidiMessageTx (final byte[] message)
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
   * @see #addRawMidiServiceListener
   * @see #removeRawMidiServiceListener
   * 
   */
  protected final void fireRawMidiMessageRx (final byte[] message)
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

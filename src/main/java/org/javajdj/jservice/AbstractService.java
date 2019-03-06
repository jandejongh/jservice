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
package org.javajdj.jservice;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Abstract base class providing a partial implementation of {@link Service}.
 * 
 * <p>
 * This base implementation takes care of {@link StatusListener} maintenance,
 * and notification of status changes upon request by sub-classes.
 * It does <i>not</i> maintain the {@link Status} itself
 * (as exposed by {@link #getStatus}.
 * 
 * <p>
 * The concurrency strategy in this class is to lock the entire object while setting its {@link Status}.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public abstract class AbstractService
  implements Service
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (AbstractService.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTORS / FACTORIES / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Constructs a service with given name.
   * 
   * @param name The service name, non-{@code null}.
   * 
   * @throws IllegalArgumentException If {@code name == null}.
   * 
   * @see Service#setName
   * 
   */
  public AbstractService (final String name)
  {
    if (name == null)
      throw new IllegalArgumentException ();
    this.name = name;
  }

  /** Constructs a service with "No Name" as its name.
   * 
   */
  public AbstractService ()
  {
    this ("No Name");
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SETTINGS LISTENERS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final Set<PropertyChangeListener> settingsListeners = new LinkedHashSet<> ();
  
  private final Object settingsListenersLock = new Object ();
  
  private volatile Set<PropertyChangeListener> settingsListenersCopy = new LinkedHashSet<> ();
  
  @Override
  public final void addSettingsListener (final PropertyChangeListener l)
  {
    synchronized (this.settingsListenersLock)
    {
      if (l != null && ! this.settingsListeners.contains (l))
      {
        this.settingsListeners.add (l);
        this.settingsListenersCopy = new LinkedHashSet<> (this.settingsListeners);
      }
    }
  }
  
  @Override
  public final void removeSettingsListener (final PropertyChangeListener l)
  {
    synchronized (this.settingsListenersLock)
    {
      if (this.settingsListeners.remove (l))
        this.settingsListenersCopy = new LinkedHashSet<> (this.settingsListeners);
    }
  }
  
  /** Fires a property-change event to registered listeners.
   * 
   * @param pce The event; log warning if {@code null}.
   * 
   * @see #addSettingsListener
   * @see PropertyChangeEvent
   * @see PropertyChangeListener
   * 
   */
  protected final void fireSettingsChanged (final PropertyChangeEvent pce)
  {
    if (pce != null)
    {
      // References are atomic.
      final Set<PropertyChangeListener> listeners = this.settingsListenersCopy;
      for (final PropertyChangeListener l : listeners)
        l.propertyChange (pce);
    }
    else
      LOG.log (Level.SEVERE, "Attempt to fire a null PropertyChangeEvent; ignored!");
  }
  
  /** Fires a property-change event to registered listeners.
   * 
   * @param propertyName The name of the property.
   * @param oldValue     The old value of the property.
   * @param newValue     The new value of the property.
   * 
   * @see #addSettingsListener
   * @see PropertyChangeEvent
   * @see PropertyChangeListener
   * 
   */
  protected final void fireSettingsChanged (final String propertyName, final Object oldValue, final Object newValue)
  {
    final PropertyChangeEvent pce = new PropertyChangeEvent (this, propertyName, oldValue, newValue);
    fireSettingsChanged (pce);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // STATUS LISTENERS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final Set<StatusListener> statusListeners = new LinkedHashSet<> ();

  private final Object statusListenersLock = new Object ();
  
  private Set<StatusListener> statusListenersCopy = new LinkedHashSet<> ();
  
  @Override
  public final void addStatusListener (final StatusListener l)
  {
    synchronized (this.statusListenersLock)
    {
      if (l != null && ! this.statusListeners.contains (l))
      {
        this.statusListeners.add (l);
        this.statusListenersCopy = new LinkedHashSet<> (this.statusListeners);
      }
    }
  }

  @Override
  public final void removeStatusListener (final StatusListener l)
  {
    synchronized (this.statusListenersLock)
    {
      if (this.statusListeners.remove (l))
        this.statusListenersCopy = new LinkedHashSet<> (this.statusListeners);
    }
  }
  
  @Override
  public final void removeStatusListeners ()
  {
    synchronized (this.statusListenersLock)
    {
      this.statusListeners.clear ();
      this.statusListenersCopy = new LinkedHashSet<> ();
    }
  }

  /** Returns a copy of the set holding the status listeners.
   * 
   * <p>
   * Internally, this class maintains a copy of the status listeners.
   * It uses that copy in order to avoid concurrency issues (references are atomic).
   * If the set of status listeners changes, the entire copy is replaced (instead of modifying the set).
   * 
   * @return A copy of the set holding the status listeners; the set is not to be modified.
   * 
   */
  protected final Set<StatusListener> getStatusListeners ()
  {
    return this.statusListenersCopy;
  }
  
  /** Fires a status changed event to registered status listeners.
   * 
   * @param oldStatus The old status, may be {@code null}.
   * @param newStatus The new status, non-{@code null}.
   * 
   * @throws IllegalArgumentException If {@code newStatus == null}.
   * 
   */
  protected final void fireStatusChanged (final Status oldStatus, final Status newStatus)
  {
    if (newStatus == null)
      throw new IllegalArgumentException ();
    if (newStatus.equals (oldStatus))
    {
      LOG.log (Level.WARNING, "Sub-class requests status change on {0},"
        + "but newStatus equals oldStatus;"
        + "ignored!",
        this);
      return;
    }
    // References are atomic.
    final Set<StatusListener> listeners = this.statusListenersCopy;
    for (final StatusListener l : listeners)
      l.onStatusChange (this, oldStatus, newStatus);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SERVICE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private volatile Service.Status serviceStatus = Status.STOPPED;
  
  @Override
  public final synchronized Status getStatus ()
  {
    return this.serviceStatus;
  }
  
  /** Changes the service status (from a sub-class).
   * 
   * <p>
   * The implementation stores the {@code newServiceStatus} value for later
   * use in notifications.
   * Sub-classes must therefore always invoke this method for status changes.
   * 
   * <p>
   * If {@code newServiceStatus} differs from the internally cached value,
   * {@link #fireStatusChanged} is invoked (and the new value is stored).
   * 
   * @param newServiceStatus The new service status.
   * 
   * @throws IllegalArgumentException If {@code newServiceStatus == null}.
   * 
   */
  protected final synchronized void setStatus (final Status newServiceStatus)
  {
    if (newServiceStatus == null)
      throw new IllegalArgumentException ();
    final Status oldServiceStatus = this.serviceStatus;
    this.serviceStatus = newServiceStatus;
    if (! this.serviceStatus.equals (oldServiceStatus))
      fireStatusChanged (oldServiceStatus, this.serviceStatus);
  }
  
  /** Enters the {@link Status#ERROR} state (from a sub-class).
   * 
   * <p>
   * The implementation logs a warning and notifies the status listeners.
   * 
   */
  protected synchronized void error ()
  {
    if (getStatus () == Status.ERROR)
      return;
    LOG.log (Level.WARNING, "Service {0} enters ERROR state!", this);
    setStatus (Status.ERROR);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // NAME / toString
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /** The property name of the "name" property, viz., "name" :-).
   * 
   */
  public final static String NAME_PROPERTY_NAME = "name";
  
  private volatile String name = "No Name";
  
  @Override
  public final /* synchronized */ String getName ()
  {
    // Jan de Jongh, 20181129.
    // This method is NOT synchronized on purpose, as object references are written atomically.
    // Having this method synchronized led to deadlock in some specific case, so we removed it.
    // Requiring a lock on the complete object is indeed overkill here.
    return this.name;
  }

  @Override
  public final synchronized void setName (final String name)
  {
    if (name == null || name.trim ().isEmpty ())
      throw new IllegalArgumentException ();
    if (! this.name.equals (name))
    {
      final String oldName = this.name;
      this.name = name;
      fireSettingsChanged (NAME_PROPERTY_NAME, oldName, this.name);
    }
  }

  /** Overridden to return the name of the {@link Service}
   * 
   * @return The result of {@link #getName}.
   * 
   */
  @Override
  public /* synchronized */ String toString ()
  {
    // Jan de Jongh, 20181129: See comment above with getName.
    // Note that toString is often used in logging; not really an excellent idea to require a lock on the complete object here.
    return getName ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}

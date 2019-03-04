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

import java.beans.PropertyChangeListener;

/** A stupid-simple interface to a service; a possibly active object that can be started and stopped
 *  and that is expected  to expose its status and notify registered listeners of status changes.
 * 
 * <p>
 In addition to being stopped or active, a service can be in a third state representing a failure to startService or failure
 to continue normal operation.
 Whether or not the service tries to recover from such failures is not part of the service contract.
 
 <p>
 * Implementation must notify registered listeners of status changes.
 * Registration and un-registration of status listeners ({@link StatusListener})
 * is done through {@link #addStatusListener} and {@link #removeStatusListener}
 * (or even {@link #removeStatusListeners}, removing <i>all</i> status listeners).
 * 
 * <p>
 * Implementations of {@link Service} must be thread-safe with respect to service status operations
 * (like {@link #startService}).
 * 
 * <p>
 * In addition to the mandatory reporting of changes in {@link Status},
 * implementations of {@link Service} are strongly encouraged
 * to report changes to (relevant) non-status properties in Java {@code Beans} style,
 * see {@link #addSettingsListener} and {@link #removeSettingsListener}.
 * 
 * <p>
 * Some methods like {@link #toggleService} have a default implementation in the {@link Service} interface.
 * These implementations use the default locking model, i.e., they lock on the {@link Object} implementing
 * the interface for synchronization.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public interface Service
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // STATUS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** The possible status value of a {@link Service}.
   * 
   * <p>
   * A {@link Service} must have {@link Status#STOPPED} status (immediately) after construction.
   * 
   */
  public enum Status
  {
    /** The service has stopped upon user request (or has not been started yet).
     * 
     */
    STOPPED,
    /** The service has started and is functioning properly.
     * 
     */
    ACTIVE,
    /** The service has started but is in error and cannot continue operations.
     * 
     * <p>
     * Whether or not the service tries to recover from such failures is not part of the service contract.
     * 
     */
    ERROR
  }
  
  /** Returns the {@link Status} of this service.
   * 
   * <p>
   * The service {@link Status} obtained through this method is obviously just a snapshot,
   * and hence unreliable unless specific (implementation-dependant) locking is in effect.
   * 
   * @return The service {@link Status}, non-{@code null}.
   * 
   */
  Status getStatus ();

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // STATUS LISTENER[S]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** A listener to status changes.
   * 
   */
  @FunctionalInterface
  public interface StatusListener
  {
    
    /** Informs the listener of a (completed) status change.
     * 
     * @param service   The service that changed status
     *                    and at which the {@link StatusListener} has been registered.
     * @param oldStatus The old status, may be {@code null} if no previous state was available (e.g., while initializing).
     * @param newStatus The new status, non-{@code null}.
     * 
     */
    void onStatusChange (Service service, Status oldStatus, Status newStatus);
    
  }

  /** Adds a {@link StatusListener}.
   * 
   * @param l The status listener, {@code null} listeners of listeners already registered are ignored,
   * 
   */
  void addStatusListener(StatusListener l);

  /** Removes a {@link StatusListener}.
   * 
   * @param l The status listener, {@code null} listeners of listeners not registered are ignored,
   * 
   */
  void removeStatusListener (StatusListener l);
  
  /** Removes all {@link StatusListener}s.
   * 
   * <p>
   * This method is <i>really not</i> for general use, but required for the default implementation of {@link #destroyService}.
   * 
   */
  void removeStatusListeners ();
 
  /** Adds a settings (property-change) listener.
   * 
   * @param l The listener, ignored if {@code null} or already present as listener.
   * 
   */
  void addSettingsListener (PropertyChangeListener l);
  
  /** Removes a settings (property-change) listener.
   * 
   * @param l The listener, ignored if {@code null} or not present as listener.
   * 
   */
  void removeSettingsListener (PropertyChangeListener l);
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // NAME
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Gets the name of this service.
   * 
   * @return The name of this service, non-{code null}.
   * 
   */
  String getName ();
  
  /** Sets the name of this service.
   * 
   * @param name The new name of this service, non-{@code null}.
   * 
   * @throws IllegalArgumentException If {@code name} is {@code null}.
   * 
   */
  void setName (String name);
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SERVICE OPERATIONS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Starts the service.
   * 
   * <p>
   * Implementations must make sure this method is thread-safe,
   * and must ignore invocation of the method if the service status ({@link #getStatus})
   * is (already) {@link Status#ACTIVE}.
   * 
   * @see Status#ACTIVE
   * 
   */
  void startService ();
  
  /** Stops the service.
   * 
   * <p>
   * Implementations must make sure this method is thread-safe,
   * and must ignore invocation of the method if the service status ({@link #getStatus})
   * is (already) {@link Status#STOPPED}.
   * 
   * @see Status#STOPPED
   * 
   */
  void stopService ();
  
  /** Forcibly restarts the service.
   * 
   * <p>
   * Unlike {@link #startService},
   * which ignores its invocation when the service is already having status {@link Status#ACTIVE},
   * this method always invokes {@link #stopService} first,
   * and then {@link #startService}.
   * Both invocations must be protected by proper synchronization.
   * The default implementation locks on the {@link Object} implementing the interface.
   * 
   */
  default void restartService ()
  {
    synchronized (this)
    {
      stopService ();
      startService ();
    }
  }
  
  /** Atomically switches the service status between {@link Status#ACTIVE} and {@link Status#STOPPED}.
   * 
   * <p>
   * If the current status is {@link Status#ERROR},
   * the service is (must be) stopped.
   * 
   * <p>
   * The default implementation locks on the {@link Object} implementing the interface.
   * 
   * @see #startService
   * @see #stopService
   * 
   */
  default void toggleService ()
  {
    synchronized (this)
    {
      switch (getStatus ())
      {
        case STOPPED:
          startService ();
          break;
        case ACTIVE:
          stopService ();
          break;
        case ERROR:
          stopService ();
          break;
        default:
          throw new RuntimeException ();
      }
    }
  }
  
  /** Destroys the service and, typically, the {@link Object} implementing it.
   * 
   * <p>
   * Optional operation.
   * 
   * <p>
   * The destruction of a {@link Service} object inhibits future use of that object as a {@link Service},
   * and actually indicates that the entire {@link Object} is to be prepared for garbage collection.
   * After invocation of {@link #destroyService}, methods like {@link #startService} and {@link #stopService} should fail,
   * and may even throw an {@link Exception}.
   * 
   * <p>
   * The operation is optional, and implementations are highly recommended to document to what extent and
   * how the operation is implemented.
   * If destruction of the {@link Service} is not (fully) supported,
   * an {@link UnsupportedOperationException} should be thrown.
   * 
   * <p>
   * The default implementation removes all {@link StatusListener}s
   * and invokes {@link #stopService} in a single {@code synchronized} block (thus locking on the {@link Service} object).
   * 
   * <p>
   * Implementations must remove all {@link StatusListener}s, assisting in garbage collection,
   * and must <i>not</i> notify these listeners beforehand.
   * 
   * @throws UnsupportedOperationException If destruction of the {@link Service} is not implemented.
   * 
   * @see #removeStatusListeners
   * @see #stopService
   * 
   */
  default void destroyService () throws UnsupportedOperationException
  {
    synchronized (this)
    {
      removeStatusListeners ();
      stopService ();
    }
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}

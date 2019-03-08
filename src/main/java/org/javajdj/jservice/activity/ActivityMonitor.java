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
package org.javajdj.jservice.activity;

import org.javajdj.jservice.Service;

/** A {@link Service} that monitors a target {@link ActivityMonitorable} for changes
 *  in its activity statuses.
 * 
 * <p>
 * Being a {@link Service}, the activity monitoring of a {@link ActivityMonitor} can be enabled and disabled
 * through its {@link #startService} and {@link #stopService}, respectively.
 * 
 * <p>
 * Notifications of changes in the activities being monitored are sent to registered {@link Listener}s.
 * These are extended {@link StatusListener}
 * and should therefore be registered and unregistered through the service's {@link StatusListener} management methods,
 * viz., {@link #addStatusListener} and {@link #removeStatusListener}.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 * @see ActivityMonitorable
 * 
 */
public interface ActivityMonitor
  extends Service
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LISTENER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** A listener for changes in the activities being monitored.
   * 
   * @see #addStatusListener
   * @see #removeStatusListener
   * 
   */
  interface Listener extends StatusListener
  {
    
    /** Notifies that a monitored activity has changed status.
     * 
     * @param activity          The activity, may be {@link null}.
     * @param newActivityStatus The new activity status.
     * 
     * @see ActivityMonitorable#getMonitorableActivities
     * 
     */
    void activityChanged (String activity, boolean newActivityStatus);
    
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // ActivityMonitorable
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** The object for which activities are being monitored.
   * 
   * <p>
   * The object being monitored is non-{@code null} and set upon construction (i.e., it cannot change).
   * 
   * @return The object for which activities are being monitored.
   * 
   */
  ActivityMonitorable getActivityMonitorable ();
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // ACTIVITY CHECK INTERVAL
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  final static long DEFAULT_ACTIVITY_CHECK_INTERVAL_MS = 100L;
  
  /** Returns the duration between checks in activity changes.
   * 
   * @return The duration (in milli-seconds) between checks in activity changes.
   * 
   */
  long getActivityCheckInterval_ms ();
  
  /** Sets the duration between checks in activity changes.
   * 
   * @param activityCheckInterval_ms The new duration (in milli-seconds) between checks in activity changes.
   * 
   * @throws IllegalArgumentException If the interval is zero or negative.
   * 
   */
  void setActivityCheckInterval_ms (long activityCheckInterval_ms);

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // ACTIVITY TIMEOUT
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  final static long DEFAULT_ACTIVITY_TIMEOUT_MS = 250L;
  
  /** Returns the activity timeout.
   * 
   * <p>
   * The activity timeout defines the (minimum) time an activity is kept active by this object.
   * This is typically used in user-interfaces to ensure that activity indicators light up long enough.
   * 
   * <p>
   * Note that implementations are free to only check for (new) activity at intervals defined by
   * {@link #getActivityCheckInterval_ms}, i.e., the {@link #getActivityTimeout} does not have to be exact.
   * 
   * @return The activity timeout in milli-seconds.
   * 
   */
  long getActivityTimeout_ms ();
  
  /** Sets the activity timeout.
   * 
   * @param activityTimeout_ms The new activity timeout, in milli-seconds.
   * 
   * @throws IllegalArgumentException If the timeout is (strictly) negative.
   * 
   * @see #getActivityTimeout_ms
   * 
   */
  void setActivityTimeout_ms (long activityTimeout_ms);
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // isActive
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Returns whether activity identified by given {@code String} is active on the {@link ActivityMonitorable}.
   * 
   * @param activity The activity {@code String}, may be {@code null}.
   * 
   * @return True if and only if the {@link Service} is {@link Status#ACTIVE},
   *         given {@code String} is {@code null} or an existing activity on the
   *         embedded {@link ActivityMonitorable},
   *         and that particular activity (non-{@code null} argument)
   *         or the {@link ActivityMonitorable} as a whole ({@code null} argument)
   *         is currently <i>active</i>.
   * 
   * @see ActivityMonitorable#getMonitorableActivities
   * 
   */
  boolean isActive (String activity);
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}

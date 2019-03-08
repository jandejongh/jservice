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

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.javajdj.jservice.Service;
import org.javajdj.jservice.support.Service_FromMix;

/** Default implementation of an {@link ActivityMonitor}
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class DefaultActivityMonitor
  extends Service_FromMix
  implements ActivityMonitor
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (DefaultActivityMonitor.class.getName ());

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTORS / FACTORIES / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static Set<String> createMonitoredActivities
    (ActivityMonitorable activityMonitorable, final Set<String> monitoredActivitiesSupplied, final boolean removeNull)
  {
    final Set<String> monitoredActivities;
    if (monitoredActivitiesSupplied != null)
    {
      monitoredActivities = new LinkedHashSet<> (monitoredActivitiesSupplied);
      monitoredActivities.retainAll (activityMonitorable.getMonitorableActivities ());
    }
    else
      monitoredActivities = new LinkedHashSet<> (activityMonitorable.getMonitorableActivities ());
    if (removeNull)
      monitoredActivities.remove (null);
    return monitoredActivities;
  }
  
  /** Constructs a {@link Service} that monitors given {@code Set} of activities on an {@link ActivityMonitorable}.
   * 
   * <p>
   * Activities that do not exist on the target object are silently ignored.
   * 
   * @param activityMonitorable The object on which to monitor activities, non-{@code null}.
   * @param monitoredActivities The activities to monitor, non-{@code null}.
   * 
   * @see ActivityMonitorable#getMonitorableActivities
   * 
   */
  public DefaultActivityMonitor (final ActivityMonitorable activityMonitorable, final Set<String> monitoredActivities)
  {
    super ();
    if (activityMonitorable == null || monitoredActivities == null)
      throw new IllegalArgumentException ();
    this.activityMonitorable = activityMonitorable;
    this.monitoredActivities = new LinkedHashSet<> (monitoredActivities);
    this.monitoredActivities.retainAll (this.activityMonitorable.getMonitorableActivities ());
    addRunnable (this.activityMonitorRunnable);
  }
  
  /** Constructs a {@link Service} that monitors all activities on an {@link ActivityMonitorable},
   *  except monitoring the entire object "as a whole".
   * 
   * <p>
   * Note that if the target object reports activity for the {@code null} {@code String},
   * the current object will exclude {@code null} in the monitored activities.
   * 
   * @param activityMonitorable The object on which to monitor (non-{@code null}) activities, non-{@code null}.
   * 
   * @see ActivityMonitorable#getMonitorableActivities
   * 
   */
  public DefaultActivityMonitor (final ActivityMonitorable activityMonitorable)
  {
    this (activityMonitorable, createMonitoredActivities (activityMonitorable, null, true));
  }
  
  /** Constructs a {@link Service} that monitors all activities on an {@link ActivityMonitorable},
   *  optionally except monitoring the entire object "as a whole".
   * 
   * <p>
   * Note that if the target object reports activity for the {@code null} {@code String},
   * the current object will include {@code null} in the monitored activities and
   * report.
   * If target object does <i>not</i>, the {@code removeNull} argument has no effect.
   * 
   * @param activityMonitorable The object on which to monitor activities, non-{@code null}.
   * @param removeNull          Whether to remove {@code null} as activity {@code String} to monitor.
   * 
   * @see ActivityMonitorable#getMonitorableActivities
   * 
   */
  public DefaultActivityMonitor (final ActivityMonitorable activityMonitorable, final boolean removeNull)
  {
    this (activityMonitorable, createMonitoredActivities (activityMonitorable, null, removeNull));
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LISTENERS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Notifies (appropriate) status listeners of a detected activity change.
   * 
   * @param monitorableActivity The activity monitored, may be {@code null}.
   * @param newActivityStatus   The new activity status.
   * 
   * @see #getStatusListeners
   * @see Listener
   * 
   */
  protected final void fireActivityChanged (final String monitorableActivity, final boolean newActivityStatus)
  {
    for (final StatusListener l : getStatusListeners ())
      if (l instanceof ActivityMonitor.Listener)
      {
        ((ActivityMonitor.Listener) l).activityChanged (monitorableActivity, newActivityStatus);
      }
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // ActivityMonitorable
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final ActivityMonitorable activityMonitorable;
  
  @Override
  public final ActivityMonitorable getActivityMonitorable ()
  {
    return this.activityMonitorable;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // MONITORED ACTIVITIES
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  final Set<String> monitoredActivities;
  
  @Override
  public final Set<String> getMonitoredActivities ()
  {
    return new LinkedHashSet<> (this.monitoredActivities);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // ACTIVITY CHECK INTERVAL
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private volatile long activityCheckInterval_ms = ActivityMonitor.DEFAULT_ACTIVITY_CHECK_INTERVAL_MS;
  
  @Override
  public final synchronized long getActivityCheckInterval_ms ()
  {
    return this.activityCheckInterval_ms;
  }

  @Override
  public final synchronized void setActivityCheckInterval_ms (final long activityCheckInterval_ms)
  {
    if (activityCheckInterval_ms <= 0)
      throw new IllegalArgumentException ();
    this.activityCheckInterval_ms = activityCheckInterval_ms;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // ACTIVITY TIMEOUT
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private volatile long activityTimeout_ms = ActivityMonitor.DEFAULT_ACTIVITY_TIMEOUT_MS;
  
  @Override
  public final synchronized long getActivityTimeout_ms ()
  {
    return this.activityTimeout_ms;
  }

  @Override
  public final synchronized void setActivityTimeout_ms (final long activityTimeout_ms)
  {
    if (activityTimeout_ms < 0)
      throw new IllegalArgumentException ();
    this.activityTimeout_ms = activityTimeout_ms;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // ACTIVITY MONITOR RUNNABLE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final Runnable activityMonitorRunnable = () ->
  {
    final ActivityMonitorable activityMonitorable = DefaultActivityMonitor.this.getActivityMonitorable ();
    LOG.log (Level.INFO, "Starting (Default) Activity Monitor {0} on {1}.",
      new Object[]{DefaultActivityMonitor.this, activityMonitorable});
    try
    {
      final Map<String, Boolean> activityStatus = new LinkedHashMap<>();
      final Set<String> toNotify = new LinkedHashSet<> ();
      while (! Thread.interrupted ())
      {
        final long activityTimeout_ms;
        synchronized (DefaultActivityMonitor.this)
        {
          activityTimeout_ms = DefaultActivityMonitor.this.getActivityTimeout_ms ();
        }
        toNotify.clear ();
        final Instant now = Instant.now ();
        for (final String activity: DefaultActivityMonitor.this.monitoredActivities)
        {
          boolean isActive;
          final Duration duration = Duration.between (activityMonitorable.lastActivity (activity), Instant.now ());
          try
          {
            isActive = duration.toMillis () <= activityTimeout_ms;
          }
          catch (ArithmeticException ae)
          {
            isActive = false;
          }
          if ((! activityStatus.containsKey (activity)) || activityStatus.get (activity) != isActive)
          {
            activityStatus.put (activity, isActive);
            toNotify.add (activity);
          }
        }
        for (final String activityToNotify : toNotify)
          DefaultActivityMonitor.this.fireActivityChanged (activityToNotify, activityStatus.get (activityToNotify));
        Thread.sleep (DefaultActivityMonitor.this.getActivityCheckInterval_ms ());
      }
    }
    catch (InterruptedException ie)
    {
      // EMPTY
    }
    for (final String activity: activityMonitorable.getMonitorableActivities ())
      DefaultActivityMonitor.this.fireActivityChanged (activity, false);
    LOG.log (Level.INFO, "Terminating (Default) Activity Monitor {0} on {1}.",
      new Object[]{DefaultActivityMonitor.this, activityMonitorable});
  };
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // isActive
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public final synchronized boolean isActive (final String activity)
  {
    if (getStatus () != Status.ACTIVE)
      return false;
    if (! getMonitoredActivities ().contains (activity))
      return false;
    final ActivityMonitorable activityMonitorable = DefaultActivityMonitor.this.getActivityMonitorable ();
    try
    {
      final Instant instantActivity = activityMonitorable.lastActivity (activity);
      final Instant now = Instant.now ();
      final Duration duration = Duration.between (instantActivity, now);
      return duration.toMillis () <= DefaultActivityMonitor.this.activityTimeout_ms;
    }
    catch (ArithmeticException ae)
    {
      return false;
    }
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}

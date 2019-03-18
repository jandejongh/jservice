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
package org.javajdj.jservice.activity.swing;

import java.util.Map;
import java.util.function.Function;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import org.javajdj.jservice.Service;
import org.javajdj.jservice.activity.ActivityMonitor;
import org.javajdj.swing.JColorCheckBox;

/** A check box that visualizes an {@link ActivityMonitor}.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JActivityMonitor
  extends JColorCheckBox.JBoolean
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JActivityMonitor.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTORS / FACTORIES / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Constructs the component with given color function.
   * 
   * <p>
   * If the monitor is {@code null}, the component will never show activity.
   * 
   * <p>
   * If the color function is null, the default function from {@link JBoolean} is used.
   * 
   * @param activityMonitor The activity monitor to use, may be {@code null}.
   * @param activity        The activity on the monitor to visualize, may be {@code null}.
   * @param colorFunction   The color function,
   *                          see {@link JBoolean#JBoolean(java.util.function.Function)}.
   * 
   */
  public JActivityMonitor
  ( final ActivityMonitor activityMonitor,
    final String activity,
    final Function<java.lang.Boolean, java.awt.Color> colorFunction)
  {
    super (colorFunction);
    this.activityMonitor = activityMonitor;
    this.activity = activity;
    if (this.activityMonitor != null)
      this.activityMonitor.addStatusListener (this.activityMonitorListener);
    setEnabled (false);
    updateActivityStatus ();
  }
  
  /** Constructs the component with given color map.
   * 
   * <p>
   * If the monitor is {@code null}, the component will never show activity.
   * 
   * <p>
   * If the color map is null, the default color mapping from {@link JBoolean} is used.
   * 
   * @param activityMonitor The activity monitor to use, may be {@code null}.
   * @param activity        The activity on the monitor to visualize, may be {@code null}.
   * @param colorMap        The color map,
   *                          see {@link JBoolean#JBoolean(java.util.Map)}.
   * 
   */
  public JActivityMonitor
  ( final ActivityMonitor activityMonitor,
    final String activity,
    final Map<java.lang.Boolean, java.awt.Color> colorMap)
  {
    this (activityMonitor, activity, colorMap == null ? null : (java.lang.Boolean b) -> colorMap.get (b));
  }
  
  /** The default color function.
   * 
   * <p>
   * The default function uses {@link java.awt.Color#green} to indicate activity, and no color for inactivity.
   * 
   * <p>
   * Note that the default deviates from the default used in {@link JBoolean}.
   * 
   * @see JActivityMonitor#JActivityMonitor(org.javajdj.jservice.activity.ActivityMonitor, java.lang.String)
   * 
   */
  public final static Function<java.lang.Boolean, java.awt.Color> DEFAULT_COLOR_FUNCTION =
    (java.lang.Boolean t) -> (t != null && t) ? java.awt.Color.green : null;
  
  /** Constructs the component with default color map.
   * 
   * <p>
   * If the monitor is {@code null}, the component will never show activity.
   * 
   * @param activityMonitor The activity monitor to use, may be {@code null}.
   * @param activity        The activity on the monitor to visualize, may be {@code null}.
   * 
   * @see JActivityMonitor#DEFAULT_COLOR_FUNCTION
   * 
   */
  public JActivityMonitor (final ActivityMonitor activityMonitor, final String activity)
  {
    this (activityMonitor, activity, JActivityMonitor.DEFAULT_COLOR_FUNCTION);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // ACTIVITY MONITOR
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private volatile ActivityMonitor activityMonitor = null;
  
  /** Returns the activity monitor.
   * 
   * @return The activity monitor; may be {@code null}.
   * 
   */
  public final synchronized ActivityMonitor getActivityMonitor ()
  {
    return this.activityMonitor;
  }
  
  /** Sets the activity monitor.
   * 
   * @param activityMonitor The new activity monitor; may be {@code null}.
   * 
   */
  
  public final synchronized void setActivityMonitor (final ActivityMonitor activityMonitor)
  {
    if (activityMonitor != this.activityMonitor)
    {
      if (this.activityMonitor != null)
      {
        this.activityMonitor.removeStatusListener (this.activityMonitorListener);
      }
      this.activityMonitor = activityMonitor;
      if (this.activityMonitor != null)
      {
        this.activityMonitor.addStatusListener (this.activityMonitorListener);
      }
      updateActivityStatus ();
    }
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // ACTIVITY
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private volatile String activity;
  
  /** Returns the activity being monitored.
   * 
   * @return The activity being monitored; may be {@code null}.
   * 
   */
  public final synchronized String getActivity ()
  {
    return this.activity;
  }
  
  /** Sets the activity being monitored.
   * 
   * @param activity The new activity being monitored; may be {@code null}.
   * 
   */
  public final synchronized void setActivity (final String activity)
  {
    this.activity = activity;
    updateActivityStatus ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // ACTIVITY MONITOR LISTENER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final ActivityMonitor.Listener activityMonitorListener = new ActivityMonitor.Listener ()
  {
    
    @Override
    public final void onStatusChange (final Service service, final Service.Status oldStatus, final Service.Status newStatus)
    {
      JActivityMonitor.this.updateActivityStatus ();
    }
    
    @Override
    public final void activityChanged (final String activity, final boolean newActivityStatus)
    {
      // Hold private copy of reference, as it may change.
      final String activityMonitored = JActivityMonitor.this.activity;
      if (activity == null && activityMonitored != null)
        return;
      if (activity != null && activityMonitored == null)
        return;
      if (activity != null && activityMonitored != null && ! activity.equals (activityMonitored))
        return;
      JActivityMonitor.this.updateActivityStatus ();
    }

  };
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // UPDATE ACTIVITY STATUS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private void updateActivityStatus ()
  {
    if (! SwingUtilities.isEventDispatchThread ())
      SwingUtilities.invokeLater (() -> updateActivityStatus ());
    else
    {
      synchronized (this)
      {
        final ActivityMonitor activityMonitor = JActivityMonitor.this.activityMonitor;
        final String activity = JActivityMonitor.this.activity;
        final boolean isActive = activityMonitor != null && activityMonitor.isActive (activity);
        if (getDisplayedValue () == null || isActive != getDisplayedValue ())
        {
          setDisplayedValue (isActive);
          repaint ();
        }
      }
    }
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}

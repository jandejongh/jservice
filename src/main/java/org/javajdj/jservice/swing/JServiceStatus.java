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
package org.javajdj.jservice.swing;

import java.util.Map;
import java.util.function.Function;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import org.javajdj.jservice.Service;
import org.javajdj.swing.JColorCheckBox;

/** A tiny {@link JComponent} visualizing the {@link Service.Status} of a {@link Service} through color coding.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JServiceStatus
  extends JColorCheckBox<Service.Status>
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTORS / FACTORIES / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Creates a service-monitoring component for given service with given color function.
   * 
   * <p>
   * If the function returns a {@code null} value,
   * or is even {@code null} itself,
   * the check-box background is <i>not</i> painted.
   * 
   * @param service       The service, non-{@code null}.
   * @param colorFunction The color function.
   * 
   * @throws IllegalArgumentException If the service is {@code null}.
   * 
   * @see JColorCheckBox
   * 
   */
  public JServiceStatus (final Service service, final Function<Service.Status, java.awt.Color> colorFunction)
  {
    super (colorFunction);
    if (service == null)
      throw new IllegalArgumentException ();
    this.service = service;
    this.service.addStatusListener (this.statusListener);
  }

  /** Creates a service-monitoring component for given service with given color function.
   * 
   * <p>
   * If the map returns a {@code null} value for some key,
   * or is even {@code null} itself,
   * the check-box background is <i>not</i> painted.
   * 
   * @param service  The service, non-{@code null}.
   * @param colorMap The color map.
   * 
   * @throws IllegalArgumentException If the service is {@code null}.
   * 
   * @see JColorCheckBox
   * 
   */
  public JServiceStatus (final Service service, Map<Service.Status, java.awt.Color> colorMap)
  {
    super (colorMap);
    if (service == null)
      throw new IllegalArgumentException ();
    this.service = service;
    this.service.addStatusListener (this.statusListener);
  }
  
  /** Creates a service-monitoring component for given service with default coloring.
   * 
   * <p>
   * The default coloring is none for {@link Service.Status#STOPPED},
   * {@link java.awt.Color#green} for {@link Service.Status#ACTIVE},
   * and {@link java.awt.Color#red} for {@link Service.Status#ERROR}.
   * 
   * @param service  The service, non-{@code null}.
   * 
   * @throws IllegalArgumentException If the service is {@code null}.
   * 
   * @see JColorCheckBox
   * 
   */
  public JServiceStatus (final Service service)
  {
    super ((final Service.Status t) ->
    {
      switch (t)
      {
        case STOPPED:
          return null;
        case ACTIVE:
          return java.awt.Color.green;
        case ERROR:
          return java.awt.Color.red;
        default:
          throw new RuntimeException ();
      }
    });
    if (service == null)
      throw new IllegalArgumentException ();
    this.service = service;
    this.service.addStatusListener (this.statusListener);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SERVICE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final Service service;
  
  /** Returns the service for which status is being displayed (or that is, in sub-classes, being controlled).
   * 
   * @return The service, non-{@code null}.
   * 
   */
  public final Service getService ()
  {
    return this.service;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SERVICE STATUS LISTENER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final Service.StatusListener statusListener =
    (final Service service1, final Service.Status oldStatus, final Service.Status newStatus) ->
    {
      final Runnable r = () -> JServiceStatus.this.setDisplayedValue (newStatus);
      if (SwingUtilities.isEventDispatchThread ())
        r.run ();
      else
        SwingUtilities.invokeLater (r);
    };
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}

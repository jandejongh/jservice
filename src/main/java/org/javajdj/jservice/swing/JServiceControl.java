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

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Map;
import java.util.function.Function;
import javax.swing.JComponent;
import org.javajdj.jservice.Service;
import org.javajdj.jswing.jcolorcheckbox.JColorCheckBox;

/** A tiny {@link JComponent} visualizing the {@link Service.Status} of a {@link Service} through color coding.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JServiceControl
  extends JServiceStatus
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTORS / FACTORIES / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Creates a service-control component for given service with given color function.
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
  public JServiceControl (final Service service, final Function<Service.Status, Color> colorFunction)
  {
    super (service, colorFunction);
    addMouseListener (this.mouseListener);
  }

  /** Creates a service-control component for given service with given color map.
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
  public JServiceControl (final Service service, Map<Service.Status, Color> colorMap)
  {
    super (service, colorMap);
    addMouseListener (this.mouseListener);
  }
  
  /** Creates a service-monitoring component for given service with default coloring.
   * 
   * <p>
   * See {@link JServiceStatus#JServiceStatus(org.javajdj.jservice.Service)}
   * for the default coloring.
   * 
   * @param service The service, non-{@code null}.
   * 
   * @throws IllegalArgumentException If the service is {@code null}.
   * 
   * @see JColorCheckBox
   * 
   */
  public JServiceControl (final Service service)
  {
    super (service);
    addMouseListener (this.mouseListener);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // MOUSE LISTENER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final MouseListener mouseListener = new MouseAdapter ()
  {
    @Override
    public final void mouseClicked (final MouseEvent e)
    {
      JServiceControl.this.getService ().toggleService ();
    }
  };
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}

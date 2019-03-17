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
package org.javajdj.swing;

import java.awt.Component;
import java.awt.Container;
import javax.swing.SwingUtilities;

/** Swing utilities.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class SwingUtilsJdJ
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTORS / FACTORIES / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Prevents instantiation.
   * 
   */
  private SwingUtilsJdJ ()
  {
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // INVOKE RUNNABLE ON SWING EDT
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Invokes the given {@link Runnable} on the Swing EDT.
   * 
   * <p>
   * If invoked from the Swing EDT, the {@link Runnable#run}
   * method is invoked immediately,
   * otherwise,
   * the {@link Runnable} is scheduled on the Swing EDT
   * through {@link SwingUtilities#invokeLater}.
   * 
   * @param r The {@link Runnable}.
   * 
   * @throws IllegalArgumentException If the runnable is {@code null}.
   * 
   * @see Runnable#run
   * @see SwingUtilities#isEventDispatchThread
   * @see SwingUtilities#invokeLater
   * 
   */
  public static void invokeOnSwingEDT (final Runnable r)
  {
    if (r == null)
      throw new IllegalArgumentException ();
    if (SwingUtilities.isEventDispatchThread ())
      r.run ();
    else
      SwingUtilities.invokeLater (r);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // ENABLE / DISABLE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Recursively enables of disables a container and all its descendants.
   * 
   * @param container The container, non-{@code null}.
   * @param enable    Whether to enable or disable the container tree.
   * 
   * @throws IllegalArgumentException If the container is {@code null}.
   * 
   * @see Container#setEnabled(boolean)
   * @see Container#getComponents
   * 
   */
  public static void enableComponentAndDescendants (final Container container, final boolean enable)
  {
    container.setEnabled (enable);
    final Component[] components = container.getComponents ();
    for (final Component component: components)
    {
      component.setEnabled (enable);
      if (component instanceof Container)
        SwingUtilsJdJ.enableComponentAndDescendants ((Container) component, enable);
    }
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}

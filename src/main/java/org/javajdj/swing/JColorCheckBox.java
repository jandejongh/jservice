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

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;

/** A (read-only) {@link JCheckBox} that displays a value by filling the box with a {@link Color},
 *   and omitting the check (tick) icon.
 * 
 * <p>
 * A {@link TableCellRenderer} implementation is available as well.
 * 
 * <p>
 * The component inherits behavior from {@link JCheckBox},
 * yet uses the {@code icon} property internally.
 * Upon construction, the {@code selected} property is set to {@code false}
 * on the super-class.
 * Also, the code ensures (hacks) that {@code selected == false},
 * inhibiting the check mark from appearing.
 * 
 * <p>
 * The same applies to the {@code icon} property,
 * since it is used internally by this class.
 * Attempts to modify the {@code icon} property will result in an {@link UnsupportedOperationException}.
 * 
 * <p>
 * The {@code class} features constructor providing a {@link Map}
 * or a {@link Function}.
 * Internally, a {@link Function} is used for the mapping.
 * 
 * @param <E> The type of the value.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 * @see #setSelected
 * @see #setEnabled
 * @see #setIcon
 * 
 */
public class JColorCheckBox<E>
  extends JCheckBox
  implements TableCellRenderer
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTORS / FACTORIES / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Constructs a {@link JColorCheckBox}
   *  taking a {@link Function} for mapping values (to represent) onto {@link Color}s.
   * 
   * <p>
   * If the function returns a {@code null} value,
   * or is even {@code null} itself,
   * the check-box background is <i>not</i> painted.
   * 
   * @param colorFunction The function; may be {@code null}.
   * 
   */
  public JColorCheckBox (final Function<E, Color> colorFunction)
  {
    super ();
    this.colorFunction = colorFunction;
    super.setSelected (false);
    // super.setEnabled (false);
    final Icon icon = new ColorCheckBoxIcon ();
    super.setIcon (icon);
    super.setPressedIcon (icon);
    super.setDisabledSelectedIcon (icon);
    super.setDisabledIcon (icon);
    // XXX This does not seem to work to remove the 'selected' check mark...
    super.setSelectedIcon (icon);
    // Ugly hack to get rid of the check mark...
    // It seems to work, BUT: XXX
    // This class has become way too complicated for its purpose...
    addMouseListener (new DefaultMouseListener ()
    {
      @Override
      public final void mouseClicked (final MouseEvent e)
      {
        JColorCheckBox.this.setSelected (false);
      }
    });
  }
  
  /** Constructs a {@link JColorCheckBox}
   *  taking a {@link Map} for mapping values (to represent) onto {@link Color}s.
   * 
   * <p>
   * If the map returns a {@code null} value for some key,
   * or is even {@code null} itself,
   * the check-box background is <i>not</i> painted.
   * 
   * @param colorMap The map; may be {@code null}.
   * 
   */
  public JColorCheckBox (final Map<E, Color> colorMap)
  {
    this ((E e) -> (colorMap != null ? colorMap.get (e) : null));
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // COLOR FUNCTION
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final Function<E, Color> colorFunction;
  
  /** Returns the color function (mapping values onto {@link Color}s.
   * 
   * <p>
   * A color function must be immutable (with respect to the {@link Function} interface).
   * 
   * @return The color function, may be {@code null}.
   * 
   */
  public final Function<E, Color> getColorFunction ()
  {
    return this.colorFunction;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // DISPLAYED VALUE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private volatile E displayedValue;
  
  /** Returns the currently displayed value.
   * 
   * @return The currently displayed value, may be {@code null}.
   * 
   */
  public final synchronized E getDisplayedValue ()
  {
    return this.displayedValue;
  }
  
  /** Sets the value to be represented in the component, and optionally order a {@link #repaint} on the Swing EDT.
   * 
   * @param displayedValue The new value; may be {@code null}.
   * @param repaint        Whether or not to repaint (schedule) the component.
   * 
   */
  protected final synchronized void setDisplayedValue (final E displayedValue, final boolean repaint)
  {
    this.displayedValue = displayedValue;
    if (repaint)
    {
      // Tell Swing EDT to redraw this component.
      SwingUtilsJdJ.invokeOnSwingEDT (() -> 
      {
        JColorCheckBox.this.repaint ();
//        JColorCheckBox.this.invalidate ();
//        JColorCheckBox.this.revalidate ();
      });
    }
  }
  
  /** Sets the value to be represented in the component.
   * 
   * @param displayedValue The new value; may be {@code null}.
   * 
   */
  public final void setDisplayedValue (final E displayedValue)
  {
    setDisplayedValue (displayedValue, true);
  }
 
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // ENABLED
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
//  private volatile boolean enabled = true;
//  
//  /** Returns whether this component is enabled.
//   * 
//   * <p>
//   * This method returns its private notion of the {@code enabled} property;
//   * it is always {@code false} on the super-class.
//   * The default value is {@code true}.
//   * 
//   * @return Whether this component is enabled.
//   * 
//   * @throws UnsupportedOperationException If called from another {@link Thread} that the Swing EDT.
//   * 
//   * @see SwingUtilities#isEventDispatchThread
//   * 
//   */
//  @Override
//  public final boolean isEnabled ()
//  {
//    if (! SwingUtilities.isEventDispatchThread ())
//      throw new UnsupportedOperationException ();
//    return this.enabled;
//  }
//  
//  /** Sets whether this component is enabled.
//   * 
//   * <p>
//   * This method holds its private notion of the {@code enabled} property;
//   * it is always {@code false} on the super-class.
//   * The default value is {@code true}.
//   * 
//   * @param enabled Whether or not the component is enabled from this point.
//   * 
//   * @throws UnsupportedOperationException If called from another {@link Thread} that the Swing EDT.
//   * 
//   * @see SwingUtilities#isEventDispatchThread
//   * 
//   */
//  @Override
//  public final void setEnabled (final boolean enabled)
//  {
//    if (! SwingUtilities.isEventDispatchThread ())
//      throw new UnsupportedOperationException ();
//    if (enabled != this.enabled)
//    {
//      this.enabled = enabled;
//      repaint ();
//    }
//  }
//  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SELECTED
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /** Returns {@code false}.
   * 
   * @return {@code false}.
   * 
   */
  @Override
  public final boolean isSelected ()
  {
    return false;
  }
  
//  /** Throws an exception.
//   * 
//   * @param selected The new selected status of the component.
//   * 
//   * @throws UnsupportedOperationException Always.
//   * 
//   */
//  @Override
//  public void setSelected (final boolean selected)
//  {
//    throw new UnsupportedOperationException ();
//  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // ICON
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Throws an exception.
   * 
   * @param icon The new icon.
   * 
   * @throws UnsupportedOperationException Always.
   * 
   */
  @Override
  public final void setIcon (final Icon icon)
  {
    throw new UnsupportedOperationException ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // HIGHLIGHT ON SELECT ICON
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private class ColorCheckBoxIcon
    extends javax.swing.plaf.metal.MetalCheckBoxIcon
  {

    public ColorCheckBoxIcon ()
    {
    }

    private final Icon wrappedIcon = UIManager.getIcon ("CheckBox.icon");

    @Override
    protected void drawCheck (final Component c, final Graphics g, final int x, final int y)
    {
      final java.awt.Color oldColor = g.getColor ();
      g.setColor (java.awt.Color.BLACK);
      super.drawCheck (c, g, x, y);
      g.setColor (oldColor);
    }

    @Override
    public void paintIcon (final Component c, final Graphics g, final int x, final int y)
    {
      this.wrappedIcon.paintIcon (c, g, x, y);
      if (/* JColorCheckBox.this.isEnabled () && */ JColorCheckBox.this.colorFunction != null)
      {
        final E value = JColorCheckBox.this.displayedValue;
        final java.awt.Color fillColor = JColorCheckBox.this.colorFunction.apply (value);
        if (fillColor != null)
        {
          final java.awt.Color oldColor = g.getColor ();
          g.setColor (fillColor);
          g.fillRect (x + 1, y + 1, getIconWidth () - 2, getIconHeight () - 2);
          g.setColor (oldColor);
        }        
      }
    }
    
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // TABLE CELL RENDERER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  @Override
  public Component getTableCellRendererComponent
   (final JTable table,
    final Object value,
    final boolean isSelected,
    final boolean hasFocus,
    final int row,
    final int column)
  {
    if (table == null)
      return null;
    setBackground (isSelected ? table.getSelectionBackground () : table.getBackground ());
    // XXX hasFocus?
    setDisplayedValue ((E) value, false);
    return this;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // JBoolean
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

   /** A ready-to-go {@link JColorCheckBox} for {@link Boolean}.
    * 
    * <p>
    * The default color scheme uses {@link Color#red} and {@link Color#green},
    * but this can be overridden through an alternative constructor.
    * 
    * @author Jan de Jongh, TNO
    * 
    */
  public static class JBoolean
    extends JColorCheckBox<Boolean>
  {
    
    /** Maps {@link Boolean} values onto {@link Color}.
     * 
     * <p>
     * A static initializer block map {@link java.lang.Boolean#FALSE} onto {@link java.awt.Color#red}
     * and {@link java.lang.Boolean#TRUE} onto {@link java.awt.Color#green}.
     * 
     */
    public final static Map<Boolean, Color> DEFAULT_BOOLEAN_COLOR_MAP = new HashMap<> ();
    
    static
    {
      DEFAULT_BOOLEAN_COLOR_MAP.put (Boolean.FALSE, Color.red);
      DEFAULT_BOOLEAN_COLOR_MAP.put (Boolean.TRUE,  Color.green);
    }
      
    /** Creates a check-box for a boolean value with given color function.
     * 
     * @param colorFunction The color function.
     * 
     */
    public JBoolean (Function<Boolean, Color> colorFunction)
    {
      super (colorFunction);
    }
    
    /** Creates a check-box for a boolean value with given color map.
     * 
     * @param colorMap The color map.
     * 
     */
    public JBoolean (Map<Boolean, Color> colorMap)
    {
      super (colorMap);
    }
    
    /** Creates a check-box for a boolean value with default color scheme.
     * 
     * <p>
     * The default color scheme is to display {@code false} with {@link Color#RED},
     * {@code true} with {@link Color#GREEN}.
     * 
     */
    public JBoolean ()
    {
      this (DEFAULT_BOOLEAN_COLOR_MAP);
    }
     
  }
   
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // JColor
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

   /** A ready-to-go {@link JColorCheckBox} for {@link Color}.
    * 
    * <p>
    * The implementation uses the identify function to map {@link Color} values
    * into their implementation.
    * 
    * @author Jan de Jongh, TNO
    * 
    */
  public static class JColor
    extends JColorCheckBox<Color>
  {
    
    public JColor ()
    {
      super (Function.identity ());
    }
     
  }
   
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}

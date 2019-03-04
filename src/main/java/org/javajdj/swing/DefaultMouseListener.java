package org.javajdj.swing;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/** A default empty implementation of {@link MouseListener}.
 * 
 * <p>
 * No clue why Swing does not come with one; probably missed it...
 *
 * @author Jan de Jongh {@literal jfcmdejongh@gmail.com}
 * 
 */
public class DefaultMouseListener
  implements MouseListener
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // MOUSE LISTENER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /** Does nothing.
   * 
   * @param me The mouse event.
   * 
   */
  @Override
  public void mouseClicked (final MouseEvent me)
  {
  }

  /** Does nothing.
   * 
   * @param me The mouse event.
   * 
   */
  @Override
  public void mousePressed (final MouseEvent me)
  {
  }

  /** Does nothing.
   * 
   * @param me The mouse event.
   * 
   */
  @Override
  public void mouseReleased (final MouseEvent me)
  {
  }

  /** Does nothing.
   * 
   * @param me The mouse event.
   * 
   */
  @Override
  public void mouseEntered (final MouseEvent me)
  {
  }

  /** Does nothing.
   * 
   * @param me The mouse event.
   * 
   */
  @Override
  public void mouseExited (final MouseEvent me)
  {
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}


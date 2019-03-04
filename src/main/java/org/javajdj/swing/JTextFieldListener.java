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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JTextField;

/** A versatile listener to value changes in a {@link JTextField}.
 * 
 * <p>
 * When using {@link JTextField}s in a {@code Swing} application, 
 * there are typically two occasions at which one should consider the text in the text field as having "changed",
 * either when the user hits 'Enter' or when the user has (possibly) entered text and switches focus
 * (usually without pressing 'Enter' to complete the input).
 * 
 * <p>
 * In Java Swing, to our knowledge, capturing these events cannot be done through a single listener type;
 * one needs a {@link ActionListener} for the 'Enter' event (pressing 'Enter' should reward the user with a response)
 * and a {@link FocusListener} for the latter (e.g., Copy/Paste should be rewarded without the user having to press 'Enter').
 * 
 * <p>
 * This class captures both events on a {@link JTextField},
 * and invokes the abstract method {@link #actionPerformed} for both use cases.
 * 
 * <p>
 * The static (utility) method {@link #addJTextFieldListener} registers a {@link JTextFieldListener} at
 * a {@link JTextField} both as focus(-lost) listener and as {@link ActionListener}.
 * 
 * @author Jan de Jongh {@literal jfcmdejongh@gmail.com}
 * 
 */
public abstract class JTextFieldListener
  implements FocusListener, ActionListener
{
  
  /** Invoked when the (text field) listener is properly triggered by value changes;
   *  to be implemented by concrete sub-classes.
   * 
   */
  public abstract void actionPerformed ();

  /** Invokes {@link #actionPerformed} on the ego object when the event argument is non-{@code null}.
   * 
   * <p>
   * This method implements the {@link ActionListener} interface.
   * 
   * <p>
   * Invocations of this method with {@code null} argument are silently ignored.
   * 
   * @param ae The {@link ActionEvent}.
   * 
   * @see #actionPerformed
   * 
   */
  @Override
  public final void actionPerformed (final ActionEvent ae)
  {
    if (ae == null)
      return;
    actionPerformed ();
  }

  /** Does nothing.
   * 
   * <p>
   * This method is part of the listener's implementation of {@link FocusListener} interface.
   * 
   * @param e The event (ignored).
   * 
   */
  @Override
  public final void focusGained (final FocusEvent e)
  {
    // DOES NOTHING [INTENDED]
  }

  /** Invokes {@link #actionPerformed} on the ego object when the event argument is non-{@code null}.
   * 
   * <p>
   * This method is part of the listener's implementation of {@link FocusListener} interface.
   * 
   * @param e The event (ignored).
   *
   * @see #actionPerformed
   * 
   */
  @Override
  public final void focusLost (final FocusEvent e)
  {
    if (e != null)
      actionPerformed ();
  }

  /** Registers a given {@link JTextFieldListener} at a given {@link JTextField}
   *  as a "focus-lost" listener and as a "Enter" listener.
   * 
   * @param jTextField         The {@link JTextField}, non-{@code null}.
   * @param jTextFieldListener The corresponding {@link JTextFieldListener}, non-{@code null}.
   * 
   * @throws IllegalArgumentException If either argument is {@code null}.
   * 
   * @see JTextField#addActionListener
   * @see JTextField#addFocusListener
   * @see #registerListener
   * 
   */
  public static void addJTextFieldListener (final JTextField jTextField, final JTextFieldListener jTextFieldListener)
  {
      if (jTextField == null || jTextFieldListener == null)
          throw new IllegalArgumentException();
      jTextField.addActionListener (jTextFieldListener);
      jTextField.addFocusListener(jTextFieldListener);
  }
  
  /** Registers the current listener to given {@link JTextField}.
   * 
   * @param jTextField The text field to register to (non-{@code null}).
   * 
   * @throws IllegalArgumentException If {@code jTextField == null}.
   * 
   * @see #addJTextFieldListener
   * 
   */
  public final void registerListener (final JTextField jTextField)
  {
    JTextFieldListener.addJTextFieldListener (jTextField, this);
  }
  
}

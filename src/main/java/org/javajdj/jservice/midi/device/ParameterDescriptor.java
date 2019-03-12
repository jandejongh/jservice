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
package org.javajdj.jservice.midi.device;

import java.util.Map;

/** A descriptor that can be used to describe a parameter (key) of a {@link MidiDevice}.
 * 
 * <p>
 * A {@link ParameterDescriptor} is an immutable object.
 *
 * <p>
 * A {@link ParameterDescriptor} is <i>not</i> part of the {@link MidiDevice} interface,
 * but heavily used and extended in {@link AbstractMidiDevice} and derived packages and sub-classes.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public interface ParameterDescriptor
{
  
  /** Returns the (immutable) parameter name.
   * 
   * <p>
   * The name of the parameter is used as key in {@link MidiDevice},
   * which itself implements the {@link Map} interface.
   * 
   * @return The immutable parameter name, non-{@code null} and (trimmed) non-empty.
   * 
   */
  String getParameterName ();
  
}

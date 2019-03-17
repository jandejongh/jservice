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
package org.javajdj.util.equals;

import java.lang.reflect.Array;

/** Utility class for checking equality in Java.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public final class EqualsUtils
{
  
  /** Prevents instantiation.
   * 
   */
  private EqualsUtils ()
  {
  }
  
  /** Compares two objects for equality, with special treatment of array objects.
   * 
   * <p>
   * Credits: Peter Walser, on {@literal https://stackoverflow.com/questions/7869050/java-comparing-arrays}.
   * 
   * @param a The first object, may be {@code null}.
   * @param b The second object, may be {@code null}.
   * 
   * @return Whether the two objects are equal, including the case in which both are {@code null},
   *           or both are arrays of equal type, length, and content (deep check).
   * 
   */
  public static boolean equals (final Object a, final Object b)
  {
    if (a == b)
      return true;
    if (a == null || b == null)
      return false;
    if (a.getClass ().isArray () && b.getClass ().isArray ())
    {

      final int length = Array.getLength (a);
      if (length > 0 && ! a.getClass ().getComponentType ().equals (b.getClass ().getComponentType ()))
        return false;
      if (Array.getLength (b) != length)
        return false;
      for (int i = 0; i < length; i++)
        if (! EqualsUtils.equals (Array.get (a, i), Array.get (b, i)))
          return false;
      return true;
    }
    return a.equals (b);
  }

}

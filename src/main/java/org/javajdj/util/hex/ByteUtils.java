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
package org.javajdj.util.hex;

/** Utility class for the {@code byte} primitive type in Java.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class ByteUtils
{
  
  /** Returns a long holding the (unsigned) value of four consecutive bytes in an array.
   * 
   * @param bytes  The byte array.
   * @param offset The offset in the byte array from which to collect the value to return.
   * 
   * @return The value of four consecutive bytes in an array, starting at given offset, treated as an unsigned (32-bit) integer.
   * 
   * @throws IllegalArgumentException If the array is {@code null}, offset is (strictly) negative,
   *                                    or the array is too small to permit reading four bytes starting at given offset.
   * 
   */
  public static long bytes4ToLong (final byte[] bytes, final int offset)
  {
    if (bytes == null || offset < 0 || bytes.length < offset + 4)
      throw new IllegalArgumentException ();
    return ((((long) bytes[offset+0]) & 0xFF) << 24)
         + ((((long) bytes[offset+1]) & 0xFF) << 16)
         + ((((long) bytes[offset+2]) & 0xFF) <<  8)
         + ((((long) bytes[offset+3]) & 0xFF));
  }
    
}
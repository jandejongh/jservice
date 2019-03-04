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

/** Utility class for hexadecimal number representation.
 *
 * @author Internet
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 *
 */
public class HexUtils
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTORS / FACTORIES / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /** Prevents instantiation.
   * 
   */
  private HexUtils ()
  {
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // HEXADECIMAL REPRESENTATION
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final static char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray ();

  /** Converts section of given byte array to hexadecimal representation.
   * 
   * <p>
   * The length of the returned {@code String} is exactly twice the number of bytes represented.
   * 
   * @param bytes  The array, non-{@code null}.
   * @param offset The offset into the array, non-negative.
   * @param length The number of bytes to represent (starting at offset), non-negative.
   * 
   * @return The hexadecimal representation of the bytes in the specified segment.
   * 
   * @throws IllegalArgumentException If {@code bytes == null}, {@code offset < 0}, {@code length < 0},
   *                                  or {@code offset + length > bytes.length}.
   * 
   */
  public static String bytesToHex (final byte[] bytes, final int offset, final int length)
  {
    if (bytes == null || offset < 0 || length < 0 || offset + length > bytes.length)
      throw new IllegalArgumentException ();
    if (length == 0)
      return "";
    char[] hexChars = new char[length * 2];
    for (int j = 0; j < length; j++)
    {
      int v = bytes[j] & 0xFF;
      hexChars[j * 2] = HEX_ARRAY[v >>> 4];
      hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
    }
    return new String (hexChars);
  }

  /** Converts a given byte array to hexadecimal representation.
   * 
   * <p>
   * The length of the returned {@code String} is exactly twice the size of the array.
   * 
   * @param bytes The array, non-{@code null}.
   * 
   * @return The hexadecimal representation of the bytes.
   * 
   * @throws IllegalArgumentException If {@code bytes == null}.
   * 
   */
  public static String bytesToHex (final byte[] bytes)
  {
    return bytesToHex (bytes, 0, bytes.length);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}

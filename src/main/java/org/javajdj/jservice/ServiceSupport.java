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
package org.javajdj.jservice;

import org.javajdj.jservice.Service.Status;

/** An implementation of {@link Service} for use as a delegate class.
 *
 * <p>
 * It does not support the service operations {@link #startService} and {@link #stopService}.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class ServiceSupport
  extends AbstractService
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTORS / FACTORIES / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Constructs a service support with given service name.
   * 
   * @param name The service name, non-{@code null}.
   * 
   * @throws IllegalArgumentException If {@code name == null}.
   * 
   * @see Service#setName
   * 
   */
  public ServiceSupport (final String name)
  {
    super (name);
  }

  /** Creates a new service support with name {@code "No Name"}.
   * 
   */
  public ServiceSupport ()
  {
    super ();
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SERVICE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /** Throws an {@link UnsupportedOperationException}.
   * 
   * @throws UnsupportedOperationException Always.
   * 
   */
  @Override
  public final void startService ()
  {
    throw new UnsupportedOperationException ("Not supported.");
  }

  /** Throws an {@link UnsupportedOperationException}.
   * 
   * @throws UnsupportedOperationException Always.
   * 
   */
  @Override
  public final void stopService ()
  {
    throw new UnsupportedOperationException ("Not supported.");
  }

  /** Exposes {@link AbstractService#setStatus}.
   * 
   * @param newServiceStatus The new service status.
   * 
   * @throws IllegalArgumentException If {@code newServiceStatus == null}.
   * 
   * @see AbstractService#setStatus
   * 
   */
  public final void setStatusServiceSupport (final Status newServiceStatus)
  {
    setStatus (newServiceStatus);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}

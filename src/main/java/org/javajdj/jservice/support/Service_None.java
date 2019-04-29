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
package org.javajdj.jservice.support;

import java.util.logging.Logger;
import org.javajdj.jservice.AbstractService;
import org.javajdj.jservice.Service;
import org.javajdj.jservice.Service.Status;

/** A {@link Service} that you can start and stop, but that does literally nothing at all.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 *
 */
public final class Service_None
  extends AbstractService
  implements Service
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (Service_None.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTORS / FACTORIES / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /** Creates a service with given name and that does nothing (guaranteed).
   * 
   * @param name The service name, non-{@code null}.
   * 
   * @throws IllegalArgumentException If {@code name == null}.
   * 
   */
  public Service_None (final String name)
  {
    super (name);
  }

  /** Creates a service that does nothing (guaranteed).
   * 
   * <p>
   * The service name is set to {@code "Service_None"}.
   * 
   */
  public Service_None ()
  {
    this ("Service_None");
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SERVICE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  @Override
  public final synchronized void startService ()
  {
    if (getStatus () == Status.ACTIVE)
      return;
    setStatus (Status.ACTIVE);
  }

  @Override
  public final synchronized void stopService ()
  {
    if (getStatus () == Status.STOPPED)
      return;
    setStatus (Status.STOPPED);
  }

  @Override
  protected final synchronized void error ()
  {
    super.error ();
  }

  @Override
  public final synchronized void destroyService ()
    throws UnsupportedOperationException
  {
    super.destroyService ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}

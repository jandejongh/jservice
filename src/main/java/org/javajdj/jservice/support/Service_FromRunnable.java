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

/** A {@link Service} from a single {@link Runnable}.
 * 
 * <p>
 * The {@link Runnable} is started when the {@link Service_FromRunnable} is started.
 * The service, however, does not monitor the termination of the {@link Runnable}.
 * 
 * <p>
 * For proper functioning of the {@link Service} interface,
 * the {@link Runnable} must terminate upon interrupt.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 *
 */
public class Service_FromRunnable
  extends AbstractService
  implements Service
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (Service_FromRunnable.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTORS / FACTORIES / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /** Creates a service with given name and from given {@link Runnable}.
   * 
   * @param name     The service name, non-{@code null}.
   * @param runnable The {@link Runnable}, may be {@code null}.
   * 
   * @throws IllegalArgumentException If {@code name == null}.
   * 
   */
  public Service_FromRunnable (final String name, final Runnable runnable)
  {
    super (name);
    this.runnable = runnable;
  }

  /** Creates a service from given {@link Runnable}.
   * 
   * <p>
   * The service name is set to {@code "Service_FromRunnable"}.
   *
   * @param runnable The {@link Runnable}, may be {@code null}.
   * 
   */
  public Service_FromRunnable (final Runnable runnable)
  {
    this ("Service_FromRunnable", runnable);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // RUNNABLE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final Runnable runnable;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SERVICE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private volatile Thread thread = null;
  
  @Override
  public final synchronized void startService ()
  {
    if (getStatus () == Status.ACTIVE)
      return;
    stopService ();
    if (this.runnable != null)
    {
      this.thread = new Thread (this.runnable);
      this.thread.start ();
    }
    if (getStatus () == Status.STOPPED)
      setStatus (Status.ACTIVE);
  }

  @Override
  public final synchronized void stopService ()
  {
    if (getStatus () == Status.STOPPED)
      return;
    if (this.thread != null)
      this.thread.interrupt ();
    this.thread = null;
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

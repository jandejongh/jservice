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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.javajdj.jservice.AbstractService;
import org.javajdj.jservice.Service;
import org.javajdj.jservice.Service.Status;

/** A {@link Service} from a (extensible) set of {@link Runnable}s.
 * 
 * <p>
 * The {@link Runnable}s are started when the {@link Service_FromRunnables} is started.
 * The service, however, does not monitor the termination of the {@link Runnable}s.
 * 
 * <p>
 * For proper functioning of the {@link Service} interface,
 * the {@link Runnable}s must terminate upon interrupt.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 *
 */
public class Service_FromRunnables
  extends AbstractService
  implements Service
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (Service_FromRunnables.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTORS / FACTORIES / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /** Creates a service with given name and from given {@link Runnable}s.
   * 
   * @param name      The service name, non-{@code null}.
   * @param runnables The {@link Runnable}s, may be {@code null} or empty.
   * 
   * @throws IllegalArgumentException If {@code name == null}.
   * 
   */
  public Service_FromRunnables (final String name, final List<Runnable> runnables)
  {
    super (name);
    if (runnables != null)
      this.runnables.addAll (runnables);
  }

  /** Creates a service from given {@link Runnable}s.
   * 
   * <p>
   * The service name is set to {@code "Service_FromRunnables"}.
   * 
   * @param runnables The {@link Runnable}s, may be {@code null} or empty.
   * 
   */
  public Service_FromRunnables (final List<Runnable> runnables)
  {
    this ("Service_FromRunnables", runnables);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // RUNNABLES
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final List<Runnable> runnables = new ArrayList<> ();
  
  /** Adds a {@link Runnable}.
   * 
   * <p>
   * If the service is currently active ({@link Status#ACTIVE})
   * or in error ({@link Status#ERROR}),
   * the {@link Runnable} is started in a private {@link Thread} immediately.
   * 
   * @param runnable The {@link Runnable}, may be {@code null}.
   * 
   */
  protected final synchronized void addRunnable (final Runnable runnable)
  {
    this.runnables.add (runnable);
    if (runnable != null && getStatus () != Status.STOPPED)
    {
      final Thread thread = new Thread (runnable);
      this.threads.add (thread);
      thread.start ();
    }
    else
      this.threads.add (null);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SERVICE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final List<Thread> threads = new ArrayList<> ();
  
  @Override
  public final synchronized void startService ()
  {
    if (getStatus () == Status.ACTIVE)
      return;
    stopService ();
    for (final Runnable runnable : this.runnables)
      if (runnable != null)
      {
        final Thread thread = new Thread (runnable);
        this.threads.add (thread);
        thread.start ();
      }
      else
        this.threads.add (null);
    if (getStatus () == Status.STOPPED)
      setStatus (Status.ACTIVE);
  }

  @Override
  public final synchronized void stopService ()
  {
    if (getStatus () == Status.STOPPED)
      return;
    for (final Thread thread : this.threads)
      if (thread != null)
        thread.interrupt ();
    this.threads.clear ();
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

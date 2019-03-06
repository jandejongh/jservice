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
import static org.javajdj.jservice.Service.Status.ACTIVE;
import static org.javajdj.jservice.Service.Status.ERROR;
import static org.javajdj.jservice.Service.Status.STOPPED;
import org.javajdj.jservice.Service.StatusListener;

/** A {@link Service} from (extensible) sets of {@link Runnable}s and {@link Service}s.
 * 
 * <p>
 * The {@link Runnable}s are started when the {@link Service_FromMix} is started.
 * The service, however, does not monitor the termination of the {@link Runnable}s.
 * 
 * <p>
 * For proper functioning of the {@link Service} interface,
 * the {@link Runnable}s must terminate upon interrupt.
 * 
 * <p>
 * The embedded {@link Service}s are started when the {@link Service_FromMix} is started.
 * The service monitors status changes of the embedded service.
 * Unless these originate from the managing service (us),
 * a {@link Status#ERROR} is raised.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 *
 */
public class Service_FromMix
  extends AbstractService
  implements Service
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (Service_FromMix.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTORS / FACTORIES / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /** Creates a service from given {@link Runnable}s and {@link Service}s.
   * 
   * @param runnables      The {@link Runnable}s, may be {@code null} or empty.
   * @param targetServices The {@link Service}s, may be {@code null} or empty.
   * 
   */
  public Service_FromMix (final List<Runnable> runnables, final List<Service> targetServices)
  {
    if (runnables != null)
      this.runnables.addAll (runnables);
    if (targetServices != null)
      this.targetServices.addAll (targetServices);
  }

  /** Creates a service without initial {@link Runnable}s or {@link Service}s.
   * 
   */
  public Service_FromMix ()
  {
    this (null, null);
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
  // TARGET SERVICES
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final List<Service> targetServices = new ArrayList<> ();
  
  /** Adds a {@link Service}.
   * 
   * <p>
   * If the service is currently active ({@link Status#ACTIVE})
   * or in error ({@link Status#ERROR}),
   * the {@link Service} is started immediately.
   * 
   * @param targetService The {@link Service}, may be {@code null}.
   * 
   */
  protected final synchronized void addTargetService (final Service targetService)
  {
    this.targetServices.add (targetService);
    if (targetService != null && getStatus () != Status.STOPPED)
      targetService.startService ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // TARGET SERVICE STATUS LISTENER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final StatusListener targetServiceStatusListener =
    (final Service service, final Status oldStatus, final Status newStatus) ->
    {
      if (service == null || newStatus == null)
        throw new IllegalArgumentException ();
      switch (newStatus)
      {
        case ACTIVE:
          break;
        case STOPPED:
        case ERROR:
          Service_FromMix.this.error ();
          break;
        default:
          throw new RuntimeException ();
      }
    };
  
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
    for (final Service targetService : this.targetServices)
      if (targetService != null)
      {
        targetService.addStatusListener (this.targetServiceStatusListener);
        targetService.startService ();
      }
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
    for (final Service targetService : this.targetServices)
      if (targetService != null)
      {
        targetService.removeStatusListener (this.targetServiceStatusListener);
        targetService.stopService ();
      }
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

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
import static org.javajdj.jservice.Service.Status.ACTIVE;
import static org.javajdj.jservice.Service.Status.ERROR;
import static org.javajdj.jservice.Service.Status.STOPPED;
import org.javajdj.jservice.Service.StatusListener;

/** A {@link Service} from a single (other) {@link Service}.
 * 
 * <p>
 * The embedded {@link Service} is started when the {@link Service_FromService} is started.
 * The service monitors status changes of the embedded services.
 * Unless these originate from the managing service (us),
 * a {@link Status#ERROR} is raised.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 *
 */
public class Service_FromService
  extends AbstractService
  implements Service
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (Service_FromService.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTORS / FACTORIES / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /** Creates a service with given name and from given {@link Service}.
   * 
   * @param name          The service name, non-{@code null}.
   * @param targetService The {@link Service}, may be {@code null}.
   * 
   * @throws IllegalArgumentException If {@code name == null}.
   * 
   */
  public Service_FromService (final String name, final Service targetService)
  {
    super (name);
    this.targetService = targetService;
  }

  /** Creates a service from given {@link Service}.
   * 
   * <p>
   * The service name is set to {@code "Service_FromService"}.
   * 
   * @param targetService The {@link Service}, may be {@code null}.
   * 
   */
  public Service_FromService (final Service targetService)
  {
    this ("Service_FromService", targetService);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // TARGET SERVICE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final Service targetService;
  
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
          Service_FromService.this.error ();
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

  @Override
  public final synchronized void startService ()
  {
    if (getStatus () == Status.ACTIVE)
      return;
    stopService ();
    if (this.targetService != null)
    {
      this.targetService.addStatusListener (this.targetServiceStatusListener);
      this.targetService.startService ();
    }
    if (getStatus () == Status.STOPPED)
      setStatus (Status.ACTIVE);
  }

  @Override
  public final synchronized void stopService ()
  {
    if (getStatus () == Status.STOPPED)
      return;
    if (this.targetService != null)
    {
      this.targetService.removeStatusListener (this.targetServiceStatusListener);
      this.targetService.stopService ();
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

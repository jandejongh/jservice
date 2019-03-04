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
package org.javajdj.jservice.activity;

import java.time.Instant;
import java.util.Set;

/** An object that time-stamps a fixed set of activities, for use in activity monitoring.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public interface ActivityMonitorable
{
  
  /** Returns the activities being monitored by name.
   * 
   * <p>
   * The set of activities should remain constant after first use of this interface.
   * 
   * <p>
   * Implementations are encouraged to return a {@code Set} with fixed ordering of the elements.
   * 
   * @return A {@code Set} of the activity names being monitored.
   * 
   */
  Set<String> getMonitorableActivities ();
  
  /** Returns the time-stamp of the last activity (taken over all registered activities).
   * 
   * <p>
   * In case no activity has been monitored yet, {@link Instant#MIN} must be returned.
   * 
   * @return The time-stamp of the last activity (taken over all registered activities), non-{@code null}.
   * 
   */
  Instant lastActivity ();
  
  /** Returns the time-stamp of the last named activity.
   * 
   * <p>
   * In case no activity has been monitored yet, or if the activity is {@code null} or unknown,
   * {@link Instant#MIN} must be returned.
   * 
   * @param monitorableActivity The name of the activity.
   * 
   * @return The time-stamp of the last activity (taken over all registered activities), non-{@code null}.
   * 
   */
  Instant lastActivity (String monitorableActivity);
  
}

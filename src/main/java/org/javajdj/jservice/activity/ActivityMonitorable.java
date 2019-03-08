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
 * <p>
 * The activities being monitored are indexed by {@code String};
 * optionally,
 * the special value {@code null} is to indicate
 * activity of the object "as a whole", i.e.,
 * without being specific.
 * 
 * <p>
 * At the present time,
 * implementations of the interface must expose
 * a fixed set of activities, determined
 * at construction time.
 * In other words,
 * activities cannot be "added" or "removed" from the object.
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
   * Implementations are strongly encouraged to return a {@code Set} with fixed ordering of the elements.
   * 
   * <p>
   * A {@code null} {@code String} is allowed in the returned {@code Set},
   * but means the implementation will report the {@link Object}'s activity as a whole
   * as well (and make it available through {@link #lastActivity()}).
   * 
   * @return A {@code Set} of the activity names being monitored.
   * 
   */
  Set<String> getMonitorableActivities ();
  
  /** Returns the time-stamp of the last activity of the {@code Object} as a whole.
   * 
   * <p>
   * Implementations may only return a value other than {@link Instant#MIN}
   * if {@code null} was part of the {@code Set} returned from
   * {@link #getMonitorableActivities}.
   * In that case,
   * the returned value must lie beyond the other reported activity {@link Instant}s
   * (taken over all registered activities).
   * 
   * <p>
   * In case no activity has been monitored yet,
   * {@link Instant#MIN} must be returned.
   * 
   * <p>
   * Equivalent to {@code lastActivity (null)}.
   * This is also the default implementation.
   * 
   * @return The time-stamp of the last activity of the {@code Object} as a whole., non-{@code null}.
   * 
   */
  default Instant lastActivity ()
  {
    return lastActivity (null);
  }
  
  /** Returns the time-stamp of the last named activity.
   * 
   * <p>
   * See the comments with {@link #lastActivity()}
   * for details on the expected behavior with {@code null} arguments.
   * 
   * <p>
   * In case no activity has been monitored yet, or if the activity is unknown,
   * {@link Instant#MIN} must be returned.
   * 
   * @param monitorableActivity The name of the activity.
   * 
   * @return The time-stamp of the last activity (taken over all registered activities), non-{@code null}.
   * 
   */
  Instant lastActivity (String monitorableActivity);
  
}

/*
 * Copyright 2019-2020 Jan de Jongh <jfcmdejongh@gmail.com>.
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

import java.util.Arrays;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

/** A {@code Runnable} that periodically, constantly, or just once invokes some target {@code Runnable}(s)
 *  that may throw {@link Exception}s.
 *
 * <p>
 * This class assumes, without checking, that its instances are run by at most one {@code Thread}
 * at any point in time. In addition, its target {@code Runnables} should not be run by other {@link Thread}s
 * than that/those induced by the definition of the encompassing {@link RunnableInvoker}.
 * 
 * <p>
 * Since the standard {@link Runnable} interface does not allow its {@link Runnable#run} to throw exceptions,
 * this class introduces an adaptation {@link RunnableWithException} as interface to target {@link Runnable}s.
 * 
 * <p>
 * Instead of a (single} {@link Runnable} as target,
 * special factory methods are present accepting a {@link Supplier} and {@link Consumer}
 * (both modified to be allowed to throw {@link Exception}s,
 * see {@link SupplierWithException} and {@link ConsumerWithException}).
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class RunnableInvoker
  implements Runnable
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private static final Logger LOG = Logger.getLogger (RunnableInvoker.class.getName ());

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /** A {@link Runnable} allowed to throw an(y) {@link Exception}.
   * 
   * @see Runnable
   * 
   */
  @FunctionalInterface
  public interface RunnableWithException
  {
    void run () throws Exception; 
  }
  
  /** A {@link Supplier} allowed to throw an(y) {@link Exception}.
   * 
   * @param <T> The type of results supplied by this supplier.
   * 
   * @see Supplier
   * 
   */
  @FunctionalInterface  
  public interface SupplierWithException<T>
  {
    T get () throws Exception; 
  }
    
  /** A {@link Consumer} allowed to throw an(y) {@link Exception}.
   * 
   * @param <T> The type of the input to the operation.
   * 
   * @see Consumer
   * 
   */
  @FunctionalInterface
  public interface ConsumerWithException<T>
  {
    void accept (T t) throws Exception;
  }
  
  private RunnableInvoker (
    final String name,
    final Object host,
    final Supplier<Double> period_s,
    final boolean randomizeStartTime,
    final OverloadPolicy overloadPolicy,
    final RunnableWithException runnable,
    final Set<Class<? extends Exception>> mustIgnore,
    final Set<Class<? extends Exception>> mustTerminate,
    final boolean defaultTerminateUponException,
    final Consumer<Boolean> terminateListener, // Argument: Termination due to abnormal condition (true) or interrupt (false).
    final Level startTerminateLogLevel,
    final Level runnableExceptionLogLevel,
    final Level overloadLogLevel)
  {
    if (name == null || name.trim ().isEmpty ())
      throw new IllegalArgumentException ();
    this.name = name;
    this.host = host;
    this.period_s = period_s;
    if (randomizeStartTime && this.period_s == null)
      throw new IllegalArgumentException ();
    this.randomizeStartTime = randomizeStartTime;
    this.overloadPolicy = overloadPolicy;
    this.runnable = runnable;
    this.mustIgnore = mustIgnore;
    this.mustTerminate = mustTerminate;
    this.defaultTerminateUponException = defaultTerminateUponException;
    this.terminateListener = terminateListener;
    this.startTerminateLogLevel = startTerminateLogLevel;
    this.runnableExceptionLogLevel = runnableExceptionLogLevel;
    if (period_s == null && overloadLogLevel != null)
      throw new IllegalArgumentException ();
    this.overloadLogLevel = overloadLogLevel;    
  }    
  
  /** Creates a {@link RunnableInvoker} that (attempts to) periodically invoke a target {@link RunnableWithException}.
   * 
   * <p>
   * The created {@link RunnableInvoker} never invokes its target {@link RunnableWithException} on top of a previous invocation.
   * In other words, it always awaits termination of the target, before invoking it again.
   * It <i>does</i> however compensate for the time spent in target invocations in order to meet the required
   * inter-invocation period.
   * 
   * @param name The name of the created object, must be non-{@code null} and non-empty, and is only used in logging output.
   *             It has <i>no</i> semantic meaning and does <i>not</i> have to be unique.
   * @param host The {@code Object} hosting the created {@link RunnableInvoker}, if applicable.
   *             It is only used in logging output.
   *             It has <i>no</i> semantic meaning, may be {@code null} and does <i>not</i> have to be unique.
   * @param period_s The (mandatory) {@link Supplier} of the invocation period in seconds.
   *                 Note that the supplier does <i>not</i> necessarily have to provide the same period over time.
   * @param randomizeStartTime Whether to randomize the start time (uniform in the first invocation period).
   * @param overloadPolicy The policy to follow in case of overload, occurring roughly speaking when an invocation of the
   *                         target {@code Runnable} takes longer than the invocation period.
   * @param runnable The target {@link RunnableWithException}.
   * @param mustIgnore Which {@link Exception}s resulting from target invocation to ignore, may be {@code null}.
   *                   Note that {@link InterruptedException} cannot be ignored;
   *                   it always terminates the enclosing {@link RunnableInvoker}.
   *                   {@code Exception}s to ignore specified here take precedence over {@code Exception}s to cause termination
   *                   as specified in the next argument.
   * @param mustTerminate Which {@link Exception}s resulting from target invocation must cause termination of the
   *                        {@link RunnableInvoker} returned, may be {@code null}.
   *                      Note that {@link InterruptedException} does <i>not</i> have to be specified;
   *                        it always terminates the enclosing {@link RunnableInvoker}.
   * @param defaultTerminateUponException The default behavior (terminate or ignore) in case of an {@link Exception}
   *                                        resulting from target invocation.
   * @param terminateListener An optional {@link Consumer} invoked when the {@link RunnableInvoker} (i.e., the created object)
   *                            terminates.
   *                          The {@link Boolean} argument to the consumer indicates whether termination occurs
   *                            due to abnormal condition ({@code true}) or interrupt ({@code false}).
   *                          An abnormal condition is either an {@link Exception} other than {@link InterruptedException}
   *                            thrown from target invocation, or an unacceptable overload.
   * @param startTerminateLogLevel The {@link Level} at which start and termination events of the {@link RunnableInvoker} are
   *                                 reported; may be {@code null}.
   * @param runnableExceptionLogLevel The {@link Level} at which {@link Exception}s thrown by the target are reported;
   *                                    may be {@code null}.
   * @param overloadLogLevel The {@link Level} at which scheduling overloads are reported; may be {@code null}.
   * 
   * @return The {@link RunnableInvoker}.
   *
   */
  public static final RunnableInvoker periodicallyFromRunnable (
    final String name,
    final Object host,
    final Supplier<Double> period_s,
    final boolean randomizeStartTime,
    final OverloadPolicy overloadPolicy,
    final RunnableWithException runnable,
    final Set<Class<? extends Exception>> mustIgnore,
    final Set<Class<? extends Exception>> mustTerminate,
    final boolean defaultTerminateUponException,
    final Consumer<Boolean> terminateListener,
    final Level startTerminateLogLevel,
    final Level runnableExceptionLogLevel,
    final Level overloadLogLevel)
  {
    if (period_s == null)
      throw new IllegalArgumentException ();
    return new RunnableInvoker (
      name,
      host,
      period_s,
      randomizeStartTime,
      overloadPolicy,
      runnable,
      mustIgnore,
      mustTerminate,
      defaultTerminateUponException,
      terminateListener,
      startTerminateLogLevel,
      runnableExceptionLogLevel,
      overloadLogLevel);
  }
  
  /** Creates a {@link RunnableInvoker} that constantly ("back-to-back") invokes a target {@link RunnableWithException}.
   * 
   * <p>
   * Apart from the absence of the specification of inter-invocation period and overload-related arguments,
   * we refer to the description of
   * {@link #periodicallyFromRunnable(
   *   java.lang.String,
   *   java.lang.Object,
   *   java.util.function.Supplier,
   *   boolean,
   *   org.javajdj.jservice.support.RunnableInvoker.OverloadPolicy,
   *   org.javajdj.jservice.support.RunnableInvoker.RunnableWithException,
   *   java.util.Set,
   *   java.util.Set,
   *   boolean,
   *   java.util.function.Consumer,
   *   java.util.logging.Level,
   *   java.util.logging.Level,
   *   java.util.logging.Level)}.
   * 
   * @param name See description and reference.
   * @param host See description and reference.
   * @param runnable See description and reference.
   * @param mustIgnore See description and reference.
   * @param mustTerminate See description and reference.
   * @param defaultTerminateUponException See description and reference.
   * @param terminateListener See description and reference.
   * @param startTerminateLogLevel See description and reference.
   * @param runnableExceptionLogLevel See description and reference.
   * 
   * @return See description and reference.
   *
   */
  public static final RunnableInvoker constantlyFromRunnable (
    final String name,
    final Object host,
    final RunnableWithException runnable,
    final Set<Class<? extends Exception>> mustIgnore,
    final Set<Class<? extends Exception>> mustTerminate,
    final boolean defaultTerminateUponException,
    final Consumer<Boolean> terminateListener,
    final Level startTerminateLogLevel,
    final Level runnableExceptionLogLevel)
  {
    return new RunnableInvoker (
      name,
      host,
      null,
      false,
      null,
      runnable,
      mustIgnore,
      mustTerminate,
      defaultTerminateUponException,
      terminateListener,
      startTerminateLogLevel,
      runnableExceptionLogLevel,
      null);
  }
  
  /** Creates a {@link RunnableInvoker} that invokes once a target {@link RunnableWithException}.
   * 
   * <p>
   * Apart from the absence of the specification of inter-invocation period and overload-related arguments,
   * we refer to the description of
   * {@link #periodicallyFromRunnable(
   *   java.lang.String,
   *   java.lang.Object,
   *   java.util.function.Supplier,
   *   boolean,
   *   org.javajdj.jservice.support.RunnableInvoker.OverloadPolicy,
   *   org.javajdj.jservice.support.RunnableInvoker.RunnableWithException,
   *   java.util.Set,
   *   java.util.Set,
   *   boolean,
   *   java.util.function.Consumer,
   *   java.util.logging.Level,
   *   java.util.logging.Level,
   *   java.util.logging.Level)}.
   * 
   * @param name See description and reference.
   * @param host See description and reference.
   * @param runnable See description and reference.
   * @param mustIgnore See description and reference.
   * @param mustTerminate See description and reference.
   * @param defaultTerminateUponException See description and reference.
   * @param terminateListener See description and reference.
   * @param startTerminateLogLevel See description and reference.
   * @param runnableExceptionLogLevel See description and reference.
   * 
   * @return See description and reference.
   *
   */
  public static final RunnableInvoker onceFromRunnable (
    final String name,
    final Object host,
    final RunnableWithException runnable,
    final Set<Class<? extends Exception>> mustIgnore,
    final Set<Class<? extends Exception>> mustTerminate,
    final boolean defaultTerminateUponException,
    final Consumer<Boolean> terminateListener,
    final Level startTerminateLogLevel,
    final Level runnableExceptionLogLevel)
  {
    return new RunnableInvoker (
      name,
      host,
      null,
      false,
      null,
      () ->
      {
        if (runnable != null)
          runnable.run ();
        while (! Thread.currentThread ().isInterrupted ())
        {
          try
          {
            Thread.currentThread ().join (1000L);
          }
          catch (InterruptedException ie)
          {
            return;
          }
        }        
      },
      mustIgnore,
      mustTerminate,
      defaultTerminateUponException,
      terminateListener,
      startTerminateLogLevel,
      runnableExceptionLogLevel,
      null);
  }
  
  /** Creates a {@link RunnableInvoker} that (attempts to) periodically invoke a target
   *  {@link SupplierWithException} followed by a target {@link ConsumerWithException}, passing the supplied value.
   * 
   * <p>
   * Apart from the construction of the target {@link RunnableWithException},
   * we refer to the description of
   * {@link #periodicallyFromRunnable(
   *   java.lang.String,
   *   java.lang.Object,
   *   java.util.function.Supplier,
   *   boolean,
   *   org.javajdj.jservice.support.RunnableInvoker.OverloadPolicy,
   *   org.javajdj.jservice.support.RunnableInvoker.RunnableWithException,
   *   java.util.Set,
   *   java.util.Set,
   *   boolean,
   *   java.util.function.Consumer,
   *   java.util.logging.Level,
   *   java.util.logging.Level,
   *   java.util.logging.Level)}.
   * 
   * @param <T> The type of data transferred from {@link SupplierWithException} to {@link ConsumerWithException}.
   * 
   * @param name See description and reference.
   * @param host See description and reference.
   * @param period_s See description and reference.
   * @param randomizeStartTime See description and reference.
   * @param overloadPolicy See description and reference.
   * @param resultSupplier The {@link SupplierWithException}, may be {@code null}, in which case {@code null} is supplied.
   * @param resultConsumer The {@link ConsumerWithException}, may be {@code null}.
   * @param mustIgnore See description and reference.
   * @param mustTerminate See description and reference.
   * @param defaultTerminateUponException See description and reference.
   * @param terminateListener See description and reference.
   * @param startTerminateLogLevel See description and reference.
   * @param runnableExceptionLogLevel See description and reference.
   * @param overloadLogLevel See description and reference.
   * 
   * @return See description and reference.
   *
   */
  public static final <T> RunnableInvoker periodicallyFromSupplierConsumerChain (
    final String name,
    final Object host,
    final Supplier<Double> period_s,
    final boolean randomizeStartTime,
    final OverloadPolicy overloadPolicy,
    final SupplierWithException<T> resultSupplier,
    final ConsumerWithException<T> resultConsumer,
    final Set<Class<? extends Exception>> mustIgnore,
    final Set<Class<? extends Exception>> mustTerminate,
    final boolean defaultTerminateUponException,
    final Consumer<Boolean> terminateListener, // Argument: Termination due to abnormal condition (true) or interrupt (false).
    final Level startTerminateLogLevel,
    final Level runnableExceptionLogLevel,
    final Level overloadLogLevel)
  {
    return new RunnableInvoker (
      name,
      host,
      period_s,
      randomizeStartTime,
      overloadPolicy,
      () ->
      {
        final T t = (resultSupplier != null) ? resultSupplier.get () : null;
        if (resultConsumer != null)
          resultConsumer.accept (t);
      },
      mustIgnore,
      mustTerminate,
      defaultTerminateUponException,
      terminateListener,
      startTerminateLogLevel,
      runnableExceptionLogLevel,
      overloadLogLevel);
  }
  
  /** Creates a {@link RunnableInvoker} that constantly ("back-to-back") invokes a target
   *  {@link SupplierWithException} followed by a target {@link ConsumerWithException}, passing the supplied value.
   * 
   * <p>
   * Apart from the absence of the specification of inter-invocation period and overload-related arguments
   * and apart from the construction of the target {@link RunnableWithException},
   * we refer to the description of
   * {@link #periodicallyFromRunnable(
   *   java.lang.String,
   *   java.lang.Object,
   *   java.util.function.Supplier,
   *   boolean,
   *   org.javajdj.jservice.support.RunnableInvoker.OverloadPolicy,
   *   org.javajdj.jservice.support.RunnableInvoker.RunnableWithException,
   *   java.util.Set,
   *   java.util.Set,
   *   boolean,
   *   java.util.function.Consumer,
   *   java.util.logging.Level,
   *   java.util.logging.Level,
   *   java.util.logging.Level)}.
   * 
   * @param <T> The type of data transferred from {@link SupplierWithException} to {@link ConsumerWithException}.
   * 
   * @param name See description and reference.
   * @param host See description and reference.
   * @param resultSupplier The {@link SupplierWithException}, may be {@code null}, in which case {@code null} is supplied.
   * @param resultConsumer The {@link ConsumerWithException}, may be {@code null}.
   * @param mustIgnore See description and reference.
   * @param mustTerminate See description and reference.
   * @param defaultTerminateUponException See description and reference.
   * @param terminateListener See description and reference.
   * @param startTerminateLogLevel See description and reference.
   * @param runnableExceptionLogLevel See description and reference.
   * 
   * @return See description and reference.
   *
   */
  public static final <T> RunnableInvoker constantlyFromSupplierConsumerChain (
    final String name,
    final Object host,
    final SupplierWithException<T> resultSupplier,
    final ConsumerWithException<T> resultConsumer,
    final Set<Class<? extends Exception>> mustIgnore,
    final Set<Class<? extends Exception>> mustTerminate,
    final boolean defaultTerminateUponException,
    final Consumer<Boolean> terminateListener, // Argument: Termination due to abnormal condition (true) or interrupt (false).
    final Level startTerminateLogLevel,
    final Level runnableExceptionLogLevel)
  {
    return new RunnableInvoker (
      name,
      host,
      null,
      false,
      null,
      () ->
      {
        final T t = (resultSupplier != null) ? resultSupplier.get () : null;
        if (resultConsumer != null)
          resultConsumer.accept (t);
      },
      mustIgnore,
      mustTerminate,
      defaultTerminateUponException,
      terminateListener,
      startTerminateLogLevel,
      runnableExceptionLogLevel,
      null);
  }
  
  /** Creates a {@link RunnableInvoker} that invokes once a target
   *  {@link SupplierWithException} followed by a target {@link ConsumerWithException},
   *  passing the supplied value.
   * 
   * <p>
   * Apart from the absence of the specification of inter-invocation period and overload-related arguments
   * and apart from the construction of the target {@link RunnableWithException},
   * we refer to the description of
   * {@link #periodicallyFromRunnable(
   *   java.lang.String,
   *   java.lang.Object,
   *   java.util.function.Supplier,
   *   boolean,
   *   org.javajdj.jservice.support.RunnableInvoker.OverloadPolicy,
   *   org.javajdj.jservice.support.RunnableInvoker.RunnableWithException,
   *   java.util.Set,
   *   java.util.Set,
   *   boolean,
   *   java.util.function.Consumer,
   *   java.util.logging.Level,
   *   java.util.logging.Level,
   *   java.util.logging.Level)}.
   * 
   * @param <T> The type of data transferred from {@link SupplierWithException} to {@link ConsumerWithException}.
   * 
   * @param name See description and reference.
   * @param host See description and reference.
   * @param resultSupplier The {@link SupplierWithException}, may be {@code null}, in which case {@code null} is supplied.
   * @param resultConsumer The {@link ConsumerWithException}, may be {@code null}.
   * @param mustIgnore See description and reference.
   * @param mustTerminate See description and reference.
   * @param defaultTerminateUponException See description and reference.
   * @param terminateListener See description and reference.
   * @param startTerminateLogLevel See description and reference.
   * @param runnableExceptionLogLevel See description and reference.
   * 
   * @return See description and reference.
   *
   */
  public static final <T> RunnableInvoker onceFromSupplierConsumerChain (
    final String name,
    final Object host,
    final SupplierWithException<T> resultSupplier,
    final ConsumerWithException<T> resultConsumer,
    final Set<Class<? extends Exception>> mustIgnore,
    final Set<Class<? extends Exception>> mustTerminate,
    final boolean defaultTerminateUponException,
    final Consumer<Boolean> terminateListener, // Argument: Termination due to abnormal condition (true) or interrupt (false).
    final Level startTerminateLogLevel,
    final Level runnableExceptionLogLevel)
  {
    return new RunnableInvoker (
      name,
      host,
      null,
      false,
      null,
      () ->
      {
        final T t = (resultSupplier != null) ? resultSupplier.get () : null;
        if (resultConsumer != null)
          resultConsumer.accept (t);
        while (! Thread.currentThread ().isInterrupted ())
        {
          try
          {
            Thread.currentThread ().join (1000L);
          }
          catch (InterruptedException ie)
          {
            return;
          }
        }        
      },
      mustIgnore,
      mustTerminate,
      defaultTerminateUponException,
      terminateListener,
      startTerminateLogLevel,
      runnableExceptionLogLevel,
      null);
  }
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // NAME
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final String name;

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // HOST
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final Object host;

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // PERIOD SUPPLIER
  // RANDOMIZE START TIME
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final Supplier<Double> period_s;

  private final boolean randomizeStartTime;

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // OVERLOAD
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public enum OverloadPolicy
  {
    IGNORE_AND_DROP,
    EXIT;
  }

  private final OverloadPolicy overloadPolicy;

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // RUNNABLE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final RunnableWithException runnable;

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // RUNNABLE EXCEPTION HANDLING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final Set<Class<? extends Exception>> mustTerminate;

  private final Set<Class<? extends Exception>> mustIgnore;

  private final boolean defaultTerminateUponException;

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // TERMINATION LISTENER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final Consumer<Boolean> terminateListener;

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGING [LEVELS]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final Level startTerminateLogLevel;

  private final Level runnableExceptionLogLevel;

  private final Level overloadLogLevel;

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // Runnable
  // RUN
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public void run ()
  {
    if (this.startTerminateLogLevel != null)
      LOG.log (this.startTerminateLogLevel, "Starting {0}{1}.",
        new Object[]
        {
          this.name,
          this.host != null ? (" on " + this.host.toString ()) : ""
        });
    boolean firstCycle = true;
    boolean hadException = false;
    while (! Thread.currentThread ().isInterrupted ())
    {
      try
      {
        if (firstCycle)
        {
          if (this.period_s != null && this.randomizeStartTime)
            Thread.sleep ((long) (new Random ().nextDouble () * this.period_s.get () * 1000));
          firstCycle = false;
        }
        if (hadException)
        {
          // Quick and dirty method to prevent continuous operation in case of (early) ignored Exceptions.
          // We should actually compensate for the time spent already...
          if (this.period_s != null)
            Thread.sleep ((long) (this.period_s.get () * 1000));
          hadException = false;
        }
        // Mark start of sojourn.
        final long timeBeforeInvocation_ms = System.currentTimeMillis ();
        //
        // Invoke the runnable if present.
        //
        if (this.runnable != null)
          this.runnable.run ();
        if (this.period_s != null)
        {
          // Mark end of sojourn.
          final long timeAfterConsumer_ms = System.currentTimeMillis ();
          // Calculate sojourn.
          final long sojourn_ms = timeAfterConsumer_ms - timeBeforeInvocation_ms;
          // Find out the remaining time to wait in order to respect the given period.
          final long remainingSleep_ms = ((long) (this.period_s.get () * 1000)) - sojourn_ms;
          if (remainingSleep_ms < 0)
          {
            if (this.overloadLogLevel != null)
              LOG.log (this.overloadLogLevel, "{0} cannot meet timing settings{1}.",
                new Object[]
                {
                  this.name, this.host != null ? (" on " + this.host.toString ()) : ""
                });
            switch (this.overloadPolicy)
            {
              case IGNORE_AND_DROP:
                // NOTHING TO DO...
                break;
              case EXIT:
                if (this.startTerminateLogLevel != null)
                  LOG.log (this.startTerminateLogLevel, "Terminating (overload) {0}{1}.",
                    new Object[]
                    {
                      this.name,
                      this.host != null ? (" on " + this.host.toString ()) : ""
                    });
                if (this.terminateListener != null)
                  this.terminateListener.accept (true);
                return;
              default:
                throw new RuntimeException ();
            }
            hadException = true;
          }
          else if (remainingSleep_ms > 0)
            Thread.sleep (remainingSleep_ms);
        }
      }
      catch (InterruptedException ie)
      {
        if (this.startTerminateLogLevel != null)
          LOG.log (this.startTerminateLogLevel, "Terminating (by request) {0}{1}.",
            new Object[]
            {
              this.name,
              this.host != null ? (" on " + this.host.toString ()) : ""
            });
        if (this.terminateListener != null)
          this.terminateListener.accept (false);
        return;
      }
      catch (Exception e)
      {
        Boolean terminate = null;
        if (this.mustIgnore != null)
          for (final Class<? extends Exception> c: this.mustIgnore)
            if (c.isInstance (e))
            {
              terminate = false;
              break;
            }
        if (terminate == null && this.mustTerminate != null)
          for (final Class<? extends Exception> c: this.mustTerminate)
            if (c.isInstance (e))
            {
              terminate = true;
              break;
            }
        if (terminate == null)
          terminate = this.defaultTerminateUponException;
        if (this.runnableExceptionLogLevel != null)
          LOG.log (this.runnableExceptionLogLevel, "{0} ({1}) in {2}{3}: {4}",
            new Object[]
            {
              e.getClass ().getSimpleName (),
              terminate ? "terminates" : "ignored",
              this.name,
              this.host != null ? (" on " + this.host.toString ()) : "",
              Arrays.toString (e.getStackTrace ())
            });
        if (terminate)
        {
          if (this.terminateListener != null)
            this.terminateListener.accept (true);
          if (this.startTerminateLogLevel != null)
            LOG.log (this.startTerminateLogLevel, "Terminating ({0}) {1}{2}.",
              new Object[]
              {
                e.getClass ().getSimpleName (),
                this.name,
                this.host != null ? (" on " + this.host.toString ()) : ""
              });
          return;
        }
        else
          hadException = true;
      }
    }
    if (this.startTerminateLogLevel != null)
      LOG.log (this.startTerminateLogLevel, "Terminating (by request) {0}{1}.",
        new Object[]
        {
          this.name,
          this.host != null ? (" on " + this.host.toString ()) : ""
        });
    if (this.terminateListener != null)
      this.terminateListener.accept (false);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}

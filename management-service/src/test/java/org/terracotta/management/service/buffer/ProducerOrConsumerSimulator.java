/*
 *
 *  The contents of this file are subject to the Terracotta Public License Version
 *  2.0 (the "License"); You may not use this file except in compliance with the
 *  License. You may obtain a copy of the License at
 *
 *  http://terracotta.org/legal/terracotta-public-license.
 *
 *  Software distributed under the License is distributed on an "AS IS" basis,
 *  WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 *  the specific language governing rights and limitations under the License.
 *
 *  The Covered Software is Entity Management Service.
 *
 *  The Initial Developer of the Covered Software is
 *  Terracotta, Inc., a Software AG company
 *
 */
package org.terracotta.management.service.buffer;

import java.util.concurrent.CountDownLatch;
import java.util.function.IntPredicate;

import static org.terracotta.management.service.TestConstants.BUFFER_SIZE;

/**
 * The producer or consumer simulator thread.
 *
 * @author RKAV
 */
public final class ProducerOrConsumerSimulator implements Runnable {
  private static final Object LOCK = new Object();

  private final CountDownLatch doneLatch;
  private final CountDownLatch startLatch;
  private final IntPredicate producerOrConsumer;
  private final IntPredicate bufferFull;
  private final boolean allowOverflow;
  private final boolean isProducer;
  private final int partitionNumber;

  public ProducerOrConsumerSimulator(CountDownLatch doneLatch,
                                     CountDownLatch startLatch,
                                     IntPredicate actualProducerOrConsumer,
                                     IntPredicate bufferFull,
                                     boolean allowOverflow,
                                     int partitionNumber) {
    this.doneLatch = doneLatch;
    this.startLatch = startLatch;
    this.producerOrConsumer = actualProducerOrConsumer;
    this.allowOverflow = allowOverflow;
    this.partitionNumber = partitionNumber;
    if (bufferFull == null) {
      this.isProducer = false;
      this.bufferFull = (x) -> false;
    } else {
      this.isProducer = true;
      this.bufferFull = bufferFull;
    }
  }

  @Override
  public void run() {
    try {
      startLatch.await();
      int i = 0;
      int numFailures = 0;
      while (i < (BUFFER_SIZE * 4) - 1) {
        if (producerOrConsumer.test(partitionNumber)) {
          numFailures = 0;
          i++;
          if (!isProducer) {
            notifyAndYield(allowOverflow);
          }
        } else {
          numFailures++;
          yieldLoop();
        }
        if (isProducer && !allowOverflow) {
          waitIfBufferFull();
        }
        if (numFailures++ > 100 && !isProducer && doneLatch.getCount() == 1) {
          // only the consumer is waiting for buffer..all producers are gone
          break;
        }
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    } finally {
      doneLatch.countDown();
    }
  }

  /**
   * Wait/block if the buffer is full.
   * <p>
   * Used by the producer to wait for the consumer to catch up.
   *
   * @throws InterruptedException
   */
  private void waitIfBufferFull() throws InterruptedException {
    synchronized (LOCK) {
      while (bufferFull.test(partitionNumber)) {
        // wait for producer and consumer to synchronize
        LOCK.wait(500);
      }
    }
  }
  /**
   * Consumer notifies if it was able to consume something
   * <p>
   * Note: the lock is simply to avoid exception as the consequence of a missing signal
   * is just an additional 500ms delay..
   *
   * @param allowOverflow signifies whether consumer should waste some time..
   */
  private void notifyAndYield(boolean allowOverflow) {
    synchronized (LOCK){
      LOCK.notify();
    }
    if (allowOverflow) {
      // slow the consumer thread down even further..
      yieldLoop();
    }
  }

  /**
   * Busy loop on CPU with some yields. Useful to inject some delays/
   */
  private void yieldLoop() {
    float f = 1.0f;
    Thread.yield();
    for (int i = 0; i < 10000; i++) {
      f = f * i * 2.0f;
    }
    Thread.yield();
  }
}

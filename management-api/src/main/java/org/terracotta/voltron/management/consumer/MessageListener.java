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
 *  The Covered Software is Entity Management API.
 *
 *  The Initial Developer of the Covered Software is
 *  Terracotta, Inc., a Software AG company
 *
 */
package org.terracotta.voltron.management.consumer;

import java.util.Collection;

/**
 * Required interface that must be implemented by consumers to collect statistics or
 * other events that are periodically pushed by managed entities on the active.
 * <p>
 * Consumer (typically management entity) can implement this interface in order to get
 * the buffered messages periodically and push it to the remote management system.
 * <p>
 * This must be implemented carefully to avoid packet flooding between stripes and
 * management systems. If the {@code postMessages} is called and an ack is pending for the
 * previously posted message, throw the exception {@link PreviousMessageAckPendingException} to
 * signify that the previously posted message is either still in transit or the acknowledgement for
 * it has not arrived.
 *
 * @author RKAV
 */
@FunctionalInterface
public interface MessageListener<M> {
  /**
   * Invokes the callback periodically when two conditions are met.
   *   1. Statistics or other events are available in the {@code message ring buffer}
   *   2. The interval specified when this {@code ManagementMessageCallback} was setup has
   *      elapsed.
   * <p>
   * On return from this method, the management messages are cleared in the
   * unless an exception was thrown by the consumer for this method.
   *
   * @param bufferedMessages the messages that were buffered.
   * @throws PreviousMessageAckPendingException if the message was not send out as the previous
   *         post is still not send out.
   */
  void postMessages(Collection<M> bufferedMessages) throws PreviousMessageAckPendingException;
}
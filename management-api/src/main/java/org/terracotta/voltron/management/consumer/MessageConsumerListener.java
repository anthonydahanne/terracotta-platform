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

/**
 * Listener interface used by the consumer (i.e management entity) which will be invoked when
 * a message producer of a given message type is created by the managed entity.
 *
 * @author RKAV
 */
@FunctionalInterface
public interface MessageConsumerListener<M> {
  void onCreate(MessageConsumer<M> messageConsumer);
}

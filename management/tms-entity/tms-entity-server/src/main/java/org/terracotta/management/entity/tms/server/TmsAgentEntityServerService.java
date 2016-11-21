/*
 * Copyright Terracotta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terracotta.management.entity.tms.server;

import org.terracotta.entity.ServiceRegistry;
import org.terracotta.management.entity.tms.TmsAgent;
import org.terracotta.management.entity.tms.TmsAgentConfig;
import org.terracotta.management.entity.tms.TmsAgentVersion;
import org.terracotta.voltron.proxy.SerializationCodec;
import org.terracotta.voltron.proxy.server.ProxyServerEntityService;

/**
 * @author Mathieu Carbou
 */
public class TmsAgentEntityServerService extends ProxyServerEntityService<TmsAgentConfig> {

  public TmsAgentEntityServerService() {
    super(TmsAgent.class, TmsAgentConfig.class, new SerializationCodec());
  }

  @Override
  public TmsAgentServerEntity createActiveEntity(ServiceRegistry registry, TmsAgentConfig tmsAgentConfig) {
    return new TmsAgentServerEntity(new TmsAgentImpl(tmsAgentConfig, registry));
  }

  @Override
  public long getVersion() {
    return TmsAgentVersion.LATEST.version();
  }

  @Override
  public boolean handlesEntityType(String typeName) {
    return TmsAgentConfig.ENTITY_TYPE.equals(typeName);
  }


}

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
package org.terracotta.management.service.monitoring.registry.provider;

import com.tc.classloader.CommonComponent;
import org.terracotta.management.model.capabilities.descriptors.Descriptor;
import org.terracotta.management.model.context.Context;
import org.terracotta.management.registry.action.ExposedObject;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

@CommonComponent
public class AliasBindingManagementProvider<T extends AliasBinding> extends AbstractConsumerManagementProvider<T> {

  public AliasBindingManagementProvider(Class<? extends T> type) {
    super(type);
  }

  @Override
  public ExposedAliasBinding<T> register(T managedObject) {
    if(getManagedType() == managedObject.getClass()) {
      return (ExposedAliasBinding<T>) super.register(managedObject);
    }
    return null;
  }

  @Override
  public ExposedAliasBinding<T> unregister(T managedObject) {
    if(getManagedType() == managedObject.getClass()) {
      return (ExposedAliasBinding<T>) super.unregister(managedObject);
    }
    return null;
  }

  @Override
  protected ExposedAliasBinding<T> internalWrap(Context context, T managedObject) {
    return new ExposedAliasBinding<>(context, managedObject);
  }

  @CommonComponent
  public static class ExposedAliasBinding<T extends AliasBinding> implements ExposedObject<T> {

    private final T binding;
    private final Context context;

    public ExposedAliasBinding(Context context, T binding) {
      this.binding = Objects.requireNonNull(binding);
      this.context = context.with("alias", binding.getAlias());
    }

    public T getBinding() {
      return binding;
    }

    @Override
    public Context getContext() {
      return context;
    }

    @Override
    public ClassLoader getClassLoader() {
      return binding.getValue().getClass().getClassLoader();
    }

    @Override
    public T getTarget() {
      return binding;
    }

    @Override
    public Collection<? extends Descriptor> getDescriptors() {
      return Collections.emptyList();
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      ExposedAliasBinding<?> that = (ExposedAliasBinding<?>) o;
      return binding.equals(that.binding);

    }
    @Override
    public int hashCode() {
      return binding.hashCode();
    }
  }
}

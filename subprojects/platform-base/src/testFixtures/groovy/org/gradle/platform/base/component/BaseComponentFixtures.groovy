/*
 * Copyright 2015 the original author or authors.
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
 */

package org.gradle.platform.base.component

import org.gradle.internal.reflect.Instantiator
import org.gradle.model.internal.core.ModelNode
import org.gradle.model.internal.core.ModelReference
import org.gradle.model.internal.core.ModelRegistrations
import org.gradle.model.internal.core.ModelRuleExecutionException
import org.gradle.model.internal.type.ModelType;
import org.gradle.model.internal.core.MutableModelNode
import org.gradle.model.internal.core.rule.describe.SimpleModelRuleDescriptor
import org.gradle.model.internal.fixture.TestNodeInitializerRegistry
import org.gradle.model.internal.registry.ModelRegistry
import org.gradle.platform.base.ComponentSpec
import org.gradle.platform.base.ComponentSpecIdentifier

class BaseComponentFixtures {

    static <T extends BaseComponentSpec> T create(Class<? extends ComponentSpec> type, Class<T> implType,  ModelRegistry modelRegistry, ComponentSpecIdentifier componentId, Instantiator instantiator, File baseDir = null) {
        createNode(type, implType,  modelRegistry, componentId, instantiator, baseDir)
            .asMutable(ModelType.of(type), new SimpleModelRuleDescriptor(componentId.getName()), Collections.emptyList()).getInstance()
            //.asImmutable(ModelType.of(type), new SimpleModelRuleDescriptor(componentId.getName())).getInstance()
    }

    static <T extends BaseComponentSpec> MutableModelNode createNode(Class<? extends ComponentSpec> type, Class<T> implType,  ModelRegistry modelRegistry, ComponentSpecIdentifier componentId, Instantiator instantiator, File baseDir = null) {
        try {
            modelRegistry.registerInstance("TestNodeInitializerRegistry", TestNodeInitializerRegistry.INSTANCE)
            modelRegistry.register(
                ModelRegistrations.unmanagedInstanceOf(ModelReference.of(componentId.name, type), {
                    BaseComponentSpec.create(type, implType, componentId, it, instantiator)
                })
                    .descriptor(componentId.name)
                    .build()
            ).atState(componentId.name, ModelNode.State.Initialized)
        } catch (ModelRuleExecutionException e) {
            throw e.cause
        }
    }

}

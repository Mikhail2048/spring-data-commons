/*
 * Copyright 2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.mapping.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.PreferredConstructor;
import org.springframework.lang.Nullable;

/**
 * @author Christoph Strobl
 * @since 2020/10
 */
public class ConfigurableTypeConstructor<T> extends PreferredConstructor implements EntityInstantiatorAware {

	private List<String> args;
	private EntityInstantiator entityInstantiator;

	public ConfigurableTypeConstructor(List<String> args, @Nullable EntityInstantiator entityInstantiator) {

		this.args = args;
		this.entityInstantiator = entityInstantiator;
	}

	public static <T> ConfigurableTypeConstructor<T> noArgsConstructor(Supplier<T> newInstanceSupplier) {
		return ConfigurableTypeConstructor.<T>builder().noArgs(newInstanceSupplier);
	}

	public static <T> ConfigurableTypeConstructorBuilder<T> builder() {
		return new ConfigurableTypeConstructorBuilder<>();
	}

	@Override
	public boolean isConstructorParameter(PersistentProperty property) {

		if (args.contains(property.getName())) {
			return true;
		}

		return super.isConstructorParameter(property);
	}

	public List<String> getParameterNames() {
		return args;
	}

	@Override
	public boolean hasParameters() {
		return !args.isEmpty();
	}

	@Nullable
	@Override
	public EntityInstantiator getEntityInstantiator() {
		return entityInstantiator;
	}

	public static class ConfigurableTypeConstructorBuilder<T> {

		private final List<String> ctorArgs;

		public ConfigurableTypeConstructorBuilder() {
			this.ctorArgs = new ArrayList<>();
		}

		public ConfigurableTypeConstructorBuilder<T> args(String... args) {

			this.ctorArgs.addAll(Arrays.asList(args));
			return this;
		}

		public ConfigurableTypeConstructor<T> noArgs(Supplier<T> newInstanceSupplier) {
			return newInstanceFunction((args) -> newInstanceSupplier.get());
		}

		public ConfigurableTypeConstructor<T> newInstanceFunction(Function<Object[], T> function) {
			return new ConfigurableTypeConstructor<>(ctorArgs, new FunctionalEntityInstantiator<>(ctorArgs, function));
		}
	}
}
package io.github.joht.snippets.cdi.generictype;

import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.InjectionPoint;

/**
 * Shows how to write a CDI producer for a generic type with one type parameter
 * and how to get and use the type parameter inside the producer.
 * <p>
 * Besides the possibility to write producer for on specific type parameter, it
 * is shown that it is also possible to do it for any type parameter in a single
 * producer method. It needs to be <code>@Dependent</code> scope (default for
 * <code>@Producer</code>) for that.
 * <p>
 * Informations of the {@link InjectionPoint} lead to the specific type
 * parameter that is used for <code>@Inject</code>.
 */
class CdiProducerWithTypeParameter {

	@Produces
	public <T> TypedInterface<T> produceTypedInterface(InjectionPoint injectionPoint) {
		Class<?> typeArgument = GenericTypeArgument.ofMemberReturnType(injectionPoint.getMember())
				.getFirstTypeArgument()
				.orElseThrow(IllegalStateException::new);
		return new TypedInterface<T>() {

			@Override
			public String getName() {
				return typeArgument.getSimpleName();
			}

			@Override
			@SuppressWarnings("unchecked")
			public T getType() {
				return (T) typeArgument;
			}
		};
	}

	public static interface TypedInterface<T> {
		String getName();

		T getType();
	}
}
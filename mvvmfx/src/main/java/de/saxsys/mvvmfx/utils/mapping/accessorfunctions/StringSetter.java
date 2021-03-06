package de.saxsys.mvvmfx.utils.mapping.accessorfunctions;

import java.util.function.BiConsumer;


/**
 * A functional interface to define a setter method of type {@link String}.
 *
 * @param <M>
 *            the generic type of the model.
 */
@FunctionalInterface
public interface StringSetter<M> extends BiConsumer<M, String> {
	
	/**
	 * @param model
	 *            the model instance.
	 * @param value
	 *            the new value to be set.
	 */
	@Override
	void accept(M model, String value);
}

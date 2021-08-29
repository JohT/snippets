package io.github.joht.snippets.cdi.generictype;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

/**
 * Extracts the generic type parameter from fields, methods, ... .
 */
class GenericTypeArgument {

	private final Type genericType;

	public static final GenericTypeArgument ofMemberReturnType(Member member) {
		if (member instanceof Field) {
			return ofField((Field) member);
		}
		if (member instanceof Method) {
			return ofMethodReturnType((Method) member);
		}
		return ofVoid();
	}
	
	public static final GenericTypeArgument ofField(Field member) {
		return new GenericTypeArgument(((Field) member).getGenericType());
	}
	
	public static final GenericTypeArgument ofMethodReturnType(Method member) {
		return new GenericTypeArgument(((Method) member).getGenericReturnType());
	}
	
	public static final GenericTypeArgument ofVoid() {
		return new GenericTypeArgument(Void.class);
	}
	
	public GenericTypeArgument(Type genericType) {
		this.genericType = genericType;
	}
	
	public Optional<Class<?>> getFirstTypeArgument() {
		return getTypeArguments().stream().findFirst().filter(Class.class::isInstance).map (Class.class::cast);
	}

	public Collection<Type> getTypeArguments() {
		if (!(genericType instanceof ParameterizedType)) {
			return Collections.emptyList();
		}
		return Arrays.asList(((ParameterizedType) genericType).getActualTypeArguments());
	}

	@Override
	public String toString() {
		return "GenericTypeArgument [genericType=" + genericType + "]";
	}
}
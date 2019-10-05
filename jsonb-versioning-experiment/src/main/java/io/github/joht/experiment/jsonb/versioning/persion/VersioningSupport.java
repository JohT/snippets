package io.github.joht.experiment.jsonb.versioning.persion;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Introduces version support for JSON deserialization.
 * <p>
 * The type, that should be deserializable from different versions <br>
 * <b>needs to provide</b>:
 * <ul>
 * <li>A public static method that takes a {@link String} and returns an int (may be named "getVersion")
 * <li>A public static method for every version that takes the old type and returns the current type (may be named "update")
 * <li>A public static subclass for every version called V + version number (e.g. "V1").
 * </ul>
 * <p>
 * This class is independent of any json/serialization libraries, even if it was written with JSON-B in mind.
 * 
 * @author JohT
 * @param <T> Type that provides sub-types for every version
 */
public class VersioningSupport<T> {

    private final Class<T> versionedType;
    private final Function<Object, String> serializer;
    private final BiFunction<String, Class<?>, Object> deserializer;

    public VersioningSupport(Class<T> versionedType, Function<Object, String> serializer,
            BiFunction<String, Class<?>, Object> deserializer) {
        this.versionedType = versionedType;
        this.serializer = serializer;
        this.deserializer = deserializer;
    }

    /**
     * Converts the given object to the given returnType.<br>
     * This is done by serializing it intermediately to JSON and then back to the given returnType.
     * <p>
     * This method is useful, when an object should be converted to e.g. "JsonObject".<br>
     * It is meant to be used for delegating to the base serialization to implement "adaptToJson".
     * 
     * @param objectToBeConverted
     * @param returnType {@link Class}
     * @return converted object
     */
    @SuppressWarnings("unchecked")
    public <R> R reserialize(T objectToBeConverted, Class<R> returnType) {
        return (R) deserializer.apply(serializer.apply(objectToBeConverted), returnType);
    }

    /**
     * Determines the version of the given json, <br>
     * converts it to the corresponding subtype and <br>
     * creates the final object by updating the subtype.
     * 
     * @param json {@link String}
     * @return T
     */
    public T adaptFromJson(String json) {
        Method versionMethod = findVersionMethod();
        int version = getVersionOf(json, versionMethod);
        if (version == 0) {
            return currentVersionOf(json);
        }
        Class<?> versionedTypeToDeserialize = getVersionedType(version);
        Object deserialized = deserializer.apply(json, versionedTypeToDeserialize);
        Method updateMethod = findUpdateMethod(versionedTypeToDeserialize);
        return upcast(json, deserialized, updateMethod);
    }

    @SuppressWarnings("unchecked")
    private T currentVersionOf(String json) {
        return (T) deserializer.apply(json, versionedType);
    }

    // Note: Only supports subclasses named equally to the version right now.
    // Example: Object "Person" with version "1" means there needs to be a static subclass "V1" inside "Person".
    private Class<?> getVersionedType(int version) {
        String versionedTypeName = versionedType.getName() + "$V" + version;
        try {
            return Class.forName(versionedTypeName);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("There is no class " + versionedTypeName + " for Version " + version, e);
        }

    }

    // Note: Only supports static methods taking a String an returning a int.
    // Example: public static final int Person.getVersion(String json)
    private Method findVersionMethod() {
        return Stream.of(versionedType.getMethods())//
                .filter(method -> Modifier.isStatic(method.getModifiers()))
                .filter(method -> method.getParameterCount() == 1)
                .filter(method -> method.getParameterTypes()[0].equals(String.class))
                .filter(method -> int.class.equals(method.getReturnType()))
                .findFirst().get();
    }

    private static int getVersionOf(String json, Method versionMethod) {
        try {
            return (int) versionMethod.invoke(null, json);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new IllegalArgumentException("Error getting version of " + json, e);
        }
    }

    // Note: Only supports public static methods taking the object to upcast and returning the current version type.
    // Example: public static final Person update(Persion.V1 previous)
    private Method findUpdateMethod(Class<?> versionedTypeToDeserialize) {
        return Stream.of(versionedType.getMethods())//
                .filter(method -> Modifier.isStatic(method.getModifiers()))
                .filter(method -> method.getParameterCount() == 1)
                .filter(method -> method.getParameterTypes()[0].equals(versionedTypeToDeserialize))
                .filter(method -> versionedType.equals(method.getReturnType()))
                .findFirst().get();
    }

    @SuppressWarnings("unchecked")
    private static <T> T upcast(String json, Object deserialized, Method upcaster) {
        try {
            return (T) upcaster.invoke(null, deserialized);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new IllegalArgumentException("Error during upcasting " + json, e);
        }
    }

    @Override
    public String toString() {
        return "VersioningSupport [versionedType=" + versionedType + "]";
    }
}
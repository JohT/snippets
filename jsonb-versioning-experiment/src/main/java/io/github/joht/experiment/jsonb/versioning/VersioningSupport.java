package io.github.joht.experiment.jsonb.versioning;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Introduces an idea for version support for JSON deserialization.
 * <p>
 * The type, that should be deserializable from different versions <br>
 * <b>needs to provide</b>:
 * <ul>
 * <li>A static method containing "version" in its name, takes a {@link String} and returns an int.
 * <li>A public static subclass for every version called V + version number (e.g. "V1").
 * <li>A method inside the subclass (old version), that creates a new object of the current version.
 * </ul>
 * <p>
 * This class is independent of any json/serialization libraries, even if it was written with JSON-B in mind.
 * 
 * @author JohT
 * @param <T> Type that provides sub-types for every version
 */
public class VersioningSupport<T> {

    private final Class<T> typeOfCurrentVersion;
    private final Function<Object, String> serializer;
    private final BiFunction<String, Class<?>, Object> deserializer;
    private final Method versionMethod;


    public static final boolean isVersioningSupported(Class<?> typeOfCurrentVersion) {
        return findVersionMethod(typeOfCurrentVersion).isPresent();
    }

    public VersioningSupport(Class<T> type, Function<Object, String> serializer, BiFunction<String, Class<?>, Object> deserializer) {
        this.typeOfCurrentVersion = type;
        this.serializer = serializer;
        this.deserializer = deserializer;
        this.versionMethod = findVersionMethod(type).orElseThrow(this::versioningNotSupported);
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
        int version = getVersionOf(json, versionMethod);
        if (version == 0) {
            return currentVersionOf(json);
        }
        Class<?> versionedTypeToDeserialize = getVersionedType(version);
        Object deserialized = deserializer.apply(json, versionedTypeToDeserialize);
        Method updateMethod = findUpdateMethod(versionedTypeToDeserialize);
        return update(json, deserialized, updateMethod);
    }

    @SuppressWarnings("unchecked")
    private T currentVersionOf(String json) {
        return (T) deserializer.apply(json, typeOfCurrentVersion);
    }

    // Note: Only supports subclasses named equally to the version right now.
    // Example: Object "Person" with version "1" means there needs to be a static subclass "V1" inside "Person".
    private Class<?> getVersionedType(int version) {
        String versionedTypeName = typeOfCurrentVersion.getName() + "$V" + version;
        try {
            return Class.forName(versionedTypeName);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("There is no class " + versionedTypeName + " for Version " + version, e);
        }
    }

    // Note: Only supports static methods containing "version" in their name, taking a String and returning an int.
    // Example: public static final int Person.versionOf(String json)
    private static Optional<Method> findVersionMethod(Class<?> typeOfCurrentVersion) {
        return Stream.of(typeOfCurrentVersion.getDeclaredMethods())//
                .filter(method -> Modifier.isStatic(method.getModifiers()))
                .filter(method -> method.getName().toLowerCase().contains("version"))
                .filter(method -> method.getParameterCount() == 1)
                .filter(method -> method.getParameterTypes()[0].equals(String.class))
                .filter(method -> int.class.equals(method.getReturnType()))
                .findFirst();
    }

    private UnsupportedOperationException versioningNotSupported() {
        return new UnsupportedOperationException(typeOfCurrentVersion.getSimpleName() +
                " does not support versioning. Version method is missing. Example: 'public static final int getVersion(String json)'");
    }

    private static int getVersionOf(String json, Method versionMethod) {
        try {
            return (int) versionMethod.invoke(null, json);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new IllegalArgumentException("Error getting version of " + json, e);
        }
    }

    // Note: Currently, the update method is taken from the version subtype.
    // It returns a new object of the current version and does not take any parameters.
    // The first method that fulfills these restrictions (sorted descending by name) will be taken.
    // Note: This could be done typesafe using a interface (pro), which needs to be known to all serializable objects (con).
    private Method findUpdateMethod(Class<?> versionedTypeToDeserialize) {
        return Stream.of(versionedTypeToDeserialize.getDeclaredMethods())//
                .filter(method -> method.getParameterCount() == 0)
                .filter(method -> typeOfCurrentVersion.equals(method.getReturnType()))
                .sorted((method1, method2) -> method2.getName().compareTo(method1.getName()))
                .findFirst().get();
    }

    @SuppressWarnings("unchecked")
    private static <T> T update(String json, Object deserialized, Method updater) {
        try {
            return (T) updater.invoke(deserialized);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new IllegalArgumentException("Error during updating " + json, e);
        }
    }

    @Override
    public String toString() {
        return "VersioningSupport [typeOfCurrentVersion=" + typeOfCurrentVersion + "]";
    }
}
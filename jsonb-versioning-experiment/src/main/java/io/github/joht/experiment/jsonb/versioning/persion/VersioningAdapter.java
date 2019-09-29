package io.github.joht.experiment.jsonb.versioning.persion;

import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.adapter.JsonbAdapter;
import javax.json.stream.JsonParser;

public class VersioningAdapter<T> {

    private final Class<T> typeToAdapt;
    private final Jsonb jsonb; // TODO get rid of jsonb-dependencies. replace by serialize/deserialize function.

    public VersioningAdapter(JsonbConfig config, Class<T> typeToAdapt) {
        JsonbConfig configWithoutThisAdapter = configWithoutAdapterType(config, getClass());
        this.typeToAdapt = typeToAdapt;
        this.jsonb = JsonbBuilder.create(configWithoutThisAdapter);
    }

    private static JsonbConfig configWithoutAdapterType(JsonbConfig config, Class<?> adapterType) {
        Map<String, Object> configMap = new HashMap<>(config.getAsMap());
        JsonbAdapter<?, ?>[] adapters = (JsonbAdapter[]) configMap.getOrDefault(JsonbConfig.ADAPTERS, new JsonbAdapter[] {});
        configMap.put(JsonbConfig.ADAPTERS, Stream.of(adapters).filter(adapterType::isInstance).toArray(JsonbAdapter[]::new));
        return createJsonbConfigFromMap(configMap);
    }

    private static JsonbConfig createJsonbConfigFromMap(Map<String, Object> configMap) {
        JsonbConfig configWithoutThisAdapter = new JsonbConfig();
        configMap.forEach((name, value) -> configWithoutThisAdapter.setProperty(name, value));
        return configWithoutThisAdapter;
    }

    public JsonObject adaptToJson(T obj) {
        String json = jsonb.toJson(obj);
        try (StringReader reader = new StringReader(json); JsonParser parser = Json.createParser(reader)) {
            parser.next();
            return parser.getObject();
        }
    }

    public T adaptFromJson(JsonObject obj) {
        String json = obj.toString();
        Method versionMethod = findVersionMethod(typeToAdapt);
        int version = getVersionOf(json, versionMethod);
        if (version == 0) {
            return currentVersionOf(json);
        }
        Class<?> versionedTypeToDeserialize = getVersionedType(version, typeToAdapt);
        Object deserialized = jsonb.fromJson(json, versionedTypeToDeserialize);
        Method upcaster = findUpcaster(versionedTypeToDeserialize, typeToAdapt);
        return upcast(obj, deserialized, upcaster);
    }

    private T currentVersionOf(String json) {
        return jsonb.fromJson(json, typeToAdapt);
    }

    // Note: Only supports subclasses named equally to the version right now.
    // Example: Object "Person" with version "1" means there needs to be a static subclass "V1" inside "Person".
    private static Class<?> getVersionedType(int version, Class<?> typeToAdapt) {
        String versionedType = typeToAdapt.getName() + "$V" + version;
        try {
            return Class.forName(versionedType);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("There is no class " + versionedType + " for Version " + version, e);
        }

    }

    // Note: Only supports static methods taking a String an returning a int.
    // Example: public static final int Person.getVersion(String json)
    private static Method findVersionMethod(Class<?> typeToAdapt) {
        return Stream.of(typeToAdapt.getMethods())//
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
    private static Method findUpcaster(Class<?> versionedTypeToDeserialize, Class<?> typeToAdapt) {
        return Stream.of(typeToAdapt.getMethods())//
                .filter(method -> Modifier.isStatic(method.getModifiers()))
                .filter(method -> method.getParameterCount() == 1)
                .filter(method -> method.getParameterTypes()[0].equals(versionedTypeToDeserialize))
                .filter(method -> typeToAdapt.equals(method.getReturnType()))
                .findFirst().get();
    }

    @SuppressWarnings("unchecked")
    private static <T> T upcast(JsonObject obj, Object deserialized, Method upcaster) {
        try {
            return (T) upcaster.invoke(null, deserialized);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new IllegalArgumentException("Error during upcasting " + obj, e);
        }
    }
}
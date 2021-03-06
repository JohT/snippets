package io.github.joht.experiment.jsonb.versioning.person.example.jsonb.generic;

import java.lang.reflect.Type;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;
import java.util.function.Function;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;

import io.github.joht.experiment.jsonb.versioning.VersioningSupport;
import io.github.joht.experiment.jsonb.versioning.person.example.Person;

/**
 * Deserializes {@link Person}-Objects supporting all previous version.
 * <p>
 * TODO Generic versioning maybe using an interface or a wrapper.
 * 
 * @author JohT
 */
public class GenericVersionDeserializer implements JsonbDeserializer<Object> {

    private final ConcurrentMap<String, Optional<VersioningSupport<?>>> versioning = new ConcurrentHashMap<>();
    private final Function<Object, String> serializer;
    private final BiFunction<String, Class<?>, Object> deserializer;

    public static final GenericVersionDeserializer forConfig(JsonbConfig config) {
        Jsonb jsonb = JsonbBuilder.create(config);
        return new GenericVersionDeserializer(jsonb::toJson, jsonb::fromJson);
    }

    public GenericVersionDeserializer(Function<Object, String> serializer, BiFunction<String, Class<?>, Object> deserializer) {
        this.serializer = serializer;
        this.deserializer = deserializer;
    }

    @Override
    public Object deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
        if (!(rtType instanceof Class)) {
            throw new UnsupportedOperationException(GenericVersionDeserializer.class.getSimpleName() + " does not support type " + rtType + ", because it is not a class.");
        }
        Class<?> realtimeType = (Class<?>) rtType;
        String json = parser.getObject().toString();
        return versioning.computeIfAbsent(realtimeType.getName(), name -> getVersioningSupport(realtimeType))//
                .map(versionSupport -> deserializeVersioned(json, versionSupport))
                .orElseGet(() -> deserializer.apply(json, realtimeType));
    }

    private Object deserializeVersioned(String json, VersioningSupport<?> versionSupport) {
        return versionSupport.adaptFromJson(json);
    }

    private Optional<VersioningSupport<?>> getVersioningSupport(Class<?> type) {
        if (VersioningSupport.isVersioningSupported(type)) {
            return Optional.of(new VersioningSupport<>(type, serializer, deserializer));
        }
        return Optional.empty();
    }
}
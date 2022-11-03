package io.github.joht.experiment.jsonb.versioning.person.example.jsonb.generic;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;
import java.util.function.Function;

import io.github.joht.experiment.jsonb.versioning.VersioningSupport;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;

/**
 * Deserializes Objects with versioning support.
 * <p>
 * Since a Deserializer can't be registered for all Objects (anymore since yasson 1.0.8),
 * there needs to be a common super type, here in this example Serializable.
 * 
 * <p>
 * TODO Generic versioning may be using an interface or a wrapper.
 * 
 * @author JohT
 */
public class GenericVersionDeserializer implements JsonbDeserializer<Serializable> {

    private final ConcurrentMap<String, Optional<VersioningSupport<?>>> versioning = new ConcurrentHashMap<>();
    private final Function<Object, String> serializer;
    private final BiFunction<String, Class<?>, Object> deserializer;

    public static final GenericVersionDeserializer forConfig(JsonbConfig config) {
        Jsonb jsonb = JsonbBuilder.create(config);
        return new GenericVersionDeserializer(jsonb::toJson, jsonb::fromJson);
    }

    public GenericVersionDeserializer(Function<Object, String> serializer,
            BiFunction<String, Class<?>, Object> deserializer) {
        this.serializer = serializer;
        this.deserializer = deserializer;
    }

    @Override
    public Serializable deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
        if (!(rtType instanceof Class)) {
            throw new UnsupportedOperationException(GenericVersionDeserializer.class.getSimpleName()
                    + " does not support type " + rtType + ", because it is not a class.");
        }
        Class<?> realtimeType = (Class<?>) rtType;
        if (!parser.hasNext()) {
            return ctx.deserialize(rtType, parser);
        }
        String json =  parser.getObject().toString();
        return (Serializable) versioning.computeIfAbsent(realtimeType.getName(), name -> getVersioningSupport(realtimeType))//
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
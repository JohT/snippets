package io.github.joht.experiment.jsonb.versioning.person.example.jsonb.generic;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.joht.experiment.jsonb.versioning.person.example.NotVersionedObject;

class GenericVersionDeserializerLetThroughTest {

    private final JsonbConfig jsonbConfig = new JsonbConfig();
    private final Jsonb jsonb = JsonbBuilder.create(jsonbConfig.withDeserializers(GenericVersionDeserializer.forConfig(jsonbConfig)));
    private final NotVersionedObject object = new NotVersionedObject();

    @Test
    void notVersionedObjectDeserializable() {
        NotVersionedObject deserialized = jsonb.fromJson(jsonb.toJson(object), NotVersionedObject.class);
        assertEquals(object.info, deserialized.info);
    }

}

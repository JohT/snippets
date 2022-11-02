package io.github.joht.experiment.jsonb.versioning.person.example.jsonb.generic;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.joht.experiment.jsonb.versioning.person.example.Name;
import io.github.joht.experiment.jsonb.versioning.person.example.NameFixture;

class GenericVersionNameDeserializerTest {

    private final JsonbConfig jsonbConfig = new JsonbConfig();
    private Jsonb jsonb = JsonbBuilder.create(jsonbConfig.withDeserializers(GenericVersionDeserializer.forConfig(jsonbConfig)));
    private final NameFixture nameFixture = NameFixture.JOHN_DOE;

    @Test
    void version1Deserializable() {
        Name.V1 name = nameFixture.buildV1();
        Name deserialized = jsonb.fromJson(jsonb.toJson(name), Name.class);
        assertEquals(name.forename, deserialized.forename);
        assertEquals(name.surename, deserialized.surename);
        assertEquals("", deserialized.title);
    }

    @Test
    void currentVersionDeserializable() {
        Name name = nameFixture.buildCurrent();
        Name deserialized = jsonb.fromJson(jsonb.toJson(name), Name.class);
        assertEquals(name.forename, deserialized.forename);
        assertEquals(name.surename, deserialized.surename);
        assertEquals(name.title, deserialized.title);
    }

}

package io.github.joht.experiment.jsonb.versioning.person.example.jsonb;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.joht.experiment.jsonb.versioning.person.example.Name;
import io.github.joht.experiment.jsonb.versioning.person.example.NameFixture;

class NameAdapterTest {

    private final JsonbConfig jsonbConfig = new JsonbConfig();
    private final Jsonb jsonb = JsonbBuilder.create(jsonbConfig.withAdapters(new NameAdapter(jsonbConfig)));
    private final NameFixture nameFixture = NameFixture.JOHN_DOE;

    @Test
    void version1SerializedCorrectly() {
        Name.V1 name = nameFixture.buildV1();
        assertEquals(nameFixture.getExpectedJsonRepresentation(1), jsonb.toJson(name));
    }

    @Test
    void currentVersionSerializable() {
        Name name = nameFixture.buildCurrent();
        assertEquals(nameFixture.getExpectedJsonRepresentationForTheCurrentVersion(), jsonb.toJson(name));
    }

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
